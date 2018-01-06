import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Decode {
    // Take the text in till line !=null.
    // For each value associated with a key, find the rgb values,and initiate the image object, and start adding/setting the color of
    // each pixel.
    int mostRecentIFrame = 0;

    int offset = 10;
    int count  = 0;
    int countOfPBlocks = 0;
    int countOfIBlocks = 0;

    // The header variables/ information.
    int numberOfMacroblocks = 2;
    int GOP                 = 4;
    int imageHeight         = 0;
    int imageWidth          = 0;
    int frameRate           = 0;
    int numberOfFrames      = 2;

    // This contains all the macroblocks.
    ArrayList<BufferedImage> macroblocks = new ArrayList<>();

    public Decode(File file) throws IOException {

        // if str == "Header", then the first value = height, so on. Save the values ----> This info
        // is missing in the bitstream object given.

        byte[] inputFile = new byte[(int) file.length()];
        FileInputStream inStream;
        inStream = new FileInputStream(file);
        inStream.read(inputFile);

        numberOfMacroblocks = ((inputFile[0] & 0xff) | ((inputFile[1] & 0xff) << 8));
        GOP = inputFile[2];
        imageHeight = ((inputFile[3] & 0xff) | ((inputFile[4] & 0xff) << 8));
        imageWidth = ((inputFile[5] & 0xff) | ((inputFile[6] & 0xff) << 8));
        frameRate = inputFile[7];
        numberOfFrames = ((inputFile[8] & 0xff) | ((inputFile[9] & 0xff) << 8));

        ArrayList<Integer> in = new ArrayList<>();

        for (int i = 0; i < inputFile.length; i++) {
            if (i >= 10) {
                    in.add(((int) inputFile[i]));
            }
        }
        offset = 0;
        inputFile = new byte[0];
        inStream.close();

        int flag = 1;
        // for (int frame = 0; frame < numberOfFrames; frame++) {
        while (count != numberOfFrames) {
            System.out.println(count);
            // Protocol: 0 - Block, 1 - Reference,

            // Take the input stream, and use for loop for each Byte.
            // System.out.println(inputFile.length);

            // The count variable counts each macroblock.
            // Each frame's macroblock is ---

            // The first 64 Bytes would be the Y values.
            if (flag == 1) {

                while (countOfIBlocks < numberOfMacroblocks) {
                    // I FRAME and length of each macro-block = 192.
                    reconstructMacroblocksForIFrame(in);
                }
                mostRecentIFrame = count;
                // Set the flag to 0, and count this I frame.
                flag = 0;
                count += 1;
                countOfIBlocks = 0;
            }

            else if (flag == 0) {
                while (countOfPBlocks < numberOfMacroblocks) {
                    reconstructMacroblocksForPFrame(in);
                }
                count += 1;
                if (count % GOP == 0) {
                    flag = 1;
                }
                // System.out.println(offset);
                countOfPBlocks = 0;
            }
        }
        if(macroblocks.size() == (numberOfFrames*numberOfMacroblocks)){
            System.out.println(macroblocks.size());
    }
    }


    public void reconstructMacroblocksForPFrame(ArrayList in){

            if(in.get(offset).equals(1)){
                //Block matches
                countOfPBlocks +=1;
                // Offsets will be messed.
                Integer i = (Integer) in.get(offset+1);
                byte j = i.byteValue();
                Integer k = (Integer) in.get((offset+2));
                byte l = k.byteValue();
                int pointer = ((j & 0xff)|((l & 0xff) << 8));
                macroblocks.add(macroblocks.get(pointer + mostRecentIFrame*numberOfMacroblocks));
                // Move the index to the next block. |1|pointer|nextBlock.|
                offset +=3;

            }
            else if(in.get(offset).equals(0)){
                // Block non-matching, so length is 64+16+16, offset if 96.
                countOfPBlocks+=1;
                ArrayList <Integer>Y  = new ArrayList<>();
                ArrayList<Integer> compressedCr = new ArrayList<>();
                ArrayList<Integer> compressedCb = new ArrayList<>();
                ArrayList<Integer> Cr = new ArrayList<>();
                ArrayList<Integer> Cb = new ArrayList<>();
                offset+=1;
                for(int i = offset; i < (96+offset); i++) {

                    if (i<(64+offset)) {
                        if((int)in.get(i)<0) {
                            Y.add(((int)in.get(i)+256));
                        }
                        else
                            Y.add((int)in.get(i));
                    } else if (i >= (64+offset)   && i <(80+offset)) {
                        compressedCr.add((int) in.get(i));
                    } else if (i >= (80+offset) && i < (96+offset)) {
                        compressedCb.add((int) in.get(i));

                    }
                }
                offset +=96;
           //     System.out.println(offset);
                CrCb x = new CrCb("test");
                Cr = x.decompressCrCb2(compressedCr);
               // CrCb x = new CrCb("test");
                Cb = x.decompressCrCb2(compressedCb);
                macroblocks.add(convertToRGB(Y,Cr,Cb));
            }
//        }

    }


    public void reconstructMacroblocksForIFrame(ArrayList in) {
        ArrayList <Integer>Y  = new ArrayList<>();
        ArrayList<Integer> compressedCr = new ArrayList<>();
        ArrayList<Integer> compressedCb = new ArrayList<>();
         ArrayList<Integer> Cr;
          ArrayList<Integer> Cb;
        for(int b = offset; b < (96+offset); b++) {
            // Increment the count of number of macros counted.

            if (b<(64+offset)) {
                    if((int)in.get(b)<0) {
                        Y.add(((int)in.get(b)+256));
                    }
                    else
                        Y.add((int)in.get(b));

            } else if (b >= (64+offset)   && b <(80+offset)) {
                compressedCr.add((int)in.get(b));

            } else if (b >= (80+offset) && b < (96+offset)) {
                compressedCb.add((int)in.get(b));

            }
        }
        offset +=96;
        countOfIBlocks+=1;
        CrCb x = new CrCb("test");
        Cr = x.decompressCrCb2(compressedCr);
        Cb = x.decompressCrCb2(compressedCb);
        compressedCb.clear();
        compressedCr.clear();
        macroblocks.add(convertToRGB(Y,Cr,Cb));
    }

    public BufferedImage convertToRGB(ArrayList<Integer> Y, ArrayList <Integer>Cr, ArrayList <Integer>Cb ){

        BufferedImage outputRGB = new BufferedImage(8,8,BufferedImage.TYPE_INT_RGB);
        int rowPixel = 0;
        int colPixel = 0;
        for (int i = 0; i < Y.size(); i++){
            double R = Y.get(i) + 1.4022 * (Cr.get(i));
            double G = Y.get(i) - 0.3437 * (Cb.get(i)) - 0.7145 * (Cr.get(i));
            double B = Y.get(i) + 1.7669 * (Cb.get(i));

            if      ( R < 0  ) R = 0;
            else if ( R > 255) R = 255;

            if      ( G < 0  )  G = 0;
            else if ( G > 255)  G = 255;

             if      ( B < 0  ) B = 0;
             else if ( B > 255) B = 255;

            outputRGB.setRGB(rowPixel, colPixel, new Color((int) R, (int) G,(int)B).getRGB());
            colPixel += 1;
            if(colPixel == 8){
                rowPixel += 1;
                colPixel = 0;
            }

        }
        return outputRGB;
    }
}



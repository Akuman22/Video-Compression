package edu.bu.ec504.team1.videoplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import static android.graphics.Bitmap.createBitmap;
import static edu.bu.ec504.team1.videoplayer.VideoViewerActivity.frame;

/**
 * Created by heqifan on 11/30/17.
 */


public class Decode {
    // Take the text in till line !=null.
    // For each value associated with a key, find the rgb values,and initiate the image object, and start adding/setting the color of
    // each pixel.
    int height = 8;
    int width  = 8;


    int offset = 10;
    int count  = 0;
    int countOfPBlocks = 0;
    int countOfIBlocks = 0;
    int currentBlock = 0;

    int mcbPointer = 0;

    int blockIndex = 0;

    int index = 0;
    // The header variables/ information.
    int numberOfMacroblocks = 2;
    int GOP                 = 4;
    int imageHeight         = 0;
    int imageWidth          = 0;
    int frameRate           = 0;
    int numberOfFrames      = 2;

    // This contains all the macroblocks.
    ArrayList<ArrayList<myColor>> macroblocks = new ArrayList<>();
    int blockArr[][];
    int colorArr[];
    int colorPointer = 0;

    // BufferedImage tempImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB);
    // File outImage = new File("C:\\Users\\Vrushali\\Desktop\\EC504\\Decoder");

    int blockRows;
    int blockCols;


    public static class myColor {
        public int R,G,B;



        public myColor (int Rin, int Gin, int Bin) {
            R = Rin; G = Gin; B = Bin;
        }

        public int getRGB (){
            int rgb = R;
            rgb = (rgb << 8) + G;
            rgb = (rgb << 8) + B;
            return rgb;
        }


    }

    public Decode(File file) throws IOException {

        // if str == "Header", then the first value = height, so on. Save the values ----> This info
        // is missing in the bitstream object given.


        byte[] inputFile = new byte[(int) file.length()];
        FileInputStream inStream = null;
        inStream = new FileInputStream(file);
        inStream.read(inputFile);

        numberOfMacroblocks = ((inputFile[0] & 0xff) | ((inputFile[1] & 0xff) << 8));
        GOP = inputFile[2];
        imageHeight = ((inputFile[3] & 0xff) | ((inputFile[4] & 0xff) << 8));
        imageWidth = ((inputFile[5] & 0xff) | ((inputFile[6] & 0xff) << 8));
        frameRate = inputFile[7];
        numberOfFrames = ((inputFile[8] & 0xff) | ((inputFile[9] & 0xff) << 8));


        blockArr = new int[numberOfMacroblocks*GOP][64];
        colorArr = new int[imageHeight*imageWidth];
        blockRows = imageHeight/8;
        blockCols = imageWidth/8;



        ArrayList<Integer> in = new ArrayList<>();

        for (int i = 0; i < inputFile.length; i++) {
            if (i >= offset) {
                in.add(((int) inputFile[i]));
            }
        }
        offset = 0;


        int flag = 1;
        // for (int frame = 0; frame < numberOfFrames; frame++) {

        while (count != numberOfFrames && VideoViewerActivity.myQueue.size() < 20) {

            System.out.println(count);
            // Protocol: 0 - Block, 1 - Reference,

            // Take the input stream, and use for loop for each Byte.
            // System.out.println(inputFile.length);

            // The count variable counts each macroblock.
            // Each frame's macroblock is ---

            // The first 64 Bytes would be the Y values.
            if (flag == 1) {
                //macroblocks.clear();
                mcbPointer = 0;
                while (countOfIBlocks < numberOfMacroblocks) {
                    // I FRAME and length of each macro-block = 192.
                    reconstructMacroblocksForIFrame(in);
                }
                // Set the flag to 0, and count this I frame.
                flag = 0;
                count += 1;
                countOfIBlocks = 0;
                frameBuilder();
                currentBlock = 0;
            }

            if (flag == 0) {
                while (countOfPBlocks < numberOfMacroblocks) {
                    reconstructMacroblocksForPFrame(in);
                }
                count += 1;
                if (count % GOP == 0) {
                    flag = 1;
                }
                // System.out.println(offset);
                countOfPBlocks = 0;
                frameBuilder();
                currentBlock = 0;
            }


            if (count % 4 == 0)
                blockIndex = 0;



        }


        if(mcbPointer == (numberOfFrames*numberOfMacroblocks)){
            System.out.println(mcbPointer);
        }

        System.out.println("Count : " + count);


        System.out.println("Size:: " + macroblocks.size());



        while(true){
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }



    public void frameBuilder() {



        int arr = 0;

        for (int row = 0; row < imageHeight; row +=8) {
            for (int col = 0; col < imageWidth; col += 8) {
                int blockCounter = 0;
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {

                        int rgb = (255 & 0xff) << 24 | blockArr[blockIndex][blockCounter];

                        colorArr[(row+j)*imageWidth + col + i] = rgb;
                        blockCounter++;
                    }
                }
                blockIndex ++;
            }
        }
        Bitmap newFrame = createBitmap(colorArr, imageWidth,imageHeight, Bitmap.Config.ARGB_8888);

        VideoViewerActivity.myQueue.add(newFrame.copy(newFrame.getConfig(), true));





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
            //macroblocks.add(macroblocks.get(pointer));
            blockArr[mcbPointer] = blockArr[pointer]; mcbPointer ++;
            // Move the index to the next block. |1|pointer|nextBlock.|
            offset +=3;



        }
        else if(in.get(offset).equals(0)){
            // Block non-matching, so length is 64+16+16, offset if 96.
            countOfPBlocks+=1;
            offset += 1;
            ArrayList <Integer>Y  = new ArrayList<>();
            ArrayList<Integer> compressedCr = new ArrayList<>();
            ArrayList<Integer> compressedCb = new ArrayList<>();
            ArrayList<Integer> Cr = new ArrayList<>();
            ArrayList<Integer> Cb = new ArrayList<>();

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
            //macroblocks.add(convertToRGB(Y,Cr,Cb));
            blockArr[mcbPointer] = convertToRGB(Y,Cr,Cb); mcbPointer ++;
            currentBlock ++;
        }
//        }

    }


    public void reconstructMacroblocksForIFrame(ArrayList in) {
        ArrayList <Integer>Y  = new ArrayList<>();
        ArrayList<Integer> compressedCr = new ArrayList<>();
        ArrayList<Integer> compressedCb = new ArrayList<>();
        ArrayList<Integer> Cr = new ArrayList<>();
        ArrayList<Integer> Cb = new ArrayList<>();
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
        // CrCb x = new CrCb("test");
        Cb = x.decompressCrCb2(compressedCb);
        //macroblocks.add(convertToRGB(Y,Cr,Cb));
        blockArr[mcbPointer] = convertToRGB(Y,Cr,Cb); mcbPointer ++;
        currentBlock ++;
    }

    public int[] convertToRGB(ArrayList<Integer> Y, ArrayList <Integer>Cr, ArrayList <Integer>Cb ){

        //ArrayList<myColor> outputRGB = new ArrayList<myColor>();

        int currentX = currentBlock % blockCols * 8;
        int currentY = currentBlock / blockCols * 8;

        int outputRGB[] = new int[Y.size()];

        int row = 0;
        int col = 0;
        for (int i = 0; i < Y.size(); i++) {
            double R = Y.get(i) + 1.4022 * (Cr.get(i));
            double G = Y.get(i) - 0.3437 * (Cb.get(i)) - 0.7145 * (Cr.get(i));
            double B = Y.get(i) + 1.7669 * (Cb.get(i));

            if (R < 0) R = 0;
            else if (R > 255) R = 255;

            if (G < 0) G = 0;
            else if (G > 255) G = 255;

            if (B < 0) B = 0;
            else if (B > 255) B = 255;

            //outputRGB.add(new myColor((int)R, (int)G, (int)B));


            int rgb = (int) R;
            rgb = (rgb << 8) + (int) G;
            rgb = (rgb << 8) + (int) B;

            outputRGB[i] = rgb;



            col += 1;
            if (col == 8) {
                row += 1;
                col = 0;
            }

        }
        return outputRGB;
    }
}




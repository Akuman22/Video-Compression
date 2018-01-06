// http://kalanir.blogspot.com/2010/02/how-to-split-image-into-chunks-java.html
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Image_Object {
    public ArrayList<img_YCrCb>macroBlocks = new ArrayList<>();
    public float loss;
    public int imgHeight, imgWidth;

    public Image_Object(String path) {
        BufferedImage image;
        BufferedImage macroBlocksBuff[];
        File file = new File(path);
        try {
            FileInputStream fis = new FileInputStream(file);

            image = ImageIO.read(fis); //reading the image file
            fis.close();
            imgHeight = image.getHeight();
            imgWidth = image.getWidth();
            int rows = image.getHeight()/8; //You should decide the values for rows and cols variables
            int cols = image.getWidth()/8;
            int chunks = rows * cols;
            int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
            int chunkHeight = image.getHeight() / rows;
            int count = 0;
            macroBlocksBuff = new BufferedImage[chunks]; //Image array to hold image chunks

            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < cols; y++) {
                    //Initialize the image array with image chunks
                    macroBlocksBuff[count] = new BufferedImage(chunkWidth, chunkHeight, image.getType());
                    // draws the image chunk
                    Graphics2D gr = macroBlocksBuff[count++].createGraphics();
                    gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y,
                            chunkHeight * x, chunkWidth * y + chunkWidth,
                            chunkHeight * x + chunkHeight, null);
                    gr.dispose();
                    macroBlocks.add(RGBtoYCrCb(macroBlocksBuff[count-1]));
                }
            }
            Arrays.fill(macroBlocksBuff, null);
            loss = (100*((float)((image.getHeight()/8*image.getWidth()/8) - macroBlocksBuff.length)*64)/
                    (float)(image.getHeight()*image.getWidth()));
        }
        catch (IOException e) {
             e.printStackTrace();
        }
    }

    private img_YCrCb RGBtoYCrCb(BufferedImage image) {
        img_YCrCb new_img = new img_YCrCb();
        int Y;
        int width = 8;
        int height = 8;
        int delta = 8 ; // 8-bit image-- reference opencv
        float R, G, B;
        Color RGB;

        for (int row = 0; row < width - 1; row++) {
            for (int col = 0; col < height - 1; col++) {
                RGB = new Color(image.getRGB(row, col));
                R = RGB.getRed();
                G = RGB.getGreen();
                B = RGB.getBlue();
                Y = (int)(0.299*R + 0.587*G + 0.114*B);
                new_img.Y[row][col] = Y;
                new_img.Cr[row][col] = (int)((R-Y)*0.713 + delta); // U = -0.8R-0.29G+0.44B
                new_img.Cb[row][col] = (int)((B-Y)*0.564 + delta); // V = 0.62R - 0.52G - 0.10B
                //https://docs.opencv.org/3.3.0/de/d25/imgproc_color_conversions.html
            }
        }
        return new_img;
    }
    // end of BGRtoYCrBr
}
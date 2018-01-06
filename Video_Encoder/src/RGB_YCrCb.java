import java.awt.*;
import java.awt.image.BufferedImage;

//convert RGB to YCrCb
/*
*YCbCr is often preferred because the human visual system is more sensitive
* to changes in luminance than quantitatively equivalent changes in chrominance.
* Therefore, an image coder such as JPEG can compress the two chrominance layers
* more than the luminance layer, resulting in a higher compression ratio.*/
public class RGB_YCrCb {
    //TODO: create constructors if necessary

    public img_YCrCb RGBtoYCrCb(BufferedImage image) {
        img_YCrCb new_img = new img_YCrCb();

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

                //https://docs.opencv.org/3.3.0/de/d25/imgproc_color_conversions.html
                int Y = (int)(0.299*R + 0.587*G + 0.114*B);
                new_img.Y[row][col] = Y;
                new_img.Cr[row][col] = (int)((R-Y)*0.713 + delta); // U = -0.8R-0.29G+0.44B
                new_img.Cb[row][col] = (int)((B-Y)*0.564 + delta); // V = 0.62R - 0.52G - 0.10B
            }
        }
        return new_img;
    } // end of BGRtoYCrBr




    public BufferedImage YCrCbtoRGB(img_YCrCb image) {
        int width = 8;
        int height = 8;

        BufferedImage new_img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        float R, G, B;
        Color RGB;
        for (int row = 0; row < width - 1; row++) {
            for (int col = 0; col < height - 1; col++) {

                int Y = image.Y[row][col];
                int Cr = image.Cr[row][col];
                int Cb = image.Cb[row][col];

                R =  (int) (Y + 0.0000 * Cb + 1.4022 * Cr);
                G =  (int) (Y - 0.3437 * Cb - 0.7145 * Cr);
                B =  (int) (Y + 1.7699 * Cb + 0.0000 * Cr);

                RGB = new Color((R*255), (G*255), (B*255));
                new_img.setRGB(row, col, RGB.getRGB());

                //https://msdn.microsoft.com/en-us/library/ff635643.aspx

            }
        }
        return new_img;
    } // end of YCrCbtoBGR


}


//TODO: delete below source if not used in  final version
//https://www.programcreek.com/java-api-examples/index.php?source_dir=TinderCardDemo-master/app/src/main/java/com/imagefilter/YCBCrLinearFilter.java
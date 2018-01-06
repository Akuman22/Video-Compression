import java.awt.image.BufferedImage;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.AdaptiveContrastEnhancement;
import Catalano.Imaging.Filters.Artistic.Blend;
import Catalano.Imaging.Filters.Artistic.FakeHDR;
import Catalano.Imaging.Filters.Artistic.FilmGrain;
import Catalano.Imaging.Filters.Artistic.HeatMap;
import Catalano.Imaging.Filters.Artistic.OilPainting;
import Catalano.Imaging.Filters.Artistic.Opacity;
import Catalano.Imaging.Filters.Artistic.Solarize;
import Catalano.Imaging.Filters.Artistic.SpecularBloom;
import Catalano.Imaging.Filters.Blur;
import Catalano.Imaging.Filters.Sepia;
import Catalano.Imaging.Filters.Sharpen;
/**
 * Created by heqifan on 11/27/17.
 */

public class EffectsApply {
    Sepia sepia;
    Sharpen sharpen;
    Blur blur;
    FakeHDR fakeHDR;
    FilmGrain filmGrain;
    HeatMap heatMap;
    OilPainting oilPainting;
    Solarize solarize;
    SpecularBloom specularBloom;


    EffectsApply(){
        sepia = new Sepia();
        sharpen = new Sharpen();
        blur = new Blur();
        fakeHDR = new FakeHDR();
        filmGrain = new FilmGrain();
        heatMap = new HeatMap();
    }

    BufferedImage apply(int id, BufferedImage frame){

        FastBitmap image = new FastBitmap(frame);
        image.toRGB();

        switch (id){
            case 1:
                sepia.applyInPlace(image);
                break;
            case 2:
                sharpen.applyInPlace(image);
                break;
            case 5:
                blur.applyInPlace(image);
                break;
            case 4:
                fakeHDR.applyInPlace(image);
                break;
            case 6:
                filmGrain.applyInPlace(image);
                break;
            case 3:
                heatMap.applyInPlace(image);
                break;

        }
        return image.toBufferedImage();
    }



}

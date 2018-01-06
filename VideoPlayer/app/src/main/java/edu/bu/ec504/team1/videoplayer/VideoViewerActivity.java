package edu.bu.ec504.team1.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MutableInt;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Concurrent.Filters.Blur;
import Catalano.Imaging.Concurrent.Filters.Emboss;
import Catalano.Imaging.Filters.Artistic.FakeHDR;
import Catalano.Imaging.Filters.Artistic.PencilSketch;
import Catalano.Imaging.Filters.Sepia;
import Catalano.Imaging.Filters.Sharpen;

import static edu.bu.ec504.team1.videoplayer.MainActivity.fileName;

/**
 * Created by heqifan on 10/28/17.
 */

public class VideoViewerActivity extends Activity {

    OurView v;
    static Bitmap frame;
    static BitmapFactory.Options options;
    public static int effectsID = 0;
    public static boolean done = false;
    static ConcurrentLinkedQueue<Bitmap> myQueue = new ConcurrentLinkedQueue<>();


    private static String filename = "frame_";
    private static int currentIndex = 10010;
    private int visibleCounter = 30;
    private boolean visible = true;
    static Decode decode;

    Button fxButton;

    private static String filepath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/seq/";


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_viewer);
        v = (OurView)findViewById(R.id.ourView);



        fxButton = (Button)findViewById(R.id.effects);
        fxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewerDialog viewerDialog = new ViewerDialog();
                viewerDialog.show(getFragmentManager(), "0");
            }
        });

        fxButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                fxButton.setVisibility(View.INVISIBLE);
            }
        }, 1000);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                v.isPlay = !v.isPlay;
                Log.d("Click OK", Boolean.toString(v.isPlay));
                if (v.isPlay) {
                    fxButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fxButton.setVisibility(View.INVISIBLE);
                        }
                    }, 1000);
                }
                else
                    fxButton.setVisibility(View.VISIBLE);

            }
        });


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        decode = new Decode(new File(fileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();



        options = new BitmapFactory.Options();
        options.inMutable = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        v.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        v.pause();
    }



    public static class OurView extends SurfaceView implements Runnable{

        Thread t = null;
        SurfaceHolder holder;

        public static EffectsApply effectsApply = new EffectsApply();


        boolean isItOk = false;
        boolean isPlay = false;

        public OurView(Context context) {
            super(context);

            holder = getHolder();
        }
        public OurView(Context context, AttributeSet attrs) {
            super(context, attrs);
            holder = getHolder();
        }

        public OurView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            holder = getHolder();
        }

        @Override
        public void run() {
            Log.i("State", "run");

            while (isItOk) {
                if (!holder.getSurface().isValid()) {
                    continue;
                } else {
                    Canvas c = holder.lockCanvas();
                    c.drawARGB(255, 150, 150, 10);
                    if (isPlay && fetchNextFrame()){
                        double ratio = (double) c.getWidth() / frame.getWidth();

                        int newHeight = (int) ((double) frame.getHeight() * ratio);
                        if (effectsID != 0)
                            filter1(effectsID);
                        c.drawBitmap(Bitmap.createScaledBitmap(frame, c.getWidth(), newHeight, false), 0, c.getHeight() / 2 - newHeight / 2, null);

                    } else {
                        if(frame != null) {
                            double ratio = (double) c.getWidth() / frame.getWidth();

                            int newHeight = (int) ((double) frame.getHeight() * ratio);
                            if (effectsID != 0)
                                filter1(effectsID);
                            c.drawBitmap(Bitmap.createScaledBitmap(frame, c.getWidth(), newHeight, false), 0, c.getHeight() / 2 - newHeight / 2, null);

                        }
                    }
                    holder.unlockCanvasAndPost(c);

                    try {
                        t.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void pause() {
            Log.i("State", "pause");
            isItOk = false;
            while(true){
                try{
                    t.join();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                break;
            }

            t = null;
        }

        public void resume() {
            Log.i("State", "resume");

            isItOk = true;
            isPlay = true;
            t = new Thread(this);
            t.start();


        }

        private static boolean fetchNextFrame() {

            /*
            //Log.i("Read image file name", filepath+filename + Integer.toString(currentIndex).substring(1) + ".jpg");
            frame = BitmapFactory.decodeFile(filepath+filename + Integer.toString(currentIndex).substring(1)+".jpg", options);
            if (frame == null){
                Log.e("Read image file name", "cannot decode file");
            }
            currentIndex ++;
            if (currentIndex > 10160){
                currentIndex = 10010;
            }*/

            if (!myQueue.isEmpty()){
                frame = myQueue.poll();
                return true;
            }

            return false;
        }

        private static void filter1(int fxID) {
            effectsApply.apply(fxID);
        }


    }




}

package com.pervasive.unrealdetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FrameProcessing {
    private static final int w = 256, h = 144;


    private Bitmap FrontImgBitmap;
    private Size size1;


    public Bitmap getFrontImgBitmap() {
        size1=FrontImgMat.size();
        Utils.matToBitmap(FrontImgMat, FrontImgBitmap);
        return FrontImgBitmap;
    }




    private Mat FrontImgMat;


    MainActivity AirSim;

    public FrameProcessing(MainActivity ma){



        FrontImgMat = new Mat();

        AirSim = ma;

        FrontImgBitmap = null;



        //Init bitmap image

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inBitmap = FrontImgBitmap;
        options.inMutable = true;
        FrontImgBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }


    public void getFramesBitmap(){
        AirSim.GetImage(FrontImgMat.getNativeObjAddr()); //get frame in Mat


        Imgproc.cvtColor(FrontImgMat, FrontImgMat, Imgproc.COLOR_BGR2RGB);
    }
}

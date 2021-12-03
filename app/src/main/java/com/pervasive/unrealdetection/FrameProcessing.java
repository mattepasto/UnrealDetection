package com.pervasive.unrealdetection;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.pervasive.unrealdetection.cnn.CNNExtractorService;
import com.pervasive.unrealdetection.cnn.impl.CNNExtractorServiceImpl;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FrameProcessing {
    private static final int w = 256, h = 144;

    private static boolean isCarMoving = false;
    private Bitmap FrontImgBitmap;
    private Size size1;

    private ImageClassification imgClass;


    public Bitmap getFrontImgBitmap() {
        size1 = FrontImgMat.size();
        Utils.matToBitmap(FrontImgMat, FrontImgBitmap);
        return FrontImgBitmap;
    }

    public void setIsCarMoving(boolean value) {
        isCarMoving = true;
    }

    private Mat FrontImgMat;
    MainActivity main;

    public FrameProcessing(MainActivity ma) {
        FrontImgMat = new Mat();
        main = ma;
        FrontImgBitmap = null;

        //Init bitmap image
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inBitmap = FrontImgBitmap;
        options.inMutable = true;
        FrontImgBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        //for image classification
        imgClass = new ImageClassification(main, this);
    }

    public void getFramesBitmap() {
        main.CarFunctions.GetImage(FrontImgMat.getNativeObjAddr()); //get frame in Mat
        //Mat is FrontImgMat
        if (isCarMoving) {
            FrontImgMat = imgClass.ImgClassification(FrontImgMat);
        }
    }

}

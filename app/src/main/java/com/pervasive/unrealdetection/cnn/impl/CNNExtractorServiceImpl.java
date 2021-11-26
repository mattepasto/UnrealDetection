package com.pervasive.unrealdetection.cnn.impl;

import android.util.Log;
import com.pervasive.unrealdetection.cnn.CNNExtractorService;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CNNExtractorServiceImpl implements CNNExtractorService {
    private static final int TARGET_IMG_WIDTH = 224;
    private static final int TARGET_IMG_HEIGHT = 224;
    private static final double SCALE_FACTOR = 1 / 255.0;
    private static final Scalar MEAN = new Scalar(0.485, 0.456, 0.406);
    private static final Scalar STD = new Scalar(0.229, 0.224, 0.225);
    private String TAG;

    @Override
    public Net getConvertedNet(String clsModelPath, String tag) {
        TAG = tag;
        Net convertedNet = Dnn.readNetFromONNX(clsModelPath);
        Log.i(TAG, "Network was successfully loaded");
        return convertedNet;
    }

    @Override
    public String getPredictedLabel(Mat inputImage, Net dnnNet, String classesPath) {
        //preprocess input frame
        Mat inputBlob = getPreprocessedImage(inputImage);
        // set OpenCV model input
        dnnNet.setInput(inputBlob);
        // provide inference
        Mat classification = dnnNet.forward();
        return getPredictedClass(classification, classesPath);
    }

    private String getPredictedClass(Mat classificationResult, String classesPath) {
        ArrayList<String> imgLabels = getImgLabels(classesPath);
        if (imgLabels.isEmpty()) {
            return "Empty label";
        }
        Core.MinMaxLocResult mm = Core.minMaxLoc(classificationResult);
        double maxValIndex = mm.maxLoc.x;
        return imgLabels.get((int)maxValIndex);
    }

    private ArrayList<String> getImgLabels(String imgLabelsFilePath) {
        ArrayList<String> imgLabels = new ArrayList();
        BufferedReader bufferReader;
        try {
            bufferReader = new BufferedReader(new FileReader(imgLabelsFilePath));
            String fileLine;
            while((fileLine = bufferReader.readLine())!=null) {
                imgLabels.add(fileLine);
            }
        } catch(IOException ex) {
            Log.i(TAG, "ImageNet classes were not obtained");
        }
        return imgLabels;
    }

    private Mat getPreprocessedImage(Mat image) {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2RGB);
        Mat imgFloat = new Mat(image.rows(), image.cols(), CvType.CV_32FC3);
        image.convertTo(imgFloat, CvType.CV_32FC3, SCALE_FACTOR);
        Imgproc.resize(imgFloat, imgFloat, new Size(256, 256));
        imgFloat = centerCrop(imgFloat);
        Mat blob = Dnn.blobFromImage(
            imgFloat,
            1.0,
            new Size(TARGET_IMG_WIDTH, TARGET_IMG_HEIGHT),
            MEAN,
            true,
            false
        );
        Core.divide(blob, STD, blob);
        return blob;
    }

    private Mat centerCrop(Mat inputImage) {
        int y1 = Math.round((inputImage.rows() - TARGET_IMG_HEIGHT)/2);
        int y2 = Math.round(y1 + TARGET_IMG_HEIGHT);
        int x1 = Math.round((inputImage.cols() - TARGET_IMG_WIDTH)/2);
        int x2 = Math.round(x1 + TARGET_IMG_WIDTH);
        Rect centerRect = new Rect(x1, y1, (x2-x1), (y2-y1));
        Mat croppedImage = new Mat(inputImage, centerRect);
        return croppedImage;
    }
}

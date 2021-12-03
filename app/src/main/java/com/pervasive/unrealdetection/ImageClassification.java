package com.pervasive.unrealdetection;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.pervasive.unrealdetection.cnn.CNNExtractorService;
import com.pervasive.unrealdetection.cnn.impl.CNNExtractorServiceImpl;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.dnn.Net;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageClassification {

    private Net opencvNet;
    private CNNExtractorService cnnService;
    private static final String TAG = "ImgClass::MainActivity";
    private static final String IMAGENET_CLASSES = "imagenet_classes.txt";
    private static final String MODEL_FILE = "pytorch_mobilenet.onnx";
    private static String classesPath;
    FrameProcessing frameP;
    MainActivity main;

    public ImageClassification(MainActivity ma, FrameProcessing fp) {
        frameP = fp;
        main = ma;

        this.cnnService = new CNNExtractorServiceImpl();
        classesPath = getPath(IMAGENET_CLASSES, ma);
        String onnxModelPath = getPath(MODEL_FILE, ma);
        if (onnxModelPath.trim().isEmpty()) {
            Log.i(TAG, "Failed to get model file");
            return;
        }
        opencvNet = cnnService.getConvertedNet(onnxModelPath, TAG);
    }

    public Mat ImgClassification(Mat FrontImgMat) {
        String predictedClass = cnnService.getPredictedLabel(FrontImgMat, opencvNet, classesPath);
        Imgproc.putText(FrontImgMat, predictedClass, new Point(0, 50),
                Imgproc.FONT_HERSHEY_SIMPLEX, 0.3, new Scalar(255, 121, 0), 1);
        Imgproc.cvtColor(FrontImgMat, FrontImgMat, Imgproc.COLOR_BGR2RGB);
        if (predictedClass.compareTo(main.getObjectToDetect()) == 0) {
            main.CarFunctions.CarStop();
            frameP.setIsCarWaiting(true);
        }
        return FrontImgMat;
    }
    private static String getPath (String file, Context context){
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream;
        try {
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            //create copy file in storage
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i(TAG, "Failed to upload a file");
        }
        return "";
    }
}

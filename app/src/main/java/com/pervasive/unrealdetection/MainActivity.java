package com.pervasive.unrealdetection;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("carclient");
    }
    public native boolean CarConnect();
    public native void CarForward();
    public native void CarStop();
    public native void GetImage(long FrontImg);

    private FrameProcessing FrameProc;

    private Boolean connection=false;
    private ImageView FrontCameraImage;


    static {//Necessary to load the OpenCv before the OnCreate, without this it doesn't find openCV files and Mat inizialization crashes
        if (!OpenCVLoader.initDebug()) {
            Log.i("OpenCV", "OpenCV not loaded properly");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameProc = new FrameProcessing(this);
    }

    public void OnButtonForward(View view) {
        if(connection){//only if the connection with the car is true
            setContentView(R.layout.car_view);
            TaskRunner runner = new TaskRunner();
            runner.executeAsync(new BaseTask() {
                @Override
                public Void call() throws Exception {
                    //Looper.prepare();
                    CarForward();   // in here both forward and steering
                    //Loop();
                    return null;
                }
            });
        }
    }

    public void OnButtonConnect(View view) {
        TaskRunner runner = new TaskRunner();
        runner.executeAsync(new CustomCallable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return CarConnect();
            }
            @Override
            public void postExecute(Boolean result) {
                Toast.makeText(getApplicationContext(),
                        "connection result " + result, Toast.LENGTH_LONG).show();
                connection=result;
                Loop();

            }
            @Override
            public void preExecute() {
            }
        });
    }

    private void Loop(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (connection) {
                    FrameProc.getFramesBitmap();

                    FrontCameraImage = (ImageView) findViewById(R.id.frontCamera);

                    if (FrontCameraImage != null) {
                        FrontCameraImage.setImageBitmap(FrameProc.getFrontImgBitmap());
                    }
                    handler.postDelayed(this, 1);
                }
            }

        }, 1);
    }
}
package com.pervasive.unrealdetection;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
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

    public String objectToDetect;
    private boolean textInitialization = false;   // initialize text in the car_view layout

    static {    //Necessary to load the OpenCv before the OnCreate, without this it doesn't find openCV files and Mat inizialization crashes
        if (!OpenCVLoader.initDebug()) {
            Log.i("OpenCV", "OpenCV not loaded properly");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        FrameProc = new FrameProcessing(this);
    }
    @Override
    public void onClick(View view) {
        if(connection){     //  only if the connection with the car is true
            int clickedId = view.getId();
            Button button = findViewById(clickedId);
            String key = button.getText().toString();
            objectToDetect=key;
            setContentView(R.layout.car_view);

            TaskRunner runner = new TaskRunner();
            runner.executeAsync(new BaseTask() {
                @Override
                public Void call() throws Exception {
                    CarForward();   // in here both forward and steering
                    FrameProc.setIsCarMoving(true);
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
                    FrameProc.getFramesBitmap();    //obtained Mat from camera.
                    FrontCameraImage = (ImageView) findViewById(R.id.frontCamera);

                    if (FrontCameraImage != null) {     //it starts when completely loaded
                        FrontCameraImage.setImageBitmap(FrameProc.getFrontImgBitmap());
                        if(!textInitialization) {
                            TextView text = (TextView) findViewById(R.id.textView2);
                            if (text != null) {
                                text.setText(objectToDetect);
                                textInitialization=true;
                            }
                        }
                    }
                    handler.postDelayed(this, 1);
                }
            }
        }, 1);
    }

    public String getObjectToDetect(){
        return objectToDetect;
    }
}
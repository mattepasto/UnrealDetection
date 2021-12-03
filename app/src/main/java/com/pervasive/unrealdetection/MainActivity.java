package com.pervasive.unrealdetection;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public CarClient CarFunctions;
    private FrameProcessing FrameProc;
    private FrameLoop FrameLo;
    private Boolean connection=false;

    public String objectToDetect;
    private boolean textInitialization = false;   // initialize text in the car_view layout

    static {    //Necessary to load the OpenCv before the OnCreate, without this it doesn't find openCV files and Mat initialization crashes
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

        CarFunctions = new CarClient();
        FrameProc = new FrameProcessing(this);
        FrameLo = new FrameLoop(this, FrameProc);
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
                    CarFunctions.CarForward();   // in here both forward and steering
                    FrameProc.setIsCarMoving(true);
                    FrameProc.setIsCarWaiting(false);
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
                return CarFunctions.CarConnect();
            }
            @Override
            public void postExecute(Boolean result) {
                Toast.makeText(getApplicationContext(),
                        "connection result " + result, Toast.LENGTH_LONG).show();
                connection=result;
                FrameLo.Loop();
            }
            @Override
            public void preExecute() {
            }
        });
    }

    public String getObjectToDetect(){return objectToDetect;}

    public boolean getConnection() {return connection;}

    public boolean getTextInitialization() {return textInitialization;}

    public void setTextInitialization(boolean value) {textInitialization=value;}

    public View getCarView() {return this.findViewById(android.R.id.content).getRootView();}

    public void OnBackClick(View view) {
        Boolean waitFlag = FrameProc.getIsCarWaiting();
        if(waitFlag){
            setContentView(R.layout.activity_main);
            Button button1 = findViewById(R.id.button1);
            Button button2 = findViewById(R.id.button2);
            Button button3 = findViewById(R.id.button3);

            button1.setOnClickListener(this);
            button2.setOnClickListener(this);
            button3.setOnClickListener(this);
            textInitialization = false;
            FrameProc.setIsCarMoving(false);

            if(connection) {
                ToggleButton tButton = findViewById(R.id.connectButton);
                tButton.setChecked(true);
            }
        }
    }
}
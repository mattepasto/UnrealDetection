package com.pervasive.unrealdetection;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FrameLoop {
    MainActivity main;
    FrameProcessing fproc;

    private ImageView FrontCameraImage;

    public FrameLoop(MainActivity ma, FrameProcessing fp){
        main = ma;
        fproc = fp;
    }
    public void Loop()  {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if (main.getConnection()) {
                    fproc.getFramesBitmap();    //obtained Mat from camera.
                    View view = main.getCarView();
                    FrontCameraImage = (ImageView) view.findViewById(R.id.frontCamera);

                    if (FrontCameraImage != null) {     //it starts when completely loaded
                        FrontCameraImage.setImageBitmap(fproc.getFrontImgBitmap());
                        if(!main.getTextInitialization()) {
                            TextView text = (TextView) view.findViewById(R.id.textView2);
                            if (text != null) {
                                text.setText(main.getObjectToDetect());
                                main.setTextInitialization(true);
                            }
                        }
                    }
                    handler.postDelayed(this, 1);
                }
            }
        }, 1);
    }
}

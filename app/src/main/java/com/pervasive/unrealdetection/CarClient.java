package com.pervasive.unrealdetection;

public class CarClient {
    static {
        System.loadLibrary("carclient");
    }

    public native boolean CarConnect();
    public native void CarForward();
    public native void CarStop();
    public native void GetImage(long FrontImg);
}

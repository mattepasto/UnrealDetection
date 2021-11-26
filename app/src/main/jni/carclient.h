#include <jni.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/features2d.hpp>
#include <vector>

using namespace std;
using namespace cv;

extern "C" {
    JNIEXPORT jboolean JNICALL Java_com_pervasive_unrealdetection_MainActivity_CarConnect(JNIEnv *env, jobject);
    JNIEXPORT void JNICALL Java_com_pervasive_unrealdetection_MainActivity_CarForward(JNIEnv *env, jobject);
    JNIEXPORT void JNICALL Java_com_pervasive_unrealdetection_MainActivity_CarStop(JNIEnv *env, jobject);
    JNIEXPORT void JNICALL Java_com_pervasive_unrealdetection_MainActivity_GetImage(JNIEnv *env, jobject, jlong FrontImg);
}
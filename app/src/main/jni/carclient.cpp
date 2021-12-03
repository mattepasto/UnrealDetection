#include <jni.h>
#include "carclient.h"
#include <vehicles/car/api/CarRpcLibClient.hpp>

#include <opencv2/core.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/features2d.hpp>
#include <vector>
using namespace msr::airlib;
using std::cin;
using std::cout;
using std::endl;
using std::vector;
using namespace std;
using namespace cv;
typedef ImageCaptureBase::ImageRequest ImageRequest;
typedef ImageCaptureBase::ImageResponse ImageResponse;
typedef ImageCaptureBase::ImageType ImageType;

typedef VectorMathf VectorMath;
typedef VectorMath::Quaternionf Quaternionr;
typedef VectorMath::Pose Pose;

CarRpcLibClient * m_client;
Pose NewNavCam_pose;
Pose NewLeftCam_pose;

JNIEXPORT jboolean JNICALL Java_com_pervasive_unrealdetection_CarClient_CarConnect(JNIEnv *env, jobject)
{
    m_client = new CarRpcLibClient("10.0.2.2");
    m_client->confirmConnection();
    m_client->enableApiControl(true);
    bool isEnabled = m_client->isApiControlEnabled();
    return isEnabled;
}

JNIEXPORT void JNICALL Java_com_pervasive_unrealdetection_CarClient_CarForward(JNIEnv *env, jobject)
{
    if (!m_client)
        return;
    CarApiBase::CarControls controls;
    controls.throttle = 0.3f;
    controls.steering = 0.5f;
    m_client->setCarControls(controls);
}

JNIEXPORT void JNICALL Java_com_pervasive_unrealdetection_CarClient_CarStop(JNIEnv *env, jobject)
{
    if (!m_client)
        return;

    CarApiBase::CarControls controls;
    controls.brake = 1;
    controls.throttle = 0.0f;
    controls.is_manual_gear = 1;
    controls.manual_gear = 0;

    m_client->setCarControls(controls);
}

JNIEXPORT void JNICALL Java_com_pervasive_unrealdetection_CarClient_GetImage(JNIEnv *env, jobject, jlong FrontImg)
{
    // Check if a client has been instantiated
    if (!m_client)
        return;

    cv::Mat& front = *(Mat*)FrontImg;

    std::vector<ImageRequest> request = {
            ImageRequest("0", ImageType::Scene, false),
    };

    const std::vector<ImageResponse>& response = m_client->simGetImages(request);
     assert(response.size() > 0);
    if(response.size() > 0) {
        front = imdecode(response.at(0).image_data_uint8, ImreadModes::IMREAD_COLOR);
        cout << "raw image response: " << response.at(0).image_data_uint8.size() << endl;
    }
}

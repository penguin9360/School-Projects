#include "RobotIdentification.hh"
#include <opencv2/imgproc.hpp>
#include <iostream>

#include <dirent.h>

using namespace cv;
using namespace std;

int main() {
    RobotIdentification test;
    Mat scene = imread("../object-identification/scene-image/irobot_scene_1.jpg", IMREAD_GRAYSCALE);
    cout << "Read" << endl;
    test.runIdentify(scene);
    return 1;
}

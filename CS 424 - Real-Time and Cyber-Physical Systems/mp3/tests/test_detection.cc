#include <chrono>
#include <iostream>
#include <opencv2/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>

#include "../object-identification/object_identification.hh"

using namespace cv;
using namespace std;

using namespace std::chrono;

int main() {
    HandleObjectDetection test(NULL, NULL, NULL, (std::chrono::milliseconds) 0, 1);

    Mat in = imread("./img/20:39:45-0.jpg", IMREAD_GRAYSCALE),scene;
    cout << "Read" << endl;
    test.crop_bottom(in, scene, 1);
	test.load_query_images();
    auto start = system_clock::now();
    cout<< test.identify(test.lamp_mat, scene, test.lamp_descriptors, test.lamp_keypoints);
    test.run_identify(scene, "test");
    auto end = system_clock::now();

    duration<double> diff = end-start;
    cout << "Identification time: " << diff.count() <<endl;
    return 1;
}

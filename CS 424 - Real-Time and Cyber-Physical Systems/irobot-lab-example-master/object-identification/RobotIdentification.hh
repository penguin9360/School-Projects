#ifndef ROBOTIDENTIFICATION_HH
#define ROBOTIDENTIFICATION_HH
#include <chrono>
#include <string>
#include <fstream>
#include <iostream>
#include <opencv2/calib3d.hpp>
#include <opencv2/core.hpp>
#include <opencv2/features2d.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/xfeatures2d.hpp>

#include <dirent.h>

using namespace cv;
using namespace std;

struct QueryImage {
    string name;
    Mat image;
};

class RobotIdentification {
    public:
        RobotIdentification();
        bool runIdentify(Mat& scene_image);
        vector<QueryImage> query_images;
        int objects_found = 0;

    private:
        bool alignPerspective(vector<Point2f>& query, vector<Point2f>& scene,
            Mat& img_query, Mat& img_scene, vector<Point2f>& scene_corners);
        bool identify(Mat& query_image, Mat& scene_image, string output_file_name);
        void cropBottom(Mat& img_scene_full, Mat& img_scene, float crop_fraction);
        void drawProjection(Mat& img_matches, Mat& img_query,
            vector<Point2f>& scene_corners);
        string type2str(int type);
};

#endif

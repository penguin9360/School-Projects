#pragma once

#include <chrono>
#include <dirent.h>
#include <fstream>
#include <iostream>
#include <opencv2/calib3d.hpp>
#include <opencv2/core.hpp>
#include <opencv2/features2d.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/xfeatures2d.hpp>
#include <raspicam/raspicam_cv.h>
#include <string>

#include "../irobot/irobot-create.hh"
#include "../utility/shared_state.hh"
#include "naive_heuristic.hh"

using namespace cv;
using namespace cv::xfeatures2d;
using namespace std;

// using namespace Color;

namespace {
const string query_dir = "./object-identification/query-image/low-resolution";
}

struct Image {
  string name;
  Mat image;
};

class HandleObjectDetection {
public:
  HandleObjectDetection(iRobot::Create *robot, SharedState *ss,
                        raspicam::RaspiCam_Cv *camera,
                        const std::chrono::milliseconds sleep_ms,
                        const bool defuse);
  void operator()() { handle_object_identification_(); }
  void process_buffered_frames();

  // Does object detection on a single image. Need to test this function, hence
  // public
  void run_identify(Mat &scene_image, string current_t_str);
  void load_query_images();
  
  bool identify(Mat &query_image, Mat &img_scene, Mat &descriptors_query,
                vector<KeyPoint> &keypoints_query);
  void crop_bottom(Mat &img_scene_full, Mat &img_scene, float crop_fraction);
  
  Mat lamp_mat, lamp_descriptors;
  vector<KeyPoint> lamp_keypoints;

private:
  iRobot::Create *robot_;
  SharedState *ss_;
  raspicam::RaspiCam_Cv *camera_;
  const std::chrono::milliseconds sleep_ms_;

  // Vector to store image strings
  vector<string> images_stored;

  // MP3 modifications
  const bool defuse_;
  

  // Map to store keypoints and descriptors for query images
  vector<Image> query_images;
  map<string, Mat> computedDescriptors;
  map<string, vector<KeyPoint>> computedKeyPoints;

  // Detect the keypoints and extract descriptors using SURF
  // Surf keypoint detector and descriptor.
  int min_hessian = 100;
  int n_octaves = 4;
  int n_octave_layers = 3;
  Ptr<SURF> detector =
      SURF::create(min_hessian, n_octaves, n_octave_layers, true);

  // Called by main thread to do object detection in loop
  void handle_object_identification_();

  // Helper functions for object detection
  bool align_perspective(vector<Point2f> &query, vector<Point2f> &scene,
                         Mat &img_query, Mat &img_scene,
                         vector<Point2f> &scene_corners);

  // Photo heursitic
  const int img_width_ = 1280;
  const int img_height_ = 960;
  NaiveHeuristic h_;
  
  // Deprecated
  vector<Image> buffered_frames;
  void handle_object_identification_in_memory();
  void process_buffered_frames_in_memory();
};

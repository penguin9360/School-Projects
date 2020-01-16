/*
 * Known object identification using Open CV
 * Author: Tanvir Amin
 * tanviralamin@gmail.com
 * maamin2@illinois.edu
 */

#include <dirent.h>
#include <raspicam/raspicam_cv.h>
#include <thread>

#include "../contour-mapper/contour_mapper.hh"
#include "../navigation-manager/navigation_manager.hh"
#include "naive_heuristic.hh"
#include "object_identification.hh"

using namespace cv;
using namespace cv::xfeatures2d;
using namespace std;
using namespace std::chrono;

using iRobot::Create;
using std::cout;
using std::endl;
using std::string;
using std::vector;

const string currentTime() {
  time_t now = time(0);
  struct tm tstruct;
  char buf[80];
  tstruct = *localtime(&now);
  strftime(buf, sizeof(buf), "%X", &tstruct);
  return buf;
}

HandleObjectDetection::HandleObjectDetection(
    iRobot::Create *robot, SharedState *ss, raspicam::RaspiCam_Cv *camera,
    const std::chrono::milliseconds sleep_ms, const bool defuse)
    : robot_(robot), ss_(ss), camera_(camera), sleep_ms_(sleep_ms),
      defuse_(defuse), h_(NaiveHeuristic(img_width_, img_height_)) {

  DIR *pDIR;
  struct dirent *image;

  if (pDIR = opendir(query_dir.c_str())) {
    while (image = readdir(pDIR)) {
      if (strcmp(image->d_name, ".") != 0 && strcmp(image->d_name, "..") != 0  && strcmp(image->d_name, "magic-lamp-600.jpg") == 0) {
        cout << "Reading in " << image->d_name << endl;
        Mat image_mat =
            imread(query_dir + "/" + image->d_name, IMREAD_GRAYSCALE);
        Image input = {image->d_name, image_mat};
        query_images.push_back(input);

        // Precompute keypoints and descriptors
        Mat descriptors_query;
        vector<KeyPoint> keypoints_query;
        detector->detectAndCompute(image_mat, Mat(), keypoints_query,
                                   descriptors_query);
        computedDescriptors[image->d_name] = descriptors_query;
        computedKeyPoints[image->d_name] = keypoints_query;

        if(defuse_ && strcmp(image->d_name, "magic-lamp-600.jpg") == 0){
          lamp_mat = image_mat;
          lamp_descriptors = descriptors_query;
          lamp_keypoints = keypoints_query; 
        }
      }
    }
  }

  // if (defuse_) {
  //   if (pDIR = opendir(query_dir.c_str())) {
  //     while (image = readdir(pDIR)) {
  //       if (strcmp(image->d_name, "magic-lamp-600.jpg") == 0) {
  //         cout << "Reading in " << image->d_name << endl;
  //         Mat lamp_mat =
  //             imread(query_dir + "/" + image->d_name, IMREAD_GRAYSCALE);
  //         detector->detectAndCompute(lamp_mat, Mat(), lamp_keypoints,
  //                                    lamp_descriptors);
  //       }
  //     }
  //   }
  // }
}

void HandleObjectDetection::handle_object_identification_() {

  bool lamp_defused = false;
  cv::Mat image, bgr_image, downsampled_image;
  string current_t_str, img_scene_name;

  int tossed = 0;

  while (!ss_->done) {
    if (ss_->go) {
      cout << "Grabbing an image\n";
      camera_->grab();
      camera_->retrieve(bgr_image);
      cv::cvtColor(bgr_image, image, CV_BGR2GRAY);
      cv::resize(image, downsampled_image, cv::Size(), 0.75, 0.75);

      if (h_.should_keep(image)) {
        std::vector<Mat> segmented_imgs;
        h_.segment(image, &segmented_imgs);
        std::cout << "Segmented image into " << segmented_imgs.size()
                  << " imgs\n";
        current_t_str = currentTime();
        for (int i = 0; i < segmented_imgs.size(); ++i) {
          img_scene_name = "./img/" + current_t_str + "-" + std::to_string(i) + ".jpg";
          images_stored.push_back(current_t_str + "-" + std::to_string(i));
          cv::imwrite(img_scene_name, segmented_imgs[i]);
        }

        bool id = identify(lamp_mat, image, lamp_descriptors, lamp_keypoints);
        cout << "Lamp detector: " << defuse_ << !lamp_defused << id << endl;
        if (defuse_ && !lamp_defused && id) {
          ss_->robot_mutex->lock();
          robot_->sendLedCommand(Create::LED_NONE, Create::LED_COLOR_RED,
                                 Create::LED_INTENSITY_FULL);
          ss_->robot_mutex->unlock();

          std::this_thread::sleep_for(chrono::milliseconds(2000));

          ss_->robot_mutex->lock();
          robot_->sendLedCommand(Create::LED_NONE, Create::LED_COLOR_RED,
                                 Create::LED_INTENSITY_OFF);
          ss_->robot_mutex->unlock();

          lamp_defused = true;
        }
      } else {
        std::cout << "[HEURISTIC] TOSS.\n";
        tossed++;
      }
    }
    std::this_thread::sleep_for(sleep_ms_);
  }
  cout << "Tossed: " << tossed << " images\n";
}

void HandleObjectDetection::load_query_images() {
  DIR *pDIR;
  struct dirent *image;

  if (pDIR = opendir(query_dir.c_str())) {
    while (image = readdir(pDIR)) {
      if (strcmp(image->d_name, ".") != 0 && strcmp(image->d_name, "..") != 0 && strcmp(image->d_name, "magic-lamp-600.jpg") != 0) {
        cout << "Reading in " << image->d_name << endl;
        Mat image_mat =
            imread(query_dir + "/" + image->d_name, IMREAD_GRAYSCALE);
        Image input = {image->d_name, image_mat};
        query_images.push_back(input);

        // Precompute keypoints and descriptors
        Mat descriptors_query;
        vector<KeyPoint> keypoints_query;
        detector->detectAndCompute(image_mat, Mat(), keypoints_query,
                                   descriptors_query);
        computedDescriptors[image->d_name] = descriptors_query;
        computedKeyPoints[image->d_name] = keypoints_query;
      }
    }
  }
}

void HandleObjectDetection::process_buffered_frames() {

  load_query_images();
  int num_frames = images_stored.size();

  cout << "Collected " << num_frames << " images\n";

  auto start = system_clock::now();
  int i = 0;
  while (!images_stored.empty()) {
    string current_t_str = images_stored[0];
    string img_scene_name = "./img/" + current_t_str + ".jpg";
    Mat scene = imread(img_scene_name, IMREAD_GRAYSCALE);
    images_stored.erase(images_stored.begin());
    cout << "Processing frame " << i << " captured at " << current_t_str
         << endl;
    run_identify(scene, current_t_str);
    i++;
  }
  auto end = system_clock::now();

  duration<double> diff = end - start;
  cout << "Identification time: " << diff.count() << endl;
}

void HandleObjectDetection::run_identify(Mat &scene_image,
                                         string current_t_str) {
  string img_scene_name = "./data/" + current_t_str + ".jpg";
  string img_scene_file = "./data/" + current_t_str + ".txt";

  Mat cropped_scene;
  crop_bottom(scene_image, cropped_scene, 1);

  int objects_found = 0;
  for (std::vector<Image>::iterator it = query_images.begin();
       it != query_images.end();) {

    Mat descriptors_query = computedDescriptors.find(it->name)->second;
    vector<KeyPoint> keypoints_query = computedKeyPoints.find(it->name)->second;

    if (identify(it->image, cropped_scene, descriptors_query,
                 keypoints_query)) {
      ofstream myfile;
      myfile.open(img_scene_file, ofstream::out | ofstream::app);
      myfile << "Found: " << to_string(++objects_found) << ": " << it->name
             << "\n\n";
      myfile.close();
    }
    it++;
  }
  cout << "Objects found: " << objects_found << endl;
  if (objects_found > 0)
    cv::imwrite(img_scene_name, scene_image);
}

bool HandleObjectDetection::identify(Mat &img_query, Mat &img_scene,
                                     Mat &descriptors_query,
                                     vector<KeyPoint> &keypoints_query) {
  try {

    // Crop bottom
    // Images taken by mounting the camera on the robot will have some portion
    // of the side of the robot at the bottom. To reduce ambiguity during
    // detection and to speed up feature extraction, we crop it.
    // The fraction of cropping will be different depending on where the camera
    // is mounted on the robot. We find the useful portion of the picture is
    // the top 62.5% when camera mounted on front. When camera mounted on the
    // left side its the top 85% that contains useful information.

    cout<<"keypoints: "<<keypoints_query.size()<<endl;
    Mat descriptors_scene;
    vector<KeyPoint> keypoints_scene;

    detector->detectAndCompute(img_scene, Mat(), keypoints_scene,
                               descriptors_scene);

    // No descriptor found
    if (countNonZero(descriptors_scene) < 1)
      return false;

    // Matching descriptor vectors using Brute Force matcher
    vector<vector<DMatch>> matches;
    BFMatcher matcher(NORM_L2);
    matcher.knnMatch(descriptors_query, descriptors_scene, matches, 2);

    // Find the location of the query in the scene
    vector<Point2f> query;
    vector<Point2f> scene;
    for (int i = 0; i < descriptors_query.rows; i++) {
      if (matches[i][0].distance < 0.75 * matches[i][1].distance) {
        query.push_back(keypoints_query[matches[i][0].queryIdx].pt);
        scene.push_back(keypoints_scene[matches[i][0].trainIdx].pt);
      }
    }

    vector<Point2f> scene_corners(4);
    bool res =
        align_perspective(query, scene, img_query, img_scene, scene_corners);
    cout << "Matching and alignment" << endl;

    if (res) {
      cout << "Object found" << endl;
    } else {
      cout << "Object not found" << endl;
    }
    return res;
  } catch (cv::Exception &e) {
    cout << "OpenCV Exception: " << e.what();
    return false;
  }
  return false;
}

void HandleObjectDetection::crop_bottom(Mat &img_scene_full, Mat &img_scene,
                                        float crop_fraction) {
  // Crop the lower part of the scene
  cv::Rect crop;
  crop.x = 0;
  crop.y = 0;
  crop.width = img_scene_full.size().width;
  crop.height = img_scene_full.size().height * crop_fraction;
  img_scene = img_scene_full(crop);
}

bool HandleObjectDetection::align_perspective(vector<Point2f> &query,
                                              vector<Point2f> &scene,
                                              Mat &img_query, Mat &img_scene,
                                              vector<Point2f> &scene_corners) {
  Mat H = findHomography(query, scene, RANSAC);
  if (H.rows == 0 && H.cols == 0) {
    cout << "Failed rule0: Empty homography" << endl;
    return false;
  }

  vector<Point2f> query_corners(4);
  query_corners[0] = cvPoint(0, 0);
  query_corners[1] = cvPoint(img_query.cols, 0);
  query_corners[2] = cvPoint(img_query.cols, img_query.rows);
  query_corners[3] = cvPoint(0, img_query.rows);

  perspectiveTransform(query_corners, scene_corners, H);

  float min_area = 32.0 * 32.0;
  double max_area = img_scene.rows * img_scene.cols;
  float ratio_inside = 0.75;
  float min_angle_sin = 0.173; // Minimum 10 degree angle required

  // Geometric verification heuristics
  // Rule 1: Must be a convex hull.
  // Rule 2: Area can’t be less than 32x32
  // Rule 3: The detected projection can’t have more than 100% area
  // Rule 4: Projection can't contain very small angle < 10 degree
  // Rule 5: More than 75% of the area of the detected projection should have
  // to be within image bounds

  // Rule 1: Must be a convex hull.
  vector<Point2f> sc_vec(4);
  // Generate 4 vectors from the 4 scene corners
  for (int i = 0; i < 4; i++) {
    sc_vec[i] = scene_corners[(i + 1) % 4] - scene_corners[i];
  }
  vector<float> sc_cross(4);
  // Calculate cross product of pairwise vectors
  for (int i = 0; i < 4; i++) {
    sc_cross[i] = sc_vec[i].cross(sc_vec[(i + 1) % 4]);
  }

  // Check for convex hull
  if (!(sc_cross[0] < 0 && sc_cross[1] < 0 && sc_cross[2] < 0 &&
        sc_cross[3] < 0) &&
      !(sc_cross[0] > 0 && sc_cross[1] > 0 && sc_cross[2] > 0 &&
        sc_cross[3] > 0)) {
    // cout << "Failed rule1: Not a convex hull" << endl;
    return false;
  }

  // Rule 2: Area can’t be less than 32x32
  // Rule 3: The detected projection can’t have more than 100% area
  float area = (sc_cross[0] + sc_cross[2]) / 2.0;
  if (fabs(area) < min_area) {
    // cout << "Failed rule2: Projection too small" << endl;
    return false;
  } else if (fabs(area) > max_area) {
    // cout << "Failed rule3: Projection too large" << endl;
    return false;
  }

  // Rule 4: Can't contain very small angle < 10 degree inside projection.
  // Check for angles
  vector<float> sc_norm(4);
  for (int i = 0; i < 4; i++) {
    sc_norm[i] = norm(sc_vec[i]);
  }
  for (int i = 0; i < 4; i++) {
    float sint = sc_cross[i] / (sc_norm[i] * sc_norm[(i + 1) % 4]);
    if (fabs(sint) < min_angle_sin) {
      // cout << "Failed rule4: Contains very small angle" << endl;
      return false;
    }
  }

  // Rule 5: More than 75% of the area of the detected projection should
  // have to be within image bounds.
  // Approximate mechanism by determining the bounding rectangle.
  cv::Rect bound = boundingRect(scene_corners);
  cv::Rect scene_rect(0.0, 0.0, img_scene.cols, img_scene.rows);
  cv::Rect isect = bound & scene_rect;
  if (isect.width * isect.height < ratio_inside * bound.width * bound.height) {
    // cout << "Failed rule5: Large proportion outside scene" << endl;
    return false;
  }
  return true;
}

// Don't use this function, it crashes the Pi
void HandleObjectDetection::handle_object_identification_in_memory() {
  string current_t_str;
  cv::Mat image, bgr_image;
  while (!ss_->done) {
    if (ss_->go) {
      cout << "Grabbing an image\n";
      camera_->grab();
      camera_->retrieve(bgr_image);
      cv::cvtColor(bgr_image, image, CV_BGR2GRAY);
      current_t_str = currentTime();
      Image frame = {current_t_str, image};
      buffered_frames.push_back(frame);
    }
    std::this_thread::sleep_for(sleep_ms_);
  }
  process_buffered_frames_in_memory();
}

void HandleObjectDetection::process_buffered_frames_in_memory() {
  int num_frames = buffered_frames.size();

  cout << "Collected " << num_frames << endl;

  auto start = system_clock::now();
  int i = 0;
  while (!buffered_frames.empty()) {
    Image frame = buffered_frames[0];
    buffered_frames.erase(buffered_frames.begin());
    cout << "Processing frame " << i << " captured at " << frame.name << endl;
    run_identify(frame.image, frame.name);
    i++;
  }
  auto end = system_clock::now();

  duration<double> diff = end - start;
  cout << "Identification time: " << diff.count() << endl;
}

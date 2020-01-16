#include <cmath>
#include <iostream>
#include <vector>

#include "../navigation-manager/navigation_manager.hh"
#include "contour_mapper.hh"

#define PI 3.14159265F

int IMG_WIDTH = 2000;
int IMG_HEIGHT = 2000;

float SCALEDOWN_FACTOR = 8.0F; // Factor by which to scale back the position (in
                               // mm) to fit into the image frame

ContourMapper::ContourMapper(BlockingQueue<std::pair<int, float>> *queue)
    : queue_(queue), pos_(cv::Point2f(0, 0)), dir_(cv::Point2f(0, 1)),
      max_abs_dim_(0) {
  // (Add initial position) - Store a scaled down position by a factor to keep
  // output image space small, then translate to keep centered
  Point2f scaled_pt(pos_.x, pos_.y);
  waypoints_.push_back(scaled_pt);
}

void ContourMapper::update_contour() {
  // Pull the directives currently in the queue before processing
  std::vector<std::pair<int, float>> directive_list;
  int num_items_found = queue_->size();
  for (int i = 0; i < num_items_found; i++) {
    directive_list.push_back(queue_->pop());
  }

  // Process each directive w.r.t. currently recorded pos & dir
  std::vector<std::pair<int, float>>::const_iterator iterator;
  for (iterator = directive_list.begin(); iterator != directive_list.end();
       ++iterator) {
    float dist = (float)iterator->first;
    float rotate_deg = iterator->second;

    // Get new waypoint from distance traveled along previous trajectory, and
    // add it to the vector
    pos_ += dist * dir_;

    if (std::abs(pos_.x) > max_abs_dim_) {
      max_abs_dim_ = std::abs(pos_.x);
    }

    if (std::abs(pos_.y) > max_abs_dim_) {
      max_abs_dim_ = std::abs(pos_.y);
    }

    // Store a scaled down position by a factor to keep output image space
    // small, then translate to keep centered
    Point2f new_pt(pos_.x, (-1.0F * pos_.y));
    waypoints_.push_back(new_pt);

    // Update direction (may not change if no turn was made), normalized
    dir_ = apply_rotation_(rotate_deg);
  }
}

cv::Point2f ContourMapper::apply_rotation_(float rotate_deg) {
  // V.x = U.x * cos(deg) - U.y * sin(deg) ; V.y = U.x * sin(deg) + U.y *
  // cos(deg)
  float x_abs = dir_.x * cos(rotate_deg) - dir_.y * sin(rotate_deg);
  float y_abs = dir_.x * sin(rotate_deg) + dir_.y * cos(rotate_deg);

  // Normalize, and return the result
  float mag = (x_abs * x_abs) + (y_abs * y_abs);
  Point2f new_dir((x_abs / mag), (y_abs / mag));
  return new_dir;
}

void ContourMapper::draw_contour() {
  // Process any remaining directives into waypoints before starting
  update_contour();

  // Taken from the hints.md file
  cv::Mat img_output(IMG_WIDTH, IMG_HEIGHT, CV_8UC3, cv::Scalar(255, 255, 255));
  cv::Scalar line_color(255, 0, 0);
  int line_width = 1;
  int radius = 3;

  // Compute worst case scaling factor required (if any) to apply
  float scale_factor =
      ((float)(max_abs_dim_)) / ((float)(IMG_WIDTH / 2.0F)) * 1.2F;

  if (scale_factor < 1.0F) {
    scale_factor = 1.0F;
  }

  // Trace out the path via dots & lines connecting
  std::vector<cv::Point2f> scaled_waypoints;
  for (int i = 0; i < waypoints_.size() - 1; i++) {
    Point2f pt1((waypoints_[i].x / scale_factor) + (IMG_WIDTH / 2.0F),
                (waypoints_[i].y / scale_factor) + (IMG_WIDTH / 2.0F));
    Point2f pt2((waypoints_[i + 1].x / scale_factor) + (IMG_WIDTH / 2.0F),
                (waypoints_[i + 1].y / scale_factor) + (IMG_WIDTH / 2.0F));
    scaled_waypoints.push_back(pt1);

    arrowedLine(img_output, pt1, pt2, line_color, line_width, CV_AA);
    circle(img_output, pt1, radius, line_color, CV_FILLED, CV_AA);
  }

  // Draw boundary
  cv::Rect bound = boundingRect(scaled_waypoints);
  rectangle(img_output, bound, cv::Scalar(0, 165, 255));

  // Store as a PNG file
  imwrite("irobot_plot.png", img_output);
}

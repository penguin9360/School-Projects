#include "naive_heuristic.hh"

#include <utility>
#include <opencv2/imgproc.hpp>

namespace {
using cv::Mat;
using cv::Point;
using cv::Rect;
const double kWhiteLowTol = 153;
const double kWhiteHighTol = 230;
const double kGoodRatio = 0.10;
const int kMinRectArea = 200000;
} // namespace

bool NaiveHeuristic::should_keep(const Mat &img) {
  // Convert to grayscale
  Mat gray_img = img.clone(); // copy
  filter_white_pixels_(&gray_img);
  const double n_white_pix = cv::sum(gray_img)[0] / 255.0;
  return n_white_pix >= thresh_white_pixels_;
}

void NaiveHeuristic::segment(const Mat &img, std::vector<Mat> *imgs) {
  Mat gray_img = img.clone(); // copy
  filter_white_pixels_(&gray_img);
  std::vector<std::vector<Point>> contours;
  cv::findContours(gray_img, contours, cv::RETR_LIST, cv::CHAIN_APPROX_NONE);
  Rect rect;
  for (int i = 0; i < contours.size(); ++i) {
    rect = cv::boundingRect(contours[i]);
    if (rect.area() >= kMinRectArea) {
      imgs->push_back(std::move(img(rect).clone()));
    }
  }
}

void NaiveHeuristic::filter_white_pixels_(Mat *img) {
  Mat &img_ref = *img;
  img_ref.setTo(0, img_ref > kWhiteHighTol);
  img_ref.setTo(0, img_ref < kWhiteLowTol);
  return;
}

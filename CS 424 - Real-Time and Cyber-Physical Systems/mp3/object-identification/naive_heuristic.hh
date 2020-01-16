#pragma once
#include "heuristic.hh"

#include <opencv2/core.hpp>
#include <vector>

class NaiveHeuristic : public Heuristic {
public:
  NaiveHeuristic(){}
  NaiveHeuristic(int w, int h) : total_pixels_(w * h) {
    thresh_white_pixels_ = good_ratio_ * total_pixels_;
  }
  bool should_keep(const cv::Mat &img);
  void segment(const cv::Mat& img, std::vector<cv::Mat>* imgs);

private:
  const double good_ratio_ = 0.1;
  int total_pixels_;
  int thresh_white_pixels_;
  void filter_white_pixels_(cv::Mat* img);
};

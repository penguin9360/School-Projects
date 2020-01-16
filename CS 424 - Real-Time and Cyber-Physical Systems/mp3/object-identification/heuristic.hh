#pragma once
#include <opencv2/core.hpp>

class Heuristic {
  virtual bool should_keep(const cv::Mat &img) = 0;
};

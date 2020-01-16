#include "controller.hh"
#include <iostream>

namespace {
const float kDampen = 0.15;
}

float Controller::calulateTurnAngle(short curr_wall_sig) {
  const short sig_diff = curr_wall_sig - target_wall_sig_;
  std::cout << sig_diff << " deviation from " << target_wall_sig_ << "\n";
  return kDampen * (-1 * kp_ * sig_diff);
}

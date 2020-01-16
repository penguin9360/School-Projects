#pragma once

class Controller {
public:
  Controller(short target_wall_sig, float kp)
      : target_wall_sig_(target_wall_sig), kp_(kp){};
  float calulateTurnAngle(short curr_wall_sig);

private:
  short target_wall_sig_;
  float kp_;
};

#include <iostream>

#include "navigation_manager.hh"

namespace {
using iRobot::Create;
}

float moving_turn_scaling_factor = 0.62;

NavigationManager::NavigationManager(Create *robot, std::mutex *robot_lock)
    : robot_(robot), curr_speed_(-1), ts_last_set_speed_ms_(-1),
      curr_drive_cmd_(Create::DriveCommand::DRIVE_STRAIGHT),
      curr_turn_radius_(0), robot_lock_(robot_lock) {}

void NavigationManager::sendDriveCommand(short speed,
                                         Create::DriveCommand drive_command,
                                         bool should_record) {
  if (should_record && ts_last_set_speed_ms_ != -1) { // Flush a coord
    // Push command if it is different than the previous.
    if (curr_turn_radius_ == 0 && speed == curr_speed_ &&
        curr_drive_cmd_ == drive_command) {
      return; // No need to send same command twice
    }
    coords_.push(make_directive_());
  }

  // Update state
  robot_lock_->lock();
  robot_->sendDriveCommand(speed, drive_command);
  robot_lock_->unlock();

  curr_speed_ = speed;
  curr_drive_cmd_ = drive_command;
  curr_turn_radius_ = 0;
  ts_last_set_speed_ms_ =
      std::chrono::duration_cast<std::chrono::milliseconds>(
          std::chrono::system_clock::now().time_since_epoch())
          .count();
}

void NavigationManager::sendDriveCommand(short speed, short radius,
                                         bool should_record) {
  if (should_record && ts_last_set_speed_ms_ != -1) { // Flush a coord
    if (curr_turn_radius_ == radius && curr_speed_ == radius) {
      return; // No need to send same command twice
    }
    coords_.push(make_directive_());
  }

  // Update state; treat as if it was an in-place turn ~ should be fairly sharp
  robot_lock_->lock();
  robot_->sendDriveCommand(speed, radius);
  robot_lock_->unlock();

  curr_turn_radius_ = radius;
  curr_speed_ = speed * moving_turn_scaling_factor;  // Scale speed by a factor to model the complex turn as a simple turn
  curr_drive_cmd_ =
      Create::DriveCommand::DRIVE_INPLACE_CLOCKWISE; // Only used for right turns ~ abstract from complex -> simple turns
  ts_last_set_speed_ms_ =
      std::chrono::duration_cast<std::chrono::milliseconds>(
          std::chrono::system_clock::now().time_since_epoch())
          .count();
}

std::pair<int, float> NavigationManager::get_coord() { return coords_.pop(); }

std::pair<int, float> NavigationManager::make_directive_() {
  const long current_ts_ms =
      std::chrono::duration_cast<std::chrono::milliseconds>(
          std::chrono::system_clock::now().time_since_epoch())
          .count();
  const float elapsed_time = (current_ts_ms - ts_last_set_speed_ms_) / 1000.0;

  const float dist = elapsed_time * curr_speed_;

  // Check if last move was a turn.
  if (curr_drive_cmd_ == Create::DRIVE_INPLACE_CLOCKWISE ||
      curr_drive_cmd_ == Create::DRIVE_INPLACE_COUNTERCLOCKWISE) {
    const float angle_rad = ((dist / kRobotCircumference) * 360 * 3.142) / 180;
    if (curr_drive_cmd_ == Create::DRIVE_INPLACE_CLOCKWISE) { // left turn
      // left turn yields negative angles
      return std::pair<int, float>(0, -1 * angle_rad);
    } else { // right turn
      return std::pair<int, float>(0, angle_rad);
    }
  }

  // straight or backing-up
  if (curr_speed_ < 0) {
    // back-up
    return std::pair<int, float>(-1 * dist, 0);
  } else {
    // going straight
    return std::pair<int, float>(dist, 0);
  }
}

#pragma once

#include "../irobot/irobot-create.hh"
#include "../utility/blocking_queue.hh"

#include <chrono>
#include <iostream>
#include <mutex>
#include <utility>

class NavigationManager {
public:
  NavigationManager(iRobot::Create *robot, std::mutex *robot_lock);

  void sendDriveCommand(short speed, iRobot::Create::DriveCommand drivecommand,
                        bool should_record = true);

  void sendDriveCommand(short speed, short radius, bool should_record = true);

  std::pair<int, float> get_coord();

  BlockingQueue<std::pair<int, float>> *get_directives() { return &coords_; }

private:
  iRobot::Create *robot_;
  short curr_speed_;
  long ts_last_set_speed_ms_; // The time of last set speed
  iRobot::Create::DriveCommand
      curr_drive_cmd_; // The last relative direction traveled
  short curr_turn_radius_;
  const int kRobotDiameter = 240; // mm
  const int kRobotCircumference = 240 * 3.142;

  BlockingQueue<std::pair<int, float>> coords_;
  std::mutex *robot_lock_;

  std::pair<int, float> make_directive_();
};

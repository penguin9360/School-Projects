#pragma once

#include "../irobot/irobot-create.hh"

#include <iostream>
#include <mutex>

class NavigationManager {
public:
  NavigationManager(iRobot::Create *robot, std::mutex *robot_lock);
  void sendDriveCommand(short speed, iRobot::Create::DriveCommand drivecommand);
  void sendDriveCommand(short speed, short radius);
  void sendDirectDriveCommand(short speed, short radius);
private:
  iRobot::Create *robot_;
  const int kRobotDiameter = 240; // mm
  const int kRobotCircumference = 240 * 3.142;
  std::mutex *robot_lock_;
};

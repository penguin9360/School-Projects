#include <iostream>

#include "navigation_manager.hh"

namespace {
using iRobot::Create;
}

NavigationManager::NavigationManager(Create *robot, std::mutex *robot_lock)
    : robot_(robot), robot_lock_(robot_lock) {}

void NavigationManager::sendDriveCommand(short speed,
                                         Create::DriveCommand drive_command) {
  robot_lock_->lock();
  robot_->sendDriveCommand(speed, drive_command);
  robot_lock_->unlock();
}

void NavigationManager::sendDriveCommand(short speed, short radius) {
  robot_lock_->lock();
  robot_->sendDriveCommand(speed, radius);
  robot_lock_->unlock();
}

void NavigationManager::sendDirectDriveCommand(short vl, short vr) {
  robot_lock_->lock();
  robot_->sendDriveDirectCommand(vl, vr);
  robot_lock_->unlock();
}

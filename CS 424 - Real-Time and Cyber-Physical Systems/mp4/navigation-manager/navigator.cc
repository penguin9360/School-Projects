#include "navigator.hh"
#include "../contour-mapper/contour_mapper.hh"
#include "../navigation-manager/navigation_manager.hh"

#include <iostream>
#include <mutex>
#include <queue>
#include <thread>

#include <boost/accumulators/accumulators.hpp>
#include <boost/accumulators/statistics.hpp>
#include <boost/accumulators/statistics/rolling_mean.hpp>

namespace {

using boost::accumulators::rolling_mean;
using iRobot::CommandNotAvailable;
using iRobot::Create;
using iRobot::InvalidArgument;
using LibSerial::SerialStream;

using std::cerr;
using std::cout;
using std::endl;
using std::ios_base;
using std::vector;

using DriveCommand = iRobot::Create::DriveCommand;

// DEBUG FLAG
int DBG = 0;

const std::chrono::milliseconds kNavigationSleepMs =
    std::chrono::milliseconds(15); // Originally 15A
const std::chrono::milliseconds kLeftTurnSleepMs =
    std::chrono::milliseconds(1900);
const std::chrono::milliseconds kRightTurnSleepMs =
    std::chrono::milliseconds(850); // Originally 950

// Foce a certain amount of sleeps to amortize period up to target
const std::chrono::milliseconds kForcesleepMs = std::chrono::milliseconds(500);
const short kForcesleepCycleMod = 50; // ~ Amortize the period -> 60ms

// Robot Navigation Constants
const short kDoneTurnWindow = 100;         // Originally 100
const short kMisidentifyRightWindow = 120; // Originally 120

const short kAlignSpeed = 50;
const short kLeftTurnSpeed = 100;
const short kTurnRightSpeed = 275; // Originally 300
const short kFastSpeed = 175;
const short kSlowSpeed = 175; // Used for STRAIGHT ONLY movement
const short kBackupSpeed = -225;

const short kRightTurnRadius = -175;       // Originally -150
const short kWallDropoffThreshold = 20;    // Originally 20
const short kWallDifferenceThreshold = 26; // Originally 20
const short kWallMinThreshold = 50;
const short kWallRightThreshold = 100;

} // namespace
Navigator::Navigator(iRobot::Create *robot, SharedState *ss,
                     const std::chrono::milliseconds sleep_ms)
    : robot_(robot), ss_(ss), sleep_ms_(sleep_ms), nav_state(NavState::BOOT),
      wall_sigs(boost::accumulators::tag::rolling_window::window_size = ws_),
      prev_wall_sigs(boost::accumulators::tag::rolling_window::window_size =
                         ws_),
      prev_prev_wall_sigs(
          boost::accumulators::tag::rolling_window::window_size = ws_) {
  for (int i = 0; i < ws_; i++) {
    wall_sig_q.push(0);
    prev_wall_sig_q.push(0);

    wall_sigs(0);
    prev_wall_sigs(0);
    prev_prev_wall_sigs(0);
  }
}

void Navigator::drive() {

  // 2 independent right turns in a period of X cycles consitutes end of maze
  int cycles_since_last_forcesleep = 0;
  int cycles_since_last_right = 0;
  short wall_sig;
  bool bump_left, bump_right, change = true;
  NavigationManager &nav_manager = *(ss_->nav_manager);

  // Main while loop.
  while (!ss_->done) {

    // cout << "MOD == " << (cycles_since_last_forcesleep % kForcesleepCycleMod)
    // << endl;

    // Every X cycles, send stop command and just sleep
    if (cycles_since_last_forcesleep % kForcesleepCycleMod == 0 &&
        nav_state == NavState::STRAIGHT) {
      nav_manager.sendDriveCommand(0, Create::DriveCommand::DRIVE_STRAIGHT);
      std::this_thread::sleep_for(kForcesleepMs);
      change = true;
    }

    // Thread safe sensor readings
    std::tie(wall_sig, bump_left, bump_right) = read_sensors_();

    // Track rolling means of wall signals via queues that hold last X ~ 3
    // contiguous sliding windows
    short to_prev_wall_sig = wall_sig_q.front();
    short to_prev_prev_wall_sig = prev_wall_sig_q.front();

    wall_sig_q.pop();
    prev_wall_sig_q.pop();
    wall_sig_q.push(wall_sig);
    prev_wall_sig_q.push(to_prev_wall_sig);

    // Update rolling means ~ [last X][last X, offset of X][last X, offset of
    // 2*X]
    wall_sigs(wall_sig);
    prev_wall_sigs(to_prev_wall_sig);
    prev_prev_wall_sigs(to_prev_prev_wall_sig);

    // cout << "\n" << endl;
    // cout << "bump sig: " << (bump_left || bump_right) << endl;
    // cout << "wall sig: " << wall_sig << endl;

    if (DBG) {
      cout << rolling_mean(wall_sigs) << " < " << rolling_mean(prev_wall_sigs)
           << " > " << rolling_mean(prev_prev_wall_sigs) << endl;

      cout << cycles_since_last_right << endl;
    }

    switch (nav_state) {
    case NavState::BOOT:
      // BOOT NavState: On startup, naively head straight until bump occurs,
      // then turn left
      if (bump_left || bump_right) {
        // If bump encountered, begin to backup, and turn left
        nav_manager.sendDriveCommand(kBackupSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        nav_state = NavState::TURN_LEFT;
        change = true;

      } else if (change) {
        // Otherwise, continue to drive forward
        nav_manager.sendDriveCommand(
            kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);
        change = false;
      }

      if (DBG) {
        cout << "BOOT" << endl;
      }
      break;

    case NavState::STRAIGHT:
      // STRAIGHT NavState: Default Navstate, moving straight forward until bump
      // sensors, or wall signal falloff

      if (DBG) {
        cout << "STRAIGHT" << endl;
      }

      if (bump_left || bump_right) {
        // If bump encountered, begin to backup, and turn left
        nav_manager.sendDriveCommand(kBackupSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        // nav_state = NavState::TURN_LEFT;

        // If we are coming out a right turn and bumping, it's an align; else,
        // we should turn
        if (cycles_since_last_right < kMisidentifyRightWindow) {
          nav_state = NavState::ALIGN_LEFT;
          change = true;
        } else {
          nav_state = NavState::TURN_LEFT;
          change = true;
        }

      } else if (rolling_mean(wall_sigs) < kWallDropoffThreshold &&
                 (rolling_mean(wall_sigs) + kWallDifferenceThreshold <
                  rolling_mean(prev_wall_sigs))) {
        // If there was a SUDDEN decrease in wall signal windows, you should
        // right turn
        nav_manager.sendDriveCommand(kFastSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        std::this_thread::sleep_for(kRightTurnSleepMs);
        nav_state = NavState::TURN_RIGHT;
        change = true;

      } else if (rolling_mean(wall_sigs) < kWallDropoffThreshold) {
        // If there was a GRADUAL decrease in wall signal windows, you should
        // align
        nav_manager.sendDriveCommand(
            kAlignSpeed, Create::DriveCommand::DRIVE_INPLACE_CLOCKWISE);
        nav_state = NavState::ALIGN_RIGHT;
        change = true;

      } else if (rolling_mean(wall_sigs) > kWallRightThreshold) {
        // If there was a GRADUAL decrease in wall signal windows, you should
        // align
        nav_manager.sendDriveCommand(
            kAlignSpeed, Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);
        nav_state = NavState::ALIGN_LEFT;
        change = true;

      } else if (change) {
        // Otherwise, continue to drive forward
        nav_manager.sendDriveCommand(kSlowSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        change = false;
      }

      cycles_since_last_forcesleep++;
      break;

    case NavState::ALIGN_LEFT:
      // ALIGN_LEFT NavState: Gradually orient to the left, until parallel again

      if (DBG) {
        cout << "ALIGN_LEFT" << endl;
      }

      if (bump_left || bump_right) {
        // If the bump sensor is engaged, back up until it isn't (will we ever
        // encounter this?)
        nav_manager.sendDriveCommand(kBackupSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        std::this_thread::sleep_for(
            kNavigationSleepMs *
            2); // Give extra backup since this is a serious error ~ into the
                // wall when aligning
        // nav_state = NavState::TURN_LEFT;  // Go into a turn left ~ should
        // never bump unless misclassifying a turn as an align
        change = true;

      } else if (wall_sig > kWallMinThreshold &&
                 rolling_mean(wall_sigs) < rolling_mean(prev_wall_sigs)) {
        // Stop turning if the wall signal just hit a local maximum ~ peak, and
        // now falling; unlike turn, doesn't check for a corner case
        nav_manager.sendDriveCommand(kSlowSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        nav_state = NavState::STRAIGHT;
        change = true;

      } else if (change) {
        nav_manager.sendDriveCommand(
            kAlignSpeed, Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);
        change = false;
      }

      break;

    case NavState::ALIGN_RIGHT:
      // ALIGN_RIGHT NavState: Gradually orient to the right, until parallel
      // again

      if (DBG) {
        cout << "ALIGN_RIGHT" << endl;
      }

      if (bump_left || bump_right) {
        // If the bump sensor is engaged, back up and align to the left
        nav_manager.sendDriveCommand(kBackupSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        nav_state = NavState::ALIGN_LEFT;
        change = true;

      } else if (wall_sig < prev_wall_sig) {
        // Stop turning if the wall signal just hit a local maximum ~ peak, and
        // now falling; unlike turn, doesn't check for a corner case
        nav_manager.sendDriveCommand(kSlowSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        nav_state = NavState::STRAIGHT;
        change = true;

      } else if (change) {
        nav_manager.sendDriveCommand(
            kAlignSpeed, Create::DriveCommand::DRIVE_INPLACE_CLOCKWISE);
        change = false;
      }

      break;

    case NavState::TURN_LEFT:
      if (DBG) {
        cout << "TURN_LEFT" << endl;
      }

      nav_manager.sendDriveCommand(
          kLeftTurnSpeed, Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);
      std::this_thread::sleep_for(kLeftTurnSleepMs);
      nav_state = NavState::STRAIGHT;
      change = true;

      break;

    case NavState::TURN_RIGHT:

      if (DBG) {
        cout << "TURN_RIGHT" << endl;
      }

      if (cycles_since_last_right <= kDoneTurnWindow) {
        nav_manager.sendDriveCommand(0, Create::DriveCommand::DRIVE_STRAIGHT);
        nav_state = NavState::DONE;
        break;
      }

      if (bump_left || bump_right) {
        // If the bump sensor is engaged, back up until it isn't, then align
        // towards the left ~ from wall
        nav_manager.sendDriveCommand(kBackupSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);

        // In case of bump on edge mid-turn, back up and give yourself some more
        // room
        std::this_thread::sleep_for(kNavigationSleepMs);
        nav_manager.sendDriveCommand(
            kTurnRightSpeed,
            Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);
        std::this_thread::sleep_for(kNavigationSleepMs);

      } else if (wall_sig > kWallMinThreshold &&
                 rolling_mean(wall_sigs) < rolling_mean(prev_wall_sigs) &&
                 rolling_mean(prev_wall_sigs) >
                     rolling_mean(prev_prev_wall_sigs)) {
        // If we end up parallel w/the wall, drive straight
        nav_manager.sendDriveCommand(kSlowSpeed,
                                     Create::DriveCommand::DRIVE_STRAIGHT);
        cycles_since_last_right = 0;
        nav_state = NavState::STRAIGHT;
        change = true;

      } else if (rolling_mean(wall_sigs) > kWallDropoffThreshold) {
        cycles_since_last_right = 0;
        nav_manager.sendDriveCommand(
            kAlignSpeed,
            Create::DriveCommand::
                DRIVE_INPLACE_CLOCKWISE); // Track on this specific align, since
                                          // the turn is 'over'
        nav_state = NavState::ALIGN_RIGHT;
        change = true;

      } else if (change) {
        nav_manager.sendDriveCommand(kFastSpeed, kRightTurnRadius);
        change = false;
        // cout << "FULL RIGHT" << endl;
      }

      break;

    case NavState::DONE:
      // Draw contour, run image detection analysis

      cout << "<<<< DONE >>>>" << endl;
      ss_->done = true;
      break;

    default:
      // cout << "Error: NavState not detected - ABORT." << endl;
      return;
    }

    // Update (singular) most immediate wall signal
    prev_wall_sig = wall_sig;
    cycles_since_last_right++;

    std::this_thread::sleep_for(kNavigationSleepMs);
  }
}

std::tuple<short, bool, bool> Navigator::read_sensors_() {
  ss_->robot_mutex->lock();
  const std::tuple<short, bool, bool> readings(
      robot_->wallSignal(), robot_->bumpLeft(), robot_->bumpRight());
  ss_->robot_mutex->unlock();
  return readings;
}

#include "handle_robot_safety.hh"
#include "../navigation-manager/navigation_manager.hh"

#include <iostream>
#include <mutex>
#include <thread>
#include <vector>

namespace {

using rw = boost::accumulators::tag::rolling_window;
using boost::accumulators::rolling_mean;
using iRobot::Create;
using note_t = Create::note_t;

const Create::song_t song = {Create::note_t(50, 7)};
const long kTwoMinutesMs = 120000L;
const std::vector<note_t> kAnthem = {
    note_t{67, 30}, note_t{72, 30}, note_t{67, 30}, note_t{69, 30},
    note_t{71, 30}, note_t{64, 30}, note_t{64, 30}, note_t{69, 30},
    note_t{67, 30}, note_t{65, 30}, note_t{67, 30}, note_t{60, 30},
    note_t{60, 30}, note_t{62, 30}, note_t{62, 30}, note_t{64, 30},
    note_t{65, 30}, note_t{65, 30}, note_t{67, 30}, note_t{69, 30},
    note_t{71, 30}, note_t{72, 30}, note_t{74, 30}, note_t{76, 30},
    note_t{74, 30}, note_t{72, 30}, note_t{74, 30}, note_t{71, 30},
    note_t{67, 30}, note_t{72, 30}, note_t{71, 30}, note_t{69, 30},
    note_t{71, 30}, note_t{64, 30}, note_t{64, 30}, note_t{69, 30},
    note_t{67, 30}, note_t{65, 30}, note_t{67, 30}, note_t{60, 30},
    note_t{60, 30}, note_t{72, 30}, note_t{71, 30}, note_t{69, 30},
    note_t{67, 30}, note_t{71, 30}, note_t{72, 30}, note_t{74, 30},
    note_t{76, 30}};

long curr_time_ms() {
  return std::chrono::duration_cast<std::chrono::milliseconds>(
             std::chrono::system_clock::now().time_since_epoch())
      .count();
}

} // end namespace

HandleRobotSafety::HandleRobotSafety(Create *robot, SharedState *ss,
                                     const short cliff_tol,
                                     const std::chrono::milliseconds sleep_ms)
    : robot_(robot), ss_(ss), cliff_tol_(cliff_tol), sleep_ms_(sleep_ms),
      init_time_(std::chrono::duration_cast<std::chrono::milliseconds>(
                     std::chrono::system_clock::now().time_since_epoch())
                     .count()),
      left_wheel_ocs_(rw::window_size = oc_window_size_),
      right_wheel_ocs_(rw::window_size = oc_window_size_),
      left_cliff_sigs_(rw::window_size = cliff_window_size_),
      right_cliff_sigs_(rw::window_size = cliff_window_size_),
      front_left_cliff_sigs_(rw::window_size = cliff_window_size_),
      front_right_cliff_sigs_(rw::window_size = cliff_window_size_) {
  // init moving averages with valid values to prevent immediate safety
  // engagement
  clear_overcurrent_readings_();
  for (int i = 0; i < cliff_window_size_; ++i) {
    left_cliff_sigs_(1.5 * cliff_tol_);
    right_cliff_sigs_(1.5 * cliff_tol_);
    front_left_cliff_sigs_(1.5 * cliff_tol_);
    front_right_cliff_sigs_(1.5 * cliff_tol_);
  }
  if (debug_) {
    std::cout << "Constructed HandleRobotSafety Thread" << std::endl;
    std::cout << "Accumulators"
              << "\nleft_wheel_ocs_: " << rolling_mean(left_wheel_ocs_)
              << "\nright_wheel_ocs_: " << rolling_mean(right_wheel_ocs_)
              << "\nleft_cliff_sigs_: " << rolling_mean(left_cliff_sigs_)
              << "\nright_cliff_sigs_: " << rolling_mean(right_cliff_sigs_)
              << "\nfront_left_cliff_sigs_: "
              << rolling_mean(front_left_cliff_sigs_)
              << "\nfront_right_cliff_sigs_: "
              << rolling_mean(front_right_cliff_sigs_) << std::endl;
  }
}

void HandleRobotSafety::handle_robot_safety_() {
  bool wheel_dropping = false;
  bool overcurrent = false;
  bool cliff = false;
  int song_i = 0;
  NavigationManager &nav_manager = *(ss_->nav_manager);
  while (!ss_->done) {
    if (is_traversal_complete_()) {
      ss_->done = true;
  		nav_manager.sendDriveCommand(0, Create::DRIVE_STRAIGHT);
      break;
    }

    wheel_dropping = is_wheel_dropping_();
    overcurrent = is_wheel_overcurrent_();
    cliff = is_falling_off_cliff_();

    if (wheel_dropping || overcurrent || cliff) {
      std::cout << "Stopping robot" << std::endl;
      nav_manager.sendDriveCommand(0, Create::DRIVE_STRAIGHT);
      // Play song and wait for advance
      // LOCK ROBOT MUTEX
      bool wait_cond = true;
      ss_->robot_mutex->lock();
      while (wait_cond) {
        robot_->sendSongCommand(7, Create::song_t{kAnthem[song_i]});
        robot_->sendPlaySongCommand(7);
        song_i = (song_i + 1) % kAnthem.size();

        if (wheel_dropping) {
          wait_cond = is_wheel_dropping_(true);
        } else if (cliff) {
          wait_cond = is_falling_off_cliff_(true);
        } else { // overcurrent
          wait_cond = !robot_->advanceButton();
        }
        std::this_thread::sleep_for(song_sleep_ms_);
      }
      ss_->robot_mutex->unlock();
    }
		if (overcurrent) {
  		clear_overcurrent_readings_();
    }

    std::this_thread::sleep_for(sleep_ms_);
  }
}

bool HandleRobotSafety::is_wheel_dropping_(bool robot_locked) {
  if (!robot_locked) {
    ss_->robot_mutex->lock();
  }
  const bool wheel_drop = robot_->wheeldropLeft() || robot_->wheeldropRight();
  if (!robot_locked) {
    ss_->robot_mutex->unlock();
  }
  return wheel_drop;
}

bool HandleRobotSafety::is_wheel_overcurrent_(bool robot_locked) {
  if (!robot_locked) {
    ss_->robot_mutex->lock();
  }
  const int lwo = robot_->leftWheelOvercurrent();
  const int rwo = robot_->rightWheelOvercurrent();
  if (!robot_locked) {
    ss_->robot_mutex->unlock();
  }
  left_wheel_ocs_(lwo);
  right_wheel_ocs_(rwo);
  return rolling_mean(left_wheel_ocs_) >= 0.5 ||
         rolling_mean(right_wheel_ocs_) >= 0.5;
}

bool HandleRobotSafety::is_falling_off_cliff_(bool robot_locked) {
  if (!robot_locked) {
    ss_->robot_mutex->lock();
  }
  const short left_sig = robot_->cliffLeftSignal();
  const short right_sig = robot_->cliffRightSignal();
  const short front_left_sig = robot_->cliffFrontLeftSignal();
  const short front_right_sig = robot_->cliffFrontRightSignal();
  if (!robot_locked) {
    ss_->robot_mutex->unlock();
  }

  left_cliff_sigs_(left_sig);
  right_cliff_sigs_(right_sig);
  front_left_cliff_sigs_(front_left_sig);
  front_right_cliff_sigs_(front_right_sig);
  if (debug_) {
    std::cout << "left_sig: " << left_sig << "\nright_sig: " << right_sig
              << "\nfront_left_sig: " << front_left_sig
              << "\nfront_right_sig: " << front_right_sig
              << "\nleft_wheel_ocs_: " << rolling_mean(left_wheel_ocs_)
              << "\nright_wheel_ocs_: " << rolling_mean(right_wheel_ocs_)
              << "\nleft_cliff_sigs_: " << rolling_mean(left_cliff_sigs_)
              << "\nright_cliff_sigs_: " << rolling_mean(right_cliff_sigs_)
              << std::endl;
  }
  return rolling_mean(left_cliff_sigs_) <= cliff_tol_ ||
         rolling_mean(right_cliff_sigs_) <= cliff_tol_ ||
         rolling_mean(front_left_cliff_sigs_) <= cliff_tol_ ||
         rolling_mean(front_right_cliff_sigs_) <= cliff_tol_;
}

bool HandleRobotSafety::is_in_danger_() {
  // analog cliff sensors, wheel drop, overccurent
  const bool wheel_dropping = is_wheel_dropping_();
  const bool wheel_overcurrent = is_wheel_overcurrent_();
  const bool falling_off_cliff = is_falling_off_cliff_();
  if (wheel_dropping || wheel_overcurrent || falling_off_cliff) {
    std::cout << "wheel_dropping: " << wheel_dropping
              << "\nwheel_overcurrent: " << wheel_overcurrent
              << "\nfalling_off_cliff: " << falling_off_cliff << std::endl;
    return true;
  } else {
    return false;
  }
}

bool HandleRobotSafety::is_traversal_complete_() {
  ss_->robot_mutex->lock();
  const bool pressed_play = robot_->playButton();
  ss_->robot_mutex->unlock();
  return pressed_play;
}

bool HandleRobotSafety::should_safety_abort_(long period) {
  const long curr_time = curr_time_ms();
  return (curr_time - init_time_) >= (kTwoMinutesMs - period);
}

void HandleRobotSafety::safety_abort_() {
  ss_->done = true;
  // Flash red LEDS and stop robot
  ss_->nav_manager->sendDriveCommand(0, Create::DRIVE_STRAIGHT);
  ss_->robot_mutex->lock();
  robot_->sendLedCommand(Create::LED_ALL, Create::LED_COLOR_RED,
                         Create::LED_INTENSITY_FULL);
  ss_->robot_mutex->unlock();
}

void HandleRobotSafety::clear_overcurrent_readings_() {
  for (int i = 0; i < oc_window_size_; ++i) {
    left_wheel_ocs_(0);
    right_wheel_ocs_(0);
  }
}

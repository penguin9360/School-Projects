#include "control_loop_navigator.hh"
#include "../irobot/irobot-create.hh"
#include "../navigation-manager/navigation_manager.hh"

#include <algorithm>
#include <chrono>
#include <iostream>
#include <thread>

namespace {
using iRobot::Create;
using rw = boost::accumulators::tag::rolling_window;
using boost::accumulators::rolling_mean;
constexpr bool kDebug = true;
constexpr float kDampen = 1.0;
} // namespace

ControlLoopNavigator::ControlLoopNavigator(iRobot::Create *robot,
                                           SharedState *ss,
                                           short target_wall_sig, long sleep_ms,
                                           float turn_ratio, float kp,
                                           short straight_speed)
    : robot_(robot), ss_(ss), t_(sleep_ms), turn_ms_(sleep_ms * turn_ratio),
      straight_speed_(straight_speed), controller_(target_wall_sig, kp),
      wall_sig_rolling_avg_(rw::window_size = window_size_) {}

void ControlLoopNavigator::drive_() {
  NavigationManager &nav_manager = *(ss_->nav_manager);
  std::cout << "Period: " << t_ << "\n"
            << "Turn Time: " << turn_ms_ << "\n";
  const auto sleep_ms = std::chrono::milliseconds(t_ - turn_ms_);
  const auto turn_ms = std::chrono::milliseconds(turn_ms_);
  float turn_angle;
  short dvl, dvr, wall_sig, avg_wall_sig;

  while (!ss_->done) {
    wall_sig = read_wall_sig_();
    wall_sig_rolling_avg_(wall_sig); // accumulate
    avg_wall_sig = rolling_mean(wall_sig_rolling_avg_);
    turn_angle = controller_.calulateTurnAngle(avg_wall_sig);
    if (kDebug) {
      std::cout << "wall_sig: " << wall_sig << "\n";
      std::tie(dvl, dvr) = translateAngleContinuous_(turn_angle);
      std::cout << "avg_wall_sig: " << avg_wall_sig << "\n";
      std::cout << "Adjusting by " << turn_angle << " radians\n";
      std::cout << "dvl: " << dvl << "\ndvr: " << dvr << "\n\n";
    }

    nav_manager.sendDirectDriveCommand(straight_speed_ + dvl,
                                       straight_speed_ + dvr);
    std::this_thread::sleep_for(turn_ms);

    nav_manager.sendDirectDriveCommand(straight_speed_, straight_speed_);
    std::this_thread::sleep_for(sleep_ms);
  }
 	nav_manager.sendDriveCommand(0, Create::DRIVE_STRAIGHT);
}

std::tuple<short, short>
ControlLoopNavigator::translateAngleContinuous_(float angle) {
  // Calculate needed velocity difference between wheels
  const bool turn_right = angle > 0.0;
  angle = angle < 0 ? -1 * angle : angle; // make positive
  const short target_vel_diff =
      kDampen * ((kRobotWheelDist * angle) / (turn_ms_ / 1000.0));

  // Determine how to split the velocity difference
  const short target_vel_split = target_vel_diff / 2;
  const short fast_vel = std::min((short)(straight_speed_ + target_vel_split),
                                  (short)iRobot::Create::VELOCITY_MAX);
  const short added_vel = fast_vel - straight_speed_;
  const short diff_left_over = target_vel_diff - added_vel;
  const short low_vel = std::max((short)(straight_speed_ - diff_left_over),
                                 (short)iRobot::Create::VELOCITY_MIN);
  const short subtracted_vel = straight_speed_ - low_vel;
  if (added_vel + subtracted_vel != target_vel_diff) {
    std::cout << "[ERROR] Could not account for a turn of " << angle
              << " radians which needed a velocity diff of " << target_vel_diff
              << "...Truncating\n";
  }

  short dvl, dvr;
  if (turn_right) {
    dvl = added_vel;
    dvr = -1 * subtracted_vel;
  } else {
    dvl = -1 * subtracted_vel;
    dvr = added_vel;
  }

  return std::tuple<short, short>(dvl, dvr);
}

short ControlLoopNavigator::read_wall_sig_() {
  ss_->robot_mutex->lock();
  const short wall_sig = robot_->wallSignal();
  ss_->robot_mutex->unlock();
  return wall_sig;
}

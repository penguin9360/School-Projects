#pragma once

#include "../irobot/irobot-create.hh"
#include "../utility/shared_state.hh"
#include "controller.hh"

#include <boost/accumulators/accumulators.hpp>
#include <boost/accumulators/statistics.hpp>
#include <boost/accumulators/statistics/rolling_mean.hpp>
#include <tuple>

class ControlLoopNavigator {
public:
  /*
   * target_wall_sig: the wall signal that the robot will try to maintain
   * sleep_ms: the sleep time of one iteration of the control loop
   * turn_ratio: the (0-1] ratio of time spent in the control loop turning
   * kp: the magical proportional constant
   * straight_speed: the velocity of the robot when going straight
   */
  ControlLoopNavigator(iRobot::Create *robot, SharedState *ss,
                       short target_wall_sig, long sleep_ms, float turn_ratio,
                       float kp, short straight_speed);
  void operator()() { drive_(); }

private:
  iRobot::Create *robot_;
  SharedState *ss_;
  long t_;
  long turn_ms_;
  short straight_speed_;
  Controller controller_;

  // Rolling average accumulator
  const int window_size_ = 3;
  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      wall_sig_rolling_avg_;

  // Robot dimensions
  const float kRobotWheelDist = 240.0; // mm
  const float kWheelradius = 25.4;     // mm (1 inch)

  void drive_();
  short read_wall_sig_();
  std::tuple<short, short> translateAngleContinuous_(float angle);
};

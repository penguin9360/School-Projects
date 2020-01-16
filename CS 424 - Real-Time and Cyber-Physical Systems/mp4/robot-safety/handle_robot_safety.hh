#pragma once

#include "../irobot/irobot-create.hh"
#include "../utility/shared_state.hh"

#include <boost/accumulators/accumulators.hpp>
#include <boost/accumulators/statistics.hpp>
#include <boost/accumulators/statistics/rolling_mean.hpp>

#include <chrono>

class HandleRobotSafety {
public:
  HandleRobotSafety(iRobot::Create *robot, SharedState *ss,
                    const short cliff_tol,
                    const std::chrono::milliseconds sleep_ms);

  void operator()() { handle_robot_safety_(); }

private:
  iRobot::Create *robot_;
  SharedState *ss_;
  const short cliff_tol_;
  const std::chrono::milliseconds sleep_ms_;       // Ms per period.
  const std::chrono::milliseconds song_sleep_ms_ = // Ms between notes
      std::chrono::milliseconds(500);
  const long init_time_;
  const int oc_window_size_ = 150;
  const int cliff_window_size_ = 20;
  const bool debug_ = false;

  // Accumulators
  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      left_wheel_ocs_;

  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      right_wheel_ocs_;

  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      left_cliff_sigs_;

  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      right_cliff_sigs_;

  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      front_left_cliff_sigs_;

  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      front_right_cliff_sigs_;

  void handle_robot_safety_();
  bool is_wheel_dropping_(bool robot_locked = false);
  bool is_wheel_overcurrent_(bool robot_locked = false);
  bool is_falling_off_cliff_(bool robot_locked = false);
  bool is_in_danger_();
  bool is_traversal_complete_();

  bool should_safety_abort_(long period);
  void safety_abort_();
  void clear_overcurrent_readings_();
};

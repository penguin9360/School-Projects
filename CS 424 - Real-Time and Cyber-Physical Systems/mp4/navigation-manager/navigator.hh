#include "../irobot/irobot-create.hh"
#include "../utility/shared_state.hh"

#include <cstdlib>
#include <queue>
#include <tuple>

#include <boost/accumulators/accumulators.hpp>
#include <boost/accumulators/statistics.hpp>
#include <boost/accumulators/statistics/rolling_mean.hpp>

class Navigator {
public:
  Navigator(iRobot::Create *robot, SharedState *ss,
            const std::chrono::milliseconds sleep_ms);
  void operator()() { drive(); }

private:
  enum NavState {
    BOOT,
    STRAIGHT,
    ALIGN_LEFT,
    ALIGN_RIGHT,
    TURN_LEFT,
    TURN_RIGHT,
    DONE
  };

  iRobot::Create *robot_;
  SharedState *ss_;
  std::chrono::milliseconds sleep_ms_;
  NavState nav_state;
  const int ws_ = 4;

  // Employ rolling means of wall signals; init to zero
  short prev_wall_sig = 0; // Most immediately previous value
  std::queue<int> wall_sig_q;
  std::queue<int> prev_wall_sig_q;

  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      wall_sigs;
  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      prev_wall_sigs;
  boost::accumulators::accumulator_set<
      int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
      prev_prev_wall_sigs;

  void drive();
  std::tuple<short, bool, bool> read_sensors_();
  bool in_range_();
};

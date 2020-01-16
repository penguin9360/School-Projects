#include "../irobot/irobot-create.hh"
#include "wrap.h"

#include <mutex>

struct Lock {
  std::mutex *lck;
  Lock(std::mutex *x) : lck(x) {}
  void operator()() const { lck->lock(); }
};

struct Unlock {
  std::mutex *lck;
  Unlock(std::mutex *x) : lck(x) {}
  void operator()() const { lck->unlock(); }
};

class ThreadSafeRobot : public Wrap<iRobot::Create, Lock, Unlock> {
public:
  ThreadSafeRobot(iRobot::Create *robot, std::mutex *robot_lock)
      : Wrap<iRobot::Create, Lock, Unlock>(robot, Lock(robot_lock),
                                           Unlock(robot_lock)) {}
};

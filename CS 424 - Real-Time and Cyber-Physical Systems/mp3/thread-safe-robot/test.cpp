#include "./thread_safe_robot.h"
#include <mutex>

int main(int argc, char** argv) {
  (void) argc;
  (void) argv;
  std::mutex* m = new std::mutex();
  iRobot::Create* robot = new iRobot::Create(new LibSerial::SerialStream());
  ThreadSafeRobot rob(robot, m);
}

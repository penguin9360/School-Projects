#include <SerialStream.h>
#include <algorithm>
#include <chrono>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <opencv2/imgproc/imgproc.hpp>
#include <pthread.h>
#include <thread>

#include "./control-loop/control_loop_navigator.hh"
#include "./irobot/irobot-create.hh"
#include "./navigation-manager/navigation_manager.hh"
#include "./robot-safety/handle_robot_safety.hh"
#include "./utility/shared_state.hh"

namespace {

using iRobot::CommandNotAvailable;
using iRobot::Create;
using iRobot::InvalidArgument;
using LibSerial::SerialStream;

using std::cerr;
using std::cout;
using std::endl;
using std::ios_base;
using std::thread;
using std::vector;

constexpr char kSerialLoc[] = "/dev/ttyUSB0";

// Robot Safety Constants
const short kSafetyCliffTol = 30;
const std::chrono::milliseconds kSafetySleepMs =
    std::chrono::milliseconds(15); // Originally 15A

// TODO: Navigation Constants
const short kTargetWallSig = 15;
const long kControllerPeriodMs = 25;
const float kControllerTurnRatio = .75;
const float kKp = 0.0059;
const short kNavSpeed = 120;

// ADD SENSORS HERE
const Create::sensorPackets_t kSensors = {
    Create::SENSOR_BUMPS_WHEELS_DROPS,
    Create::SENSOR_WALL_SIGNAL,
    Create::SENSOR_BUTTONS,
    Create::SENSOR_OVERCURRENTS,
    Create::SENSOR_CLIFF_FRONT_LEFT_SIGNAL,
    Create::SENSOR_CLIFF_FRONT_RIGHT_SIGNAL,
    Create::SENSOR_CLIFF_LEFT_SIGNAL,
    Create::SENSOR_CLIFF_RIGHT_SIGNAL};

Create *create_robot(const Create::sensorPackets_t &sensors,
                     SerialStream &stream) {
  cout << "Opened Serial Stream to" << kSerialLoc << endl;
  std::this_thread::sleep_for(std::chrono::milliseconds(1000));
  Create *robot = new Create(stream);
  cout << "Created iRobot Object" << endl;
  robot->sendFullCommand();
  cout << "Setting iRobot to Full Mode" << endl;
  std::this_thread::sleep_for(std::chrono::milliseconds(1000));
  cout << "Robot is ready" << endl;

  // Stream some sensors.
  robot->sendStreamCommand(sensors);
  cout << "Sent Stream Command" << endl;
  return robot;
}

SharedState *init_shared_state(Create *robot) {
  // TODO: add more fields to SharedState, and intialize here
  std::mutex *robot_mutex = new std::mutex();
  return new SharedState{true, new NavigationManager(robot, robot_mutex),
                         robot_mutex, false};
}

void assignThreadPriorites(vector<thread> *ordered_threads) {
  for (size_t i = 0; i < ordered_threads->size(); ++i) {
    std::cout << "Assigned thread " << i << '\n';
    sched_param sch;
    int policy;
    pthread_getschedparam((*ordered_threads)[i].native_handle(), &policy, &sch);
    sch.sched_priority = (i * 2) + 1; // Sets priority to 1, 3, 5....; detect ==
                                      // 1, navigate == 3, safety == 5
    if (pthread_setschedparam((*ordered_threads)[i].native_handle(), SCHED_FIFO,
                              &sch)) {
      std::cout << "Failed to setschedparam: " << std::strerror(errno) << endl;
    }
  }
  std::cout << "Finished assigning thread priorities" << endl;
}

} // namespace

int main(int argc, char **argv) {

  try {
    // Initialize
    SerialStream *stream =
        new SerialStream(kSerialLoc, LibSerial::SerialStreamBuf::BAUD_57600);
    Create *robotPtr = create_robot(kSensors, *stream);

    // Initialise shared state and objects
    SharedState *ss = init_shared_state(robotPtr);
    HandleRobotSafety safety(robotPtr, ss, kSafetyCliffTol, kSafetySleepMs);
    ControlLoopNavigator navigation(robotPtr, ss, kTargetWallSig,
                                    kControllerPeriodMs, kControllerTurnRatio,
                                    kKp, kNavSpeed);

    // Start timer
    const auto start = std::chrono::system_clock::now();
    robotPtr->sendDriveCommand(120, Create::DRIVE_STRAIGHT);
    while (!robotPtr->bumpLeft() || !robotPtr->bumpRight()) {
      std::this_thread::sleep_for(std::chrono::milliseconds(50));
    }
    robotPtr->sendDriveCommand(-50, Create::DRIVE_STRAIGHT);
    std::this_thread::sleep_for(std::chrono::milliseconds(50));
    robotPtr->sendDriveCommand(100,
                               Create::DRIVE_INPLACE_COUNTERCLOCKWISE);
    std::this_thread::sleep_for(std::chrono::milliseconds(1900));

    // Start Threads; priority rangee [low, hi] == [1, 99]; use {1, 2, 3} ==
    // {MIN, MID, MAX} priority
    vector<thread> ordered_threads;
    ordered_threads.push_back(thread(std::ref(navigation))); // priority == ?
    ordered_threads.push_back(thread(std::ref(safety))); // priority == 5, MAX
    assignThreadPriorites(&ordered_threads);

    std::cout << "Finished starting threads!" << std::endl;
    std::for_each(ordered_threads.begin(), ordered_threads.end(),
                  [](thread &t) { t.join(); });

    const auto end = std::chrono::system_clock::now();

    const std::chrono::duration<double> diff = end - start;
    cout << "Maze time: " << diff.count() << endl;

    std::cout << "Bye felicia" << std::endl;

    delete stream;
    delete ss->robot_mutex;
    delete ss->nav_manager;
    delete ss;
    delete robotPtr;

  } catch (InvalidArgument &e) {
    cerr << e.what() << endl;
    return 3;
  } catch (CommandNotAvailable &e) {
    cerr << e.what() << endl;
    return 4;
  } catch (const ios_base::failure &e) {
    cerr << e.code().message() << endl;
    return 5;
  }
}

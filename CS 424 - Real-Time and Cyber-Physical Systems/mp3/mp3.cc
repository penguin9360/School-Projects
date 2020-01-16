#include <SerialStream.h>
#include <algorithm>
#include <chrono>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <opencv2/imgproc/imgproc.hpp>
#include <pthread.h>
#include <raspicam/raspicam_cv.h>
#include <thread>

#include "./contour-mapper/contour_mapper.hh"
#include "./irobot/irobot-create.hh"
#include "./navigation-manager/navigation_manager.hh"
#include "./navigation-manager/navigator.hh"
#include "./object-identification/object_identification.hh"
#include "./robot-safety/handle_robot_safety.hh"
#include "./utility/shared_state.hh"

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

using namespace std::chrono; // Andrew will hate this visually, but it needs to compile :P

namespace {

constexpr char kSerialLoc[] = "/dev/ttyUSB0";

// Robot Safety Information
const short kSafetyCliffTol = 30;
const std::chrono::milliseconds kSafetySleepMs = std::chrono::milliseconds(15); // Originally 15A
const std::chrono::milliseconds kNavigationSleepMs =
    std::chrono::milliseconds(60);
const std::chrono::milliseconds kDetectionSleepMs =
    std::chrono::milliseconds(3600);

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

  if (argc < 2) {
    cout << "Usage: ./mp2 <defuse>\n";
    return 0;
  }

  const bool defuse = bool(argv[1]);

  try {
    // Initialize
    raspicam::RaspiCam_Cv Camera;
    if (!Camera.open()) {
      cerr << "Error opening the camera" << endl;
      return -1;
    }
    cout << "Opened Camera" << endl;

    SerialStream *stream =
        new SerialStream(kSerialLoc, LibSerial::SerialStreamBuf::BAUD_57600);
    Create *robotPtr = create_robot(kSensors, *stream);

    // Initialise shared state and objects
    SharedState *ss = init_shared_state(robotPtr);
    HandleRobotSafety safety(robotPtr, ss, kSafetyCliffTol, kSafetySleepMs);
    Navigator navigate(robotPtr, ss, kNavigationSleepMs);
    HandleObjectDetection detect(robotPtr, ss, &Camera, kDetectionSleepMs,
                                 defuse);

    // Initialize Contour mapper
    NavigationManager &nav_manager = *(ss->nav_manager);
    ContourMapper mapper = ContourMapper(nav_manager.get_directives());

    // Start timer
    const auto start = system_clock::now();

    // Start Threads; priority rangee [low, hi] == [1, 99]; use {1, 2, 3} ==
    // {MIN, MID, MAX} priority

    vector<thread> ordered_threads;
    ordered_threads.push_back(thread(std::ref(detect)));   // priority == 1, MIN
    ordered_threads.push_back(thread(std::ref(navigate))); // priority == 3, MED
    ordered_threads.push_back(thread(std::ref(safety)));   // priority == 5, MAX
    assignThreadPriorites(&ordered_threads);

    std::cout << "Finished starting threads!" << std::endl;
    std::for_each(ordered_threads.begin(), ordered_threads.end(),
                  [](thread &t) { t.join(); });

    const auto end = system_clock::now();

    const duration<double> diff = end - start;
    cout << "Maze time: " << diff.count() << endl;

    std::cout << "Bye felicia" << std::endl;
    mapper.draw_contour();
    detect.process_buffered_frames();

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

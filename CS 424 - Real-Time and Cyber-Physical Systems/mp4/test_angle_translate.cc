#include <SerialStream.h>
#include <chrono>
#include <cstdlib>
#include <iostream>
#include <thread>
#include <utility>

#include "./irobot/irobot-create.hh"
#include "./utility/shared_state.hh"
#include "./navigation-manager/navigation_manager.hh"

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
const float kRobotWheelDist = 240.0; // mm
const float kWheelRadius = 25.4;     // mm (1 inch)
const short kConstVel = 120;

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
std::tuple<short, Create::DriveCommand, std::chrono::milliseconds>
translateAngleStop(float angle) {
  // TODO: translate angle to a drive command and a sleep.
  return std::tuple<short, Create::DriveCommand, std::chrono::milliseconds>(
      0, Create::DRIVE_INPLACE_CLOCKWISE, 0);
}

std::tuple<short, short> translateAngleContinuous(float angle, long t) {
  const bool turn_right = angle > 0.0;
  angle = angle < 0 ? -1 * angle : angle; // make positive
  const float vel_diff = (kRobotWheelDist * angle) / (t / 1000);
  std::cout << "vel_diff: " << vel_diff << "\n";
  const short vel_diff_trunc = (short)vel_diff;
  const short vel_diff_split = vel_diff / 2;
  if (turn_right) {
    return std::tuple<short, short>(vel_diff_split, -1 * vel_diff_split);
  } else {
    return std::tuple<short, short>(-1 * vel_diff_split, vel_diff_split);
  }
}

} // namespace

int main(int argc, char **argv) {

  try {
    SerialStream *stream =
        new SerialStream(kSerialLoc, LibSerial::SerialStreamBuf::BAUD_57600);
    Create *robotPtr = create_robot(kSensors, *stream);

    // Initialise shared state and objects
    SharedState *ss = init_shared_state(robotPtr);
    NavigationManager &nav_manager = *(ss->nav_manager);

    // Move forward.. Maybe do init stuff here?
    nav_manager.sendDirectDriveCommand(kConstVel, kConstVel);
    std::this_thread::sleep_for(std::chrono::milliseconds(3000));
    const float angle_to_turn = -1 * (1.57079632679 / 2); // 45 Degrees
    short dvl, dvr;
    std::tie(dvl, dvr) = translateAngleContinuous(angle_to_turn, 1000);
    std::cout << "dvl: " << dvl << "\ndvr: " << dvr << "\n";
    nav_manager.sendDirectDriveCommand(kConstVel + dvl, kConstVel + dvr);
    std::this_thread::sleep_for(std::chrono::milliseconds(1000));
    nav_manager.sendDirectDriveCommand(kConstVel, kConstVel);
    std::this_thread::sleep_for(std::chrono::milliseconds(1000));
    std::cout << "Done!" << std::endl;

    nav_manager.sendDirectDriveCommand(0, 0);

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

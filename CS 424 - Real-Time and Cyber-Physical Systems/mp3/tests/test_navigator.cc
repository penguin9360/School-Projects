#include <SerialStream.h>
#include <chrono>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <thread>

#include "../irobot/irobot-create.hh"
#include "../navigation-manager/navigator.hh"
#include "../utility/shared_state.hh"

using iRobot::CommandNotAvailable;
using iRobot::Create;
using iRobot::InvalidArgument;
using LibSerial::SerialStream;

using std::cerr;
using std::cout;
using std::endl;
using std::ios_base;
using std::vector;

namespace {
constexpr char kSerialLoc[] = "/dev/ttyUSB0";
// Robot Navigation Information
const short kRobotSlowSpeed = 50;
const short kRobotTurnSpeed = 100;
const short kRobotFastSpeed = 175;
const short kRobotBackupSpeed = -50;

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
  return new SharedState{
      true,
      new NavigationManager(robot, kRobotSlowSpeed, kRobotTurnSpeed,
                            kRobotBackupSpeed, kRobotFastSpeed, robot_mutex),
      robot_mutex};
}
}

int main() {
  try {
    // Initialize stream, robot
    SerialStream *stream =
        new SerialStream(kSerialLoc, LibSerial::SerialStreamBuf::BAUD_57600);
    Create *robot = create_robot(kSensors, *stream);
    SharedState *ss = init_shared_state(robot);

    // TODO Code goes here.
    Navigator navigator(robot, ss);
    navigator.drive();

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

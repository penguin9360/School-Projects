#include <SerialStream.h>
#include <chrono>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <thread>
#include <queue>
#include <pthread.h>

#include <boost/accumulators/accumulators.hpp>
#include <boost/accumulators/statistics.hpp>
#include <boost/accumulators/statistics/rolling_mean.hpp>

#include "./contour-mapper/contour_mapper.hh"
#include "./irobot/irobot-create.hh"
#include "./navigation-manager/navigation_manager.hh"
#include "./object-identification/object_identification.hh"

using boost::accumulators::rolling_mean;
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
	using rw = boost::accumulators::tag::rolling_window;
}

// Robot Setup
constexpr char kSerialLoc[] = "/dev/ttyUSB0";

const Create::sensorPackets_t kSensors = {
    Create::SENSOR_BUMPS_WHEELS_DROPS,
    Create::SENSOR_WALL_SIGNAL,
    Create::SENSOR_BUTTONS,
    Create::SENSOR_OVERCURRENTS,
    Create::SENSOR_CLIFF_FRONT_LEFT_SIGNAL,
    Create::SENSOR_CLIFF_FRONT_RIGHT_SIGNAL,
    Create::SENSOR_CLIFF_LEFT_SIGNAL,
    Create::SENSOR_CLIFF_RIGHT_SIGNAL
};

// FSM State Enumeraton
enum State {
	BOOT,
	STRAIGHT,
	ALIGN_LEFT,
	ALIGN_RIGHT,
	TURN_LEFT,
	TURN_RIGHT,
	DONE
};

// Periods for each thread
const std::chrono::milliseconds kNavigationSleepMs = std::chrono::milliseconds(15);
const std::chrono::milliseconds kLeftTurnSleepMs = std::chrono::milliseconds(1900);
const std::chrono::milliseconds kRightTurnSleepMs = std::chrono::milliseconds(850);

const std::chrono::milliseconds kDetectionSleepMs = std::chrono::milliseconds(2000);

// Robot Navigation Constants
const short kTurnSpeed = 100;
const short kTurnRightSpeed = 300;
const short kFastSpeed = 175;
const short kBackupSpeed = -225;

const short kRightTurnRadius = -150;

const short kLeftTurnThreshold = 0.1;

const short kWallDropoffThreshold = 20;

const short kWallMinThreshold = 50;
const short kWallRightThreshold = 100;
const short kWallSigWindow = 3;

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


int main() {
 	try {
		// Initialize stream, robot
		SerialStream *stream =
      	new SerialStream(kSerialLoc, LibSerial::SerialStreamBuf::BAUD_57600);
  		Create *robot = create_robot(kSensors, *stream);
		
		//Initialize PiCamera
		raspicam::RaspiCam_Cv Camera;
	    if (!Camera.open()) {
	      cerr << "Error opening the camera" << endl;
	      return -1;
	    }
	    cout << "Opened Camera" << endl;

		//Object detection thread
		HandleObjectDetection detect(robot, NULL, &Camera, kDetectionSleepMs);
		thread detect_thread = thread(std::ref(detect));

		robot->sendDriveCommand(kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						

		// Start in a boot state ~ naively going straight until bump is detected
		State curr_state = State::BOOT;

		// Employ rolling means of wall signals; init to zero
		short prev_wall_sig = 0;  // Most immediately previous value
		queue<int> wall_sig_q;
		queue<int> prev_wall_sig_q;

		boost::accumulators::accumulator_set<
		int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
		wall_sigs(rw::window_size = kWallSigWindow);
		boost::accumulators::accumulator_set<
		int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
		prev_wall_sigs(rw::window_size = kWallSigWindow);
		boost::accumulators::accumulator_set<
		int, boost::accumulators::stats<boost::accumulators::tag::rolling_mean>>
		prev_prev_wall_sigs(rw::window_size = kWallSigWindow);

		for (int i = 0; i < kWallSigWindow; i++) {
			wall_sig_q.push(0);
			prev_wall_sig_q.push(0);

			wall_sigs(0);
			prev_wall_sigs(0);
			prev_prev_wall_sigs(0);
		}

		// Set Priorities
	    sched_param sch;
	    int policy;
	    pthread_getschedparam(pthread_self(), &policy, &sch);
	    sch.sched_priority = 2;  // priority == 2, MID
	    if (pthread_setschedparam(pthread_self(), SCHED_FIFO, &sch)) {
	      std::cout << "Failed to setschedparam: " << std::strerror(errno)
	                << endl;
	    }

	    pthread_getschedparam(detect_thread.native_handle(), &policy, &sch);
	    sch.sched_priority = 1;  // Sets priority to 1, 3, 5....; detect == 1, security == 3
	    if (pthread_setschedparam(detect_thread.native_handle(), SCHED_FIFO,
	                              &sch)) {
	      std::cout << "Failed to setschedparam: " << std::strerror(errno) << endl;
	    }

		// 2 independent right turns in a period of X cycles consitutes end of maze
		int cycles_since_last_right = 0;

		// Main while loop.
		while (true) {

			// TODO Whenever there is a STATE CHANGE, update the contour.
			// TODO Unless there is a ALIGN or DONE state transition, since they constitute no 'real' change in contour.
			// TODO Comply sensor readings with the navigation manager

	  		short wall_sig = robot->wallSignal();
	  		bool bump_left = robot->bumpLeft();
	  		bool bump_right = robot->bumpRight();

			// Track rolling means of wall signals via queues that hold last X ~ 3 contiguous sliding windows		
			short to_prev_wall_sig = wall_sig_q.front();
			short to_prev_prev_wall_sig = prev_wall_sig_q.front();

			wall_sig_q.pop();
			prev_wall_sig_q.pop();
			wall_sig_q.push(wall_sig);
			prev_wall_sig_q.push(to_prev_wall_sig);

			// Update rolling means ~ [last X][last X, offset of X][last X, offset of 2*X]
			wall_sigs(wall_sig);
			prev_wall_sigs(to_prev_wall_sig);
			prev_prev_wall_sigs(to_prev_prev_wall_sig);

			cout << "\n" << endl;	
			cout << "bump sig: " << (bump_left || bump_right) << endl;
			cout << "wall sig: " << wall_sig << endl;

			cout << rolling_mean(wall_sigs) << " < " << rolling_mean(prev_wall_sigs) << " > " << rolling_mean(prev_prev_wall_sigs) << endl;

			switch (curr_state) {
				case State::BOOT:
					// BOOT State: On startup, naively head straight until bump occurs, then turn left
					if (bump_left || bump_right) {
						// If bump encountered, begin to backup, and turn left
						robot->sendDriveCommand(kBackupSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
						curr_state = State::TURN_LEFT;

					} else {
						// Otherwise, continue to drive forward
						robot->sendDriveCommand(kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
					}

					cout << "BOOT" << endl;
					break;

				case State::STRAIGHT:
					// STRAIGHT State: Default state, moving straight forward until bump sensors, or wall signal falloff
					if (bump_left || bump_right) {
						// If bump encountered, begin to backup, and turn left
						robot->sendDriveCommand(kBackupSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
						curr_state = State::TURN_LEFT;

					} else if ( rolling_mean(wall_sigs) < kWallDropoffThreshold && 
								(rolling_mean(wall_sigs) + kWallDropoffThreshold < rolling_mean(prev_wall_sigs))) {
						// If there was a SUDDEN decrease in wall signal windows, you should right turn
						robot->sendDriveCommand(kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);			
						std::this_thread::sleep_for(kRightTurnSleepMs);
						curr_state = State::TURN_RIGHT;

					} else if (rolling_mean(wall_sigs) < kWallDropoffThreshold) {
						// If there was a GRADUAL decrease in wall signal windows, you should align
						robot->sendDriveCommand(kTurnSpeed, Create::DriveCommand::DRIVE_INPLACE_CLOCKWISE);									
						curr_state = State::ALIGN_RIGHT;

					} else if (rolling_mean(wall_sigs) > kWallRightThreshold) {
						// If there was a GRADUAL decrease in wall signal windows, you should align
						robot->sendDriveCommand(kTurnSpeed, Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);						
						curr_state = State::ALIGN_LEFT;

					} else {
						// Otherwise, continue to drive forward
						robot->sendDriveCommand(kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
					} 

					cout << "STRAIGHT" << endl;
					break;

				case State::ALIGN_LEFT:
					// ALIGN_LEFT State: Gradually orient to the left, until parallel again
					if (bump_left || bump_right) {
						// If the bump sensor is engaged, back up until it isn't
						robot->sendDriveCommand(kBackupSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						

					} else if (wall_sig > kWallMinThreshold && rolling_mean(wall_sigs) < rolling_mean(prev_wall_sigs)) {
						// Stop turning if the wall signal just hit a local maximum ~ peak, and now falling; unlike turn, doesn't check for a corner case
						robot->sendDriveCommand(kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
						curr_state = State::STRAIGHT;
						
					} else {
						robot->sendDriveCommand(kTurnSpeed, Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);						
					}

					cout << "ALIGN_LEFT" << endl;
					break;

				case State::ALIGN_RIGHT:
					// ALIGN_RIGHT State: Gradually orient to the right, until parallel again
					if (bump_left || bump_right) {
						// If the bump sensor is engaged, back up and align to the left
						robot->sendDriveCommand(kBackupSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
						curr_state = State::ALIGN_LEFT;

					} else if (wall_sig < prev_wall_sig) {
						// Stop turning if the wall signal just hit a local maximum ~ peak, and now falling; unlike turn, doesn't check for a corner case
						robot->sendDriveCommand(kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
						curr_state = State::STRAIGHT;
						
					} else {
						robot->sendDriveCommand(kTurnSpeed, Create::DriveCommand::DRIVE_INPLACE_CLOCKWISE);						
					}
					
					cout << "ALIGN_RIGHT" << endl;
					break;

				case State::TURN_LEFT:
					// TURN_LEFT State: An in-place turn that occurs upon bumping into a wall; requires backing up; stop at local maxima
					// if (bump_left || bump_right) {
					// 	// If the bump sensor is engaged, back up until it isn't
					// 	robot->sendDriveCommand(kBackupSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						

					// } else if (rolling_mean(wall_sigs) > kWallMinThreshold && (rolling_mean(wall_sigs) + kLeftTurnThreshold) < rolling_mean(prev_wall_sigs) 
					// 						&& rolling_mean(prev_wall_sigs) > (rolling_mean(prev_prev_wall_sigs) + kLeftTurnThreshold)) {
					// 	// Stop turning if the wall signal just hit a local maximum ~ peak, and now falling
					// 	robot->sendDriveCommand(kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
					// 	curr_state = State::STRAIGHT;

					// } else {	
					// 	robot->sendDriveCommand(kTurnSpeed, Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);						
					// }
					robot->sendDriveCommand(kTurnSpeed, Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);
					std::this_thread::sleep_for(kLeftTurnSleepMs);
					curr_state = State::STRAIGHT;
					cout << "TURN_LEFT" << endl;
					break;

				case State::TURN_RIGHT:

					if (cycles_since_last_right <= 50) {
						robot->sendDriveCommand(0, Create::DriveCommand::DRIVE_STRAIGHT);
						curr_state = State::DONE;
						break;
					}

					if (bump_left || bump_right) {
						// If the bump sensor is engaged, back up until it isn't, then align towards the left ~ from wall
						robot->sendDriveCommand(kBackupSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
						
						// In case of bump on edge mid-turn, back up and give yourself some more room
						std::this_thread::sleep_for(kNavigationSleepMs);
						robot->sendDriveCommand(kTurnRightSpeed, Create::DriveCommand::DRIVE_INPLACE_COUNTERCLOCKWISE);
						std::this_thread::sleep_for(kNavigationSleepMs);

					} else if (wall_sig > kWallMinThreshold && rolling_mean(wall_sigs) < rolling_mean(prev_wall_sigs) 
											&& rolling_mean(prev_wall_sigs) > rolling_mean(prev_prev_wall_sigs)) {
						// If we end up parallel w/the wall, drive straight
						robot->sendDriveCommand(kFastSpeed, Create::DriveCommand::DRIVE_STRAIGHT);						
						cycles_since_last_right = 0;
						curr_state = State::STRAIGHT;

					} else if (rolling_mean(wall_sigs) > kWallDropoffThreshold) {
						cycles_since_last_right = 0;
						curr_state = State::ALIGN_RIGHT;

					} else {
						robot->sendDriveCommand(kFastSpeed, kRightTurnRadius);
						cout << "FULL RIGHT" << endl;
					}

					cout << "TURN_RIGHT" << endl;
					break;

				case State::DONE:
					// TODO Draw contour, run image detection analysis

					cout << "<<<< DONE >>>>" << endl;
					detect.process_buffered_frames();
					break;

				default:
					cout << "Error: State not detected - ABORT." << endl;
					return -1;
			}

			// Update (singular) most immediate wall signal      
			prev_wall_sig = wall_sig;
			cycles_since_last_right++;

			std::this_thread::sleep_for(kNavigationSleepMs);
		}

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

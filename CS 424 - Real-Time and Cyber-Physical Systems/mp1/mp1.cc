#include "irobot-create.hh"
#include <SerialStream.h>
#include <chrono>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <opencv2/imgproc/imgproc.hpp>
#include <raspicam/raspicam_cv.h>
#include <thread>

using namespace iRobot;
using namespace LibSerial;
using namespace std;

typedef struct LedCommand {
  Create::Led led;
  unsigned char color;
  unsigned char intensity;
} LedCommand;

const vector<LedCommand> ledSequence = {
    {Create::LED_PLAY, Create::LED_COLOR_GREEN, Create::LED_INTENSITY_FULL},
    {Create::LED_ALL, Create::LED_COLOR_GREEN, Create::LED_INTENSITY_OFF},
    {Create::LED_ADVANCE, Create::LED_COLOR_RED, Create::LED_INTENSITY_FULL},
    {Create::LED_PLAY, Create::LED_COLOR_RED, Create::LED_INTENSITY_FULL},
    {Create::LED_ALL, Create::LED_COLOR_GREEN, Create::LED_INTENSITY_OFF},
    {Create::LED_ADVANCE, Create::LED_COLOR_GREEN, Create::LED_INTENSITY_FULL}};

bool backingUp = false, blinking = false;

void *backup_drive(Create *robotPtr, raspicam::RaspiCam_Cv *CameraPtr) {

  cout << "Bump detected: Backing Up" << endl;
  robotPtr->sendDriveCommand(-165, Create::DRIVE_STRAIGHT);
  this_thread::sleep_for(chrono::milliseconds(2309));
  robotPtr->sendDriveCommand(0, Create::DRIVE_STRAIGHT);

  // take a picture
  cv::Mat rgb_image, bgr_image;

  CameraPtr->grab();
  CameraPtr->retrieve(bgr_image);
  cv::cvtColor(bgr_image, rgb_image, CV_RGB2BGR);
  cv::imwrite("irobot_image.jpg", rgb_image);
  cout << "Taking photo" << endl;

  // Stop blinking
  blinking = false;

  // Rotate 120 degree to 240 degree at a speed of 107 mms.
  // Assuming radius = 5 inches. time = radius*theta/velocity (theta E [120,
  // 240] or [0.6667*pi, 1.333*pi])
  robotPtr->sendDriveCommand(107, Create::DRIVE_INPLACE_CLOCKWISE);
  float angle = (rand() % 120 + 120.0) * 3.14 / 180.0;
  int t = (int)1190 * angle; // 5 inches * angle * 1000 / 107 mm/s
  this_thread::sleep_for(chrono::milliseconds(t));

  // Start driving again
  robotPtr->sendDriveCommand(0, Create::DRIVE_STRAIGHT);
  cout << "Starting off again" << endl;
  backingUp = false;
}

void *led_blink(Create *robotPtr) {

  int led_i = 0;
  while (blinking) {
    robotPtr->sendLedCommand(ledSequence[led_i].led, ledSequence[led_i].color,
                             ledSequence[led_i].intensity);
    led_i = (led_i + 1) % ledSequence.size();
    this_thread::sleep_for(chrono::milliseconds(200));
  }
}

int main() {
  char serial_loc[] = "/dev/ttyUSB0";

  try {

    // Initialize
    raspicam::RaspiCam_Cv Camera;
    if (!Camera.open()) {
      cerr << "Error opening the camera" << endl;
      return -1;
    }
    cout << "Opened Camera" << endl;
    SerialStream stream(serial_loc, LibSerial::SerialStreamBuf::BAUD_57600);
    cout << "Opened Serial Stream to" << serial_loc << endl;
    this_thread::sleep_for(chrono::milliseconds(1000));
    Create robot(stream);
    cout << "Created iRobot Object" << endl;
    robot.sendFullCommand();
    cout << "Setting iRobot to Full Mode" << endl;
    this_thread::sleep_for(chrono::milliseconds(1000));
    cout << "Robot is ready" << endl;

    // Stream some sensors.
    Create::sensorPackets_t sensors;
    sensors.push_back(Create::SENSOR_BUMPS_WHEELS_DROPS);
    sensors.push_back(Create::SENSOR_WALL_SIGNAL);
    sensors.push_back(Create::SENSOR_BUTTONS);

    robot.sendStreamCommand(sensors);
    cout << "Sent Stream Command" << endl;

    // Let's turn!
    robot.sendDriveCommand(0, Create::DRIVE_STRAIGHT);
    cout << "Starting off" << endl;

    short wallSignal;
    while (!robot.playButton()) {
      // Bump code
      if (!backingUp && (robot.bumpLeft() || robot.bumpRight())) {
        backingUp = true;
        blinking = true;

        // Stop the robot
        robot.sendDriveCommand(0, Create::DRIVE_STRAIGHT);
        // Start blinking the LEDs
        thread(led_blink, &robot).detach();
        // Start backing up
        thread(backup_drive, &robot, &Camera).detach();
      }

      // Wall signal code
      wallSignal = robot.wallSignal();
      if (wallSignal > 0) {
        cout << "Wall signal " << robot.wallSignal() << endl;

        // Play sound
        short frequency = 32, duration = 7;
        if (wallSignal > 3) {
          if (wallSignal > 60) {
            frequency = 90;
          } else {
            frequency = wallSignal + 54;
          }

          // if(wallSignal<8) duration = 24 - 3*wallSignal;

          Create::note_t note(frequency, duration);
          Create::song_t song = {note};
          robot.sendSongCommand(7, song);
          robot.sendPlaySongCommand(7);
        }
      }

      // You can add more commands here.
      this_thread::sleep_for(chrono::milliseconds(200));
    }

    cout << "Play button pressed, stopping Robot" << endl;
    robot.sendDriveCommand(0, Create::DRIVE_STRAIGHT);
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

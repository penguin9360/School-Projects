#include <SerialStream.h>
#include "irobot-create.hh"
#include <ctime>
#include <iostream>
#include <chrono>
#include <thread>
#include <raspicam/raspicam_cv.h>
#include <opencv2/imgproc/imgproc.hpp>

using namespace iRobot;
using namespace LibSerial;
using namespace std;

int main ()
{
  char serial_loc[] = "/dev/ttyUSB0";

  try {
    raspicam::RaspiCam_Cv Camera;
    cv::Mat rgb_image, bgr_image;
    if (!Camera.open()) {
      cerr << "Error opening the camera" << endl;
      return -1;
    }
    cout << "Opened Camera" << endl;
    SerialStream stream (serial_loc, LibSerial::SerialStreamBuf::BAUD_57600);
    cout << "Opened Serial Stream to" << serial_loc << endl;
    this_thread::sleep_for(chrono::milliseconds(1000));
    Create robot(stream);
    cout << "Created iRobot Object" << endl;
    robot.sendFullCommand();
    cout << "Setting iRobot to Full Mode" << endl;
    this_thread::sleep_for(chrono::milliseconds(1000));
    cout << "Robot is ready" << endl;

    // Let's stream some sensors.
    Create::sensorPackets_t sensors;
    sensors.push_back(Create::SENSOR_BUMPS_WHEELS_DROPS);
    sensors.push_back(Create::SENSOR_WALL_SIGNAL);
    sensors.push_back (Create::SENSOR_BUTTONS);

    robot.sendStreamCommand (sensors);
    cout << "Sent Stream Command" << endl;
    // Let's turn!
    int speed = 200;
    int ledColor = Create::LED_COLOR_GREEN;
    robot.sendDriveCommand (speed, Create::DRIVE_STRAIGHT);
    robot.sendLedCommand (Create::LED_PLAY, 0, 0);
    cout << "Sent Drive Command" << endl;

    short wallSignal, prevWallSignal = 0;
    while (!robot.playButton ())
    {
      if (robot.bumpLeft () || robot.bumpRight ()) {
        cout << "Bump !" << endl;
        robot.sendDriveCommand(-speed, Create::DRIVE_STRAIGHT);
        this_thread::sleep_for(chrono::milliseconds(1000));
        robot.sendDriveCommand(speed, Create::DRIVE_INPLACE_COUNTERCLOCKWISE);
        this_thread::sleep_for(chrono::milliseconds(300));
        robot.sendDriveCommand(speed, Create::DRIVE_STRAIGHT);
      
      }
      short wallSignal = robot.wallSignal();
      if (wallSignal > 0) {
        cout << "Wall signal " << robot.wallSignal() << endl;

        if (prevWallSignal == 0) {
          Camera.grab();
          Camera.retrieve (bgr_image);
          cv::cvtColor(bgr_image, rgb_image, CV_RGB2BGR);
          cv::imwrite("irobot_image.jpg", rgb_image);
          cout << "Taking photo" << endl;
        }
      }
      prevWallSignal = wallSignal;
      if (robot.advanceButton ())
      {
        cout << "Advance button pressed" << endl;
        speed = -1 * speed;
        ledColor += 10;
        if (ledColor > 255)
          ledColor = 0;

        robot.sendDriveCommand (speed, Create::DRIVE_INPLACE_CLOCKWISE);
        if (speed < 0)
          robot.sendLedCommand (Create::LED_PLAY,
              ledColor,
              Create::LED_INTENSITY_FULL);
        else
          robot.sendLedCommand (Create::LED_ADVANCE,
              ledColor,
              Create::LED_INTENSITY_FULL);
      }

      // You can add more commands here.
      this_thread::sleep_for(chrono::milliseconds(100));
    }
    
    cout << "Play button pressed, stopping Robot" << endl;
    robot.sendDriveCommand (0, Create::DRIVE_STRAIGHT);
  }
  catch (InvalidArgument& e)
  {
    cerr << e.what () << endl;
    return 3;
  }
  catch (CommandNotAvailable& e)
  {
    cerr << e.what () << endl;
    return 4;
  }
}

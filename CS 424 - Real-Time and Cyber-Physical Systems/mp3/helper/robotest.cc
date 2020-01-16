#include <SerialStream.h>
#include <ctime>
#include <iostream>
#include <chrono>
#include <thread>
#include <cstdlib>
#include <raspicam/raspicam_cv.h>
#include <opencv2/imgproc/imgproc.hpp>

#include "./object-identification/RobotIdentification.hh"
#include "./irobot/irobot-create.hh"

using namespace iRobot;
using namespace LibSerial;
using namespace std;

<<<<<<< HEAD
RobotIdentification test;

void *detect_objects(raspicam::RaspiCam_Cv* CameraPtr){

    // cv::Mat image, bgr_image;

    // CameraPtr->grab();
    // CameraPtr->retrieve (bgr_image);
    // cv::cvtColor(bgr_image, image, CV_BGR2GRAY);

    cv::Mat image = imread("./object-identification/irobot_scene_1.jpg", IMREAD_GRAYSCALE);
    test.runIdentify(image);
    
}

=======
namespace {
  const Create::note_t kNote(31, 120);
  const Create::song_t kSong = {kNote};
  constexpr int kSongId = 0;
}

void handleSound(std::shared_ptr<Create> robot, double multiplier) {
    robot->sendPlaySongCommand(kSong);
    const short wallSignal = robot->wallSignal();
    // high value -> wall is close
    if (wallSignal > 0) {
        const long sleepms = 1.0 / wallSignal * multiplier;
        this_thread::sleep_for(chrono::milliseconds(sleepms));
    }
    handleSound(robot, multiplier);
}
>>>>>>> 925e23064e21d58458ad6a1f3d26b63b6b2c3731

int main ()
{

    //Initialize 
    raspicam::RaspiCam_Cv Camera;
    if (!Camera.open()) {
        cerr << "Error opening the camera" << endl;
        return -1;
    }
<<<<<<< HEAD

    thread detect_objects_t(detect_objects, &Camera);
    detect_objects_t.join();
    /*
    char serial_loc[] = "/dev/ttyUSB0";

    try {

        //Initialize 
        raspicam::RaspiCam_Cv Camera;
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

        // Stream some sensors.
        Create::sensorPackets_t sensors;
        sensors.push_back(Create::SENSOR_BUMPS_WHEELS_DROPS);
        sensors.push_back(Create::SENSOR_WALL_SIGNAL);
        sensors.push_back (Create::SENSOR_BUTTONS);

        robot.sendStreamCommand (sensors);
        cout << "Sent Stream Command" << endl;
    
    
        while (!robot.playButton())
        {
            //Bump code
            if (!backingUp && (robot.bumpLeft() || robot.bumpRight())) {                
                backingUp = true;
                blinking = true;

                //Stop the robot
                robot.sendDriveCommand(0, Create::DRIVE_STRAIGHT);
                //Start blinking the LEDs
                thread (led_blink, &robot).detach();
                //Start backing up
                thread (backup_drive, &robot, &Camera).detach(); 
            }

            //Wall signal code
            wallSignal = robot.wallSignal();
            if (wallSignal > 0) {
                cout << "Wall signal " << robot.wallSignal() << endl;

                //Play sound
                short frequency = 32, duration = 7;
                if (wallSignal>3){
			if (wallSignal>60){
				frequency = 90;
			}
			else{
				frequency = wallSignal+54;
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
        robot.sendDriveCommand (0, Create::DRIVE_STRAIGHT);
    }
    catch (InvalidArgument& e)
    {
        cerr << e.what () << endl;
        return 3;
    }
    catch (CommandNotAvailable& e)
    {
        cerr << e.what() << endl;
        return 4;
    }
    catch (const ios_base::failure &e) {
        cerr << e.code().message() << endl;
        return 5;
    }
    */
=======
    cout << "Opened Camera" << endl;
    SerialStream stream (serial_loc, LibSerial::SerialStreamBuf::BAUD_57600);
    cout << "Opened Serial Stream to" << serial_loc << endl;
    this_thread::sleep_for(chrono::milliseconds(1000));
    std::shared_ptr<Create> robot(new Create(stream));

    cout << "Created iRobot Object" << endl;
    robot->sendFullCommand();
    cout << "Setting iRobot to Full Mode" << endl;
    this_thread::sleep_for(chrono::milliseconds(1000));
    robot->sendSongCommand(kSongId, kSong);
    cout << "Sending Song to Robot" << endl;
    cout << "Robot is ready" << endl;

    cout << "Starting sound thread!" << endl;
    const thread sound_handler(handleSound, robot, 1000.0);

    // Let's stream some sensors.
    Create::sensorPackets_t sensors;
    sensors.push_back(Create::SENSOR_BUMPS_WHEELS_DROPS);
    sensors.push_back(Create::SENSOR_WALL_SIGNAL);
    sensors.push_back(Create::SENSOR_BUTTONS);

    robot->sendStreamCommand(sensors);
    cout << "Sent Stream Command" << endl;
    // Let's turn!
    int speed = 287;
    int ledColor = Create::LED_COLOR_GREEN;
    robot->sendDriveCommand(speed, Create::DRIVE_STRAIGHT);
    robot->sendLedCommand(Create::LED_PLAY, 0, 0);
    cout << "Sent Drive Command" << endl;

    short wallSignal, prevWallSignal = 0;
    while (!robot->playButton())
    {
      if (robot->bumpLeft() || robot->bumpRight()) {
        cout << "Bump !" << endl;
        robot->sendDriveCommand(-speed, Create::DRIVE_STRAIGHT);
        this_thread::sleep_for(chrono::milliseconds(1000));
        robot->sendDriveCommand(speed, Create::DRIVE_INPLACE_COUNTERCLOCKWISE);
        this_thread::sleep_for(chrono::milliseconds(300));
        robot->sendDriveCommand(speed, Create::DRIVE_STRAIGHT);
      }
      short wallSignal = robot->wallSignal();
      if (wallSignal > 0) {
        cout << "Wall signal " << robot->wallSignal() << endl;

        if (prevWallSignal == 0) {
          Camera.grab();
          Camera.retrieve(bgr_image);
          cv::cvtColor(bgr_image, rgb_image, CV_RGB2BGR);
          cv::imwrite("irobot_image.jpg", rgb_image);
          cout << "Taking photo" << endl;
        }
      }
      prevWallSignal = wallSignal;
      if (robot->advanceButton())
      {
        cout << "Advance button pressed" << endl;
        speed = -1 * speed;
        ledColor += 10;
        if (ledColor > 255)
          ledColor = 0;

        robot->sendDriveCommand(speed, Create::DRIVE_INPLACE_CLOCKWISE);
        if (speed < 0) {
          robot->sendLedCommand(Create::LED_PLAY,
              ledColor,
              Create::LED_INTENSITY_FULL);
        }
        else {
          robot->sendLedCommand(Create::LED_ADVANCE,
              ledColor,
              Create::LED_INTENSITY_FULL);
        }
      }

      // You can add more commands here.
      this_thread::sleep_for(chrono::milliseconds(100));
    }
    
    cout << "Play button pressed, stopping Robot" << endl;
    robot->sendDriveCommand (0, Create::DRIVE_STRAIGHT);
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
>>>>>>> 925e23064e21d58458ad6a1f3d26b63b6b2c3731
}


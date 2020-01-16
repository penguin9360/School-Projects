#include <SerialStream.h>
#include <chrono>
#include <cstdlib>
#include <ctime>
#include <iostream>
#include <mutex>
#include <opencv2/imgproc/imgproc.hpp>
#include <raspicam/raspicam_cv.h>
#include <thread>

#include "./irobot/irobot-create.hh"
#include "./thread-safe-robot/thread_safe_robot.hh"

using namespace iRobot;
using namespace LibSerial;
using namespace std;

using note_t = Create::note_t;

namespace {
Create::song_t kSong1 = {note_t{50, 10}};
Create::song_t kSong2 = {note_t{70, 10}};

const vector<note_t> russia = {
    note_t{67, 30}, note_t{72, 30}, note_t{67, 30}, note_t{69, 30},
    note_t{71, 30}, note_t{64, 30}, note_t{64, 30}, note_t{69, 30},
    note_t{67, 30}, note_t{65, 30}, note_t{67, 30}, note_t{60, 30},
    note_t{60, 30}, note_t{62, 30}, note_t{62, 30}, note_t{64, 30},
    note_t{65, 30}, note_t{65, 30}, note_t{67, 30}, note_t{69, 30},
    note_t{71, 30}, note_t{72, 30}, note_t{74, 30}, note_t{76, 30},
    note_t{74, 30}, note_t{72, 30}, note_t{74, 30}, note_t{71, 30},
    note_t{67, 30}, note_t{72, 30}, note_t{71, 30}, note_t{69, 30},
    note_t{71, 30}, note_t{64, 30}, note_t{64, 30}, note_t{69, 30},
    note_t{67, 30}, note_t{65, 30}, note_t{67, 30}, note_t{60, 30},
    note_t{60, 30}, note_t{72, 30}, note_t{71, 30}, note_t{69, 30},
    note_t{67, 30}, note_t{71, 30}, note_t{72, 30}, note_t{74, 30},
    note_t{76, 30}};

void playSong(ThreadSafeRobot *robot) {
  ThreadSafeRobot &rob_ref = *robot;
  while (true) {
    rob_ref->sendSongCommand(3, kSong2);
    rob_ref->sendPlaySongCommand(3);
    this_thread::sleep_for(chrono::milliseconds(300));
  }
}

} // namespace

int main() {

  // Initialize
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
    ThreadSafeRobot robot(new Create(stream), new std::mutex());
    cout << "Created iRobot Object" << endl;
    robot->sendFullCommand();
    cout << "Setting iRobot to Full Mode" << endl;
    this_thread::sleep_for(chrono::milliseconds(1000));
    cout << "Robot is ready" << endl;

    // std::thread(&playSong, &robot).detach();
    for (auto &n : russia) {
      robot->sendSongCommand(7, Create::song_t{n});
      robot->sendPlaySongCommand(7);
      this_thread::sleep_for(chrono::milliseconds(500));
    }

    // while (true) {
    //   robot->sendSongCommand(7, kSong1);
    //   robot->sendPlaySongCommand(7);
    //   this_thread::sleep_for(chrono::milliseconds(500));
    // }
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

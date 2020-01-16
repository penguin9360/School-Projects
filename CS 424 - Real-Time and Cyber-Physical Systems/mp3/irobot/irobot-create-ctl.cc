// Copyright (C) 2008 by Thomas Moulard, the University of Southern California
// (USC), and iLab at USC.
//
// This file is part of the iRobot Create library.
//
// libirobot-create is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// libirobot-create is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with libirobot-create.  If not, see <http://www.gnu.org/licenses/>.

/**
 * \file irobot-create-ctl.cc
 *
 * \brief iRobot Create command line control software.
 */
#include <sstream>
#include <string>
#include <stdlib.h>
#include "irobot-create.hh"

#include "config.h"

using namespace iRobot;

#define APPLY_ON_OPCODES(Macro)                                         \
  Macro (Create::OPCODE_START, "start", 0, 0,);                         \
  Macro (Create::OPCODE_BAUD, "baud", 1, 1,                             \
         robot_.sendBaudCommand (castArg<Create::Baud> (argv[2])));     \
  Macro (Create::OPCODE_SAFE, "safe", 0, 0,);                           \
  Macro (Create::OPCODE_FULL, "full", 0, 0, robot_.sendFullCommand ()); \
  Macro (Create::OPCODE_SPOT, "spot", 0, 0,                             \
         robot_.sendDemoCommand (Create::DEMO_SPOT_COVER));             \
  Macro (Create::OPCODE_COVER, "cover", 0, 0,                           \
         robot_.sendDemoCommand (Create::DEMO_COVER));                  \
  Macro (Create::OPCODE_COVER_AND_DOCK, "cover-dock", 0, 0,             \
         robot_.sendDemoCommand (Create::DEMO_COVER_AND_DOCK));         \
  Macro (Create::OPCODE_DEMO, "demo", 1, 1,                             \
         robot_.sendDemoCommand (castArg<Create::Demo> (argv[2])));     \
  Macro (Create::OPCODE_DIGITAL_OUTPUTS, "digital-outputs", 3, 3,       \
         robot_.sendDigitalOutputsCommand                               \
         (castArg<bool> (argv[2]),                                      \
          castArg<bool> (argv[3]),                                      \
          castArg<bool> (argv[4])));                                    \
  Macro (Create::OPCODE_DRIVE, "drive", 2, 2,                           \
         robot_.sendDriveCommand                                        \
         (castArg<int> (argv[2]),                                       \
          castArg<int> (argv[3])));                                     \
  Macro (Create::OPCODE_WAIT_EVENT+1,                                   \
         "drive-straight", 1, 1,                                        \
         robot_.sendDriveCommand                                        \
         (castArg<int> (argv[2]),                                       \
          Create::DRIVE_STRAIGHT));                                     \
  Macro (Create::OPCODE_WAIT_EVENT+2,                                   \
         "turn-clockwise", 1, 1,                                        \
         robot_.sendDriveCommand                                        \
         (castArg<int> (argv[2]),                                       \
          Create::DRIVE_INPLACE_CLOCKWISE));                            \
  Macro (Create::OPCODE_WAIT_EVENT+3,                                   \
         "turn-counter-clockwise", 1, 1,                                \
         robot_.sendDriveCommand                                        \
         (castArg<int> (argv[2]),                                       \
          Create::DRIVE_INPLACE_COUNTERCLOCKWISE));                     \
  Macro (Create::OPCODE_DRIVE_DIRECT, "drive-direct", 2, 2,             \
         robot_.sendDriveDirectCommand                                  \
         (castArg<int> (argv[2]),                                       \
          castArg<int> (argv[3])));                                     \
  Macro (Create::OPCODE_LEDS, "leds", 3, 3,                             \
         robot_.sendLedCommand                                          \
         (castArg<Create::Led> (argv[2]),                               \
         castArg<unsigned char> (argv[3]),                              \
         castArg<unsigned char> (argv[4])));                            \
  Macro (Create::OPCODE_LOW_SIDE_DRIVERS, "low-side", 3, 3,             \
         robot_.sendLowSideDriversCommand                               \
         (castArg<bool> (argv[2]),                                      \
          castArg<bool> (argv[3]),                                      \
          castArg<bool> (argv[4])));                                    \
  Macro (Create::OPCODE_PAUSE_RESUME_STREAM, "pause-stream", 1, 1,      \
         robot_.sendPauseStreamCommand                                  \
         (castArg<Create::StreamState> (argv[2])));                     \
  Macro (Create::OPCODE_PLAY, "play", 1, 1,                             \
         robot_.sendPlaySongCommand (castArg<unsigned char> (argv[2]))); \
  Macro (Create::OPCODE_PLAY_SCRIPT, "play-script", 0, 0,               \
         robot_.sendPlayScriptCommand ());                              \
  Macro (Create::OPCODE_PWM_LOW_SIDE_DRIVERS, "pwm-low-side", 3, 3,     \
         robot_.sendPwmLowSideDriversCommand                            \
         (castArg<unsigned char> (argv[2]),                             \
          castArg<unsigned char> (argv[3]),                             \
          castArg<unsigned char> (argv[4])));                           \
  Macro (Create::OPCODE_QUERY_LIST, "query-list", 0, 0,                 \
         sendQueryList (argc, argv));                                   \
  Macro (Create::OPCODE_SCRIPT, "script", 0, 0, sendScript (argc, argv)); \
  Macro (Create::OPCODE_SEND_IR, "send-ir", 1, 1,                       \
         robot_.sendIrCommand (castArg<unsigned char> (argv[2])));      \
  Macro (Create::OPCODE_SENSORS, "sensors", 1, 1,                       \
         robot_.sendSensorsCommand                                      \
         (castArg<Create::SensorPacket> (argv[2])); showSensors ());    \
  Macro (Create::OPCODE_SHOW_SCRIPT, "show-script", 0, 0,               \
         robot_.sendShowScriptCommand ());                              \
  Macro (Create::OPCODE_SONG, "song", 1, 1+16*2, sendSong (argc, argv)); \
  Macro (Create::OPCODE_STREAM, "stream", 1, 999, sendStream (argc, argv)); \
  Macro (Create::OPCODE_WAIT_TIME, "wait-time", 1, 1,                   \
         robot_.sendWaitTimeCommand (castArg<unsigned char> (argv[2]))); \
  Macro (Create::OPCODE_WAIT_DISTANCE, "wait-distance", 1, 1,           \
         robot_.sendWaitDistanceCommand (castArg<int> (argv[2])));      \
  Macro (Create::OPCODE_WAIT_ANGLE, "wait-angle", 1, 1,                 \
         robot_.sendWaitAngleCommand (castArg<int> (argv[2])));         \
  Macro (Create::OPCODE_WAIT_EVENT, "wait-event", 1, 1,                 \
         robot_.sendWaitEventCommand (castArg<Create::Event> (argv[2])));

template <typename T>
T castArg (const char*);

#define CASTARG_ENUM(ENUM_TYPE)                 \
  template <>                                   \
  ENUM_TYPE castArg (const char* arg)           \
  {                                             \
    std::istringstream i (arg);                 \
    int res = 0;                                \
    if (!(i >> res))                            \
      res = 0;                                  \
    return static_cast<ENUM_TYPE> (res);        \
  }

CASTARG_ENUM (Create::Baud)
CASTARG_ENUM (Create::Demo)
CASTARG_ENUM (Create::Event)
CASTARG_ENUM (Create::EventState)
CASTARG_ENUM (Create::DriveCommand)
CASTARG_ENUM (Create::Led)
CASTARG_ENUM (Create::SensorPacket)
CASTARG_ENUM (Create::StreamState)
CASTARG_ENUM (bool);
CASTARG_ENUM (int);
CASTARG_ENUM (unsigned char);


template <typename T>
T castArg (const char*)
{
  std::cerr << "Invalid conversion." << std::endl;
  exit (-1);
}

struct Application
{


#define SEND_CMD(OPCODE, OPCODE_STRING, NARGMIN, NARGMAX, CODE)    \
  case OPCODE:                                                     \
  if (argc < (NARGMIN) + 2)                                        \
    usage ();                                                      \
  if (argc > (NARGMAX) + 2)                                        \
    usage ();                                                      \
  CODE;                                                            \
  break

  Application (int argc, char ** argv, std::iostream& stream)
    : binaryName_ (argv[0]),
      mode_ (),
      robot_ (stream)
  {
    if (argc < 2)
      usage ();
    std::string argv1 = argv[1];
    if (argv1 == "--help"
        || argv1 == "-h"
        || argv1 == "help")
      help ();
    if (argv1 == "--version"
        || argv1 == "-v"
        || argv1 == "version")
      version ();

    mode_ = getMode (argv[1]);

    try
      {
        if (mode_ == Create::OPCODE_START)
          return;
        robot_.sendSafeCommand ();
        switch (mode_)
          {
            APPLY_ON_OPCODES (SEND_CMD)
          default:
              usage ();
          }
      }
    catch (std::runtime_error& e)
      {
        std::cerr << e.what () << std::endl;
        exit (2);
      }
    catch (...)
      {
        std::cerr << "Unexpected error" << std::endl;
        exit (42);
      }
  }

#define STR_TO_OPCODE(OPCODE, OPCODE_STRING, NARGMIN, NARGMAX, CODE)    \
  if (m == OPCODE_STRING)                                               \
    return static_cast<Create::Opcode> (OPCODE)

  Create::Opcode getMode (const std::string& m)
  {
    APPLY_ON_OPCODES (STR_TO_OPCODE);
    return static_cast<Create::Opcode> (0);
  }

#define PRINT_ACTIONS(OPCODE, OPCODE_STRING, NARGMIN, NARGMAX, CODE)    \
  std::cout << "\t" << OPCODE_STRING << std::endl

  void help (int status = 0)
  {
    std::cout
      << "Usage: irobot-create-ctl <action> [options]" << std::endl
      << std::endl
      << "Available actions are:" << std::endl;
    APPLY_ON_OPCODES (PRINT_ACTIONS);
    std::cout << std::endl;
    std::cout << "Additional actions are:" << std::endl
              << "\t" "help (this message)" << std::endl
              << "\t" "version" << std::endl;

    std::cout
      << std::endl
      << "Display the specified action on the standard output using the"
      << std::endl
      << "Open Interface protocol version 2. The stream can be redirected"
      << std::endl
      << "to a device file to send the action to a real robot." << std::endl
      << std::endl
      << "Example (serial port): irobot-create-ctl safe > /dev/ttySO"
      << std::endl
      << "Example (bluetooth): irobot-create-ctl safe > /dev/rfcomm0"
      << std::endl;

    std::cout
      << std::endl
      << "Report bugs to <" << PACKAGE_BUGREPORT << ">" << std::endl;

    exit (status);
  }

  void sendScript (int argc, char** argv)
  {
    Create::opcodes_t script;

    for (int i = 2; i < argc; ++i)
      script.push_back (castArg<unsigned char> (argv[i]));
    robot_.sendScriptCommand (script);
  }

  void sendSong (int argc, char** argv)
  {
    Create::song_t song;
    if (argc % 2 == 0)
      usage ();

    const unsigned char sid = castArg<unsigned char> (argv[2]);
    for (int i = 3; i + 1 < argc; i += 2)
      song.push_back (std::make_pair (castArg<unsigned char> (argv[i]),
                                      castArg<unsigned char> (argv[i+1])));
    robot_.sendSongCommand (sid, song);
  }

#define SENSOR_CMD(CALL)                                                \
  Create::sensorPackets_t sensors;                                      \
  for (int i = 2; i < argc; ++i)                                        \
    sensors.push_back (castArg<Create::SensorPacket> (argv[i]));        \
  CALL

  void sendQueryList (int argc, char** argv)
  {
    SENSOR_CMD (robot_.sendQueryListCommand (sensors));
    showSensors ();
  }

  void sendStream (int argc, char** argv)
  {
    SENSOR_CMD (robot_.sendStreamCommand (sensors));
    showSensors ();
  }

  void showSensors ()
  {
    std::cerr
      << "Wheeldrop: "
      << " caster: " << ((!!robot_.wheeldropCaster ()) ? "true" : "false")
      << " left wheel: " << ((!!robot_.wheeldropLeft ()) ? "true" : "false")
      << " right wheel: " << ((!!robot_.wheeldropRight ()) ? "true" : "false")
      << " bump left: " << ((!!robot_.bumpLeft ()) ? "true" : "false")
      << " bump right: " << ((!!robot_.bumpRight ()) ? "true" : "false")
      << std::endl;

    std::cerr
      << "Wall: " << ((!!robot_.wall ()) ? "true" : "false")
      << std::endl;

    std::cerr
      << "Cliff: "
      << " left: " << ((!!robot_.cliffLeft ()) ? "true" : "false")
      << " front-left: " << ((!!robot_.cliffFrontLeft ()) ? "true" : "false")
      << " front-right: " << ((!!robot_.cliffFrontRight ()) ? "true" : "false")
      << " right: " << ((!!robot_.cliffRight ()) ? "true" : "false") << std::endl;

    std::cerr
      << "Digital pin status: "
      << " device detect: " << ((!!robot_.deviceDetect ()) ? "true" : "false")
      << " 3: " << ((!!robot_.digitalInput3 ()) ? "true" : "false")
      << " 2: " << ((!!robot_.digitalInput2 ()) ? "true" : "false")
      << " 1: " << ((!!robot_.digitalInput1 ()) ? "true" : "false")
      << " 0: " << ((!!robot_.digitalInput0 ()) ? "true" : "false") << std::endl;

    std::cerr
      << "Analog signal: " << robot_.analogSignal () << std::endl;

    std::cerr
      << "Chargers available? "
      << " home base: "
      << ((!!robot_.homeBaseChargerAvailable ()) ? "true" : "false")
      << " internal: "
      << ((!!robot_.internalChargerAvailable ()) ? "true" : "false") << std::endl;

    std::cerr
      << "Virtual Wall: "
      << ((!!robot_.virtualWall ()) ? "true" : "false") << std::endl;

    std::cerr
      << "Overcurrent: "
      << " left wheel: "
      << ((!!robot_.leftWheelOvercurrent ()) ? "true" : "false")
      << " right wheel: "
      << ((!!robot_.rightWheelOvercurrent ()) ? "true" : "false")
      << " LD2: " << ((!!robot_.ld2Overcurrent ()) ? "true" : "false")
      << " LD1: " << ((!!robot_.ld1Overcurrent ()) ? "true" : "false")
      << " LD0: " << ((!!robot_.ld0Overcurrent ()) ? "true" : "false");

    std::cerr
      << "IR: " << robot_.ir () << std::endl;

    std::cerr
      << "Buttons: "
      << " advance: "<< ((!!robot_.advanceButton ()) ? "true" : "false")
      << " play: " << ((!!robot_.playButton ()) ? "true" : "false") << std::endl;

    std::cerr
      << "Distance: " << robot_.distance () << std::endl;

    std::cerr
      << "Angle: " << robot_.angle () << std::endl;

    std::cerr
      << "Charging state: " << robot_.chargingState () << std::endl;

    std::cerr
      << "Battery voltage: " << robot_.batteryVoltage () << std::endl;

    std::cerr
      << "Battery current: " << robot_.batteryCurrent () << std::endl;

    std::cerr
      << "Battery temperature: " << robot_.batteryTemperature () << std::endl;

    std::cerr
      << "Battery charge: " << robot_.batteryCharge () << std::endl;

    std::cerr
      << "Battery capacity: " << robot_.batteryCapacity () << std::endl;

    std::cerr
      << "Wall signal: " << robot_.wallSignal () << std::endl;

    std::cerr
      << "Cliff left signal: "
      << robot_.cliffLeftSignal () << std::endl;

    std::cerr
      << "Cliff front-left signal: "
      << robot_.cliffFrontLeftSignal () << std::endl;

    std::cerr
      << "Cliff front-right signal: "
      << robot_.cliffFrontRightSignal () << std::endl;

    std::cerr
      << "Cliff right signal: " << robot_.cliffRightSignal () << std::endl;

    std::cerr
      << "Song number: " << static_cast<int> (robot_.songNumber ())
      << std::endl;

    std::cerr
      << "Song playing: "
      << ((!!robot_.songPlaying ()) ? "true" : "false") << std::endl;

    std::cerr
      << "Streamed packets: " << robot_.streamPackets () << std::endl;

    std::cerr
      << "Requested velocty: "
      << robot_.requestedVelocity () << std::endl;

    std::cerr
      << "Request radius: "
      << robot_.requestedRadius () << std::endl;

    std::cerr
      << "Request left velocity: "
      << robot_.requestedLeftVelocity () << std::endl;

    std::cerr
      << "Request right velocity: "
      << robot_.requestedRightVelocity () << std::endl;

  }

  void usage ()
  {
    help (1);
  }

  void version ()
  {
    std::cerr
      << PACKAGE_STRING << std::endl
      << "Copyright (C) 2008  iLab."
      << std::endl
      << "This is free software; see the source for copying conditions."
      << std::endl
      << "There is NO warranty; not even for MERCHANTABILITY or FITNESS FOR A"
      << std::endl
      << "PARTICULAR PURPOSE." << std::endl;
    exit (0);
  }

private:
  std::string binaryName_;
  Create::Opcode mode_;
  Create robot_;
};

int main (int argc, char** argv)
{
  std::stringstream stream;
  Application app (argc, argv, stream);
  std::cout << stream.str ();
}

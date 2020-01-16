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
 * \file irobot-create.hh
 *
 * \brief Declaration of the Create class.
 */

#ifndef IROBOT_CREATE_HH
# define IROBOT_CREATE_HH
# include <iostream>
# include <queue>
# include <stdexcept>
# include <utility>
# include <vector>

# include "irobot-create-fwd.hh"

namespace iRobot
{

  /// \brief Create an exception class which inherits from std::runtime_error.
  ///
  /// The class inherits from std::runtime_error and is default constructible.
  /// The default error message is set by the DESCRIPTION parameter, a specific
  /// error message can also be set by passing a string to the constructor.
  /// \param NAME Name of the generated exception class.
  /// \param DESCRIPTION Default error message.
# define IROBOT_MAKE_EXCEPTION(NAME, DESCRIPTION)                       \
  struct NAME : public std::runtime_error                               \
  {                                                                     \
    explicit NAME ()                                                    \
      : std::runtime_error (DESCRIPTION)                                \
    {}                                                                  \
    explicit NAME (const std::string& what)                             \
      : std::runtime_error (what)                                       \
    {}                                                                  \
  }

  /// \brief Error thrown when the command is not valid in the current mode.
  ///
  /// The Create class never changes the mode behind your back, so you have
  /// to make sure that the command you want to send is supported by the
  /// current mode.
  /// \see Create::sendStartCommand
  /// \see Create::sendSafeCommand
  /// \see Create::sendFullCommand
  IROBOT_MAKE_EXCEPTION (CommandNotAvailable,
                         "This command is not available in this mode.");

  /// \brief Exception thrown when an argument's value is invalid.
  /// Typically, it means that an argument is out of bound.
  IROBOT_MAKE_EXCEPTION (InvalidArgument, "This argument is invalid.");

  /// \brief Exception thrown if no serial port support is available but
  /// one tries to use the feature anyway.
  IROBOT_MAKE_EXCEPTION (LibSerialNotAvailable, "Serial port support has not been compiled.");

# undef IROBOT_MAKE_EXCEPTION


  /// \brief Robot communication class, wraps Open Interface version 2.

  /// This class allows easy and safe communication with an iRobot Create
  /// robot.  When this class is instanciated, the specified std::iostream will
  /// be used for the communication.
  ///
  /// After being initialized, the object automatically set the robot into
  /// the passive mode.
  class Create
  {
  public:
    /// Define a song note (frequency, duration).
    typedef std::pair<unsigned char, unsigned char> note_t;
    /// \brief Define a song (a list of notes).
    /// \see note_t
    typedef std::vector<note_t> song_t;

    /// \brief iRobot Create modes
    /// The mode defines which commands are available and
    /// how the robot behaves when some events happens
    /// like wheel drop or if a cliff is detected.
    enum Mode
      {
        /// \brief Start-up mode, no command can be sent except start.
        /// \see Create::sendStartCommand
        IROBOT_CREATE_OFF,
        /// Passive mode, actuators can not be controlled.
        IROBOT_CREATE_PASSIVE,
        /// \brief Safe mode, all commands are available.
        /// iRobot Create will stop if the wheels are dropped,
        /// if a cliff is detected or if the charger is plugged in
        /// and powered.
        IROBOT_CREATE_SAFE,
        /// \brief Full mode, all commands are available.
        /// No safety check is done in this mode. The robot
        /// may fall from cliffs and will keep running if the
        /// wheels are dropped.
        IROBOT_CREATE_FULL
      };

    /// Enumerate all possible communication speeds.
    enum Baud
      {
        /// Communicate at 300 bauds per second.
        BAUD_300 = 0,
        /// Communicate at 600 bauds per second.
        BAUD_600 = 1,
        /// Communicate at 1200 bauds per second.
        BAUD_1200 = 2,
        /// Communicate at 2400 bauds per second.
        BAUD_2400 = 3,
        /// Communicate at 4800 bauds per second.
        BAUD_4800 = 4,
        /// Communicate at 9600 bauds per second.
        BAUD_9600 = 5,
        /// Communicate at 14400 bauds per second.
        BAUD_14400 = 6,
        /// Communicate at 19200 bauds per second.
        BAUD_19200 = 7,
        /// Communicate at 28800 bauds per second.
        BAUD_28800 = 8,
        /// Communicate at 38400 bauds per second.
        BAUD_38400 = 9,
        /// Communicate at 57600 bauds per second.
        BAUD_57600 = 10,
        /// \brief Communicate at 115200 bauds per second.
        /// In this mode, be sure to keep at least 15 µs between each command.
        BAUD_115200 = 11
      };

    /// \brief Enumerate built-in demos.
    enum Demo
      {
        /// iRobot Create covers an entire room using a combination of
        /// behaviors,such as bouncing off of walls, following walls, and
        /// spiraling.
        DEMO_COVER = 0,

        /// Identical to the Cover demo, with one exception; if iRobot Create
        /// sees the Home Base’s* infrared signals, it uses these to move
        /// towards the Home Base and dock with it.
        DEMO_COVER_AND_DOCK = 1,

        /// iRobot Create spirals outward, then inward, to cover an area around
        /// its starting position.
        DEMO_SPOT_COVER = 2,

        /// iRobot Create tries to follow around the edges of a room using its
        /// wall sensor and bumper.
        DEMO_MOUSE = 3,

        /// iRobot Create continuously drives in a figure 8 pattern.
        DEMO_DRIVE_FIGURE_EIGHT = 4,

        /// iRobot Create drives forward when pushed from behind. If iRobot
        /// Create hits an obstacle while driving, it drives away from the
        /// obstacle.
        DEMO_WIMP = 5,

        /// iRobot Create drives toward a Virtual Wall when the back and sides
        /// of its Omnidirectional IR Receiver are covered with black
        /// electrical tape. When it touches the Virtual Wall or another
        /// obstacle, it stops.
        DEMO_HOME = 6,

        /// Identical to the Home demo, except iRobot Create goes back and
        /// forth between multiple Virtual Walls by bumping into one, turning
        /// around, driving to the next Virtual Wall, bumping into it and
        /// turning around to bump into the next Virtual Wall.
        DEMO_TAG = 7,

        /// iRobot Create plays the notes of Pachelbel’s Canon in sequence when
        /// its cliff sensors are activated.
        DEMO_PACHELBEL = 8,

        /// iRobot Create’s four cliff sensors play the notes of a chord,
        /// depending on how the bumper is pressed:
        /// * No bumper: G major
        /// * Right or left bumper: D major7
        /// * Both bumpers (center): C major
        DEMO_BANJO = 9,

        /// Abort current running demo.
        DEMO_ABORT = 255
      };

    /// \brief Enumerates special driving commands.
    enum DriveCommand
      {
        /// Drive straight.
        DRIVE_STRAIGHT,
        /// Turn in place clockwise.
        DRIVE_INPLACE_CLOCKWISE,
        /// Turn in place counter-clockwise.
        DRIVE_INPLACE_COUNTERCLOCKWISE
      };

    /// \brief Enumerate available leds (and combinations).
    /// The power led is not in this list because it is handled
    /// differently (as its color is not fixed).
    enum Led
      {
        /// No led.
        LED_NONE = 0,
        /// Play led only.
        LED_PLAY = 2,
        /// Advance led only.
        LED_ADVANCE = 8,
        /// All leds.
        LED_ALL = LED_PLAY | LED_ADVANCE
      };

    /// Activate or disable sensor streaming.
    enum StreamState
      {
        /// Disable sensor streaming.
        STREAM_STATE_OFF = 0,
        /// Activate sensor streaming.
        STREAM_STATE_ON = 1
      };

    /// Enumerate possible charging states.
    enum ChargingState
      {
        CHARGING_STATE_NOT_CHARGING = 0,
        CHARGING_STATE_RECONDITIONING_CHARGING = 1,
        CHARGING_STATE_FULL_CHARGING = 2,
        CHARGING_STATE_TRICKLE_CHARGING = 3,
        CHARGING_STATE_WAITING = 4,
        CHARGING_STATE_CHARGING_FAULT_CONDITION = 5

      };

    /// Enumerate available events.
    enum Event
      {
        /// A wheel is dropped.
        EVENT_WHEEL_DROP = 1,
        /// Front wheel is dropped.
        EVENT_FRONT_WHEEL_DROP = 2,
        /// Left wheel is dropped.
        EVENT_LEFT_WHEEL_DROP = 3,
        /// Right wheel is dropped.
        EVENT_RIGHT_WHEEL_DROP = 4,
        /// Front part has bumped.
        EVENT_BUMP = 5,
        /// Left part has bumped.
        EVENT_LEFT_BUMP = 6,
        /// Right part has bumped.
        EVENT_RIGHT_BUMP = 7,
        /// A virtual wall has been detected.
        EVENT_VIRTUAL_WALL = 8,
        /// A wall has been detected.
        EVENT_WALL = 9,
        /// A cliff has been detected.
        EVENT_CLIFF = 10,
        /// A cliff has been detected (left).
        EVENT_LEFT_CLIFF = 11,
        /// A cliff has been detected (front left).
        EVENT_FRONT_LEFT_CLIFF = 12,
        /// A cliff has been detected (front right).
        EVENT_FRONT_RIGHT_CLIFF = 13,
        /// A cliff has been detected (right).
        EVENT_RIGHT_CLIFF = 14,
        /// Home base has been detected.
        EVENT_HOME_BASE = 15,
        /// Advance button has been pushed.
        EVENT_ADVANCE_BUTTON = 16,
        /// Play button has been pushed.
        EVENT_PLAY_BUTTON = 17,
        /// Digital input 0 has changed.
        EVENT_DIGITAL_INPUT_0 = 18,
        /// Digital input 1 has changed.
        EVENT_DIGITAL_INPUT_1 = 19,
        /// Digital input 2 has changed.
        EVENT_DIGITAL_INPUT_2 = 20,
        /// Digital input 3 has changed.
        EVENT_DIGITAL_INPUT_3 = 21,
        /// Robot has switched to passive mode.
        EVENT_OI_MODE_PASSIVE = 22
      };

    /// Indicate an event state.
    enum EventState
      {
        /// The event is happening currently.
        EVENT_OCCURRING,
        /// The event is not happening currently.
        EVENT_NOT_OCCURRING
      };

    /// \brief Enumerate available opcodes.
    /// Opcode are the basics instructions that the protocol
    /// support. This class wraps them to avoid using them directly.
    /// The only case where you have to use them is when you want
    /// to send an Open Interface script.
    enum Opcode
      {
        /// Start command.
        OPCODE_START = 128,
        /// Baud command.
        OPCODE_BAUD = 129,
        /// Control command (equivalent to safe).
        OPCODE_CONTROL = 130,
        /// Safe command.
        OPCODE_SAFE = 131,
        /// Full command.
        OPCODE_FULL = 132,
        /// Spot demo command.
        OPCODE_SPOT = 134,
        /// Cover demo command.
        OPCODE_COVER = 135,
        /// Demo command.
        OPCODE_DEMO = 136,
        /// Driver command.
        OPCODE_DRIVE = 137,
        /// Low side drivers command.
        OPCODE_LOW_SIDE_DRIVERS = 138,
        /// Leds command.
        OPCODE_LEDS = 139,
        /// Song command.
        OPCODE_SONG = 140,
        /// Play song command.
        OPCODE_PLAY = 141,
        /// Sensors command.
        OPCODE_SENSORS = 142,
        /// Cover and dock demo command.
        OPCODE_COVER_AND_DOCK = 143,
        /// Pwm low side drivers command.
        OPCODE_PWM_LOW_SIDE_DRIVERS = 144,
        /// Driver direct command.
        OPCODE_DRIVE_DIRECT = 145,
        /// Digital ouputs command.
        OPCODE_DIGITAL_OUTPUTS = 147,
        /// Stream command.
        OPCODE_STREAM = 148,
        /// Query list command.
        OPCODE_QUERY_LIST = 149,
        /// Pause/resume stream command.
        OPCODE_PAUSE_RESUME_STREAM = 150,
        /// Send IR command.
        OPCODE_SEND_IR = 151,
        /// Script command.
        OPCODE_SCRIPT = 152,
        /// Play script command.
        OPCODE_PLAY_SCRIPT = 153,
        /// Show script command.
        OPCODE_SHOW_SCRIPT = 154,
        /// Wait time command.
        OPCODE_WAIT_TIME = 155,
        /// Wait distance command.
        OPCODE_WAIT_DISTANCE = 156,
        /// Wait angle command.
        OPCODE_WAIT_ANGLE = 157,
        /// Wait event command.
        OPCODE_WAIT_EVENT = 158
      };

    /// Enumerate available sensor packets.
    enum SensorPacket
      {
        /// Groups packets 7 to 26.
        SENSOR_GROUP_0 = 0,
        /// Groups packets 7 to 16.
        SENSOR_GROUP_1 = 1,
        /// Groups packets 17 to 20.
        SENSOR_GROUP_2 = 2,
        /// Groups packets 21 to 26.
        SENSOR_GROUP_3 = 3,
        /// Groups packets 27 to 34.
        SENSOR_GROUP_4 = 4,
        /// Groups packets 35 to 42.
        SENSOR_GROUP_5 = 5,
        /// Groups packets 7 to 42.
        SENSOR_GROUP_6 = 6,
        /// Wheel and bumper states.
        SENSOR_BUMPS_WHEELS_DROPS = 7,
        /// Wall sensor state.
        SENSOR_WALL = 8,
        /// Left cliff sensor state.
        SENSOR_CLIFF_LEFT = 9,
        /// Front left cliff sensor state.
        SENSOR_CLIFF_FRONT_LEFT = 10,
        /// Front right cliff sensor state.
        SENSOR_CLIFF_FRONT_RIGHT = 11,
        /// Right cliff sensor state.
        SENSOR_CLIFF_RIGHT = 12,
        /// Virtual wall sensor state.
        SENSOR_VIRTUAL_WALL = 13,
        /// Overcurrent sensors states.
        SENSOR_OVERCURRENTS = 14,
        /// IR bytes received.
        SENSOR_IR = 17,
        /// Buttons states.
        SENSOR_BUTTONS = 18,
        /// Traveled distance since last read.
        SENSOR_DISTANCE = 19,
        /// Turned angle since last read.
        SENSOR_ANGLE = 20,
        /// Charging state.
        SENSOR_CHARGING_STATE = 21,
        /// Battery voltage.
        SENSOR_VOLTAGE = 22,
        /// Batty current.
        SENSOR_CURRENT = 23,
        /// Battery temperature.
        SENSOR_BATTERY_TEMPERATURE = 24,
        /// Battery charge in milliamp-hours (mAh).
        SENSOR_BATTERY_CHARGE = 25,
        /// Battery charge capacity in milliamp-hours (mAh).
        SENSOR_BATTERY_CAPACITY = 26,
        /// Wall's sensor signal strength.
        SENSOR_WALL_SIGNAL = 27,
        /// Left cliff signal strength.
        SENSOR_CLIFF_LEFT_SIGNAL = 28,
        /// Front left cliff signal strength.
        SENSOR_CLIFF_FRONT_LEFT_SIGNAL = 29,
        /// Front right cliff signal strength.
        SENSOR_CLIFF_FRONT_RIGHT_SIGNAL = 30,
        /// Right cliff signal strength.
        SENSOR_CLIFF_RIGHT_SIGNAL = 31,
        /// Cargo Bay digital input strength.
        SENSOR_CARGO_BAY_DIGITAL_INPUT = 32,
        /// Cargo Bay analog input strength.
        SENSOR_CARGO_BAY_ANALOG_SIGNAL = 33,
        /// Available charging sources.
        SENSOR_CHARGING_SOURCES_AVAILABLE = 34,
        /// Current Open Interface mode.
        SENSOR_OI_MODE = 35,
        /// Current selected song.
        SENSOR_SONG_NUMBER = 36,
        /// Indicates whether or not a song is being played.
        SENSOR_SONG_PLAYING = 37,
        /// List of streamed packets.
        SENSOR_NUMBER_STREAM_PACKETS = 38,
        /// Requested velocity.
        SENSOR_REQUESTED_VELOCITY = 39,
        /// Requested radius.
        SENSOR_REQUESTED_RADIUS = 40,
        /// Requested right velocity.
        SENSOR_REQUESTED_RIGHT_VELOCITY = 41,
        /// Requested left velocity.
        SENSOR_REQUESTED_LEFT_VELOCITY = 42
      };

    /// \brief Define a vector of opcodes and arguments (a script).
    /// \see Opcode
    typedef std::vector<unsigned char> opcodes_t;

    /// \brief Define a vector of sensor packets.
    /// \see sendQueryListCommand
    /// \see sendStreamCommand
    typedef std::vector<SensorPacket> sensorPackets_t;

    typedef std::queue<SensorPacket> queriedPackets_t;

    /// Define the green color for power led.
    static const unsigned char LED_COLOR_GREEN = 0;
    /// Define the red color for power led.
    static const unsigned char LED_COLOR_RED = 255;

    /// Define minimum intensity for power led (off).
    static const unsigned char LED_INTENSITY_OFF = 0;
    /// Define full intensity for power led.
    static const unsigned char LED_INTENSITY_FULL = 255;

    /// Define minimum velocity for low side drivers.
    static const unsigned char LOW_SIDE_VELOCITY_MIN = 0;

    /// Define maximum velocity for low side drivers.
    static const unsigned char LOW_SIDE_VELOCITY_MAX = 128;

    /// Define minimum velocity for robot wheels motors.
    static const int VELOCITY_MIN = -500;
    /// Define maximum velocity for robot wheels motors.
    static const int VELOCITY_MAX = 500;

    /// Define minimum radius turn of the robot.
    static const int RADIUS_MIN = -2000;
    /// Define maximum radius turn of the robot.
    static const int RADIUS_MAX = 2000;

    /// Define minimum song id.
    static const unsigned char SONG_MIN = 0;
    /// Define maximum song id.
    static const unsigned char SONG_MAX = 15;

    /// Define song maximum size.
    static const unsigned char SONG_MAX_SIZE = 16;

    /// Define the value for a rest node (no sound).
    static const unsigned char NO_NOTE = 30;
    /// Define the minimum note (G).
    static const unsigned char NOTE_MIN = 31;
    /// Define the maximum note (G).
    static const unsigned char NOTE_MAX = 127;

    /// Define maximum script size.
    static const unsigned char SCRIPT_MAX_SIZE = 100;

    /// Stream header ``magic value''.
    static const unsigned char STREAM_HEADER = 19;


    /// Bit used to retrieve the wheeldrop caster status.
    static const unsigned char SENSOR_BIT_WHEELDROP_CASTER = 4;
    /// Bit used to retrieve the wheeldrop caster status.
    static const unsigned char SENSOR_BIT_WHEELDROP_LEFT = 3;
    /// Bit used to retrieve the wheeldrop caster status.
    static const unsigned char SENSOR_BIT_WHEELDROP_RIGHT = 2;
    /// Bit used to retrieve the bump left status.
    static const unsigned char SENSOR_BIT_BUMP_LEFT = 1;
    /// Bit used to retrieve the bump right status.
    static const unsigned char SENSOR_BIT_BUMP_RIGHT = 0;

    /// Bit used to retrieve the left wheel overcurrent status.
    static const unsigned char SENSOR_BIT_LEFTWHEELOVERCURRENT = 4;
    /// Bit used to retrieve the right wheel overcurrent status.
    static const unsigned char SENSOR_BIT_RIGHTWHEELOVERCURRENT = 3;
    /// Bit used to retrieve the LD2 overcurrent status.
    static const unsigned char SENSOR_BIT_LD2OVERCURRENT = 2;
    /// Bit used to retrieve the LD1 overcurrent status.
    static const unsigned char SENSOR_BIT_LD1OVERCURRENT = 1;
    /// Bit used to retrieve the LD0 overcurrent status.
    static const unsigned char SENSOR_BIT_LD0OVERCURRENT = 0;

    /// Bit used to retrieve the advance button status.
    static const unsigned char SENSOR_BIT_ADVANCEBUTTON = 2;
    /// Bit used to retrieve the play button status.
    static const unsigned char SENSOR_BIT_PLAYBUTTON = 0;

    /// Bit used to retrieve the device detect pin status.
    static const unsigned char SENSOR_BIT_DEVICEDETECT = 4;
    /// Bit used to retrieve the digital input 3 pin status.
    static const unsigned char SENSOR_BIT_DIGITALINPUT3 = 3;
    /// Bit used to retrieve the digital input 2 pin status.
    static const unsigned char SENSOR_BIT_DIGITALINPUT2 = 2;
    /// Bit used to retrieve the digital input 1 pin status.
    static const unsigned char SENSOR_BIT_DIGITALINPUT1 = 1;
    /// Bit used to retrieve the digital input 0 pin status.
    static const unsigned char SENSOR_BIT_DIGITALINPUT0 = 0;

    /// Bit used to retrieve whether the home base charger is available or not.
    static const unsigned char SENSOR_BIT_HOMEBASECHARGERAVAILABLE = 1;
    /// Bit used to retrieve whether the internal charger is available or not.
    static const unsigned char SENSOR_BIT_INTERNALCHARGERAVAILABLE = 0;

    /// \{

    /// \brief Construct an instance of Create.
    /// \param stream Stream used for communication.
    explicit Create (std::iostream& stream) throw (InvalidArgument);

    /// \brief Construct an instance of Create using serial port communication.
    /// \param stream Stream used for communication.
    explicit Create (LibSerial::SerialStream& stream)
      throw (InvalidArgument, LibSerialNotAvailable);

    /// \brief Destroy an instance of Create.
    ~Create () throw ();

    /// \}

    /// \{

    /// \brief Change the communication speed.
    /// After changing the communication speed,
    /// it is required to wait at least 100ms.
    /// \param speed New communication speed.
    void sendBaudCommand (Baud speed)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Switch to passive mode.
    /// Automatically done when the class is instanciated.
    void sendStartCommand ();

    /// \brief Switch to safe mode.
    void sendSafeCommand ()
      throw (CommandNotAvailable);

    /// \brief Switch to full mode.
    void sendFullCommand ()
      throw (CommandNotAvailable);

    /// \brief Run a built-in demo.
    /// \param demo Demo id.
    void sendDemoCommand (Demo demo)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Drive the robot.
    /// \param v Robot's velocity.
    /// \param r Robot's turning radius.
    /// \see sendDriveDirectCommand
    void sendDriveCommand (short v, short r)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Drive the robot using a special drigin mode.
    /// \param v Robot's velocity.
    /// \param dc Special driving mode.
    void sendDriveCommand (short v, DriveCommand dc)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Drive the two wheels separately.
    /// \param vr Right wheel's velocity.
    /// \param vl Left wheel's velocity.
    void sendDriveDirectCommand (short vr, short vl)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Change led status.
    /// \param l Switch on/off advance or play leds.
    /// \param c Power led color.
    /// \param i Power led light intensity.
    void sendLedCommand (Led l, unsigned char c, unsigned char i)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Change the digital output values.
    /// \param d1 Switch on/off first digital output.
    /// \param d2 Switch on/off second digital output.
    /// \param d3 Switch on/off third digital output.
    void sendDigitalOutputsCommand (bool d1, bool d2, bool d3)
      throw (CommandNotAvailable);

    /// \brief Drive low side drivers with variable power.
    /// \param lsd1 Low side driver 1 power.
    /// \param lsd2 Low side driver 2 power.
    /// \param lsd3 Low side driver 3 power.
    void sendPwmLowSideDriversCommand (unsigned char lsd1,
                                       unsigned char lsd2,
                                       unsigned char lsd3)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Switch on/off low side drivers.
    /// \param lsd1 Switch on/off low side driver 1.
    /// \param lsd2 Switch on/off low side driver 2.
    /// \param lsd3 Switch on/off low side driver 3.
    /// \see sendPwmLowSideDriversCommand
    void sendLowSideDriversCommand (bool lsd1, bool lsd2, bool lsd3)
      throw (CommandNotAvailable);

    /// \brief Send an IR command on low side driver 1.
    /// The data will be encoded using the iRobot's Create receiver format.
    /// \param v Sent value.
    void sendIrCommand (unsigned char v)
      throw (CommandNotAvailable);

    /// \brief Define a song.
    /// \param sid Song id.
    /// \param song Song to send.
    void sendSongCommand (unsigned char sid, const song_t& song)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Play a song
    /// \param sid Sond id to play.
    void sendPlaySongCommand (unsigned char sid)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Request the robot to send a particular sensor packet.
    /// \param sp Sensor packet to send.
    void sendSensorsCommand (SensorPacket sp)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Request the robot to send a list of sensor packets.
    /// \param lsp List of sensor packets to send
    void sendQueryListCommand (const sensorPackets_t& lsp)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Request the robot to start streaming some sensor packets.
    /// After the streaming is started, the requested values will be sent
    /// every 15ms.
    /// \param lsp List of sensor packets to stream.
    void sendStreamCommand (const sensorPackets_t& lsp)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Pause or resume sensor streaming.
    /// \param st Stream state to set.
    void sendPauseStreamCommand (StreamState st)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Send a script.
    /// \param script Script to send.
    void sendScriptCommand (const opcodes_t& script)
      throw (CommandNotAvailable, InvalidArgument);

    /// \brief Play the current stored script.
    void sendPlayScriptCommand ()
      throw (CommandNotAvailable);

    /// \brief Show the stored script.
    void sendShowScriptCommand ()
      throw (CommandNotAvailable);

    /// \brief Make the robot wait for a specific amount of time.
    /// \param t Time to wait in tenth of a second (resolution of 15 ms).
    void sendWaitTimeCommand (unsigned char t)
      throw (CommandNotAvailable);

    /// \brief Make the robot wait until it travelled a certain distance.
    /// \param d Distance to travel
    void sendWaitDistanceCommand (short d)
      throw (CommandNotAvailable);

    /// \brief Make the robot wait until it has rotated enough.
    /// \param a Angle (in degrees)
    void sendWaitAngleCommand (short a)
      throw (CommandNotAvailable);

    /// \brief Make the robot wait for a specific event.
    /// \param e Wait for this event.
    /// \param es Wait for this event state.
    void sendWaitEventCommand (Event e, EventState es = EVENT_OCCURRING)
      throw (CommandNotAvailable, InvalidArgument);
    /// \}

    /// \{

    /// \brief Read the stream to update sensors values.
    /// This handles both streamed data and specific queries
    /// through querylist or sensors commands.
    void updateSensors ();

    /// \brief Read streamed sensors and update sensors values.
    void readStream ();

    /// \}

    /// \{

    Mode mode () throw ();

    bool wheeldropCaster ();
    bool wheeldropLeft ();
    bool wheeldropRight ();
    bool bumpLeft ();
    bool bumpRight ();

    /// Get wall sensor value.
    bool wall ();

    bool cliffLeft ();
    bool cliffFrontLeft ();
    bool cliffFrontRight ();
    bool cliffRight ();
    bool deviceDetect ();
    bool digitalInput3 ();
    bool digitalInput2 ();
    bool digitalInput1 ();
    bool digitalInput0 ();
    short analogSignal ();
    bool homeBaseChargerAvailable ();
    bool internalChargerAvailable ();
    bool virtualWall ();
    bool leftWheelOvercurrent ();
    bool rightWheelOvercurrent ();
    bool ld2Overcurrent ();
    bool ld1Overcurrent ();
    bool ld0Overcurrent ();
    unsigned char ir ();
    bool advanceButton ();
    bool playButton ();
    short distance ();
    short angle ();
    ChargingState chargingState ();
    short batteryVoltage ();
    short batteryCurrent ();
    short batteryTemperature ();
    short batteryCharge ();
    short batteryCapacity ();
    short wallSignal ();
    short cliffLeftSignal ();
    short cliffFrontLeftSignal ();
    short cliffFrontRightSignal ();
    short cliffRightSignal ();
    unsigned char songNumber ();
    bool songPlaying ();
    unsigned char streamPackets ();
    short requestedVelocity ();
    short requestedRadius ();
    short requestedLeftVelocity ();
    short requestedRightVelocity ();

    /// \}

  protected:
    /// \brief Read a specific sensor packet and update sensors values.
    /// The next expected sensor packet will be read.
    bool readSensorPacket ();

    /// \brief Read sensor packet on a specified stream, update sensor values.
    bool readSensorPacket (SensorPacket, std::istream&);

  private:
    /// Code shared between constructors.
    void init () throw (InvalidArgument);

    /// Current robot's mode.
    Mode currentMode_;

    /// Stream used for communication.
    std::iostream& stream_;

    /// \{

    /// List of sensors currently streamed.
    sensorPackets_t streamedSensors_;

    /// \brief List of queried packets
    /// Sensor packets that have to be read but which are not streamed.
    queriedPackets_t queriedSensors_;

    /// Wheeldrop caster
    bool wheeldropCaster_;
    /// Wheeldrop left
    bool wheeldropLeft_;
    /// Wheeldrop right
    bool wheeldropRight_;
    /// Bump left
    bool bumpLeft_;
    /// Bump right
    bool bumpRight_;
    /// Wall sensor.
    bool wall_;
    /// Left cliff sensor.
    bool cliffLeft_;
    /// Front left cliff sensor.
    bool cliffFrontLeft_;
    /// Front right cliff sensor.
    bool cliffFrontRight_;
    /// Right cliff sensor.
    bool cliffRight_;
    /// Device detect pin state.
    bool deviceDetect_;
    /// Digital input 3 pin state.
    bool digitalInput3_;
    /// Digital input 2 pin state.
    bool digitalInput2_;
    /// Digital input 1 pin state.
    bool digitalInput1_;
    /// Digital input 0 pin state.
    bool digitalInput0_;
    /// Analog signal pin state.
    short analogSignal_;
    /// Is the home base charger available?
    bool homeBaseChargerAvailable_;
    /// Is the internal charger available?
    bool internalChargerAvailable_;
    /// Virtual wall sensor.
    bool virtualWall_;
    /// Left wheel overcurrent (true = overcurrent).
    bool leftWheelOvercurrent_;
    /// Right wheel overcurrent (true = overcurrent).
    bool rightWheelOvercurrent_;
    /// LD2 overcurrent (true = overcurrent).
    bool ld2Overcurrent_;
    /// LD1 overcurrent (true = overcurrent).
    bool ld1Overcurrent_;
    /// LD0 overcurrent (true = overcurrent).
    bool ld0Overcurrent_;
    /// Ir sensor.
    unsigned char ir_;
    /// Advance button state (true = pushed).
    bool advanceButton_;
    /// Play button (true = pushed).
    bool playButton_;
    /// Distance sensor.
    short distance_;
    /// Angle sensor.
    short angle_;
    /// Current charging state.
    ChargingState chargingState_;
    /// Battery voltage.
    short batteryVoltage_;
    /// Battery current.
    short batteryCurrent_;
    /// Battery temperature.
    short batteryTemperature_;
    /// Battery charge.
    short batteryCharge_;
    /// Battery capacity.
    short batteryCapacity_;
    /// Distance from wall.
    short wallSignal_;
    /// Distance from cliff (left).
    short cliffLeftSignal_;
    /// Distance from cliff (front left).
    short cliffFrontLeftSignal_;
    /// Distance from cliff (front right).
    short cliffFrontRightSignal_;
    /// Distance from cliff (right).
    short cliffRightSignal_;
    /// Current song number.
    unsigned char songNumber_;
    /// Is a song currently played?
    bool songPlaying_;
    /// Number of streamed packets.
    unsigned char streamPackets_;
    /// Last requested velocity (drive command).
    short requestedVelocity_;
    /// Last requested radius (drive command).
    short requestedRadius_;
    /// Last requested left velocity (drive direct command).
    short requestedLeftVelocity_;
    /// Last requested right velocity (drive direct command).
    short requestedRightVelocity_;

    /// \}
  };

} // end of namespace iRobot

#endif //! IROBOT_CREATE_HH

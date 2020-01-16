# Machine Problem 1: Explorer Robot

## Due

* 2 WEEKS INCLUDING SETUP TIME

## Problem Statement

* In this problem, you will set up iRobot Create with Raspberry Pi, and implement a program to move the robot according to the given instructions. The initial set up is described in detail in the `setup` page

* Write a C++ program that brings the robot to Full mode. It then starts moving straight at a speed of 287 mm/s.YOUR REPO STRUCTURE HERE / team# If the robot is nearing a wall or obstacle according to the wall sensor, it starts periodically playing a sound/note. As it nears the obstacle, it plays that sound more frequently. Eventually it will hit the obstacle, and that will be detected by using the bump sensor. After the bump, the robot should stop moving. It will then start the LEDs.

* Suppose G stands for Green, R for Red, and O for Off. Power LED has three states {R, G, O}. The Play and Advance LED has two states {G, O}. An Off sequence will run through {Power, Play, Advance}, with the Power LED having alternating colors. Therefore, the sequence of (Power, Play, Advance) LED states are GGO ⇒ OGG ⇒ ROG ⇒ RGO ⇒ OGG ⇒ GOG ⇒ ... You need to run this sequence over and over, with each state living for 200ms.

* Keeping the LEDs blinking sequence, the iRobot backs up 15 inches, with a speed of 165 mm/s. Then it again stops moving, takes a photo using the camera, and stores it in a file. After taking the picture, it turns off the LED sequence, rotates by a random angle between 120 degree to 240 degree at a speed of 107 mm/s. Then it again starts moving straight with the initial speed of 287 mm/s. The cycle continues in this way.

## Instructions and Directions

### Before You Start

* Make sure you fork the repository `irobot_lab_example` into your own teamspace, which contains the starter code for this lab assignment.

* Go to your forked project `settings` page and expand `Advanced` tab. You can rename your project so that you can distinguish it from the provided sample code repository. We suggest you name it something like `lab_assignments`, and all your future lab assignments will be developed in this repository.

* In your Raspberry Pi, type in the command
  <pre>
  git clone <i>your_git_repo_url</i>
  </pre>
  where *your_git_repo_url* can be found under the name of the repository you just cloned.
  
* Now we will do some test to see if your intial set up was successful. In your local repos you just cloned, type in `make`. The compilation will succeed only if you have performed all the steps (and installed all the necessary libraries as mentioned in the initial setup document​). Once you are done with the initial setup you can run robotest using the command ​`./robotest`

* The program will at first initialize camera, robot, etc. Once ready it will send a command to the robot to continuously stream (1) Bump and Wheel Drop sensor, (2) Wall Signal, (3) Buttons. After that, it will command the robot to drive straight at 200 mm/s. Then the robot will continuously look for bumps (with 100ms sleep in between). If the robot has bumped, it will back up straight, rotate, and continue running. If the robot has seen a “wall”, it will take a photo using the Raspberry Pi camera and save it to a file named `irobot_image.jpg`​. At every time, it will also print at the console what it is doing.

* Note the location of the wall sensor. The sensor works by transmitting a signal and measuring the strength of the received signal. This type of positioning allows it to detect a wall that is on the side. ​**You can artificially check the wall sensor by a bringing a white colored paper near it or taking it away**.

* If the "Advance" button is pressed, the robot will start rotating in place. Subsequent presses of the advance button results in reversing the direction of rotation. It will also change the colors of LED. The program will continue running in the aforementioned manner until the play button is pressed. Once the play button is pressed, the robot will stop and the ​robotest program will exit gracefully. You can also use the power button to turn off the iRobot at any time, but the program `robotest` does not detect such event and as a result it will continue running. But iRobot will not respond as the power has been turned off.

### During Your Development

* You need to use C++ for this assignment. The entire program should be a single binary. You have to use OpenCV and RaspiCam API for taking the picture within the same C++ program. You can use the provided `irobot_lab_example` to start your assignment, which also utilizes the aforementioned libraries.

* The file named ​`robotest.cc`​ contains an example program that will be helpful for this assignment. Study the code to make sure you understand it.

* The program `robotest` links with the modified version of a library named “iRobot Create Communications Library” or [`libirobot­create`]((http://www.nongnu.org/libirobot-create/doc/libirobot-create-0.1/index.html)). The library is declared in the file `irobot­create.hh`​. It uses a serial communications library named libserial to communicate to the robot using the iRobot’s [Open Interface](https://www.irobot.lv/uploaded_files/File/iRobot_Roomba_500_Open_Interface_Spec.pdf) (OI) protocol. The file ​`irobot­create.hh` along with the OI protocol specification guide will be very helpful for you to understand the iRobot’s commands, and as a documentation for “what to call” to implement a particular feature on the iRobot platform. It is important to be familiar with the ​OI protocol specification ​, as it will give you the absolute answer to many questions. `irobot­create.hh` ​just defines a wrapper to the protocol and makes it easier to program the robot. You may have noticed that ​`robotest.cc` uses different constants like `DRIVE_STRAIGHT`​, `LED_PLAY`​, `​SENSOR_WALL_SIGNAL`​, types like  ​`Create::sensorPackets_t`​, functions `Create::sendStreamCommand`, `Create::sendDriveCommand`​, or ​`Create::bumpLeft`​. You can use the header file ​`irobot­create.hh` as the documentation for such constants, functions, or data types.

* The lines ​69~­72 ​in ​`robotest.cc` takes a picture and saves it to a file.
    ```c++
    69 Camera.grab();
    70 Camera.retrieve (bgr_image);
    71 cv::cvtColor(bgr_image, rgb_image, CV_RGB2BGR);
    72 cv::imwrite( ​"irobot_image.jpg" ​, rgb_image);
    ```
* The ​Camera ​object is initialized in line 20 as `​raspicam::RaspiCam_Cv Camera;` The image matrices are initialized in lines 21 as ​`cv::Mat`, `rgb_image`, `bgr_image`; Note that ​`rgb_image`, ​`bgr_image` etc are of type `​cv::Mat` ​that are OpenCV matrices containing different channels of the image. The `​cv::` ​namespace is related to OpenCV, and the ​`raspicam:: ​namespace` is related to Raspicam. We have already installed both of these in the initial setup document.

### After You Are Done

* Make sure you commit all your changes through `git commit`. In fact we would you recommend you to commit your changes frequently to make the development process more transparent.

* You should also push you local changes to the remote from time to time by doing `git push` so that you have a remote copy of all your changes, which you can recover from in case something went wrong with your local repository or Raspberry Pi.

* When the deadline comes or if you feel that you're satsified with your code, we ask that you to tag your repo with `mp1`. You can create the tag through command line with `git tag` or you can do it in your gitlab repository through the GUI.

## Grading

* Make sure you submit all your code before the deadline, following the instruction above. You will need to schedule a demo slot with a TA. During the demo, we will your wind back to your lastest commit before the deadline and treat it as your demo content. Demo slots and further instructions will be released on Piazza when the deadline comes. Please note that you will need to pring all your equipment for you demo.

* The following grading scale table shows you want features we are looking for when you demo your code with the TA.

|    |     |    |
|:--:|:----|:---:|
| 1 | (a) You have a mobile set up of your iRobot Create and Raspberry Pi, (b) You can connect your Raspberry Pi to IllinoisNet WiFi (c) You can show your code in Raspberry Pi. | 3 |
| 2 | iRobot starts moving with the correct speed. | 1 |
| 3 | iRobot plays a periodic sound when an obstacle is nearby (according to wall sensor) and the period decreases as it is nearing the obstacle. | 1 |
| 4 | iRobot detects that it has hit an obstacle and stops moving. | 1 |
| 5 | Once stopped after hitting an obstacle, iRobot blinks the LEDs as described. | 1 |
| 6 | iRobot backs up from the “accident” scene, while blinking the LEDs. | 1 |
| 7 | After backing up, iRobot takes a picture and saves it, while blinking the LEDs. | 1 |
| 8 | After taking the picture, iRobot turns off the LEDs, rotates by a random angle, and starts moving with the initial speed (and the cycle continues) | 1 |
|   | Total | 10 |
# Machine Problem 4

## Due

* December 5 at 11:59:59 pm

## Problem Description

* In this MP, you will apply the principles learned in class to improve your navigation module. Specifically, you will use a **feedback control loop** to **follow walls without bumps**. While the earlier machine problems focused on safety and mission critical functionality and the overall architecture, this one focuses on designing and implementing the correct control loop. You may use the approach followed in earlier MPs to align the robot initially so it is parallel to the wall, as well as to recover from bumps if they occur. (Ideally, if your control works well, bumps should not occur.) Once the robot is aligned roughly parallel to the wall, you will use a new feedback module to keep following the wall.

## Control

* The general idea of feedback control is to measure a variable and manipulate another so that the measured variable reaches and is maintained around a set point. For example, a temperature control loop measures temperature and manipulates heat (emitted by a heater or air conditioner) such that temperature reaches a desired value (the set point). In this MP, your goal is to keep the robot at a roughly constant distance away from the wall. You will do so by designing a module (called the controller) with the following input and output:

    * **Input**: The measured wall sensor value
    * **Output**: Turn angle (adjustment)

* The simplest feedback controller has the following form: _Output = K * Input_, where _K_ is a constant called controller gain. The controller should be invoked periodically once every period, denoted as _T_. The controller design problem in this MP is therefore reduced to finding an appropriate value of _K_ for your chosen value of _T_ such that the robot follows the wall smoothly.

* There are tradeoffs. If the period, _T_, is too large, too much time elapses between successive angle adjustments, which makes it harder to keep the distance from the wall constant. If the period, _T_, is too small, the next angle adjustment is due before the robot had a chance to move enough to tell the effect of previous angle adjustments on distance from wall. Note that, the selection of a good period, _T_, also depends on the chosen robot speed, _v_. You should choose _T_ such that your robot covers a meaningful distance between angle adjustments (which depends on your choice of _v_). A distance in the range of 1-3 inches between angle adjustments should work well, but you are free to experiment with other parameter choices. Given a selected period, _T_, if controller gain, _K_, is too large, the robot overreacts by exaggerating the angle adjustment every time the controller is invoked. This over-reaction leads to instability; the robot deviates more and more from the desired wall-following behavior. On the other hand, if _K_ is too small, the angle adjustments are too small to be effective.

* Control theory offers a way to determine _K_, given a selected value of controller period _T_. In this MP, you are asked to use the gain and phase equation to come up with a good value of K. To do so, you will follow a process similar to what you learned in class, namely:

    * **Model the controlled process**: In this case, the controlled process is the robot. The model should describe an approximate relation between the angle adjustment done by the controller and resulting distance to the wall. Please describe this relation in terms of parameters of elements we know how to model in class; namely, as an appropriate combination of the gain, the integrator, the differentiator, the time-constant, and the delay element, as needed. To arrive at the right elements and parameter values for these elements, please use basic knowledge of geometry to inform you how distance will change when the robot turns. This is a creative exercise. You can make simplifying assumptions as needed to simplify the model.

    * **Model the sensor**: In this case, the sensor is the wall sensor. If the raw sensor is noisy, it is appropriate to do some processing on the raw value (as part of the sensor module); for example, you can read the raw value multiple times and average the results, then return the average as the sensor output. Your model of the sensor should describe the approximate relation between physical distance to the wall and sensor output. As before, this relation should be described in terms of parameters of elements we covered in class; namely, as an appropriate combination of the gain, the integrator, the differentiator, the time-constant, and the delay element. This part is done empirically. You will place the robot at different distances from the wall and record sensor output. You will then compute the desired parameter values from the resulting curve.

    * **Develop the controller**: Use the gain and phase equation to determine controller parameters.

## Robot safety

* Ensure safety of the robot at all times. You should continue to check for Cliff Signal, Overcurrent, and Wheel drop sensors, and take appropriate actions when they trigger. In case of an actual overcurrent, you must stop the motors, play a sound, and wait for an ‘advance button’ input to resume mission. In case of the robot falling off a cliff or the wheels being dropped, the robot should stop the motors, play a sound, and automatically resume once the problems are solved. Pressing the ‘play button’ anytime should always stop the robot as if the traversal is complete.

## Robot Performance

* Wooden logs placed inside the lab will be used as walls to create mazes that your robot needs to traverse. The walls are not going to be weak or fragile for mp4. The entire maze will be built with either brown or white colored logs. Different colors will not be mixed. **There will be no sharp turns in the maze**. Figure 1a, and Figure 1b shows two sample mazes to explain these points. Note that the turning angles are small.

![alt text](https://courses.engr.illinois.edu/cs424/fa2018/mp/mp4_1.png "Sample Maze")

* The robot should traverse the maze by closely following the wall. The approximate length you need to traverse will be around 25 feet. It includes 7 corners of random angles. Initially the robot will have an arbitrary direction towards a wall inside the maze. From there, it will start driving until it reaches very near to the wall, or bumps it. Use the wall sensor and the bump sensor to detect this event. After this event the robot should rotate to align its direction parallel to the wall. After this first alignment, **you must use the wall signal, and try to drive parallel to the wall edges**. There are two performance requirements:

    * **You must minimize bumps to the wall**: Every bump other than the very first bump (which happens at the beginning, when the robot has not aligned itself parallel to the wall), will take away some points. For every bump that happens in the maze, 50% of score of “Number of bumps” requirement will be deducted.
    * **The robot should not take more than 110 seconds for the traversal**: Note in mp3, this requirement was shortened. Being in the maze for more than 110s will take away some points. For every second exceeding the 110s, 10% of the score of “Time in maze” requirement is taken away. Timer starts after the first bump.

## Project Report

* The project report should contain three sections:

    * **Modeling the robot**: From the geometry of the problem, (i) derive the mathematical relation between angle adjustment and distance from wall, ~~then (ii) express that relation in terms of elements we covered in class and their parameters.~~
    * **Modeling the sensor**: From your empirical observations, (i) show a graph of the empirically-measured data points describing the relation between physical distance (plotted on the X-axis) and corresponding wall sensor readings (plotted on the Y-axis)~~, then (ii) express that relation in terms of elements we covered in class and their parameters.~~ Are there errors or bias in the sensor values? If so, how would you remove them or, at least, reduce their affect on your navigation?
    * **Designing the controller**: ~~Show the phase and gain equations that pertain to the above system, and their use to derive the controller.~~ Given the components above, how would you model the control loop? What type of controller (PID, PI, PD) you used and explain why?

## Grading

* Make sure you push all your code to the remote repository and tag you submission with `mp4` before the deadline. You will need to schedule a demo slot with a TA. Demo slots and further instructions will be released on Piazza when the deadline comes. Please note that you will need to bring all your equipment for you demo.

* The following grading scale table shows you want features we are looking for when you demo your code with the TA.

|    |     |    |
|:--:|:----|:---:|
|  | Requirement | Points |
| 1 | Design Report |  |
| 1a | Modeling the Robot | 2 |
| 1b | Profiling the Sensor | 2 |
| 1c | Controller Design (from Phase and Gain Equations) | 2 |
| | Report Total | 6 |
| 2 | Implemetation | |
| 2a | Time in Maze | 2 |
| 2b | Number of Bumps | 2 |
| | Implemetation Total | 4 |
| | Total | 10 |
# Machine Problem 3

## Due

* Nov 19th, 11:59:59 PM

## Problem Description

* In this MP, you will apply the principles learned in class to improve your design of the robot software (your C++ program) developed for MP2. While MP2 focused primarily on functionality needed for the robot mission (such as navigation, safety, and computer vision tasks), MP3 will focus on the overall software architecture.

* We recall the mission description from MP2, which described a robotic mission to explore a newly discovered ancient underground structure (the maze). The description said: “A research team is assembled. They send a robot into the maze. The robot must map the maze while taking pictures of all encountered objects. The maze may include cliffs and traps. The robot must protect itself while in the maze. Scientists are excited and have software (payload) they want to download to the robot. This payload will execute analytics on data received. It may be buggy, but it needs to be executed in the maze because limited bandwidth prevents the robot from reporting all data outside. The robot should complete the mission as quickly as possible”.

* To develop a good software architecture for this mission, we must first distill the **safety-critical**, **mission-critical**, and **performance/value-added requirements**. Recall that **safety-critical** requirements are those that relate to basic survival/safety. Violating them can cause serious harm. They must therefore be implemented reliably and take priority over everything else. Problems with other parts of the system should not impact functionality of safety-critical components. **Mission-critical** requirements are those that must be met for the mission to succeed. They define the purpose of the mission. Violating them may cause the mission to fail (in some aspect), but the system itself will remain unharmed. Finally, **performance requirements** specify quality attributes that describe how well the mission is performed.We now list the **safety-critical**, **mission-critical**, and **performance requirements** for the maze exploration mission:

    * **Safety-critical requirements**: Protect the robot from harm. The robot can be harmed by collisions with walls or by dropping into a hole. Also, since the maze can be dangerous, we require in MP3 that the robot spend no more than two minutes in the maze. If the robot is in the maze for more than two minutes, a safety violation will occur. Please note the following threat model:

        * *Wall collisions*: In MP3, lighter walls will be set up to mimic structurally weak ancient caves. A hard collision with a wall may topple the wall on top of the robot and physically break it - do not let that happen. Toppling a wall would be a safety violation that kills your robot (you will also be responsible for replacing any physically broken components so please test with care and be ready to catch any falling objects so they do not harm your robot). Bump sensors on the robot warn of impending collisions. When a bump sensor touches an obstacle and!is depressed, you have only a small fraction of a second (depending on robot speed) before the bulk of the body of the robot hits the obstacle. You will need to bring the robot to a full stop before its core hits the wall and potentially topples it causing a safety violation. Therefore, it is for your best interest to figure out the appropriate speed and increase responsiveness in case of bump. Also, it is possible for walls to have different colors (white or brown). You program should be robust enough to handle such situation.
        * *Cliffs*: In addition, TA might lift the robot to simulate cliffs. The cliff and wheel-drop sensors warn about approaching drop-offs. Should the robot fail to stop immediately, it can be damaged too, so it is a safety violation. In case such situation happens, the robot should stop its motors, and play a song while any such unexpected event happens. It should resume operation, when the situation is fixed (manually by pressing the Advance button).
        * *Timing constraint*: To guard against other unknown threats, as mentioned above, we limit the mission time to 120s. Failure to meet this timing constraint is a safety violation. If you are getting close to this deadline, the robot might want to reprioritize things accordingly so that it is able to get out on time. You can also choose to abort. (See [Safe Mission Abort](#Safe Mission Abort))!
  
    * **Mission-critical requirements**: The mission is to do three things:

        * *Map the maze*: The robot must generate an accurate map of the layout of the maze. In MP3, the corners are right angles; the wall segments will be straight (no curved walls). However, it should be noted that in this MP, we ask you to tune parameters such that the robot will following the wall closer with a gap no larger than 3 inches between robot and the wall.
        * *Identify encountered objects*: The robot must document (i.e., record and identify) all encountered objects. The list of encountered objects will be needed by the scientists to plan further excavation missions. Failures to document an object (a false-negative) and object misclassifications will be considered as violations of this mission requirement.
        * *Disarm Aladdin’s Lamp*: In MP3, your mission will also include disarming Aladdin’s Lamp, if it is found. This Lamp is a dangerous supernatural device in the maze. See Figure 1 below.

            ![alt text](https://courses.engr.illinois.edu/cs424/fa2018/mp/mp3_1.jpg "Aladdin's Lamp")

            Figure 1: Aladdin’s Lamp

            To disarm the Lamp, the robot must activate the Proton Transfunctioner： a weapon that it carries on board. To be effective, it has to be activated when the robot is near the Lamp. (Since the real robot does not actually carry weapons, you will emulate weapon activation by turning on the red light on for 2 seconds when it sees the Lamp.) The transfunctioner is the only weapon that can be used to disarm the Lamp, but it has a single charge, so if you use it at the wrong time (i.e., turn the red light on when the Lamp is not in view), you will no longer be able to disarm the Lamp and will fail to satisfy this mission requirement.
        * *Run scientists’ payload*: The scientists need to run some proprietary code/external task during the mission, while the robot is in the maze. The task is provided below:
            ``` C++
            int main(int argc, char const *argv[]) {
                int array1[10000];
                int array2[20000];
                unsigned long long counter = 0;
                for (int aid = 0; aid < 3; aid++) {
                    fork();
                }
                pid_t pid = getpid();
                pid_t ppid = getppid();
                while (1) {
                    for (int j = 0; j < 100000; j++) {
                        for(int k = 0; k < 80; k++) {
                            array1[j % 10000 ] = array2[j % 10000] * 20- array2[j % 100];
                            array2[(j + 5) % 10000] = array1[j % 10000] * 20.03 -
                            array2[(j + 100) % 1000];
                            array1[k % 10000] = array2[(k+j * 10) % 10000] * 50 -
                            array1[ (k*10 + 9) % 1000];
                        }
                    }
                    for (int i = 0; i < 10; i++) {
                        array1[i] = 6 * i + i * i - i % 105;
                        array2[i] = array1[i] - array1[(i+60) % 1000] * 60;
                    }
                    usleep(1 * 1000);
                    counter++;
                    cout << "pid:" << pid << " ppid:" << ppid << " counter:" <<
                    counter << endl;
                }
                return 0;
            }
            ```
            The scientists want to make sure that the task will finish with at least 25% (`couter` value of at least 25) when the robot is outside the maze. Please make sure that you're not running the maze too fast, otherwise you will not have enough time to detect objects within the maze nor finish the external task. Also note that we will run it similarly as we did in MP2 through `nice -n 19 ./external`.
  
    * **Performance requirements**: The following dimensions of performance will be used to evaluate how well the mission is done:

        * *Time to traverse maze*: How long did the robot spend in the maze. The shorter the better. Note that, if you spend more than two minutes in the maze, it will be a safety violation.
        * *Time to finish entire mission*: In some designs, part of the needed processing can be postponed until after the robot gets out of the maze (and hence, such processing is not subject to the two minute deadline). The mission is done when all processing is finished. The sooner it is finished the better.!
  
## Safe Mission Abort

* While we encourage you to do every possible effort to finish the mission successfully on time by traversing the entire maze in less than two minutes (and meet all requirements), if all else fails and you are running out of time (i.e., approaching the end of the two-minute constraint), you can engage the safe mission abort procedure. For purposes of this MP, safe mission abort is to stop the robot and turn the red LED light on as a distress signal. If you execute safe abort before the two-minute deadline expires, you will be considered to have met the safety-critical time constraint. You will not be allowed to continue navigation after aborting. However, you will be allowed to finish any pending computation. Once you abort safely, you will be allowed to either count this mission (for grading purposes) or restart it without penalty (i.e., forget it happened and start over). You can choose to abort a mission for other reasons as well (e.g., it is not going well). The only condition is that it needs to be done in software (by stopping the robot and turning the red LED) and not by you pressing any button. You are allowed at most two aborts (3rd trial will have to be used for grading). Note that, you cannot abort a mission after a safety violation has occurred. If the robot didn’t abort itself, you cannot ask for a re-run.

## System Architecture

* You must use multiple threads, and assign appropriate priorities to them. You should use an appropriate architecture of dependencies that allows the mission to complete while ensuring safety, mission, and performance requirements.

## Project Report

* Update your project report from MP2 to reflect the latest architecture with figures. The updated report should contain a diagram showing all components of the system. Please use different colors for components as follows: (i) safety-critical in red, (ii) mission-critical in blue, and (iii) performance-related in black. If a component serves more than one function (e.g., it is needed for both safety-critical and mission-critical functions), choose the color that reflects the higher criticality. Remember that safety is more critical than mission, and mission is more critical than performance. The diagram should also show dependencies among your components. A red solid arrow from component A to component B should mean that B depends on A (i.e., failure in A will result in failure in B). A dashed black arrow from A to B means that B uses results of A. Explain in a table, which thread/component relates to which requirement, and explain their priority levels. The table should have the following three columns: (i) requirement (for example, “Disarm Aladdin’s Lamp”), (ii) component name and function (for example, “The Weapon Release Thread: It is invoked when the Lamp is detected and it turns the red LED on”), and (iii) criticality level (which is one of “Safety-critical”, “Mission-critical”, or “Performance/Value-added”). The report should also include the algorithms used for navigating the robot. Explain the major issues encountered while solving the problem, and how you solved those. Report the maximum speed you could run your robot during your tests. Include three different examples of contours plotted using your program, the locations where you tested it, and the time it required for that experiment. Note that you are allowed to reuse any parts of the report from MP2 if those parts have not changed (e.g., algorithms that were not modified), and of course you should update any parts that changed. The report must be in pdf format.

## Grading

* Make sure you submit all your code and tag them with `mp3` before the deadline, You will need to schedule a demo slot with a TA. During the demo, we will your wind back to your lastest commit before the deadline and treat it as your demo content. Demo slots and further instructions will be released on Piazza when the deadline comes. Please note that you will need to bring all your equipment for you demo.

* You will be graded on the design plus the execution of the mission. The execution grade will depend on how well you met safety, mission, and performance requirements. To emphasize the point that safety violations are really critical, safety will be viewed as a multiplicative factor, multiplied by the mission execution grade. More specifically, in MP3, your robot is given 3 “lives”. Each safety violation takes one of your lives, voids and restarts the mission, and reduces the multiplicative factor by 1/3 (i.e., if your robot dies three times, you get zero on mission execution). Missions you aborted safely and chose to restart are not counted (it will not reduce the multiplicative factor). The following table shows the complete distribution of points.

|    |     |    |
|:--:|:----|:---:|
|  | Requirement Points | (F: factor) |
| 1 | Robot Safety | F = 1, 0.67, 0.33, or 0 |
| 1a | (a) Mapped countor reasonably represents the maze (b) maintain robot-wall distance | 1.5 |
| 1b | Identified all objects in the maze | 2 |
| 1c | Disarmed lamp | 1 |
| 1d | Execution of Scientists Payload | 1 |
| 1e | Travel through 7 walls within 120 seconds | 1.5 |
| 1f | Total mission time (including outsize maze processing) within 500s | 1.5 |
| |Total (Execution) | 75% * F |
| 2 | Design Report | 1.5 |
| | Total | 10 |
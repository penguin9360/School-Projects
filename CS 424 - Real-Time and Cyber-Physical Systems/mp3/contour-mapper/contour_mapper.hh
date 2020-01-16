#include <iostream>
#include <opencv2/opencv.hpp>

#include "../irobot/irobot-create.hh"
#include "../navigation-manager/navigation_manager.hh"
#include "../utility/blocking_queue.hh"

using namespace cv;
using namespace std;

/*
 * usage:
 *   Provide ContourMapper a BlockingQueue object, and pass it directives. A
 * directive constitutes an update to the robot (change in speed, direction,
 * turning, etc.) and each thread that performs such an update to the robot is
 * responsible for pushing a directive to the BlockingQueue; the parameters of a
 * directive are determined via shared variables between the threads (e.g. time
 * of last update, speed since last update).
 *
 *   Each directive takes the form std::pair<int, Direction>, where,
 *   	int == distance traveled (in mm) ~ speed * (current time - time of last
 * update) since last directive float == direction the robot is turning after
 * this directive in *radians*
 *
 *   Calling update_contour() will process all directives in the queue, and
 * generate positional waypoints; call this incrementally as needed.
 *
 *   Calling draw_contour() will take process all generated positional
 * waypoints, create a contour, and save it to disk.
 *   **Internally calls update_contour() to ensure up-to-date image. Call
 * sparesely.
 *
 */

class ContourMapper {
public:
  ContourMapper(BlockingQueue<std::pair<int, float>> *queue);

  // Processes the update directives in the queue
  void update_contour();

  // Draws the contour using the currently processed waypoints, and writes it
  // out to an image file
  void draw_contour();

private:
  // Shared queue that contains pos/dir update directives
  BlockingQueue<std::pair<int, float>> *queue_;

  std::vector<cv::Point2f> waypoints_; // Recorded list of waypoints
  cv::Point2f pos_;                    // Current position of the robot
  cv::Point2f dir_; // Current direction (unit vector) of the robot

  int max_abs_dim_;

  // Applies a rotation degree (in radians) to the current dir_, normalizes and
  // returns the result
  Point2f apply_rotation_(float rotate_deg);
};

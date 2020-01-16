#include "../contour-mapper/contour_mapper.hh"
#include "../navigation-manager/navigation_manager.hh"
#include "../utility/blocking_queue.hh"

#include <stdio.h>

using namespace cv;
using namespace std;

typedef std::pair<int, float> Directive;

#define PI 3.14159265F

int main() {
  BlockingQueue<std::pair<int, float>> queue;
  ContourMapper mapper(&queue);

  // Push distance traveledi (in mm), direction of turn made (if any) at the end of that distance
  std::cout << "Pushed directive #1\n";
  queue.push(Directive(2500, PI * 90.0F / 180.0F));

  std::cout << "Pushed directive #2\n";
  queue.push(Directive(100, 0.0F));

  std::cout << "Pushed directive #3\n";
  queue.push(Directive(150, PI * 90.0F / 180.0F));

  std::cout << "Pushed directive #4\n";
  queue.push(Directive(250, PI * 90.0F / 180.0F));

  std::cout << "Pushed directive #5\n";
  queue.push(Directive(125, PI * -90.0F / 180.0F));

  std::cout << "Pushed directive #6\n";
  queue.push(Directive(125, 0.0F));

  // Process the directives, then draw & save to disk
  std::cout << "Updating contour...\n";
  mapper.update_contour();
  std::cout << "Building image...\n";
  mapper.draw_contour();
  std::cout << "Done!\n";

  return 0;
}

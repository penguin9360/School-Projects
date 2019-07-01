#include "cs225/PNG.h"
#include "FloodFilledImage.h"
#include "Animation.h"

#include "imageTraversal/DFS.h"
#include "imageTraversal/BFS.h"

#include "colorPicker/RainbowColorPicker.h"
#include "colorPicker/GradientColorPicker.h"
#include "colorPicker/GridColorPicker.h"
#include "colorPicker/SolidColorPicker.h"
#include "colorPicker/MyColorPicker.h"

using namespace cs225;

int main() {

  // @todo [Part 3]
  // - The code below assumes you have an Animation called `animation`
  // - The code provided below produces the `myFloodFill.png` file you must
  //   submit Part 3 of this assignment -- uncomment it when you're ready.

  PNG png;
  png.readFromFile("lmao.png");

  FloodFilledImage image(png);
  BFS bfs1(png, Point(30, 40), 0.5);
  BFS bfs2(png, Point(74, 85), 0.5);
  BFS bfs3(png, Point(87, 86), 0.5);
  HSLAPixel black(0, 0, 0, 1);
  SolidColorPicker picker(black);
  // MyColorPicker blue(250);



  image.addFloodFill(bfs1, picker);
  Animation animation1 = image.animate(10);

  image.addFloodFill(bfs2, picker);
  Animation animation2 = image.animate(10);

  image.addFloodFill(bfs3, picker);
  Animation animation3 = image.animate(10);

  // image.addFloodFill(dfs2, blue);
  // Animation animation2 = image.animate(10);

  Animation animation;

  for (unsigned i = 0; i < animation2.frameCount(); ++i)
  {
    // animation.addFrame(animation1.getFrame(i));
    animation1.addFrame(animation2.getFrame(i));
    animation1.addFrame(animation2.getFrame(i));
  }

  PNG lastFrame = animation1.getFrame( animation1.frameCount() - 1 );
  lastFrame.writeToFile("myFloodFill.png");
  animation1.write("myFloodFill.gif");



  return 0;
}

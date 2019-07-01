#include <iostream>
#include "dsets.h"
#include "maze.h"
#include "cs225/PNG.h"

using namespace std;
using namespace cs225;

int main()
{
    // Write your own main here

  SquareMaze newMaze;
  PNG * final = newMaze.creative();
  final->writeToFile("creative.png");
  return 0;
}

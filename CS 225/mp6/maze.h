#ifndef MAZE_H_
#define MAZE_H_
#include <vector>
#include "cs225/PNG.h"
#include "cs225/HSLAPixel.h"
#include "dsets.h"
#include <math.h>

using namespace std;
using namespace cs225;

class SquareMaze{
	public:
		SquareMaze();
    ~SquareMaze();
		void makeMaze(int width, int height);
		bool canTravel(int x, int y, int dir) const;
		void setWall(int x, int y, int dir, bool exists);
		vector<int> solveMaze();
		PNG * drawMaze() const;
		PNG * drawMazeWithSolution();

    PNG * creative();

	private:
		int MazeWidth = 0;
		int MazeHeight = 0;
		DisjointSets dset;
		vector<int> maze;
};
#endif

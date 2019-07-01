#include "maze.h"
#include <vector>
#include "cs225/HSLAPixel.h"
#include <math.h>
#include <queue>
#include <map>
#include "maze.h"
#include "dsets.h"
#include "cs225/PNG.h"

using namespace std;
using namespace cs225;

SquareMaze::SquareMaze() {
}

SquareMaze::~SquareMaze() {
}


void SquareMaze::makeMaze(int width, int height) {
  int size = width * height;
	maze.clear();
	maze.resize(size);
	MazeWidth = width;
	MazeHeight = height;
	for(int i = 0; i < size; i++) {
		maze[i] = 3;
	}

	dset.addelements(size);

	while(dset.size(0) < size) {

		int size_rand = rand() % size;
		int rand_num = rand() % 2;

		if((size_rand + 1) % width == 0) {
			rand_num = width;
		}
		if(size_rand >= width * (height  -  1)) {
			rand_num = 1;
		}
    if(rand_num == 0) {
      rand_num = width;
    }
		if(size_rand == size - 1) {
			continue;
    }

		if(dset.find(size_rand) != dset.find(size_rand + rand_num)) {
			dset.setunion(dset.find(size_rand), dset.find(size_rand + rand_num));
			if(rand_num == 1) {
				setWall(size_rand % width, size_rand / width, 0, false);
			} else {
				setWall(size_rand % width, size_rand / width, 1, false);
			}
		}
	}
}

bool SquareMaze::canTravel(int x, int y, int dir) const {
	if (x >= 0 and x < MazeWidth and y >= 0 and y < MazeHeight) {
		int direction = maze[y * MazeWidth + x];
		if (dir == 0 and x + 1 < MazeWidth) {
			if (direction == 0 or direction == 2) {
        return true;
      }
		} else if (dir == 1 and y + 1 < MazeHeight) {
			if (direction == 0 or direction == 1) {
        return true;
      }
		} else if (dir == 2 and x - 1 >= 0) {
			if (maze[(y * MazeWidth) + x - 1] == 0 or maze[(y * MazeWidth) + x - 1] == 2) {
        return true;
      }
		} else if (dir == 3 and y - 1 >= 0) {
			if (maze[(y - 1) * MazeWidth + x] == 0 or maze[(y - 1) * MazeWidth + x] == 1) {
        return true;
      }
		}
	}
	return false;
}

void SquareMaze::setWall(int x, int y, int dir, bool exists) {
  int direction = maze[y * MazeWidth + x];
  if(exists) {
    if(dir == 0) {
      if(direction == 0 or direction == 1) {
        maze[y * MazeWidth + x] = 1;
        return;
      } else {
        maze[y * MazeWidth + x] = 3;
        return;
      }
    } else {
      if(direction == 0 or direction == 2) {
        maze[y * MazeWidth + x] = 2;
        return;
      } else {
        maze[y * MazeWidth + x] = 3;
        return;
      }
    }
  } else {
    if(dir == 0) {
      if(direction == 3) {
        maze[y * MazeWidth + x] = 2;
        return;
      } else if (direction == 1) {
        maze[y * MazeWidth + x] = 0;
        return;
      }
    } else {
      if(direction == 3) {
        maze[y * MazeWidth + x] = 1;
        return;
      } else if (direction == 2) {
        maze[y * MazeWidth + x] = 0;
        return;
      }
    }
  }
}

vector<int> SquareMaze::solveMaze() {

  queue<int> q;
  q.push(0);
  map<int, int> map_tmp;
  map_tmp.insert(pair<int,int>(0, 0));
  map<int, int> parent;
  vector<int> path;

  while (!q.empty()) {
      int temp = q.front();
      q.pop();
      int x = temp % MazeWidth;
      int y = temp / MazeWidth;
      for (int i = 0; i < 4; i++) {
      	int parent_idx;
      	if (i == 0) {
          parent_idx = (y * MazeWidth) + (x + 1);
        }
      	if (i == 1) {
          parent_idx = ((y + 1) * MazeWidth) + x;
        }
      	if (i == 2) {
          parent_idx = (y * MazeWidth) + (x - 1);
        }
      	if (i == 3) {
          parent_idx = ((y - 1) * MazeWidth) + x;
        }
      	if (canTravel(x, y, i) and map_tmp.find(parent_idx) == map_tmp.end()) {
      		q.push(parent_idx);
      		map_tmp.insert(pair<int, int>(parent_idx, map_tmp[temp] + 1));
      		parent[parent_idx] = temp;
      	}
      }
	}

  int dis = 0;
	unsigned max = 0;
	for(int i = 0; i < MazeWidth ; i++) {
		int wtf = ((MazeHeight - 1) * MazeWidth) + i;
		vector<int> new_dir;
		while(wtf != 0) {
			int dir_diff = wtf - parent[wtf];
			if(dir_diff == 1) {
				new_dir.push_back(0);
			} else if(dir_diff == -1) {
				new_dir.push_back(2);
			} else if(dir_diff == MazeWidth) {
				new_dir.push_back(1);
			} else if(dir_diff == -MazeWidth) {
				new_dir.push_back(3);
			}
			wtf = parent[wtf];
		}
		if(max < new_dir.size()) {
			max = new_dir.size();
			path = new_dir;
			dis = wtf;
		}
	}
  reverse(path.begin(), path.end());
  return path;
}

PNG * SquareMaze::drawMaze() const {
  PNG * png = new PNG(MazeWidth * 10 + 1, MazeHeight * 10 + 1);
  for(int i = 10; i < MazeWidth * 10 + 1; i++) {
    HSLAPixel & ret = png->getPixel(i, 0);
    ret.l = 0;
  }
  for(int i = 0; i < MazeHeight * 10 + 1; i++) {
    HSLAPixel & ret = png->getPixel(0, i);
    ret.l = 0;
  }
  for(int i = 0; i < MazeWidth; i++) {
    for(int j = 0; j < MazeHeight; j++) {
      int direction = maze[(j * MazeWidth) + i];
      if(direction == 3 or direction == 1) {
        for(int k = 0; k <= 10; k++) {
          HSLAPixel & ret = png->getPixel((i + 1) * 10, j * 10 + k);
          ret.l = 0;
        }
      }
      if(direction == 3 or direction == 2) {
        for(int k = 0; k <= 10; k++) {
          HSLAPixel& ret = png->getPixel(i * 10 + k, (j + 1) * 10);
          ret.l = 0;
        }
      }
    }
  }
  return png;
}

PNG * SquareMaze::drawMazeWithSolution() {
	PNG * ret = drawMaze();
	vector<int> path = solveMaze();
	int x = 5;
  int y = 5;
	for (int i : path) {
      for (int ct = 1; ct < 11; ct++) {
			HSLAPixel & pixel = ret->getPixel(x,y);
			pixel.h = 0;
			pixel.s = 1;
			pixel.l = 0.5;
			pixel.a = 1;

			if (i == 0) { x++;}
			if (i == 1) {	y++;}
			if (i == 2) {	x--;}
			if (i == 3) {	y--;}
		}
		HSLAPixel & pixel = ret->getPixel(x,y);
		pixel.h = 0;
		pixel.s = 1;
		pixel.l = 0.5;
		pixel.a = 1;
	}
	for(int i = 1; i < 10; i++) {
		HSLAPixel & pixel = ret->getPixel(x + i - 5, y + 5);
		pixel.l = 1;
	}
  return ret;
}

PNG * SquareMaze::creative() {
  maze.resize(16);
	MazeWidth = 4;
	MazeHeight = 4;
	maze[0] = 0;
	maze[1] = 0;
	maze[2] = 1;
	maze[3] = 1;
	maze[4] = 0;
	maze[5] = 0;
	maze[6] = 2;
	maze[7] = 1;
	maze[8] = 2;
	maze[9] = 1;
	maze[10] = 0;
	maze[11] = 0;
	maze[12] = 1;
	maze[13] = 2;
	maze[14] = 0;
	maze[15] = 3;

	PNG * creative = new PNG(MazeWidth * 10 + 1, MazeHeight * 10 + 1);
	for(int i = 0; i < MazeWidth * 10 + 1; i++) {
    for(int j = 0; j < MazeHeight * 10 + 1; j++) {
			HSLAPixel & ret = creative->getPixel(i,j);
			if(ret.l != 0)
				ret.h = (i*j - 30 ) % 360;
				ret.s = 1;
				ret.l = 0.2;
				ret.a = 1;
			}
		}
		for(int i = 10; i < MazeWidth * 10 + 1; i++) {
	    HSLAPixel & ret = creative->getPixel(i, 0);
	    ret.l = 0;
	  }
	  for(int i = 0; i < MazeHeight * 10 + 1; i++) {
	    HSLAPixel & ret = creative->getPixel(0, i);
	    ret.l = 0;
	  }

	  for(int i = 0; i < MazeWidth; i++) {
	    for(int j = 0; j < MazeHeight; j++) {
	      int direction = maze[(j*MazeWidth) + i];
	      if(direction == 3 or direction == 1) {
	        for(int k = 0; k <= 10; k++) {
	          HSLAPixel& ret = creative->getPixel((i+1)*10, j*10+k);
	          ret.l = 0;
	        }
	      }

	      if(direction == 3 or direction == 2) {
	        for(int k = 0; k <= 10; k++) {
	          HSLAPixel& ret = creative->getPixel(i*10+k, (j+1)*10);
	          ret.l = 0;
	        }
	      }
	    }
	  }


	vector<int> path;
	int x = 5;
	int y = 5;
	path = solveMaze();
	for (int gg : path) {
			for (int ct = 1; ct < 11; ++ct) {//color 11 pixels in a line
			HSLAPixel & ret = creative->getPixel(x,y);
			ret.h = 0;
			ret.s = 1;
			ret.l = .7;
			ret.a = 1;

			if (gg == 0) {	x++;}
			if (gg == 1) {	y++;}
			if (gg == 2) {	x--;}
			if (gg == 3) {	y--;}
		}
		HSLAPixel & ret = creative->getPixel(x,y);
		ret.h = 0;
		ret.s = 1;
		ret.l = 0.3;
		ret.a = 1;
	}
	for(int i =1; i < 10; i++) {
		HSLAPixel& ret = creative->getPixel(x+i - 5, y+5);
		ret.l = 1;
	}
	return creative;
}

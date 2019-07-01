#include <algorithm>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "transpose.h"

// will be useful
// remember that you shouldn't go over SIZE
using std::min;

// modify this function to add tiling
void
transpose_tiled(int **src, int **dest) {
  for (int ct = 0; ct < SIZE; ct += 32) {
    for (int i = 0; i < SIZE; i++) {
        for (int j = ct; j < ((ct + 32) < SIZE ? (ct + 32) : SIZE); j ++) {
            dest[i][j] = src[j][i];
        }
    }
  }
}

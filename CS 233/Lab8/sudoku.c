#include <stdio.h>
#include <assert.h>
#include "boards.h"

const int ALL_VALUES = (1 << GRID_SQUARED) - 1;

const char symbollist[] = "0123456789ABCDEFG";

bool has_single_bit_set(unsigned value) {  // returns 1 if a single bit is set
  if (value == 0) {  
    return 0;   // has no bits set
  }
  if (value & (value - 1)) {
    return 0;   // has more than one bit set
  }
  return 1;
}

unsigned get_lowest_set_bit(unsigned value) {
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    if (value & (1 << i)) {
      return i;
    }
  }
  return 0;
}

void
init_board(const char *board_init[GRID_SQUARED], unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
      char c = board_init[i][j];
      if (c == '*') {
        board[i][j] = ALL_VALUES;
      } else {
        if ((c >= '0') && (c <= '9')) {
          board[i][j] = 1 << (c - '0' - 1);
        } else {
          board[i][j] = 1 << (c - 'A' + 9);
        }
      }
    }
  }
}

bool
board_done(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
      if (!has_single_bit_set(board[i][j])) {
        return false;
      }
    }
  }
  return true;
}

void
print_board(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    // if ((i % GRIDSIZE) == 0) { printf("\n"); }     // un-comment these two lines
    for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
      // if ((j % GRIDSIZE) == 0) { printf(" "); }    // to put blank lines between each sub-square
      unsigned value = board[i][j];
      char c = '*';
      if (has_single_bit_set(value)) {
        unsigned num = get_lowest_set_bit(value) + 1;
        c = symbollist[num];
      }
      printf("%c", c);
    }
    printf("\n");
  }
  printf("\n");
}

void
print_board_mips(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    printf(".half ");
    for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
      printf(" %d", board[i][j]);
    }
    printf("\n");
  }
  printf("\n");
}

void
print_board_verbose(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
      unsigned value = board[i][j];
      char c = ' ';
      if (has_single_bit_set(value)) {
        c = '*';
      }
      for (int k = 0 ; k < GRID_SQUARED ; ++ k) {
        char cc = (value & (1<<k)) ? ('1'+k) : c;
        printf("%c", cc);
      }
      printf("%c", (j == (GRID_SQUARED-1)) ? '\n' : '|');
    }
  }
}

int get_square_begin(int index) {
  return (index/GRIDSIZE) * GRIDSIZE;
}

bool
rule1(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  bool changed = false;
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
      unsigned value = board[i][j];
      if (has_single_bit_set(value)) {
        for (int k = 0 ; k < GRID_SQUARED ; ++ k) {
          // eliminate from row
          if (k != j) {
            if (board[i][k] & value) {
              board[i][k] &= ~value;
              changed = true;
            }
          }
          // eliminate from column
          if (k != i) {
            if (board[k][j] & value) {
              board[k][j] &= ~value;
              changed = true;
            }
          }
        }

        // elimnate from square
        int ii = get_square_begin(i);
        int jj = get_square_begin(j);
        for (int k = ii ; k < ii + GRIDSIZE ; ++ k) {
          for (int l = jj ; l < jj + GRIDSIZE ; ++ l) {
            if ((k == i) && (l == j)) {
              continue;
            }
            if (board[k][l] & value) {
              board[k][l] &= ~value;
              changed = true;
            }
          }
        }
      }
    }
  }
  return changed;
}

bool
rule2(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  bool changed = false;
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
      unsigned value = board[i][j];
      if (has_single_bit_set(value)) {
        continue;
      }
      
      int jsum = 0, isum = 0;
      for (int k = 0 ; k < GRID_SQUARED ; ++ k) {
        if (k != j) {
          jsum |= board[i][k];        // summarize row
        }
        if (k != i) {
          isum |= board[k][j];         // summarize column
        }
      }
      if (ALL_VALUES != jsum) {
        board[i][j] = ALL_VALUES & ~jsum;
        changed = true;
        continue;
      } else if (ALL_VALUES != isum) {
        board[i][j] = ALL_VALUES & ~isum;
        changed = true;
        continue;
      }

      // eliminate from square
      int ii = get_square_begin(i);
      int jj = get_square_begin(j);
      unsigned sum = 0;
      for (int k = ii ; k < ii + GRIDSIZE ; ++ k) {
        for (int l = jj ; l < jj + GRIDSIZE ; ++ l) {
          if ((k == i) && (l == j)) {
            continue;
          }
          sum |= board[k][l];
        }
      }

      if (ALL_VALUES != sum) {
        board[i][j] = ALL_VALUES & ~sum;
        changed = true;
      } 
    }
  }
  return changed;
}

bool
board_failed(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  for (int i = 0 ; i < GRID_SQUARED ; ++ i) {
    for (int j = 0 ; j < GRID_SQUARED ; ++ j) {
      if (board[i][j] == 0) {
        return true;
      }
    }
  }
  return false;
}

bool 
solve(unsigned short board[GRID_SQUARED][GRID_SQUARED]) {
  bool changed;
  do {
    changed = rule1(board);
    changed |= rule2(board);
  } while (changed);

  if (board_done(board)) {
    printf("SUCCESS!\n");
    print_board(board);
  } else {
    printf("FAILED!\n");
    print_board_verbose(board);
  }
  printf("\n");
}

int
main() {
  unsigned short my_board[GRID_SQUARED][GRID_SQUARED];

//   init_board(test_board4_init, my_board);
//   print_board_mips(my_board);
//   exit(0);

  printf("EASY BOARD:\n");
  init_board(easy_board_init, my_board);
  bool solved = solve(my_board);

  if (solved) {    // only do hard board if easy board worked
    printf("HARD BOARD:\n");
    init_board(hard_board_init, my_board);
    solve(my_board);
  }
  return 0;
}

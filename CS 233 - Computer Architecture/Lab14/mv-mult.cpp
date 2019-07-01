#include "mv-mult.h"
#include <xmmintrin.h>

// Matrix-Vector multiplication
// mat is a SIZE by SIZE matrix, that is arranged in row-column, format,
// That is, you first select a particular row, and then a particular column.
// Each row is laid out as a one-dimensional, array, so if you wanted
// to select a particular row, you would use mat[row].  You can
// also select smaller intervals, by using &mat[row][col].
// The vector is also laid out as a one-dimensional arrow, similar to a row.
// M-V multiplication proceeds by taking the dot product of a matrix row
// with the vector, and doing this for each row in the matrix

// vectorize the below code using SIMD intrinsics
float *
mv_mult_vector(float mat[SIZE][SIZE], float vec[SIZE]) {
  static float ret[SIZE];

  __m128 temp1, row, col;
  float inner_product, temp[4];
  int i = 0;
  int j = 0;

  for (; i < SIZE; i++) {
    temp1 = _mm_set1_ps(0.0);
    for (j = 0; j < SIZE - 3; j += 4){
      col = _mm_loadu_ps(& vec[j]);
      row = _mm_loadu_ps(& mat[i][j]);
      temp1 = _mm_add_ps(temp1, _mm_mul_ps(row, col));
    }
    _mm_storeu_ps(temp, temp1);
    inner_product = temp[0] + temp[1] + temp[2] + temp[3];
    for (; j < SIZE; j++) {
      inner_product += mat[i][j] * vec[j];
    }
    ret[i] = inner_product;
  }
  return ret;
}

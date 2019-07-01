#include "mandelbrot.h"
#include <xmmintrin.h>

// cubic_mandelbrot() takes an array of SIZE (x,y) coordinates --- these are
// actually complex numbers x + yi, but we can view them as points on a plane.
// It then executes 200 iterations of f, using the <x,y> point, and checks
// the magnitude of the result; if the magnitude is over 2.0, it assumes
// that the function will diverge to infinity.

// vectorize the code below using SIMD intrinsics
int *
cubic_mandelbrot_vector(float x[SIZE], float y[SIZE]) {

    float temp_vec[4];
    __m128 mm_x1, mm_y1, x1_squared, y1_squared, mm_x2, mm_y2, mm_xi, mm_yi;
    __m128 mm_set_3 = _mm_set1_ps(3.0);
    __m128 max_magnitude = _mm_set1_ps(M_MAG*M_MAG);

    static int vec[SIZE];

    for (int i = 0; i < SIZE; i += 4) {
        mm_x1 = mm_y1 = _mm_set1_ps(0.0);
        mm_xi = _mm_loadu_ps(&x[i]);
        mm_yi = _mm_loadu_ps(&y[i]);

        for (int j = 0; j < M_ITER; j ++) {
            x1_squared = _mm_mul_ps(mm_x1, mm_x1);
            y1_squared = _mm_mul_ps(mm_y1, mm_y1);

            mm_x2 = _mm_add_ps(mm_xi, _mm_mul_ps(mm_x1, _mm_sub_ps(x1_squared, _mm_mul_ps(mm_set_3, y1_squared))));
            mm_y2 = _mm_add_ps(mm_yi, _mm_mul_ps(mm_y1, _mm_sub_ps(_mm_mul_ps(mm_set_3, x1_squared), y1_squared)));
            mm_x1 = mm_x2;
            mm_y1 = mm_y2;
        }
        _mm_storeu_ps(temp_vec, _mm_cmplt_ps(_mm_add_ps(_mm_mul_ps(mm_x2, mm_x2), _mm_mul_ps(mm_y2, mm_y2)), max_magnitude));
        vec[i] = temp_vec[0] == 0 ? 0 : 1;
        vec[i + 3] = temp_vec[3] == 0 ? 0 : 1;
        vec[i + 2] = temp_vec[2] == 0 ? 0 : 1;
        vec[i + 1] = temp_vec[1] == 0 ? 0 : 1;
    }
    return vec;
}

#include "mv-mult.h"
#include <math.h> // useful for result comparison
#include <stdio.h>
#include <sys/time.h>

// this scary-looking thing is a function pointer -
// the C equivalent of C ++ function objects (functors).
// Google it if you're interested
typedef float *(*mv_mult_fn)(float mat[SIZE][SIZE], float vec[SIZE]);

float *
mv_mult_scalar(float mat[SIZE][SIZE], float vec[SIZE]) {
    static float ret[SIZE];

    for (int i = 0; i < SIZE; i ++) {
        ret[i] = 0;
        for (int j = 0; j < SIZE; j ++) {
            ret[i] += mat[i][j] * vec[j];
        }
    }

    return ret;
}

float *
time_mult(float mat[SIZE][SIZE], float vec[SIZE], mv_mult_fn mv_mult, const char *version) {
    struct timeval start, end;
    float *ret;

    gettimeofday(&start, NULL);
    for (int i = 0; i < ITER; i ++) {
        ret = mv_mult(mat, vec);
    }
    gettimeofday(&end, NULL);

    long elapsed = (end.tv_sec - start.tv_sec) * 1000000 + (end.tv_usec - start.tv_usec);
    printf("%s version: %d iterations, %ld usec\n", version, ITER, elapsed);
    return ret;
}

int
main(int argc, char **argv) {
    static float mat[SIZE][SIZE];
    static float vec[SIZE];

    for (int i = 0; i < SIZE; i ++) {
        for (int j = 0; j < SIZE; j ++)
            mat[i][j] = (float) i / (float) (j + 1);
        vec[i] = (float) i;
    }

    // variable names commented to prevent unused variable warnings
    // uncomment if you add comparison code
    /*float *scalar_ret = */ time_mult(mat, vec, mv_mult_scalar, "Scalar");
    /*float *vector_ret = */ time_mult(mat, vec, mv_mult_vector, "Vector");

    // you probably want to add code here to compare the scalar and vector results
    // remember that floating point numbers should be compared using a tolerance
    // instead of direct equality - 0.001 works well for the magnitudes involved here
    // http://docs.oracle.com/cd/E19957-01/806-3568/ncg_goldberg.html
    // is a long but classic read

    return 0;
}

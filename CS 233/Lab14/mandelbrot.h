#ifndef MANDLEBROT
#define MANDLEBROT

// this is the number of pixels per unit along the coordinate axes
// purposely chosen so that HEIGHT, and therefore SIZE, is divisible by 4
#define PIXELS_PER_AXES_UNIT 253

// don't change any of these
#define WIDTH (2 * PIXELS_PER_AXES_UNIT + 1)
#define HEIGHT (3 * PIXELS_PER_AXES_UNIT + 1)
#define SIZE (WIDTH * HEIGHT)

#define ITER 10
#define M_ITER 200
#define M_MAG 2.0

int *cubic_mandelbrot_vector(float x[SIZE], float y[SIZE]);

#endif

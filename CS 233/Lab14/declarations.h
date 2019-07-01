#include <malloc.h>
#include <math.h>
#include <mm_malloc.h>
#include <stdio.h>
#include <stdlib.h>
#include <x86intrin.h>

#define ntimes 100000

#define LEN1 1024
void t1(float *A, float *B);

#define LEN2 1280
void t2(float *A, float *B);

#define LEN3 512
void t3(float A[LEN3][LEN3]);

#define LEN4 256
void t4(float M1[LEN4][LEN4], float M2[LEN4][LEN4], float M3[LEN4][LEN4]);

#define LEN5 1024
void t5(float *A, float *B, float *C, float *D, float *E);

#define LEN6 1024
void t6(float *A, float *D);

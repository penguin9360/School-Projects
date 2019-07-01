#include "declarations.h"

#ifdef SCALAR
void
t2(float *A, float *B) {
    for (int nl = 0; nl < 10000000; nl ++) {
        for (int i = 0; i < LEN2 - 4; i += 5) {
            A[i] = B[i] * A[i];
            A[i + 1] = B[i + 1] * A[i + 1];
            A[i + 2] = B[i + 2] * A[i + 2];
            A[i + 3] = B[i + 3] * A[i + 3];
            A[i + 4] = B[i + 4] * A[i + 4];
        }
        A[0] ++;
    }
}
#endif

int
main() {
    float *A = (float *) _mm_malloc(LEN2 * sizeof(float), 16);
    float *B = (float *) _mm_malloc(LEN2 * sizeof(float), 16);
    for (int i = 0; i < LEN2; i ++) {
        A[i] = 1. / (i + 10000.);
        B[i] = 1. / (i + 10000.);
    }

    unsigned long long start_c, end_c, diff_c;
    start_c = __rdtsc();

    t2(A, B);

    end_c = __rdtsc();
    diff_c = end_c - start_c;
    float giga_cycle = diff_c / 1000000000.0;
    float ret = 0.;
    for (int i = 0; i < LEN2; i ++) {
        ret += A[i];
    }
    printf("t2 took %f giga cycles and the result is: %f\n", giga_cycle, ret);
}

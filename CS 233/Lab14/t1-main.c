#include "declarations.h"

#ifdef SCALAR
void
t1(float *A, float *B) {
    for (int nl = 0; nl < 1000000; nl ++) {
        for (int i = 0; i < LEN1; i += 2) {
            A[i + 1] = (A[i] + B[i]) / (A[i] + B[i] + 1.);
        }
        B[0] ++;
    }
}
#endif

int
main() {
    float *A = (float *) _mm_malloc(LEN1 * sizeof(float), 16);
    float *B = (float *) _mm_malloc(LEN1 * sizeof(float), 16);
    for (int i = 0; i < LEN1; i ++) {
        A[i] = 1. / (i + 1);
        B[i] = 2. / (i + 1);
    }

    unsigned long long start_c, end_c, diff_c;
    start_c = __rdtsc();

    t1(A, B);

    end_c = __rdtsc();
    diff_c = end_c - start_c;
    float giga_cycle = diff_c / 1000000000.0;
    float ret = 0;

    for (int i = 0; i < LEN1; i ++) {
        ret += A[i];
    }
    printf("t1 took %f giga cycles and the result is: %f\n", giga_cycle, ret);
}

#include "declarations.h"

void
t1(float *restrict A, float *restrict B) {
    for (int nl = 0; nl < 1000000; nl ++) {
        #pragma clang loop vectorize(enable)
        for (int i = 0; i < LEN1; i += 2) {
            A[i + 1] = (A[i] + B[i]) / (A[i] + B[i] + 1.);
        }

        B[0] ++;
    }
}

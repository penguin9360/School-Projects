
#include "declarations.h"

void
t5(float *restrict A, float *restrict B, float *restrict C, float *restrict D, float *restrict E) {
    for (int nl = 0; nl < ntimes; nl ++) {
        #pragma clang loop vectorize(enable)
        for (int i = 1; i < LEN5; i ++) {
            A[i] = D[i - 1] + (float) sqrt(C[i]);
        }
        #pragma clang loop vectorize(enable)
        for (int i = 1; i < LEN5; i ++) {
            D[i] = B[i] + (float) sqrt(E[i]);
        }

        A[0] ++;
    }
}

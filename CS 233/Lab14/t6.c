
#include "declarations.h"

void
t6(float *restrict A, float *restrict D) {
    for (int nl = 0; nl < ntimes; nl ++) {
        A[0] = 0;
        #pragma clang loop vectorize(assume_safety) interleave(enable) interleave_count(16)
        for (int i = 0; i < (LEN6 - 3); i ++) {
            A[i] = D[i] + (float) 1.0;
            D[i + 3] = A[i] + (float) 2.0;
        }
    }
}

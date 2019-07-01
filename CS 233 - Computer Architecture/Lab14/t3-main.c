#include "declarations.h"

#ifdef SCALAR
void
t3(float A[LEN3][LEN3]) {
    for (int nl = 0; nl < 1000; nl ++) {
        for (int i = 1; i < LEN3; i ++) {
            for (int j = 1; j < LEN3; j ++) {
                A[i][j] = A[i - 1][j] + A[i][j - 1];
            }
        }
        A[0][0] ++;
    }
}
#endif

int
main() {
    float A[LEN3][LEN3] __attribute__((aligned(16)));
    for (int i = 0; i < LEN3; i ++) {
        for (int j = 0; j < LEN3; j ++) {
            A[i][j] = 0.1 / (i + j + 1);
        }
    }

    unsigned long long start_c, end_c, diff_c;
    start_c = __rdtsc();

    t3(A);

    end_c = __rdtsc();
    diff_c = end_c - start_c;
    float giga_cycle = diff_c / 1000000000.0;
    float ret = 0.;
    for (int i = 0; i < 4; i ++) {
        for (int j = 0; j < LEN3; j += 2) {
            ret += A[i][j] - A[i][j + 1];
        }
    }
    printf("t3 took %f giga cycles and the result is: %f\n", giga_cycle, ret);
}

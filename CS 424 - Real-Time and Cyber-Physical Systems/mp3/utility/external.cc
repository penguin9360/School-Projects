#include <stdio.h>
#include <unistd.h>

#include <chrono>
#include <iostream>

int main(int argc, char const *argv[]) {
    int array1[10000];
    int array2[20000];
    unsigned long long counter = 0;
    for (int aid = 0; aid < 3; aid++) {
        fork();
    }
    pid_t pid = getpid();
    pid_t ppid = getppid();
    while (1) {
        for (int j = 0; j < 100000; j++) {
            for(int k = 0; k < 80; k++) {
                array1[j % 10000 ] = array2[j % 10000] * 20- array2[j % 100];
                array2[(j + 5) % 10000] = array1[j % 10000] * 20.03 -
                array2[(j + 100) % 1000];
                array1[k % 10000] = array2[(k+j * 10) % 10000] * 50 -
                array1[ (k*10 + 9) % 1000];
            }
        }
        for (int i = 0; i < 10; i++) {
            array1[i] = 6 * i + i * i - i % 105;
            array2[i] = array1[i] - array1[(i+60) % 1000] * 60;
        }
        usleep(1 * 1000);
        counter++;
        std::cout << "pid:" << pid << " ppid:" << ppid << " counter:" <<
        counter << std::endl;
    }
    return 0;
}

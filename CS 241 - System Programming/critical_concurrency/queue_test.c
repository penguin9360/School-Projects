/**
 * Critical Concurrency
 * CS 241 - Fall 2019
 */
#include <assert.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "queue.h"

static pthread_t pthreads[10];
static queue *q;
void* runner(void*);
void* runner2(void*i);
int main(int argc, char **argv) {
    if (argc != 2) {
        printf("usage: %s test_number\n", argv[0]);
        exit(1);
    }
    long int i;
    void* ret;
    q=queue_create(4);

    for (i = 5; i < 10; i++) {
        pthread_create(&pthreads[i], NULL, runner2, (void *)i);
    }
    printf("Throwaway Test:\n");
    for (i = 5; i < 10; i++) {
        pthread_join(pthreads[i], (&ret));
        if(i>4)
        printf("poping %ld\n",(*(long int*)ret) );

    }
    return 0;
}

void* runner(void*i){
  long int* p=malloc(sizeof(long int));
  *p=(long int)i;
  printf("pushing %ld\n",(long int)i );
  queue_push(q,p);
  return NULL;
}
void* runner2(void*i){
  long int* p=malloc(sizeof(long int));
  *p=(long int)i;
  printf("poping %ld\n",(long int)i );

  void* t=queue_pull(q);
  return t;
}

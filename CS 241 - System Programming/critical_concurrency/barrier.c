/**
 * Critical Concurrency
 * CS 241 - Fall 2019
 */
#include "barrier.h"

// The returns are just for errors if you want to check for them.
int barrier_destroy(barrier_t *barrier) {
    int error = 0;
	
	pthread_mutex_destroy(&(barrier->mtx));
    pthread_cond_destroy(&(barrier->cv));
	
    return error;
}

int barrier_init(barrier_t *barrier, unsigned int num_threads) {
    int error = 0;
	
	pthread_mutex_init(&(barrier->mtx), NULL);
	pthread_cond_init(&(barrier->cv), NULL);
	barrier->n_threads = num_threads;
	barrier->count = 0;
	barrier->times_used = 0;
	
    return error;
}

int barrier_wait(barrier_t *barrier) {
	pthread_mutex_lock(&(barrier->mtx));
	barrier->count++;
	
	unsigned int used = barrier->times_used;
	
	if (barrier->count == barrier->n_threads) {
		barrier->count = 0;
		barrier->times_used++;
		pthread_cond_broadcast(&(barrier->cv));
	} else {
		while (used == barrier->times_used) {
            pthread_cond_wait(&(barrier->cv), &(barrier->mtx));
     	}
	}
	
	pthread_mutex_unlock(&(barrier->mtx));
    return 0;
}

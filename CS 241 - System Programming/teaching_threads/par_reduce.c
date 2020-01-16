/**
 * Teaching Threads
 * CS 241 - Fall 2019
 */
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "reduce.h"
#include "reducers.h"

// copy all params to static vars
static int * input_list;
static size_t input_list_len;
static reducer input_reduce_funct;
static int input_base_case;
static size_t input_num_threads;


/* You might need a struct for each task ... */
typedef struct s{
	size_t input_index;
	pthread_t tid;
	int result;
} reduce_struct;

/* You should create a start routine for your threads. */
void * funct_ptr (void * ptr) {
	
	reduce_struct * r = (reduce_struct *) ptr;
	
	size_t start_idx, end_idx;
	// set up start idx
	start_idx = (input_list_len / input_num_threads) * r->input_index;
	// set up end idx
	if (r->input_index == input_num_threads - 1) {
		end_idx = input_list_len;
	} else {
		end_idx = (input_list_len / input_num_threads) * (r->input_index + 1);
	}
	
	r->result = reduce(input_list + start_idx, end_idx - start_idx, input_reduce_funct, input_base_case);
	
	return ptr;
}

int par_reduce(int *list, size_t list_len, reducer reduce_func, int base_case,
               size_t num_threads) {
    /* Your implementation goes here */
	
	input_list = list;
	input_list_len = list_len;
	input_reduce_funct = reduce_func;
	input_base_case = base_case;
	input_num_threads = num_threads;
	
	if (num_threads < 2 || num_threads >= list_len) {
		return reduce(list, list_len, reduce_func, base_case);
	}
	
	int ret = base_case;
	reduce_struct * reduce_threads = calloc(num_threads, sizeof(reduce_struct));
	
	size_t i = 0;
	// create threads
	while (i < num_threads) {
        reduce_threads[i].input_index = i;
        pthread_create(&reduce_threads[i].tid, NULL, funct_ptr, &reduce_threads[i]);
		i++;
    }
	
	i = 0;
	void * ret_val;
	// join threads
	while (i < num_threads) {
        pthread_join(reduce_threads[i].tid, &ret_val);
		ret = reduce_func(ret, reduce_threads[i].result);
		i++;
    }
	
	free(reduce_threads);
	
    return ret;
}

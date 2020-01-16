/**
 * Password Cracker
 * CS 241 - Fall 2019
 */
#include "cracker1.h"
#include "format.h"
#include "utils.h"
#include "./includes/queue.h"
#include <unistd.h>
#include <stdio.h>
#include <crypt.h>
#include <string.h>
#include <pthread.h>
#include <math.h>

static queue * q;
struct crypt_data * c;
pthread_mutex_t m = PTHREAD_MUTEX_INITIALIZER;
void * cracker(void * tid);

int start(size_t thread_count) {
    // TODO your code here, make sure to use thread_count!
    // Remember to ONLY crack passwords in other threads
	
	q = queue_create(thread_count);
	c = malloc(sizeof(struct crypt_data) * thread_count);
	pthread_t * tid = malloc(thread_count * sizeof(pthread_t));
	
	
	size_t i = 0;
	while (i < thread_count) {
		(c + i)->initialized = 0;
		i++;
	}
	
	i = 0;
	while (i < thread_count) {
		pthread_create(tid + i, NULL, cracker, (void*)i);
		i++;
	}
	
	char * line = NULL;
	size_t n = 0;
	
	while(getline(&line, &n, stdin) != -1) {
        char * ptr=line;
        size_t n = strlen(ptr);
        if(ptr[n - 1] == '\n') {
			ptr[n - 1] = '\0';
		}
        queue_push(q, ptr);
        line = NULL;
        n = 0;
    }
	
	free(line);
	
	i = 0;
	while (i < thread_count) {
		queue_push(q, NULL);
		i++;
	}
	
	i = 0;
	int * retval;
	int numRecovered = 0, numFailed = 0;
	while (i < thread_count) {
		pthread_join(tid[i], (void**) &retval);
		numRecovered += retval[0];
		numFailed += retval[1];
		free(retval);
		i++;
	}

	v1_print_summary(numRecovered, numFailed);

	queue_destroy(q);
	free(c);
	free(tid);	
	
    return 0; // DO NOT change the return code since AG uses it to check if your
              // program exited normally
}

void * cracker(void * tid) {
	int * retval = calloc(2, sizeof(int));
	size_t realTID = (size_t) (tid + 1);
	struct crypt_data* crypt_struct = c + (size_t)tid;
	char * userName = NULL;
	char * hashed = NULL;
	char * part = NULL;
	
	char * curr_line = NULL;
	
	while((curr_line = queue_pull(q)) != NULL) {

		size_t curr_len = strlen(curr_line);
		double curr_CPUtime = getThreadCPUTime();
		size_t i = 0;
		char temp[curr_len];
		
		
		while (i < curr_len + 1) {
			temp[i] = curr_line[i];
			i++;
		}
		
		i = 0;
		size_t ct = 0;
		while (i < curr_len) {
			if (temp[i] == '.' && ct == 2) {
				break;
			}
			if (temp[i] == ' ') {
				temp[i] = '\0';
				ct++;
			}
			i++;
		}
		
		userName = temp;
		hashed = temp + strlen(userName) + 1;
		part = hashed + strlen(hashed)  + 1;
		
		char * crack = temp + i;
		v1_print_thread_start(realTID, userName);
		int num_to_crack = 0;
		while (*crack != '\0') {
			*crack = 'a';
			crack++;
			num_to_crack++;
		}
		
		int success = 0;
		int num_hash = 1;
		if(num_to_crack == 0) {
			char* cracked_str = crypt_r(part, "xx", crypt_struct);
			if(!strcmp(hashed, cracked_str)) {
				success=0;
			} else {
				success=1;
			}
		} else {
			long total_cracks = pow(26, num_to_crack);
			long j = 0;
			success = 1;
			while(j != total_cracks){
				char* cracked_str = crypt_r(part, "xx", crypt_struct);
				if(!strcmp(hashed, cracked_str)) {
					success = 0;
					break;
				}
				num_hash++;
				incrementString(part);
				j++;
			}

		}
		if(success == 0) {
			retval[0]++;
		} else {
			retval[1]++;
		}

		v1_print_thread_result(realTID, userName, part,num_hash, getThreadCPUTime()- curr_CPUtime, success);
		free(curr_line);
	}
	
	return (void*)retval;
}

/**
 * Critical Concurrency
 * CS 241 - Fall 2019
 */
#include "queue.h"
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

/**
 * This queue is implemented with a linked list of queue_nodes.
 */
typedef struct queue_node {
    void *data;
    struct queue_node *next;
} queue_node;

struct queue {
    /* queue_node pointers to the head and tail of the queue */
    queue_node *head, *tail;

    /* The number of elements in the queue */
    ssize_t size;

    /**
     * The maximum number of elements the queue can hold.
     * max_size is non-positive if the queue does not have a max size.
     */
    ssize_t max_size;

    /* Mutex and Condition Variable for thread-safety */
    pthread_cond_t cv;
    pthread_mutex_t m;
};

queue *queue_create(ssize_t max_size) {
    /* Your code here */
	queue * q = malloc(sizeof(queue));
	if (!q) {
		return NULL;
	}
	q->head = NULL;
	q->tail = NULL;
	q->max_size = max_size;
	q->size = 0;
	
	pthread_mutex_init(&(q->m), NULL);
	pthread_cond_init(&(q->cv), NULL);
	return q;
}

void queue_destroy(queue *this) {
    /* Your code here */
	
	queue_node * curr = this->head;
	// use head as iterator
	while (this->head) {
		curr = this->head;
		if (this->size == 1) {
			this->head = NULL;
		} else {
			this->head = curr->next;
		}
		free(curr);
	}
	this->head = NULL;
	this->tail = NULL;
	pthread_mutex_destroy(&(this->m));
	pthread_cond_destroy(&(this->cv));
	free(this);
}

void queue_push(queue *this, void *data) {
    /* Your code here */
	queue_node * new = malloc(sizeof(queue_node));
    new->data = data;
    new->next = NULL;
    pthread_mutex_lock(&this->m);
    if (this->max_size > 0) {
        while (this->size == this->max_size) {
            pthread_cond_wait(&this->cv, &this->m);
        }
    }
    if (!this->head) {
        this->head = new;
        this->tail = new;
    } else {
        this->tail->next = new;
        this->tail = new;
    }
    this->size++;
    pthread_cond_signal(&this->cv);
    pthread_mutex_unlock(&this->m);
}

void *queue_pull(queue *this) {
    /* Your code here */
	pthread_mutex_lock(&(this->m));
	// if the queue is empty, block
	while (this->size == 0) {
		pthread_cond_wait(&(this->cv), &(this->m));
	}
	queue_node * to_pop = this->head;
	void * ret_data;
	
	// if only 1 elem remains, clear the queue
	if (this->size == 1) {
		this->head = NULL;
		this->tail = NULL;
	} else {
		this->head = to_pop->next;
	}
	this->size--;
	
	pthread_cond_signal(&(this->cv));
	pthread_mutex_unlock(&(this->m));
	
	ret_data = to_pop->data;
	free(to_pop);
	
	return ret_data;
}

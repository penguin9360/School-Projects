/**
 * Savvy Scheduler
 * CS 241 - Fall 2019
 */
#include "libpriqueue/libpriqueue.h"
#include "libscheduler.h"

#include <assert.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "print_functions.h"

/**
 * The struct to hold the information about a given job
 */
typedef struct _job_info {
    int id;

    /* TODO: Add any other information and bookkeeping you need into this
     * struct. */
    scheduler_info * stats;
    double arrival;
    double start;
    double response;
    double remain;
    double prev_start;
    double prev_end;

}job_info;

priqueue_t pqueue;
scheme_t pqueue_scheme;
comparer_t comparer;
double res_total;
double wait_total;
double turnaround_total;
long num_jobs;

void scheduler_start_up(scheme_t s) {
    switch (s) {
        case FCFS:
            comparer = comparer_fcfs;
            break;
        case PRI:
            comparer = comparer_pri;
            break;
        case PPRI:
            comparer = comparer_ppri;
            break;
        case PSRTF:
            comparer = comparer_psrtf;
            break;
        case RR:
            comparer = comparer_rr;
            break;
        case SJF:
            comparer = comparer_sjf;
            break;
        default:
            printf("Did not recognize scheme\n");
            exit(1);
    }
    priqueue_init( & pqueue, comparer);
    pqueue_scheme = s;
    // Put any additional set up code you may need here
    res_total = 0;
    wait_total = 0;
    turnaround_total = 0;
    num_jobs = 0 ;
}

static int break_tie(const void * a,
    const void * b) {
    return comparer_fcfs(a, b);
}

int comparer_fcfs(const void * a,
    const void * b) {
    // TODO: Implement me!
    job_info * a1 = ((job_info * )(((job * ) a)->metadata));
    job_info * b1 = ((job_info * )(((job * ) b)->metadata));
    
    if (a1->arrival == b1->arrival) {
        return 0;
    } else if (a1->arrival < b1->arrival) {
        return -1;
    } else {
        return 1;
    }
}

int comparer_ppri(const void * a,
    const void * b) {
    // Complete as is
    return comparer_pri(a, b);
}

int comparer_pri(const void * a,
    const void * b) {
    // TODO: Implement me!
    double a1 = ((job_info * )(((job * ) a)->metadata))->stats->priority;
    double b1 = ((job_info * )(((job * ) b)->metadata))->stats->priority;
    
    if (a1 == b1) {
        return break_tie(a, b);
    } else if (a1 < b1) {
        return -1;
    } else {
        return 1;
    }
}

int comparer_psrtf(const void * a,
    const void * b) {
    // TODO: Implement me!
    double a1 = ((job_info * )(((job * ) a)->metadata))->remain;
    double b1 = ((job_info * )(((job * ) b)->metadata))->remain;
    
    if (a1 == b1) {
        return break_tie(a, b);
    } else if (a1 < b1) {
        return -1;
    } else {
        return 1;
    }
}

int comparer_rr(const void * a,
    const void * b) {
    // TODO: Implement me!
    job * a1 = ((job * ) a);
    job * b1 = ((job * ) b);
    job_info * meta_a = (job_info * ) a1->metadata;
    job_info * meta_b = (job_info * ) b1->metadata;
    
    if (meta_a->prev_end == meta_b->prev_end) {
        return break_tie(a, b);
    } else if (meta_a->prev_end < meta_b->prev_end) {
        return -1;
    } else {
        return 1;
    }
}

int comparer_sjf(const void * a,
    const void * b) {
    // TODO: Implement me!
    double a1 = ((job_info * )(((job * ) a)->metadata))->stats->running_time;
    double b1 = ((job_info * )(((job * ) b)->metadata))->stats->running_time;
    
    if (a1 == b1) {
        return break_tie(a, b);
    } else if (a1 < b1) {
        return -1;
    } else {
        return 1;
    }
}

// Do not allocate stack space or initialize ctx. These will be overwritten by
// gtgo
void scheduler_new_job(job * newjob, int job_number, double time,
    scheduler_info * sched_data) {
    // TODO: Implement me!
    job_info * new_info = malloc(sizeof(job_info));
    newjob->metadata = new_info;
    new_info->id = job_number;
    new_info->stats = malloc(sizeof(scheduler_info));
    memcpy(new_info->stats, sched_data, sizeof(scheduler_info));
    new_info->arrival = time;
    new_info->start = 0;
    new_info->response = 0;
    new_info->remain = sched_data->running_time;
    new_info->prev_start = 0;
    new_info->prev_end = 0;

    priqueue_offer( & pqueue, newjob);
}

job * scheduler_quantum_expired(job * job_evicted, double time) {
    // TODO: Implement me!
    if (job_evicted == NULL) {
        if (priqueue_size( & pqueue) == 0) {
            return NULL;
        } else {
            job * newjob = priqueue_poll( & pqueue);
            job_info * new_ = newjob->metadata;
            if (new_->start == 0) {
                new_->start = time;
                new_->response = time - new_->arrival;
            }
            new_->prev_start = time;
            return newjob;
        }
    }
    if (pqueue_scheme != PPRI && pqueue_scheme != PSRTF && pqueue_scheme != RR) {
        return job_evicted;
    }
    if (job_evicted) {
        job_info * job_e = job_evicted->metadata;
        job_e->remain -= time - job_e->prev_start;
        job_e->prev_end = time;
        priqueue_offer( & pqueue, job_evicted);
    }
    job * newjob = priqueue_poll( & pqueue);
    job_info * new_ = newjob->metadata;
    if (new_->start == 0) {
        new_->start = time;
        new_->response = time - new_->arrival;
    }
    new_->prev_start = time;
    return newjob;
}

void scheduler_job_finished(job * job_done, double time) {
    // TODO: Implement me!
    job_info * i = job_done->metadata;
    res_total += i->response;
    wait_total += time - i->arrival - i->stats->running_time;
    turnaround_total += time - i->arrival;
    num_jobs++;
    free(i->stats);
    free(job_done->metadata);
}

static void print_stats() {
    fprintf(stderr, "turnaround     %f\n", scheduler_average_turnaround_time());
    fprintf(stderr, "wait_total  %f\n", scheduler_average_waiting_time());
    fprintf(stderr, "res_total %f\n", scheduler_average_response_time());
}

double scheduler_average_waiting_time() {
    // TODO: Implement me!
    return wait_total / num_jobs;
}

double scheduler_average_turnaround_time() {
    // TODO: Implement me!
    return turnaround_total / num_jobs;
}

double scheduler_average_response_time() {
    // TODO: Implement me!
    return res_total / num_jobs;
}

void scheduler_show_queue() {
    // OPTIONAL: Implement this if you need it!
}

void scheduler_clean_up() {
    priqueue_destroy( & pqueue);
    print_stats();
}
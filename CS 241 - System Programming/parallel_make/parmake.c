/**
* Parallel Make Lab
* CS 241 - Fall 2019
*/

#include <pthread.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdio.h>

#include "format.h"
#include "graph.h"
#include "parmake.h"
#include "parser.h"
#include "queue.h"
#include "dictionary.h"

#define NOT_RUN 0
#define NORMAL 1
#define COMPLETE 2
#define FAIL 3
#define CYCLE 4

pthread_mutex_t *rule_counter_lock;
pthread_mutex_t *rule_state_lock;
pthread_mutex_t lock_finish;
dictionary* dict2 = NULL;
pthread_cond_t cond_var;
dictionary* dict_lock;
dictionary* dict_ctr;

int sum_rules = 0;
int rules_finished = 0;
 
void push_predessecor(char * target);
int check_time(vector* dependency, char * target);
void* new_thread(void* tid);
int cycle_helper(void* vertex, dictionary * dict);
int cycle(graph*,char*);
int run(void* target);
void push_rules(vector*vec,char* target);
void order_rules(vector*vec,char** targets);
void update_state(rule_t* rule, pthread_mutex_t * curr_lock, int state, pthread_cond_t cond_var, char * target, vector * dependency);

queue * global_queue = NULL;
graph * resource_alloc_graph = NULL;
int white = 0;
int black = 1;
int grey = 2;

void update_state(rule_t* rule, pthread_mutex_t * curr_lock, int state, pthread_cond_t cond_var, char * target, vector * dependency) {
	pthread_mutex_lock(curr_lock);
	rule->state=state;
	pthread_cond_broadcast(&cond_var);
	pthread_mutex_unlock(curr_lock);
	push_predessecor(target);
	vector_destroy(dependency);
}

void* new_thread(void* tid) {
	while(1) {
		char * target=queue_pull(global_queue);
		
		if(!target) {
			queue_push(global_queue,NULL);
			break;
		}
		
		run(target);
	}
	return NULL;
}

int run(void* target) {
	rule_t* rule = (rule_t*)graph_get_vertex_value(resource_alloc_graph,target);
	int lock_idx = *(int*)dictionary_get(dict_lock, rule->target);
	pthread_mutex_t *curr_lock = rule_state_lock + lock_idx;
	pthread_mutex_lock(curr_lock);
	if(rule->state == COMPLETE) {
		pthread_mutex_unlock(curr_lock);
		return 0;
	} else if (rule->state == CYCLE) {
		print_cycle_failure(target);
		pthread_mutex_unlock(curr_lock);
		return 1;
	} else if (rule->state == FAIL) {
		pthread_mutex_unlock(curr_lock);
		return 1;
	} else if (rule->state == NORMAL) {
		pthread_mutex_unlock(curr_lock);
		return 2;
	} else {
		rule->state = NORMAL;
	}
	pthread_mutex_unlock(curr_lock);

	vector* dependency = graph_neighbors(resource_alloc_graph,target);
	if(vector_empty(dependency)) {
	
		if(access(target,F_OK)==0) {
			push_predessecor(target);
			pthread_mutex_lock(curr_lock);
			rule->state=COMPLETE;
			pthread_cond_broadcast(&cond_var);
			pthread_mutex_unlock(curr_lock);
			vector_destroy(dependency);
			return 0;
		} else {
			pthread_mutex_lock(curr_lock);
			if(rule->state==COMPLETE || rule->state==FAIL) {
				pthread_cond_broadcast(&cond_var);
				pthread_mutex_unlock(curr_lock);
				vector_destroy(dependency);
				push_predessecor(target);
				if (rule->state==COMPLETE) {
					return 0;
				} else {
					return 1;
				}
			}

			pthread_mutex_unlock(curr_lock);
			vector* commands = rule->commands;
			void **front = vector_begin(commands);
			void **back = vector_end(commands);
			for (; front != back; ++front) {
				void *temp = *front;
				int system_ret_value = system(temp);
				if(system_ret_value != 0) {
					update_state(rule, curr_lock, FAIL, cond_var, target, dependency);
					return 1;
				}
			}
			update_state(rule, curr_lock, COMPLETE, cond_var, target, dependency);

			return 0;
		}
	}

	void **front = vector_begin(dependency);
	void **back = vector_end(dependency);
	for (; front != back; ++front) {
		void *temp = *front;
		rule_t* v_value =(rule_t*)graph_get_vertex_value(resource_alloc_graph,temp);
		int v_idx = *((int*)dictionary_get(dict_lock,v_value->target));
		pthread_mutex_t *v_lock = rule_state_lock + v_idx;
		
		pthread_mutex_lock(v_lock);
		if(v_value->state==FAIL) {
			rule->state=FAIL;
			pthread_cond_broadcast(&cond_var);
			pthread_mutex_unlock(v_lock);
			push_predessecor(target);
			vector_destroy(dependency);
			return 1;
		} else if(v_value->state == NOT_RUN) {
			pthread_mutex_unlock(v_lock);
			if(run(temp) == 1) {
				pthread_mutex_lock(v_lock);
				rule->state=FAIL;
				pthread_mutex_unlock(v_lock);
			}
			continue;
		} else if(v_value->state == NORMAL) {
			while(v_value->state == NORMAL) {
				pthread_cond_wait(&cond_var,v_lock);
			}
			if(v_value->state == FAIL) {
				rule->state=FAIL;
				pthread_cond_broadcast(&cond_var);
				pthread_mutex_unlock(v_lock);
				push_predessecor(target);
				vector_destroy(dependency);
				return 1;
			}
		}
		pthread_mutex_unlock(v_lock);
		
	}
	if(access(target,F_OK)==0) {
		front = vector_begin(dependency);
		back = vector_end(dependency);
		int all_files_accessed = 1;
		for (; front != back; ++front) {
			void *temp = *front;
			rule_t* v_value=(rule_t*)graph_get_vertex_value(resource_alloc_graph,temp);
			if(!(access(v_value->target,F_OK)==0)) {
				all_files_accessed = 0;
			}
		}
		if(all_files_accessed) {
			if(check_time(dependency,target) == 0) {
				update_state(rule, curr_lock, COMPLETE, cond_var, target, dependency);
				return 0;
			} else {
				vector* commands=rule->commands;
				front = vector_begin(commands);
				back = vector_end(commands);
				for (; front != back; ++front) {
					void *temp = *front;
					int system_ret_value=system(temp);
					if(system_ret_value != 0) {
						update_state(rule, curr_lock, FAIL, cond_var, target, dependency);
						return 1;
					}
				}
				update_state(rule, curr_lock, COMPLETE, cond_var, target, dependency);

				return 0;
			}
		} else {
			vector* commands=rule->commands;
			front = vector_begin(commands);
			back = vector_end(commands);
			for (; front != back; ++front) {
				void *temp = *front;
				int system_ret_value=system(temp);
				if(system_ret_value!=0) {
					update_state(rule, curr_lock, FAIL, cond_var, target, dependency);

					return 1;
				}

			}
			update_state(rule, curr_lock, COMPLETE, cond_var, target, dependency);

			return 0;
		}

	} else {
		vector* commands=rule->commands;
		front = vector_begin(commands);
		back = vector_end(commands);
		for (; front != back; ++front) {
			void *temp = *front;
			int system_ret_value=system(temp);
			if(system_ret_value!=0) {
				update_state(rule, curr_lock, FAIL, cond_var, target, dependency);

				return 1;
			}

		}
		update_state(rule, curr_lock, COMPLETE, cond_var, target, dependency);

		return 0;
	}
	return 0;

}

int parmake(char *makefile, size_t num_threads, char **targets) {
	// good luck!
	pthread_cond_init(&cond_var,NULL);
	dict_lock=string_to_int_dictionary_create();
	dict_ctr=string_to_int_dictionary_create();
	pthread_mutex_init(&lock_finish,NULL);
	
	resource_alloc_graph = parser_parse_makefile(makefile,targets);
	vector* vertex_ = graph_vertices(resource_alloc_graph);
	size_t num_vertices = graph_vertex_count(resource_alloc_graph);
	int *vertex_index = malloc(sizeof(int)*num_vertices);
	rule_state_lock = malloc(sizeof(pthread_mutex_t)*num_vertices);
	rule_counter_lock = malloc(sizeof(pthread_mutex_t)*num_vertices);
	
	size_t i = 0;
	for(i = 0;i < num_vertices; i++) {
		pthread_mutex_init(rule_state_lock + i,NULL);
		pthread_mutex_init(rule_counter_lock + i,NULL);
		char* curr_v=vector_get(vertex_,i);
		vertex_index[i]=i;
		dictionary_set(dict_lock,curr_v,vertex_index+1);
		dictionary_set(dict_ctr,curr_v,vertex_index+1);
		
	}
	
	global_queue = queue_create(num_threads + 1);
	vector* ordered_rules = string_vector_create();
	if(targets[0]==NULL) {
		vector* goals = graph_neighbors(resource_alloc_graph,"");
		char** first_tgt = malloc(2*sizeof(char*));
		first_tgt[0] = (char*)vector_get(goals,0);
		first_tgt[1] = NULL;
		order_rules(ordered_rules, first_tgt);
		vector_destroy(goals);
	} else {
		order_rules(ordered_rules,targets);
	}
	if(dict2) {
		dictionary_destroy(dict2);
	}
	dict2=string_to_int_dictionary_create();
	void **vertex_front = vector_begin(vertex_);
	void **vertex_back = vector_end(vertex_);
	for (; vertex_front != vertex_back; ++vertex_front) {
		void *vertex_temp = *vertex_front;
		if(!strcmp(vertex_temp,"")) {
			continue;
		} else {
			int num_neighbors=(int)graph_vertex_degree(resource_alloc_graph,vertex_temp);
			dictionary_set(dict2,vertex_temp,&num_neighbors);
		}
	}
	
	pthread_t * tids=malloc(sizeof(pthread_t)*num_threads);
	i = 0;
	while(i<num_threads) {
		pthread_create(tids+i,NULL,new_thread,(void*)i);
		i++;
	}
	
	void **front = vector_begin(ordered_rules);
	void **back = vector_end(ordered_rules);
	for (; front != back; ++front) {
		void *temp = *front;
		
		queue_push(global_queue,temp);
	}
	i=0;
	while(i<num_threads) {
		pthread_join(tids[i],NULL);
		i++;
	}
	
	graph_destroy(resource_alloc_graph);
	queue_destroy(global_queue);
	dictionary_destroy(dict_lock);
	dictionary_destroy(dict2);
	dictionary_destroy(dict_ctr);
	pthread_cond_destroy(&cond_var);
	vector_destroy(vertex_);
	vector_destroy(ordered_rules);
	free(tids);
	free(vertex_index);
	free(rule_state_lock);
	free(rule_counter_lock);
	
	return 0;
}


int check_time(vector* dependency,char*target) {
	struct stat stat1,stat2;
	stat(target, &stat1);
	void **front = vector_begin(dependency);
	void **back = vector_end(dependency);
	for (; front != back; ++front) {
		void *temp = *front;
		rule_t* v_value=(rule_t*)graph_get_vertex_value(resource_alloc_graph,temp);
		stat(v_value->target,&stat2);
		if(stat2.st_mtime>stat1.st_mtime) {
			return 1;
		}
	}
	return 0;
}


int cycle(graph*this,char* target) {
	
	vector* dependency = graph_vertices(resource_alloc_graph);
	dictionary* dict = shallow_to_int_dictionary_create();
	void **front = vector_begin(dependency);
	void **back = vector_end(dependency);
	for (; front != back; ++front) {
		void *temp = *front;
		dictionary_set(dict,temp,&white);
	}
	int ret = cycle_helper(target,dict);
	dictionary_destroy(dict);
	vector_destroy(dependency);
	return ret;
}


int cycle_helper(void* vertex,dictionary*dict) {
	
	if(graph_vertex_degree(resource_alloc_graph,vertex) == 0) {
		return 0;
	}
	vector* temp = graph_neighbors(resource_alloc_graph,vertex);  
	dictionary_set(dict,vertex, &grey);
	void **front = vector_begin(temp);
	void **back = vector_end(temp);
	for (; front != back; ++front) {
		void *cur = *front;
		if(*((int*)dictionary_get(dict,cur))==grey) {
			vector_destroy(temp);
			return 1;
		} else {
			if(cycle_helper(cur,dict) == 1){
				vector_destroy(temp);
				return 1;
			}
		}
	}
	dictionary_set(dict,vertex,&black);
	vector_destroy(temp);
	return 0;

}



void order_rules(vector*vec,char** targets) {
	int i = 0;
	while (targets[i]) {
		if(cycle(resource_alloc_graph,targets[i])) {
			rule_t* rule=(rule_t*)graph_get_vertex_value(resource_alloc_graph,targets[i]);
			int lock_idx=*(int*)dictionary_get(dict_lock,targets[i]);
			pthread_mutex_t *curr_lock = rule_state_lock + lock_idx;
			pthread_mutex_lock(curr_lock);
			rule->state = CYCLE;
			pthread_mutex_unlock(curr_lock);
			print_cycle_failure(targets[i]);
			if(targets[i+1]==NULL)
			vector_push_back(vec,NULL);
		} else {
			push_rules(vec,targets[i]);
		} 
		i++;
	}
	sum_rules=i;
}


void push_rules(vector*vec,char* target) { 
	vector* dependency=graph_neighbors(resource_alloc_graph,target);
	if(dict2==NULL)
	dict2=string_to_int_dictionary_create();
	if(vector_empty(dependency)) {
		if(dictionary_contains(dict2,target)) {
		
		} else {
			dictionary_set(dict2,target,&black);
			vector_push_back(vec,target);
		}
	} else {
		void **front = vector_begin(dependency);
		void **back = vector_end(dependency);
		for (; front != back; ++front) {
			void *temp = *front;
			push_rules(vec,temp);
		}
	}
	vector_destroy(dependency);
}


void push_predessecor(char* target) {
	if(graph_adjacent(resource_alloc_graph,"",target))	{
		queue_push(global_queue,NULL);
		return;
	}
	vector* anti_neighbor = graph_antineighbors(resource_alloc_graph,target);
	void **front = vector_begin(anti_neighbor);
	void **back = vector_end(anti_neighbor);
	for (; front != back; ++front) {
		char *temp = (char*)*front;

		int lock_idx=*(int*)dictionary_get(dict_ctr,temp);
		pthread_mutex_t *curr_lock=rule_counter_lock+lock_idx;
			
		pthread_mutex_lock(curr_lock);
		int val = *((int*)dictionary_get(dict2,temp));
			
		if(val == 1){
			queue_push(global_queue,temp);
				
		} else {
			val--;
			dictionary_set(dict2,temp,&val);
		}
		pthread_mutex_unlock(curr_lock);

	}
	vector_destroy(anti_neighbor);
}


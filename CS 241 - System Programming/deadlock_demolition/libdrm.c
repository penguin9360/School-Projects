/**
 * Deadlock Demolition
 * CS 241 - Fall 2019
 */
#include "graph.h"
#include "libdrm.h"
#include "set.h"
#include <pthread.h>
#include <stdio.h>

struct drm_t {
	pthread_mutex_t m;
};

int dfs_cycle(pthread_t * vertex);
int cycle_helper(void* vertex, dictionary * d);
static graph * resource_alloc_graph = NULL;
static pthread_mutex_t g_lock = PTHREAD_MUTEX_INITIALIZER;
static int t = 1;
static int f = 0;

drm_t *drm_init() {
    /* Your code here */
	
	drm_t * drm = malloc(sizeof(drm_t));
	pthread_mutex_init(&(drm->m), NULL);
	if (!resource_alloc_graph) {
		resource_alloc_graph = shallow_graph_create();
	}
	pthread_mutex_lock(&g_lock);
	graph_add_vertex(resource_alloc_graph, drm);
	pthread_mutex_unlock(&g_lock);
	
    return drm;
}

int drm_post(drm_t *drm, pthread_t *thread_id) {
    /* Your code here */
	pthread_mutex_lock(&g_lock);
	
	if (!graph_contains_vertex(resource_alloc_graph, thread_id) || 
		!graph_contains_vertex(resource_alloc_graph, drm)) {
			
		pthread_mutex_unlock(&g_lock);
		return 0;
    } 
	
	if (graph_adjacent(resource_alloc_graph, drm, thread_id)) {
		
		graph_remove_edge(resource_alloc_graph, drm, thread_id);
		pthread_mutex_unlock(&g_lock);
		pthread_mutex_unlock(&(drm->m));
		
		return 0;
	} 
	pthread_mutex_unlock(&g_lock);
	return 1;
	
}

int drm_wait(drm_t *drm, pthread_t *thread_id) {
    /* Your code here */
	pthread_mutex_lock(&g_lock);
	
	graph_add_vertex(resource_alloc_graph, thread_id);
	
	if (graph_adjacent(resource_alloc_graph, drm, thread_id)) {
		pthread_mutex_unlock(&g_lock);
		return 0;
    }
	
	graph_add_edge(resource_alloc_graph, thread_id, drm);
	
	
	
	if (dfs_cycle(thread_id)) {
		graph_remove_edge(resource_alloc_graph, drm, thread_id);
		pthread_mutex_unlock(&g_lock);
		return 0;
	} else {
		pthread_mutex_unlock(&g_lock);
		
		pthread_mutex_lock(&(drm->m));
		
		pthread_mutex_lock(&g_lock);
		
		graph_remove_edge(resource_alloc_graph, thread_id, drm);
		graph_add_edge(resource_alloc_graph, drm, thread_id);

		pthread_mutex_unlock(&g_lock);
	}
	
	
	pthread_mutex_unlock(&g_lock);
    return 1;
}

void drm_destroy(drm_t *drm) {
    /* Your code here */
	graph_remove_vertex(resource_alloc_graph, drm);
    pthread_mutex_destroy(&(drm->m));
    free(drm);
    pthread_mutex_destroy(&g_lock);
    return;
}

int dfs_cycle(pthread_t * thread_id) {
    vector * vertices = graph_vertices(resource_alloc_graph);
    dictionary * d = shallow_to_int_dictionary_create();
    void ** front = vector_begin(vertices);         
    void ** back = vector_end(vertices);        
    while (front != back) {               
        void * curr_vertex = *front;                   
        dictionary_set(d, curr_vertex, &f);   
		front++;		
    } 
    int is_cycle = cycle_helper(thread_id, d);
    dictionary_destroy(d);
    return is_cycle;
}

int cycle_helper(void* vertex, dictionary * d) {
	size_t i = graph_vertex_degree(resource_alloc_graph,vertex);
	if(!i) {
		return 0;
	}
	vector * curr_vertex=graph_neighbors(resource_alloc_graph, vertex);
	  dictionary_set(d,vertex,&t);
	  void **front = vector_begin(curr_vertex);         
	  void **back = vector_end(curr_vertex);       
	  while (front != back) { 	              
		  void *curr_vertex = *front;                   
		  if(*((int*)dictionary_get(d,curr_vertex))==t)
					return 1;
		  else
		  {
			int f=cycle_helper(curr_vertex,d);
			if(f==1)
			  return 1;
		  }
			front++;
	} 
	return 0;
}



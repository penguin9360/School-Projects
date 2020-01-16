/**
 * Mini Memcheck
 * CS 241 - Fall 2019
 */
#include "mini_memcheck.h"
#include <stdio.h>
#include <string.h>

void *mini_malloc(size_t request_size, const char *filename,
                  void *instruction) {
    // your code here

	if (!request_size) {
		return NULL;
	}
	meta_data * meta = malloc(sizeof(meta_data) + request_size);
	if (!meta) {
		 return NULL;
	}       	
	total_memory_requested += request_size;
	meta->request_size = request_size;
    meta->filename = filename;
 	meta->instruction = instruction;
 	meta->next = head;
    head = meta;
    return (void *)(meta + 1);
}

void *mini_calloc(size_t num_elements, size_t element_size,
                  const char *filename, void *instruction) {
    // your code here
	 size_t num_bytes_requested = num_elements * element_size;

	if (num_bytes_requested / num_elements != element_size) { 
		return NULL; 
	}
	meta_data * meta  = calloc(sizeof(meta_data) + num_bytes_requested, 1);
    
	if (!meta) {
		return NULL;
	}
  
    total_memory_requested += num_bytes_requested;
    meta->request_size = num_bytes_requested;
    meta->filename = filename;
    meta->instruction = instruction;
	
	meta->next = head;
    head = meta;
    
    return (void *)(meta + 1);
}

void *mini_realloc(void *payload, size_t request_size, const char *filename,
                   void *instruction) {
    // your code here
	if (!payload) { 
		return mini_malloc(request_size, filename, instruction); 
	}
	
    if (!request_size) {
        mini_free(payload);
       	return NULL;
	}
	size_t prev_size = 0;
	int flag_valid_addr = 0;
   	meta_data * meta_curr = head;
   	meta_data * meta_prev = NULL;
   
    while (meta_curr) {
        if (meta_curr + 1 == payload) {
            prev_size = meta_curr->request_size;
            flag_valid_addr  = 1;
            break;
        }
        meta_prev = meta_curr;
        meta_curr = meta_curr->next;
    }

    if (!flag_valid_addr) {
        invalid_addresses++;
        return NULL;
    }

    if (meta_prev) {
		meta_prev->next = meta_curr->next;
	} else {
		head = meta_curr->next;
	}

    meta_data * meta = (meta_data *)realloc(meta_curr, sizeof(meta_data) + request_size);
    if (!meta) {
		return NULL;
	}

    if (request_size <= meta->request_size) {
		total_memory_freed += (prev_size - request_size);
	} else {
		total_memory_requested += (request_size - prev_size);
	} 

    meta->request_size = request_size;
    meta->filename = filename;
    meta->instruction = instruction;
	meta->next = head;
    head = meta;
   
	return (void *)(meta + 1);
}

void mini_free(void *payload) {
    // your code here
	if (!payload) return;
    meta_data * meta_curr = head;
    meta_data * meta_prev = NULL;

    while (meta_curr) {
        if (meta_curr + 1 == payload) {
         
	   total_memory_freed += meta_curr->request_size;
            if (meta_prev) {
                meta_prev->next = meta_curr->next;
                free(meta_curr);
                return;
            } else {
                head = meta_curr->next;
                free(meta_curr);
                return;
            }
        }

        meta_prev = meta_curr;
        meta_curr = meta_curr->next;
    }
    invalid_addresses++;
    return;
}

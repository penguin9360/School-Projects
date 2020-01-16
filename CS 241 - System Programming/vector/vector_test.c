/**
 * Vector
 * CS 241 - Fall 2019
 */
#include "vector.h"
#include <stdio.h>
int main(int argc, char *argv[]) {
    // Write your test cases here 
	
	vector* v=vector_create(&int_copy_constructor,&int_destructor,&int_default_constructor);
	if(vector_empty(v))
	printf("empty\n");
    int* c=malloc(20*sizeof(int));
    int i;
    for(i=0;i<20;i++){
      *(c+i)=i;
      vector_push_back(v,c+i);
    }
    for(i=0;i<20;i++){
       printf("%d,%d ",*(int*)vector_get(v,i),**(int**)vector_at(v,i));
    }
	puts("");
	vector_set(v,0,c+19);
	printf("after set %d\n",**(int**)vector_front(v));
	vector_pop_back(v);
	printf("after pop: %d",**(int**)vector_back(v));
	vector_insert(v,9,c);
	    for(i=0;i<20;i++){
       printf("%d ",*(int*)vector_get(v,i));
    }
    puts("insert");
	vector_erase(v,9);
	    for(i=0;i<19;i++){
       printf("%d ",*(int*)vector_get(v,i));
    }
    puts("erase\n\n\n");
	vector_resize(v,5);
	    for(i=0;i<5;i++){
       printf("%d ",*(int*)vector_get(v,i));
    }
	for(;i<19;i++)
	printf("%p ",(int*)vector_get(v,i));
	vector_clear(v);
	vector_destroy(v);
	free(c);
    return 0;
	
    return 0;
}

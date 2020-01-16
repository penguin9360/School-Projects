/**
 * Extreme Edge Cases
 * CS 241 - Fall 2019
 */
#include "camelCaser.h"
#include <stdlib.h>
#include <string.h>
#include<stdio.h>
#include<ctype.h>

// declear helper fuctions
void write_str(char** arr, char c);
void concat(char*** arr, char* ptr);

void concat(char*** arr, char* ptr) {
  int len = 0;
  char** tmp = *arr;
  while(*tmp) {
    len++; 
    tmp++;
  }
  char** new_arr = realloc(*arr, (len + 2) * sizeof(char*));
  new_arr[len] = ptr;
  new_arr[len + 1] = NULL;
  *arr = new_arr;
}

void write_str(char** arr1, char c){
  int len = strlen(*arr1) + 1;
  char* arr = realloc(*arr1, len + 1);
  if(arr == NULL) {
    return;
  }
  arr[len - 1] = c;
  arr[len] = '\0';
  *arr1 = arr;
}

char** camel_caser(const char *input_str) {
  if(input_str == NULL) {
	return NULL;
  }
  
  char** arr_ptr = malloc(sizeof(input_str));
  *arr_ptr = NULL;
  
  if(*input_str == '\0') {
    return arr_ptr;
  }

  int length = strlen(input_str);
  char* temp_str = malloc(length + 1);
  
  // initialize a null byte
  char* null_byte = malloc(1);
  *null_byte = '\0';
  char prev = '\0';
 
  //make copy of input string
  strcpy(temp_str, input_str);
  char* idx = temp_str;

  // make lowercases
  while(*idx){
    if(isalpha(*idx) && isupper(*idx)) {
		*idx += 32;
	}
    idx++;
  }
  // return to the beginging of the copied string
  idx = temp_str;

  while(isspace(*idx) || ispunct(*idx)) {
    if(isspace(*idx)) {
		// do nothing
	} else {
      concat(&arr_ptr,null_byte);
      null_byte = malloc(1);
      *null_byte = '\0';
    }
    idx++;
  }

  while(*idx) {
      if(isalpha(*idx)) {
        if(isspace(prev)) {
			
		//printf("current char: %c\n", *idx);
		
		// turn to Upper Cases
          write_str(&null_byte,*idx - 32);
          prev='\0';
        } else {
          write_str(&null_byte,*idx);
		}
        idx++;
		prev='\0';
      } else if(isspace(*idx)) {
		if(prev!='p') {
			prev=' ';
		}
		idx++;
      } else if(ispunct(*idx)) {
        concat(&arr_ptr,null_byte);
        null_byte = malloc(1);
        *null_byte = '\0';
        prev='p';
        idx++;
      } else {
        write_str(&null_byte,*idx);
		if(prev!=' '){
			prev='\0';
		}
        idx++;
      }
  }
	
// cleanup
  free(temp_str);
  free(null_byte);
  
  return arr_ptr;
}

void destroy(char **result) {
	if(result == NULL) {
		return;
	}		
    char** ptr = result;
    while(*ptr)
    {
      free(*ptr);
      ptr++;
    }
    free(result);
 }


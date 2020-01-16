/**
 * Vector
 * CS 241 - Fall 2019
 */
#include "sstring.h"
#include "vector.h"

#ifndef _GNU_SOURCE
#define _GNU_SOURCE
#endif

#include <assert.h>
#include <string.h>

struct sstring {
    // Anything you want 
	char * s_str;
    size_t length;
};

sstring *cstr_to_sstring(const char *input) {
    // your code goes here
	assert(input);
	
	size_t len = strlen(input);
    sstring * sstr = malloc(sizeof(sstring));
    sstr->length = len;
    sstr->s_str = malloc(len + 1);
    size_t i = 0;
	
    for(; i < len + 1; i++){
		sstr->s_str[i] = *input;
		input++;
    }
    return sstr;
}

char *sstring_to_cstr(sstring *input) {
    // your code goes here
	assert(input);
	
	char * cstr = malloc(input->length + 1);
	size_t i = 0;
    for(; i < input->length + 1; i++){
		cstr[i] = input->s_str[i];	
    }
	
    return cstr;
}

int sstring_append(sstring *this, sstring *addition) {
    // your code goes here
	assert(this);
	assert(addition);
	
    char * new_str = realloc(this->s_str, this->length + addition->length + 1);
    char * addition_ptr = addition->s_str;
    char * append_ptr = new_str + this->length;
	
    while(*addition_ptr){
      *append_ptr = *addition_ptr;
      addition_ptr++;
      append_ptr++;
    }
	
    *append_ptr='\0';
    this->s_str = new_str;
    this->length = this->length + addition->length;
	
    return this->length;
}

vector *sstring_split(sstring *this, char delimiter) {
    // your code goes here
	assert(this);
	
    vector *vec = vector_create(string_copy_constructor, string_destructor, string_default_constructor);
    char * cpy = strdup(this->s_str);
    size_t i = 0;
    size_t count = 0;
    size_t len = strlen(cpy);
	
    for(; i < len; i++){
      if(cpy[i] == delimiter){
        cpy[i]='\0';
        count++;
      }
    }
	
    char * temp = cpy;
    i = 0;
	
    for(; i < count + 1; i++){
		vector_push_back(vec, cpy);
		len = strlen(cpy);
		cpy = cpy + len + 1;
    }
	
    free(temp);
    return vec;
}

int sstring_substitute(sstring *this, size_t offset, char *target,
                       char *substitution) {
    // your code goes here
	
	assert(this);
	assert(target);
	assert(offset >= 0);
	
    int len = strlen(target);
	int s_str_len = strlen(this->s_str); 
	int sub_len = strlen(substitution);
    char * tmp_str = this->s_str;
    char * dup_str = NULL;
    char * tmp2 = NULL;
	
    int i = offset;
	int j = i;
	
	int gg = -1;
	int gg2 = 0;
	
	int end = s_str_len;
	
    while(i < end){
		if(tmp_str[i] == target[0]){
			j = 0;
			gg2 = 0;
			for(; j < len; j++; j++) {
				if(tmp_str[i + j] != target[j]){
				gg2 = 1;
				break;
				}
			}
			if(gg2 == 0) {
				gg = 0;
				tmp2 = realloc(tmp_str, s_str_len - len + 1 + sub_len);
				this->s_str = tmp2;
				tmp_str = tmp2;
				dup_str = strdup(tmp_str + i + len);
				tmp_str[i] = '\0';
				strcat(tmp_str, substitution);
				strcat(tmp_str, dup_str);
				free(dup_str);
				end = end + sub_len - len;
				i = i + sub_len - 1;
				s_str_len = s_str_len - len + sub_len;
			}
		}
		i++;
    }
	this->length=s_str_len;    
    return gg;
}

char *sstring_slice(sstring *this, int start, int end) {
    // your code goes here
	assert(this);
	assert(start >= 0);
	// assert(end < this->length);
	
	char * wtf = malloc(end - start + 1);
    int i = start;
    for(; i < end; i++){
      wtf[i - start] = this->s_str[i];
    }
    wtf[i - start]='\0';
    return wtf;
}

void sstring_destroy(sstring *this) {
    // your code goes here
	assert(this);
	free(this->s_str);
    free(this);
}

/**
 * Vector
 * CS 241 - Fall 2019
 */
#include "sstring.h"
#include <stdio.h>

int main(int argc, char *argv[]) {
    // TODO create some tests 

	sstring *s=cstr_to_sstring("this is a  sample");
	sstring *s2=cstr_to_sstring(" and");
    int t=sstring_append(s,s2);
    int t2 =sstring_substitute(s,0,"a","HHH");
    char* t3=sstring_slice(s,0,5);
    printf("4:%s%d%d\n",t3,t,t2);
	vector * v=sstring_split(s,' ');
	free(t3);
	vector_destroy(v);
	sstring_destroy(s);
	sstring_destroy(s2);
	
    return 0;
}

/**
 * Perilous Pointers
 * CS 241 - Fall 2019 
 */
#include "part2-functions.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/**
 * (Edit this function to print out the "Illinois" lines in
 * part2-functions.c in order.)
 */ 
int main() {
    // your code here.
	int i2 = 132;
	first_step(81);
	second_step(&i2);
	
	int i3 = 8942;
	int * i3_ptr = &i3;
	double_step(&i3_ptr);

	char value[9];
	*(int*)(value + 5) = 15;
	strange_step(value);
	
	empty_step("wtf");
	
	char gg[9];
	*gg = 'f';
	*(gg + 3) = 'u';
	void * gg1 = gg;
	two_step(gg1, gg);
	
	char * fu = "gggggggggg";
	three_step(fu, fu + 2, fu + 4);
	
	char * fu2 = "aiqggggggg";
	three_step(fu2, fu2 + 2, fu2 + 4);
	
	char * sb = "f";
	it_may_be_odd(sb, *sb);
	
	char cs241[] = "fuk,CS241";
	tok_step(cs241);
	
	int * fk = malloc(sizeof(int));
	*fk = 0;
	while(*(char *)fk != 1) {
		*fk += 3;
	}
	the_end(fk, fk);
	free(fk);
	
    return 0;
}

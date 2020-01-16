/**
 * Mini Memcheck
 * CS 241 - Fall 2019
 */
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main() { 
    // Your tests here using malloc and free
	char* p2=malloc(20);
	int i=0;
	while(i<20)
	{
	p2[i]='a'+i;
	i++;	}
	p2[19]='\0';
	void* p3=malloc(9);
	void* p4=realloc((char*)p2-1,10);
	char* t=realloc((char*)p2,3000);
	if(t==p2)
		write(1,"h\n",3);
	free(t);
	free(p2);
	free(p3);
	free(p4);
    return 0;
}

/**
 * Extreme Edge Cases
 * CS 241 - Fall 2019
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "camelCaser.h"
#include "camelCaser_tests.h"

/* 
 * Testing function for various implementations of camelCaser.
 *
 * @param  camelCaser   A pointer to the target camelCaser function.
 * @param  destroy      A pointer to the function that destroys camelCaser
 * 			output.
 * @return              Correctness of the program (0 for wrong, 1 for correct).
 */

int diff(char** output, char** input);

int diff(char**output, char**input){
	while(*output){
		if(strcmp(*output, *input)) {
			return 1;
		}
		output++;
		input++;
	}
	if(*input) {
		return 1;
	}
	return 0;	
}

int test_camelCaser(char **(*camelCaser)(const char *), void (*destroy)(char **)) {
    // TODO: Return 1 if the passed in function works properly; 0 if it doesn't.

    char a[] ="  I am_ a very  cute penguin , please give me an A.";
	char** result = (*camelCaser)(a);
	char** output = malloc(4*sizeof(char*));
	
	*output = "iAm";
	*(output + 1) = "aVeryCutePenguin";
	*(output + 2) = "pleaseGiveMeAnA";
	*(output + 3) = NULL;
	
	if(diff(output, result)) {
		return 0;
	}
	
	printf("input: %s\n", a);
	printf("%s,\n%s\n%s\n", *result, *(result + 1), *(result + 2));
	(*destroy)(result);
	
	free(output);	

	result = (*camelCaser)(NULL);
	output=NULL;
	if(result)
		return 0;
	(*destroy)(result);
	free(output);	

	printf("\ntesting b:\n");
	char b[] = "  x ,,1 cnm 66gg cnm2 ,       , gghj8964  ";
	result=(*camelCaser)(b);
	output=malloc(5 * sizeof(char*));
	*output="x";
	*(output + 1)="";
	*(output + 2)="1Cnm66GgCnm2";
	*(output + 3)="";
	*(output + 4)=NULL;
	if(diff(output,result)) {
		return 0;
	}
	printf("input: %s\n", b);
	printf("%s,\n%s\n%s\n%s\n", *result, *(result + 1), *(result + 2), *(result + 3));
	(*destroy)(result);
	free(output);
	
	printf("testing c:\n");
	char c[]="  \a  \a bc \a;/bb cd \tlsat \a  ,\a i am groot,";
	result=(*camelCaser)(c);
	output=malloc(5*sizeof(char*));
	*output="\a\aBc\a";
	*(output + 1)="";
	*(output + 2)="bbCdLsat\a";
	*(output + 3)="\aIAmGroot";
	*(output + 4)=NULL;
	if(diff(output,result))
		return 0;
	printf("input: %s\n", c);
	printf("%s,\n%s\n%s\n%s\n", *result, *(result + 1), *(result + 2), *(result + 3));
	(*destroy)(result);
	free(output);

	printf("\ntesting d:\n");
	char d[]="i i i,i,i,";
	result=(*camelCaser)(d);
	output=malloc(4*sizeof(char*));
	*output="iII";
	*(output + 1)="i";
	*(output + 2)="i";
	*(output + 3)=NULL;
	if(diff(output,result))
		return 0;
	(*destroy)(result);
	free(output);

	printf("\ntesting e:\n");
	char e[]="  IF  ,  you  ARE CURRENTLY ], in insert OR\a\a\b\\ append mode.";
	result=(*camelCaser)(e);
	output=malloc(6*sizeof(char*));
	*output="if";
	*(output + 1)="youAreCurrently";
	*(output + 2)="";
	*(output + 3)="inInsertOr\a\a\b";
	*(output + 4)="appendMode";
	*(output+5)=NULL;
	if(diff(output,result))
	return 0;
	(*destroy)(result);
	free(output);	
	
	printf("\ntesting f:\n");
	char f[]="\a\b \a, \a,\a";
	result=(*camelCaser)(f);
	output=malloc(3*sizeof(char*));
	*output="\a\b\a";
	*(output + 1)="\a";
	*(output + 2)=NULL;
	if(diff(output,result))
		return 0;
	(*destroy)(result);
	free(output);
	
	printf("\ntesting e:\n");
	char g[]="! \" # $ % & \' ( ) * +, - . / : ;  \? @ [ \\ ] ^ _ ` { | } ~";
	result=(*camelCaser)(g);
	output=malloc(30*sizeof(char*));
	int ti=0;
	for(ti=0;ti<30;ti++){
		*(output+ti)="";
	}
	*(output + 29)=NULL;
	if(diff(output,result))
		return 0;
	(*destroy)(result);
	free(output);
	char h[]=" ab\tab\nab\vab\fab\r.";
	result=(*camelCaser)(h);
	output=malloc(2*sizeof(char*));
	*(output)="abAbAbAbAb";

	*(output + 1) = NULL; // f
	if(diff(output,result))
		return 0;
	(*destroy)(result);
	free(output);
	
	printf("testing iiii\n");
	char i[]=" \001ab\t\a\tab.\n\b\nab\v\f\vba.";
	result=(*camelCaser)(i);
	output=malloc(3*sizeof(char*));
	*(output)="\001ab\aAb";
	*(output + 1)="\bAbBa";
	*(output + 2)=NULL;
	if(diff(output,result))
		return 0;
	(*destroy)(result);
	free(output);
	char j[]="¡ bb aa¢, £¤ ss,";
	result=(*camelCaser)(j);
	output=malloc(3*sizeof(char*));
	*(output)="¡ bbAa¢";
	*(output + 1)="£¤Ss";
	*(output + 2)=NULL;
	if(diff(output,result))
	return 0;
	(*destroy)(result);
	free(output);
	char k[]="";
	result=(*camelCaser)(k);
	output=malloc(1*sizeof(char*));
	*(output)=NULL;
	if(diff(output,result))
	return 0;
	(*destroy)(result);
	free(output);
	return 1;
}



/**
 * Mad Mad Access Patterns
 * CS 241 - Fall 2019
 */
#include "tree.h"
#include "utils.h"
#include<stdio.h>
#include<string.h>
#include<stdlib.h>

void recursive_find(FILE* file,char* iter);
/*
  Look up a few nodes in the tree and print the info they contain.
  This version uses fseek() and fread() to access the data.

  ./lookup1 <data_file> <word> [<word> ...]
*/

int main(int argc, char ** argv) {
    if (argc < 3) {
        printArgumentUsage();
        exit(1);
    }
    char * name = argv[1];
    char ** iter = argv + 2;
    FILE * file = fopen(name, "r");
    if (!file) {
        openFail(name);
        exit(1);
    }
    char BTRE[5];
    fread(BTRE, 1, 4, file);
    BTRE[4] = '\0';
    if (strcmp(BTRE, "BTRE")) {
        formatFail(name);
        exit(2);
    }
    while (*iter) {
        fseek(file, 4, SEEK_SET);
        recursive_find(file, * iter);
        iter++;
    }
    fclose(file);
    return 0;
}

void recursive_find(FILE * file, char * iter) {
	
    uint32_t left_child = 0;
    fread(&left_child, 4, 1, file);
    uint32_t right_child = 0;
    fread(&right_child, 4, 1, file);
	
    uint32_t num = 0;
    fread(&num, 4, 1, file);
	
    float price = 0;
    fread(&price, 4, 1, file);
    
	char bytes_found[20];
	
    fread(&bytes_found, 20, 1, file);
    if (strcmp(bytes_found, iter) == 0) {
        printFound(iter, num, price);
        return;
    } else if (strcmp(iter, bytes_found) < 0) {
        if (left_child == 0) {
            printNotFound(iter);
            return;
        }
        fseek(file, left_child, SEEK_SET);
        recursive_find(file, iter);
        return;
    } else {
        if (right_child == 0) {
            printNotFound(iter);
            return;
        }
        fseek(file, right_child, SEEK_SET);
        recursive_find(file, iter);
        return;

    }
}
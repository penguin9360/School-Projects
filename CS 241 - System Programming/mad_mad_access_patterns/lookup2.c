/**
 * Mad Mad Access Patterns
 * CS 241 - Fall 2019
 */
#include "tree.h"
#include "utils.h"
#include<stdio.h>
#include<stdlib.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <unistd.h>
#include<string.h>
#include <sys/types.h>

void recursive_find(char * fd, char * iter);
/*
  Look up a few nodes in the tree and print the info they contain.
  This version uses mmap to access the data.

  ./lookup2 <data_file> <word> [<word> ...]
*/
static char * FILE_MAP = NULL;
int main(int argc, char ** argv) {

    if (argc < 3) {
        printArgumentUsage();
        exit(1);
    }
    char * name = argv[1];
    char ** iter = argv + 2;
    int fd = open(name, O_RDWR);
    if (fd == -1) {
        openFail(name);
        exit(1);
    }
    struct stat buffer;
    stat(name, &buffer);
    char * temp = mmap(NULL, buffer.st_size, PROT_READ, MAP_PRIVATE, fd, 0);
    FILE_MAP = temp;
    char BTRE[5];
    strncpy(BTRE, temp, 4);
    BTRE[4] = '\0';
    if (strcmp(BTRE, "BTRE")) {
        formatFail(name);
        exit(2);
    }
    while (*iter) {
        recursive_find(temp + 4, * iter);
        iter++;
    }
    close(fd);
    return 0;
}
void recursive_find(char * fd, char * iter) {
    BinaryTreeNode * node = (BinaryTreeNode * ) fd;
    if (strcmp(node->word, iter) == 0) {
        printFound(iter, node->count, node->price);
        return;
    } else if (strcmp(iter, node->word) < 0) {
        if (node->left_child == 0) {
            printNotFound(iter);
            return;
        }
        recursive_find(FILE_MAP + node->left_child, iter);
        return;
    } else {
        if (node->right_child == 0) {
            printNotFound(iter);
            return;
        }
        recursive_find(FILE_MAP + node->right_child, iter);
        return;

    }
}

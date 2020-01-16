/**
 * Charming Chatroom
 * CS 241 - Fall 2019
 */
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include "utils.h"
#include <unistd.h>
static const size_t MESSAGE_SIZE_DIGITS = 4;

char *create_message(char *name, char *message) {
    int name_len = strlen(name);
    int msg_len = strlen(message);
    char *msg = calloc(1, msg_len + name_len + 4);
    sprintf(msg, "%s: %s", name, message);

    return msg;
}

ssize_t get_message_size(int socket) {
    int32_t size;
    ssize_t read_bytes =
        read_all_from_socket(socket, (char *)&size, MESSAGE_SIZE_DIGITS);
    if (read_bytes == 0 || read_bytes == -1)
        return read_bytes;

    return (ssize_t)ntohl(size);
}

// You may assume size won't be larger than a 4 byte integer
ssize_t write_message_size(size_t size, int socket) {
    // Your code here
    if(size>MSG_SIZE) {
		return -1;
	}
    uint32_t temp = htonl(size);
    return write_all_to_socket(socket,(char *) &temp, MESSAGE_SIZE_DIGITS); 
}

ssize_t read_all_from_socket(int socket, char *buffer, size_t count) {
    // Your Code Here
	ssize_t total = 0;
	char * b = buffer;
	while(count > 0) {
		ssize_t num_read = read(socket, b, count);
		if(num_read == -1) {
			if(errno != EINTR) {
				return -1;
			}
		} else if((size_t)num_read == count) {
			total += num_read;
			return total;
			
		} else if(num_read == 0) {
			return total;
			
		} else if(num_read>0){
			total += num_read;
			b += num_read;
			count -= num_read;
		}
	}
	return total;
}

ssize_t write_all_to_socket(int socket, const char *buffer, size_t count) {
    // Your Code Here
	ssize_t total = 0;
	const char * b = buffer;
	while(count > 0) {
		ssize_t num_read = write(socket, b, count);
		if(num_read == -1) {
			if(errno != EINTR) {
				return -1;
			}
		} else if((size_t)num_read==count)	{
			total += num_read;
			return total;
		} else if(num_read == 0) {
			return total;
		} else if(num_read > 0){
			total += num_read;
			b += num_read;
			count -= num_read;
		}
	}
	return total;
}

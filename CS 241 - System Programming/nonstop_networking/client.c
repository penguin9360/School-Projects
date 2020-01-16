/**
 * Nonstop Networking
 * CS 241 - Fall 2019
 */
#include "common.h"
#include "format.h"
#include <ctype.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/socket.h>
#include <netdb.h>

char **parse_args(int argc, char **argv);
verb check_args(char **args);
void error_handler(int fd);
void check_valid_input(char** args, int server_response_flag);
size_t find_min(size_t a, size_t b);

void check_valid_input(char ** args, int server_response_flag) {
    if (server_response_flag == 0) {
        if ((!args[3]) || !(args[4])) {
            print_client_usage();
            exit(1);
        }
    } else {
        if (!args[3]) {
            print_client_usage();
            exit(1);
        }
    }
}void error_handler(int fd) {
    char buf[16];
    int r = read(fd, buf, 16);
    buf[r - 1] = '\0';
    print_error_message(buf);

}

size_t find_min(size_t a, size_t b) {
    if (a < b) {
        return a;
    }
    return b;
}

int main(int argc, char ** argv) {
    // Good luck!
    verb operation = check_args(argv);
    char ** inputs = parse_args(argc, argv);
    if (inputs == NULL) {
        print_client_help();
        return 1;
    }

    char * port = inputs[1];
    char * host = inputs[0];
    struct addrinfo hints, * res;
    memset( & hints, 0, sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    int addr_info_ret = getaddrinfo(host, port, & hints, & res);

    if (addr_info_ret != 0) {
        freeaddrinfo(res);
        perror(gai_strerror(addr_info_ret));
        free(inputs);
        exit(1);
    }
    int sock_ret = socket(AF_INET, SOCK_STREAM, res->ai_protocol);
    if (sock_ret == -1) {
        freeaddrinfo(res);
        perror(NULL);
        free(inputs);
        exit(1);
    }
    if (connect(sock_ret, res->ai_addr, res->ai_addrlen) == -1) {
        freeaddrinfo(res);
        perror(NULL);
        free(inputs);
        exit(1);
    }
    freeaddrinfo(res);
    size_t read_ret = 0;

    if (operation == DELETE) {
        check_valid_input(inputs, 1);
        char * DELETE_request = malloc(9 + strlen(inputs[3]));
        sprintf(DELETE_request, "DELETE %s\n", inputs[3]);
        write(sock_ret, DELETE_request, strlen(DELETE_request));
        char response[7];
        response[3] = '\0';
        response[6] = '\0';
        read_ret = read(sock_ret, response, 3);
        if (read_ret == 0) {
            print_connection_closed();
            free(DELETE_request);
            free(inputs);
            exit(1);
        }
        if (!strcmp("OK\n", response)) {
            print_success();
            free(DELETE_request);
            free(inputs);
            return 0;
        }
        read_ret = read(sock_ret, response + 3, 3);
        if (read_ret == 0) {
            print_connection_closed();
            free(DELETE_request);
            free(inputs);
            exit(1);
        }
        if (strcmp(response, "ERROR\n") == 0) {
            error_handler(sock_ret);
        } else {
            print_invalid_response();
        }
        free(DELETE_request);

    } else if (operation == GET) {
        check_valid_input(inputs, 0);

        char * GET_request = malloc(6 + strlen(inputs[3]));
        sprintf(GET_request, "GET %s\n", inputs[3]);
        write(sock_ret, GET_request, 5 + strlen(inputs[3]));

        char response[7];
        response[3] = '\0';
        response[6] = '\0';
        read_ret = read(sock_ret, response, 3);
        if (read_ret == 0) {
            print_connection_closed();
            free(GET_request);
            free(inputs);
            exit(1);
        }
        size_t server_response_flag = 0;
        if (!strcmp("OK\n", response)) {
            server_response_flag = 1;
        }
        if (server_response_flag == 0) {

            read(sock_ret, response + 3, 3);
            if (strcmp(response, "ERROR\n") == 0) {
                error_handler(sock_ret);
            } else {
                print_invalid_response();
            }

            free(GET_request);
            free(inputs);
            return 0;
        }
        size_t file_size = 0;

        read_ret = read(sock_ret, & file_size, sizeof(size_t));

        if (read_ret == 0) {
            print_connection_closed();
            free(GET_request);
            free(inputs);
            exit(1);
        }

        int local_file = open(argv[4], O_RDWR | O_CREAT | O_TRUNC, 00777);
        if (local_file == -1) {
            perror(NULL);
            free(GET_request);
            close(local_file);
            free(inputs);
            exit(1);
        }

        size_t bytes_read = 0;
        char filebuf[1024];
        size_t bytes_left = file_size;

        while (bytes_left > 0) {

            size_t read_ret = read(sock_ret, filebuf, find_min(1024, bytes_left));

            if ((int) read_ret == -1) {
                print_connection_closed();
                close(local_file);
                free(GET_request);
                free(inputs);
                exit(1);
            }

            if (read_ret == 0) {
                break;
            }
            write(local_file, filebuf, read_ret);

            bytes_read += read_ret;
            bytes_left -= read_ret;
        }

        if (bytes_read < file_size) {
            print_too_little_data();
            close(local_file);
            unlink(inputs[4]);
        } else {
            read_ret = read(sock_ret, filebuf, 1);
            if (read_ret == 1) {
                print_received_too_much_data();
                close(local_file);
                unlink(inputs[4]);
            } else {
                close(local_file);
            }
        }

        free(GET_request);

    } else if (operation == LIST) {
        write(sock_ret, "LIST\n", 5);
        char response[7];
        response[3] = '\0';
        response[6] = '\0';
        read_ret = read(sock_ret, response, 3);
        if (read_ret == 0) {
            print_connection_closed();
            free(inputs);
            exit(1);
        }
        if (!strcmp("OK\n", response)) {
            size_t list_size = 0;
            read(sock_ret, & list_size, sizeof(size_t));
            char * LIST_request = malloc(list_size + 1);
            read(sock_ret, LIST_request, list_size);
            LIST_request[list_size] = '\0';
            printf("%s", LIST_request);
            free(LIST_request);
            free(inputs);
            return 0;
        }
        read_ret = read(sock_ret, response + 3, 3);
        if (read_ret == 0) {
            print_connection_closed();
            free(inputs);
            exit(1);
        }
        if (strcmp(response, "ERROR\n") == 0) {
            error_handler(sock_ret);
        } else {
            print_invalid_response();
        }
		

    } else if (operation == PUT) {
        check_valid_input(inputs, 0);
        int PUT_fd = open(argv[4], O_RDONLY);
        if (PUT_fd == -1) {
            perror(NULL);
            close(PUT_fd);
            free(inputs);
            exit(1);
        }

        struct stat PUT_stat;
        fstat(PUT_fd, & PUT_stat);
        size_t file_size = (size_t) PUT_stat.st_size;
        char * mmap_ret = mmap(NULL, file_size, PROT_READ, MAP_PRIVATE, PUT_fd, 0);

        if ((void * ) mmap_ret == (void * ) - 1) {
            perror(NULL);
            close(PUT_fd);
            free(inputs);
            exit(1);
        }
        char * PUT_request = malloc(6 + strlen(inputs[3]));
        sprintf(PUT_request, "PUT %s\n", inputs[3]);
        write(sock_ret, PUT_request, 5 + strlen(inputs[3]));
        write(sock_ret, & file_size, sizeof(size_t));
        size_t byte_written = 0;

        while (byte_written < file_size) {
            size_t temp_ret = 0;
            temp_ret = write(sock_ret, mmap_ret + byte_written, file_size - byte_written);
            byte_written += temp_ret;
        }
        shutdown(sock_ret, SHUT_WR);
        char response[7];
        response[3] = '\0';
        response[6] = '\0';
        read_ret = read(sock_ret, response, 3);
		
        if (read_ret == 0) {
            print_connection_closed();
            free(PUT_request);
            close(PUT_fd);
            free(inputs);
            exit(1);
        }
        if (!strcmp("OK\n", response)) {
            print_success();
            free(PUT_request);
            close(PUT_fd);
            free(inputs);
            return 0;
        }
        read_ret = read(sock_ret, response + 3, 3);
        if (read_ret == 0) {
            print_connection_closed();
            close(PUT_fd);
            free(PUT_request);
            free(inputs);
            exit(1);
        }
        if (strcmp(response, "ERROR\n") == 0) {
            error_handler(sock_ret);
        } else {
            print_invalid_response();
        }
        free(PUT_request);
        close(PUT_fd);
    }
    free(inputs);

}

/**
 * Given commandline argc and argv, parses argv.
 *
 * argc argc from main()
 * argv argv from main()
 *
 * Returns char* array in form of {host, port, method, remote, local, NULL}
 * where `method` is ALL CAPS
 */
char **parse_args(int argc, char **argv) {
    if (argc < 3) {
        return NULL;
    }

    char *host = strtok(argv[1], ":");
    char *port = strtok(NULL, ":");
    if (port == NULL) {
        return NULL;
    }

    char **args = calloc(1, 6 * sizeof(char *));
    args[0] = host;
    args[1] = port;
    args[2] = argv[2];
    char *temp = args[2];
    while (*temp) {
        *temp = toupper((unsigned char)*temp);
        temp++;
    }
    if (argc > 3) {
        args[3] = argv[3];
    }
    if (argc > 4) {
        args[4] = argv[4];
    }

    return args;
}/**
 * Validates args to program.  If `args` are not valid, help information for the
 * program is printed.
 *
 * args     arguments to parse
 *
 * Returns a verb which corresponds to the request method
 */
verb check_args(char **args) {
    if (args == NULL) {
        print_client_usage();
        exit(1);
    }

    char *command = args[2];

    if (strcmp(command, "LIST") == 0) {
        return LIST;
    }

    if (strcmp(command, "GET") == 0) {
        if (args[3] != NULL && args[4] != NULL) {
            return GET;
        }
        print_client_help();
        exit(1);
    }

    if (strcmp(command, "DELETE") == 0) {
        if (args[3] != NULL) {
            return DELETE;
        }
        print_client_help();
        exit(1);
    }

    if (strcmp(command, "PUT") == 0) {
        if (args[3] == NULL || args[4] == NULL) {
            print_client_help();
            exit(1);
        }
        return PUT;
    }

    // Not a valid Method
    print_client_help();
    exit(1);
}
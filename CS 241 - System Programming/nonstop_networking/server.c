/**
 * Nonstop Networking
 * CS 241 - Fall 2019
 */

#include "common.h"
#include "format.h"
#include "includes/dictionary.h"
#include <stdio.h>
#include <vector.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <sys/epoll.h>
#include <errno.h>
#include <stdlib.h>
#include <unistd.h>
#include <netdb.h>
#include <fcntl.h>
#include <signal.h>

static int epoll_fd;
typedef struct epoll_event epoll_event;
static epoll_event * epoll_arr;
static char * path;
static dictionary * states;
static vector * files;

typedef struct meta {
    int run;
    int verb; 
    int fd;
	int error;
    size_t bytes_send;
    size_t bytes_write;
    size_t header_size;
    size_t file_size;
    char header[1024];
    int bytes_of_file_size;
}meta;

void act_handler();
void remove_from_dict(meta * data, int fd);
void add_file(meta * data);
void delete_file(meta * data, int f);
int get_file_size(meta * data, int fd);
size_t find_min(size_t a, size_t b);
void read_file(meta * data, int socket);
void create_directory();
void change_permission(int fd);
int read_header(meta * m, int socket_fd);
void cleanup();
void init(meta * m);

static size_t name_len;

void cleanup() {
    free(epoll_arr);
    dictionary_destroy(states);

    if (!path) {
        vector_destroy(files);
        return;
    }
    void ** front = vector_begin(files);
    void ** back = vector_end(files);
    char file_path[1024];
    while (front != back) {
        char * curr_f = * front;
        sprintf(file_path, "./%s/%s", path, curr_f);
        remove(file_path);
		front++;
    }
    vector_destroy(files);
    remove(path);
    free(path);
}

void init(meta * m) {
    m->run = -1;
    m->verb = -1;
    m->fd = -1;
    m->error = 0;
	m->bytes_send = 0;
    m->header_size = 0;
    m->file_size = 0;
    m->bytes_write = 0;
    m->bytes_of_file_size = 0;

    memset(m->header, 0, 1024);
}

int read_header(meta * m, int socket_fd){
    int i = 0;
    int reader_flag = 0;
    int bytes_read = m->header_size;
    char * buf = m->header + bytes_read;
    for (i = 0; i < 1024 - bytes_read; i++) {
        int ret = read(socket_fd, buf + i, 1);
        if (ret == 0) {
            reader_flag = 2;
            break;
        }
        if (ret == -1) {
            if (errno == EAGAIN || errno == EWOULDBLOCK) {
                reader_flag = 3;
                break;
            }

            reader_flag = 4;
            break;
        }
        bytes_read++;
        if (buf[i] == '\n') {

            reader_flag = 1;
            break;
        }
    }

    m->header_size = bytes_read;
    m->run = 0;
    int ret;
    if (reader_flag == 0) {
        return -1;
    } else if (reader_flag == 1) {
        m->run = 1;
        if (strncmp(m->header, "PUT ", 4) == 0) {

            m->verb = 1;
            m->header[m->header_size - 1] = '\0';

            ret = read(socket_fd, & m->file_size, sizeof(size_t));
            if (ret == 0) {
                return -1;
            } else if (ret == -1) {
                if (errno == EAGAIN) {
                    m->run = 3;
                    return 2;
                }
            } else if (ret != sizeof(size_t)) {
                m->bytes_of_file_size = ret;
                m->run = 3;
                return 2;
            } else
                return 0;
        } else if (strncmp(m->header, "GET ", 4) == 0) {
            m->verb = 2;
            m->header[m->header_size - 1] = '\0';
            return 0;
        } else if (strncmp(m->header, "LIST", 4) == 0) {
            m->verb = 3;
            m->header[m->header_size - 1] = '\0';
            return 0;
        } else if (strncmp(m->header, "DELETE ", 7) == 0) {
            m->verb = 4;
            m->header[m->header_size - 1] = '\0';
            return 0;
        } else {
            return -1;
        }
    } else if (reader_flag == 2) {
        return -1;
    } else if (reader_flag == 3) {
        return 2;
    }

    return 32;
}
void change_permission(int fd) {
    epoll_event epoll_temp;
    memset( & epoll_temp, 0, sizeof(epoll_event));
    epoll_temp.data.fd = fd;
    epoll_temp.events = EPOLLOUT;
    epoll_ctl(epoll_fd, EPOLL_CTL_DEL, fd, NULL);
    epoll_ctl(epoll_fd, EPOLL_CTL_ADD, fd, & epoll_temp);
}
void create_directory() {
    char * dir_temp = malloc(7);
    memset(dir_temp, 'X', 6);
    dir_temp[6] = '\0';
    path = mkdtemp(dir_temp);
    print_temp_directory(path);
}
void read_file(meta * data, int socket) {
    if (data->fd == -1) {
        if (path == NULL) {
            create_directory();
        }
        char * filename = data->header + 4;
        char file_path[1024];
        sprintf(file_path, "./%s/%s", path, filename);
        int local_file = open(file_path, O_RDWR | O_CREAT | O_TRUNC, 00777);
        data->fd = local_file;
    }
    size_t bytes = data->bytes_send;
    size_t bytes_left = data->file_size - data->bytes_send;
    char buffer[1024];
    int read_flag = 0;
    int read_ret = 0;
    while (bytes_left > 0) {
        read_ret = read(socket, buffer, find_min(1024, bytes_left));
        if (read_ret == 0) {
            read_flag = 1;
            break;
        } else if (read_ret == -1) {
            if (errno == EAGAIN || errno == EWOULDBLOCK) {
                read_flag = 2;
                data->bytes_send = bytes;
                break;
            } else {
                read_flag = 3;
                break;
            }
        }
        bytes_left -= read_ret;
        bytes += read_ret;
        write(data->fd, buffer, read_ret);
    }
    if (read_flag == 1) {
        change_permission(socket);
        if (bytes == data->file_size) {
            data->bytes_send = bytes;
            add_file(data);
        } else if (bytes < data->file_size) {
            print_too_little_data();
            data->bytes_send = bytes;
            delete_file(data, 0);
            data->error = 2;
        }
    } else if (read_flag == 0) {
        read_ret = read(socket, buffer, 1);
        change_permission(socket);
        data->bytes_send = bytes;
        if (read_ret == 1) {
            print_received_too_much_data();
            delete_file(data, 0);
            data->bytes_send = bytes;
            data->error = 2;
        } else {
            add_file(data);
        }
    }
}
void delete_file(meta * data, int f) {
    char * filename = NULL;
    if (data->verb == 1) {
        filename = data->header + 4;
    } else if (data->verb == 4) {
        filename = data->header + 7;
    }
    if (data->fd != -1)
        close(data->fd);
    char file_path[1024];
    sprintf(file_path, "./%s/%s", path, filename);
    int ret = remove(file_path);
    if (ret == -1) {
        data->error = 3;
    } else {
        if (f == 0)
            return;
        name_len -= strlen(filename) + 1;
        void ** front = vector_begin(files);
        void ** back = vector_end(files);
        size_t position = 0;
        while (front != back) {
            char * curr_f = * front;
            if (strcmp(filename, curr_f) == 0) {
                vector_erase(files, position);
                break;
            }
            position++;
			front++;
        }
    }
}
void add_file(meta * data) {
    char * filename = NULL;
    if (data->verb == 1) {
        filename = data->header + 4;
    }
    void ** front = vector_begin(files);
    void ** back = vector_end(files);
    int f = 1;
    for (; front != back; ++front) {
        char * curr_f = * front;
        if (strcmp(curr_f, filename) == 0)
            f = 1;
    }
    if (f) {
        vector_push_back(files, filename);
        name_len += strlen(filename) + 1;
    }
}

void remove_from_dict(meta * data, int fd) {
    epoll_ctl(epoll_fd, EPOLL_CTL_DEL, fd, NULL);
    dictionary_remove(states, & fd);
    free(data);
    close(fd);
}

int get_file_size(meta * data, int f) {
    char * target = (char * )( & data->file_size);
    int have_read = data->bytes_of_file_size;
    int left = sizeof(size_t) - have_read;
    target += have_read;
    int ret = read(f, target, left);
    if (ret == left) {
        data->run = 1;
        return 0;
    } else if (ret == -1) {
        if (errno == EAGAIN) {
            return 2;
        } else {
            perror(NULL);
            return -1;
        }
    } else if (ret == 0) {
        return -1;
    } else {
        data->bytes_of_file_size += ret;
        return 2;
    }
    return 103;
}

void act_handler() {
    cleanup();
    exit(1);
}

size_t find_min(size_t a, size_t b) {
    if (a < b) {
        return a;
    }
    return b;
}

int main(int argc, char ** argv) {
    // good luck!
    path = NULL;
    name_len = 0;
    if (argc != 2) {
        print_server_usage();
        exit(1);
    }
	// oldact
    struct sigaction act, oldact;
    memset(&act, 0, sizeof(act));
    memset(&oldact, 0, sizeof(oldact));
    act.sa_handler = act_handler;
    act.sa_flags = 0;
    sigemptyset( & act.sa_mask);
    if (sigaction(SIGINT, & act, NULL) == -1) {
        perror(NULL);
	}
    sigignore(SIGPIPE);

    files = string_vector_create();
    states = int_to_shallow_dictionary_create();
    char * port = argv[1];
    epoll_fd = epoll_create(1);
    struct addrinfo addr;
	struct addrinfo * res;
    memset(&addr, 0, sizeof(addr));
    addr.ai_family = AF_INET;
    addr.ai_socktype = SOCK_STREAM;
    addr.ai_flags = AI_PASSIVE;
    int ret_getadd = getaddrinfo(NULL, port, & addr, & res);
    if (ret_getadd != 0) {
        perror(gai_strerror(ret_getadd));
        freeaddrinfo(res);
        exit(1);
    }
    int ret_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (ret_socket == -1) {
        perror(NULL);
        exit(1);
    }
    int optval = 1;
    setsockopt(ret_socket, SOL_SOCKET, SO_REUSEPORT, & optval, sizeof(optval));
    int ret_bind = bind(ret_socket, res->ai_addr, res->ai_addrlen);
    if (ret_bind != 0) {
        perror(NULL);
        freeaddrinfo(res);
        exit(1);
    }
    freeaddrinfo(res);
    int ret_listen = listen(ret_socket, 8);
    if (ret_listen != 0) {
        perror(NULL);
        freeaddrinfo(res);
        exit(1);
    }
    struct epoll_event event, epoll_temp;
    memset( & event, 0, sizeof(event));
    memset( & epoll_temp, 0, sizeof(event));

    event.data.fd = ret_socket;
    event.events = EPOLLIN;
    int ret_ctl = epoll_ctl(epoll_fd, EPOLL_CTL_ADD, ret_socket, & event);
    if (ret_ctl == -1) {
        perror(NULL);
        exit(1);
    }
	
	// set the maximum number of clients to 100
    epoll_arr = calloc(100, sizeof(epoll_event));
    while (1) {
        memset(epoll_arr, 0, 100 * sizeof(epoll_event));
        int number_of_fds = epoll_wait(epoll_fd, epoll_arr, 100, 30);
        int j = 0;
        for (j = 0; j < number_of_fds; j++) {
            epoll_event * cur_event = & epoll_arr[j];
            if (cur_event->data.fd == ret_socket) {
                int new_fd = accept(ret_socket, NULL, NULL);
                if (new_fd == -1) {
                    perror(NULL);
                    freeaddrinfo(res);
                    cleanup();
                    exit(1);
                }
                epoll_temp.data.fd = new_fd;
                epoll_temp.events = EPOLLIN;
                int none_bloking_flags = fcntl(epoll_temp.data.fd, F_GETFL, 0);
                int fcntl_ret = fcntl(epoll_temp.data.fd, F_SETFL, none_bloking_flags | O_NONBLOCK);
                if (fcntl_ret == -1) {
                    perror(NULL);
                    freeaddrinfo(res);
                    cleanup();
                    exit(1);
                }
                epoll_ctl(epoll_fd, EPOLL_CTL_ADD, new_fd, & epoll_temp);
            } else if (cur_event->events == EPOLLIN) {
                if (!dictionary_contains(states, & cur_event->data.fd)) {
                    meta * temp_m = malloc(sizeof(meta));
                    init(temp_m);
                    dictionary_set(states, & cur_event->data.fd, temp_m);
                    int header_ret = read_header(temp_m, cur_event->data.fd);
                    if (header_ret == -1) {
                        temp_m->error = 1;
                        change_permission(cur_event->data.fd);
                        continue;
                    } else if (header_ret == 2) {
                        continue;
                    }
                }
                meta * m = dictionary_get(states, & cur_event->data.fd);
                if (m->run == 0) {
                    int header_ret = read_header(m, cur_event->data.fd);
                    if (header_ret == -1) {
                        m->error = 1;
                        change_permission(cur_event->data.fd);
                        continue;
                    } else if (header_ret == 2) {
                        continue;
                    }
                }
                if (m->run == 3) {
                    int header_ret = get_file_size(m, cur_event->data.fd);
                    if (header_ret == -1) {
                        m->error = 1;
                        change_permission(cur_event->data.fd);
                        continue;
                    } else if (header_ret == 2) {
                        continue;
					}
                }
                if (m->run == 1) {
                    if (m->verb == 1) {
                        read_file(m, cur_event->data.fd);
                    } else if (m->verb == 2) {
						
						char * file_name = m->header + 4;
						char f_p[1024];
						sprintf(f_p, "./%s/%s", path, file_name);
						int mfd = open(f_p, O_RDONLY);
						if (mfd == -1) {
							return -1;
						} else {
							m->fd = mfd;
						}
					
                        if (mfd == -1) {
                            m->error = 3;
                        }
						
                        change_permission(cur_event->data.fd);
                    } else if (m->verb == 3) {
                        change_permission(cur_event->data.fd);
                    } else if (m->verb == 4) {
                        delete_file(m, 1);
                        change_permission(cur_event->data.fd);
                    }
                }

            } else if (cur_event->events == EPOLLOUT) {
                meta * m = dictionary_get(states, & cur_event->data.fd);
                char message_err[30];
                int len = 0;
                if (m->error == 1) {
                    print_invalid_response();
                    sprintf(message_err, "ERROR\n%s", err_bad_request);
                    len = strlen(message_err);

                    size_t bytes_left = len - m->bytes_write;
                    while (bytes_left > 0) {

                        int ret = write(cur_event->data.fd, message_err + m->bytes_write, find_min(len, bytes_left));
                        if (ret == -1) {
                            if (errno == EWOULDBLOCK || errno == EAGAIN) {
                                m->bytes_write = len - bytes_left;
                                break;
                            } else {
                                perror(NULL);
                                remove_from_dict(m, cur_event->data.fd);
                                break;
                            }
                        }
                        if (ret == 0) {
                            remove_from_dict(m, cur_event->data.fd);
                            break;
                        }
                        bytes_left -= ret;
                    }
                    if (bytes_left == 0)
                        remove_from_dict(m, cur_event->data.fd);

                } else if (m->error == 2) {
                    sprintf(message_err, "ERROR\n%s", err_bad_file_size);
                    len = strlen(message_err);
                    size_t bytes_left = len - m->bytes_write;
                    while (bytes_left > 0) {
                        int ret = write(cur_event->data.fd, message_err + m->bytes_write, find_min(len, bytes_left));
                        if (ret == -1) {
                            if (errno == EWOULDBLOCK || errno == EAGAIN) {
                                m->bytes_write = len - bytes_left;
                                break;
                            }
                        }
                        if (ret == 0) {
                            remove_from_dict(m, cur_event->data.fd);
                            break;
                        }
                        bytes_left -= ret;
                    }
                    if (bytes_left == 0)
                        remove_from_dict(m, cur_event->data.fd);
                } else if (m->error == 3) {
                    sprintf(message_err, "ERROR\n%s", err_no_such_file);
                    len = strlen(message_err);
                    size_t bytes_left = len - m->bytes_write;
                    while (bytes_left > 0) {
                        int ret = write(cur_event->data.fd, message_err + m->bytes_write, find_min(len, bytes_left));
                        if (ret == -1) {
                            if (errno == EWOULDBLOCK || errno == EAGAIN) {
                                m->bytes_write = len - bytes_left;
                                break;
                            }
                        }
                        if (ret == 0) {
                            remove_from_dict(m, cur_event->data.fd);
                            break;
                        }
                        bytes_left -= ret;
                    }
                    if (bytes_left == 0)
                        remove_from_dict(m, cur_event->data.fd);
                } else {
                    if (m->verb == 1 || m->verb == 4) {
                        sprintf(message_err, "OK\n");
                        len = strlen(message_err);
                        size_t bytes_left = len - m->bytes_write;
                        while (bytes_left > 0) {
                            int ret = write(cur_event->data.fd, message_err + m->bytes_write, find_min(len, bytes_left));
                            if (ret == -1) {
                                if (errno == EWOULDBLOCK || errno == EAGAIN) {
                                    m->bytes_write = len - bytes_left;
                                    break;
                                }
                            }
                            if (ret == 0) {
                                remove_from_dict(m, cur_event->data.fd);
                                break;
                            }
                            bytes_left -= ret;
                        }
                        if (bytes_left == 0) {
                            remove_from_dict(m, cur_event->data.fd);
                        }
                    } else if (m->verb == 2) {
						// init file size 
                        if (m->file_size == 0){
							int fd_init = m->fd;
							struct stat put_file_stat;
							fstat(fd_init, & put_file_stat);
							size_t fs = (size_t) put_file_stat.st_size;
							m->file_size = fs;
						}
						
                        if (m->bytes_write < 3 + sizeof(size_t)) {
                            sprintf(message_err, "OK\n");
                            memmove(message_err + 3, & m->file_size, sizeof(size_t));
                            len = 3 + sizeof(size_t);

                            size_t bytes_left = len - m->bytes_write;
                            while (bytes_left > 0) {
                                int ret = write(cur_event->data.fd, message_err + m->bytes_write, find_min(len, bytes_left));
                                if (ret == -1) {
                                    if (errno == EWOULDBLOCK || errno == EAGAIN) {
                                        m->bytes_write = len - bytes_left;
                                        break;
                                    }
                                }
                                if (ret == 0) {
                                    remove_from_dict(m, cur_event->data.fd);
                                    break;
                                }
                                bytes_left -= ret;
                            }
                            m->bytes_write = len - bytes_left;
                        }
                        if (m->bytes_write < 3 + sizeof(size_t))
                            continue;
                        char write_buf[1024];
                        size_t start_point = m->bytes_write - 3 - sizeof(size_t);
                        size_t left = m->file_size - start_point;
                        size_t write_num = 0;
                        while (left > 0) {
                            int ret_r = read(m->fd, write_buf, find_min(1024, left));
                            if (ret_r == 0 || ret_r == -1) {
                                printf("fail to read from local file\n");
                                break;
                            }
                            int ret_w = write(cur_event->data.fd, write_buf, ret_r);
                            int diff = ret_r - ret_w;
                            if (ret_w == 0) {
                                remove_from_dict(m, cur_event->data.fd);
                                break;
                            } else if (ret_w == -1) {
                                if (errno == EWOULDBLOCK || errno == EAGAIN) {
                                    lseek(m->fd, -ret_r, SEEK_CUR);
                                    m->bytes_write += write_num;
                                    break;
                                }
                                break;
                            } else if (ret_w < ret_r) {
                                lseek(m->fd, -diff, SEEK_CUR);
                                m->bytes_write += write_num + ret_w;
                                break;
                            }
                            left -= ret_w;
                            write_num += ret_w;
                        }
                        if (left == 0) {
                            remove_from_dict(m, cur_event->data.fd);
                        }
                    } else {
                        if (m->bytes_write < 3 + sizeof(size_t)) {
                            sprintf(message_err, "OK\n");
                            size_t temp_l_size = name_len - 1;
                            if (vector_empty(files))
                                temp_l_size = 0;
                            memmove(message_err + 3, & temp_l_size, sizeof(size_t));
                            len = 3 + sizeof(size_t);
                            size_t bytes_left = len - m->bytes_write;
                            while (bytes_left > 0) {
                                int ret = write(cur_event->data.fd, message_err + m->bytes_write, find_min(len, bytes_left));
                                if (ret == -1) {
                                    if (errno == EWOULDBLOCK || errno == EAGAIN) {
                                        m->bytes_write = len - bytes_left;
                                        break;
                                    }
                                }
                                if (ret == 0) {
                                    remove_from_dict(m, cur_event->data.fd);
                                    break;
                                }
                                bytes_left -= ret;
                            }
                            m->bytes_write = len - bytes_left;
                        }
                        if (m->bytes_write < 3 + sizeof(size_t))
                            continue;
                        size_t position = 0;
                        size_t start_point = m->bytes_write - 3 - sizeof(size_t);
                        if (vector_empty(files)) {
                            remove_from_dict(m, cur_event->data.fd);
                            continue;
                        }

                        void ** front = vector_begin(files);
                        void ** back = vector_end(files);
                        char list_buf[1024];
                        while (front != back) {
                            memset(list_buf, 0, 1024);
                            char * curr_f = * front;
                            int cur_len = strlen(curr_f) + 1;
                            if (position + cur_len < start_point) {
                                position += cur_len;
                                continue;
                            }
                            if (front + 1 == back) {
                                sprintf(list_buf, "%s", curr_f);
                                cur_len--;
                            } else
                                sprintf(list_buf, "%s\n", curr_f);

                            size_t off = start_point - position;
                            int ret_w = write(cur_event->data.fd, list_buf + off, cur_len - off);
                            if (ret_w == -1) {
                                if (errno == EWOULDBLOCK || errno == EAGAIN) {
                                    m->bytes_write = position;
                                    break;
                                }
                                break;
                            } else if (ret_w == 0) {
                                remove_from_dict(m, cur_event->data.fd);
                                break;
                            } else if ((size_t) ret_w != cur_len - off) {
                                position += ret_w;
                                m->bytes_write = position;
                                break;
                            }
                            position += cur_len;
                            start_point += ret_w;
							front++;
                        }
                        if (position == name_len - 1) {
                            remove_from_dict(m, cur_event->data.fd);
						}
                    }
                }
            }
        }
    }
}

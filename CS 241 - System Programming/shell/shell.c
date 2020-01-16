/**
 * Shell
 * CS 241 - Fall 2019
 */

#include "format.h"
#include "shell.h"
#include "vector.h"
 
#include "sstring.h"
#include <string.h>
#include <sys/types.h>
#include <signal.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <unistd.h>
#include <sys/wait.h>

extern char* optarg;
extern int optind, opterr, optopt;

static pid_t curr_pid = 0;

typedef struct process {
    char * command;
    pid_t pid;
} process;
static vector * proc = NULL;

void int_handler() {
    if (curr_pid != 0) {
        kill(curr_pid, SIGKILL);
	}
}

void children_handler() {
    int status = 0;
    while (waitpid(-1, &status, WNOHANG) > 0) {
    }

}

void int_handler();
void children_handler();
void * process_copy_constructor(void *ptr);
void process_destructor(void *ptr);
void * process_default_constructor(void);

void * process_copy_constructor(void * ptr) {
    if (!ptr) {
        return NULL;
	}
    process * new_proc = calloc(1, sizeof(process));
    new_proc->pid = ((process * ) ptr)->pid;
    new_proc->command = strdup(((process * ) ptr)->command);
    return new_proc;
}

void process_destructor(void * ptr) {
    free(((process * ) ptr)->command);
    free(ptr);
}

void * process_default_constructor(void) {
    return calloc(1, sizeof(process));
}

int shell(int argc, char * argv[]) {
    // TODO: This is the entry point for your shell.
    signal(SIGINT, int_handler);
    signal(SIGCHLD, children_handler);

    proc = vector_create(process_copy_constructor, process_destructor, process_default_constructor);
    if (argc != 1 && argc != 3 && argc != 5) {
        print_usage();
        exit(1);
    }
    char cmd[100];
    cmd[0] = '\0';
    pid_t pid_main = getpid();
    size_t dir_size = 0;
    while (dir_size < (size_t) argc) {
        strcat(cmd, argv[dir_size]);
        dir_size++;
    }
    dir_size = 0;
    char * cwd = NULL;
    cwd = getcwd(cwd, dir_size);

    vector * history = string_vector_create();
    FILE * stream = stdin;
    char * opt_str = "h:f:";
    char opt = 0;
    FILE * hist_file = NULL;
    int num_args = -1;
    size_t counter = 0;
    int pre_run = 0;

    while (opt != -1) {
        num_args++;
        opt = getopt(argc, argv, opt_str);
        if (opt == 'h') {
            if (access(optarg, F_OK) == -1) {
                print_history_file_error();
                hist_file = fopen(optarg, "w+");
            } else {
                hist_file = fopen(optarg, "a+");
                char * hist_ = NULL;
                size_t getline_len = 0;
                int hist_size = 0;
                while ((hist_size = getline( & hist_, &getline_len, hist_file)) != -1) {
                    if (hist_[hist_size - 1] != '\n') {
                        hist_ = realloc(hist_, hist_size + 1);
                        hist_[hist_size] = '\0';
                    } else {
                        hist_[hist_size - 1] = '\0';
                    }
                    vector_push_back(history, hist_);
                    counter++;
                }
                free(hist_);
                hist_ = NULL;
            }
        } else if (opt == 'f') {

            FILE * fopen_ret = fopen(optarg, "r");
            if (fopen_ret == NULL) {
                print_script_file_error();
                exit(1);
            } else
                stream = fopen_ret;
        }

    }
    if (num_args * 2 + 1 != argc) {
        print_usage();
        exit(1);
    }

    counter = vector_size(history);

    char * command = NULL;
    int to_run = 0;
    int n = 0;
    size_t proc_counter = 0;

    while (1) {
        size_t num_proc = 0;
        if (to_run == 0) {
            print_prompt(cwd, pid_main);
            free(command);
            command = NULL;
            size_t capacity = 0;
            n = getline( & command, & capacity, stream);
            if (n == -1) {
                while (num_proc < vector_size(proc)) {
                    kill(((process * ) vector_get(proc, num_proc))->pid, SIGKILL);
                    num_proc++;
                }
                if (hist_file != NULL) {
                    while (counter < vector_size(history)) {
                        fprintf(hist_file, "%s\n", (char * ) vector_get(history, counter));
                        counter++;
                    }
                    fclose(hist_file);
                }
                vector_destroy(history);
                vector_destroy(proc);
                exit(0);
            }
        } else {
            n = (int) strlen(command) + 1;
            to_run = 0;
        }
        if (command[n - 1] != '\n') {
            command = realloc(command, n + 1);
            command[n] = '\0';

        } else {
            command[n - 1] = '\0';
        }
        if (stream != stdin || to_run == 1) {
            print_command(command);
        }
        if (!strcmp(command, "exit")) {
            while (num_proc < vector_size(proc)) {
                kill(((process * ) vector_get(proc, num_proc))->pid, SIGKILL);
                num_proc++;
            }
            if (hist_file != NULL) {
                while (counter < vector_size(history)) {
                    fprintf(hist_file, "%s\n", (char * ) vector_get(history, counter));
                    counter++;
                }
                fclose(hist_file);
            }
            free(command);
            vector_destroy(history);
            vector_destroy(proc);
            exit(0);
        }

        sstring * s = cstr_to_sstring(command);
        vector * splited_cmd = sstring_split(s, ' ');
        if (!strcmp((char * ) vector_get(splited_cmd, 0), "!history")) {
            if (vector_size(splited_cmd) > 1) {
                print_invalid_command(command);
                exit(1);
            }
            size_t i = 0;
            while (i < vector_size(history)) {
                print_history_line(i, (char * ) vector_get(history, i));
                i++;
            }
        } else if (!strcmp((char * ) vector_get(splited_cmd, 0), "ps") || !strcmp((char * ) vector_get(splited_cmd, 0), "ps;")) {
            if (pre_run == 0)
                vector_push_back(history, command);
            else
                pre_run = 0;
            size_t ps_idx = 0, splited_cmd_len = 0;

            proc_counter = 0;

            if (vector_size(splited_cmd) != 1) {
                if (vector_size(splited_cmd) < 1) {
                    print_invalid_command(command);
                    exit(1);
                } else {
                    if (!strcmp((char * ) vector_get(splited_cmd, 1), "||") || !strcmp((char * ) vector_get(splited_cmd, 1), "&&") || ((char * ) vector_get(splited_cmd, 0))[strlen((char * ) vector_get(splited_cmd, 0)) - 1] == ';') {
                        pre_run = 1;
                        char * ps_new_cmd = NULL;
                        splited_cmd_len += strlen((char * ) vector_get(splited_cmd, 0)) + 1;
                        if (!strcmp((char * ) vector_get(splited_cmd, 1), "||")) {
                            ps_idx = 1;
                            splited_cmd_len += 3;
                            ps_new_cmd = strdup(command + splited_cmd_len);
                            to_run = 1;
                            free(command);
                            command = ps_new_cmd;
                        } else if (!strcmp((char * ) vector_get(splited_cmd, 1), "&&")) {
                            ps_idx = 2;
                            splited_cmd_len += 3;
                            ps_new_cmd = strdup(command + splited_cmd_len);
                            to_run = 1;
                            free(command);
                            command = ps_new_cmd;
                        } else {
                            ps_idx = 3;
                            ps_new_cmd = strdup(command + splited_cmd_len);
                            to_run = 1;
                            free(command);
                            command = ps_new_cmd;
                        }


                    } else {
                        print_invalid_command(command);
                        exit(1);
                    }
                }
            }

            if (ps_idx == 3)
                ( * ((char ** ) vector_at(splited_cmd, 1)))[strlen((char * ) vector_get(splited_cmd, 0)) - 1] = '\0';



            print_process_info_header();
            unsigned long long bootime = 0;
            FILE * getbtime = fopen("/proc/stat", "r");
            char * btimeentry = NULL;
            size_t btimecap = 0;
            int flag3 = 0;
            while (flag3 != -1) {
                flag3 = getline( & btimeentry, & btimecap, getbtime);
                if (flag3 > 6) {
                    btimeentry[5] = '\0';
                    if (!strcmp(btimeentry, "btime")) {
                        bootime = atoll( & btimeentry[6]);
                        free(btimeentry);
                        break;
                    }
                }
                free(btimeentry);
                btimecap = 0;
                btimeentry = NULL;

            }
            process_info proc_info;
            proc_counter = 0;
            process shell;
            shell.command = strdup(cmd);
            shell.pid = pid_main;
            vector_push_back(proc, & shell);
            while (proc_counter < vector_size(proc)) {
                pid_t processid = ((process * ) vector_get(proc, proc_counter))->pid;
                proc_info.pid = processid;
                proc_info.command = strdup(((process * ) vector_get(proc, proc_counter))->command);
                char procbuffer[30];
                sprintf(procbuffer, "/proc/%d/stat", processid);
                FILE * statusfile = fopen(procbuffer, "r");
                if (statusfile == NULL) {
                    proc_counter++;
                    continue;
                }
                char * status_info = NULL;
                size_t status_cap = 0;
				int gg = 0;
                gg = getline(&status_info, &status_cap, statusfile);
                sstring * stat_str = cstr_to_sstring(status_info);
                vector * statvector = sstring_split(stat_str, ' ');
                proc_info.state = ((char * ) vector_get(statvector, 2))[0];
                long int wtf;
                sscanf((char * ) vector_get(statvector, 19), "%ld", & wtf);
                proc_info.nthreads = wtf;
                unsigned long int tvm;
                sscanf((char * ) vector_get(statvector, 22), "%lu", & tvm);
                proc_info.vsize = tvm / 1024;
                unsigned long long startime;
                sscanf((char * ) vector_get(statvector, 21), "%llu", & startime);
                time_t startimepoch = startime / sysconf(_SC_CLK_TCK) + bootime;
                struct tm * tm_info;
                tm_info = localtime( & startimepoch);
                proc_info.start_str = malloc(30);
                time_struct_to_string(proc_info.start_str, 30, tm_info);;
                unsigned long runutime, runstime;
                sscanf((char * ) vector_get(statvector, 13), "%lu", & runutime);
                sscanf((char * ) vector_get(statvector, 14), "%lu", & runstime);
                time_t sum = (runutime + runstime) / sysconf(_SC_CLK_TCK);
                size_t minutes = sum / 60;
                size_t seconds = sum % 60;
                proc_info.time_str = malloc(30);
                execution_time_to_string(proc_info.time_str, 30, minutes, seconds);
                print_process_info( & proc_info);
                
				free(proc_info.start_str);
                free(proc_info.time_str);
                free(status_info);
                
				fclose(statusfile);
                proc_counter++;
            }
            vector_pop_back(proc);
            if (ps_idx == 1)
                to_run = 0;

        } else if (!strcmp((char * ) vector_get(splited_cmd, 0), "pfd")) {
            size_t pfd_idx = 0, pfd_cmd_size = 0;

            proc_counter = 0;
            if (vector_size(splited_cmd) != 2) {
                if (vector_size(splited_cmd) < 2) {
                    print_invalid_command(command);
                    exit(1);
                } else {
                    if (!strcmp((char * ) vector_get(splited_cmd, 2), "||") || !strcmp((char * ) vector_get(splited_cmd, 2), "&&") || ((char * ) vector_get(splited_cmd, 1))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] == ';') {
                        pre_run = 1;
                        char * pfd_new_cmd = NULL;
                        pfd_cmd_size += strlen((char * ) vector_get(splited_cmd, 0)) + strlen((char * ) vector_get(splited_cmd, 1)) + 2;
                        if (!strcmp((char * ) vector_get(splited_cmd, 2), "||")) {
                            pfd_idx = 1;
                            pfd_cmd_size += 3;
                            pfd_new_cmd = strdup(command + pfd_cmd_size);
                            to_run = 1;
                            free(command);
                            command = pfd_new_cmd;
                        } else if (!strcmp((char * ) vector_get(splited_cmd, 2), "&&")) {
                            pfd_idx = 2;
                            pfd_cmd_size += 3;
                            pfd_new_cmd = strdup(command + pfd_cmd_size);
                            to_run = 1;
                            free(command);
                            command = pfd_new_cmd;
                        } else {
                            pfd_idx = 3;
                            pfd_new_cmd = strdup(command + pfd_cmd_size);
                            to_run = 1;
                            free(command);
                            command = pfd_new_cmd;
                        }


                    } else {
                        print_invalid_command(command);
                        exit(1);
                    }
                }
            }

            if (pfd_idx == 3)
                ( * ((char ** ) vector_at(splited_cmd, 1)))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] = '\0';

            pid_t pid_lookup = atoi((char * ) vector_get(splited_cmd, 1));
            if (pid_lookup < 1) {
                print_no_process_found(pid_lookup);
                if (pfd_idx == 2) {
                    to_run = 0;
                    pre_run = 0;
                }
                continue;
            }
            size_t i = 0;
            char * pfd_buff;
            pfd_buff = malloc(30);
            while (1) {
                FILE * fd = NULL;
                char * buff;
                buff = malloc(50);
                int pfd_link_len = 0;
                sprintf(pfd_buff, "/proc/%d/fd/%d", pid_lookup, (int) i);
                pfd_link_len = readlink(pfd_buff, buff, 50);
                buff[pfd_link_len] = '\0';
                if (pfd_link_len == -1) {
                    if (pfd_idx == 2 && i == 0) {
                        to_run = 0;
                        pre_run = 0;
                    }
                    if (i == 0)
                        print_no_process_found(pid_lookup);
                    break;
                }
                sprintf(pfd_buff, "/proc/%d/fdinfo/%d", pid_lookup, (int) i);
                fd = fopen(pfd_buff, "r");
                char * posbuffer = malloc(40);
                size_t capacity = 40;
                size_t getnum = 0;
                getnum = getline( & posbuffer, & capacity, fd);
                size_t pos = atoi(posbuffer + 4);
                if (i == 0)
                    print_process_fd_info_header();

                print_process_fd_info(i, pos, buff);
                fclose(fd);
                i++;
                free(posbuffer);
                free(buff);
            }
            if (pfd_idx == 1 && i != 0)
                to_run = 0;

        } else if (!strcmp((char * ) vector_get(splited_cmd, 0), "kill")) {
            size_t kill_idx = 0, nkillcmdsiz = 0;

            proc_counter = 0;

            if (pre_run == 0)
                vector_push_back(history, command);
            else
                pre_run = 0;

            if (vector_size(splited_cmd) != 2) {
                if (vector_size(splited_cmd) < 2) {
                    print_invalid_command(command);
                    exit(1);
                } else {
                    if (!strcmp((char * ) vector_get(splited_cmd, 2), "||") || !strcmp((char * ) vector_get(splited_cmd, 2), "&&") || ((char * ) vector_get(splited_cmd, 1))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] == ';') {
                        pre_run = 1;
                        char * killnewcommand = NULL;
                        nkillcmdsiz += strlen((char * ) vector_get(splited_cmd, 0)) + strlen((char * ) vector_get(splited_cmd, 1)) + 2;
                        if (!strcmp((char * ) vector_get(splited_cmd, 2), "||")) {
                            kill_idx = 1;
                            nkillcmdsiz += 3;
                            killnewcommand = strdup(command + nkillcmdsiz);
                            to_run = 1;
                            free(command);
                            command = killnewcommand;
                        } else if (!strcmp((char * ) vector_get(splited_cmd, 2), "&&")) {
                            kill_idx = 2;
                            nkillcmdsiz += 3;
                            killnewcommand = strdup(command + nkillcmdsiz);
                            to_run = 1;
                            free(command);
                            command = killnewcommand;
                        } else {
                            kill_idx = 3;
                            killnewcommand = strdup(command + nkillcmdsiz);
                            to_run = 1;
                            free(command);
                            command = killnewcommand;
                        }


                    } else {
                        print_invalid_command(command);
                        exit(1);
                    }
                }
            }


            if (kill_idx == 3)
                ( * ((char ** ) vector_at(splited_cmd, 1)))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] = '\0';

            pid_t to_kill = (pid_t) atoi((char * ) vector_get(splited_cmd, 1));
            int killflag = 0;
            if (to_kill <= 0) {
                if (kill_idx == 2) {
                    to_run = 0;
                    pre_run = 0;
                }
                print_no_process_found(to_kill);

            } else {
                killflag = kill(to_kill, SIGTERM);
            }
            char * commandtoprint = NULL;
            if (!killflag) {
                while (proc_counter < vector_size(proc)) {
                    if (((process * ) vector_get(proc, proc_counter))->pid == to_kill) {
                        commandtoprint = ((process * ) vector_get(proc, proc_counter))->command;
                        break;
                    }
                    proc_counter++;
                }
                print_killed_process(to_kill, commandtoprint);
                if (kill_idx == 1)
                    to_run = 0;
            } else {
                if (kill_idx == 2) {
                    to_run = 0;
                    pre_run = 0;
                }
                print_no_process_found(to_kill);
            }

        } else if (!strcmp((char * ) vector_get(splited_cmd, 0), "cont")) {
            size_t contindex = 0, ncontcmdsiz = 0;

            proc_counter = 0;

            if (pre_run == 0)
                vector_push_back(history, command);
            else
                pre_run = 0;

            if (vector_size(splited_cmd) != 2) {
                if (vector_size(splited_cmd) < 2) {
                    print_invalid_command(command);
                    exit(1);
                } else {
                    if (!strcmp((char * ) vector_get(splited_cmd, 2), "||") || !strcmp((char * ) vector_get(splited_cmd, 2), "&&") || ((char * ) vector_get(splited_cmd, 1))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] == ';') {
                        pre_run = 1;
                        char * contnewcommand = NULL;
                        ncontcmdsiz += strlen((char * ) vector_get(splited_cmd, 0)) + strlen((char * ) vector_get(splited_cmd, 1)) + 2;
                        if (!strcmp((char * ) vector_get(splited_cmd, 2), "||")) {
                            contindex = 1;
                            ncontcmdsiz += 3;
                            contnewcommand = strdup(command + ncontcmdsiz);
                            to_run = 1;
                            free(command);
                            command = contnewcommand;
                        } else if (!strcmp((char * ) vector_get(splited_cmd, 2), "&&")) {
                            contindex = 2;
                            ncontcmdsiz += 3;
                            contnewcommand = strdup(command + ncontcmdsiz);
                            to_run = 1;
                            free(command);
                            command = contnewcommand;
                        } else {
                            contindex = 3;
                            contnewcommand = strdup(command + ncontcmdsiz);
                            to_run = 1;
                            free(command);
                            command = contnewcommand;
                        }


                    } else {
                        print_invalid_command(command);
                        exit(1);
                    }
                }
            }


            if (contindex == 3)
                ( * ((char ** ) vector_at(splited_cmd, 1)))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] = '\0';

            pid_t processtocont = (pid_t) atoi((char * ) vector_get(splited_cmd, 1));
            int contflag = 0;
            if (processtocont <= 0) {
                if (contindex == 2) {
                    to_run = 0;
                    pre_run = 0;
                }
                print_no_process_found(processtocont);

            } else {
                contflag = kill(processtocont, SIGCONT);
            }
            if (!contflag) {
                if (contindex == 1)
                    to_run = 0;
            } else {
                if (contindex == 2) {
                    to_run = 0;
                    pre_run = 0;
                }
                print_no_process_found(processtocont);
            }

        } else if (!strcmp((char * ) vector_get(splited_cmd, 0), "stop")) {
            size_t stopindex = 0, nstopcmdsiz = 0;
            if (pre_run == 0)
                vector_push_back(history, command);
            else
                pre_run = 0;
            proc_counter = 0;


            if (vector_size(splited_cmd) != 2) {
                if (vector_size(splited_cmd) < 2) {
                    print_invalid_command(command);
                    exit(1);
                } else {
                    if (!strcmp((char * ) vector_get(splited_cmd, 2), "||") || !strcmp((char * ) vector_get(splited_cmd, 2), "&&") || ((char * ) vector_get(splited_cmd, 1))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] == ';') {
                        pre_run = 1;
                        char * stopnewcommand = NULL;
                        nstopcmdsiz += strlen((char * ) vector_get(splited_cmd, 0)) + strlen((char * ) vector_get(splited_cmd, 1)) + 2;
                        if (!strcmp((char * ) vector_get(splited_cmd, 2), "||")) {
                            stopindex = 1;
                            nstopcmdsiz += 3;
                            stopnewcommand = strdup(command + nstopcmdsiz);
                            to_run = 1;
                            free(command);
                            command = stopnewcommand;
                        } else if (!strcmp((char * ) vector_get(splited_cmd, 2), "&&")) {
                            stopindex = 2;
                            nstopcmdsiz += 3;
                            stopnewcommand = strdup(command + nstopcmdsiz);
                            to_run = 1;
                            free(command);
                            command = stopnewcommand;
                        } else {
                            stopindex = 3;
                            stopnewcommand = strdup(command + nstopcmdsiz);
                            to_run = 1;
                            free(command);
                            command = stopnewcommand;
                        }


                    } else {
                        print_invalid_command(command);
                        exit(1);
                    }
                }
            }


            if (stopindex == 3)
                ( * ((char ** ) vector_at(splited_cmd, 1)))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] = '\0';

            pid_t processtostop = (pid_t) atoi((char * ) vector_get(splited_cmd, 1));
            int stopflag = 0;
            if (processtostop <= 0) {
                if (stopindex == 2) {
                    to_run = 0;
                    pre_run = 0;
                }
                print_no_process_found(processtostop);

            } else {
                stopflag = kill(processtostop, SIGTSTP);
            }
            char * commandtoprint = NULL;
            if (!stopflag) {
                while (proc_counter < vector_size(proc)) {
                    if (((process * ) vector_get(proc, proc_counter))->pid == processtostop) {
                        commandtoprint = ((process * ) vector_get(proc, proc_counter))->command;
                        break;
                    }
                    proc_counter++;
                }
                print_stopped_process(processtostop, commandtoprint);
                if (stopindex == 1)
                    to_run = 0;
            } else {
                if (stopindex == 2) {
                    to_run = 0;
                    pre_run = 0;
                }
                print_no_process_found(processtostop);
            }

        } else if (!strcmp((char * ) vector_get(splited_cmd, 0), "cd")) {
            size_t cdindex = 0, ncmdsiz = 0;
            if (pre_run == 0)
                vector_push_back(history, command);
            else
                pre_run = 0;
            if (vector_size(splited_cmd) != 2) {
                if (vector_size(splited_cmd) < 2) {
                    print_invalid_command(command);
                    exit(1);
                } else {
                    if (!strcmp((char * ) vector_get(splited_cmd, 2), "||") || !strcmp((char * ) vector_get(splited_cmd, 2), "&&") || ((char * ) vector_get(splited_cmd, 1))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] == ';') {
                        pre_run = 1;
                        char * cdnewcommand = NULL;
                        ncmdsiz += strlen((char * ) vector_get(splited_cmd, 0)) + strlen((char * ) vector_get(splited_cmd, 1)) + 2;
                        if (!strcmp((char * ) vector_get(splited_cmd, 2), "||")) {
                            cdindex = 1;
                            ncmdsiz += 3;
                            cdnewcommand = strdup(command + ncmdsiz);
                            to_run = 1;
                            free(command);
                            command = cdnewcommand;
                        } else if (!strcmp((char * ) vector_get(splited_cmd, 2), "&&")) {
                            cdindex = 2;
                            ncmdsiz += 3;
                            cdnewcommand = strdup(command + ncmdsiz);
                            to_run = 1;
                            free(command);
                            command = cdnewcommand;
                        } else {
                            cdindex = 3;
                            cdnewcommand = strdup(command + ncmdsiz);
                            to_run = 1;
                            free(command);
                            command = cdnewcommand;
                        }


                    } else {
                        print_invalid_command(command);
                        exit(1);
                    }
                }
            }
            if (cdindex == 3)
                ( * ((char ** ) vector_at(splited_cmd, 1)))[strlen((char * ) vector_get(splited_cmd, 1)) - 1] = '\0';
            int chidirflag = chdir((char * ) vector_get(splited_cmd, 1));
            if (chidirflag == -1) {
                print_no_directory((char * ) vector_get(splited_cmd, 1));
                if (cdindex == 2) {
                    to_run = 0;
                    pre_run = 0;
                }
            } else {
                free(cwd);
                cwd = NULL;
                cwd = getcwd(cwd, dir_size);
                if (cdindex == 1)
                    to_run = 0;
            }
        } else if (((char * ) vector_get(splited_cmd, 0))[0] == '!') {
            if (strlen((char * ) vector_get(splited_cmd, 0)) == 1) {
                if (vector_size(history) == 0) {
                    print_no_history_match();
                    continue;
                } else {
                    free(command);
                    to_run = 1;
                    command = strdup( * (char ** ) vector_back(history));
                    continue;
                }
            } else {
                char * substring = command + 1;
                size_t len = strlen(substring);
                int checkinghistroy = 0;
                int i = vector_size(history) - 1;
                sstring * his = NULL;
                char * substringinhistory = NULL;
                while (i != 0) {
                    if (strlen((char * ) vector_get(history, i)) < len) {
                        i--;
                        continue;
                    } else {
                        his = cstr_to_sstring((char * ) vector_get(history, i));
                        substringinhistory = sstring_slice(his, 0, len);
                        if (!strcmp(substringinhistory, substring)) {
                            checkinghistroy = 1;
                            sstring_destroy(his);
                            free(substringinhistory);
                            break;
                        }
                        sstring_destroy(his);
                        free(substringinhistory);
                    }
                    i--;
                }
                if (checkinghistroy == 1) {
                    free(command);
                    to_run = 1;
                    command = strdup((char * ) vector_get(history, i));
                    continue;
                } else {
                    his = cstr_to_sstring((char * ) vector_get(history, i));
                    substringinhistory = sstring_slice(his, 0, len);
                    if (!strcmp(substringinhistory, substring)) {
                        free(command);
                        to_run = 1;
                        command = strdup((char * ) vector_get(history, i));
                        sstring_destroy(his);
                        free(substringinhistory);
                        continue;
                    } else {
                        print_no_history_match();
                        sstring_destroy(his);
                        free(substringinhistory);
                        continue;
                    }
                }
            }

        } else if (((char * ) vector_get(splited_cmd, 0))[0] == '#') {
            int histroyindex = atoi(command + 1);
            if (histroyindex < 0) {
                print_invalid_index();
            } else if ((size_t) histroyindex < vector_size(history)) {
                free(command);
                command = strdup((char * ) vector_get(history, histroyindex));
                to_run = 1;
                continue;
            } else {
                print_invalid_index();
            }
        } else {
            int wait_flag = 0;
            size_t vec_iter = 0;
            int status = 0;
            int logic = 0;
            size_t commandsize = 0;
            while (vec_iter < vector_size(splited_cmd)) {
                commandsize += strlen((char * ) vector_get(splited_cmd, vec_iter)) + 1;
                if (!strcmp((char * ) vector_get(splited_cmd, vec_iter), "||") || !strcmp((char * ) vector_get(splited_cmd, vec_iter), "&&") || ((char * ) vector_get(splited_cmd, vec_iter))[strlen((char * ) vector_get(splited_cmd, vec_iter)) - 1] == ';') {

                    if (!strcmp((char * ) vector_get(splited_cmd, vec_iter), "||"))
                        logic = 1;
                    else if (!strcmp((char * ) vector_get(splited_cmd, vec_iter), "&&"))
                        logic = 2;
                    else
                        logic = 3;

                    to_run = 1;
                    break;
                }
                vec_iter++;
            }
            if (!logic) {
                if (!strcmp((char * ) vector_get(splited_cmd, vec_iter - 1), "&")) {
                    wait_flag = 1;
                } else if (((char * ) vector_get(splited_cmd, vec_iter - 1))[strlen((char * ) vector_get(splited_cmd, vec_iter - 1)) - 1] == '&') {
                    wait_flag = 2;
                }
            }
            if (wait_flag == 0) {
                if (logic == 3) {
                    ( * ((char ** ) vector_at(splited_cmd, vec_iter)))[strlen((char * ) vector_get(splited_cmd, vec_iter)) - 1] = '\0';
                    vector_insert(splited_cmd, vec_iter + 1, NULL);
                } else
                    vector_insert(splited_cmd, vec_iter, NULL);
            } else {
                if (wait_flag == 1) {
                    vector_insert(splited_cmd, vec_iter - 1, NULL);
                    vector_erase(splited_cmd, vec_iter);
                } else if (wait_flag == 2) {
                    ( * ((char ** ) vector_at(splited_cmd, vec_iter - 1)))[strlen((char * ) vector_get(splited_cmd, vec_iter - 1)) - 1] = '\0';
                    vector_insert(splited_cmd, vec_iter, NULL);

                }
            }
            char * tempcommand = strdup(command);
            char * commandtopush = strdup(command);
            if (logic == 1 || logic == 2) {
                tempcommand[commandsize - 4] = '\0';

            } else if (logic == 3) {
                tempcommand[commandsize - 2] = '\0';
            } else {
                tempcommand[commandsize - 1] = '\0';
            }
            if (logic) {
                char * newcommand = NULL;
                if (logic == 1) {
                    newcommand = strdup(command + commandsize);
                    free(command);
                    command = newcommand;
                }
                if (logic == 2) {
                    newcommand = strdup(command + commandsize);
                    free(command);
                    command = newcommand;
                }
                if (logic == 3) {
                    newcommand = strdup(command + commandsize);
                    free(command);
                    command = newcommand;
                }





            }
            process curp;
            curp.command = strdup(tempcommand);
            curp.pid = 0;
            curr_pid = 0;
            pid_t pid = fork(), waitflag = 0;
            if (pid > 0) {
                curp.pid = pid;
                if (wait_flag)
                    vector_push_back(proc, & curp);

                if (setpgid(pid, pid) == -1) {
                    print_setpgid_failed();
                    exit(1);
                }
                if (!wait_flag) {
                    curr_pid = pid;
                    waitflag = waitpid(pid, & status, 0);
                    if (logic == 1) {
                        if (WIFEXITED(status)) {
                            if (!WEXITSTATUS(status))
                                to_run = 0;
                        }
                    }
                    if (logic == 2) {
                        if (!WIFEXITED(status)) {
                            to_run = 0;
                        } else {
                            if (WEXITSTATUS(status))
                                to_run = 0;
                        }
                    }
                    if (pre_run == 0) {
                        vector_push_back(history, commandtopush);
                    } else {
                        pre_run = 0;
                    }
                    if (to_run != 0) {
                        pre_run = 1;
                    }
                    if (waitflag < 0) {
                        print_wait_failed();
					}
                } else {
                    vector_push_back(history, commandtopush);
                    continue;
                }

            } else if (pid < 0) {
                print_fork_failed();
                exit(1);
            } else {
                if (hist_file) {
                    fclose(hist_file);
				}
                if (stream != stdin) {
                    fclose(stream);
				}
                print_command_executed(getpid());

                execvp((char * ) vector_get(splited_cmd, 0), (char ** ) vector_front(splited_cmd));
                print_exec_failed(tempcommand);
                exit(1);
            }
            free(tempcommand);
            free(curp.command);
        }

    }
    return 0;
}

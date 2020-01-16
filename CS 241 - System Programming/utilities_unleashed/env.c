/**
 * Utilities Unleashed
 * CS 241 - Fall 2019
 */

#include "format.h"
 
#include<time.h> 
#include<sys/wait.h>
#include<sys/types.h>
#include<unistd.h>
#include<stdlib.h> 
#include<string.h> 


int main(int argc, char *argv[]) {
	
	int cmd_count = 0;
	while(cmd_count < argc) {
		if(!strcmp(argv[cmd_count],"--")) {
			break;
		}
		cmd_count++;
	}
  
	// minimum # of args must == 3, or it's an user error
	// "./env -- command"
	if(argc < 3 || cmd_count == argc) {
		print_env_usage();
		exit(1);
	}
	//ready to fork...
	pid_t pid = fork();
	
	if(pid < 0) {
		// parent process, but fork failed
		print_fork_failed();
		exit(0);
		
	} else if (pid > 0) {
		// parent proc
		int status;
		waitpid(pid, &status, 0);
		return 0;
		
	} else {
		// pid == 0, child
		int i = 0;
		while(i < cmd_count){
			int cmd_len = strlen(argv[i]);
			int j = 0;
			while(j < cmd_len + 1){
				if(argv[i][j] == '=') {
					argv[i][j] = '\0';
					break;
				}
				j++;
			}
			char* key = strdup(argv[i]);
			char* value = argv[i] + j + 1;
			
			// set env vars
			if(argv[i][j + 1] == '%') {
				value = getenv(argv[i] + j + 2);
				if(value == NULL){
					free(key);
					print_environment_change_failed();
					exit(1);
				}
			}
			int set_env = setenv(key, value, 1);
			free(key);
			if(set_env == -1) {
				print_environment_change_failed();
				exit(1);
			}
			i++;
		}
		execvp(argv[cmd_count + 1],argv + cmd_count + 1);
		print_exec_failed();
		exit(1);
	} 
} 

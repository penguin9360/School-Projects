/**
 * Utilities Unleashed
 * CS 241 - Fall 2019
 */
 
 #include <unistd.h>
 #include <time.h>
 #include "format.h"
 #include <sys/wait.h>
 #include <sys/types.h> 
 #include <stdlib.h>
 

int main(int argc, char *argv[]) { 
	if (argc < 2) {
		print_time_usage();
		exit(1);
	}
	struct timespec tp;
	clock_gettime(CLOCK_MONOTONIC, &tp);
	pid_t pid = fork();
	if (pid < 0) { // fork failure
		print_fork_failed();
		exit(1);
	} else if (pid > 0) {
		// parent process
		int status;
		waitpid(pid, &status, 0);
		
		if (!WIFEXITED(status) || WEXITSTATUS(status) == 42) {
			print_exec_failed();
			exit(1);
		}
		
		//if (WEXITSTATUS(status) == 42) {
		//	print_exec_failed();
		//}
		
		struct timespec tpgg;
		clock_gettime(CLOCK_MONOTONIC, &tpgg);
		double tv = 0;
		tv = (double) tpgg.tv_sec - tp.tv_sec + (((double)tpgg.tv_nsec - tp.tv_nsec)/1000000000);
		display_results(argv, tv);
		return 0;
		
	} else { // pid == 0
		// child process
		execvp(argv[1], &argv[1]);
		return 42;
		exit(1); // For safety.
	}
}

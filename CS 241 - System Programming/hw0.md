Homework 0
----------

### Watch the videos and write up your answers to the following questions

**Important!**

The virtual machine-in-your-browser and the videos you need for HW0 are here:

<http://cs-education.github.io/sys/>

Questions? Comments? Use Piazza: <https://piazza.com/illinois/fall2019/cs241>

The in-browser virtual machine runs entirely in Javascript and is fastest in Chrome. Note the VM and any code you write is reset when you reload the page, **so copy your code to a separate document.** The post-video challenges are not part of homework 0 but you learn the most by doing rather than just passively watching - so we suggest you have some fun with each end-of-video challenge.

HW0 questions are below. Please use this document to write the answers. This will be hand graded.

### Chapter 1

In which our intrepid hero battles standard out, standard error, file descriptors and writing to files

1.  **Hello, World! (system call style)** Write a program that uses `write()` to print out “Hi! My name is &lt;Your Name&gt;”.

```c
<<<<<<< HEAD
#include <unistd.h>

int main() {
	write(1, "Hi! My name is Pengxu Zheng", 50);
	return 0;
}
=======
// Your code here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

2.  **Hello, Standard Error Stream!** Write a function to print out a triangle of height `n` to standard error. Your function should have the signature `void write_triangle(int n)` and should use `write()`. The triangle should look like this, for n = 3:

```
*
**
***
```

```c
<<<<<<< HEAD
#include <unistd.h>

int main() {
	write_triangle(5);
	return 0;
}

void write_triangle(int n) {
	int len, i;
	char s[n + 1];
	for (i = 0; i < n; i++) {
		s[i] = '*';
	}
	for(len = 0; len <= n ; len++) {
		write(2, s, len);
		write(2,"\n", 6);
	}
}
=======
// Your code here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

3.  **Writing to files** Take your program from “Hello, World!” modify it write to a file called `hello_world.txt`. Make sure to to use correct flags and a correct mode for `open()` (`man 2 open` is your friend).

```c
<<<<<<< HEAD
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
int main() {
	mode_t mode = S_IRUSR | S_IWUSR;
	int fildes = open("hello_world.txt", O_CREAT | O_TRUNC | O_RDWR, mode);
	write(fildes, "Hi! My name is Pengxu Zheng \n", 50);
	close(fildes);
	return 0;
}
=======
// Your code here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

5. **Not everything is a system call** Take your program from “Writing to files” and replace `write()` with `printf()`. *Make sure to print to the file instead of standard out!*

```c
<<<<<<< HEAD
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
int main() {
	mode_t mode = S_IRUSR | S_IWUSR;
	close(1);
	int fildes = open("hello_world.txt", O_CREAT | O_TRUNC | O_RDWR, mode);
	if (fildes == -1) {
		perror("open failed");
		exit(1);
	}
	printf("fildes is %d\n", fildes);
	write(fildes, "Hi! My name is Pengxu Zheng \n", 50);
	close(fildes);
	return 0;
}
=======
// Your code here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

6.  What are some differences between `write()` and `printf()`?

```c
<<<<<<< HEAD
write() is the most basic system call that allows the user to write anything in the format of bytes of data; printf() is a function - not a system call - that calls write() when the user wants to write data in multiple formats. 
=======
// Your code here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

### Chapter 2

Sizing up C types and their limits, `int` and `char` arrays, and incrementing pointers

1.  How many bits are there in a byte?

```c
<<<<<<< HEAD
8
=======
// Your answer here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

2.  How many bytes are there in a `char`?

```c
<<<<<<< HEAD
for ASCII encoding, 1 byte 
=======
// Your answer here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

3.  How many bytes the following are on your machine? 

<<<<<<< HEAD
* `int`: 4
* `double`: 8
* `float`:4
* `long`:8
* `long long`: 8
=======
* `int`: 
* `double`: 
* `float`:
* `long`:
* `long long`: 
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c

4.  On a machine with 8 byte integers, the declaration for the variable `data` is `int data[8]`. If the address of data is `0x7fbd9d40`, then what is the address of `data+2`?

```c
<<<<<<< HEAD
0x7fbd9d42
=======
// Your answer here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

5.  What is `data[3]` equivalent to in C? Hint: what does C convert `data[3]` to before dereferencing the address? Remember, the type of a string constant `abc` is an array.

```c
<<<<<<< HEAD
*(data + 3), 3[data];
=======
// Your answer here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

6.  Why does this segfault?

```c
char *ptr = "hello";
*ptr = 'J';
<<<<<<< HEAD
ptr is read-only; it crashes when it's assigned values without malloc();
=======
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

7.  What does `sizeof("Hello\0World")` return?

```c
<<<<<<< HEAD
12
=======
// Your answer here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

8.  What does `strlen("Hello\0World")` return?

```c
<<<<<<< HEAD
5
=======
// Your answer here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

9.  Give an example of X such that `sizeof(X)` is 3.

```c
<<<<<<< HEAD
char X[3];
=======
// Your code here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

10. Give an example of Y such that `sizeof(Y)` might be 4 or 8 depending on the machine.

```c
<<<<<<< HEAD
char * Y;
=======
// Your code here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

### Chapter 3

Program arguments, environment variables, and working with character arrays (strings)

1.  What are two ways to find the length of `argv`?
<<<<<<< HEAD
```c
int count = 0; 
while(argv[++count] != NULL); // count will have the length of argv once the loop finishes
```

2.  What does `argv[0]` represent?
```c
The exceution name of the program, "./program".
```

3.  Where are the pointers to environment variables stored (on the stack, the heap, somewhere else)?
```c
stack or heap
```
=======

2.  What does `argv[0]` represent?

3.  Where are the pointers to environment variables stored (on the stack, the heap, somewhere else)?
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c

4.  On a machine where pointers are 8 bytes, and with the following code:

    ``` c
    char *ptr = "Hello";
    char array[] = "Hello";
    ```
<<<<<<< HEAD
 
    What are the values of `sizeof(ptr)` and `sizeof(array)`? Why?

```c
sizeof(ptr) = 8, sizeof(array) = 6
sizeof(ptr) is the size of a pointer which is 8B; sizeof(array) is the size of all charaters, including the null byte at the end, so the total is 6 bytes. 
```

5.  What data structure manages the lifetime of automatic variables?
```c
stack
```
=======

    What are the values of `sizeof(ptr)` and `sizeof(array)`? Why?

```c
// Your answer here
```

5.  What data structure manages the lifetime of automatic variables?
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c

### Chapter 4

Heap and stack memory, and working with structs

1.  If I want to use data after the lifetime of the function it was created in ends, where should I put it? How do I put it there?
<<<<<<< HEAD
```c
put it on heap. use 'static' or malloc().
```

2.  What are the differences between heap and stack memory?
```c
stack mainly stores local variables used inside of a function; heap mainly stores programmer-defined variables by malloc(), mostly global variables.
```

3.  Are there other kinds of memory in a process?
```c
yes, like text segment
```

4.  Fill in the blank: “In a good C program, for every malloc, there is a \free\_\_”.

5.  What is one reason `malloc` can fail?
```c
if the memory management data stucture corrupted, malloc could fail due to buffer overflow. 
```

6.  What are some differences between `time()` and `ctime()`?
```c
time() gives seconds since 1970;
ctime() uses static storage; 
ctime() is ASCII-based and human-readable. 
```
=======

2.  What are the differences between heap and stack memory?

3.  Are there other kinds of memory in a process?

4.  Fill in the blank: “In a good C program, for every malloc, there is a \_\_\_”.

5.  What is one reason `malloc` can fail?

6.  What are some differences between `time()` and `ctime()`?
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c

7.  What is wrong with this code snippet?

``` c
free(ptr);
free(ptr);
```

<<<<<<< HEAD
``` c
it frees the same pointer twice, which could possibly corrupt another process's data structure.
```

=======
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
8.  What is wrong with this code snippet?

``` c
free(ptr);
printf("%s\n", ptr);
```
<<<<<<< HEAD
``` c
it uses a pointer that is already freed. It could cause unpredictable problems. 
```

9.  How can one avoid the previous two mistakes?
``` c
only free a pointer once at a time, and never use a free'd pointer. 
```
=======

9.  How can one avoid the previous two mistakes?

>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c

10. Use the following space for the next four questions

```c
// 10
<<<<<<< HEAD
Struct Person {
	char* name;
	int age;
	struct Person* friends[];
};
typedef struct Person person;

// 12

person * Agent_Smith = (person*) malloc(sizeof(person));
Agent_smith -> name = 'Agent Smith';
Agent_smith -> age = 128;
Agent_smith -> friends = Sonny_Moore;

person * Sonny_Moore = (person*) malloc(sizeof(person));
Sonny_Moore -> name = 'Sonny Moore';
Sonny_Moore -> age = 256;
Sonny_Moore -> friends = Agent_smith;

// 13
person * create(char* aname, int anage) {
	person* newPerson = (person*) malloc(sizeof(person) * 11);
	newPerson -> name = aname;
	newPerson -> age = anage;
	newPerson -> friends = NULL;
	return newPerson;
}

void destroy(person* p) {
	free(p -> name);
	free(p -> age);
	free(p -> friends);
	memset(p, 0, sizeof(person));
	free(p);
}

int main() {
// 11
	return 0;
=======

// 12

// 13

int main() {
// 11
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
}
```

* Create a `struct` that represents a `Person`. Then make a `typedef`, so that `struct Person` can be replaced with a single word. A person should contain the following information: their name (a string), their age (an integer), and a list of their friends (stored as a pointer to an array of pointers to `Person`s). 

*  Now, make two persons on the heap, “Agent Smith” and “Sonny Moore”, who are 128 and 256 years old respectively and are friends with each other. Create functions to create and destroy a Person (Person’s and their names should live on the heap).

* `create()` should take a name and age. The name should be copied onto the heap. Use malloc to reserve sufficient memory for everyone having up to ten friends. Be sure initialize all fields (why?).

* `destroy()` should free up not only the memory of the person struct, but also free all of its attributes that are stored on the heap. Destroying one person should not destroy any others.


### Chapter 5

Text input and output and parsing using `getchar`, `gets`, and `getline`.

1.  What functions can be used for getting characters from `stdin` and writing them to `stdout`?
<<<<<<< HEAD
getchar(), putchar()

2.  Name one issue with `gets()`.
gets() can't distinguish if the given address is valid, could cause buffer overflow. 
=======

2.  Name one issue with `gets()`.
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c

3.  Write code that parses the string “Hello 5 World” and initializes 3 variables to “Hello”, 5, and “World”.

```c
<<<<<<< HEAD
#include <stdio.h>

int main()
{
    char * data = "Hello 5 World";
    char buffer[20];
    int gg = 0;
    char buffer2[20];
    
    sscanf(data, "%s %d %s", buffer, & gg, buffer2);
    printf("%s, %d, %s", buffer, gg, buffer2);
    
    return 0;
}

```

4.  What does one need to define before including `getline()`?
#define _GNU_SOURCE
=======
// Your code here
```

4.  What does one need to define before including `getline()`?
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c

5.  Write a C program to print out the content of a file line-by-line using `getline()`.

```c
<<<<<<< HEAD
#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>

int main() {
	char * buffer = NULL;
	size_t capacity = 0;
	ssize_t result = getline(&buffer, &capacity, stdin);
	if (result > 0 && buffer[result - 1] == '\n') {
		buffer[result - 1] = 0;
	}
	printf("%d : %s\n:, result, buffer);
	free(buffer); 
	return 0;
}
=======
// Your code here
>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c
```

### C Development

These are general tips for compiling and developing using a compiler and git. Some web searches will be useful here


1.  What compiler flag is used to generate a debug build?
<<<<<<< HEAD
gcc -g
=======

>>>>>>> 90604809f7c544cd428ac5b8407e66436982512c

2.  You fix a problem in the Makefile and type `make` again. Explain why this may be insufficient to generate a new build.


3.  Are tabs or spaces used to indent the commands after the rule in a Makefile?


4.  What does `git commit` do? What’s a `sha` in the context of git?


5.  What does `git log` show you?


6.  What does `git status` tell you and how would the contents of `.gitignore` change its output?


7.  What does `git push` do? Why is it not just sufficient to commit with `git commit -m ’fixed all bugs’ `?


8.  What does a non-fast-forward error `git push` reject mean? What is the most common way of dealing with this?


### Optional: Just for fun

-   Convert your a song lyrics into System Programming and C code covered in this wiki book and share on Piazza.

-   Find, in your opinion, the best and worst C code on the web and post the link to Piazza.

-   Write a short C program with a deliberate subtle C bug and post it on Piazza to see if others can spot your bug.

-   Do you have any cool/disastrous system programming bugs you’ve heard about? Feel free to share with your peers and the course staff on piazza.

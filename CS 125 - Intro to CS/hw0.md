Homework 0
----------

### Watch the videos and write up your answers to the following questions

**Important!**

The virtual machine-in-your-browser and the videos you need for HW0 are here:

<http://cs-education.github.io/sys/>

Questions? Comments? Use Piazza: <https://piazza.com/illinois/spring2019/cs241>

The in-browser virtual machine runs entirely in Javascript and is fastest in Chrome. Note the VM and any code you write is reset when you reload the page, **so copy your code to a separate document.** The post-video challenges are not part of homework 0 but you learn the most by doing rather than just passively watching - so we suggest you have some fun with each end-of-video challenge.

HW0 questions are below. Please use this document to write the answers. This will be hand graded.

### Chapter 1

In which our intrepid hero battles standard out, standard error, file descriptors and writing to files

1.  **Hello, World! (system call style)** Write a program that uses `write()` to print out “Hi! My name is &lt;Your Name&gt;”.

```c
// Your code here
#include <stdio.h>
#include <unistd.h>
int main() {
	write(1,"Hi,My name is Xingjian Qi",25);
	return 0;
}
```

2.  **Hello, Standard Error Stream!** Write a function to print out a triangle of height `n` to standard error. Your function should have the signature `void write_triangle(int n)` and should use `write()`. The triangle should look like this, for n = 3:

```
*
**
***
```

```c
// Your code here
void write_triangle(int n);
void write_triangle(int n){
	int i,j;
	for(j=0;j<n;j++)
	{
		for(i=0;i<j+1;i++)
		{
			write(2,"*",2);
		}
		write(2,"\n",2);
	}
}

```

3.  **Writing to files** Take your program from “Hello, World!” modify it write to a file called `hello_world.txt`. Make sure to to use correct flags and a correct mode for `open()` (`man 2 open` is your friend).

```c
// Your code here
#include <stdio.h>
#include <unistd.h>
int main() {
	mode_t mode=S_IRUSR|S_IWUSR;
	int fileflag = open("hello_world.txt",O_CREAT | O_TRUNC | O_RDWR, mode);
	write(fileflag,"Hi,My name is Xingjian Qi\n",27);
	close(fileflag);
	return 0;
}
```

5. **Not everything is a system call** Take your program from “Writing to files” and replace `write()` with `printf()`. *Make sure to print to the file instead of standard out!*

```c
// Your code here
#include <stdio.h>
#include <unistd.h>
int main() {
	close(1);
	mode_t mode=S_IRUSR|S_IWUSR;
	int fileflag = open("hello_world.txt",O_CREAT | O_TRUNC | O_RDWR, mode);
	printf("Hi,My name is Xingjian Qi\n");
	close(fileflag);
	return 0;
}
```

6.  What are some differences between `write()` and `printf()`?

```c
// Your code here
printf() is not a system call but write() is.
printf() is writing the context into a buffer instead of output immediately but write will.
```

### Chapter 2

Sizing up C types and their limits, `int` and `char` arrays, and incrementing pointers

1.  How many bits are there in a byte?

```c
// Your answer here
at least 8 bits
```

2.  How many bytes are there in a `char`?

```c
// Your answer here
1
```

3.  How many bytes the following are on your machine?

* `int`:4
* `double`:8
* `float`:4
* `long`:4
* `long long`:8

4.  On a machine with 8 byte integers, the declaration for the variable `data` is `int data[8]`. If the address of data is `0x7fbd9d40`, then what is the address of `data+2`?

```c
// Your answer here
0x7fbd9d50
```

5.  What is `data[3]` equivalent to in C? Hint: what does C convert `data[3]` to before dereferencing the address? Remember, the type of a string constant `abc` is an array.

```c
// Your answer here
data[3] is equivalent to *(data+3)
```

6.  Why does this segfault?

```c
char *ptr = "hello";
*ptr = 'J';

This segfault because that the string literal is stored in the read-only memory so any attempt
to write the memory could cause segfault.
```

7.  What does `sizeof("Hello\0World")` return?

```c
// Your answer here
12
```

8.  What does `strlen("Hello\0World")` return?

```c
// Your answer here
5
```

9.  Give an example of X such that `sizeof(X)` is 3.

```c
// Your code here
"hi"
```

10. Give an example of Y such that `sizeof(Y)` might be 4 or 8 depending on the machine.

```c
// Your code here
int
```

### Chapter 3

Program arguments, environment variables, and working with character arrays (strings)

1.  What are two ways to find the length of `argv`?

```c
// Your answer here
1.the length of argv is argc+1
2.use a loop
  int i=0;
  while(argv[i]!=NULL){i++;}
after this loop the value of i is the length of argv.
```
2.  What does `argv[0]` represent?
```c
// Your answer here
the execution name of the program.
```
3.  Where are the pointers to environment variables stored (on the stack, the heap, somewhere else)?

```c
// Your answer here
above the stack together with command line arguments
```
4.  On a machine where pointers are 8 bytes, and with the following code:

    ``` c
    char *ptr = "Hello";
    char array[] = "Hello";
    ```

    What are the values of `sizeof(ptr)` and `sizeof(array)`? Why?

```c
// Your answer here
1.sizeof(ptr) is 8 because ptr is a pointer, so sizeof(ptr) is equivalent to sizeof(char*) and the pointer is 8 bytes.
2.sizeof(array) is 6 because sizeof(array) will calculate all the memory that the array[] takes and every char needs 1 bytes. "Hello" with a hidden "\0" will take 6 bytes in total.
```

5.  What data structure manages the lifetime of automatic variables?
```c
// Your answer here
stack
```

### Chapter 4

Heap and stack memory, and working with structs

1.  If I want to use data after the lifetime of the function it was created in ends, where should I put it? How do I put it there?
```c
// Your answer here
Heap
use malloc() function
```

2.  What are the differences between heap and stack memory?
```c
// Your answer here
data stored in heap will exist after the function ends. and can be access globally.
stack memory is for the local variable which is destroyed after the function go out of their scope.
```
3.  Are there other kinds of memory in a process?
```c
// Your answer here
yes
```
4.  Fill in the blank: “In a good C program, for every malloc, there is a \_\_\_”.
```c
// Your answer here
free
```
5.  What is one reason `malloc` can fail?
```c
// Your answer here
there is no enough memory to match the size user requires
```
6.  What are some differences between `time()` and `ctime()`?
```c
// Your answer here
time() returns seconds since 1.1.1970
but ctime() will return a readable string to denote the current ctime
```
7.  What is wrong with this code snippet?

``` c
free(ptr);
free(ptr);
```
```c
// Your answer here
after the first free() ptr is still pointing to the same memory that is no longer valid for this process, as a result, the second free() will cause the program crash or have some undefined behavior.
```

8.  What is wrong with this code snippet?

``` c
free(ptr);
printf("%s\n", ptr);
```
```c
// Your answer here
after freeing the pointer, the memory that the pointer points to is no longer
valid, as a result, the printf may not print the expected data.
```
9.  How can one avoid the previous two mistakes?

```c
// Your answer here
after freeing a pointer, set the value of the pointer to NULL
```
10. Use the following space for the next four questions

```c
// 10
#include <string.h>
#include <stdlib.h>
#include <stdio.h>

struct Person{
	char* name;
	int age;
	struct Person ** friends;
};
typedef struct Person person;
// 12

// 13
person * create(char* ,int);
void destroy(person* p);

int main() {
	// 11
	person* simth= create("Agent Smith",128);
	person* sonny= create("Sonny Moore",256);
	destroy(simth);
	destroy(sonny);

	return 0;
}
person* create(char* n,int a){
	person* ptr = malloc(sizeof(person));
	ptr->name = malloc(strlen(n)+1);
	strcpy(ptr->name,n);
	ptr->age=a;
	ptr->friends=calloc(10,sizeof(person*));
	return ptr;
}
void destroy(person* p){
	free(p->name);
	int i;
	for(i=0;i<10;i++)
	{
		free(p->friends[i]);
	}
	free(p->friends);
  free(p);
}

the reason to initialize all fields is when destroy the struct we need to free all the friends,
however, if a person have less than 10 friends, the uncertain data stored in the array that "friends" points to may be recognized as an address and call free on that incorrect address will cause problems.
```

* Create a `struct` that represents a `Person`. Then make a `typedef`, so that `struct Person` can be replaced with a single word. A person should contain the following information: their name (a string), their age (an integer), and a list of their friends (stored as a pointer to an array of pointers to `Person`s).

*  Now, make two persons on the heap, “Agent Smith” and “Sonny Moore”, who are 128 and 256 years old respectively and are friends with each other. Create functions to create and destroy a Person (Person’s and their names should live on the heap).

* `create()` should take a name and age. The name should be copied onto the heap. Use malloc to reserve sufficient memory for everyone having up to ten friends. Be sure initialize all fields (why?).

* `destroy()` should free up not only the memory of the person struct, but also free all of its attributes that are stored on the heap. Destroying one person should not destroy any others.


### Chapter 5

Text input and output and parsing using `getchar`, `gets`, and `getline`.

1.  What functions can be used for getting characters from `stdin` and writing them to `stdout`?

```c
// Your code here
gets() getline() scanf() can get characters from "stdin"
puts() printf() write() can write characters to "stdout"
```
2.  Name one issue with `gets()`.
```c
// Your code here
gets() won not check the length of the input and just directly stored it in to the destination which can cause overflow and damage other data.
```
3.  Write code that parses the string “Hello 5 World” and initializes 3 variables to “Hello”, 5, and “World”.

```c
// Your code here
#include <stdarg.h>
#include <stdio.h>

int main(){
	char* s="Hello 5 World";
	char a[10],c[10];
	int b;
	sscanf(s,"%s %d %s",a,&b,c );
	return 0;
}
```

4.  What does one need to define before including `getline()`?
```c
// Your code here
#define _GNU_SOURCE      
#include <stdio.h>
```

5.  Write a C program to print out the content of a file line-by-line using `getline()`.

```c
// Your code here
//assuming content.txt is the input file.
#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
int main(){
  char* buffer=NULL;
  size_t capacity=0;
  FILE* file=fopen("content.txt", "r");
  ssize_t result=1;
  while((result= getline(&buffer, &capacity, file))>0)
  {
    printf ("%s", buffer);
  }

  free(buffer);
  return 0;
}
```

### C Development

These are general tips for compiling and developing using a compiler and git. Some web searches will be useful here


1.  What compiler flag is used to generate a debug build?

```c
// Your code here
-g
```

2.  You fix a problem in the Makefile and type `make` again. Explain why this may be insufficient to generate a new build.

```c
// Your code here
because 'make' uses time stamp on the source files to check if the corresponding intermediate files need to rebuild. so if after the changes in makefile the intermediate files still follow a right chronological order the make will not generate a new build. so it is better to make clean to make sure that make will produce the result expected.
```
3.  Are tabs or spaces used to indent the commands after the rule in a Makefile?

```c
tabs
```
4.  What does `git commit` do? What’s a `sha` in the context of git?
```c
Stores the current contents of the index in a new commit along with a log message from the user describing the changes.
"sha" is the SHA-1 hash which can provide checksum of the content and its header.
```
5.  What does `git log` show you?
```c
the commits made in the repository in reverse chronological order.
```


6.  What does `git status` tell you and how would the contents of `.gitignore` change its output?
```c
git status will display paths that have differences between the index file and the current HEAD commit, paths that have differences between the working tree and the index file, and paths in the working tree that are not tracked by git.
.gitignore can make git status ignore certain files which means when running git status it will not show these files.  
```


7.  What does `git push` do? Why is it not just sufficient to commit with `git commit -m ’fixed all bugs’ `?
```c
Updates remote refs using local refs, while sending objects necessary to complete the given refs.
It is better to give some more specific messages like which bug is fixed, jus 'fixed all bugs' is too general for others and yourself to know the exact changes.
```

8.  What does a non-fast-forward error `git push` reject mean? What is the most common way of dealing with this?
```c
it means that the push you are making will cause the repository lose commits so it refuse to accept the push.
git pull before commit and push.
```

### Optional: Just for fun

-   Convert your a song lyrics into System Programming and C code covered in this wiki book and share on Piazza.

-   Find, in your opinion, the best and worst C code on the web and post the link to Piazza.

-   Write a short C program with a deliberate subtle C bug and post it on Piazza to see if others can spot your bug.

-   Do you have any cool/disastrous system programming bugs you’ve heard about? Feel free to share with your peers and the course staff on piazza.


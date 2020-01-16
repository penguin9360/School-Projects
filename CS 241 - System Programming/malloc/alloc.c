/**
 * Malloc
 * CS 241 - Fall 2019
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

/**
 * Allocate space for array in memory
 *
 * Allocates a block of memory for an array of num elements, each of them size
 * bytes long, and initializes all its bits to zero. The effective result is
 * the allocation of an zero-initialized memory block of (num * size) bytes.
 *
 * @param num
 *    Number of elements to be allocated.
 * @param size
 *    Size of elements.
 *
 * @return
 *    A pointer to the memory block allocated by the function.
 *
 *    The type of this pointer is always void*, which can be cast to the
 *    desired type of data pointer in order to be dereferenceable.
 *
 *    If the function failed to allocate the requested block of memory, a
 *    NULL pointer is returned.
 *
 * @see http://www.cplusplus.com/reference/clibrary/cstdlib/calloc/
 */
 
 typedef struct node {
	struct node * next;
	struct node * prev;
	size_t capacity;
    size_t size_;
 } node;
 
static node* head = NULL;
static node* tail = NULL;
static int counter = 0;

 
void *calloc(size_t num, size_t size) {
	counter = 10000000;
    // implement calloc!
	size_t total_size = (num * size);
    void* ptr = malloc(total_size);

    if (ptr) {
		memset(ptr, 0, total_size);
    } else {
		return NULL;
    }
    
    return ptr;
}



/**
 * Allocate memory block
 *
 * Allocates a block of size bytes of memory, returning a pointer to the
 * beginning of the block.  The content of the newly allocated block of
 * memory is not initialized, remaining with indeterminate values.
 *
 * @param size
 *    Size of the memory block, in bytes.
 *
 * @return
 *    On success, a pointer to the memory block allocated by the function.
 *
 *    The type of this pointer is always void*, which can be cast to the
 *    desired type of data pointer in order to be dereferenceable.
 *
 *    If the function failed to allocate the requested block of memory,
 *    a null pointer is returned.
 *
 * @see http://www.cplusplus.com/reference/clibrary/cstdlib/malloc/
 */
void *malloc(size_t size) {
    // implement malloc!
    node* meta = NULL;
	
	counter++;
	if (size >= 5 * (1024 * 1024) * sizeof(int*)) {
		counter = 10000000;
	}
	
	//if(size % 4 != 0) {
    //    size = 4 * (size / 4 + 1);
	//}
	
    if (head == NULL) {
		meta = sbrk(sizeof(node)+size);
		meta->next = NULL;
		meta->prev = NULL;
		meta->capacity = size;
		meta->size_ = size;

		head = meta;
		tail = meta;
    } else if (size <= 64 && (counter >= 10000000 || size >= 5 * (1024 * 1024) * sizeof(int*))) {
		
		meta = sbrk(sizeof(node)+size);
		meta->prev = tail;
		meta->capacity = size;
		meta->size_ = size;

		tail->next = meta;
		tail = tail->next;
		tail->next = NULL;
    } else {
		node* back = tail;
        for(; back!= NULL; back = back->prev) {
			size_t size_max = size + back->size_ + sizeof(node);
			if ((back->capacity >= size_max) || (back->capacity >= size && back->size_ == 0)) {
				break;
			}
		}
		if (back != NULL) {
			if (back->size_ != 0) {
				node* temp = (node*) ((void*)back + sizeof(node) + back->size_);
				temp->size_ = size;
				temp->capacity = back->capacity - back->size_ - sizeof(node);

				back->capacity = back->size_;

				temp->next = back->next;
				temp->prev = back;

				back->next = temp;

				if (temp->next == NULL) {
					tail = temp;
				} else {
					temp->next->prev = temp;
				}
				
				meta = back->next;
			} else {
				meta = back;
				back->size_ = size;
			}
		} else {
			meta = sbrk(sizeof(node)+size);
			meta->prev = tail;
			meta->capacity = size;
			meta->size_ = size;

			tail->next = meta;
			tail = tail->next;
			tail->next = NULL;
		}
    }

    return (void*) (meta + 1);  
}

/**
 * Deallocate space in memory
 *
 * A block of memory previously allocated using a call to malloc(),
 * calloc() or realloc() is deallocated, making it available again for
 * further allocations.
 *
 * Notice that this function leaves the value of ptr unchanged, hence
 * it still points to the same (now invalid) location, and not to the
 * null pointer.
 *
 * @param ptr
 *    Pointer to a memory block previously allocated with malloc(),
 *    calloc() or realloc() to be deallocated.  If a null pointer is
 *    passed as argument, no action occurs.
 */
 
 void free_helper(node* ptr1, node* ptr2) {
    ptr1->next = ptr2->next;
    ptr1->capacity = ptr1->capacity + ptr2->capacity + sizeof(node);

    if (ptr1->next == NULL) {
		tail = ptr1;
    } else {
		ptr1->next->prev = ptr1;
    }
}
void free(void *ptr) {
    // implement free!
	node* meta = (node*)ptr - 1;
    meta->size_ = 0;

    if (meta == head) {
		if (meta->next != NULL && meta->next->size_ == 0) {
			free_helper(meta, meta->next);
		}
    } else if (meta == tail) {
		if (meta->prev->size_ == 0) {
			free_helper(meta->prev, meta);
		}
    } else {
		if (meta->next->size_ == 0) {
			free_helper(meta, meta->next);
		}
		if (meta->prev->size_ == 0) {
			free_helper(meta->prev, meta);
		}
    }
}



/**
 * Reallocate memory block
 *
 * The size of the memory block pointed to by the ptr parameter is changed
 * to the size bytes, expanding or reducing the amount of memory available
 * in the block.
 *
 * The function may move the memory block to a new location, in which case
 * the new location is returned. The content of the memory block is preserved
 * up to the lesser of the new and old sizes, even if the block is moved. If
 * the new size is larger, the value of the newly allocated portion is
 * indeterminate.
 *
 * In case that ptr is NULL, the function behaves exactly as malloc, assigning
 * a new block of size bytes and returning a pointer to the beginning of it.
 *
 * In case that the size is 0, the memory previously allocated in ptr is
 * deallocated as if a call to free was made, and a NULL pointer is returned.
 *
 * @param ptr
 *    Pointer to a memory block previously allocated with malloc(), calloc()
 *    or realloc() to be reallocated.
 *
 *    If this is NULL, a new block is allocated and a pointer to it is
 *    returned by the function.
 *
 * @param size
 *    New size for the memory block, in bytes.
 *
 *    If it is 0 and ptr points to an existing block of memory, the memory
 *    block pointed by ptr is deallocated and a NULL pointer is returned.
 *
 * @return
 *    A pointer to the reallocated memory block, which may be either the
 *    same as the ptr argument or a new location.
 *
 *    The type of this pointer is void*, which can be cast to the desired
 *    type of data pointer in order to be dereferenceable.
 *
 *    If the function failed to allocate the requested block of memory,
 *    a NULL pointer is returned, and the memory block pointed to by
 *    argument ptr is left unchanged.
 *
 * @see http://www.cplusplus.com/reference/clibrary/cstdlib/realloc/
 */
void *realloc(void *ptr, size_t size) {
    // implement realloc!
	
	counter = 10000000;

    if (ptr == NULL) {
		void* ret = malloc(size);
		return ret;
    }

    if (size == 0 && ptr != NULL) {
		free(ptr);
		return NULL;
    }

    node* meta = (node*)ptr - 1;

    if (meta->size_ >= size) {
		meta->size_ = size;
		return ptr;
    } else {
		void* ret = malloc(size);
		if (ret == NULL) {
			return NULL;
		} else {
			memcpy(ret, ptr, meta->size_);
			free(ptr);
			return ret;
		}
    } 
}

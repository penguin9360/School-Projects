/**
 * @file
 * Contains an implementation of the extractMessage function.
 */

#include <iostream> // might be useful for debugging
#include <assert.h>
#include "extractMessage.h"

using namespace std;

char *extractMessage(const char *message_in, int length) {
   // Length must be a multiple of 8
   assert((length % 8) == 0);

   // allocates an array for the output
   char *message_out = new char[length];
   for (int i=0; i<length; i++) {
   		message_out[i] = 0;    // Initialize all elements to zero.
	}

	// TODO: write your code here
  int i = 0;
  unsigned char bitmask = 0x01;
  int rounds = 0;
  while(i < length) {
    rounds = i % 8;
    if (rounds == 0) {
      bitmask = 0x01;
    }
    for (int j = 0; j <= 7; j++) {
      message_out[i] += (((message_in[(i/8)*8 + j] & bitmask) >> rounds) << j);
    }
    bitmask = bitmask << 1;
    i++;
  }

	return message_out;
}

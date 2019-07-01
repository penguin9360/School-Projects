#include "simplecache.h"

int SimpleCache::find(int index, int tag, int block_offset) {
  // read handout for implementation details
  auto & wtf = _cache[index];
    for (size_t i = 0; i < wtf.size(); i++) {
    	if (wtf[i].valid() && wtf[i].tag() == tag) {
    		return wtf[i].get_byte(block_offset);
    	}
    }
  return 0xdeadbeef;
}

void SimpleCache::insert(int index, int tag, char data[]) {
  // read handout for implementation details
  // keep in mind what happens when you assign in in C++ (hint: Rule of Three)
  auto & wtf = _cache[index];
    size_t i;
    for (i = 0; i < wtf.size(); i++) {
    	if (!wtf[i].valid()) {
        break;
      }
    }
    if (i == wtf.size()) {
      i = 0;
    }
    wtf[i].replace(tag, data);
}

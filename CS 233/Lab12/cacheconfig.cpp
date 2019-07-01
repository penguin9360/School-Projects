#include "cacheconfig.h"
#include "utils.h"
#include <cmath>

CacheConfig::CacheConfig(uint32_t size, uint32_t block_size, uint32_t associativity)
: _size(size), _block_size(block_size), _associativity(associativity) {
  /**
   * TODO
   * Compute and set `_num_block_offset_bits`, `_num_index_bits`, `_num_tag_bits`.
  */
	_num_block_offset_bits = (uint32_t)((int)(log(block_size * 1.0) / log(2.0)));
  _num_index_bits = (uint32_t)((int)(log(size / block_size / associativity)/ log(2.0)));
  _num_tag_bits = 32 - _num_block_offset_bits - _num_index_bits;
}

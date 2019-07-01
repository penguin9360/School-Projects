#include "cacheblock.h"
#include <cmath>

uint32_t Cache::Block::get_address() const {
  // TODO
  if (_cache_config.get_num_tag_bits() == 32) {
    return _tag;
  }
  auto size_diff = 32 - _cache_config.get_num_tag_bits() - _cache_config.get_num_index_bits();
  return (_tag << (_cache_config.get_num_index_bits() + size_diff)) + (_index << size_diff);
}

#include "cachesimulator.h"

Cache::Block* CacheSimulator::find_block(uint32_t address) const {
  /**
   * TODO
   *
   * 1. Use `_cache->get_blocks_in_set` to get all the blocks that could
   *    possibly have `address` cached.
   * 2. Loop through all these blocks to see if any one of them actually has
   *    `address` cached (i.e. the block is valid and the tags match).
   * 3. If you find the block, increment `_hits` and return a pointer to the
   *    block. Otherwise, return NULL.
   */
  auto tag = extract_tag(address, _cache->get_config());
  auto index = extract_index(address, _cache->get_config());

  auto cacheSet = _cache->get_blocks_in_set(index);

  for (int i = 0; i < cacheSet.size(); i++) {
    if (cacheSet[i]->is_valid() && cacheSet[i]->get_tag() == tag) {
     	_hits++;
     	return cacheSet[i];
    }
  }
  return NULL;
}

Cache::Block* CacheSimulator::bring_block_into_cache(uint32_t address) const {
  /**
   * TODO
   *
   * 1. Use `_cache->get_blocks_in_set` to get all the blocks that could
   *    cache `address`.
   * 2. Loop through all these blocks to find an invalid `block`. If found,
   *    skip to step 4.
   * 3. Loop through all these blocks to find the least recently used `block`.
   *    If the block is dirty, write it back to memory.
   * 4. Update the `block`'s tag. Read data into it from memory. Mark it as
   *    valid. Mark it as clean. Return a pointer to the `block`.
   */
  auto index = extract_index(address, _cache->get_config());
  auto tag = extract_tag(address, _cache->get_config());
  auto cacheSet = _cache->get_blocks_in_set(index);
  auto LRU = cacheSet[0];
  for (int i = 0; i < cacheSet.size(); i++) {
    if (!cacheSet[i]->is_valid()) {
      cacheSet[i]->set_tag(tag);
     	cacheSet[i]->read_data_from_memory(_memory);
     	cacheSet[i]->mark_as_clean();
     	cacheSet[i]->mark_as_valid();
     	return cacheSet[i];
     }
  }
  for (int i = 0; i < cacheSet.size(); i++) {
   	if (cacheSet[i]->get_last_used_time() < LRU->get_last_used_time()) {
   		LRU = cacheSet[i];
    }
  }
  if (LRU->is_dirty()) {
   	LRU->write_data_to_memory(_memory);
  }
  LRU->set_tag(tag);
  LRU->read_data_from_memory(_memory);
  LRU->mark_as_clean();
  LRU->mark_as_valid();
  return LRU;
}

uint32_t CacheSimulator::read_access(uint32_t address) const {
  /**
   * TODO
   *
   * 1. Use `find_block` to find the `block` caching `address`.
   * 2. If not found, use `bring_block_into_cache` cache `address` in `block`.
   * 3. Update the `last_used_time` for the `block`.
   * 4. Use `read_word_at_offset` to return the data at `address`.
   */
  auto block_addr = find_block(address);
  if (!block_addr) {
    block_addr = bring_block_into_cache(address);
  }
  block_addr->set_last_used_time(_use_clock.get_count());
  _use_clock++;
  auto offset = extract_block_offset(address, _cache->get_config());
  return block_addr->read_word_at_offset(offset);
}

void CacheSimulator::write_access(uint32_t address, uint32_t word) const {
  /**
   * TODO
   *
   * 1. Use `find_block` to find the `block` caching `address`.
   * 2. If not found
   *    a. If the policy is write allocate, use `bring_block_into_cache`.
   *    a. Otherwise, directly write the `word` to `address` in the memory
   *       using `_memory->write_word` and return.
   * 3. Update the `last_used_time` for the `block`.
   * 4. Use `write_word_at_offset` to to write `word` to `address`.
   * 5. a. If the policy is write back, mark `block` as dirty.
   *    b. Otherwise, write `word` to `address` in memory.
   */
   auto block_addr = find_block(address);
   	if (!block_addr) {
   		if (_policy.is_write_allocate()) {
   			block_addr = bring_block_into_cache(address);
   		} else {
   			_memory->write_word(address, word);
   			return;
   		}
   	}
   	block_addr->set_last_used_time(_use_clock.get_count());
   	_use_clock++;
   	auto offset = extract_block_offset(address, _cache->get_config());
   	block_addr->write_word_at_offset(word, offset);
   	if (_policy.is_write_back()) {
   		block_addr->mark_as_dirty();
   	} else {
   		_memory->write_word(address, word);
   	}
}

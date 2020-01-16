// SOURCE:
// https://stackoverflow.com/questions/12805041/c-equivalent-to-javas-blockingqueue
// ANSWERED BY: Dietmar Kühl

#pragma once

#include <condition_variable>
#include <deque>
#include <mutex>

template <typename T> class BlockingQueue {
private:
  std::mutex d_mutex;
  std::condition_variable d_condition;
  std::deque<T> d_queue;

public:
  void push(T const &value) {
    {
      std::unique_lock<std::mutex> lock(this->d_mutex);
      d_queue.push_front(value);
    }
    this->d_condition.notify_one();
  }
  T pop() {
    std::unique_lock<std::mutex> lock(this->d_mutex);
    this->d_condition.wait(lock, [=] { return !this->d_queue.empty(); });
    T rc(std::move(this->d_queue.back()));
    this->d_queue.pop_back();
    return rc;
  }
  int size() {
    int size_of_deque = d_queue.size();
    return size_of_deque;
  }
};

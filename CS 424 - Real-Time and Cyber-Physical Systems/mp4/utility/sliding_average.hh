#pragma once

template<class T, int N>
class SlidingAverage {
  public:
    void push(T data) {
      data_[curr_i_] = data;
      curr_i_ = (curr_i_ + 1) % N;
    }

    double getAvg() const {
      T sum{0};
      for (const T& d: data_) {
        sum += d;
      }
      return sum / double(N);
    }
  public:
    T data_[N];
    int curr_i_ = 0;
};

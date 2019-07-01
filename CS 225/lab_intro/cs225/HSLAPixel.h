/**
 * @file HSLAPixel.h
 *
 * @author CS 225: Data Structures
 * @version 2018r1-lab1
 */

#ifndef CS225_HSLAPIXEL_H_
#define CS225_HSLAPIXEL_H_

#include <iostream>
#include <sstream>

namespace cs225 {
  class HSLAPixel {
    public:
      double h;
      double s;
      double l;
      double a;
      HSLAPixel();
      HSLAPixel(double h, double s, double l);
      HSLAPixel(double h, double s, double l, double a);
  };
}

#endif

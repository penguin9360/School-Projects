#include <cmath>
#include <iterator>
#include <iostream>

#include "../cs225/HSLAPixel.h"
#include "../cs225/PNG.h"
#include "../Point.h"

#include "ImageTraversal.h"

/**
 * Calculates a metric for the difference between two pixels, used to
 * calculate if a pixel is within a tolerance.
 *
 * @param p1 First pixel
 * @param p2 Second pixel
 * @return the difference between two HSLAPixels
 */
double ImageTraversal::calculateDelta(const HSLAPixel & p1, const HSLAPixel & p2) {
  double h = fabs(p1.h - p2.h);
  double s = p1.s - p2.s;
  double l = p1.l - p2.l;

  // Handle the case where we found the bigger angle between two hues:
  if (h > 180) { h = 360 - h; }
  h /= 360;

  return sqrt( (h*h) + (s*s) + (l*l) );
}

/**
 * Default iterator constructor.
 */
ImageTraversal::Iterator::Iterator() {
  /** @todo [Part 1] */
  trav = NULL;
}

ImageTraversal::Iterator::~Iterator(){
  // delete iter;
  // iter = NULL;
}

ImageTraversal::Iterator::Iterator(PNG & png, Point & point, double tolerance,
          ImageTraversal * next){
  trav = next;
  image = png;
  start = point;
  current = point;
  tolerance_ = tolerance;
  vector<bool> vec;

  for (unsigned i = 0; i < image.height(); i++){
    vec.push_back(false);
  }
  for (unsigned i = 0; i < image.width(); i++){
    visited.push_back(vec);
  }
  visited[start.x][start.y] = true;

  HSLAPixel & sta = image.getPixel(start.x, start.y);

  if (current.x + 1 < image.width()){
    HSLAPixel & curr = image.getPixel(current.x + 1, current.y);
    if (calculateDelta(curr, sta) < tolerance_){
      trav->add(Point(current.x + 1, current.y));
    }
  }

  if (current.y + 1 < image.height()){
    HSLAPixel & curr = image.getPixel(current.x, current.y + 1);
    if (calculateDelta(curr, sta) < tolerance_){
      trav->add(Point(current.x, current.y + 1));
    }
  }

  if ((int)current.x - 1 >= 0) {
    HSLAPixel & curr = image.getPixel(current.x - 1, current.y);
    if (calculateDelta(curr, sta) < tolerance_){
      trav->add(Point(current.x - 1, current.y));
    }
  }

  if ((int)current.y - 1 >= 0) {
    HSLAPixel & curr = image.getPixel(current.x, current.y - 1);
    if (calculateDelta(curr, sta) < tolerance_){
      trav->add(Point(current.x, current.y - 1));
    }
  }
}
/**
 * Iterator increment opreator.
 *
 * Advances the traversal of the image.
 */
ImageTraversal::Iterator & ImageTraversal::Iterator::operator++() {
  /** @todo [Part 1] */

  if (trav->empty()){
    trav = NULL;
    return *this;
  }

  Point temp = trav->pop();
  while (visited[temp.x][temp.y]){
    if (trav->empty()){
      trav = NULL;
      return *this;
    } else {
      temp = trav->pop();
    }
  }

  visited[temp.x][temp.y] = true;
  current = temp;

  HSLAPixel & sta = image.getPixel(start.x, start.y);

  if (current.x + 1 < image.width()){
    HSLAPixel & curr = image.getPixel(current.x + 1, current.y);
    if (calculateDelta(curr, sta) < tolerance_){
      trav->add(Point(current.x + 1, current.y));
    }
  }

  if (current.y + 1 < image.height()){
    HSLAPixel & curr = image.getPixel(current.x, current.y + 1);
    if (calculateDelta(curr, sta) < tolerance_){
      trav->add(Point(current.x, current.y + 1));
    }
  }

  if ((int)current.x - 1 >= 0) {
    HSLAPixel & curr = image.getPixel(current.x - 1, current.y);
    if (calculateDelta(curr, sta) < tolerance_){
      trav->add(Point(current.x - 1, current.y));
    }
  }

  if ((int)current.y - 1 >= 0) {
    HSLAPixel & curr = image.getPixel(current.x, current.y - 1);
    if (calculateDelta(curr, sta) < tolerance_){
      trav->add(Point(current.x, current.y - 1));
    }
  }
  return *this;
}

/**
 * Iterator accessor opreator.
 *
 * Accesses the current Point in the ImageTraversal.
 */
Point ImageTraversal::Iterator::operator*() {
  /** @todo [Part 1] */
  return current;
}

/**
 * Iterator inequality operator.
 *
 * Determines if two iterators are not equal.
 */
bool ImageTraversal::Iterator::operator!=(const ImageTraversal::Iterator &other) {
  /** @todo [Part 1] */
  return trav != NULL;
}

#include "../cs225/HSLAPixel.h"
#include "../Point.h"

#include "ColorPicker.h"
#include "MyColorPicker.h"

using namespace cs225;

/**
 * Picks the color for pixel (x, y).
 * Using your own algorithm
 */
MyColorPicker::MyColorPicker(double i) : h(i), ad(0) {
  // nothing
}

HSLAPixel MyColorPicker::getColor(unsigned x, unsigned y) {
  /* @todo [Part 3] */
	HSLAPixel pixel(h, 1, 0.5);
  if (h >= 360) {
     h -= 360;
  }
  h -= ad;
  return pixel;
}

#pragma once

#include <list>
#include <iostream>

#include "cs225/PNG.h"
#include "colorPicker/ColorPicker.h"
#include "imageTraversal/ImageTraversal.h"
#include "Point.h"
#include "Animation.h"

using namespace cs225;

class FloodFilledImage {
public:
  FloodFilledImage(const PNG & png);
  void addFloodFill(ImageTraversal & traversal, ColorPicker & colorPicker);
  Animation animate(unsigned frameInterval) const;

private:
  ColorPicker* color;
  PNG canvas;
	ImageTraversal* trav;
};

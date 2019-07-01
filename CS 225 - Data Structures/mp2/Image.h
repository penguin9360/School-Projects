/**
 * @file Image.h
 * Contains your declaration of the interface for the Image class.
 */

#ifndef IMAGE_H_
#define IMAGE_H_

#include "cs225/PNG.h"
#include "cs225/HSLAPixel.h"

using namespace cs225;


class Image: public cs225::PNG {
  public:
    Image():PNG(){};
    Image(unsigned w, unsigned h) : PNG(w, h){};
    void lighten();
    void lighten(double amount);
    void darken();
    void darken(double amount);
    void saturate();
    void saturate(double amount);
    void desaturate();
    void desaturate(double amount);
    void grayscale();
    void rotateColor(double degrees);
    void illinify();
    void scale(double factor);
    void scale(unsigned w, unsigned h);
    // void hue_helper(double hue);
  private:
};

#endif

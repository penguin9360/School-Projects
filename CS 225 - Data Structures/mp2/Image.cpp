#include <cmath>
#include "Image.h"
#include "cs225/PNG.h"
#include "cs225/HSLAPixel.h"

using namespace cs225;

void Image::lighten(){
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      if (pixel->l + 0.1 <= 1.0) {
        pixel->l += 0.1;
      } else {
        pixel->l = 1.0;
      }
    }
  }
}

void Image::lighten(double amount) {
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      if (pixel->l + amount <= 1.0) {
        pixel->l += amount;
      } else {
        pixel->l = 1.0;
      }
    }
  }
}

void Image::darken() {
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      if (pixel->l - 0.1 >= 0.0) {
        pixel->l = pixel->l - 0.1;
      } else {
        pixel->l = 0.0;
      }
    }
  }
}

void Image::darken(double amount) {
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      if (pixel->l - amount >= 0.0) {
        pixel->l = pixel->l - amount;
      } else {
        pixel->l = 0.0;
      }
    }
  }
}

void Image::saturate() {
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      if (pixel->s + 0.1 <= 1.0) {
        pixel->s += 0.1;
      } else {
        pixel->s = 1.0;
      }
    }
  }
}

void Image::saturate(double amount) {
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      if (pixel->s + amount <= 1.0) {
        pixel->s += amount;
      } else {
        pixel->s = 1.0;
      }
    }
  }
}

void Image::desaturate() {
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      if (pixel->s - 0.1 >= 0.0) {
        pixel->s = pixel->s - 0.1;
      } else {
        pixel->s = 0.0;
      }
    }
  }
}

void Image::desaturate(double amount){
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      if (pixel->s - amount >= 0.0) {
        pixel->s = pixel->s - amount;
      } else {
        pixel->s = 0.0;
      }
    }
  }
}

void Image::grayscale(){
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel * pixel = &getPixel(j, i);
      pixel->s = 0.0;
    }
  }
}

void Image::rotateColor(double degrees){
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++){
      HSLAPixel & pixel = getPixel(j, i);
      pixel.h = (pixel.h) + degrees;
      while ((pixel.h) > 360){
        pixel.h = (pixel.h) - 360;
      }
      while ((pixel.h) < 0){
        pixel.h = (pixel.h) + 360;
      }
      // Image::hue_helper(pixel.h);
      // if (pixel.h > 360 || pixel.h < 0) {
      //   std::cout << "outbound! current h: " << pixel.h << " current degrees: " << degrees << std::endl;
      //   pixel.h = (int)pixel.h % 360;
      //   std::cout << "Modified! current h: " << pixel.h << " current degrees: " << degrees << std::endl;
      // } else {
      //   std::cout << "In bound, current h: " << pixel.h << " current degrees: " << degrees << std::endl;
      // }
    }
  }
}

// void Image::hue_helper(double hue){
//   if (hue >= 0.0 && hue <= 360.0){
//     hue = hue + 0;
//     std::cout << "In bound, current h: " << hue << std::endl;
//   }
//   if (hue > 360.0) {
//     std::cout << "outbound (>360)! current h: " << hue << std::endl;
//     hue = hue - 360.0;
//     std::cout << "Modified (>360)! current h: " << hue << std::endl;
//     Image::hue_helper(hue);
//   }
//   if (hue < 0.0) {
//     std::cout << "outbound (<0)! current h: " << hue << std::endl;
//     hue = hue + 360.0;
//     std::cout << "Modified (<0)! current h: " << hue << std::endl;
//     Image::hue_helper(hue);
//   }
// }

void Image::illinify(){
  for (unsigned i = 0; i < height(); i++) {
    for (unsigned j = 0; j < width(); j++) {
      HSLAPixel * pixel = &getPixel(j, i);
      if (((int)pixel->h - 11) % 360 < 113.5 || ((int)pixel->h - 11) % 360 > 293.5) {
        pixel->h = 11;
      } else {
        pixel->h = 216;
      }
    }
  }
}

void Image::scale(double factor){
  Image old = *this;
  resize(width() * factor ,height() * factor);
  for (unsigned i = 0; i < width(); i++){
    for (unsigned j = 0; j < height(); j++){
	     HSLAPixel *pixel = & getPixel(i, j);
       unsigned int scaledWidth = (unsigned int) i/factor;
       unsigned int scaledHeight = (unsigned int) j/factor;
	      HSLAPixel *temp = & old.getPixel(scaledWidth, scaledHeight);
	       pixel->h = temp -> h;
         pixel->s = temp -> s;
         pixel->l = temp -> l;
 	       pixel->a = temp -> a;
    }
  }
}

void Image::scale(unsigned w, unsigned h){
  // unsigned int scaledWidth = w;
	unsigned ratio = fmax(w/width(),h/height());
	scale(ratio);
}

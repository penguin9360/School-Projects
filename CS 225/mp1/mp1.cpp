#include "cs225/PNG.h"
using cs225::PNG;

#include "cs225/HSLAPixel.h"
using cs225::HSLAPixel;
#include "mp1.h"
#include <string>



void rotate(std::string inputFile, std::string outputFile) {
  // TODO: Part 2
  PNG *input_file = new PNG;
  input_file->readFromFile(inputFile);
  PNG *output_file = new PNG(input_file->width(), input_file->height());
  for (unsigned i = 0; i < input_file->width(); i++){
    for (unsigned j = 0; j < input_file->height(); j++){
      output_file->getPixel(i, j) = input_file->getPixel(output_file->width() - i - 1,output_file->height() - j - 1);
    }
  }
  output_file->writeToFile(outputFile);
  delete input_file;
  delete output_file;
}

PNG myArt(unsigned int width, unsigned int height) {
  PNG png(width, height);
  HSLAPixel MyFavouriteColor = HSLAPixel(200, 1.0, 0.7, 1.0);
  HSLAPixel HerFavouriteColor = HSLAPixel(155, 1.0, 0.52, 0.96);
  HSLAPixel summer = HSLAPixel(47, 1.0, 0.67, 1.0);
  for (unsigned i = 0; i < width; i++) {
    for (unsigned j = 0; j < height; j++) {
      if (i > 504 && j < 95) {
        png.getPixel(i, j) = HerFavouriteColor;
      } else if (i < 412 && j > 97) {
        png.getPixel(i, j) = MyFavouriteColor;
      } else {
        png.getPixel(i, j) = summer;
      }
    }
  }
  return png;
}

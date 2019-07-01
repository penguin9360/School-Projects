#include "StickerSheet.h"
#include <iostream>
#include "cs225/PNG.h"
#include "cs225/HSLAPixel.h"

using namespace cs225;

StickerSheet::StickerSheet(const Image &picture, unsigned max){
  base_ = new Image();
  *base_ = picture;
  max_ = max;
  stickers_ = new Image*[max_];
  x_ = new unsigned int[max];
  y_ = new unsigned int[max];

  for (unsigned i = 0; i < max; i++) {
    stickers_[i] = nullptr;
    x_[i] = 0;
    y_[i] = 0;
  }
}

StickerSheet::StickerSheet(const StickerSheet & other){
  base_ = new Image();
  *base_ = *(other.base_);
  max_ = other.max_;
  stickers_ = new Image*[max_];
  x_ = new unsigned[max_];
  y_ = new unsigned[max_];

  for (unsigned i = 0; i < max_; i++){
    stickers_[i] = other.stickers_[i];
    x_ = new unsigned int;
    x_[i] = other.x_[i];
    y_ = new unsigned int;
    y_[i]= other.y_[i];
  }
}

const StickerSheet& StickerSheet::operator=(const StickerSheet & other){
  if (this != &other){
    delete[] x_;
    x_ = nullptr;
    delete[] y_;
    y_ = nullptr;
    delete[] stickers_;
    stickers_ = nullptr;
    delete[] base_;

    base_ = new Image();
    *base_ = *(other.base_);
    max_ = other.max_;
    stickers_ = new Image*[max_];
    x_ = new unsigned[max_];
    y_ = new unsigned[max_];

    for (unsigned i = 0; i < max_; i++){
      stickers_[i] = other.stickers_[i];
      x_ = new unsigned int;
      x_[i] = other.x_[i];
      y_ = new unsigned int;
      y_[i]= other.y_[i];
    }
    return *this;
    } else {
    return *this;
  }
}

void StickerSheet::changeMaxStickers(unsigned max) {
  if (max_ == max)return;
  Image** temp_image = new Image*[max];
  unsigned* temp_x = new unsigned[max];
  unsigned* temp_y = new unsigned[max];
  unsigned temp_max = 0;
  if (max < max_) {
    temp_max = max;
  } else if (max > max_) {
    temp_max = max_;
  }
  for (unsigned i = 0; i < temp_max; i++){
    temp_image[i] = stickers_[i];
    temp_x[i] = x_[i];
    temp_y[i] = y_[i];
  }
  delete[] x_;
  x_ = temp_x;
  delete[] y_;
  y_ = temp_y;
  delete[] stickers_;
  stickers_ = temp_image;
  max_ = max;
}

int StickerSheet::addSticker(Image & sticker, unsigned x, unsigned y){
  if(x < base_->width() && y < base_->height()){
    for (unsigned i = 0; i < max_; i++){
      if (stickers_[i] == nullptr){
        stickers_[i] = new Image(sticker);
        x_[i] = x;
        y_[i] = y;
        return i;
      }
    }
  }
    return -1;
}

bool StickerSheet::translate(unsigned index, unsigned x, unsigned y){
  if (index > max_ ||
    stickers_[index] == nullptr ||
    stickers_[index] == nullptr ||
    x + x_[index] > base_->width() ||
    y + y_[index] > base_->height()){
    return false;
  }
  x_[index] = x;
  y_[index] = y;
  return true;
}

void StickerSheet::removeSticker(unsigned index){
  if (stickers_[index] != nullptr && index < max_){
    delete stickers_[index];
    stickers_[index] = nullptr;
  } else {return;}
}

Image * StickerSheet::getSticker(unsigned index) const{
  if (index < max_){
    return stickers_[index];
  } else {
    return nullptr;
  }
}

Image StickerSheet::render() const{
  unsigned width = 0; unsigned height = 0;
  unsigned i = 0;
  while (i < max_ && stickers_[i] != nullptr){
    if (width < x_[i] + stickers_[i]->width() && height < y_[i] + stickers_[i]->height()){
      width = x_[i] + stickers_[i]->width();
      height = y_[i] + stickers_[i]->height();
    }
    i++;
  }
  std::cout << "line 147 sticker" << std::endl;
  Image *output = new Image();
  output->resize(base_->width(), base_->height());
  for (unsigned i = 0; i < output->width(); i++){
    for (unsigned j = 0; j < output->height(); j++){
      output->getPixel(i, j) = base_->getPixel(i, j);
    }
  }
  std::cout << "line 154 sticker" << std::endl;
  unsigned int a = 0;
  while(a < max_ && stickers_[a] != nullptr){
    for (unsigned x = 0; x < stickers_[a]->width(); x++){
      for (unsigned y = 0; y < stickers_[a]->height(); y++){
        HSLAPixel & prev = output->getPixel(x + x_[a], y + y_[a]);
        HSLAPixel & curr = stickers_[a]->getPixel(x, y);
        if (curr.a != 0){
          prev = curr;
        }
      }
    }
    std::cout << "line 166 sticker" << std::endl;
    a++;
  }
  return *output;
}

StickerSheet::~StickerSheet() {
  for (unsigned i = 0; i < max_; i++){
    if (stickers_ != nullptr){
      delete stickers_[i];
      stickers_[i] = nullptr;
    }
  }
  delete[] stickers_;
  stickers_ = nullptr;

  if (x_ != nullptr){
    delete[] x_;
    x_ = nullptr;
  }
  if (y_ != nullptr){
    delete[] y_;
    y_ = nullptr;
  }
  if (base_ != nullptr){
    delete[] base_;
    base_ = nullptr;
  }
}

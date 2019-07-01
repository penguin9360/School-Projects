/**
 * @file StickerSheet.h
 * Contains your declaration of the interface for the StickerSheet class.
 */
 #pragma once
 #include <iostream>
 #include "Image.h"
 #include "cs225/PNG.h"
 #include "cs225/HSLAPixel.h"

namespace cs225{
  class StickerSheet{
      public:
        StickerSheet(const Image &picture, unsigned max);
        ~StickerSheet();
        StickerSheet(const StickerSheet & other);
        const StickerSheet & operator=(const StickerSheet & other);
        void changeMaxStickers(unsigned max);
        int addSticker(Image & sticker, unsigned x, unsigned y);
        bool translate(unsigned index, unsigned x, unsigned y);
        void removeSticker(unsigned index);
        Image * getSticker(unsigned index) const;
        Image render() const;
      private:
        Image* base_;
        Image** stickers_;
        unsigned* x_;
        unsigned* y_;
        unsigned max_;
  };
}

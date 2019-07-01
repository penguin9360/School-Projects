#include "Image.h"
#include "StickerSheet.h"
using namespace cs225;
using namespace std;

int main() {

  //
  // Reminder:
  //   Before exiting main, save your creation to disk as myImage.png
  //
  Image a;
  a.readFromFile("tests/Trump_head.png");

  Image soccer1;
  soccer1.readFromFile("tests/soccer.png");
  soccer1.resize(1920, 1080);

  Image prof;
  prof.readFromFile("tests/prof.png");

  Image alma1;
  alma1.readFromFile("tests/alma.png");


  StickerSheet t(soccer1, 5);
  t.addSticker(a, 400, 50);
  std::cout << "trump" << std::endl;


  StickerSheet tt(soccer1, 5);
  t.addSticker(prof, 500, 180);
  std::cout << "prof" << std::endl;


  StickerSheet ttt(soccer1, 5);
  t.addSticker(alma1, 600, 600);
  std::cout << "alma" << std::endl;

  Image myImage;
  myImage = t.render();
  myImage.writeToFile("myImage.png");

  return 0;
}

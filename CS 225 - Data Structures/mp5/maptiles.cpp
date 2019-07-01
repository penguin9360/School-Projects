/**
 * @file maptiles.cpp
 * Code for the maptiles function.
 */

#include <iostream>
#include <map>
#include "maptiles.h"
//#include "cs225/RGB_HSL.h"

using namespace std;


Point<3> convertToXYZ(LUVAPixel pixel) {
    return Point<3>( pixel.l, pixel.u, pixel.v );
}

MosaicCanvas* mapTiles(SourceImage const& theSource,
                       vector<TileImage>& theTiles) {
  MosaicCanvas* canvas = new MosaicCanvas(theSource.getRows(),theSource.getColumns());
  vector<Point<3>> tiles;
  map<Point<3>, int> newmap;
    for (size_t i = 0; i < theTiles.size(); i++){
        LUVAPixel pixel = theTiles[i].getAverageColor();
        double luv_vec[3];
        luv_vec[0] = pixel.l;
        luv_vec[1] = pixel.u;
        luv_vec[2] = pixel.v;
        Point<3> luv_Point(luv_vec);
        tiles.push_back(luv_Point);
    }
    for (size_t i = 0; i < theTiles.size(); i++) {
        newmap[tiles[i]] = i;
    }
    KDTree<3> tile_Images(tiles);
    for (int i = 0; i < theSource.getRows(); i++){
      for(int j = 0; j < theSource.getColumns(); j++){
          LUVAPixel pixel = theSource.getRegionColor(i, j);
          double luv_vec[3];
          luv_vec[0] = pixel.l;
          luv_vec[1] = pixel.u;
          luv_vec[2] = pixel.v;
          Point<3> luv_Point(luv_vec);
          Point<3> key = tile_Images.findNearestNeighbor(luv_Point);
          int index = newmap[key];
          canvas->setTile(i, j, &theTiles[index]);
        }
      }
      return canvas;
}

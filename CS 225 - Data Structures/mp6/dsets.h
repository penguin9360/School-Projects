/* Your code here! */
#ifndef DSETS_H
#define DSETS_H

#include <vector>
using namespace std;

struct DisjointSets
{
  vector<int> sets;
  void addelements(int num);
  int find(int elem);
  void setunion(int a, int b);
  int size(int elem);
};

#endif

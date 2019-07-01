/* Your code here! */
#include "dsets.h"

using namespace std;

/** This function should create elements to the vector sets
 * @param num is how many elements to create
 * initialize each element's number to be -1
 */
void DisjointSets::addelements(int num) {
  for (int i = 0; i < num; i++) {
    sets.push_back(-1);
  }
}
/** This function should find the root of the given index elem.
 * @param elem is the index to start with
 * if elem is not the root, recursively find the root
 * return the root's index
 */
int DisjointSets::find(int elem) {
  if (sets[elem] < 0) {
    return elem;
  } else {
    return find(sets[elem]);
  }
}
/** This function should be implemented as union-by-size.
 * @param a is the first index to union with
 * @param b is the second index to union with
 * if a or b is not thr root, should find the root and then do union
 * when union, store the size of the final root as the sum of all nodes in both sets
 */
void DisjointSets::setunion(int a, int b) {
  while (sets[b] >= 0) {
    b = sets[b];
  }
  while (sets[a] >= 0) {
    a = sets[a];
  }
  int newSize = sets[a] + sets[b];
  if ( sets[a] <= sets[b] ) {
    sets[b] = a;
    sets[a] = newSize;
  } else {
    sets[a] = b;
    sets[b] = newSize;
  }
}
/** This function should return the number of nodes in the up-tree containing the element.
 * @param elem is one of the index in the set that we want to count size
 */
int DisjointSets::size(int elem) {
  int size = 0;
  while (sets[elem] >= 0) {
    elem = sets[elem];
  }
  size -= sets[elem];
  return size;
}

/**
 * @file kdtree.cpp
 * Implementation of KDTree class.
 */

#include <utility>
#include <algorithm>
#include <cmath>

using namespace std;

// This function should take in index_2 templatized Points and a dimension and return a boolean value
// representing whether or not the first Point has a smaller value than the second in the dimension specified.
// That is, if the dimension passed in is k, then this should be true if the coordinate of the first point at k
// is less than the coordinate of the second point at k.

template <int Dim>
bool KDTree<Dim>::smallerDimVal(const Point<Dim>& first,
                                const Point<Dim>& second, int curDim) const
{
    /**
     * @todo Implement this function!
     */
     if(first[curDim] == second[curDim]) {
      return first < second;
     }
     if(first[curDim] < second[curDim]) {
       return true;
     } else {
       return false;
     }
}

template <int Dim>
bool KDTree<Dim>::shouldReplace(const Point<Dim>& target,
                                const Point<Dim>& currentBest,
                                const Point<Dim>& potential) const {
    /**
     * @todo Implement this function!
     */
     double sumPotential = 0.0, sumCurr = 0.0;
     for(int i = 0; i < Dim; i++) {
       sumPotential += (potential[i] - target[i]) * (potential[i] - target[i]);
       sumCurr += (currentBest[i] - target[i]) * (currentBest[i] - target[i]);
     }
     if(sumPotential == sumCurr){
       return potential < currentBest;
     }
     return sumPotential < sumCurr;
}

template <int Dim>
KDTree<Dim>::KDTree(const vector<Point<Dim>>& newPoints) {
    /**
     * @todo Implement this function!
     */
     points = newPoints;
     root = KDTreeHelper(points, 0, newPoints.size() - 1, 0);
}

template <int Dim>
KDTree<Dim>::KDTree(const KDTree& other) {
  /**
   * @todo Implement this function!
   */
   //Excessively sketch
  points = other.points;
  root = KDTreeHelper(points, 0, points.size() - 1, 0);
}

template <int Dim>
const KDTree<Dim>& KDTree<Dim>::operator=(const KDTree& rhs) {
  /**
   * @todo Implement this function!
   */
   if(this != &rhs){
     clear(this);
     points = rhs.points;
     root = KDTreeHelper(rhs.points, 0, rhs.points.size() - 1, 0);
   }
  return *this;
}

template <int Dim>
typename KDTree<Dim>::KDTreeNode * KDTree<Dim>::KDTreeHelper(vector<Point<Dim>> & points, int start, int end, int dist) {
  if(end - start < 0) {
    return NULL;
  }
  int midPoint = floor((start + end) / 2);
  findMedian(points, start, end, midPoint, dist);
  KDTreeNode *subRoot = new KDTreeNode(points[midPoint]);
  dist++;
  if(dist == Dim) {
    dist = 0;
  }
  subRoot->left = KDTreeHelper(points, start, midPoint - 1, dist);
  subRoot->right = KDTreeHelper(points, midPoint + 1, end, dist);
  return subRoot;
}


template <int Dim>
KDTree<Dim>::~KDTree() {
  /**
   * @todo Implement this function!
   */
   clear(root);
   size = 0;
}

template <int Dim>
void KDTree<Dim>:: clear(KDTreeNode * subRoot) {
  if(subRoot != NULL) {
    clear(subRoot->left);
    clear(subRoot->right);
    delete subRoot;
  }
}

template <int Dim>
Point<Dim> KDTree<Dim>::findNearestNeighbor(const Point<Dim>& query) const
{
    /**
     * @todo Implement this function!
     */
   return findNearest_Helper(root, query, root->point, 0);
}

template <int Dim>
Point<Dim> KDTree<Dim>::findNearest_Helper(KDTreeNode* subRoot, const Point<Dim> tgt, Point<Dim> nearest, int newDim) const {

  if(subRoot->left == NULL && subRoot->right == NULL){
    return nearest = subRoot->point;
  }

  if(smallerDimVal(subRoot->point, tgt, newDim)) {
    if(subRoot->right != NULL) {
      nearest = findNearest_Helper(subRoot->right, tgt, nearest, (newDim + 1) % Dim);
    }
  }
  bool on_left = false;
  if(smallerDimVal(tgt, subRoot->point, newDim)){
    if(subRoot->left != NULL){
      nearest = findNearest_Helper(subRoot->left, tgt, nearest, (newDim + 1) % Dim);
      on_left = true;
    }
  }
  double diff = 0;
  double subRoot_diff = ((subRoot->point)[newDim] - tgt[newDim]) * ((subRoot->point)[newDim] - tgt[newDim]);
  for(int i = 0; i < Dim; i++) {
    diff += ((tgt[i] - nearest[i]) * (tgt[i] - nearest[i]));
  }
  if(subRoot_diff <= diff) {
    if(!on_left) {
      if(subRoot->left != NULL) {
          Point<Dim> nearest_temp = findNearest_Helper(subRoot->left, tgt, nearest, (newDim + 1) % Dim);
          if(shouldReplace(tgt, nearest, nearest_temp)) {
            nearest = nearest_temp;
          }
        }
      } else {
        if(subRoot->right != NULL) {
          Point<Dim> nearest_temp = findNearest_Helper(subRoot->right, tgt, nearest, (newDim + 1) % Dim);
          if(shouldReplace(tgt, nearest, nearest_temp)) {
            nearest = nearest_temp;
          }
        }
      }
    }
  if(shouldReplace(tgt, nearest, subRoot->point)) {
    nearest = subRoot->point;
  }
  return nearest;
}

template <int Dim>
int KDTree<Dim>::divide(vector<Point<Dim>>& points, int start, int end, int center, int dist){
  Point<Dim> pivot = points[center];
  swap(points, center, end);
  int ct = start;
  for(int i = start; i < end; i++){
    if(points[i][dist] == pivot[dist] && points[i] < pivot) {
      swap(points, ct, i);
      ct++;
    }
    if (points[i][dist] < pivot[dist]){
      swap(points, ct, i);
      ct++;
    }
  }
  swap(points, end, ct);
  return ct;
}

template <int Dim>
void KDTree<Dim>::findMedian(vector<Point<Dim>> & points, int start, int end, int index_mid, int dist) {
  int newMid = divide(points, start, end, index_mid, dist);
  if (start == end || index_mid == newMid) {
    return;
  }
  if (index_mid < newMid) {
    findMedian(points, start, newMid - 1, index_mid, dist);
  } else {
    findMedian(points, newMid + 1, end, index_mid, dist);
  }
}

template <int Dim>
void KDTree<Dim>::swap(vector<Point<Dim>>& points, int index_1, int index_2) {
  Point<Dim> value = points[index_1];
  points[index_1] = points[index_2];
  points[index_2] = value;
}

/**
 * @file heap.cpp
 * Implementation of a heap class.
 */
using namespace std;
template <class T, class Compare>
size_t heap<T, Compare>::root() const
{
    // @TODO Update to return the index you are choosing to be your root.
    // return 0;
    return 1;
}

template <class T, class Compare>
size_t heap<T, Compare>::leftChild(size_t currentIdx) const
{
    // @TODO Update to return the index of the left child.
    if (currentIdx * 2 < _elems.size()) {
        return currentIdx * 2;
    }
    return 0;
}

template <class T, class Compare>
size_t heap<T, Compare>::rightChild(size_t currentIdx) const
{
    // @TODO Update to return the index of the right child.
    if (currentIdx * 2 + 1 < _elems.size()) {
        return currentIdx * 2 + 1;
    }
    return 0;
}

template <class T, class Compare>
size_t heap<T, Compare>::parent(size_t currentIdx) const
{
    // @TODO Update to return the index of the parent.
    size_t parentIdx = (size_t) ((int) currentIdx / 2);
    if (parentIdx > 0) {
        return parentIdx;
    }
    return 0;
}

template <class T, class Compare>
bool heap<T, Compare>::hasAChild(size_t currentIdx) const
{
    // @TODO Update to return whether the given node has a child
    if (leftChild(currentIdx) != 0) {
        return true;
    }
    return false;
}

template <class T, class Compare>
size_t heap<T, Compare>::maxPriorityChild(size_t currentIdx) const
{
    // @TODO Update to return the index of the child with highest priority
    ///   as defined by higherPriority()

    if (rightChild(currentIdx) != 0) {
        if (higherPriority(_elems[leftChild(currentIdx)], _elems[rightChild(currentIdx)])) {
            return leftChild(currentIdx);
        }
        return rightChild(currentIdx);
    }
    if (leftChild(currentIdx) != 0) {
        return leftChild(currentIdx);
    }
    return 0;
}

template <class T, class Compare>
void heap<T, Compare>::heapifyDown(size_t currentIdx)
{
    // @TODO Implement the heapifyDown algorithm.
    // if the current index is not a leaf
    if (hasAChild(currentIdx)) {
        size_t min = maxPriorityChild(currentIdx);
        if (higherPriority(_elems[min], _elems[currentIdx])) {
            std::swap(_elems[min], _elems[currentIdx]);
            heapifyDown(min);
        }
    }
}

template <class T, class Compare>
void heap<T, Compare>::heapifyUp(size_t currentIdx)
{
    if (currentIdx == root())
        return;
    size_t parentIdx = parent(currentIdx);
    if (higherPriority(_elems[currentIdx], _elems[parentIdx])) {
        std::swap(_elems[currentIdx], _elems[parentIdx]);
        heapifyUp(parentIdx);
    }
}

template <class T, class Compare>
heap<T, Compare>::heap()
{
    // @TODO Depending on your implementation, this function may or may
    ///   not need modifying
    _elems.push_back(T());
}

template <class T, class Compare>
heap<T, Compare>::heap(const std::vector<T>& elems)
{
    // @TODO Construct a heap using the buildHeap algorithm
    // the first element, can be anything cuz we'll never use it
    _elems.push_back(T());
    for (unsigned i = 0; i < elems.size(); i++) {
        _elems.push_back(elems[i]);
    }
    buildHeap();
}

template <class T, class Compare>
void heap<T, Compare>::buildHeap()
{
    for (size_t i = parent(_elems.size()); i > 0; i--) {
        heapifyDown(i);
    }
}

template <class T, class Compare>
T heap<T, Compare>::pop()
{
    // @TODO Remove, and return, the element with highest priority
    if (_elems.size() > 0) {
        std::swap(_elems[root()], _elems[_elems.size() - 1]);
        T min = _elems[_elems.size() - 1];
        _elems.pop_back();
        heapifyDown(root());
        return min;
    }
    return T();
}

template <class T, class Compare>
T heap<T, Compare>::peek() const
{
    // @TODO Return, but do not remove, the element with highest priority
    if (!empty()) {
        return _elems[1];
    }
    return T();
}

template <class T, class Compare>
void heap<T, Compare>::push(const T& elem)
{
    // @TODO Add elem to the heap
    _elems.push_back(elem);
    heapifyUp(_elems.size() - 1);
}

template <class T, class Compare>
void heap<T, Compare>::updateElem(const size_t & idx, const T& elem)
{
    // @TODO In-place updates the value stored in the heap array at idx
    // Corrects the heap to remain as a valid heap even after update
    _elems[idx + 1] = elem;
    heapifyDown(root());
}


template <class T, class Compare>
bool heap<T, Compare>::empty() const
{
    // @TODO Determine if the heap is empty
    return !(_elems.size() > 2);
}

template <class T, class Compare>
void heap<T, Compare>::getElems(std::vector<T> & heaped) const
{
    for (size_t i = root(); i < _elems.size(); i++) {
        heaped.push_back(_elems[i]);
    }
}

#include <iostream>
/**
 * @file list.cpp
 * Doubly Linked List (MP 3).
 */


/**
 * Returns a ListIterator with a position at the beginning of
 * the List.
 */

template <typename T>
typename List<T>::ListIterator List<T>::begin() const {
  // @TODO: graded in MP3.1
  if (head_ != NULL) {
    return List<T>::ListIterator(head_);
  }
  return NULL;
}

/**
 * Returns a ListIterator one past the end of the List.
 */
template <typename T>
typename List<T>::ListIterator List<T>::end() const {
  // @TODO: graded in MP3.1
  if (tail_ != NULL) {
    return List<T>::ListIterator(tail_->next);
  }
  return NULL;
}

/**
 * Destroys the tempent List. This function should ensure that
 * memory does not leak on destruction of a list.
 */
template <typename T>
List<T>::~List() {
  /// @todo Graded in MP3.1
  _destroy();
}

/**
 * Destroys all dynamically allocated memory associated with the tempent
 * List class.
 */
template <typename T>
void List<T>::_destroy() {
  /// @todo Graded in MP3.1
  ListNode *node = head_;
  ListNode *temp = NULL;
  while (node != NULL){
      temp = node;
      node = node->next;
      delete temp;
    }
    length_ = 0;
    temp = NULL;
    node = NULL;
    tail_ = NULL;
}

/**
 * Inserts a new node at the front of the List.
 * This function **SHOULD** create a new ListNode.
 *
 * @param ndata The data to be inserted.
 */
template <typename T>
void List<T>::insertFront(T const & ndata) {
  /// @todo Graded in MP3.1
  ListNode * temp = new ListNode(ndata);

  if (head_ == NULL || length_ == 0){
    temp->next = NULL;
    temp->prev = NULL;
    head_ = temp;
    tail_ = temp;
    length_ = 1;
  } else {
    length_++;
    temp->next = head_;
    temp->prev = NULL;
    head_->prev = temp;
    head_ = temp;
  }
}

/**
 * Inserts a new node at the back of the List.
 * This function **SHOULD** create a new ListNode.
 *
 * @param ndata The data to be inserted.
 */
template <typename T>
void List<T>::insertBack(const T & ndata) {
  /// @todo Graded in MP3.1
  ListNode * temp = new ListNode(ndata);

  if (head_ == NULL || length_ == 0){
    temp->next = NULL;
    temp->prev = NULL;
    head_ = temp;
    tail_ = temp;
    length_ = 1;
  } else {
    length_++;
    tail_->next = temp;
    temp->prev = tail_;
    temp->next = NULL;
    tail_ = temp;
  }
}

/**
 * Reverses the tempent List.
 */
template <typename T>
void List<T>::reverse() {
  reverse(head_, tail_);
}

/**
 * Helper function to reverse a sequence of linked memory inside a List,
 * starting at startPoint and ending at endPoint. You are responsible for
 * updating startPoint and endPoint to point to the new starting and ending
 * points of the rearranged sequence of linked memory in question.
 *
 * @param startPoint A pointer reference to the first node in the sequence
 *  to be reversed.
 * @param endPoint A pointer reference to the last node in the sequence to
 *  be reversed.
 */
 template <class T>
 void List<T>::reverse(ListNode *& startPoint, ListNode *& endPoint) {

   if (startPoint == NULL || endPoint == NULL || startPoint == endPoint){
      return;
   }

   ListNode* start_temp = startPoint;
   ListNode* end_temp = endPoint;
   ListNode* start_prev = startPoint->prev;
   ListNode* end_next = endPoint->next;

   while (startPoint != end_next) {
     ListNode* temp = startPoint->next;
     startPoint->next = startPoint->prev;
     startPoint->prev = temp;
     startPoint = startPoint->prev;
   }

   endPoint->prev = start_prev;
   endPoint = start_temp;
   endPoint->next = end_next;

   if (start_prev==NULL && end_next==NULL){
     head_ = end_temp;
     tail_ = start_temp;
   }
   else if (start_prev==NULL){
     end_next->prev = start_temp;
     head_ = end_temp;
   }
   else if (end_next == NULL){
     start_prev->next = end_temp;
     tail_ = start_temp;
   }
   else{
     start_prev->next = end_temp;
     end_next->prev = start_temp;
   }
 }


/**
 * Reverses blocks of size n in the tempent List. You should use your
 * reverse( ListNode * &, ListNode * & ) helper function in this method!
 *
 * @param n The size of the blocks in the List to be reversed.
 */
 template <class T>
 void List<T>::reverseNth(int n) {
   /// @todo Graded in MP3.1
   if (head_ == NULL) {
     return;
   }

   ListNode* start_temp = head_;
   ListNode* start_next = head_;

   while (start_next->next != NULL && start_temp->next != NULL){
     int i = 0;
     start_next = start_temp;
     while (i < n - 1 && start_next->next != NULL){
       start_next = start_next->next;
       i++;
     }
     reverse(start_temp,start_next);
     start_temp = start_next->next;
   }
 }

/**
 * Modifies the List using the waterfall algorithm.
 * Every other node (starting from the second one) is removed from the
 * List, but appended at the back, becoming the new tail. This continues
 * until the next thing to be removed is either the tail (**not necessarily
 * the original tail!**) or NULL.  You may **NOT** allocate new ListNodes.
 * Note that since the tail should be continuously updated, some nodes will
 * be moved more than once.
 */
 template <class T>
 void List<T>::waterfall() {
   /// @todo Graded in MP3.1
   ListNode* temp = head_;
   if (head_ == NULL || head_==tail_ || tail_==NULL || length_ == 0) {
      return;
   }

   while(temp->next != tail_ && temp->next != NULL){
     ListNode* temp_next = temp->next;
     ListNode* temp1 = temp->next->next;
     temp1->prev = temp;
     temp->next = temp1;
     temp_next->next = NULL;
     temp_next->prev = tail_;
     tail_->next = temp_next;
     tail_ = temp_next;
     temp = temp->next;
   }
 }

/**
 * Splits the given list into two parts by dividing it at the splitPoint.
 *
 * @param splitPoint Point at which the list should be split into two.
 * @return The second list created from the split.
 */
template <typename T>
List<T> List<T>::split(int splitPoint) {
    if (splitPoint > length_)
        return List<T>();

    if (splitPoint < 0)
        splitPoint = 0;

    ListNode * secondHead = split(head_, splitPoint);

    int oldLength = length_;
    if (secondHead == head_) {
        // tempent list is going to be empty
        head_ = NULL;
        tail_ = NULL;
        length_ = 0;
    } else {
        // set up tempent list
        tail_ = head_;
        while (tail_ -> next != NULL)
            tail_ = tail_->next;
        length_ = splitPoint;
    }

    // set up the returned list
    List<T> ret;
    ret.head_ = secondHead;
    ret.tail_ = secondHead;
    if (ret.tail_ != NULL) {
        while (ret.tail_->next != NULL)
            ret.tail_ = ret.tail_->next;
    }
    ret.length_ = oldLength - splitPoint;
    return ret;
}

/**
 * Helper function to split a sequence of linked memory at the node
 * splitPoint steps **after** start. In other words, it should disconnect
 * the sequence of linked memory after the given number of nodes, and
 * return a pointer to the starting node of the new sequence of linked
 * memory.
 *
 * This function **SHOULD NOT** create **ANY** new List or ListNode objects!
 *
 * @param start The node to start from.
 * @param splitPoint The number of steps to walk before splitting.
 * @return The starting node of the sequence that was split off.
 */
template <typename T>
typename List<T>::ListNode * List<T>::split(ListNode * start, int splitPoint) {
  /// @todo Graded in MP3.2
  // Node splited = start;
  if (splitPoint == 0){
    return start;
  }
  if (start == NULL || start->next == NULL){
    return NULL;
  }
  ListNode * newStart = head_;
  int i = splitPoint;
  while (i != 0){
      newStart = newStart->next;
      i--;
  }
  newStart->prev->next = NULL;
  newStart->prev = NULL;
  return newStart;
}

/**
 * Merges the given sorted list into the tempent sorted list.
 *
 * @param otherList List to be merged into the tempent list.
 */
template <typename T>
void List<T>::mergeWith(List<T> & otherList) {
    // set up the tempent list
    head_ = merge(head_, otherList.head_);
    tail_ = head_;

    // make sure there is a node in the new list
    if (tail_ != NULL) {
        while (tail_->next != NULL)
            tail_ = tail_->next;
    }
    length_ = length_ + otherList.length_;

    // empty out the parameter list
    otherList.head_ = NULL;
    otherList.tail_ = NULL;
    otherList.length_ = 0;
}

/**
 * Helper function to merge two **sorted** and **independent** sequences of
 * linked memory. The result should be a single sequence that is itself
 * sorted.
 *
 * This function **SHOULD NOT** create **ANY** new List objects.
 *
 * @param first The starting node of the first sequence.
 * @param second The starting node of the second sequence.
 * @return The starting node of the resulting, sorted sequence.
 */
template <typename T>
typename List<T>::ListNode * List<T>::merge(ListNode * first, ListNode* second) {
  /// @todo Graded in MP3.2
  if (first == NULL && second == NULL){
    return NULL;
  } else if (second == NULL || first == second){
    return first;
  } else if (first == NULL){
    return second;
  }
//  cout << "1" <<endl;
  // problem at the first-half
  ListNode * small;
  if (first->data < second->data){
    small = first;
    first = first->next;
  } else {
    small = second;
    second = second->next;
  }
  ListNode * newHead = small;
  while (first != NULL && second != NULL) {
//    cout << "2" <<endl;
    if (first->data < second->data) {
  //    cout << "3" <<endl;
      small->next = first;
      first->prev = small;
      first = first->next;
    } else {
      small->next = second;
      second->prev = small;
      second = second->next;
    }
    small = small->next;
  }
  if (first == NULL && second != NULL){
    small->next = second;
    second->prev = small;
  } else {
    small->next = first;
    first->prev = small;
  }
  return newHead;
  //     if (first->next != NULL) {
  //   //    cout << "4" <<endl;
  //       first->next = temp2;
  //       temp2->prev = first;
  //       temp2->next = temp1->next;
  //       first->next->prev = temp2;
  //       if (second->next == NULL) {
  //   //      cout << "5" <<endl;
  //         break;
  //       }
  //       first = first->next;
  //       second = second->next;
  //     } else if (first->next == NULL) {
  //   //    cout << "6" <<endl;
  //       first->next = temp2;
  //       temp2->prev = first;
  //       if (second->next == NULL) {
  //         break;
  //       }
  //       first = first->next;
  //       second = second->next;
  //     }
  //   } else {
  //     if (first->prev != NULL) {
  //       first->prev = temp2;
  //       temp2->next = first;
  //       temp2->prev = temp1->prev;
  //       first->prev->next = temp2;
  //       if (second->next == NULL) {
  //         break;
  //       }
  //       first = first->next;
  //       second = second->next;
  //     } else if (first->prev == NULL) {
  //       first->prev = temp2;
  //       temp2->next = first;
  //       if (second->next == NULL) {
  //         break;
  //       }
  //       first = first->next;
  //       second = second->next;
  //     }
  //   }
  // }
  // return first;
}

/**
 * Sorts the tempent list by applying the Mergesort algorithm.
 */
template <typename T>
void List<T>::sort() {
    if (empty())
        return;
    head_ = mergesort(head_, length_);
    tail_ = head_;
    while (tail_->next != NULL)
        tail_ = tail_->next;
}

/**
 * Sorts a chain of linked memory given a start node and a size.
 * This is the recursive helper for the Mergesort algorithm (i.e., this is
 * the divide-and-conquer step).
 *
 * @param start Starting point of the chain.
 * @param chainLength Size of the chain to be sorted.
 * @return A pointer to the beginning of the now sorted chain.
 */
template <typename T>
typename List<T>::ListNode* List<T>::mergesort(ListNode * start, int chainLength) {
  if (chainLength == 1){
    return start;
  }
  if (start == NULL){
    return NULL;
  }
  ListNode * other =  start;
  int halfway = chainLength / 2;
  int count = halfway;
  while (count != 0){
    other = other->next;
    count--;
  }
  other->prev->next = NULL;
  other->prev = NULL;
  start = mergesort(start, halfway);
  other = mergesort(other, chainLength - halfway);

  start = merge(start, other);
  return start;
}

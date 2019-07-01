/**
 * @file NimLearner.cpp
 * CS 225: Data Structures
 */

#include "NimLearner.h"
#include <ctime>

#include <string>
using namespace std;

/**
 * Constructor to create a game of Nim with `startingTokens` starting tokens.
 *
 * This function creates a graph, `g_` representing all of the states of a
 * game of Nim with vertex labels "p#-X", where:
 * - # is the current player's turn; p1 for Player 1, p2 for Player2
 * - X is the tokens remaining at the init of a player's turn
 *
 * For example:
 *   "p1-4" is Player 1's turn with four (4) tokens remaining
 *   "p2-8" is Player 2's turn with eight (8) tokens remaining
 *
 * All legal moves between states are created as edges with initial weights
 * of 0.
 *
 * @param startingTokens The number of starting tokens in the game of Nim.
 */
NimLearner::NimLearner(unsigned startingTokens) : g_(true, true) {
    /* Your code goes here! */
    unsigned i = startingTokens;
    g_.insertVertex("p1-" + to_string(0));
    g_.insertVertex("p2-" + to_string(0));
    while(i){
      g_.insertVertex("p1-" + to_string(i));
      g_.insertVertex("p2-" + to_string(i));
      i--;
    }

    int j = startingTokens;
    while(j > 2){
      g_.insertEdge(("p1-" + to_string(j)) , ("p2-" + to_string(j - 1)));
      g_.setEdgeWeight(("p1-" + to_string(j)) , ("p2-" + to_string(j - 1)), 0);

      g_.insertEdge(("p1-" + to_string(j)) , ("p2-" + to_string(j - 2)));
      g_.setEdgeWeight(("p1-" + to_string(j)) , ("p2-" + to_string(j - 2)), 0);

      g_.insertEdge(("p2-" + to_string(j)) , ("p1-" + to_string(j - 1)));
      g_.setEdgeWeight(("p2-" + to_string(j)) , ("p1-" + to_string(j - 1)), 0);

      g_.insertEdge(("p2-" + to_string(j)) , ("p1-" + to_string(j - 2)));
      g_.setEdgeWeight(("p2-" + to_string(j)) , ("p1-" + to_string(j - 2)), 0);

      j--;
    }
    if(startingTokens > 1) {
      g_.insertEdge(("p1-" + to_string(2)) , ("p2-" + to_string(1)));
      g_.setEdgeWeight(("p1-" + to_string(2)) , ("p2-" + to_string(1)), 0);

      g_.insertEdge(("p1-" + to_string(2)) , ("p2-" + to_string(0)));
      g_.setEdgeWeight(("p1-" + to_string(2)) , ("p2-" + to_string(0)), 0);

      g_.insertEdge(("p2-" + to_string(2)) , ("p1-" + to_string(1)));
      g_.setEdgeWeight(("p2-" + to_string(2)) , ("p1-" + to_string(1)), 0);

      g_.insertEdge(("p2-" + to_string(2)) , ("p1-" + to_string(0)));
      g_.setEdgeWeight(("p2-" + to_string(2)) , ("p1-" + to_string(0)) , 0);
    }

    g_.insertEdge(("p1-" + to_string(1)) , ("p2-" + to_string(0)));
    g_.setEdgeWeight(("p1-" + to_string(1)) , ("p2-" + to_string(0)), 0);

    g_.insertEdge(("p2-" + to_string(1)) , ("p1-" + to_string(0)));
    g_.setEdgeWeight(("p2-" + to_string(1)) , ("p1-" + to_string(0)), 0);
    startingVertex_ = ("p1-" + to_string(startingTokens));
}

/**
 * Plays a random game of Nim, returning the path through the state graph
 * as a vector of `Edge` classes.  The `origin` of the first `Edge` must be
 * the vertex with the label "p1-#", where # is the number of starting
 * tokens.  (For example, in a 10 token game, result[0].origin must be the
 * vertex "p1-10".)
 *
 * @returns A random path through the state space graph.
 */
vector<Edge> NimLearner::playRandomGame() const {
  vector<Edge> path;
 /* Your code goes here! */
 Vertex init = ("p1-" + to_string(g_.getVertices().size() / 2 - 1));

 while(g_.getAdjacent(init).size() != 0){
   vector<Vertex> vertices = g_.getAdjacent(init);
   if(g_.getAdjacent(init).size() == 2){
     if(rand() % 2 == 0){
       if(g_.edgeExists(init, vertices[0])){
         Edge newEdge = g_.getEdge(init,vertices[0]);
         path.push_back(newEdge);
         init = vertices[0];
       } else {
         break;
       }
     } else {
       if(g_.edgeExists(init, vertices[1])){
         Edge newEdge = g_.getEdge(init,vertices[1]);
         path.push_back(newEdge);
         init = vertices[1];
       } else {
         break;
       }
     }
   } else {
     if(g_.edgeExists(init, vertices[0])){
       Edge newEdge = g_.getEdge(init,vertices[0]);
       path.push_back(newEdge);
       init = vertices[0];
     } else {
       break;
     }
   }
 }
 return path;
}

/*
 * Updates the edge weights on the graph based on a path through the state
 * tree.
 *
 * If the `path` has Player 1 winning (eg: the last vertex in the path goes
 * to Player 2 with no tokens remaining, or "p2-0", meaning that Player 1
 * took the last token), then all choices made by Player 1 (edges where
 * Player 1 is the source vertex) are positiveed by increasing the edge weight
 * by 1 and all choices made by Player 2 are punished by changing the edge
 * weight by -1.
 *
 * Likewise, if the `path` has Player 2 winning, Player 2 choices are
 * positiveed and Player 1 choices are punished.
 *
 * @param path A path through the a game of Nim to learn.
 */
void NimLearner::updateEdgeWeights(const vector<Edge> & path) {
 /* Your code goes here! */
 int size = path.size() - 1;
 int ct = 1;
 int positive = 1;

while(size >= 0) {
  if(ct % 2 == 1) {
    Vertex source_ = path[size].source;
    Vertex dest_ = path[size].dest;
    Edge newEdge = path[size];
    g_.setEdgeWeight(source_,dest_, g_.getEdgeWeight(source_,dest_) + 1);
    ct++;
    size--;
  } else {
    Vertex source_ = path[size].source;
    Vertex dest_ = path[size].dest;
    g_.setEdgeWeight(source_,dest_, g_.getEdgeWeight(source_,dest_) - 1);
    ct++;
    size--;
  }

  }
}

/**
 * Label the edges as "WIN" or "LOSE" based on a threshold.
 */
void NimLearner::labelEdgesFromThreshold(int threshold) {
  for (const Vertex & v : g_.getVertices()) {
    for (const Vertex & w : g_.getAdjacent(v)) {
      int weight = g_.getEdgeWeight(v, w);

      // Label all edges with positve weights as "WINPATH"
      if (weight > threshold)           { g_.setEdgeLabel(v, w, "WIN"); }
      else if (weight < -1 * threshold) { g_.setEdgeLabel(v, w, "LOSE"); }
    }
  }
}

/**
 * Returns a constant reference to the state space graph.
 *
 * @returns A constant reference to the state space graph.
 */
const Graph & NimLearner::getGraph() const {
  return g_;
}

#include "Graph.h"
#include "Edge.h"
#include "Vertex.h"

#include <string>
#include <iostream>



using namespace std;

/**
* @return The number of vertices in the Graph
*/
template <class V, class E>
unsigned int Graph<V,E>::size() const {
  // TODO: Part 2
  return ((unsigned)this->vertexMap.size());
}


/**
* @return Returns the degree of a given vertex.
* @param v Given vertex to return degree.
*/
template <class V, class E>
unsigned int Graph<V,E>::degree(const V & v) const {
  // TODO: Part 2
  return ((unsigned)this->adjList[v.key()].size());
}

/**
* Inserts a Vertex into the Graph by adding it to the Vertex map and adjacency list
* @param key The key of the Vertex to insert
* @return The inserted Vertex
*/
template <class V, class E>
V & Graph<V,E>::insertVertex(std::string key) {
  // TODO: Part 2
  V & v = *(new V(key));
  pair<string, V &> new_pair (key, v);
  this->vertexMap.insert(new_pair);

  list<edgeListIter> newlist;
  pair<string, list<edgeListIter>> adj_pair (key, newlist);
  this->adjList.insert(adj_pair);
  return v;
}


/**
* Removes a given Vertex
* @param v The Vertex to remove
*/
template <class V, class E>
void Graph<V,E>::removeVertex(const std::string & key) {
  // TODO: Part 2
  list<edgeListIter> & edges = adjList[key];
  for (auto & i: edges) {
    removeEdge(i);
  }
  this->adjList.erase(adjList.find(key));
  this->vertexMap.erase(vertexMap.find(key));
}


/**
* Inserts an Edge into the adjacency list
* @param v1 The source Vertex
* @param v2 The destination Vertex
* @return The inserted Edge
*/
template <class V, class E>
E & Graph<V,E>::insertEdge(const V & v1, const V & v2) {
  // TODO: Part 2
  E & e = *(new E(v1, v2));
  this->edgeList.push_front(e);
  adjList[v1.key()].push_back(this->edgeList.begin());
  adjList[v2.key()].push_back(this->edgeList.begin());
  return e;
}


/**
* Removes an Edge from the Graph
* @param key1 The key of the ource Vertex
* @param key2 The key of the destination Vertex
*/
template <class V, class E>
void Graph<V,E>::removeEdge(const std::string key1, const std::string key2) {
  // TODO: Part 2
  list<edgeListIter> key1_edges = adjList[key1];

  for (auto & iter : key1_edges) {
    const E & e = *iter;
    if ((e.directed() && e.dest().key() == key2) || (e.dest().key() == key2 || e.source().key() == key2)) {
      removeEdge(iter);
      break;
    }
  }
}


/**
* Removes an Edge from the adjacency list at the location of the given iterator
* @param it An iterator at the location of the Edge that
* you would like to remove
*/
template <class V, class E>
void Graph<V,E>::removeEdge(const edgeListIter & it) {
  // TODO: Part 2
  E & e = *it;
  string dest = e.dest().key();
  string source = e.source().key();

  adjList[source].remove(it);
  adjList[dest].remove(it);
  edgeList.erase(it);
}


/**
* @param key The key of an arbitrary Vertex "v"
* @return The list edges (by reference) that are adjacent to "v"
*/
template <class V, class E>
const std::list<std::reference_wrapper<E>> Graph<V,E>::incidentEdges(const std::string key) const {
  // TODO: Part 2
  std::list<std::reference_wrapper<E>> edges;

  for (auto iter : adjList.at(key)) {
    edges.push_back(*iter);
  }
  return edges;
}


/**
* Return whether the two vertices are adjacent to one another
* @param key1 The key of the source Vertex
* @param key2 The key of the destination Vertex
* @return True if v1 is adjacent to v2, False otherwise
*/
template <class V, class E>
bool Graph<V,E>::isAdjacent(const std::string key1, const std::string key2) const {
  // TODO: Part 2
  auto & key1_edges = adjList.at(key1);

  for (auto & i: key1_edges) {
    E & e = *i;
    if (e.dest().key() == key2 && e.source().key() == key1) {
      return true;
    }
  }

  return false;
}

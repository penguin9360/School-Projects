#include <queue>
#include <algorithm>
#include <string>
#include <list>

#include <unordered_map>

using namespace std;
using std::unordered_map;

/**
 * Returns an std::list of vertex keys that creates some shortest path between `start` and `end`.
 *
 * This list MUST include the key of the `start` vertex as the first vertex in the list, the key of
 * the `end` vertex as the last element in the list, and an ordered list of all vertices that must
 * be traveled along the shortest path.
 *
 * For example, the path a -> c -> e returns a list with three elements: "a", "c", "e".
 *
 * @param start The key for the starting vertex.
 * @param end   The key for the ending vertex.
 */
template <class V, class E>
std::list<std::string> Graph<V,E>::shortestPath(const std::string start, const std::string end) {

  list<string> path;
  unordered_map<string, string> visited;
  queue<string> vertex_queue;

  visited[start] = "visited";
  vertex_queue.push(start);

  while (!vertex_queue.empty()) {
    string curr = vertex_queue.front();
    vertex_queue.pop();
    list<reference_wrapper<E>> incident_edges = incidentEdges(curr);
    for (auto it = incident_edges.begin(); it != incident_edges.end(); ++it) {
      E & tmp_e = *it;
      string v;
      if (tmp_e.dest().key() != curr) {
        v = tmp_e.dest().key();
      } else {
        v = tmp_e.source().key();
      }
      if (visited.find(v) == visited.end()) {
        visited[v] = curr;
        vertex_queue.push(v);
      }
    }
  }

  path.push_front(end);
  string str_iter = end;

  while (str_iter != start) {
    string prev = visited[str_iter];
    path.push_front(prev);
    str_iter = prev;
  }
  return path;
}

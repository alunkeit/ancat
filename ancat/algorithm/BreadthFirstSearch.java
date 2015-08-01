/**
 *   This file is part of ancat.
 *
 *   ancat is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ancat is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ancat.  If not, see <http://www.gnu.org/licenses/>.
 */
package ancat.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 *         BreadthFirstSearch for unweighted search operation. Scans the
 *         underlying graph in bfs style and provides an instance of
 *         DirectedGraphPath containing the path between start and end node.
 *         This implementation may also be used to figure out the connected
 *         components of a graph. BFS is one of the basic algorithms used for
 *         graph scanning in ancat.
 * 
 */
public class BreadthFirstSearch<V, E> extends GraphScanner<V>
{
  /**
   * Graph oject the implementation is working on
   */
  protected Graph<V, E> _graph;

  /**
   * Queue object used for bfs search
   */
  protected Queue<V> _queue = null;

  /**
   * Map storing all visited nodes
   */
  protected Map<V, SearchNode<V>> _map = null;

  /**
   * Result of the operation
   */
  protected DirectedGraphPath<V, E> _path = null;

  /**
   * Class Constructor
   * 
   * @param graph
   */
  public BreadthFirstSearch( Graph<V, E> graph )
  {
    _graph = graph;
  }

  public void search( V s, V t )
  {
    _queue = new LinkedList<V>();

    _map = new HashMap<V, SearchNode<V>>();

    SearchNode<V> sn = new SearchNode<V>();
    sn._pred = null;
    sn._node = s;

    _map.put(s, sn);
    _queue.add(s);

    while( !_queue.isEmpty() )
    {
      V k = _queue.poll();

      if( k.equals(t) )
      {
        _path = new DirectedGraphPath<V, E>();

        // store check mark in result -> _start is the beginning containing no
        // delta- edges (incoming edges)
        _path._root = _map.get(s)._node;
        _path._target = t;

        SearchNode<V> nd = _map.get(k);

        // adding vertices
        while( nd != null )
        {
          _path.addVertex(nd._node);

          nd = _map.get(nd._pred);
        }

        // adding edges
        nd = _map.get(k);

        while( nd != null )
        {
          if( nd._pred != null )
            _path.addEdge(_graph.findEdge(nd._pred, nd._node), nd._pred,
                nd._node);

          nd = _map.get(nd._pred);
        }

        break;
      }

      Collection<E> edges = _graph.getOutEdges(k);
      LinkedList<V> neighbors = new LinkedList<V>();

      for( E e : edges )
      {
        neighbors.add(_graph.getDest(e));
      }

      for( V neighbor : neighbors )
      {
        // check if the neighbor node has already been visited
        if( _map.containsKey(neighbor) == false )
        {
          // if not, create a new helper object and store the node in the map
          sn = new SearchNode<V>();
          sn._node = neighbor;
          sn._pred = k;

          _map.put(neighbor, sn);
          _queue.add(neighbor);
        }
      }
    }
  }

  /**
   * Returns the operation result
   * 
   * @return
   */
  public DirectedGraphPath<V, E> getPath()
  {
    return _path;
  }

  /**
   * Returns the map containing the visited nodes. A better way would be
   * providing iterators to test the visited nodes. The returned map is
   * unordered, meaning, that the order used for storing / accessing the
   * elements is not the same order used for graph scanning.
   * 
   * @return
   */
  public Map<V, SearchNode<V>> getVisited()
  {
    return _map;
  }
}

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 *         This provides an implementation of BFS returning all paths to the
 *         target node.
 */
public class DepthFirstSearch<V, E> extends GraphScanner<V>
{

  /**
   * Internal graph representation
   */
  Graph<V, E> _graph;

  /**
   * Logger instance
   */
  Logger _logger = Logger.getRootLogger();

  /**
   * LinkedList for temporary storing the visited path
   */
  LinkedList<V> nodes = new LinkedList<V>();

  /**
   * Node to start from
   */
  V _start;

  /**
   * A list containing the paths
   */
  LinkedList<DirectedGraphPath<V, E>> _paths = new LinkedList<DirectedGraphPath<V, E>>();

  /**
   * Constructor
   * 
   * @param graph
   *          - The graph the algorithm is working on
   */
  public DepthFirstSearch( Graph<V, E> graph )
  {
    _graph = graph;
  }

  @Override
  public void search( V s, V t )
  {
    _start = s;

    recursiveScan(s, t);
  }

  /**
   * Recursive scan function for testing the graph
   * 
   * @param s
   *          - bfs scan starts from source s
   * @param t
   *          - bfs scan ends in target t
   */
  void recursiveScan( V s, V t )
  {
    boolean containsCircle = false;

    if( nodes.contains(s) )
    {
      _logger.debug("circle detected!");
      containsCircle = true;
    }

    nodes.add(s);

    Collection<E> edges = _graph.getOutEdges(s);
    LinkedList<V> neighbors = new LinkedList<V>();

    // target node found or no out edges available
    if( (t == null && edges.size() == 0) || (t != null && s.equals(t))
        || containsCircle == true )
    {
      if( t != null )
        _logger.debug("bfs scan finished, target has been found");
      else
      {
        _logger
            .debug("bfs scan finished, no target node given, active node is := "
                + s);
      }

      // create new path instance
      DirectedGraphPath<V, E> path = new DirectedGraphPath<V, E>();

      for( V v : nodes )
      {
        // _logger.debug("node := " + v);
        // add node to path
        path.addVertex(v);
      }
      // backward iterator
      Iterator<V> iter = nodes.descendingIterator();

      // add edges to the path
      while( iter.hasNext() )
      {
        V v = iter.next();

        Collection<E> col = _graph.getInEdges(v);

        for( E e : col )
        {
          if( nodes.contains(_graph.getSource(e)) )
          {
            path.addEdge(e, _graph.getSource(e), v);

            break;
          }
        }
      }

      path._root = _start;
      path._target = s;

      _paths.add(path);

      nodes.remove(s);

      _logger.debug("path finished...");

      return;
    }

    for( E e : edges )
    {
      neighbors.add(_graph.getDest(e));
    }

    for( V v : neighbors )
    {
      recursiveScan(v, t);
    }

    nodes.remove(s);
  }

  /**
   * Returns a list with all identified paths
   * 
   * @return - a list of paths or an empty set
   */
  public List<DirectedGraphPath<V, E>> paths()
  {
    return _paths;
  }

  /**
   * Returns the count of found paths
   * 
   * @return
   */
  public int pathCount()
  {
    return _paths.size();
  }
}

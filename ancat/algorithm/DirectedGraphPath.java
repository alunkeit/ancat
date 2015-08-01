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

import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * @author a.lunkeit
 * 
 *         This implementation represents a path through an Directed Graph.
 * 
 */
public class DirectedGraphPath<V, E> extends DirectedSparseGraph<V, E>
{

  protected V _root;

  protected V _target;

  /**
   * Generated serial version uid
   */
  private static final long serialVersionUID = -6770838538364184610L;

  public DirectedGraphPath()
  {
    super();
  }

  public V getRoot()
  {
    return _root;
  }

  public V getTarget()
  {
    return _target;
  }

}

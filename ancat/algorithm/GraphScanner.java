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

/**
 * @author alunkeit
 * 
 */
public abstract class GraphScanner<V>
{

  /**
   * Common search interface function
   * 
   * @param s
   * @param t
   */
  public abstract void search( V s, V t );

  /**
   * 
   * @author alunkeit
   * 
   * @param <V>
   * 
   *          An internal helper object used for GraphScanning
   */
  public static class SearchNode<V>
  {
    V _node;

    V _pred;

    public V node()
    {
      return _node;
    }

    public V pred()
    {
      return _pred;
    }
  }
}

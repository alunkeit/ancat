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
package ancat.common;

/**
 * 
 * @author alunkeit
 * 
 *         A special Vertex implementation describing an executable file.
 */
public class ExecutableFile extends Vertex
{

  /**
   * 
   */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 7473661662369795669L;

  /**
   * Constructor
   */
  public ExecutableFile( String elementID )
  {
    super(elementID);
  }

  @Override
  public String toString()
  {
    return "executable file";
  }
}

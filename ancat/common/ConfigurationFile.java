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
 * This file contains a specific implementation of a Vertex describing a Configuration file.
 */
public class ConfigurationFile extends Vertex
{

  /**
   * 
   */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 2926282403293654113L;

  public ConfigurationFile( String elementID )
  {
    super( elementID );
  }

  @Override
  public String toString()
  {
    return "configuration file";
  }
}

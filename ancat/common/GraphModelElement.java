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
 * @author a.lunkeit
 *
 * A base class used by depending implementations of Vertex and Edge to
 * store specific information of the software model.
 */
public class GraphModelElement
{

  /**
   * This is the id of the element in the XML structured model
   */
  protected String _elementID;
  
  protected GraphModelElement( String elementID )
  {
    this._elementID = elementID;
  }
  
  /**
   * Returns the element id of the element in the XML structued model.
   * 
   * @return A String containing the element id
   */
  public String getElementID()
  {
    return _elementID;
  }
}

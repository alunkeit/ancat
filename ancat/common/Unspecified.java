/**
 * This file is part of ancat.
 * 
 * ancat is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * ancat is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * ancat. If not, see <http://www.gnu.org/licenses/>.
 */
package ancat.common;

/**
 * 
 * @author alunkeit
 * 
 *         A vertex implementation describing an unrecognized software object.
 *         This Vertex type is generated for all objects that have no own
 *         specialization in the Software model.
 */
public class Unspecified extends Vertex
{

  /**
   * serialVersionUID
   */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = 4951733520647149127L;

  /**
   * Constructor
   */
  public Unspecified( String elementID )
  {
    super( elementID );
  }

  @Override
  public String toString()
  {
    if( _properties.containsKey( "filename" ) )
      return _properties.get( "filename" );

    return "unspecified";
  }

}

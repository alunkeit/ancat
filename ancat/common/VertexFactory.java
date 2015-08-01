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
 */
public class VertexFactory
{
  public static Vertex nodeToVertex( ModelNode node )
  {
    String type = node.getAttribute( "itemtype" );
    
    Vertex v = null;
    
    if( null == type )
    {
      v = new Unspecified( node.getElementID());
    }
    
    else if( type.equalsIgnoreCase( "executable" ))
    {
      v = new ExecutableFile( node.getElementID() );
    }
    
    else if( type.equalsIgnoreCase( "dll" ))
    {
      v = new DynamicLibrary( node.getElementID() );
    }
    
    else if( type.equalsIgnoreCase( "config" ))
    {
      v = new ConfigurationFile( node.getElementID());
    }
    
    else if( type.equalsIgnoreCase( "vulnerability" ))
    {
      v = new Vulnerability( node.getElementID() );
    }
    
    else
    {
      v = new Unspecified( node.getElementID());
    }
    
    
    v.cloneAttributes( node._attributes );
    
    return v;
  }
}

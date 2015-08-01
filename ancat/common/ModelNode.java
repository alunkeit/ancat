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


import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ancat.importer.GraphImporter;
import ancat.importer.graphml.DataType;

/**
 * @author a.lunkeit
 *
 * This class supports the transformation of the JAXB object into the class hierarchy used
 * for the Graph representation. The Graph representation in the Graph Tool implementation 
 * makes use of base class Vertex, the ModelNode is a kind of Pre-Object required to build
 * a Vertex with a specialized type like DynamicLibrary etc.
 */
public class ModelNode
{
  /**
   * The elementID in the Software model.
   */
  protected String _elementID;
  
  /**
   * The map of attributes assigned to the ModelNode
   */
  protected Hashtable<String, String> _attributes;
  
  /**
   * The importer used for resolving attributes
   */
  protected GraphImporter _importer;
  
  /**
   * The used logger
   */
  protected Logger _logger = Logger.getLogger( ModelNode.class );
  
  /**
   * Constructor of the class
   * 
   * @param importer The importer used to build the model
   * @param elementID The element id of the node in the XML description of the model
   */
  public ModelNode( GraphImporter importer, String elementID )
  {
    this._elementID = elementID;
    
    this._importer = importer;
    
    _attributes = new Hashtable<String, String>();
  }
  
  public void setAttributes( List<Object> attributes )
  {
    Iterator<Object> nIter = attributes.iterator();
    
    while( nIter != null && nIter.hasNext() )
    {
      DataType dType = (DataType)nIter.next();

      String key = _importer.resolveAttributeName( dType.getKey() );
      String value = dType.getContent();
            
      _logger.debug( "key: " + key );
      _logger.debug( "value: " + value );
            
     _attributes.put( key, value );
    }
  }
  
  /**
   * Returns a particular attribute or null
   * @param attrName
   * @return The requested attribute or null
   */
  public String getAttribute( String attrName )
  {
    return _attributes.get( attrName );
  }
  
  /**
   * The ID of the XML-ID
   * @return
   */
  public String getElementID()
  {
    return _elementID;
  }
}

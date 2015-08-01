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

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author alunkeit
 * 
 *         An abstract base class describing an Vertex to work in the JGraph-T
 *         implementation
 */
public abstract class Vertex extends GraphModelElement
{
  /**
   * 
   */
  @SuppressWarnings("unused")
  private static final long serialVersionUID = -2474017444121340160L;

  /**
   * A hashtable storing attributes of the Vertex
   */
  protected Hashtable<String, String> _properties;

  /**
   * Private logging instance
   */
  private Logger _logger = Logger.getLogger( Vertex.class );

  /**
   * Constructor of the class
   */
  public Vertex( String elementID )
  {
    super( elementID );

    _properties = new Hashtable<String, String>();
  }

  /**
   * Overloaded by each class inheriting from that base class.
   */
  public abstract String toString();

  /**
   * Adds a new property to the Vertex. Because the pairs of key and value are
   * stored internally in a Hashtable, each key can only be used one time. In
   * case that the key 'A' is already set, reuse of the key 'A' will overwrite
   * the value stored for the key 'A'.
   * 
   * @param key
   *          - The key to be added
   * @param value
   *          - The value to be added
   */
  public void setProperty( String key, String value )
  {
    _properties.put( key, value );
  }

  /**
   * Returns the property of a Vertex specified by the key, null otherwise.
   * 
   * @param key
   *          The key of the property.
   * @return Returns the property value or null if no assigned value has been
   *         found
   */
  public Object getProperty( String key )
  {
    return _properties.get( key );
  }

  /**
   * Clones given attributes into the Vertex representation
   * 
   * @param attributes
   *          The attributes to be cloned
   */
  public void cloneAttributes( Hashtable<String, String> attributes )
  {
    _properties.clear();
    _properties.putAll( attributes );
  }

  /**
   * Returns the map of assigned properties
   * 
   * @return a map containing property values
   */
  public Map<String, String> getProperties()
  {
    return _properties;
  }
}

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import ancat.importer.BadParameterException;

/**
 * @author alunkeit
 * 
 */
public class Edge implements Cloneable, Serializable, Comparable<Edge>
{

  /**
   * generated serialVersionUID
   */
  private static final long serialVersionUID = 1463363529374643794L;

  /**
   * The id of the edge in the xml document
   */
  private String _xmlid;

  /**
   * Vertex representing the source of the edge
   */
  private Vertex _source;

  /**
   * Vertex representing the target of the edge
   */
  private Vertex _target;

  /**
   * Logging instance
   */
  private Logger _logger = Logger.getLogger(Edge.class);

  /**
   * Tells whether the edge is directed or not
   */
  private boolean _isDirected = false;

  /**
   * The weight of the edge (required for some search algorithms)
   */
  private float _weight = 1;

  /**
   * Metadata of the edge, required for rendering
   */
  private Map<String, String> _additionalData = new HashMap<String, String>();

  /**
   * Constructor. Required when creating edges by use of the factory pattern. If
   * not available the EdgeFactoryPattern cannot be used.
   */
  public Edge()
  {
    super();
  }

  /**
   * Set the source vertex.
   * 
   * @param source
   */
  public void setSource( Vertex source )
  {
    this._source = source;
  }

  /**
   * set the target vertex
   * 
   * @param target
   */
  public void setTarget( Vertex target )
  {
    this._target = target;
  }

  /**
   * set the xml id tag
   * 
   * @param id
   */
  public void setID( String id )
  {
    this._xmlid = id;
  }

  /**
   * Constructor that can be used independent from the EdgeFactory
   */
  public Edge( Vertex source, Vertex target, String id )
      throws BadParameterException
  {
    super();

    if( null == source )
      throw new BadParameterException("Edge cannot exist without source");

    if( null == target )
      throw new BadParameterException("Edge cannot exist without target");

    // if( null == id )
    // throw new BadParameterException( "Edge must have an id" );

    this._source = source;
    this._target = target;
    this._xmlid = id;

    _logger.debug("New Edge: ");
    _logger.debug("\t source: " + source.toString());
    _logger.debug("\t target: " + target.toString());
    _logger.debug("\t id: " + id);
  }

  /**
   * Inherited from Cloneable interface
   */
  public Object clone()
  {
    Edge clone = new Edge();

    clone.setSource(_source);
    clone.setTarget(_target);
    clone.setID(_xmlid);

    return clone;
  }

  /**
   * Returns the source vertex
   * 
   * @return - The source vertex
   */
  public Vertex getSource()
  {
    return _source;
  }

  /**
   * Returns the target vertex
   * 
   * @return - The target vertex
   */
  public Vertex getTarget()
  {
    return _target;
  }

  /**
   * Returns the xml id of the edge in the xml description
   * 
   * @return
   */
  public String getXMLId()
  {
    return _xmlid;
  }

  /**
   * Overloaded <code>toString</code> method
   */
  public String toString()
  {
    return "(" + _source + " : " + _target + ")";
  }

  /**
   * Returns the weight of the edge
   * 
   * @return The weight as integer value
   */
  public float getWeight()
  {
    return this._weight;
  }

  /**
   * Set the value of the weight
   * 
   * @param weight
   *          The weight parameter of the Edge
   */
  public void setWeight( float weight )
  {
    this._weight = weight;
  }

  /**
   * Set if the edge is directed.
   * 
   * @param isDirected
   */
  public void setDirected( boolean isDirected )
  {
    this._isDirected = isDirected;
  }

  /**
   * Returns true, if the edge directed
   * 
   * @return
   */
  public boolean isDirected()
  {
    return this._isDirected;
  }

  @Override
  public int compareTo( Edge e0 )
  {
    if( e0.getWeight() < this.getWeight() )
      return 1;
    else if( e0.getWeight() == this.getWeight() )
      return 0;
    else
      return -1;
  }

  public void addDataElement( String key, String value )
  {
    _additionalData.put(key, value);
  }

  public String getAdditionalElement( String key )
  {
    return _additionalData.get(key);
  }

  public Map<String, String> getAdditionalMap()
  {
    return _additionalData;
  }
}

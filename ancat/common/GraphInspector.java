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
import java.util.Vector;

import ancat.importer.config.Inspector;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 */
public abstract class GraphInspector<V, E>
{

  protected String _id = null;

  protected String _idRef = null;

  protected Object _input = null;

  protected Inspector _configuration = null;

  protected Hashtable<String, Object> _globalConfiguration = null;

  protected Map<String, String> _properties = new Hashtable<String, String>();

  protected Vector<InspectionOutputConsumer> _consumers =
      new Vector<InspectionOutputConsumer>();

  protected InspectionOutput _output = new InspectionOutput();

  protected String _modelFile;

  /**
   * This functions needs to be implemented by an class acting as an graph
   * inspector.
   * 
   * @param graph
   */
  public abstract void inspect( Object graph );

  public void setModelFile( String file )
  {
    _modelFile = file;
  }

  public String getModelFile()
  {
    return _modelFile;
  }

  public void setIdRef( String idRef )
  {
    this._idRef = idRef;
  }

  public String getIdRef()
  {
    return this._idRef;
  }

  public boolean hasIdRef()
  {
    return this._idRef != null;
  }

  public Object getOutput()
  {
    return "test";
  }

  public void setInput( Object input )
  {
    this._input = input;
  }

  public void setId( String id )
  {
    this._id = id;
  }

  public String getId()
  {
    return this._id;
  }

  /**
   * Sets the configuration parameters directly associated with the inspector in
   * the program configuration file. Do not use those parameters, they will be
   * skipped in future.
   * 
   * @param configuration
   */
  public void setInspectorConfiguration( Inspector configuration )
  {
    this._configuration = configuration;
  }

  public Inspector getConfiguration()
  {
    return this._configuration;
  }

  @SuppressWarnings("unchecked")
  public void setGlobalConfiguration( Hashtable<String, Object> configuration )
  {
    _globalConfiguration = configuration;
  }

  public Hashtable<String, Object> getGlobalConfiguration()
  {
    return _globalConfiguration;
  }

  public Map<String, String> getProperties()
  {
    return _properties;
  }

  public void setProperties( Map<String, String> properties )
  {
    _properties = properties;
  }

  /**
   * Adds a new output consumer to the inspector instance. Must be set before
   * inspection takes place.
   * 
   * @param consumer
   */
  public void addOutputConsumer( InspectionOutputConsumer consumer )
  {
    _consumers.add( consumer );
  }

  public class InspectionOutput
  {
    protected Graph<V, E> _graph;

    protected StringBuffer _buffer;

    public InspectionOutput()
    {

    }

    public void setGraph( Graph<V, E> graph )
    {
      _graph = graph;
    }

    public Graph<V, E> getGraph()
    {
      return _graph;
    }

    public void initBuffer()
    {
      _buffer = new StringBuffer();
    }

    public StringBuffer getBuffer()
    {
      return _buffer;
    }

  }

  public InspectionOutput getInspectionOutput()
  {
    return _output;
  }

}

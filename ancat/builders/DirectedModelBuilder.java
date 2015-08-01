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
package ancat.builders;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;

import ancat.common.Edge;
import ancat.common.Vertex;
import ancat.importer.InitializationException;

/**
 * @author a.lunkeit
 *
 * This class implements a model builder used for directed graphs.
 */
public class DirectedModelBuilder extends ModelBuilder
{
  
  /**
   * Constructor of the class. Initializes the importer and loads the graph
   */
  public DirectedModelBuilder( File f ) throws Exception
  {
    super( f, ModelBuilder.GraphType.DIRECTED );
  }
  
  /**
   * Returns the graph representation using the JUNG framework
   * 
   * @return
   * @throws Exception
   */
  public DirectedSparseGraph<Vertex, Edge> getGraph() throws Exception
  {
    try
    {
      DirectedSparseGraph<Vertex, Edge> usg = new DirectedSparseGraph<Vertex, Edge>();
      
      // add vertexes by looping over the hashtable of vertexes created when reading the xml file
      Enumeration<String> keys = _vertices.keys();
      
      while( keys.hasMoreElements())
      {
        String key = keys.nextElement();
        
        Vertex v = _vertices.get( key );
        
        usg.addVertex( v );
        
        _logger.debug( "Added vertex: " + v.toString() );
      }
      
      // add edges by looping over the hashtable of edges
//      Enumeration<String> ekeys = _edges.keys();
//      
//      while( ekeys.hasMoreElements())
//      {
//        String key = ekeys.nextElement();
//        
//        Edge edge = _edges.get( key );
//        
//        if( null != edge )
//          _logger.info( "edge != null" );
//        
//        usg.addEdge( edge, edge.getSource(), edge.getTarget() );
//        
//        _logger.info(  "added edge: " + edge.toString() );
//      }
      
      Iterator<Edge> iter = _edges.iterator();
      
      while( iter.hasNext())
      {
        Edge e = iter.next();
        
        usg.addEdge( e, e.getSource(), e.getTarget() );
        
        _logger.info(  "added edge: " + e.toString() );
      }
      
      return usg;
    }
    catch( Exception e )
    {
      _logger.error( e.getMessage(), e );
    }
    
    throw new InitializationException( "Graph has not been created" );
  }

}

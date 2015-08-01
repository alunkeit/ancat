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
package ancat.inspection;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nocrala.tools.texttablefmt.Table;

import ancat.algorithm.DepthFirstSearch;
import ancat.algorithm.DirectedGraphPath;
import ancat.common.Edge;
import ancat.common.GraphInspector;
import ancat.common.Vertex;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 *         Beginning from the start node the PathInspector builds all possible
 *         paths until no outgoing edges are found. The output is written into
 *         an output file
 * 
 */
public class PathInspector<V, E> extends GraphInspector<V, E>
{

  protected Logger _logger = Logger.getRootLogger();

  protected Graph<Vertex, Edge> _graph;

  protected BufferedOutputStream _out;

  /**
   * Constructor
   */
  public PathInspector()
  {
    _logger.debug("PathInspector - constructor");
  }

  /*
   * (non-Javadoc)
   * 
   * @see ancat.common.GraphInspector#inspect(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void inspect( Object graph )
  {
    _logger.debug("PathInspector.inspect()");

    _graph = (Graph<Vertex, Edge>) graph;

    try
    {
      _out = new BufferedOutputStream(new FileOutputStream(new File(
          _properties.get("output_file"))));
    }
    catch( FileNotFoundException e )
    {
      _logger.error(e);
      return;
    }

    Vertex start = null;

    for( Vertex v : _graph.getVertices() )
    {
      _logger.debug("Vertex " + v);

      Set<String> keys = v.getProperties().keySet();

      for( String key : keys )
      {
        String property = v.getProperties().get(key);

        _logger.debug(key + " : " + property);
      }

      if( v.getProperty("filename")
          .equals(this._properties.get("search_start")) )
        start = v;
    }

    if( start != null )
    {
      DepthFirstSearch<Vertex, Edge> search = new DepthFirstSearch<Vertex, Edge>(
          _graph);

      search.search(start, null);

      List<DirectedGraphPath<Vertex, Edge>> paths = search.paths();

      _logger.debug("Count of detected paths: " + paths.size());

      for( DirectedGraphPath<Vertex, Edge> p : paths )
      {
        _logger.debug("Path from " + p.getRoot().getProperty("filename")
            + " -> " + p.getTarget().getProperty("filename"));

        pathStatistics(p);
      }

      _logger.debug("count of processed paths: " + paths.size());
    }

    try
    {
      _out.flush();

      _out.close();
    }
    catch( IOException e )
    {
      _logger.error(e);
    }
  }

  private void pathStatistics( DirectedGraphPath<Vertex, Edge> p )
  {
    Vertex start = p.getRoot();

    Vertex vertex = start;

    Table table = new Table(3);
    table.addCell("source");
    table.addCell("target");
    table.addCell("context");

    while( vertex != null )
    {
      Collection<Edge> outEdges = p.getOutEdges(vertex);

      if( outEdges.size() > 1 )
      {
        _logger.error("Count of outgoing edges is > 1!");
        return;
      }

      if( outEdges.size() > 0 )
      {

        Edge e = outEdges.iterator().next();

        Vertex target = e.getTarget();

        table.addCell((String) vertex.getProperty("filename"));
        table.addCell((String) target.getProperty("filename"));
        table.addCell((String) e.getAdditionalElement("labeldata"));

        vertex = target;
      }
      else
      {
        break;
      }
    }

    try
    {
      _out.write(table.render().getBytes("utf-8"));
      _out.write("\n\n\n".getBytes());
    }
    catch( UnsupportedEncodingException e )
    {
      _logger.error(e);
    }
    catch( IOException e )
    {
      _logger.error(e);
    }
  }
}

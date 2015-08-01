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

import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import ancat.common.Edge;
import ancat.common.ModelNode;
import ancat.common.Vertex;
import ancat.common.VertexFactory;
import ancat.importer.DirectedGraphMLImporter;
import ancat.importer.GraphImporter;
import ancat.importer.UndirectedGraphMLImporter;
import ancat.importer.graphml.DataType;
import ancat.importer.graphml.EdgeType;
import ancat.importer.graphml.NodeType;

/**
 * @author a.lunkeit
 * 
 *         The ModelBuilder is the base implementation for generation of the
 *         software model. Direct known implementing classes are the
 *         DirectedModelBuilder and the UndirectedModelBuilder as well.
 */
public abstract class ModelBuilder
{
  /**
   * The GraphImporter implementation
   */
  protected GraphImporter _importer;

  /**
   * The imported vertices. Key is the name of the vertex, the corresponding
   * object is of type Vertex of the common package.
   */
  protected Hashtable<String, Vertex> _vertices;

  /**
   * Imported Edges.
   */
  protected Vector<Edge> _edges;

  /**
   * The used logger implementation
   */
  protected Logger _logger = Logger.getLogger(ModelBuilder.class);

  /**
   * 
   * @author a.lunkeit
   * 
   *         The Graph type defines which type of graph is generated. This is
   *         required so the implementation of the ModelBuilder can make a
   *         choice which GraphMLImporter to use.
   */
  public enum GraphType
  {
    UNDIRECTED, DIRECTED;
  }

  /**
   * Constructor of the class. Initializes the importer and loads the graph
   */
  protected ModelBuilder( File f, ModelBuilder.GraphType type )
      throws Exception
  {
    _logger.debug("creating new DirectedGraphMLImporter");

    if( type == GraphType.DIRECTED )
      _importer = new DirectedGraphMLImporter(f);

    else if( type == GraphType.UNDIRECTED )
      _importer = new UndirectedGraphMLImporter(f);

    _logger.debug("DirectedGraphMLImporter created");

    _importer.loadGraph();

    _logger.debug("Graph loaded");

    _vertices = new Hashtable<String, Vertex>();

    _edges = new Vector<Edge>();
  }

  /**
   * Creates the Model.
   * 
   * 
   */
  public void createModel()
  {
    _logger.debug("Creating model of loaded graph");
    _logger.debug("Count of found nodes: " + _importer.getNodes().size());

    Iterator<NodeType> iter = _importer.getNodes().iterator();

    while( iter != null && iter.hasNext() )
    {
      _logger.debug("checking node");

      NodeType node = iter.next();

      ModelNode mNode = new ModelNode(_importer, node.getId());
      mNode.setAttributes(node.getDataOrPort());

      Vertex vertice = VertexFactory.nodeToVertex(mNode);

      _logger.debug(vertice);

      _vertices.put(node.getId(), vertice);
    }

    // Now process the edges
    Vector<EdgeType> edges = _importer.getEdges();

    Iterator<EdgeType> eIter = edges.iterator();

    while( eIter != null && eIter.hasNext() )
    {
      try
      {
        EdgeType eType = eIter.next();

        String source = eType.getSource();
        String target = eType.getTarget();
        String id = eType.getId();

        Vertex sourceVertex = _vertices.get(source);
        Vertex edgeVertex = _vertices.get(target);

        if( null != sourceVertex )
          _logger.debug("sourceVertex != null");
        if( null != edgeVertex )
          _logger.debug("edgeVertex != null");

        // Create a new EdgeObject, reference the corresponding vertexes
        ancat.common.Edge edge = new Edge(sourceVertex, edgeVertex, id);

        List<DataType> data = eType.getData();

        Iterator<DataType> iter1 = data.iterator();

        while( iter1.hasNext() )
        {
          DataType dt = iter1.next();

          String key = dt.getKey();
          String content = dt.getContent();
          String keyName = _importer.resolveAttributeName(key);

          _logger.debug("edge additional data: key := " + key);
          _logger.debug("edge additional data: key name := " + keyName);
          _logger.debug("edge additional data content := " + content);

          edge.addDataElement(keyName, content);
        }

        _edges.add(edge);
      }
      catch( Exception e )
      {
        _logger.error("Error when creating edge", e);
      }
    }
  }

  /**
   * This function must be overloaded by inheriting class. The function is
   * intended to return the object representation of the Graph to be used.
   * 
   * @return Object representing the Graph
   * @throws Exception
   *           In case of an Exception
   */
  public abstract Object getGraph() throws Exception;
}

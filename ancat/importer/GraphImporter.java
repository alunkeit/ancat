/**
 * Copyright (C) Armin Lunkeit 2013
 */
package ancat.importer;


import java.util.Vector;

import ancat.importer.graphml.EdgeType;
import ancat.importer.graphml.NodeType;

/**
 * @author a.lunkeit
 *
 * An interface defining the basic functions required to be an importer.
 */
public interface GraphImporter
{
  /**
   * Starts loading the graph from the Model Description
   * 
   * @throws ImporterException Is thrown in case of any error when importing the graph
   */
  public void loadGraph() throws ImporterException;
  
  /**
   * Returns an set containing the nodes of the graph.
   * 
   * @return Vector of NodeType
   */
  public Vector<NodeType> getNodes();
  
  /**
   * Returns an set containing the edges of the graph.
   * 
   * @return Vector of EdgeType
   */
  public Vector<EdgeType> getEdges();
  
  public String resolveAttributeName( String id );
}

/**
 * Copyright (C) Armin Lunkeit
 * 
 * This implementation is foreseen to import Undirected Graphs that have a description
 * conforming the GraphML specification. For detailed information of the GraphML syntax
 * check the following page: http://graphml.graphdrawing.org/primer/graphml-primer.html#BCfile
 * 
 * The final implementation of this importer, graphs describing security attributes in modular
 * constructions are processed. The description of the data model can be found in tgraphs.de
 * wiki.
 */
package ancat.importer;


import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import ancat.importer.graphml.EdgeType;
import ancat.importer.graphml.GraphEdgedefaultType;
import ancat.importer.graphml.GraphType;
import ancat.importer.graphml.GraphmlType;
import ancat.importer.graphml.KeyType;
import ancat.importer.graphml.NodeType;

/**
 * @author alunkeit
 *
 */
public class UndirectedGraphMLImporter implements GraphImporter
{

    /**
     * The file to be imported by the UndirectedGraphMLImporter
     */
    private File _iFile; 
    
    /**
     * Root node object created when parsing the undirected graph description
     */
    private GraphmlType _gmlType;
    
    /**
     * Object representing the edge type of the imported graph
     */
    private GraphEdgedefaultType _edgeType;
    
    /**
     * Vector containing the nodes of the graphs
     */
    Vector<NodeType> _nodes;
    
    /**
     * Vector containing the edges of the graph
     */
    Vector<EdgeType> _edges;
    
    /**
     * The private used logging instance
     */
    static Logger _logger = Logger.getLogger(UndirectedGraphMLImporter.class);
    
    /**
     * Constructor. Checks the input argument of type file not be zero
     * 
     * @param f
     * @throws Exception
     */
    public UndirectedGraphMLImporter( File f ) throws BadParameterException, InitializationException
    { 
      if( false == f.exists())
        {
          _logger.error( "Bad input parameter, file parameter is null!" );  
          throw new BadParameterException( "File must not be null" );
        }
        
        _iFile = f;
        
        _logger.info( "File parameter successfullfy checked" );
        
        _nodes = new Vector<NodeType>();
        
        _edges = new Vector<EdgeType>();
    }
    
  /**
   * Loads the graph from the XML description
   */
  public void loadGraph() throws ImporterException
  {

    try
    {
      JAXBContext jc = JAXBContext.newInstance( "graphtool.importer.graphml" );

      Unmarshaller unmarshaller = jc.createUnmarshaller();

      @SuppressWarnings("unchecked")
      JAXBElement<GraphmlType> element = (JAXBElement<GraphmlType>) unmarshaller
          .unmarshal( _iFile );

      _gmlType = element.getValue();
    }
    catch( Exception exc )
    {
      _logger.error( "Failed to initialize JAXB parser!", exc );
      throw new ImporterException( exc.getMessage() );
    }
    
    //-----------------------------------------------------------
    // Extract the key list from the graph description
    // Extracting the key helps later when associating attributes
    // with the graph. The keys can refer to nodes and to edges
    // as well
    //-----------------------------------------------------------
    List<KeyType> keyList = _gmlType.getKey();
    
    Iterator<KeyType> keyIter = keyList.iterator();
    
    while( keyIter != null && keyIter.hasNext() )
    {
      KeyType kType = keyIter.next();
      
      // the clear text name of the attribute (e.g. color)
      _logger.debug( "attribute name: " + kType.getAttrName());
      // the type of the attribute (check the GraphML primer for that)
      _logger.debug( "attribute type: " + kType.getAttrType().name());
      // the attribute value describes, whether the key refers to a node or an edge
      _logger.debug( "attribute value: " + kType.getFor().value() );
      // the id name that matches this attribute. E.g. color can be referred to as 'd0'
      _logger.debug( "attribute key:" + kType.getId() );
      // the attribute can have a default value
      if( null != kType.getDefault() )
        _logger.debug( "attribute default value: " + kType.getDefault().getContent());
    }

    //---------------------------------------------------------
    // Contains the section with real parsing code of the graph
    // description
    //---------------------------------------------------------
    List<Object> list = _gmlType.getGraphOrData();

    Iterator<Object> iter = list.iterator();

    while( iter != null && iter.hasNext() )
    {
      Object o = iter.next();

      if( o instanceof GraphType )
      {
        GraphType t = (GraphType) o;

        _edgeType = t.getEdgedefault();

        if( 0 != _edgeType.value().compareToIgnoreCase( "undirected" ) )
          throw new ImporterException( "Wrong type of imported graph!" );

        List<Object> dne = t.getDataOrNodeOrEdge();

        Iterator<Object> iter1 = dne.iterator();

        while( iter1 != null && iter1.hasNext() )
        {
          Object i = iter1.next();

          if( i instanceof NodeType )
          {
            NodeType nt = (NodeType) i;
            _nodes.add( nt );
          }
          else if( i instanceof EdgeType )
          {
            EdgeType et = (EdgeType) i;
            _edges.add( et );
          }
        }
      }
    }

  }
  
  /**
   * Returns the value of the graph type (should be 'undirected')
   * 
   * @return
   */
  public String getGraphType()
  {
    return _edgeType.value();
  }
  
  /**
   * Returns a vector containing the nodes of the graph
   * 
   * @return
   */
  public Vector<NodeType> getNodes()
  {
    return _nodes;
  }
  
  /**
   * Returns a vector containing the edges of the graph
   * 
   * @return
   */
  public Vector<EdgeType> getEdges()
  {
    return _edges;
  }
  
  /**
   * Resolves the name of an abstract attribute reference. The xml file references names like 'd0'
   * for a given property. The real name of the property must be resolved by checking the header
   * section of the file. For more information check the GraphML primer documentation
   * 
   * @param id - The id name of the element to be resolved, e.g. 'd0'
   * @return - The resolved attribute name
   */
  public String resolveAttributeName( String id )
  {
    List<KeyType> keyList = _gmlType.getKey();
    
    Iterator<KeyType> keyIter = keyList.iterator();
    
    while( keyIter != null && keyIter.hasNext() )
    {
      KeyType kType = keyIter.next();
      
      if( kType.getId().equalsIgnoreCase( id ))
      {
        return kType.getAttrName();
      }     
    }
    
    return null;
  }
}

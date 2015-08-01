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
package ancat.analyzer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author alunkeit
 * 
 */
public class GraphMLTransformer extends OutputTransformer
{

  private Map<String, Node> _nodes;

  private Map<String, Edge> _edges;

  private StringBuffer _buffer;

  protected GraphMLTransformer()
  {
    _nodes = new HashMap<String, Node>();

    _edges = new HashMap<String, Edge>();

    _buffer = new StringBuffer();
  }

  public void addNode( Node n )
  {
    _nodes.put( n._label, n );
  }

  public void addEdge( Edge e )
  {
    _edges.put( e._label, e );
  }

  private void header() throws TransformationError
  {
    StringBuffer buffer = new StringBuffer();

    buffer.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );

    buffer
        .append( "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" " + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " + "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns " + "http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n" + "<key id=\"d0\" for=\"node\" attr.name=\"itemtype\" attr.type=\"string\"/>\n" + "<key id=\"d1\" for=\"node\" attr.name=\"filename\" attr.type=\"string\"/>\n" + "<key id=\"d2\" for=\"node\" attr.name=\"target\" attr.type=\"string\"/>\n" + "<key id=\"d3\" for=\"edge\" attr.name=\"labeldata\" attr.type=\"string\"/>\n" );
    _buffer.append( buffer );
  }

  private void trailer() throws TransformationError
  {
    _buffer.append( "</graphml>" );
  }

  private void beginGraph() throws TransformationError
  {
    _buffer.append( "<graph id=\"G\" edgedefault=\"directed\">" );
  }

  private void endGraph() throws TransformationError
  {
    _buffer.append( "</graph>" );
  }

  /**
   * Create XML file here and write to disc
   * 
   * @throws IOException
   * @throws UnsupportedEncodingException
   */
  public void transform() throws TransformationError
  {
    header();

    beginGraph();

    Set<String> keys = _nodes.keySet();

    int idCnt = 1;

    Hashtable<String, Integer> mapping = new Hashtable<String, Integer>();

    for( String key : keys )
    {
      Node n = _nodes.get( key );

      _buffer.append( "<node id = \"" );
      // _buffer.append(n._label);
      _buffer.append( idCnt );
      _buffer.append( "\">\n" );

      _buffer.append( "<data key = \"d0\">" );
      _buffer.append( n._type );
      _buffer.append( "</data>\n" );

      _buffer.append( "<data key = \"d1\">" );
      _buffer.append( "<![CDATA[" );
      _buffer.append( n._label );
      _buffer.append( "]]>" );
      _buffer.append( "</data>\n" );

      _buffer.append( "</node>\n" );

      mapping.put( n._label, idCnt );

      idCnt += 1;
    }

    Set<String> eKeys = _edges.keySet();

    long counter = 0;
    String edgeName = "e";

    for( String s : eKeys )
    {
      Edge e = _edges.get( s );

      _buffer.append( "<edge id=\"" );
      _buffer.append( edgeName );
      _buffer.append( counter );
      _buffer.append( "\" " );
      _buffer.append( "source = \"" + mapping.get( e._source ) + "\" " );
      _buffer.append( "target = \"" + mapping.get( e._target ) + "\" " );

      /**
       * Make a difference between edges with or without additional data.
       * Current implementation supports the labeldata tag providing data to be
       * rendered on the edge.
       */
      if( e._label == null )
      {
        _buffer.append( "/>" );
      }
      else
      {
        String s1 = "&&";

        _buffer.append( "><data key = \"d3\">" );
        _buffer.append( "<![CDATA[" );
        _buffer.append( e._label );
        _buffer.append( "]]>" );
        _buffer.append( "</data>\n" );
        _buffer.append( "</edge>" );
      }
      counter += 1;
    }

    endGraph();

    trailer();
  }

  private String escape( String s )
  {
    s = s.replaceAll( "&", "&amp;" );
    s = s.replaceAll( "<", "&lt;" );
    s = s.replaceAll( ">", "&gt;" );
    s = s.replaceAll( "\"", "&quot;" );
    s = s.replaceAll( "'", "&apos" );
    s = s.replace( '\\', ' ' );
    return s;
  }

  public StringBuffer getBuffer()
  {
    return _buffer;
  }
}

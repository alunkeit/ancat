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
package ancat.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Test;

import ancat.algorithm.BreadthFirstSearch;
import ancat.algorithm.DepthFirstSearch;
import ancat.algorithm.DirectedGraphPath;
import ancat.algorithm.GraphScanner;
import ancat.analyzer.AnalyzerException;
import ancat.analyzer.CScopeAnalyzer;
import ancat.analyzer.GCCPluginAnalyzer;
import ancat.analyzer.TransformationError;
import ancat.configuration.ConfigurationParser;
import ancat.configuration.ConfigurationParser.ConfigurationItem;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 */
public class BasicTestSet
{

  protected static Logger _logger = Logger.getRootLogger();

  static
  {
    try
    {
      PatternLayout appenderLayout = new PatternLayout();
      appenderLayout.setConversionPattern( "%d %p - %m%n" );
      ConsoleAppender console = new ConsoleAppender( appenderLayout );
      _logger.addAppender( console );
      _logger.setLevel( org.apache.log4j.Level.ALL );
    }
    catch( Exception e )
    {
    }
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
  }

  @Test
  public void test()
  {
    fail( "Not yet implemented" );
  }

  @Test
  public void testImport()
  {
    try
    {
      String file = "./cscope_analyzer.conf";

      CScopeAnalyzer analyzer = new CScopeAnalyzer();

      analyzer.init( file );

      analyzer.analyze();

      analyzer.doOutput();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

  }

  public void testConfigurationParser()
  {
    ConfigurationParser p = new ConfigurationParser( "ssl_connect.conf" );

    try
    {
      p.load();

      Set<String> gKeys = p.simpleItems().keySet();

      for( String s : gKeys )
      {
        System.out.println( s + " : " + p.simpleItems().get( s ) );
      }

      System.out.println( "------------ complex types ------------------" );

      for( ConfigurationItem item : p.complexItems() )
      {
        System.out.println( "Type: " + item._type );

        Set<String> keys = item._items.keySet();

        for( String s : keys )
        {
          System.out.println( "\t " + s + " : \t" + item._items.get( s ) );
        }
      }
    }
    catch( IOException e )
    { // TODO Auto-generated
      e.printStackTrace();
    }

  }

  @Test
  public void testBFS()
  {
    Graph<String, String> g = new DirectedSparseGraph<String, String>();

    g.addVertex( "a" );
    g.addVertex( "b" );
    g.addVertex( "c" );
    g.addVertex( "d" );
    g.addVertex( "e" );
    g.addVertex( "f" );
    g.addVertex( "g" );
    g.addVertex( "h" );

    g.addEdge( "e1", "a", "b" );
    g.addEdge( "e2", "b", "d" );
    g.addEdge( "e3", "b", "e" );
    g.addEdge( "e4", "e", "h" );
    g.addEdge( "e5", "a", "c" );
    g.addEdge( "e6", "c", "f" );
    g.addEdge( "e7", "c", "g" );

    BreadthFirstSearch<String, String> search =
        new BreadthFirstSearch<String, String>( g );

    search.search( "a", "h" );

    DirectedGraphPath<String, String> path = search.getPath();

    if( null != path )
    {
      Collection<String> edges = path.getOutEdges( "a" );

      while( edges != null && edges.size() != 0 )
      {
        if( edges.size() > 1 )
          fail( "There should only be one edge!" );

        for( String edge : edges )
        {
          _logger.debug( "edge: " + edge );
          _logger.debug( "this node: " + path.getEndpoints( edge ).getFirst() );
          _logger.debug( "next node: " + path.getEndpoints( edge ).getSecond() );

          edges = path.getOutEdges( path.getEndpoints( edge ).getSecond() );
        }

      }

    }

    _logger.debug( "------------- visited nodes  --------------" );

    Set<String> keys = search.getVisited().keySet();

    for( String key : keys )
    {
      GraphScanner.SearchNode<String> sn = search.getVisited().get( key );

      _logger.info( "node: " + sn.node() );
    }
  }

  @Test
  public void testDFS1()
  {
    Graph<String, String> g = new DirectedSparseGraph<String, String>();

    g.addVertex( "a" );
    g.addVertex( "b" );
    g.addVertex( "c" );
    g.addVertex( "d" );
    g.addVertex( "e" );
    // g.addVertex("f");
    // g.addVertex("g");
    // g.addVertex("h");

    g.addEdge( "e1", "a", "b" );
    g.addEdge( "e2", "a", "c" );
    g.addEdge( "e3", "a", "d" );
    g.addEdge( "e4", "b", "e" );
    g.addEdge( "e5", "c", "e" );
    g.addEdge( "e6", "d", "e" );

    DepthFirstSearch<String, String> search =
        new DepthFirstSearch<String, String>( g );

    search.search( "a", "e" );

    _logger.debug( "path count: " + search.pathCount() );

    for( DirectedGraphPath<String, String> path : search.paths() )
      _logger.debug( "path: " + path );
  }

  @Test
  public void testDFS2()
  {
    Graph<String, String> g = new DirectedSparseGraph<String, String>();

    g.addVertex( "a" );
    g.addVertex( "b" );
    g.addVertex( "c" );
    g.addVertex( "d" );
    g.addVertex( "e" );
    g.addVertex( "f" );
    g.addVertex( "g" );
    // g.addVertex("h");

    g.addEdge( "e1", "a", "b" );
    g.addEdge( "e2", "b", "c" );
    g.addEdge( "e3", "b", "d" );
    g.addEdge( "e4", "b", "e" );
    g.addEdge( "e5", "c", "f" );
    g.addEdge( "e6", "d", "f" );
    g.addEdge( "e7", "e", "f" );
    g.addEdge( "e8", "f", "g" );

    DepthFirstSearch<String, String> search =
        new DepthFirstSearch<String, String>( g );

    // search.search("a", "g");
    search.search( "a", null );

    _logger.debug( "path count: " + search.pathCount() );

    for( DirectedGraphPath<String, String> path : search.paths() )
    {
      _logger.debug( "path: " + path );
      _logger.debug( "start: " + path.getRoot() + " : end : " + path
          .getTarget() );

    }
  }

  @Test
  public void testGraphVizImporter()
  {
    GCCPluginAnalyzer analyzer = new GCCPluginAnalyzer();

    try
    {
      analyzer.init( "gcc-plugin.conf" );
      analyzer.analyze();
    }
    catch( IOException | AnalyzerException e )
    {
      fail( e.getMessage() );
    }
    catch( TransformationError e )
    {
      fail( e.getMessage() );
    }
  }

}

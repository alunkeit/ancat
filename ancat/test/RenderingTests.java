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

import java.awt.Dimension;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 */
public class RenderingTests
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
  @BeforeClass
  public static void setUpBeforeClass() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void tearDownAfterClass() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception
  {
  }

  @Test
  public void testLayout()
  {

    Graph<String, String> graph = new DirectedSparseGraph<String, String>();

    for( int i = 0; i < 10001; i++ )
      graph.addVertex( "v" + i );

    for( int i = 0; i < 10000; i++ )
      graph.addEdge( "e" + i, "v" + i, "v" + i + 1 );

    ISOMLayout<String, String> layout = new ISOMLayout<String, String>( graph );

    layout.setSize( new Dimension( 9000, 9000 ) );
  }
}

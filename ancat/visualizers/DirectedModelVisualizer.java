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
package ancat.visualizers;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.log4j.Logger;

import ancat.common.Utilities;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author a.lunkeit
 * 
 * @param <V>
 *          Parameter of Vertex Type
 * @param <E>
 *          PArameter of Edge Type
 * 
 *          This class provides the Visualizer for the Directed Graph.
 */
public class DirectedModelVisualizer<V, E> extends ModelVisualizer<V, E>
{

  /**
   * The graph to be rendered
   */
  // protected Graph<V, E> _graph;

  /**
   * Current timestamp is used for generation of output file name
   */
  private long _timestamp;

  /**
   * Output path used for writing the rendered files
   */
  private String _path;

  private String _fileToken;

  protected static Logger _logger = Logger.getRootLogger();

  /**
   * Constructor of the class
   */
  public DirectedModelVisualizer()
  {
    super();

    _timestamp = System.currentTimeMillis();
  }

  private String basePath()
  {
    if( null != _fileToken )
      return _path + File.separatorChar + _fileToken + "_" + _timestamp + "_";
    else
      return _path + _timestamp + "_";
  }

  /**
   * Renders the graph in Circle Layout
   */
  private void circleLayout()
  {
    init( _graph, new CircleLayout<V, E>( _graph ) );
    BufferedImage img = produce();

    Utilities.save( img, basePath() + "circle-layout-directed.png" );
  }

  /**
   * Renders the graph in ISOMLayout
   */
  private void ISOMLayout()
  {
    init( _graph, new ISOMLayout<V, E>( _graph ) );
    BufferedImage img = produce();

    Utilities.save( img, basePath() + "isom-layout-directed.png" );
  }

  /**
   * Renders the Graph in KK Layout
   */
  private void KKLayout()
  {
    init( _graph, new KKLayout<V, E>( _graph ) );
    BufferedImage img = produce();

    Utilities.save( img, basePath() + "kk-layout-directed.png" );
  }

  /**
   * Renders the graph in FR layout
   */
  private void FRLayout()
  {
    init( _graph, new FRLayout<V, E>( _graph ) );

    BufferedImage img = produce();

    Utilities.save( img, basePath() + "fr-layout-directed.png" );
  }

  private void TreeLayout()
  {
    init( _graph, new SpringLayout<V, E>( _graph ) );

    BufferedImage img = produce();

    Utilities.save( img, basePath() + "spring-layout-directed.png" );
  }

  @SuppressWarnings("unchecked")
  @Override
  public void inspect( Object graph )
  {
    try
    {
      ConfigurationReader r = new ConfigurationReader( "directed.conf" );

      r.parse();

      _renderConfig = r;

      _logger.info( "Rendering begins..." );

      this._graph = (Graph<V, E>) graph;

      _path = r.getOutputFolder();

      if( null == _path || _path.length() == 0 )
        _path = System.getProperty( "user.home" );

      if( !_path.endsWith( "/" ) || !_path.endsWith( "\\" ) )
        _path += "/";

      _logger.debug( "Output folder := " + _path );

      _fileToken = _renderConfig.getFileToken();

      for( String layout : r.getLayouts() )
      {
        _logger.info( "lookup key:" + layout );

        if( layout.equalsIgnoreCase( "circle" ) )
        {
          _logger.info( "circle layout" );
          circleLayout();
        }

        if( layout.equals( "isom" ) )
        {
          _logger.info( "isom layout" );
          ISOMLayout();
        }

        if( layout.equals( "kk" ) )
        {
          _logger.info( "kk layout" );
          KKLayout();
        }

        if( layout.equals( "fr" ) )
        {
          _logger.info( "fr layout" );
          FRLayout();
        }

        if( layout.equals( "tree" ) )
        {
          _logger.info( "tree layout" );
          TreeLayout();
        }
      }
      _logger.info( "rendering finished..." );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }
}

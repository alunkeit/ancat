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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import ancat.common.GraphInspector;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * @author a.lunkeit
 * 
 *         Abstract base class for model visualization. All Visualizers should
 *         be inherited from this class in order to access shared rendering
 *         policies.
 */
public abstract class ModelVisualizer<V, E> extends GraphInspector<V, E>
{

  /**
   * The Visualization server
   */
  protected VisualizationImageServer<V, E> _server;

  /**
   * The layout used for rendering
   */
  protected Layout<V, E> _layout;

  protected Graph<V, E> _graph;

  protected RenderingConfiguration _renderConfig;

  /**
   * Constructor
   */
  protected ModelVisualizer()
  {
  }

  /**
   * Initialization method. Must be called before any rendering takes place,
   * otherwise Exceptions and undefined behaviour may occur.
   * 
   * @param layout
   */
  protected void init( Graph<V, E> graph, Layout<V, E> layout )
  {
    _graph = graph;
    _layout = layout;

    _layout.setSize( new Dimension( (short) _renderConfig.width(),
        (short) _renderConfig.height() ) );

    _server =
        new VisualizationImageServer<V, E>( _layout, new Dimension(
            (short) _renderConfig.width(), (short) _renderConfig.height() ) );
  }

  /**
   * Creates the visualization.
   * 
   * @return
   */
  protected BufferedImage produce()
  {
    _server.setPreferredSize( new Dimension( (short) _renderConfig.width(),
        (short) _renderConfig.height() ) );

    VertexTransformers<V, E> vTransformers =
        new VertexTransformers<V, E>( _renderConfig, _graph );

    EdgeTransformers<E> eTransformers = new EdgeTransformers<E>( _renderConfig );

    _server.getRenderContext().setVertexFillPaintTransformer(
        vTransformers.new VertexPaint() );
    _server.getRenderContext().setEdgeStrokeTransformer(
        eTransformers.new StrokeTransformer() );

    _server.getRenderContext().setVertexLabelTransformer(
        new ToStringLabeller<V>() );
    _server.getRenderContext().setEdgeLabelTransformer(
        new ToStringLabeller<E>() );
    // ---->
    _server.getRenderContext().setEdgeFontTransformer(
        eTransformers.new FontTransformer() );
    _server.getRenderContext().setLabelOffset( 25 );
    _server.getRenderContext().setEdgeLabelTransformer(
        eTransformers.new LabelTransformer() );

    _server.getRenderContext().setVertexFontTransformer(
        vTransformers.new FontTransformer() );
    _server.getRenderContext().setVertexLabelTransformer(
        vTransformers.new LabelTransformer() );

    _server.getRenderer().getVertexLabelRenderer().setPosition( Position.N );

    _server.getRenderContext().setVertexShapeTransformer(
        vTransformers.new ShapeTransformer() );

    BufferedImage img =
        new BufferedImage( (int) _layout.getSize().getWidth(), (int) _layout
            .getSize().getHeight(), BufferedImage.TYPE_INT_ARGB );

    _server.getImage( new Point( 0, 0 ), new Dimension( (short) _renderConfig
        .width() + 10, (short) _renderConfig.height() + 10 ) );

    Graphics2D g2d = (Graphics2D) img.getGraphics();

    _server.paint( g2d );

    setDisclaimer( g2d );

    g2d.dispose();

    return img;
  }

  /**
   * Renders a disclaimer into the generated picture.
   * 
   * @param g2d
   */

  protected void setDisclaimer( Graphics2D g2d )
  {
    g2d.setFont( new Font( "Dialog", Font.PLAIN, 8 ) );
    g2d.drawString( "ancat - author: Armin Lunkeit 2013 This tool is GPLv3",
        10, 10 );

    // g2d.drawRect( 20, 20, 40, 10 );
    /*
     * g2d.setColor( Color.BLUE ); g2d.fillRect( 20, 20, 40, 10 ); g2d.setColor(
     * Color.BLACK ); g2d.drawString( "Executable", 65, 30 );
     * 
     * g2d.setColor( Color.YELLOW ); g2d.fillRect( 20, 40, 40, 10 );
     * g2d.setColor( Color.BLACK ); g2d.drawString( "Library", 65, 50 );
     * 
     * g2d.setColor( Color.ORANGE ); g2d.fillRect( 20, 60, 40, 10 );
     * g2d.setColor( Color.BLACK ); g2d.drawString( "Configuration", 65, 70 );
     * 
     * g2d.setColor( Color.RED ); g2d.fillRect( 20, 80, 40, 10 ); g2d.setColor(
     * Color.BLACK ); g2d.drawString( "Undefined", 65, 90 );
     */
  }
}

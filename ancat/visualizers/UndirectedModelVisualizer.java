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
package ancat.visualizers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.collections15.Transformer;

import ancat.common.Edge;
import ancat.common.Vertex;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

/**
 * @author alunkeit
 * 
 */
//public class UndirectedModelVisualizer<V, E> extends GraphInspector<V, E>
public class UndirectedModelVisualizer<V, E> extends ModelVisualizer<V, E>
{

  private UndirectedSparseGraph<V, E> _graph;

  private Layout<V, E> _currentLayout;

  /**
   * 
   */
  public UndirectedModelVisualizer()
  {
  }

  private void circleLayout()
  {
    _currentLayout = new CircleLayout<V, E>( _graph );
  }

  private void ISOMLayout()
  {
    _currentLayout = new ISOMLayout<V, E>( _graph );
  }

  private void KKLayout()
  {
    _currentLayout = new KKLayout<V, E>( _graph );
  }

  private void FRLayout()
  {
    _currentLayout = new FRLayout<V, E>( _graph );
  }

  private void produce( String fileName )
  {
    _currentLayout.setSize( new Dimension( 600, 600 ) );

    VisualizationImageServer<V, E> vv = new VisualizationImageServer<V, E>( _currentLayout, new Dimension( 600, 600 ) );

    vv.setPreferredSize( new Dimension( 600, 600 ) );

    // Setup up a new vertex to paint transformer...
    Transformer<V, Paint> vertexPaint = new Transformer<V, Paint>()
    {
      public Paint transform( V v )
      {
        //return Color.GREEN;
        ancat.common.Vertex vertex = (Vertex)v;
        
        if( vertex instanceof ancat.common.ExecutableFile )
          return Color.BLUE;
        
        if( vertex instanceof ancat.common.DynamicLibrary )
          return Color.YELLOW;
        
        if( vertex instanceof ancat.common.ConfigurationFile )
          return Color.ORANGE;
        
        if( vertex instanceof ancat.common.Unspecified )
          return Color.RED;
        
        return Color.darkGray;
      }
    };
    // Set up a new stroke Transformer for the edges
    float dash[] = { 10.0f };

    final Stroke edgeStroke = new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f );

    Transformer<E, Stroke> edgeStrokeTransformer = new Transformer<E, Stroke>()
    {
      public Stroke transform( E s )
      {
        return edgeStroke;
      }
    };

    Transformer<E, Font> edgeFontTransformer = new Transformer<E, Font>()
    {
      public Font transform( E e )
      {
        return new Font( "Dialog", Font.PLAIN, 10 );
      }
    };

    Transformer<E, String> lblTransformer = new Transformer<E, String>()
    {
      public String transform( E e )
      {
        Edge edge = (Edge) e;
        //return edge.getXMLId();
        return edge.getXMLId() != null ? edge.getXMLId() : "";
      }
    };

    Transformer<V, Font> vertexFontTransformer = new Transformer<V, Font>()
    {
      public Font transform( V v )
      {
        return new Font( "Dialog", Font.PLAIN, 10 );
      }
    };

    Transformer<V, String> vLblTransformer = new Transformer<V, String>()
    {
      public String transform( V v )
      {
        Vertex vertex = (Vertex) v;
        return vertex.getElementID();
      }
    };

    vv.getRenderContext().setVertexFillPaintTransformer( vertexPaint );
    vv.getRenderContext().setEdgeStrokeTransformer( edgeStrokeTransformer );
    vv.getRenderContext().setVertexLabelTransformer( new ToStringLabeller<V>() );
    vv.getRenderContext().setEdgeLabelTransformer( new ToStringLabeller<E>() );
    // ---->
    vv.getRenderContext().setEdgeFontTransformer( edgeFontTransformer );
    vv.getRenderContext().setLabelOffset( 20 );
    vv.getRenderContext().setEdgeLabelTransformer( lblTransformer );

    vv.getRenderContext().setVertexFontTransformer( vertexFontTransformer );
    vv.getRenderContext().setVertexLabelTransformer( vLblTransformer );

    vv.getRenderer().getVertexLabelRenderer().setPosition( Position.N );

    BufferedImage img = new BufferedImage( (int) _currentLayout.getSize().getWidth(), (int) _currentLayout.getSize().getHeight(), BufferedImage.TYPE_INT_ARGB );

    vv.getImage( new Point( 0, 0 ), new Dimension( 700, 700 ) );

    Graphics2D g2d = (Graphics2D) img.getGraphics();

    vv.paint( g2d );
    
    setDisclaimer( g2d );

    g2d.dispose();

    try
    {
      File file = new File( fileName );

      ImageIO.write( img, "png", file );
    }
    catch( IOException e )
    {
      e.printStackTrace();
    }
  }
  
  /**
   * Paints the disclaimer in the generated graphic.
   * 
   * @param g2d
   */
//  private void setDisclaimer( Graphics2D g2d )
//  {
//    g2d.setFont( new Font( "Dialog", Font.PLAIN, 8 ) );
//    g2d.drawString( "ancat (C) Armin Lunkeit 2013", 10, 10 );
//    
//    //g2d.drawRect( 20, 20, 40, 10 );
//    g2d.setColor( Color.BLUE );
//    g2d.fillRect( 20, 20, 40, 10 );
//    g2d.setColor( Color.BLACK );
//    g2d.drawString( "Executable", 65, 30 );
//    
//    g2d.setColor( Color.YELLOW );
//    g2d.fillRect( 20, 40, 40, 10 );
//    g2d.setColor( Color.BLACK );
//    g2d.drawString( "Library", 65, 50 );
//    
//    g2d.setColor( Color.ORANGE );
//    g2d.fillRect( 20, 60, 40, 10 );
//    g2d.setColor( Color.BLACK );
//    g2d.drawString( "Configuration", 65, 70 );
//    
//    g2d.setColor( Color.RED );
//    g2d.fillRect( 20, 80, 40, 10 );
//    g2d.setColor( Color.BLACK );
//    g2d.drawString( "Undefined", 65, 90 );
//  }

  @SuppressWarnings("unchecked")
  @Override
  public void inspect( Object graph )
  {

    this._graph = (UndirectedSparseGraph<V, E>) graph;

    circleLayout();
    produce( "circle.png" );

    ISOMLayout();
    produce( "isom.png" );

    KKLayout();
    produce( "kk.png" );

    FRLayout();
    produce( "fr.png" );
  }

}

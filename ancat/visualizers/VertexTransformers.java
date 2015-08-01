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

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Map;

import org.apache.commons.collections15.Transformer;

import ancat.common.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.picking.PickedInfo;

/**
 * @author a.lunkeit
 * 
 */
public class VertexTransformers<V, E> extends RenderingTransformer
{

  /**
   * The Font used for label drawing
   */
  protected Font _font;

  /**
   * The default node color that is used for rendering
   */
  protected Color _defaultColor;

  /**
   * Default color of picked nodes
   */
  protected Color _defaultpColor;

  /**
   * The graph that gets rendered
   */
  protected Graph<V, E> _graph;

  /**
   * This flag indicates if scaling is permitted or not. Scaling means that some
   * nodes size will increase depending on the incoming edges on these nodes.
   */
  protected boolean _scaling = true;

  /**
   * Default non.scaling shape
   */
  protected Shape _defaultShape = null;

  /**
   * Scaled shape
   */
  protected Shape _defaultShape2 = null;

  /**
   * Scaled shape
   */
  protected Shape _defaultShape3 = null;

  /**
   * Scaled shape
   */
  protected Shape _defaultShape4 = null;

  /**
   * configuration flag if vertex labels need to be rendered
   */
  protected boolean _renderLabel = true;

  /**
   * 
   * @param config
   * @param graph
   */
  public VertexTransformers( RenderingConfiguration config, Graph<V, E> graph )
  {
    super( config );

    _graph = graph;

    _font = super.setupFont( "node:", config.nodeConfiguration() );

    colorConfig();

    nodeConfig();
  }

  /**
   * Check some node specific rendering options so it will not be necessary to
   * do this directly at rendering time.
   */
  private void nodeConfig()
  {
    Map<String, String> nodeConfig = _renderConfig.nodeConfiguration();

    if( nodeConfig.containsKey( "node:scaling" ) )
    {
      String value = _renderConfig.nodeConfiguration().get( "node:scaling" );

      if( value.equalsIgnoreCase( "on" ) )
        _scaling = true;
      else
        _scaling = false;
    }

    if( nodeConfig.containsKey( "node:shape:default" ) )
    {
      String value =
          _renderConfig.nodeConfiguration().get( "node:shape:default" );

      if( value.equalsIgnoreCase( "rectangle" ) )
      {
        _defaultShape = new Rectangle( -5, -5, 10, 10 );
        _defaultShape2 = new Rectangle( -10, -10, 20, 20 );
        _defaultShape3 = new Rectangle( -15, -15, 30, 30 );
        _defaultShape4 = new Rectangle( -20, -20, 40, 40 );
      }

      else if( value.equalsIgnoreCase( "bubble" ) )
      {
        _defaultShape = new Ellipse2D.Double( -5, -5, 10, 10 );
        _defaultShape2 = new Ellipse2D.Double( -10, -10, 20, 20 );
        _defaultShape3 = new Ellipse2D.Double( -15, -15, 30, 30 );
        _defaultShape4 = new Ellipse2D.Double( -20, -20, 40, 40 );
      }
    }
    else
    {
      _defaultShape = new Rectangle( -5, -5, 10, 10 );
      _defaultShape2 = new Rectangle( -10, -10, 20, 20 );
      _defaultShape3 = new Rectangle( -15, -15, 30, 30 );
      _defaultShape4 = new Rectangle( -20, -20, 40, 40 );
    }

    /**
     * This option is required when working with extremely dense graphs.
     * Rendering full labels must be turned off in this case.
     */
    if( nodeConfig.containsKey( "node:label" ) )
    {
      _renderLabel =
          nodeConfig.get( "node:label" ).equals( "on" ) ? true : false;
    }

  }

  /**
   * Prepares the default colour configuration that is used by the rendering
   * engine
   */
  private void colorConfig()
  {
    Map<String, String> nodeConfig = _renderConfig.nodeConfiguration();

    if( nodeConfig.containsKey( "node:default-color" ) )
    {

      try
      {
        Color c = Color.decode( nodeConfig.get( "node:default-color" ) );

        if( c != null )
          _defaultColor = c;
        else
          _defaultColor = Color.darkGray;
      }
      catch( NumberFormatException e )
      {
        _logger.error( "Cannot process color value for node object", e );
        _defaultColor = Color.darkGray;
      }
    }

    if( nodeConfig.containsKey( "node:default-pcolor" ) )
    {

      try
      {
        Color c = Color.decode( nodeConfig.get( "node:default-pcolor" ) );

        if( c != null )
          _defaultpColor = c;
        else
          _defaultpColor = Color.lightGray;
      }
      catch( NumberFormatException e )
      {
        _logger.error( "Cannot process color value for node object", e );
        _defaultpColor = Color.darkGray;
      }
    }
  }

  /**
   * Responsible for colouring the vertices in the graphic.
   * 
   * @author a.lunkeit
   * 
   */
  public class VertexPaint implements Transformer<V, Paint>
  {
    public Paint transform( V v )
    {
      try
      {
        ancat.common.Vertex vertex = (Vertex) v;

        String itemtype = (String) vertex.getProperty( "itemtype" );

        if( null != itemtype )
        {
          // The render configuration may define a dedicated type that contains
          // special rendering information. The type may be defined as follows
          // begin type
          // target = node
          // meta-type = function
          // color = #CCFFAA
          // end type
          if( _renderConfig.getTypes().containsKey( itemtype ) )
          {
            RenderingType type = _renderConfig.getTypes().get( itemtype );

            if( type._color != null )
              return Color.decode( type._color );
          }
        }
      }
      catch( Exception e )
      {

      }

      return _defaultColor;
    }
  };

  public class PickableVertexPaint extends PickableVertexPaintTransformer<V>
  {

    public PickableVertexPaint( PickedInfo<V> pi )
    {
      super( pi, Color.WHITE, Color.WHITE );
    }

    public Paint transform( V v )
    {
      try
      {
        ancat.common.Vertex vertex = (Vertex) v;

        String itemtype = (String) vertex.getProperty( "itemtype" );

        if( null != itemtype )
        {
          if( !pi.isPicked( v ) )
          {
            if( _renderConfig.getTypes().containsKey( itemtype ) )
            {
              RenderingType type = _renderConfig.getTypes().get( itemtype );

              if( type._color != null )
                return Color.decode( type._color );
            }
          }
          else if( pi.isPicked( v ) )
          {
            RenderingType type = _renderConfig.getTypes().get( itemtype );

            _logger.debug( "####### picked node: " + v.toString() );

            if( type._pcolor != null )
              return Color.decode( type._pcolor );
          }
        }
        else
        {
          if( !pi.isPicked( v ) )
            return _defaultColor;
          else
            return _defaultpColor;
        }
      }
      catch( Exception e )
      {

      }

      if( !pi.isPicked( v ) )
        return _defaultColor;
      else
        return _defaultpColor;
    }

  }

  /**
   * Transformer responsible delivering the Font for the graphic visualization
   * 
   * @author a.lunkeit
   * 
   */
  public class FontTransformer implements Transformer<V, Font>
  {
    public FontTransformer()
    {
      super();

      _logger.debug( "Contructor: Font Transformer" );
    }

    public Font transform( V v )
    {
      return _font;
    }
  };

  /**
   * Implementation responsible delivering the label of the Vertex
   * 
   * @author a.lunkeit
   * 
   */

  public class LabelTransformer implements Transformer<V, String>
  {
    private boolean _on = true;

    public void enable( boolean enable )
    {
      _on = enable;
    }

    public String transform( V v )
    {
      // _logger.debug( "LabelTransformer, _renderLabel := " + _renderLabel );

      if( false == _renderLabel || false == _on )
        return "";

      if( v instanceof Vertex )
      {
        Vertex vertex = (Vertex) v;

        return (String) vertex.getProperty( "filename" );
      }

      // _logger.debug( "vertex label: returning default" );

      return v.toString();

    }
  };

  /**
   * 
   * @author alunkeit
   * 
   *         This implementation provides the shape of the vertex to be drawn.
   *         Depending on the node type, the shape and the shape scaling is
   *         performed.
   * 
   * @param <E>
   *          The Edge Type
   * 
   */
  public class ShapeTransformer implements Transformer<V, Shape>
  {
    @Override
    public Shape transform( V v )
    {
      if( true == _scaling )
      {
        int cnt = _graph.getInEdges( v ).size();

        if( cnt < 3 )
          return _defaultShape;
        else if( cnt < 5 )
          return _defaultShape2;
        else if( cnt < 10 )
          return _defaultShape3;
        else
          return _defaultShape4;
      }

      return _defaultShape;
    }
  }
}

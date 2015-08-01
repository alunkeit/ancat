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
package ancat.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import ancat.common.Edge;
import ancat.common.Vertex;
import ancat.visualizers.ConfigurationReader;
import ancat.visualizers.ParserException;
import ancat.visualizers.VertexTransformers;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.LensMagnificationGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.HyperbolicTransformer;
import edu.uci.ics.jung.visualization.transform.LayoutLensSupport;
import edu.uci.ics.jung.visualization.transform.MagnifyTransformer;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.MagnifyShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;

/**
 * 
 * @author alunkeit
 * 
 *         Rendering Component for Graphs
 */
public class GraphView extends JPanel implements LayoutChangeListener
{

  @SuppressWarnings("rawtypes")
  Graph _graph = null;

  private Logger _logger = Logger.getRootLogger();

  private VisualizationViewer<Vertex, Edge> _viewer;

  private DefaultModalGraphMouse<Vertex, Edge> _graphMouse1;

  private AncatGraphMousePlugin<Vertex, Edge> _ancatMousePlugin;

  private ViewLensSupport<Vertex, Edge> _hyperbolicViewSupport;

  private ViewLensSupport<Vertex, Edge> _magnifyViewSupport;

  private LayoutLensSupport<Vertex, Edge> _magnifyLayoutSupport;

  private LayoutLensSupport<Vertex, Edge> _hyperbolicLayoutSupport;

  private VertexTransformers<Vertex, Edge> _transformers;

  private VertexTransformers<Vertex, Edge>.LabelTransformer _vertexLabelTransformer;

  final ScalingControl scaler = new CrossoverScalingControl();

  private Logger _output = Logger.getLogger( "output" );

  @SuppressWarnings("rawtypes")
  void setGraph( Graph graph )
  {
    _graph = graph;

  }

  @SuppressWarnings("rawtypes")
  Graph getGraph()
  {
    return _graph;
  }

  void activate()
  {
    this.setPreferredSize( new Dimension( 1600, 1600 ) );

    // create one layout for the graph
    FRLayout<Vertex, Edge> layout = new FRLayout<Vertex, Edge>( _graph );
    // layout.setMaxIterations( 500 );
    layout.setMaxIterations( 10 );

    VisualizationModel<Vertex, Edge> vm =
        new DefaultVisualizationModel<Vertex, Edge>( layout );

    // -->
    ConfigurationReader r = new ConfigurationReader( "directed.conf" );

    try
    {
      r.parse();
    }
    catch( ParserException e )
    {
      _logger.error( e );
    }

    _transformers = new VertexTransformers<Vertex, Edge>( r, _graph );

    _vertexLabelTransformer = _transformers.new LabelTransformer();

    _viewer = new VisualizationViewer<Vertex, Edge>( vm );

    /**
     * KeyListener is utilized to provide Zoom operations in case that no mouse
     * wheel is available
     */
    _viewer.addKeyListener( new KeyListener()
    {

      @Override
      public void keyTyped( KeyEvent e )
      {
      }

      @Override
      public void keyPressed( KeyEvent e )
      {
      }

      @Override
      public void keyReleased( KeyEvent e )
      {
        if( e.getKeyChar() == '+' )
        {
          _logger.debug( "zoom in" );

          scaler.scale( _viewer, 1.1f, _viewer.getCenter() );
        }
        else if( e.getKeyChar() == '-' )
        {
          _logger.debug( "zoom out" );

          scaler.scale( _viewer, 1 / 1.1f, _viewer.getCenter() );
        }
      }

    } );

    _viewer.setBackground( Color.white );

    _viewer.getRenderContext().setEdgeDrawPaintTransformer(
        new PickableEdgePaintTransformer<Edge>( _viewer.getPickedEdgeState(),
            Color.black, Color.cyan ) );

    _viewer.getRenderContext()
        .setVertexFillPaintTransformer(
            _transformers.new PickableVertexPaint( _viewer
                .getPickedVertexState() ) );

    _viewer.getRenderContext().setVertexShapeTransformer(
        _transformers.new ShapeTransformer() );

    _viewer.getRenderContext().setVertexLabelTransformer(
        _vertexLabelTransformer );

    // <--

    _viewer.getRenderer().getVertexLabelRenderer().setPosition(
        Renderer.VertexLabel.Position.CNTR );

    // add default listener for ToolTips.
    _viewer.setVertexToolTipTransformer( new ToStringLabeller<Vertex>() );

    GraphZoomScrollPane gzsp = new GraphZoomScrollPane( _viewer );
    add( gzsp, BorderLayout.CENTER );

    RenderContext<Vertex, Edge> rc = _viewer.getRenderContext();

    AnnotatingGraphMousePlugin<Vertex, Edge> annotatingPlugin =
        new AnnotatingGraphMousePlugin<Vertex, Edge>( rc );

    _ancatMousePlugin = new AncatGraphMousePlugin<Vertex, Edge>();

    _graphMouse1 = new DefaultModalGraphMouse<Vertex, Edge>();
    _graphMouse1.add( _ancatMousePlugin );
    _viewer.setGraphMouse( _graphMouse1 );

    /**
     * Adds an item listener so picked vertices will be detected
     */
    _viewer.getPickedVertexState().addItemListener( new ItemListener()
    {

      @Override
      public void itemStateChanged( ItemEvent e )
      {
        _logger.info( "item state changed: " + e.getStateChange() );

        if( e.getSource() != null )
        {
          _logger.info( "event source: " + e.getSource() );
          _logger.info( "event getItemSelectable(): " + e.getItemSelectable() );
        }

        if( e.getItem() != null )
        {
          _logger.info( "item: " + e.getItem() );

          // In case that a valid vertex has been detected, the information will
          // be displayed
          if( e.getStateChange() == ItemEvent.SELECTED )
          {
            _output.info( "Selected Vertex: " + e.getItem() );

            _output.info( "\t outgoing edges (delta+): " + _graph.getOutEdges(
                e.getItem() ).size() );
            _output.info( "\t incoming edges (delta-): " + _graph.getInEdges(
                e.getItem() ).size() );
            _output.info( "\t neighbor count: " + _graph.getNeighborCount( e
                .getItem() ) );
            _output.info( "\n" );
          }
        }
      }

    } );

    // --> hyperbolic view support
    _hyperbolicViewSupport =
        new ViewLensSupport<Vertex, Edge>( _viewer,
            new HyperbolicShapeTransformer( _viewer, _viewer.getRenderContext()
                .getMultiLayerTransformer().getTransformer( Layer.VIEW ) ),
            new ModalLensGraphMouse() );

    _hyperbolicLayoutSupport =
        new LayoutLensSupport<Vertex, Edge>( _viewer,
            new HyperbolicTransformer( _viewer, _viewer.getRenderContext()
                .getMultiLayerTransformer().getTransformer( Layer.LAYOUT ) ),
            new ModalLensGraphMouse() );

    _magnifyViewSupport =
        new ViewLensSupport<Vertex, Edge>( _viewer,
            new MagnifyShapeTransformer( _viewer, _viewer.getRenderContext()
                .getMultiLayerTransformer().getTransformer( Layer.VIEW ) ),
            new ModalLensGraphMouse( new LensMagnificationGraphMousePlugin(
                1.f, 6.f, .2f ) ) );

    _magnifyLayoutSupport =
        new LayoutLensSupport<Vertex, Edge>( _viewer, new MagnifyTransformer(
            _viewer, _viewer.getRenderContext().getMultiLayerTransformer()
                .getTransformer( Layer.LAYOUT ) ), new ModalLensGraphMouse(
            new LensMagnificationGraphMousePlugin( 1.f, 6.f, .2f ) ) );

    _hyperbolicLayoutSupport.getLensTransformer().setLensShape(
        _hyperbolicViewSupport.getLensTransformer().getLensShape() );

    _magnifyViewSupport.getLensTransformer().setLensShape(
        _hyperbolicLayoutSupport.getLensTransformer().getLensShape() );

    _magnifyLayoutSupport.getLensTransformer().setLensShape(
        _magnifyViewSupport.getLensTransformer().getLensShape() );

    _graphMouse1.addItemListener( _hyperbolicViewSupport.getGraphMouse()
        .getModeListener() );

    _graphMouse1.addItemListener( new ItemListener()
    {

      @Override
      public void itemStateChanged( ItemEvent e )
      {
        _logger.info( "item changed # := " + e.getSource().toString() );
      }

    } );
  }

  public void beforeDisplay()
  {
    _output.info( "Graph properties" );
    _output.info( "\t count of nodes: " + _graph.getVertexCount() );
    _output.info( "\t edge count: " + _graph.getEdgeCount() );
  }

  @Override
  public void layoutChangeRequest( LayoutEvent event )
  {
    _logger.debug( "layout change requested" );

    if( event._type == LayoutEvent.GraphLayout )
    {
      _logger.debug( "changing graph layout..." );
      _logger.debug( "event._value: " + event._other );

      AbstractLayout<Vertex, Edge> layout = null;

      switch( (Integer) event._other )
      {
        case LayoutChangeListener.CIRCLE_LAYOUT:
          layout = new CircleLayout<Vertex, Edge>( _graph );
          break;

        case LayoutChangeListener.FR_LAYOUT:
          layout = new FRLayout<Vertex, Edge>( _graph );
          ((FRLayout<Vertex, Edge>) (layout)).setMaxIterations( 10 );
          break;

        case LayoutChangeListener.ISOM_LAYOUT:
          layout = new ISOMLayout<Vertex, Edge>( _graph );
          break;

        case LayoutChangeListener.KK_LAYOUT:
          layout = new KKLayout<Vertex, Edge>( _graph );
          ((KKLayout<Vertex, Edge>) (layout)).setMaxIterations( 10 );
          break;

        case LayoutChangeListener.SPRING_LAYOUT:
          layout = new SpringLayout<Vertex, Edge>( _graph );
          break;

        default:
          return;
      }

      _viewer.setGraphLayout( layout );
    }
    else if( event._type == LayoutEvent.MouseMode )
    {
      _graphMouse1.setMode( (Mode) event._other );
    }
    else if( event._type == LayoutEvent.HyperbolicView )
    {
      _hyperbolicViewSupport.activate( (Boolean) event._other );
    }
    else if( event._type == LayoutEvent.MagnifyView )
    {
      _logger.debug( "magnify view support - activate" );
      _magnifyViewSupport.activate( (Boolean) event._other );
    }
    else if( event._type == LayoutEvent.VertexLabel )
    {
      Boolean enabled = (Boolean) event._other;

      setLabelsEnabled( enabled );
    }
    else if( event._type == LayoutEvent.PickVertex )
    {
      PickedState<Vertex> state = _viewer.getPickedVertexState();

      Hashtable<String, Object> values =
          (Hashtable<String, Object>) event._other;

      state.pick( (Vertex) values.get( "vertex" ), (Boolean) values
          .get( "enabled" ) );
    }
  }

  protected void setLabelsEnabled( boolean enabled )
  {
    _logger.debug( "Displaying labels is: " + enabled );

    _vertexLabelTransformer.enable( enabled );

    _viewer.getRenderer().getVertexLabelRenderer().setPosition(
        Renderer.VertexLabel.Position.CNTR );

    _viewer.getGraphLayout().setGraph( _graph );

    _viewer.updateUI();

    _viewer.repaint();

    updateUI();

    _logger.debug( "end of method setLabelsEnabled()" );
  }

  protected Set<Vertex> checkSelectedVertex()
  {
    return _viewer.getPickedVertexState().getPicked();
  }
}

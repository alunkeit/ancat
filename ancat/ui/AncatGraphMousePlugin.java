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

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * 
 * @author alunkeit
 * 
 *         This Plugin implementation allows reaction on mouse click events in
 *         the graph visualization. We have a special use case: when rendering
 *         huge networks, node labels are turned off. Nodes can be selected in
 *         the visualization and the corresponding label will be selected in the
 *         table view on the bottom. In order to react on such mouse event, the
 *         plugin is inserted into the GraphMouse hierarchy.
 * 
 * @param <V>
 * @param <E>
 */
public class AncatGraphMousePlugin<V, E> extends PickingGraphMousePlugin<V, E> implements ItemSelectable
{

  protected Logger _logger = Logger.getRootLogger();

  protected EventListenerList listenerList = new EventListenerList();

  /**
   * last selected node
   */
  protected V _selected;

  /**
   * 
   */
  public AncatGraphMousePlugin()
  {
    super();
  }

  /**
   * 
   * @param selectionModifiers
   * @param additionalModifiers
   */
  public AncatGraphMousePlugin( int selectionModifiers, int additionalModifiers )
  {
    super( selectionModifiers, additionalModifiers );
  }

  /**
   * 
   */
  @Override
  public void mousePressed( MouseEvent e )
  {
    down = e.getPoint();
    @SuppressWarnings("unchecked")
    VisualizationViewer<V, E> vv = (VisualizationViewer<V, E>) e.getSource();
    GraphElementAccessor<V, E> pickSupport = vv.getPickSupport();
    PickedState<V> pickedVertexState = vv.getPickedVertexState();
    PickedState<E> pickedEdgeState = vv.getPickedEdgeState();

    if( pickSupport != null && pickedVertexState != null )
    {
      Layout<V, E> layout = vv.getGraphLayout();

      if( e.getModifiers() == modifiers )
      {
        rect.setFrameFromDiagonal( down, down );

        // p is the screen point for the mouse event
        Point2D ip = e.getPoint();

        vertex = pickSupport.getVertex( layout, ip.getX(), ip.getY() );

        if( null != vertex )
        {
          _logger.info( vertex );
          _selected = vertex;

          fireItemStateChanged( new ItemEvent( this,
              ItemEvent.ITEM_STATE_CHANGED, _selected, ItemEvent.SELECTED ) );
        }
        else if( _selected != null )
        {
          fireItemStateChanged( new ItemEvent( this,
              ItemEvent.ITEM_STATE_CHANGED, _selected, ItemEvent.DESELECTED ) );

          _selected = null;
        }
      }
    }

    super.mousePressed( e );
  }

  @Override
  public void mouseEntered( MouseEvent e )
  {
    _logger.debug( "mouse entered" );
  }

  /**
   * add a listener for mode changes
   */
  @Override
  public void addItemListener( ItemListener aListener )
  {
    listenerList.add( ItemListener.class, aListener );
  }

  /**
   * remove a listener for mode changes
   */
  @Override
  public void removeItemListener( ItemListener aListener )
  {
    listenerList.remove( ItemListener.class, aListener );
  }

  /**
   * Returns an array of all the <code>ItemListener</code>s added to this
   * JComboBox with addItemListener().
   * 
   * @return all of the <code>ItemListener</code>s added or an empty array if no
   *         listeners have been added
   * @since 1.4
   */
  public ItemListener[] getItemListeners()
  {
    return listenerList.getListeners( ItemListener.class );
  }

  @Override
  public Object[] getSelectedObjects()
  {
    return new Object[] { _selected };
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type.
   * 
   * @param e
   *          the event of interest
   * 
   * @see EventListenerList
   */
  protected void fireItemStateChanged( ItemEvent e )
  {
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for( int i = listeners.length - 2; i >= 0; i -= 2 )
    {
      if( listeners[i] == ItemListener.class )
      {
        ((ItemListener) listeners[i + 1]).itemStateChanged( e );
      }
    }
  }
}

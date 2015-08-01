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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

import ancat.common.Edge;
import ancat.common.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;

/**
 * @author alunkeit
 * 
 */
public class GraphSettingsPanel extends JPanel implements ItemListener, ListCellRenderer<LayoutEvent>, ActionListener
{
  /**
   * 
   */
  private static final long serialVersionUID = 2891066162787040441L;

  private JComboBox<LayoutEvent> _layouts;

  private JComboBox<LayoutEvent> _mouseModes;

  private LayoutChangeListener _activeListener;

  private Logger _logger = Logger.getRootLogger();

  private JTable _table = null;

  private GraphTableModel _tableModel = new GraphTableModel();

  private JToolBar _toolBar = null;

  private boolean _initialized = false;

  private JScrollPane _tablePane;

  public GraphSettingsPanel()
  {
    super();

    setLayout( new BorderLayout() );

    addComponentListener( new ComponentListener()
    {

      public void componentHidden( ComponentEvent e )
      {
      }

      public void componentMoved( ComponentEvent e )
      {
      }

      public void componentResized( ComponentEvent e )
      {
      }

      public void componentShown( ComponentEvent e )
      {
        _logger.debug( e.getComponent().getClass().getName() + " --- Shown" );

        updateView();

      }
    } );

  }

  public void updateView()
  {
    if( null != _tablePane )
      _tablePane.getViewport().updateUI();
    updateUI();
  }

  protected void activate( LayoutChangeListener listener, Graph<Vertex, Edge> graph )
  {

    _activeListener = listener;

    if( !_initialized )
    {

      _toolBar = new JToolBar();

      Vector<LayoutEvent> vecLayouts = new Vector<LayoutEvent>();

      vecLayouts.add( new LayoutEvent( LayoutEvent.GraphLayout, "FRLayout",
          LayoutChangeListener.FR_LAYOUT ) );
      vecLayouts.add( new LayoutEvent( LayoutEvent.GraphLayout, "ISOM-Layout",
          LayoutChangeListener.ISOM_LAYOUT ) );
      vecLayouts.add( new LayoutEvent( LayoutEvent.GraphLayout, "Circle",
          LayoutChangeListener.CIRCLE_LAYOUT ) );
      vecLayouts.add( new LayoutEvent( LayoutEvent.GraphLayout, "KK-Layout",
          LayoutChangeListener.KK_LAYOUT ) );
      vecLayouts.add( new LayoutEvent( LayoutEvent.GraphLayout,
          "Spring-Layout", LayoutChangeListener.SPRING_LAYOUT ) );

      _layouts = new JComboBox<LayoutEvent>( vecLayouts );
      _layouts.addItemListener( this );
      _layouts.setRenderer( this );
      _layouts.setBorder( BorderFactory.createTitledBorder( "Layout Types" ) );

      Vector<LayoutEvent> vecMouseModes = new Vector<LayoutEvent>();

      vecMouseModes.add( new LayoutEvent( LayoutEvent.MouseMode,
          "Transforming", ModalGraphMouse.Mode.TRANSFORMING ) );
      vecMouseModes.add( new LayoutEvent( LayoutEvent.MouseMode, "Edit",
          ModalGraphMouse.Mode.EDITING ) );
      vecMouseModes.add( new LayoutEvent( LayoutEvent.MouseMode, "Picking",
          ModalGraphMouse.Mode.PICKING ) );

      _mouseModes = new JComboBox<LayoutEvent>( vecMouseModes );
      _mouseModes.addItemListener( this );
      _mouseModes.setRenderer( this );
      _mouseModes.setBorder( BorderFactory.createTitledBorder( "Mouse Mode" ) );

      JToggleButton hyperBolicView = new JToggleButton();
      hyperBolicView.setText( "Hyperbolic" );
      hyperBolicView.addActionListener( this );
      hyperBolicView.setActionCommand( "HYPERBOLIC" );

      JToggleButton magnifyView = new JToggleButton();
      magnifyView.setText( "Magnify" );
      magnifyView.addActionListener( this );
      magnifyView.setActionCommand( "MAGNIFY" );

      JCheckBox labelSwitch = new JCheckBox( "Vertex Label", true );
      labelSwitch.addActionListener( this );
      labelSwitch.setActionCommand( "VERTEX_LABEL" );

      _toolBar.add( _layouts );
      _toolBar.add( _mouseModes );
      _toolBar.add( hyperBolicView );
      _toolBar.add( magnifyView );
      _toolBar.add( labelSwitch );

      this.add( _toolBar, BorderLayout.NORTH );
    }

    if( null != graph )
    {
      _tableModel.init( graph );

      if( _table == null )
      {
        _table = new JTable( _tableModel );
        _table.setName( "graph table 1" );

        _table.setCellSelectionEnabled( true );
        _table.setFillsViewportHeight( true );

        ListSelectionModel cellSelectionModel = _table.getSelectionModel();

        cellSelectionModel
            .setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        cellSelectionModel
            .addListSelectionListener( new ListSelectionListener()
            {
              Object _lastSelected = null;

              public void valueChanged( ListSelectionEvent e )
              {
                int[] selectedRow = _table.getSelectedRows();
                int[] selectedColumns = _table.getSelectedColumns();

                int row = selectedRow[0];

                String label = _table.getValueAt( row, 0 ).toString();

                _logger.debug( "label: " + label );

                Object vertex = _tableModel.getValueAt( row, 0 );

                Hashtable<String, Object> values =
                    new Hashtable<String, Object>();

                // In case that a Vertex has been selected fire event and
                // de-select
                if( null != _lastSelected )
                {
                  values.put( "vertex", _lastSelected );
                  values.put( "enabled", false );

                  _activeListener
                      .layoutChangeRequest( new LayoutEvent(
                          LayoutEvent.PickVertex, _lastSelected.toString(),
                          values ) );
                }

                values.put( "vertex", vertex );
                values.put( "enabled", true );

                // fire selection event
                _activeListener.layoutChangeRequest( new LayoutEvent(
                    LayoutEvent.PickVertex, label, values ) );

                // store current object and save state
                _lastSelected = vertex;
              }

            } );

        _tablePane = new JScrollPane( _table );
        _tablePane
            .setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
        _tablePane
            .setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
        add( _tablePane, BorderLayout.CENTER );
      }
    }

    _initialized = true;
  }

  @Override
  public void itemStateChanged( ItemEvent e )
  {
    if( e.getStateChange() == ItemEvent.SELECTED )
    {
      System.out.println( "item: " + e.getItem() );

      _activeListener.layoutChangeRequest( ((LayoutEvent) (e.getItem())) );
    }
  }

  @Override
  public Component getListCellRendererComponent( JList<? extends LayoutEvent> list, LayoutEvent value, int index, boolean isSelected, boolean cellHasFocus )
  {
    Color background;
    Color foreground;
    JLabel c = value._component;

    JList.DropLocation dropLocation = list.getDropLocation();
    if( dropLocation != null && !dropLocation.isInsert() && dropLocation
        .getIndex() == index )
    {

      background = Color.WHITE;
      foreground = Color.BLUE;

      // check if this cell is selected
    }
    else if( isSelected )
    {
      background = Color.WHITE;
      foreground = Color.RED;

      // unselected, and not the DnD drop location
    }
    else
    {
      background = Color.WHITE;
      foreground = Color.BLACK;
    }

    c.setBackground( background );
    c.setForeground( foreground );

    return c;
  }

  @Override
  public void actionPerformed( ActionEvent e )
  {
    AbstractButton btn = (AbstractButton) e.getSource();

    _activeListener.layoutChangeRequest( new LayoutEvent( e, "", new Boolean(
        btn.isSelected() ) ) );
  }

  /**
   * 
   * @author alunkeit
   * 
   */
  protected class GraphTableModel extends AbstractTableModel
  {
    private Graph _graph;

    private Object[] _vertices;

    public void init( Graph graph )
    {
      _graph = graph;
      _vertices = graph.getVertices().toArray();
    }

    @Override
    public String getColumnName( int index )
    {
      _logger.debug( "GraphTableModel -> getColumnName" );

      switch( index )
      {
        case 0:
          return "Vertex";

        case 1:
          return "Outgoining Edges";

        case 2:
          return "Incoming Edges";

        case 3:
          return "Mapping";

        default:
          return "";
      }
    }

    @Override
    public int getRowCount()
    {
      return _graph.getVertexCount();
    }

    @Override
    public int getColumnCount()
    {
      return 4;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex )
    {
      if( columnIndex == 0 )
        return _vertices[rowIndex];

      if( columnIndex == 1 )
        return _graph.getOutEdges( _vertices[rowIndex] ).size();

      if( columnIndex == 2 )
        return _graph.getInEdges( _vertices[rowIndex] ).size();

      return "not set";
    }

  }

}

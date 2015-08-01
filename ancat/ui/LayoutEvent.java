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

import java.awt.event.ActionEvent;

import javax.swing.JLabel;

/**
 * 
 * @author alunkeit
 * 
 *         This class is used to put messages between several parts of the UI.
 * 
 */
public class LayoutEvent
{
  /**
   * Indicates a change in the GraphLayout
   */
  final static int GraphLayout = 1;

  /**
   * Indicates a change in the Mouse Mode
   */
  final static int MouseMode = 2;

  /**
   * Indicates a change in the hyperbolic view
   */
  final static int HyperbolicView = 3;

  /**
   * Indicates a change in the magnify view
   */
  final static int MagnifyView = 4;

  /**
   * turn on / turn off label view
   */
  final static int VertexLabel = 5;

  final static int PickVertex = 6;

  protected int _type;

  protected String _label;

  protected JLabel _component;

  protected Object _other;

  /**
   * 
   * @param e
   * @param label
   * @param value
   */
  public LayoutEvent( ActionEvent e, String label, Object value )
  {
    if( e.getActionCommand().equalsIgnoreCase( "HYPERBOLIC" ) )
    {
      _type = HyperbolicView;
    }
    else if( e.getActionCommand().equalsIgnoreCase( "MAGNIFY" ) )
    {
      _type = MagnifyView;
    }
    else if( e.getActionCommand().equalsIgnoreCase( "VERTEX_LABEL" ) )
    {
      _type = VertexLabel;
    }

    init( label, value );
  }

  /**
   * 
   * @param type
   * @param label
   * @param value
   */
  public LayoutEvent( int type, String label, Object value )
  {
    _type = type;
    init( label, value );
  }

  private void init( String label, Object value )
  {
    _label = label;
    _other = value;
    _component = new JLabel();
    _component.setText( _label );
  }

}

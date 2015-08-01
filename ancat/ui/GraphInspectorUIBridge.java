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

import org.apache.log4j.Logger;

import ancat.common.Edge;
import ancat.common.GraphInspector;
import ancat.common.InspectionOutputConsumer;
import ancat.common.Vertex;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 */
public class GraphInspectorUIBridge<V, E> extends GraphInspector<V, E>
{

  private Logger _logger = Logger.getRootLogger();

  /*
   * (non-Javadoc)
   * 
   * @see ancat.common.GraphInspector#inspect(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public void inspect( Object graph )
  {
    _logger.debug( "######################################" );
    _logger.debug( "model file: " + _globalConfiguration.get( "modelFile" ) );
    _logger.debug( "######################################" );

    _output.setGraph( (Graph<V, E>) graph );

    for( InspectionOutputConsumer c : _consumers )
    {
      c.consume( (GraphInspector<Vertex, Edge>) this );
    }
  }
}

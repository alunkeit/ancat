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
package ancat.drivers;

import java.util.Iterator;
import java.util.LinkedList;

import ancat.builders.ModelBuilder;
import ancat.common.GraphInspector;

public class ModelDriver<V, E> extends Driver<V, E>
{

  @Override
  public void analyzeModel( ModelBuilder builder,
      LinkedList<GraphInspector<V, E>> inspectors )
  {
    try
    {
      builder.createModel();

      Object graph = builder.getGraph();

      Iterator<GraphInspector<V, E>> iter = inspectors.iterator();

      while( iter.hasNext() )
      {
        GraphInspector<V, E> inspector = iter.next();

        // If the inspector needs input data from another inspector then the
        // id-ref
        // attribute is used to specify this.
        if( inspector.hasIdRef() )
        {
          // Object output = inspectors.get( inspector.inputReference
          // ).getOutput();
          // find the element with the corresponding id
          Iterator<GraphInspector<V, E>> ii = inspectors.iterator();

          while( ii.hasNext() )
          {
            GraphInspector<V, E> gi = ii.next();

            if( gi.getId().equals(inspector.getIdRef()) )
            {
              inspector.setInput(gi.getOutput());
              break;
            }
          }
        }

        inspector.setGlobalConfiguration(_configuration);

        inspector.inspect(graph);
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

}

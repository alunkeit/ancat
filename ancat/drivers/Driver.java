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

import java.util.Hashtable;
import java.util.LinkedList;

import ancat.builders.ModelBuilder;
import ancat.common.GraphInspector;



/**
 * @author a.lunkeit
 *
 */
public abstract class Driver<V, E>
{
  protected Hashtable<String, Object> _configuration;
  
  
  @SuppressWarnings("unchecked")
  public void setConfiguration( Hashtable<String, Object> configuration )
  {
    _configuration = (Hashtable<String, Object>) configuration.clone();
  }
  
  public abstract void analyzeModel( ModelBuilder builder, LinkedList< GraphInspector<V, E> > inspectors );
}

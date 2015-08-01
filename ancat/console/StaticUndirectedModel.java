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
package ancat.console;

import java.io.File;

import ancat.builders.ModelBuilder;
import ancat.builders.UndirectedModelBuilder;
import ancat.common.Edge;
import ancat.common.Vertex;
import ancat.drivers.Driver;
import ancat.drivers.ModelDriver;

/**
 * @author alunkeit
 * 
 *         This implementation reads the description of a static undirected
 *         graph and performs various tests on it. For performance reasons it is
 *         a console program.
 */
public class StaticUndirectedModel extends ConsoleModelChecker
{

  /**
   * Class constructor
   */
  protected StaticUndirectedModel()
  {
    super();
  }

  public void analyze()
  {
    try
    {
      Driver<Vertex, Edge> d = new ModelDriver<Vertex, Edge>();

      for( String file : _modelFiles )
      {

        ModelBuilder builder = new UndirectedModelBuilder( new File( file ) );

        d.setConfiguration( getConfiguration() );

        d.analyzeModel( builder, getInspectors() );
      }
    }
    catch( Exception e )
    {
      _logger.error( "Error: ", e );
    }
  }

  /**
   * @param args
   * 
   *          http://commons.apache.org/cli/usage.html
   */
  public static void main( String[] args )
  {
    try
    {
      // Create a new driver
      StaticUndirectedModel driver = new StaticUndirectedModel();
      // Evaluate the command line presented to the driver
      driver.evaluateCommandLine( args );
      // Prepare the inspectors to be used on the load graph model
      driver.prepareInspectors();
      // Analyze the properties of the Graph by using the created drivers
      driver.analyze();
    }
    catch( Exception e )
    {
      System.out.println( "Exception caught: " + e.getMessage() );
    }
  }
}

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

import ancat.builders.DirectedModelBuilder;
import ancat.builders.ModelBuilder;
import ancat.common.Edge;
import ancat.common.GraphInspector;
import ancat.common.Vertex;
import ancat.drivers.Driver;
import ancat.drivers.ModelDriver;

/**
 * @author a.lunkeit
 * 
 */
public class StaticDirectedModel extends ConsoleModelChecker
{

  /**
   * Class constructor
   */
  public StaticDirectedModel()
  {
    super();
  }

  /**
   * Do analysis of the model files.
   */
  public void analyze()
  {
    try
    {
      Driver<Vertex, Edge> d = new ModelDriver<Vertex, Edge>();

      for( String file : _modelFiles )
      {
        ModelBuilder builder = new DirectedModelBuilder( new File( file ) );

        d.setConfiguration( getConfiguration() );

        for( GraphInspector<Vertex, Edge> inspector : getInspectors() )
        {
          inspector.setModelFile( file );
        }
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
      StaticDirectedModel driver = new StaticDirectedModel();
      // Evaluate the command line presented to the driver
      driver.evaluateCommandLine( args );
      // do the analysis
      driver.runAnalyzers();
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

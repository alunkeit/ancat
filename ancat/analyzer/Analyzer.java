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
package ancat.analyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * @author alunkeit
 * 
 */
public abstract class Analyzer
{

  protected Map<String, String> _configuration =
      new Hashtable<String, String>();

  protected static Logger _logger = Logger.getRootLogger();

  protected Analyzer()
  {

  }

  public abstract void init( String file );

  /**
   * Persist the output of the analyzer operation
   * 
   * @throws Exception
   */
  public abstract void doOutput() throws Exception;

  /**
   * Returns the output of the analyzer in StringBuffer
   * 
   * @return StringBuffer with analyzer output
   * @throws Exception
   */
  public abstract StringBuffer getOutput() throws Exception;

  /**
   * Do the basic analysis on the input data
   * 
   * @throws IOException
   * @throws AnalyzerException
   */
  public abstract void analyze() throws IOException,
      AnalyzerException,
      TransformationError;

  /**
   * Shared method between analyzer to read a configuration file
   * 
   * @param file
   * @throws IOException
   */
  protected void readConfiguration( String file ) throws IOException
  {
    BufferedReader reader =
        new BufferedReader( new LineNumberReader( new FileReader( new File(
            file ) ) ) );

    String line = null;

    while( (line = reader.readLine()) != null )
    {
      if( line.isEmpty() )
        continue;

      line = line.trim();

      if( line.startsWith( "#" ) )
        continue;

      StringTokenizer tokenizer = new StringTokenizer( line, "=" );

      if( tokenizer.countTokens() != 2 )
        continue;

      _configuration.put( tokenizer.nextToken().trim(), tokenizer.nextToken()
          .trim() );

    }

    for( String key : _configuration.keySet() )
    {
      _logger.debug( key + " : " + _configuration.get( key ) );
    }

    reader.close();
  }
}

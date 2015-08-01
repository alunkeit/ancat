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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * @author alunkeit
 * 
 *         This class utilizes the free tool cscope to obtain information about
 *         the reviewed software packet.
 * 
 *         TODO: Currently the analyzer is responsible for persisting the result
 *         of the analysis. It could use a kind of storage interface which
 *         encapsulates the way of persisting the result data.
 */
public class CScopeAnalyzer extends Analyzer
{
  /**
   * Stores the path to the configuration file
   */
  private String _configFile;

  /**
   * Stores configuration data
   */
  // private Map<String, String> _confData;

  /**
   * Logging instance
   */
  // private Logger _logger = Logger.getRootLogger();

  /**
   * OutputStream is created in case that value cscope.output is available
   */
  private BufferedOutputStream _outStream;

  /**
   * Output transformer instance. The analyzer output may be transformed into
   * several flavours
   * 
   */
  private OutputTransformer _transformer;

  public CScopeAnalyzer()
  {
  }

  public void init( String configFile )
  {
    _configFile = configFile;

    // _confData = new Hashtable<String, String>();
  }

  /*
   * private void parseConfiguration() throws IOException { BufferedReader
   * reader = new BufferedReader(new LineNumberReader( new FileReader(new
   * File(_configFile))));
   * 
   * String line = null;
   * 
   * while( (line = reader.readLine()) != null ) { if( line.isEmpty() )
   * continue;
   * 
   * line = line.trim();
   * 
   * if( line.startsWith("#") ) continue;
   * 
   * StringTokenizer tokenizer = new StringTokenizer(line, "=");
   * 
   * if( tokenizer.countTokens() != 2 ) continue;
   * 
   * _confData.put(tokenizer.nextToken().trim(), tokenizer.nextToken().trim());
   * 
   * }
   * 
   * for( String key : _confData.keySet() ) { _logger.debug(key + " : " +
   * _confData.get(key)); }
   * 
   * reader.close(); }
   */

  private void produce( String func ) throws AnalyzerException
  {

    // change into the selected directory and call cscope
    try
    {
      Process p =
          Runtime.getRuntime().exec( "cscope -d -l -L -2" + func, null,
              new File( _configuration.get( "cscope.dir" ) ) );

      BufferedReader in =
          new BufferedReader( new InputStreamReader( p.getInputStream() ) );

      String line = null;

      Node n = new Node();
      n._label = func;
      _transformer.addNode( n );

      while( (line = in.readLine()) != null )
      {
        // parse output of program
        StringTokenizer tokenizer = new StringTokenizer( line, " " );

        String location = tokenizer.nextToken();
        String function = tokenizer.nextToken();
        String num = tokenizer.nextToken();
        StringBuffer buf = new StringBuffer();

        // context data may contain strings with spaces, collect all tokens and
        // put them back into one string
        while( tokenizer.hasMoreTokens() )
        {
          buf.append( tokenizer.nextToken() );
          buf.append( " " );
        }

        _logger.debug( "----------------- line output -----------" );
        _logger.debug( line );
        _logger.debug( "........................................." );

        _logger.debug( "------------- parsed:" );
        _logger.debug( "\t location: " + location );
        _logger.debug( "\t function: " + function );
        _logger.debug( "\t num: " + num );
        _logger.debug( "\t context: " + buf.toString() );

        Edge e = new Edge();
        e._source = func;
        e._target = function;
        e._label = buf.toString();

        _transformer.addEdge( e );

        produce( function );
      }

    }
    catch( IOException e )
    {
      _logger.error( e );

      throw new AnalyzerException( "Error during creation of analyzer output",
          e );
    }

  }

  public void analyze() throws IOException, AnalyzerException
  {
    // parseConfiguration();
    readConfiguration( _configFile );

    _transformer =
        OutputTransformer
            .create( _configuration.get( "cscope.transformation" ) );

    if( null == _transformer )
      throw new AnalyzerException(
          "No suitable output transformation available" );

    try
    {
      if( _configuration.containsKey( "cscope.output" ) )
      {
        _outStream =
            new BufferedOutputStream( new FileOutputStream( _configuration
                .get( "cscope.output" ) ) );
      }
    }
    catch( FileNotFoundException e1 )
    {
      _logger.error( e1 );

      throw new AnalyzerException(
          "Possibly missing input parameter cscope.output", e1 );
    }

    produce( _configuration.get( "cscope.functions" ) );
  }

  public void doOutput() throws Exception
  {
    _transformer.transform();

    if( null != _outStream )
    {
      _outStream
          .write( _transformer.getBuffer().toString().getBytes( "utf-8" ) );

      _outStream.flush();

      _outStream.close();
    }
  }

  public StringBuffer getOutput() throws Exception
  {
    return _transformer.getBuffer();
  }
}

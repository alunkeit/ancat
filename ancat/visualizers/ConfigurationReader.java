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
package ancat.visualizers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author alunkeit
 * 
 *         The configuration reader is a special purpose implementation for
 *         parameters that are specific for the rendering engine.
 * 
 *         <code>
 *      # The layout section determins which layout types o be generated
        #layout = circle, isom, kk, fr, tree
        layout = isom

        # height of the rendering area
        height = 900
        # width of the rendering area
        width = 900

        #output folder
        output = ./trunk/ancat/out/ssl/

        #node definitions
        node:font-family = Dialog
        node:font-size = 12
        node:font-style = bold
        node:default-color = #FFFFFF
        node:scaling = on
        node:shape:default = rectangle

        #edge definitions
        edge:font-family = Dialog
        edge:font-size = 10
        edge:font-style = italics
        edge:style = dashed

        #node type definition
        begin type
          target      = node
          meta-type   = function
          color       = #CCFFAA
        end type
</code>
 */
public class ConfigurationReader implements RenderingConfiguration
{
  /**
   * absolute path of the configuration file
   */
  String _absFileName;

  /**
   * A vector containing the layouts read from the configuration
   */
  Vector<String> _layouts;

  /**
   * If no parameters are given, the rendering engine will select default
   * parameters
   */
  long _height = 600;

  /**
   * If no width parameter is given, the rendering engine will be initialized
   * with a default width
   */
  long _width = 600;

  /**
   * Map containing values starting with tag 'node:'
   */
  Map<String, String> _nodeSettings = new HashMap<String, String>();

  /**
   * Map containing value starting with tag 'edge:'
   */
  Map<String, String> _edgeSettings = new HashMap<String, String>();

  /**
   * Logger object
   */
  protected static Logger _logger = Logger.getRootLogger();

  /**
   * used output folder
   */
  String _output = "./";

  Map<String, RenderingType> _types = new HashMap<String, RenderingType>();

  String _fileToken = null;

  /**
   * 
   * @param absFileName
   */
  public ConfigurationReader( String absFileName )
  {
    _absFileName = absFileName;

    _layouts = new Vector<String>();
  }

  /**
   * Parsing function parseNodeSpecific();
   */
  public void parse() throws ParserException
  {
    try
    {
      BufferedReader reader =
          new BufferedReader( new LineNumberReader( new FileReader( new File(
              _absFileName ) ) ) );

      String line = null;

      while( (line = reader.readLine()) != null )
      {
        line = line.trim();

        if( line.startsWith( "#" ) )
          continue;

        if( line.isEmpty() )
          continue;

        if( line.startsWith( "begin type" ) )
        {
          parseTypeDefinition( reader );
          continue;
        }

        StringTokenizer tokenizer = new StringTokenizer( line, "=" );

        if( tokenizer.countTokens() > 2 )
        {
          continue;
        }

        String key = tokenizer.nextToken().trim();
        String token = tokenizer.nextToken().trim();

        _logger.debug( "key := " + key + "\n\t token: " + token );

        // parse the layout section
        if( key.equalsIgnoreCase( "layout" ) )
        {
          StringTokenizer tokenizer2 = new StringTokenizer( token, "," );

          while( tokenizer2.hasMoreTokens() )
          {
            String s = tokenizer2.nextToken();
            _logger.debug( "adding to set := " + s );
            _layouts.add( s.trim() );
          }
        }
        else if( key.equalsIgnoreCase( "width" ) )
        {
          _width = Long.parseLong( token );
        }
        else if( key.equalsIgnoreCase( "height" ) )
        {
          _height = Long.parseLong( token );
        }
        else if( key.equals( "output" ) )
        {
          _output = token;
        }
        else if( key.equals( "file" ) )
        {
          _fileToken = token;
        }
        else if( key.startsWith( "node:" ) )
        {
          _nodeSettings.put( key, token );
        }
        else if( key.startsWith( "edge:" ) )
        {
          _edgeSettings.put( key, token );
        }
      }

      reader.close();
    }
    catch( IOException e )
    {
      _logger.error( e.getMessage() );

      throw new ParserException( e );
    }
  }

  public Vector<String> getLayouts()
  {
    return _layouts;
  }

  public long height()
  {
    return _height;
  }

  public long width()
  {
    return _width;
  }

  public String getOutputFolder()
  {
    return _output;
  }

  public Map<String, String> nodeConfiguration()
  {
    return _nodeSettings;
  }

  public Map<String, String> edgeConfiguration()
  {
    return _edgeSettings;
  }

  private void parseTypeDefinition( BufferedReader reader ) throws IOException
  {
    String line;

    RenderingType type = new RenderingType();

    while( (line = reader.readLine()) != null )
    {
      line = line.trim();

      if( line.startsWith( "#" ) || line.isEmpty() )
        continue;

      if( line.startsWith( "end type" ) )
      {
        _types.put( type._metatype, type );
        return;
      }

      StringTokenizer tokenizer = new StringTokenizer( line, "=" );

      if( tokenizer.countTokens() != 2 )
        continue;

      String k = tokenizer.nextToken().trim();
      String v = tokenizer.nextToken().trim();

      if( k.equalsIgnoreCase( "target" ) )
        type._target = v;
      else if( k.equalsIgnoreCase( "meta-type" ) )
        type._metatype = v;
      else if( k.equalsIgnoreCase( "color" ) )
        type._color = v;
      else if( k.equalsIgnoreCase( "pcolor" ) )
        type._pcolor = v;
    }
  }

  public Map<String, RenderingType> getTypes()
  {
    return _types;
  }

  public String getFileToken()
  {
    return _fileToken;
  }

  /**
   * @param args
   */
  public static void main( String[] args )
  {
    try
    {
      try
      {
        PatternLayout appenderLayout = new PatternLayout();
        appenderLayout.setConversionPattern( "%d %p - %m%n" );
        ConsoleAppender console = new ConsoleAppender( appenderLayout );
        _logger.addAppender( console );
        _logger.setLevel( org.apache.log4j.Level.ALL );
      }
      catch( Exception e )
      {
      }

      ConfigurationReader reader = new ConfigurationReader( "directed.conf" );

      reader.parse();

      System.out.println( "height: " + reader.height() );
      System.out.println( "width : " + reader.width() );
      System.out.println( "output: " + reader.getOutputFolder() );

    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }
}

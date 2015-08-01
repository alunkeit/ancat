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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Vector;

/**
 * @author alunkeit
 * 
 */
public class GCCPluginAnalyzer extends Analyzer
{

  /**
   * Configuration file
   */
  protected String _file;

  /**
   * Current parsed line
   */
  protected String _tokenLine;

  /**
   * OutputTransformer instance
   */
  protected GraphMLTransformer _transformer;

  /**
   * Flag hint for recursive directory scan
   */
  private boolean _recursive = false;

  /**
   * Vector containing files to be processed
   */
  private Vector<FileObject> _files = new Vector<FileObject>();

  /**
   * 
   * @author alunkeit
   * 
   *         Internal helper class used to store results of recursive search for
   *         input files.
   */
  private static class FileObject
  {
    String _absPath;

    String _relPath;

    String _fileName;
  }

  /**
   * Constructor
   */
  public GCCPluginAnalyzer()
  {
    _transformer = new GraphMLTransformer();
  }

  @Override
  public void init( String file )
  {
    _file = file;

    // read the configuration file
    try
    {
      readConfiguration( _file );
    }
    catch( IOException e )
    {
      _logger.error( e );
      return;
    }

    // find out about the intended configuration
    if( _configuration.containsKey( "recursive" ) )
    {
      if( _configuration.get( "recursive" ).equalsIgnoreCase( "yes" ) )
        _recursive = true;
    }
  }

  @Override
  public void doOutput() throws Exception
  {
    flush();
  }

  @Override
  public StringBuffer getOutput() throws Exception
  {
    return _transformer.getBuffer();
  }

  /**
   * A function for transformation of the input data to our internal
   * representation. The input of this function is a dependency file generated
   * by codeviz patched gcc compilers. The internal graph representation of
   * ancat utilizes GraphML style files. This function performs the required
   * transformation.
   * 
   * @param file
   *          - The FileObject containing all data for processing
   * @param storeTmpResult
   *          - boolean flag indicating if the temporary transformation result
   *          shall be stored
   * @throws IOException
   */
  private void transformFile( FileObject file, boolean storeTmpResult ) throws IOException
  {

    _logger.info( "processing input file: " + file._absPath );

    BufferedReader in =
        new BufferedReader( new LineNumberReader( new FileReader( new File(
            file._absPath ) ) ) );

    String line = null;

    // check if an additional GraphMLTransformer shall be used.
    GraphMLTransformer tmp =
        storeTmpResult == true ? new GraphMLTransformer() : null;

    while( (line = in.readLine()) != null )
    {
      if( line.startsWith( "F" ) )
        parseFunctionLine( line, tmp );
      else if( line.startsWith( "C" ) )
        parseCallLine( line, tmp );
    }

    if( tmp != null )
    {
      try
      {
        flush( _configuration.get( "outPath" ), file._fileName + ".xml", tmp );
      }
      catch( Exception e )
      {
        _logger.error( e );
      }
    }

    in.close();
  }

  /**
   * Processes input files and starts conversion procedure from codeviz
   * dependency files to GraphML files.
   * 
   * @throws IOException
   * @throws TransformationError
   */
  private void processFiles() throws IOException, TransformationError
  {
    String current = null;

    if( !_recursive )
    {
      current = _configuration.get( "firstFile" );

      try
      {
        FileObject o = new FileObject();
        o._fileName = current;
        o._absPath = _configuration.get( "basePath" );

        transformFile( o, false );
      }
      catch( IOException e )
      {
        _logger.error( e );
      }
    }
    else
    {
      // find all files starting at basePath
      findFiles( _configuration.get( "basePath" ) );

      for( FileObject o : _files )
      {
        // merge all data into one big file so the program is able to check all
        // dependencies
        _logger.info( "starting xml transformation" );

        transformFile( o, true );
      }

    }

    flush();
  }

  /**
   * Flush out the network into a graphml represenation.
   * 
   * @throws TransformationError
   * 
   */
  private void flush() throws TransformationError
  {
    flush( _configuration.get( "outPath" ), _configuration.get( "outFile" ),
        _transformer );
  }

  /**
   * 
   * @param path
   * @param fileName
   * @param transformer
   * @throws TransformationError
   */
  private void flush( String path, String fileName, OutputTransformer transformer ) throws TransformationError
  {
    transformer.transform();

    try
    {
      FileOutputStream out = new FileOutputStream( new File( path + fileName ) );

      out.write( transformer.getBuffer().toString().getBytes( "utf-8" ) );
      out.flush();
      out.close();
    }
    catch( Exception exc )
    {
      _logger.error( exc );
    }
  }

  /**
   * Recursive search scan for dependency files
   * 
   * @param path
   *          - Absolute path to start the search from
   */
  private void findFiles( String path )
  {
    File[] files = new File( path ).listFiles();

    if( null == files )
      return;

    for( File f : files )
    {
      if( f.isDirectory() )
      {
        if( f.getPath().equals( "." ) || f.getPath().equals( ".." ) )
        {
          _logger.debug( "ignoring path" );
          continue;
        }
        else
        {
          _logger.debug( "checking path: " + f.getAbsolutePath() );
          findFiles( f.getAbsolutePath() );
        }
      }
      else
      {
        if( f.getAbsolutePath().endsWith( _configuration.get( "extension" ) ) )
        {
          String depFile = f.getAbsolutePath();
          String subPath =
              f.getAbsolutePath().substring(
                  _configuration.get( "basePath" ).length() );

          String fileName = f.getName();

          _logger.debug( "path separator: " + File.separatorChar );

          int index = subPath.lastIndexOf( File.separatorChar );

          if( index > 0 )
            subPath = subPath.substring( 0, index );

          _logger.debug( "dep file: " + depFile );
          _logger.debug( "relative path: " + subPath );

          FileObject o = new FileObject();
          o._fileName = fileName;
          o._relPath = subPath;
          o._absPath = depFile;

          _files.add( o );
        }
      }
    }
  }

  @Override
  public void analyze() throws IOException,
      AnalyzerException,
      TransformationError
  {
    processFiles();
  }

  /**
   * Parses a function line of the input file
   * 
   * @param line
   */
  protected void parseFunctionLine( String line, OutputTransformer transformer )
  {
    _tokenLine = line.substring( 2 );

    String s1 = findToken();
    String s2 = findToken();
  }

  /**
   * Searches for a Token Line starting with a opening bracket and ending by a
   * closing bracket
   * 
   * @return
   */
  private String findToken()
  {
    int i1 = _tokenLine.indexOf( '{' );
    int i2 = _tokenLine.indexOf( '}' );

    String s1 = _tokenLine.substring( i1 + 1, i2 ).trim();

    _tokenLine = _tokenLine.substring( i2 + 1 ).trim();

    return s1;
  }

  /**
   * Parse a call line of the input file
   * 
   * @param line
   */
  protected void parseCallLine( String line, OutputTransformer transformer )
  {
    _tokenLine = line.substring( 2 );

    String s1 = findToken();
    String s2 = findToken();
    String s3 = findToken();

    Node src = new Node();
    src._type = "function";
    src._label = s1;

    Node trgt = new Node();
    trgt._type = "function";
    trgt._label = s3;

    Edge e = new Edge();

    e._label = s1 + " - " + s3;
    e._source = s1;
    e._target = s3;

    _transformer.addNode( src );
    _transformer.addNode( trgt );
    _transformer.addEdge( e );

    if( null != transformer )
    {
      transformer.addNode( src );
      transformer.addNode( trgt );
      transformer.addEdge( e );
    }
  }
}

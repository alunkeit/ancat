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
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import ancat.analyzer.Analyzer;
import ancat.analyzer.AnalyzerException;
import ancat.common.Edge;
import ancat.common.GraphInspector;
import ancat.common.Vertex;
import ancat.configuration.ConfigurationParser;
import ancat.importer.config.Inspector;
import ancat.importer.config.Inspectors;

/**
 * @author a.lunkeit
 * 
 */
public class ConsoleModelChecker
{

  /**
   * The name of the model file
   */
  // private String _modelFile;
  protected List<String> _modelFiles = new LinkedList<String>();

  /**
   * The folder to be used for output (files etc.)
   */
  private String _outFolder;

  /**
   * The array containing the parsed names of the inspectors
   */
  private Vector<String> _inspectors;

  /**
   * The array containing the objects with the inspector implementations
   */
  private LinkedList<GraphInspector<Vertex, Edge>> _inspectorObjects;

  /**
   * Logger object
   */
  protected static Logger _logger = Logger.getRootLogger();

  /**
   * Parsed XML representation from input file. This representation is required
   * to keep the information which inspector is required to process the output
   * of another inspector.
   */
  private Inspectors _inspectorData;

  private LinkedList<Analyzer> analyzers = new LinkedList<Analyzer>();

  static
  {
    try
    {
      // Initialize the logger only in case that no appender is installed.
      if( _logger.getAllAppenders().hasMoreElements() == false )
      {

        PatternLayout appenderLayout = new PatternLayout();
        appenderLayout.setConversionPattern( "%d %p - %m%n" );
        ConsoleAppender console = new ConsoleAppender( appenderLayout );
        _logger.addAppender( console );
        _logger.setLevel( org.apache.log4j.Level.ALL );
      }
    }
    catch( Exception e )
    {
    }
  }

  /**
   * Class constructor
   */
  protected ConsoleModelChecker()
  {
    _inspectorObjects = new LinkedList<GraphInspector<Vertex, Edge>>();

    _inspectors = new Vector<String>();
  }

  /**
   * Prepare the options processed from the command line
   * 
   * @return an array of Options
   */
  protected Options prepareOptions()
  {
    Options options = new Options();

    Option fileOption =
        new Option( "f", true, "File containing the graph description" );
    fileOption.setRequired( false );
    options.addOption( fileOption );

    Option inspectorOption =
        new Option( "i", true, "Inspector to be used when analyzing the model" );
    inspectorOption.setRequired( false );
    options.addOption( inspectorOption );

    Option configurationOption =
        new Option( "c", true,
            "use xml based configuration file instead of -f and -i parameters" );
    configurationOption.setRequired( false );
    options.addOption( configurationOption );

    Option config2Option =
        new Option( "x", true, "configuration with new config file format" );
    config2Option.setRequired( false );
    options.addOption( config2Option );

    return options;
  }

  /**
   * Evaluates the argument set on the command line.
   * 
   * @param args
   */
  public void evaluateCommandLine( String[] args ) throws ParameterException
  {

    try
    {
      Options o = this.prepareOptions();

      CommandLineParser parser = new PosixParser();
      CommandLine cmd = parser.parse( o, args );

      // at first check, if an xml based configuration file has been specified
      /*
       * String xml = cmd.getOptionValue( 'c' );
       * 
       * if( null != xml && !xml.isEmpty() ) { JAXBContext jc =
       * JAXBContext.newInstance( "ancat.importer.config" );
       * 
       * if( jc == null ) throw new ParameterException(
       * "Unable to set up JAXBContext of type ancat.importer.config" );
       * 
       * Unmarshaller unmarshaller = jc.createUnmarshaller();
       * 
       * if( unmarshaller == null ) throw new ParameterException(
       * "Unable to create unmarshaller object from context" );
       * 
       * Configuration conf = (Configuration) unmarshaller.unmarshal( new File(
       * xml ) );
       * 
       * if( conf == null ) throw new ParameterException(
       * "No configuration object created" );
       * 
       * Model model = conf.getModel();
       * 
       * if( model == null ) throw new ParameterException(
       * "model parameter not found in configuration file" );
       * 
       * // since now the output node is treated as an optional element Output
       * out = conf.getOutput();
       * 
       * // if( out == null ) // throw new ParameterException(
       * "no output from configuration file" );
       * 
       * _inspectorData = conf.getInspectors();
       * 
       * if( _inspectorData == null ) throw new ParameterException(
       * "no inspectors specified (working with empty inspector specifiction " +
       * "makes no sense, specify at least one!" );
       * 
       * _modelFile = model.getFilename();
       * 
       * if( _modelFile == null ) throw new ParameterException(
       * "no model file given, specify a valid model file in the configuration"
       * );
       * 
       * if( null != _outFolder ) _outFolder = out.getOutputfolder();
       * 
       * // if( _outFolder == null ) // throw new ParameterException( //
       * "no output folder found, specify a valid one");
       * 
       * _logger.debug( "----------- configuration data ---------" );
       * _logger.debug( "model file: \t" + _modelFile ); _logger .debug(
       * "output folder: \t" + (_outFolder != null ? _outFolder :
       * "<empty path>") ); _logger.debug(
       * "----------- end configuration data -----" );
       * 
       * } else {
       */

      /**
       * Here the new version of configuration files is processed
       */
      if( cmd.getOptionValue( "x" ) != null )
      {
        String configFile = cmd.getOptionValue( "x" );

        ConfigurationParser p = new ConfigurationParser( configFile );

        p.load();

        if( p.simpleItems().containsKey( "conf.input" ) )
        {
          _modelFiles.add( p.simpleItems().get( "conf.input" ) );
        }
        else if( p.simpleItems().containsKey( "conf.inputdir" ) )
        {
          // read all files from directory
          File dir = new File( p.simpleItems().get( "conf.inputdir" ) );

          if( !dir.isDirectory() || !dir.exists() )
          {
            _logger
                .error( "Invalid directory entry in configuration, returning" );
            return;
          }

          File f[] = dir.listFiles();

          for( File file : f )
          {
            if( file.isDirectory() )
              continue;

            if( file.getName().startsWith( "." ) )
              continue;

            _modelFiles.add( file.getAbsolutePath() );
            _logger.info( "adding file: " + file.getName() );
          }
        }

        for( ConfigurationParser.ConfigurationItem i : p.complexItems() )
        {
          if( i._type.equalsIgnoreCase( "inspector" ) )
          {
            _logger.debug( "adding implementation: " + i._items
                .get( "implementation" ) );

            _inspectors.add( i._items.get( "implementation" ) );

            Class<?> c = Class.forName( i._items.get( "implementation" ) );

            Object object = c.getConstructor().newInstance();

            @SuppressWarnings("unchecked")
            GraphInspector<Vertex, Edge> inspector =
                (GraphInspector<Vertex, Edge>) (object);

            inspector.setProperties( i._items );

            _inspectorObjects.add( inspector );
          }
          else if( i._type.equalsIgnoreCase( "analyzer" ) )
          {
            _logger
                .debug( "adding analyzer" + i._items.get( "implementation" ) );

            Class<?> c = Class.forName( i._items.get( "implementation" ) );

            Analyzer a = (Analyzer) c.getConstructor().newInstance();

            a.init( i._items.get( "configuration" ) );

            analyzers.add( a );
          }
        }
      }
      // no longer supported
      /*
       * else { _modelFile = cmd.getOptionValue( 'f' ); String _inspectorOption
       * = cmd.getOptionValue( 'i' );
       * 
       * StringTokenizer tokenizer = new StringTokenizer( _inspectorOption, ";"
       * );
       * 
       * while( tokenizer.hasMoreTokens() ) { String token =
       * tokenizer.nextToken(); _logger.debug( "Inspector class: " + token );
       * 
       * _inspectors.add( token ); } }
       */
      // }
    }
    catch( Exception e )
    {
      _logger.error( e.getMessage(), e );
      throw new ParameterException( e.getMessage() );
    }
  }

  /**
   * Loads the inspectors and stores them in an internal array. Inspectors that
   * cannot be instantiated are not loaded into the internal representation.
   */
  public void prepareInspectors() throws InstantiationException
  {
    if( _inspectorObjects.size() > 0 )
    {
      _logger.debug( "inspectors are already initialized, function returns" );
      return;
    }

    if( null != _inspectorData )
    {
      Iterator<Inspector> iter = _inspectorData.getInspector().iterator();

      try
      {
        while( iter.hasNext() )
        {
          Inspector i = iter.next();
          String implementation = i.getImplementation();

          Class<?> c = Class.forName( implementation );

          Object o = c.getConstructor().newInstance();

          if( o != null )
          {
            _logger.debug( "New object of type " + implementation + "created!" );
          }
          else
          {
            _logger.error( "Object creation failed, type := " + implementation );
            throw new InstantiationException(
                "Object creation failed, type := " + implementation );
          }

          @SuppressWarnings("unchecked")
          GraphInspector<Vertex, Edge> io = (GraphInspector<Vertex, Edge>) o;

          /**
           * The output of an inspector might be foreseen to be used by another
           * inspector Therefore the 'use output' option is introduced.
           */
          if( null != i.getUseoutput() )
          {
            Inspector ii = (Inspector) i.getUseoutput();
            io.setIdRef( ii.getId() );
          }

          io.setId( i.getId() );
          io.setInspectorConfiguration( i );

          _inspectorObjects.add( io );
        }
      }
      catch( Exception cnfe )
      {
        _logger.error( "Error: ", cnfe );
      }

    }
    else
    {
      try
      {
        for( String inspector : _inspectors )
        {
          Class<?> c = Class.forName( inspector );

          Object o = c.getConstructor().newInstance();

          if( o != null )
            _logger.debug( "New object created!" );
          else
            _logger.error( "Object creation failed!" );

          @SuppressWarnings("unchecked")
          GraphInspector<Vertex, Edge> i = (GraphInspector<Vertex, Edge>) o;

          _inspectorObjects.add( i );
        }
      }
      catch( Exception e )
      {
        _logger.error( "Error: ", e );
      }
    }
  }

  /**
   * Returns the name of the model file read from the command line
   * 
   * @return
   */
  protected List<String> getModelFiles()
  {
    return _modelFiles;
  }

  public LinkedList<GraphInspector<Vertex, Edge>> getInspectors()
  {
    return _inspectorObjects;
  }

  /**
   * Creates a table with configuration data. Each time the function is called a
   * new object is created.
   * 
   * @return HashTable containing configuration data. Supported Keys: modelFile
   *         - The file containing the model description (the graph file)
   *         outFolder - The output folder inspectors - A Vector<String> object
   *         containing the names of the requested inspectors inspectorsRaw -
   *         Parsed XML configuration data inspectorObjects - The created
   *         inspector objects
   */
  protected Hashtable<String, Object> getConfiguration()
  {
    Hashtable<String, Object> configuration = new Hashtable<String, Object>();

    configuration.put( "modelFile", _modelFiles );

    if( null != _outFolder )
      configuration.put( "outFolder", _outFolder );

    if( null != _inspectors )
      configuration.put( "inspectors", _inspectors );

    if( null != _inspectorData )
      configuration.put( "inspectorsRaw", _inspectorData );

    if( null != _inspectorObjects )
      configuration.put( "inspectorObjects", _inspectorObjects );

    return configuration;
  }

  public void runAnalyzers()
  {
    for( Analyzer a : analyzers )
    {
      try
      {
        _logger.debug( "------------>running analyzer for building data basis" );
        a.analyze();
        a.doOutput();
        _logger.debug( "------------> finished" );
      }
      catch( IOException e )
      {
        _logger.error( e );
      }
      catch( AnalyzerException e )
      {
        _logger.error( e );
      }
      catch( Exception e )
      {
        _logger.error( e );
      }
    }
  }
}

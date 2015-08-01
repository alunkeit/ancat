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

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import ancat.common.Edge;
import ancat.common.GraphInspector;
import ancat.common.InspectionOutputConsumer;
import ancat.common.Unspecified;
import ancat.common.Vertex;
import ancat.console.StaticDirectedModel;
import edu.uci.ics.jung.algorithms.filters.Filter;
import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter;
import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter.EdgeType;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 *         MainFrame is the main frame window of the ancat ui. It combines all
 *         graphic elements into views.
 * 
 * @TODO - use firePropertyChange method instead of own interfaces and listeners
 *       - do better state handling of the ui - provide a generic text window to
 *       be used for analyzers which produce text output and no graphical output
 *       - provide possibilities to export image - install own renderers in
 *       GraphView
 */
public class MainFrame extends JFrame implements ActionListener, ChangeListener, InspectionOutputConsumer
{
  /**
   * 
   */
  private static final long serialVersionUID = 5784387529927937778L;

  private Map<String, String> _params = new Hashtable<String, String>();

  private JScrollPane _leftPanel;

  private JPanel _rightPanel;

  private JScrollPane _bottomPanel;

  private JPanel _backPanel;

  private JPanel _rTopPanel;

  private JPanel _rBtmPanel;

  private JTabbedPane _topTabs;

  private JTabbedPane _btmTabs;

  private JPanel _statusBar;

  private Map<String, GraphView> _views = new HashMap<String, GraphView>();

  private GraphView _activeView = null;

  private Map<String, GraphSettingsPanel> _settingsPanels =
      new HashMap<String, GraphSettingsPanel>();

  private GraphSettingsPanel _activeSettingsPanel = new GraphSettingsPanel();

  private NetworkTree _networkTree = new NetworkTree( this );

  private Logger _logger = Logger.getRootLogger();

  private Logger _output = Logger.getLogger( "output" );

  private File _activeConfig = null;

  private JTextArea _console = new JTextArea();

  private JTextArea _debugConsole = new JTextArea();

  private String _modelFile;

  protected Integer asInt( String key )
  {
    return Integer.parseInt( _params.get( key ) );
  }

  protected Boolean asBool( String key )
  {
    return Boolean.parseBoolean( key );
  }

  protected MainFrame()
  {
    super();

    _logger.addAppender( new TextConsoleAppender( _debugConsole ) );
    _output.addAppender( new TextConsoleAppender( _console ) );

    BufferedImage image = null;

    try
    {
      image = ImageIO.read( getClass().getResource( "/img/logo.jpg" ) );

      if( image != null )
        _logger.debug( "image loaded" );

      setIconImage( image );

      TrayIcon ti = new TrayIcon( image );
      ti.setToolTip( "ancat" );

      SystemTray.getSystemTray().add( ti );
    }
    catch( IOException e )
    {
      _logger.error( e );
    }
    catch( AWTException e )
    {
      _logger.error( e );
    }
  }

  /**
   * Initialize components of the MainFrame
   * 
   * @param params
   */
  protected void initComponents( Map<String, String> params )
  {
    _params.putAll( params );

    this.setSize( asInt( "width" ), asInt( "height" ) );
    this.setAlwaysOnTop( asBool( "onTop" ) );
    this.setTitle( _params.get( "title" ) );

    this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    // set the back Panel
    _backPanel = new JPanel();
    _backPanel.setLayout( new BorderLayout() );

    // initialize graph properties panel
    _activeSettingsPanel = new GraphSettingsPanel();
    _activeSettingsPanel.setLayout( new BorderLayout() );

    // configuration of menu
    configMenu();
    configPanes();
    configStatusBar();
    initNetworkTree();

    // for test
    insertTestData();

    this.setLayout( new BorderLayout() );
    this.add( _backPanel, BorderLayout.CENTER );

    Set<String> vKeys = _views.keySet();

    for( String key : vKeys )
    {
      _views.get( key ).activate();
    }
  }

  /**
   * Insert Test Data
   */
  private void insertTestData()
  {
    Graph<Vertex, Edge> graph = new DirectedSparseMultigraph<Vertex, Edge>();

    for( int i = 0; i < 100; i++ )
    {
      Unspecified u = new Unspecified( "" + i );
      u.setProperty( "filename", "label: " + i );
      graph.addVertex( u );
    }

    addGraphView( "Sample Graph", graph );
  }

  private void configMenu()
  {
    JMenuBar bar = new JMenuBar();

    JMenu file = new JMenu( "File" );
    JMenu config = new JMenu( "Configuration" );
    JMenu run = new JMenu( "Run" );
    JMenu help = new JMenu( "Help" );

    // Items of File menu
    JMenuItem iImport = new JMenuItem( "Import" );
    JMenuItem iExit = new JMenuItem( "Exit" );

    iImport.setActionCommand( "IMPORT" );
    iExit.setActionCommand( "EXIT" );
    iImport.addActionListener( this );
    iExit.addActionListener( this );

    // JMenuItem iConfig = new JMenuItem( "Configuration" );
    // iConfig.setActionCommand( "CONFIGURATION" );
    // iConfig.addActionListener( this );

    JMenuItem iExec = new JMenuItem( "Execute" );
    iExec.setActionCommand( "EXECUTE" );
    iExec.addActionListener( this );

    JMenuItem iExtractSubGraph = new JMenuItem( "Extract Subgraph" );
    iExtractSubGraph.setActionCommand( "EXTRACT-SUB" );
    iExtractSubGraph.addActionListener( this );

    JMenuItem iIndependentPaths = new JMenuItem( "Independent Paths" );
    iIndependentPaths.setActionCommand( "INDEPENDENT-PATHS" );
    iIndependentPaths.addActionListener( this );

    JMenuItem iInfo = new JMenuItem( "Info" );
    iInfo.setActionCommand( "INFO" );
    iInfo.addActionListener( this );

    file.add( iImport );
    file.add( iExit );

    // config.add( iConfig );

    run.add( iExec );
    run.addSeparator();
    run.add( iExtractSubGraph );
    run.add( iIndependentPaths );

    help.add( iInfo );

    bar.add( file );
    bar.add( config );
    bar.add( run );
    bar.add( help );

    this.setJMenuBar( bar );
  }

  private void configPanes()
  {
    _leftPanel = new JScrollPane( _networkTree );
    _rightPanel = new JPanel();
    _bottomPanel = new JScrollPane();

    _leftPanel.setLayout( new ScrollPaneLayout() );
    _rightPanel.setLayout( new BorderLayout() );
    _bottomPanel.setLayout( new ScrollPaneLayout() );

    _leftPanel.setName( "left panel" );
    _rightPanel.setName( "right panel" );
    _bottomPanel.setName( "bottom panel" );

    _rTopPanel = new JPanel();
    _rBtmPanel = new JPanel();

    _rTopPanel.setLayout( new BorderLayout() );
    _rBtmPanel.setLayout( new BorderLayout() );

    _rTopPanel.setBackground( Color.WHITE );
    _rBtmPanel.setBackground( Color.WHITE );

    _topTabs = new JTabbedPane();
    _topTabs.addChangeListener( this );
    // _topTabs.a

    _btmTabs = new JTabbedPane();
    _btmTabs.add( "Console", new JScrollPane( _console ) );
    _btmTabs.add( "Debug", new JScrollPane( _debugConsole ) );
    // _btmTabs.add( "Graph Settings", new JScrollPane( _propPanel ) );
    _btmTabs.add( "Graph Settings", _activeSettingsPanel );

    _rTopPanel.add( _topTabs, BorderLayout.CENTER );
    _rBtmPanel.add( _btmTabs, BorderLayout.CENTER );

    JSplitPane verticalPane =
        new JSplitPane( JSplitPane.VERTICAL_SPLIT, _rTopPanel, _rBtmPanel );

    verticalPane.setDividerLocation( 600 );
    verticalPane.setDividerSize( 10 );

    _rightPanel.add( verticalPane, BorderLayout.CENTER );

    JSplitPane horizontalPane =
        new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, _leftPanel, _rightPanel );

    horizontalPane.setDividerLocation( 300 );
    horizontalPane.setDividerSize( 10 );

    _backPanel.add( horizontalPane, BorderLayout.CENTER );
  }

  private void configStatusBar()
  {
    _statusBar = new JPanel();
    _statusBar.setLayout( new GridLayout( 1, 3 ) );
    _statusBar.add( new JLabel( "ancat ui 0.0.1" ) );
    _backPanel.add( _statusBar, BorderLayout.SOUTH );
  }

  /**
   * Creates a new graph view control and adds this control to the tabbed pane
   * on the right upper side
   * 
   * @param name
   *          - The name of the graph view, must be unique
   * @param g
   *          - The graph object
   */
  protected void addGraphView( String name, Graph g )
  {
    _logger
        .debug( "addGraphView, name := " + name + " graph := vertex count -> " + g
            .getVertexCount() );

    GraphView view = new GraphView();
    view.setLayout( new BorderLayout() );
    view.setGraph( g );
    view.setName( name );
    _views.put( name, view );

    JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.setBackground( Color.blue );
    panel.add( view );

    _topTabs.addTab( name, null, panel, BorderLayout.CENTER );

    synchronized( _activeView )
    {
      _activeView = view;
    }

    if( isShowing() )
    {
      view.activate();
      _topTabs.updateUI();
    }
  }

  /**
   * Initializes graph property panel and makes an indication to the graph view
   * that it will be displayed soon.
   * 
   * @param view
   * @param graph
   */
  private void initGraphPropertiesPanel( String view, Graph graph )
  {
    /**
     * Here we handle the situation that more than one graph is displayed in the
     * graph displaying area and more than one graph properties view is
     * required. The instances of GraphSettingsPanel are stored in a hash map,
     * we check if we already have one. Otherwise a new one is created and
     * displayed.
     */
    if( false == _activeSettingsPanel.equals( _settingsPanels.get( view ) ) )
    {

      boolean showing = false;

      if( _activeSettingsPanel.isShowing() )
        showing = true;

      if( _settingsPanels.containsKey( view ) )
      {
        _btmTabs.remove( _activeSettingsPanel );

        _activeSettingsPanel = _settingsPanels.get( view );

        _btmTabs.add( "Graph Settings", _activeSettingsPanel );
      }
      else
      {
        _btmTabs.remove( _activeSettingsPanel );
        GraphSettingsPanel panel = new GraphSettingsPanel();
        panel.setLayout( new BorderLayout() );
        _activeSettingsPanel = panel;
        _btmTabs.add( "Graph Settings", _activeSettingsPanel );

        _settingsPanels.put( view, panel );
      }

      if( true == showing )
        _btmTabs.setSelectedComponent( _activeSettingsPanel );
    }

    if( null != graph )
      _activeSettingsPanel.activate( _views.get( view ), graph );
    else
      _activeSettingsPanel.activate( _views.get( view ), _views.get( view )
          .getGraph() );

    // tell the graph view component that it will be displayed soon.
    _views.get( view ).beforeDisplay();

    _activeView = _views.get( view );

    if( _activeSettingsPanel.isShowing() )
      _activeSettingsPanel.updateView();

  }

  /**
   * Initializes the network tree view
   */
  private void initNetworkTree()
  {
    _networkTree.activate();
  }

  @Override
  public void actionPerformed( ActionEvent e )
  {
    if( e.getActionCommand().equals( "IMPORT" ) )
      importNetwork();
    else if( e.getActionCommand().equals( "EXIT" ) )
      exitApplication();
    else if( e.getActionCommand().equals( "EXECUTE" ) )
      execAnalysis();
    else if( e.getActionCommand().equals( "INFO" ) )
      showInfo();
    else if( e.getActionCommand().equals( "CONFIGURATION" ) )
      showConfiguration();
    else if( e.getActionCommand().equals( "EXTRACT-SUB" ) )
      extractSubGraph();
    else if( e.getActionCommand().equals( "INDEPENDENT-PATHS" ) )
      findIndependentPaths();
  }

  /**
   * Imports a project file that may contain more than one network definitions.
   */
  private void importNetwork()
  {
    JFileChooser fc = new JFileChooser();

    fc.setCurrentDirectory( new File( System.getProperty( "user.dir" ) ) );

    if( JFileChooser.APPROVE_OPTION == fc.showOpenDialog( this ) )
    {
      _activeConfig = fc.getSelectedFile();

      _logger.debug( "selected file: " + _activeConfig.getAbsolutePath() );

      _networkTree.setNetworkConfiguration( _activeConfig );
    }
  }

  private void exitApplication()
  {
    _logger.debug( "exit option" );

    System.exit( 0 );
  }

  /**
   * Starts the execution of an analysis. Prerequisite is the loading of an
   * active configuration.
   */
  private void execAnalysis()
  {
    _logger.debug( "exec analysis" );

    if( _activeConfig == null )
    {
      _logger
          .debug( "No active configuration chosen, select network definition first" );
      return;
    }

    Thread t = new AnalyzerThread( this );
    t.start();
  }

  /**
   * 
   * @author alunkeit
   * 
   *         A helper class for performing the analysis tasks. Analysis is done
   *         in a separate thread so the UI can still be operated. Currently
   *         this is not a real background task but the UI is not blocking using
   *         this solution.
   * 
   */
  class AnalyzerThread extends Thread
  {
    private MainFrame _frame;

    private StaticDirectedModel _driver = new StaticDirectedModel();

    public AnalyzerThread( MainFrame frame )
    {
      _frame = frame;
    }

    public void run()
    {
      try
      {
        // Create a new driver
        _driver = new StaticDirectedModel();
        // Evaluate the command line presented to the driver
        _driver.evaluateCommandLine( new String[] { "-x" + _activeConfig
            .getAbsolutePath() } );
        // do the analysis
        _driver.runAnalyzers();
        // Prepare the inspectors to be used on the load graph model
        _driver.prepareInspectors();

        // install consumers for output result
        for( GraphInspector<Vertex, Edge> inspector : _driver.getInspectors() )
        {
          inspector.addOutputConsumer( _frame );
        }

        _driver.analyze();
      }
      catch( Exception exc )
      {
        _logger.error( exc );
      }
    }
  }

  private void showInfo()
  {
    _logger.debug( "show info" );
  }

  private void showConfiguration()
  {
    _logger.debug( "show configuration" );
  }

  /**
   * Intended to extract a sub graph beginning at a given vertex V
   * 
   * TODO: Current implementation is a quick hack for checking if the approach
   * works. Make analysis of neighborhood more comfortable possibly by
   * introduction of analysis Plugins.
   */
  private void extractSubGraph()
  {
    Set<Vertex> selected = _activeView.checkSelectedVertex();

    if( null == selected || selected.size() == 0 )
    {
      _output.info( "no vertex selected. unable to perform operation" );
    }
    else
    {
      int k = 3; // maximum hops
      Vertex startVertex = selected.iterator().next();

      Filter<Vertex, Edge> filter =
          new KNeighborhoodFilter<Vertex, Edge>( startVertex, k,
              EdgeType.IN_OUT );
      Graph<Vertex, Edge> neighborhood =
          filter.transform( _activeView.getGraph() );

      Collection<Vertex> neighbors = neighborhood.getVertices();

      _output.info( "---------------------------------------------" );
      _output.info( "neighbor analysis, depth = 3" );
      _output.info( "---------------------------------------------" );

      for( Vertex v : neighbors )
      {
        _output.info( "neighbor vertex: \n\t" + v.toString() );
      }

      addGraphView( "subgraph-" + _activeView.getName() + " " + new Date(),
          neighborhood );

    }
  }

  /**
   * Find all independent execution paths in the network beginning at a given
   * vertex V
   */
  private void findIndependentPaths()
  {
    // create a new analyzer run based on bfs and consume the results
  }

  /**
   * 
   * @author alunkeit
   * 
   *         Internal helper class for putting output from the logger into the
   *         console view
   */
  private static class TextConsoleAppender extends AppenderSkeleton
  {
    private JTextArea _area;

    TextConsoleAppender( JTextArea area )
    {
      _area = area;
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean requiresLayout()
    {
      return false;
    }

    @Override
    protected void append( LoggingEvent e )
    {
      _area.append( (String) e.getRenderedMessage() + "\n" );
    }

  }

  @Override
  public void stateChanged( ChangeEvent e )
  {
    if( e.getSource().equals( _topTabs ) )
    {
      int index = _topTabs.getSelectedIndex();

      String title = _topTabs.getTitleAt( index );

      initGraphPropertiesPanel( title, null );
    }

  }

  /**
   * The MainFrame implementation acts as a consumer of analysis results. The
   * method consume is called by the analysis task and the MainFrame
   * implementation decides how to render the results.
   * 
   * @param inspector
   */
  @Override
  public void consume( GraphInspector<Vertex, Edge> inspector )
  {
    _logger.debug( "MainFrame::consume" );

    String fileName = (String) inspector.getModelFile();

    Graph<Vertex, Edge> graph = inspector.getInspectionOutput().getGraph();

    _networkTree.addNetwork( fileName );

    addGraphView( fileName, graph );
  }

  public void setActiveView( String name )
  {
    int i = _topTabs.getTabCount();

    for( int x = 0; x < i; x++ )
    {
      if( _topTabs.getTitleAt( x ).equals( name ) )
      {
        _topTabs.setSelectedIndex( x );
        return;
      }
    }

  }
}

/**
 * 
 */
package ancat.visualizers;

import java.io.IOException;

import javax.swing.JFrame;

import edu.uci.ics.jung.graph.Graph;

/**
 * @author alunkeit
 * 
 */
public class UIVisualizer<V, E> extends ModelVisualizer<V, E>
{

  /*
   * (non-Javadoc)
   * 
   * @see ancat.common.GraphInspector#inspect(java.lang.Object)
   */
  @Override
  public void inspect( Object graph )
  {
    this._graph = (Graph<V, E>) graph;

    JFrame f = new JFrame();
    f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    // f.getContentPane().add( new UIVisualizer<V, E>() );
    f.getContentPane().add( new UIViewerComponent<V, E>( _graph ) );

    f.pack();
    f.setAlwaysOnTop( true );
    f.setSize( 800, 800 );
    f.setVisible( true );

    System.out.println( "XXXXX" );
    try
    {
      System.in.read();
    }
    catch( IOException e )
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}

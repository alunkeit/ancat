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

import java.util.Hashtable;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * @author alunkeit
 * 
 *         This is the base for the graphical user interface of ancat.
 */
public class AncatUI
{
  private MainFrame _frame;

  protected static Logger _logger = Logger.getRootLogger();

  private static AncatUI _instance;

  static
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
  }

  private AncatUI()
  {
    try
    {
      // Set System L&F
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException e)
    {
      // handle exception
    }
    catch (ClassNotFoundException e)
    {
      // handle exception
    }
    catch (InstantiationException e)
    {
      // handle exception
    }
    catch (IllegalAccessException e)
    {
      // handle exception
    }
  }

  public static AncatUI getInstance()
  {
    if( null == _instance )
      _instance = new AncatUI();

    return _instance;
  }

  public void display()
  {
    _frame = new MainFrame();

    Map<String, String> params = new Hashtable<String, String>();

    params.put( "height", "1200" );
    params.put( "width", "1600" );
    params.put( "onTop", "true" );
    params.put( "title", "ancat ui" );

    _frame.initComponents( params );
    _frame.setVisible( true );
  }

  /**
   * @param args
   */
  public static void main( String[] args )
  {
    AncatUI ui = AncatUI.getInstance();

    ui.display();
  }

}

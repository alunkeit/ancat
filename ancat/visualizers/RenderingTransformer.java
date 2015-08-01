/**
 * 
 */
package ancat.visualizers;

import java.awt.Font;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author alunkeit
 * 
 */
public class RenderingTransformer
{

  protected RenderingConfiguration _renderConfig;

  protected Logger _logger = Logger.getRootLogger();

  public RenderingTransformer( RenderingConfiguration reader )
  {
    _renderConfig = reader;
  }

  protected Font setupFont( String section, Map<String, String> config )
  {
    int fontStyle = Font.PLAIN;
    int fontSize = 10;
    String fontFamily = "Dialog";

    // Map<String, String> nodeConfig = _renderConfig.nodeConfiguration();

    if( config.containsKey(section + "font-style") )
    {
      String style = config.get(section + "font-style");

      if( style.equalsIgnoreCase("PLAIN") )
        fontStyle = Font.PLAIN;

      else if( style.equalsIgnoreCase("BOLD") )
        fontStyle = Font.BOLD;

      else if( style.equalsIgnoreCase("ITALIC") )
        fontStyle = Font.ITALIC;
    }

    if( config.containsKey(section + "font-size") )
    {
      fontSize = Integer.parseInt(config.get(section + "font-size"));
    }

    if( config.containsKey(section + "font-family") )
    {
      fontFamily = config.get(section + "font-family");
    }

    return new Font(fontFamily, fontStyle, fontSize);
  }
}

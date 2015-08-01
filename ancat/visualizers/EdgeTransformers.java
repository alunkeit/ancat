/**
 *   This file is part of ancat.
 *
 *   ancat is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   ancat is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with ancat.  If not, see <http://www.gnu.org/licenses/>.
 */
package ancat.visualizers;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections15.Transformer;

import ancat.common.Edge;

/**
 * @author a.lunkeit
 * 
 *         The Edge Transformer class. Reads configuration data from
 *         RenderingConfiguration interface and applies the configuration to the
 *         edges of the graph.
 * 
 */
public class EdgeTransformers<E> extends RenderingTransformer
{

  /**
   * The stroke used for rendering edges
   */
  private Stroke _edgeStroke = null;

  /**
   * The labeling font
   */
  private Font _font = null;

  /**
   * This flag is set to true in case that labeling on edges is required
   */
  private boolean _labeling = false;

  /**
   * The key used for labeling data on edges
   */
  private String _labelingKey = "";

  public EdgeTransformers( RenderingConfiguration renderConfig )
  {
    super(renderConfig);

    /**
     * Get the font for rendering labels on edges
     */
    _font = setupFont("edge:", renderConfig.edgeConfiguration());

    setupStroke();

    /**
     * Tag edge:label with values on / off is used to enable rendering of label
     * data. By default, this option is turned off.
     */
    if( _renderConfig.edgeConfiguration().containsKey("edge:label") )
    {
      _labeling = _renderConfig.edgeConfiguration().get("edge:label")
          .equalsIgnoreCase("on") ? true : false;

    }
    /**
     * Tag edge:data:labeldata is used for rendering additional data on the
     * edge. For function analysis this data is used to render the call
     * parameters on edges.
     */
    if( _renderConfig.edgeConfiguration().containsKey("edge:data:labeldata") )
    {
      _labelingKey = _renderConfig.edgeConfiguration().get(
          "edge:data:labeldata");
    }
  }

  /**
   * Configuration of the Stroke used for drawing edges. It processes the
   * following values:
   * 
   * <code> edge:style </code> may be set to solid, dashed or dotted. By default
   * the edge style is set to dashed.
   * 
   * <code> edge:stroke-width </code> The width of the stroke used for
   * rendering.
   * 
   * <code> edge:dash-pattern</code> Contains an array of float values
   * describing the dash pattern. See documentation of Java Stroke class to
   * understand the pattern style</code>
   */
  protected void setupStroke()
  {
    String style = "solid";
    float width = 0.5f;
    float dash[] = null;

    Map<String, String> edgeConfig = _renderConfig.edgeConfiguration();

    if( edgeConfig.containsKey("edge:style") )
    {
      style = edgeConfig.get("edge:style");

      if( !style.equalsIgnoreCase("solid") && !style.equalsIgnoreCase("dashed")
          && !style.equalsIgnoreCase("dotted") )
      {
        _logger.error("unsupported edge style attribute: " + style
            + " allowed is solid, dashed, dotted");

        style = "solid";
      }
    }

    if( edgeConfig.containsKey("edge:stroke-width") )
    {
      try
      {
        width = Float.parseFloat(edgeConfig.get("edge:stroke-width"));
      }
      catch( Exception e )
      {
        _logger
            .error("unsupported specification of float value for edge width, changing to default");
      }
    }

    if( edgeConfig.containsKey("edge:dash-pattern") )
    {
      try
      {
        StringTokenizer tokenizer = new StringTokenizer(
            edgeConfig.get("edge:dash-pattern"), ",");

        dash = new float[tokenizer.countTokens()];

        int counter = 0;

        while( tokenizer.hasMoreElements() )
        {
          dash[counter] = Float.parseFloat(tokenizer.nextToken().trim());
          counter += 1;
        }
      }
      catch( Exception e )
      {
        _logger.error("Error while parsing dash style, changing to default");

        dash = new float[] { 10.0f };
      }
    }

    if( style.equalsIgnoreCase("dashed") )
    {
      _edgeStroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
          BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
    }
    else if( style.equalsIgnoreCase("dotted") )
    {
      dash = new float[] { 2.0f, 2.0f };
      _edgeStroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
          BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
    }
    else if( style.equalsIgnoreCase("solid") )
    {
      _edgeStroke = new BasicStroke(width, BasicStroke.CAP_BUTT,
          BasicStroke.JOIN_MITER);
    }
  }

  /**
   * 
   * @author a.lunkeit
   * 
   */
  protected class StrokeTransformer implements Transformer<E, Stroke>
  {
    /**
     * Returns the Stroke for Edge rendering
     */
    public Stroke transform( E s )
    {
      return _edgeStroke;
    }
  };

  /**
   * 
   * @author a.lunkeit
   * 
   */
  protected class FontTransformer implements Transformer<E, Font>
  {
    /**
     * Provides the font for rendering
     */
    public Font transform( E e )
    {
      return _font;
    }
  };

  /**
   * 
   * @author a.lunkeit
   * 
   *         Provides the label for rendering. Depends on the configuration
   *         values provide in <code>directed.conf</code>
   * 
   */
  protected class LabelTransformer implements Transformer<E, String>
  {
    public String transform( E e )
    {
      Edge edge = (Edge) e;

      if( _labeling == true )
      {
        String labelData = edge.getAdditionalElement(_labelingKey);

        return labelData != null ? labelData : "missing";
      }

      return "";
    }
  };
}

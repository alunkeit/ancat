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
package ancat.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author alunkeit
 * 
 */
public class ConfigurationParser
{
  private String _file;

  private List<ConfigurationItem> _configuration;

  private Map<String, String> _configItems;

  public ConfigurationParser( String file )
  {
    _file = file;

    _configuration = new Vector<ConfigurationItem>();

    _configItems = new HashMap<String, String>();
  }

  public void load() throws IOException
  {
    BufferedReader in = new BufferedReader(new LineNumberReader(new FileReader(
        new File(_file))));

    String line = null;

    while( (line = in.readLine()) != null )
    {
      if( line.isEmpty() )
        continue;

      line = line.trim();

      if( line.startsWith("#") )
        continue;

      if( line.startsWith("begin type ") )
      {
        StringTokenizer tokenizer = new StringTokenizer(line, " ");

        tokenizer.nextToken();
        tokenizer.nextToken();
        String type = tokenizer.nextToken().trim();

        ConfigurationItem i = parseTypeDefinition(in);
        i._type = type;
        _configuration.add(i);
      }
      else
      {
        StringTokenizer tokenizer = new StringTokenizer(line, "=");
        _configItems.put(tokenizer.nextToken().trim(), tokenizer.nextToken()
            .trim());
      }
    }

    in.close();
  }

  private ConfigurationItem parseTypeDefinition( BufferedReader in )
      throws IOException
  {
    String line = null;

    ConfigurationItem item = new ConfigurationItem();

    item._items = new HashMap<String, String>();

    while( (line = in.readLine()) != null )
    {
      if( line.isEmpty() )
        continue;

      line = line.trim();

      if( line.startsWith("#") )
        continue;

      if( line.startsWith("end type") )
      {
        return item;
      }

      StringTokenizer tokenizer = new StringTokenizer(line, "=");

      if( tokenizer.countTokens() != 2 )
        continue;

      item._items.put(tokenizer.nextToken().trim(), tokenizer.nextToken()
          .trim());
    }

    return null;
  }

  /**
   * 
   * @author alunkeit
   * 
   */
  public static class ConfigurationItem
  {
    public String _type;

    public Map<String, String> _items;
  }

  public List<ConfigurationItem> complexItems()
  {
    return _configuration;
  }

  public Map<String, String> simpleItems()
  {
    return _configItems;
  }

}

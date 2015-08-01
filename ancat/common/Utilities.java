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
package ancat.common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author a.lunkeit
 *
 * This class encapsulates common service routines required by the graph tool
 */
public class Utilities
{

  /**
   * 
   */
  public Utilities()
  {
    // TODO Auto-generated constructor stub
  }
  
  /**
   * Saves a given BufferedImage to a file
   * 
   * @param img
   * @param strFileName
   */
  public static void save( BufferedImage img, String strFileName )
  {
    try
    {
      ImageIO.write( img, "png", new File( strFileName ));
    }
    catch( IOException e )
    {
      e.printStackTrace();
    }
  }

}

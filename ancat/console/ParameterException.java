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
package ancat.console;

/**
 * @author alunkeit
 * 
 * A ParameterException is thrown if a CommandLine Parameter was insufficient or 
 * if not enough parameters have been set as input data.
 *
 */
public class ParameterException extends Exception
{

  public ParameterException( String message )
  {
    super( message );
  }

  /**
   * 
   */
  private static final long serialVersionUID = 3158706078735217473L;

}

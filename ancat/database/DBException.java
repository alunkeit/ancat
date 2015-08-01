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
package ancat.database;

/**
 * @author alunkeit
 *
 */
public class DBException extends Exception
{

  /**
   * 
   */
  public DBException()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   */
  public DBException( String message )
  {
    super( message );
    // TODO Auto-generated constructor stub
  }

  /**
   * @param cause
   */
  public DBException( Throwable cause )
  {
    super( cause );
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   */
  public DBException( String message, Throwable cause )
  {
    super( message, cause );
    // TODO Auto-generated constructor stub
  }

  /**
   * @param message
   * @param cause
   * @param enableSuppression
   * @param writableStackTrace
   */
  public DBException( String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace )
  {
    super( message, cause, enableSuppression, writableStackTrace );
    // TODO Auto-generated constructor stub
  }

}

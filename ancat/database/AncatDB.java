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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * @author alunkeit
 * 
 */
public class AncatDB
{
  Connection _conn;

  static Logger _logger = Logger.getRootLogger();

  /**
   * Constructor of the class. Creates a new connection to the database. If the db
   * does not exist, an exception is thrown.
   * 
   * @throws SQLException
   * @throws ClassNotFoundException
   * @throws DBException
   */
  public AncatDB( String file, String user, String password )
      throws SQLException, ClassNotFoundException, DBException
  {
    Class.forName( "org.hsqldb.jdbcDriver" );
    
    _logger.debug( "sql driver instantiated" );
    _logger.debug( "will open db file: " + file );
 
    _conn = DriverManager.getConnection( "jdbc:hsqldb:" + file + ";ifexists=true", 
        user, 
        password ); 

    if( _conn != null )
    {
      _logger.debug( "Connection successfull created" );
    }
    else
    {
      _logger.error( "Failed to create connection to: " + file + " for user " + user );
      throw new DBException( "Failed to create connection to: " + file + " for user " + user );
    }
  }

  /**
   * Closes the database connection. Performs a shutdown operation before forcing the hsql system to
   * persist all data.
   * 
   * @throws SQLException
   */
  public void close() throws SQLException
  {

    Statement st = _conn.createStatement();

    st.execute( "SHUTDOWN" );
    _conn.close();
  }

  /**
   * performs a self check to find out if the opened database is one that can be used by our tool.
   * @return true in case of successful initialization.
   */
  public boolean init()
  {
    try
    {
      boolean result = tableCheck( "graphs" ) & tableCheck( "edges" ) & tableCheck( "vertices" );

      return result;
    }
    catch( SQLException e )
    {
      _logger.error( e.getMessage() + "\n" + e.getSQLState() );

      return false;
    }

  }

  /**
   * Private routine that checks if a certain table exists.
   * 
   * @param table
   * @return
   * @throws SQLException
   */
  private boolean tableCheck( String table ) throws SQLException
  {
    Statement st = null;
    ResultSet rs = null;

    st = _conn.createStatement();
    
    _logger.debug( "executing statement..." );

    rs = st.executeQuery( "select count(*) " + "from information_schema.tables " + "where table_schema = 'PUBLIC' " + "and table_name = '" + table.toUpperCase() + "'" );

    long result = 0;

    if( rs != null )
    {
      _logger.debug( "result set is not empty, checking result" );
      
      rs.next();
      result = (long)rs.getObject( 1 );
      
      _logger.debug( "count of detected tables with name := " + table );
    }
    else
    {
      _logger.debug( "empty result set..." );
    }

    st.close();
    
    _logger.debug( "will return := " + (result == 1));
    
    return result == 1;
  }
  
  /**
   * Acquires a statement object.
   * 
   * @return
   * @throws SQLException
   */
  Statement acquireStatement() throws SQLException
  {
    return _conn.createStatement();
  }
  
  /**
   * Close the statement object
   * 
   * @param s
   * @throws SQLException
   */
  void closeStatement( Statement s ) throws SQLException
  {
    s.close();
  }
  
  DatabaseMetaData getMetaData() throws SQLException
  {
    return _conn.getMetaData();
  }
}

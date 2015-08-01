/**
 * This file is part of ancat.
 * 
 * ancat is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
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
package ancat.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import ancat.util.ErrorHandler;

/**
 * @author alunkeit
 * 
 *         GraphsTable provides a binding interface between the SQL definition
 *         of table graphs and the use in the program. It allows to insert,
 *         remove and modify data in the table. It contains an inner class
 *         (GraphDescription) which represents one single row in the sql table.
 */
public class GraphsTable extends AncatTable
{

  private static Logger _logger = Logger.getLogger( GraphsTable.class );

  /**
   * 
   * @author alunkeit
   *
   * GraphDescription represents a single row in table graphs. Members of this
   * class are public. The GraphDescription structure is a kind of transfer 
   * structure that can be filled from the underlying database or being altered
   * by the program and updated in the database.
   */
  public static class GraphDescription
  {
    public int _id = -1;

    public String _description;

    public String _configuration;

    public String _edge_table;

    public String _vertex_table;

    public String toString()
    {
      return String.format( 
          "\n" +
          "id: %d\n " +
          "description: %s \n " +
          "configuration: %s\n " +
          "edge_table: %s \n " +
          "vertex_table: %s\n", 
          _id, _description, _configuration, _edge_table, _vertex_table );
    }
  }

  /**
   * Public constructor of class GraphsTable
   */
  public GraphsTable( AncatDB db )
  {
    super( db, "graphs" );
  }

  /**
   * Reads all entries in the graphs table. Using this method might be expensive
   * in case that the database contains a high amount of entries.
   * 
   * @return A vector containing all GraphDescription elements found in the table.
   * @throws SQLException
   */
  public Vector<GraphDescription> readAll() throws SQLException
  {
    _logger.debug( "GraphsTable::readAll()" );
    
    Statement s = null;
    SQLException exc = null;
    Vector<GraphDescription> results = new Vector<GraphDescription>();

    try
    {

      s = _db_handle.acquireStatement();

      ResultSet r = s.executeQuery( "select * from graphs" );

      if( null == r )
      {
        _logger.error( "result set is empty!" );
        s.close();
      }

      while( r.next() )
      {
        GraphDescription desc = new GraphDescription();
        desc._id = r.getInt( 1 );
        desc._description = r.getString( 2 );
        desc._configuration = r.getString( 3 );
        desc._edge_table = r.getString( 4 );
        desc._vertex_table = r.getString( 5 );

        _logger.debug( "description object := " + desc );

        results.add( desc );
      }

      r.close();
    }
    catch( SQLException e )
    {
      exc = e;

      _logger.error( e.getMessage() );
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );
    }
    finally
    {
      _db_handle.closeStatement( s );
    }

    if( exc != null )
      throw exc;

    return results;
  }

  /**
   * Returns a single row of the GraphsTable
   * 
   * @param primary_key
   * @return
   * @throws SQLException
   */
  public GraphDescription getById( int primary_key ) throws SQLException
  {
    _logger.debug( "GraphDescription::getById()" );
    
    Statement s = null;
    SQLException exc = null;
    GraphDescription desc = null;

    try
    {
      s = _db_handle.acquireStatement();

      ResultSet r = s.executeQuery( "select * from graphs where id = " + primary_key );

      desc = new GraphDescription();

      if( r != null )
      {
        r.next();
        desc._id = r.getInt( 1 );
        desc._description = r.getString( 2 );
        desc._configuration = r.getString( 3 );
        desc._edge_table = r.getString( 4 );
        desc._vertex_table = r.getString( 5 );

        _logger.debug( "description object := " + desc );

        r.close();
      }
    }
    catch( SQLException e )
    {
      exc = e;
    }
    finally
    {
      if( null != s )
        _db_handle.closeStatement( s );
    }

    if( exc != null )
      throw exc;

    return desc;
  }

  /**
   * Inserts a new description of a graph into the leading graph table. The
   * description object must be initialized with all values except the id value.
   * The id value will be updated after successful execution of the statement by
   * the sql engine. It will contain the value of the identity assigned to this
   * row.
   * 
   * @param desc
   * @throws SQLException
   */
  public void insert( GraphDescription desc ) throws SQLException
  {
    _logger.debug( "GraphsTable::insert()" );

    Statement s = null;
    SQLException exc = null;

    try
    {
      s = _db_handle.acquireStatement();

      _logger.debug( "statement acquired" );

      // compile the statement
      String statement = String.format( "insert into graphs( description, configuration, edge_table, vertex_table  ) values ( '%s', '%s', '%s', '%s' ) CALL IDENTITY()", 
          desc._description, 
          desc._configuration, 
          desc._edge_table, 
          desc._vertex_table );
      
      _logger.debug( "compiled statement:\n" + statement );

      ResultSet r = s.executeQuery( statement );

      _logger.debug( "statement executed" );

      if( r != null )
      {
        r.next();

        int identity = r.getInt( 1 );

        _logger.debug( "identity value: " + identity );

        desc._id = identity;

        r.close();
      }
      else
      {
        _logger.debug( "The answer to statement was an empty result set!" );
      }
    }
    catch( SQLException e )
    {
      exc = e;

      _logger.error( "Exception caught: " + exc.getMessage() );

      StackTraceElement[] elements = exc.getStackTrace();

      _logger.error( ErrorHandler.stackTrace2Buffer( elements ).toString() );
    }
    finally
    {
      if( s != null )
        _db_handle.closeStatement( s );
    }

    if( exc != null )
      throw exc;
  }

  /**
   * Removes a row from the graphs table.
   * 
   * @param desc
   * @throws SQLException
   */
  public void delete( GraphDescription desc ) throws SQLException
  {
    _logger.debug( "GraphsTable::delete()" );

    Statement s = null;
    SQLException exc = null;

    try
    {
      _logger.debug( "creating new statement object" );

      s = _db_handle.acquireStatement();

      String statement = String.format( "DELETE from graphs WHERE id = %d", desc._id );

      _logger.debug( "executing statement: " + statement );

      ResultSet r = s.executeQuery( statement );

      _logger.debug( "statement executed" );

      if( null == r )
      {
        _logger.error( "result set is empty!" );
        _db_handle.closeStatement( s );
      }

      desc._id = -1;

      r.close();
    }
    catch( SQLException e )
    {
      exc = e;
      _logger.error( e.getMessage() );
    }
    finally
    {
      if( s != null )
        _db_handle.closeStatement( s );
    }

    if( exc != null )
      throw exc;
  }

  /**
   * Updates the description object stored in table graphs
   * 
   * @param desc
   * @throws SQLException
   */
  public void update( GraphDescription desc ) throws SQLException
  {
    _logger.debug( "GraphsTable::update()" );

    Statement s = null;
    SQLException exc = null;

    try
    {
      s = _db_handle.acquireStatement();

      String statement = String.format( "UPDATE graphs SET description = '%s', configuration = '%s', edge_table = '%s', vertex_table = '%s' WHERE id = '%d'", 
          desc._description, 
          desc._configuration, 
          desc._edge_table, 
          desc._vertex_table,
          desc._id );
      
      _logger.debug( "will execute the following statement: " + statement );

      ResultSet r = s.executeQuery( statement );

      if( null == r )
      {
        _logger.error( "result set is empty!" );

      }
      else
      {
        r.close();
      }
    }
    catch( SQLException e )
    {
      exc = e;
    }
    finally
    {
      if( s != null )
        _db_handle.closeStatement( s );
    }

    if( exc != null )
      throw exc;

  }

  /**
   * Count rows in table graphs. This provides no guarantee that each of the
   * counted rows contains valid data. This function can be used to trigger
   * piecewise iteration over the table or to decide if all description data
   * should be read by one operation using <code>readTable()</code>
   * 
   * @return Returns the count of rows in the table.
   * @throws SQLException
   */
  public int count() throws SQLException
  {
    _logger.debug( "GraphsTable::count()" );

    Statement s = null;
    SQLException exc = null;
    int count = 0;

    try
    {
      s = _db_handle.acquireStatement();

      ResultSet r = s.executeQuery( "SELECT count(*) from graphs" );

      if( null == r )
      {
        _logger.error( "result set is empty!" );

      }
      else
      {
        r.next();

        count = r.getInt( 1 );

        _logger.debug( "count of rows in GRAPHS := " + count );

        r.close();
      }
    }
    catch( SQLException e )
    {
      exc = e;
    }
    finally
    {
      if( s != null )
        _db_handle.closeStatement( s );
    }

    if( exc != null )
      throw exc;

    return count;
  }

  /**
   * Returns the list of primary keys in table graphs
   * 
   * @return
   * @throws SQLException
   */
  public List<Integer> listIdentities() throws SQLException
  {
    _logger.debug( "GraphsTable::listIdentities()" );

    Statement s = null;
    SQLException exc = null;

    List<Integer> keys = new LinkedList<Integer>();

    try
    {
      s = _db_handle.acquireStatement();

      _logger.debug( "statement acquired!" );

      ResultSet r = s.executeQuery( "select id from graphs" );

      if( r != null )
      {
        _logger.debug( "iterating through result set..." );

        while( r.next() )
        {
          keys.add( r.getInt( 1 ) );
        }

        _logger.debug( "count of primary keys in list := " + keys.size() );

        r.close();
      }
      else
      {
        _logger.debug( "empty result set, something went wrong?" );
      }
    }
    catch( SQLException e )
    {
      exc = e;
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );
    }
    finally
    {
      if( s != null )
        _db_handle.closeStatement( s );
    }

    if( exc != null )
      throw exc;

    return keys;
  }

}

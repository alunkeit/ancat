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
 */
public class VertexTable extends AncatTable
{

  private static Logger _logger = Logger.getLogger( GraphsTable.class );
  
  /**
   * 
   * @author alunkeit
   *
   */
  public static class Vertex
  {
    /**
     * primary key field in table
     */
    public int _id = -1;
    
    /**
     * id of the vertex in the model.
     */
    public int _vertex_id = -1;
    
    /**
     * value of b-transshipment of there is one
     */
    public double _b = 0;
    
    /**
     * Overloads operation Object::toString()
     */
    public String toString()
    {
      return String.format( "Vertex: _id := %d\n, _vertex_id := %d\n, _b := %s", _id, _vertex_id, Double.toString( _b ));
    }
  }
  
  /**
   * 
   */
  public VertexTable( AncatDB db )
  {
    super( db, "vertices" );
  }
  
  /**
   * 
   * @return
   * @throws SQLException
   */
  public int count() throws SQLException
  {
    _logger.debug( "VertexTable::count()" );

    Statement s = null;
    SQLException exc = null;
    int count = 0;

    try
    {
      s = _db_handle.acquireStatement();

      ResultSet r = s.executeQuery( String.format( "SELECT count(*) from %s", _name ));

      if( null == r )
      {
        _logger.error( "result set is empty!" );

      }
      else
      {
        r.next();

        count = r.getInt( 1 );

        _logger.debug( "count of rows in vertices := " + count );

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
   * 
   * @param v
   * @throws SQLException
   */
  public void insert( Vertex v ) throws SQLException
  {
    _logger.debug( "VertexTable::insert" );
    
    Statement s = null;
    SQLException exc = null;

    try
    {
      s = _db_handle.acquireStatement();

      _logger.debug( "statement acquired" );
      
     

      // compile the statement
      String statement = String.format( "insert into %s( vertex_id, b ) values ( '%d', '%s' ) CALL IDENTITY()", 
          _name,
          v._vertex_id, 
          Double.toString( v._b ));
      
      _logger.debug( "compiled statement:\n" + statement );

      ResultSet r = s.executeQuery( statement );

      _logger.debug( "statement executed" );

      if( r != null )
      {
        r.next();

        int identity = r.getInt( 1 );

        _logger.debug( "identity value: " + identity );

        v._id = identity;

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
   * 
   * @param id
   * @return
   * @throws SQLException
   */
  public Vertex getById( int id ) throws SQLException
  {
    _logger.debug( "VertexTable::getById()" );
    
    Statement s = null;
    SQLException exc = null;
    Vertex vertex = null;

    try
    {
      s = _db_handle.acquireStatement();

      ResultSet r = s.executeQuery( String.format( "select * from %s where id = %d", _name, id ));

      vertex = new Vertex();

      if( r != null )
      {
        r.next();
        vertex._id = r.getInt( 1 );
        vertex._vertex_id = r.getInt( 2 );
        vertex._b = r.getDouble( 3 );
        
        _logger.debug( "vertex object := " + vertex );

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

    return vertex;
  }
  
  /**
   * 
   * @return
   * @throws SQLException
   */
  public List<Integer> listIdentities() throws SQLException
  {
    _logger.debug( "VertexTable::listIdentities()" );

    Statement s = null;
    SQLException exc = null;

    List<Integer> keys = new LinkedList<Integer>();

    try
    {
      s = _db_handle.acquireStatement();

      _logger.debug( "statement acquired!" );

      ResultSet r = s.executeQuery( String.format( "select id from %s", _name ));

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
  
  /**
   * 
   * @param vertex
   * @throws SQLException
   */
  public void delete( Vertex vertex ) throws SQLException
  {
    _logger.debug( "VertexTable::delete()" );

    Statement s = null;
    SQLException exc = null;

    try
    {
      _logger.debug( "creating new statement object" );

      s = _db_handle.acquireStatement();

      String statement = String.format( "DELETE from %s WHERE id = %d", _name, vertex._id );

      _logger.debug( "executing statement: " + statement );

      ResultSet r = s.executeQuery( statement );

      _logger.debug( "statement executed" );

      if( null == r )
      {
        _logger.error( "result set is empty!" );
        _db_handle.closeStatement( s );
      }

      vertex._id = -1;

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
   * 
   * @return
   * @throws SQLException
   */
  public Vector<Vertex> readAll() throws SQLException
  {
    _logger.debug( "VertexTable::readAll()" );
    
    Statement s = null;
    SQLException exc = null;
    Vector<Vertex> results = new Vector<Vertex>();

    try
    {
      s = _db_handle.acquireStatement();

      ResultSet r = s.executeQuery( String.format( "select * from %s", _name ));

      if( null == r )
      {
        _logger.error( "result set is empty!" );
        s.close();
      }

      while( r.next() )
      {
        Vertex vertex = new Vertex();
        vertex._id = r.getInt( 1 );
        vertex._vertex_id = r.getInt( 2 );
        vertex._b = r.getDouble( 3 );
        
        _logger.debug( "vertex object := " + vertex );

        results.add( vertex );
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
   * 
   * @param v
   * @throws SQLException
   */
  public void update( Vertex v ) throws SQLException
  {
    _logger.debug( "VertexTable::update()" );
    
    Statement s = null;
    SQLException exc = null;

    try
    {
      s = _db_handle.acquireStatement();

      String statement = String.format( "UPDATE %s SET vertex_id = '%d', b = '%s' WHERE id = '%d'", 
          _name, 
          v._vertex_id, 
          Double.toString( v._b ),
          v._id);
      
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

}

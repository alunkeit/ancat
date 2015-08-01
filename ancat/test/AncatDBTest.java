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
package ancat.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ancat.database.AncatDB;
import ancat.database.DBException;
import ancat.database.GraphsTable;
import ancat.database.GraphsTable.GraphDescription;
import ancat.database.VertexTable;
import ancat.database.VertexTable.Vertex;
import ancat.util.ErrorHandler;

public class AncatDBTest
{

  protected static Logger _logger = Logger.getRootLogger();

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
  
  AncatDB _db_handle = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception
  {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception
  {
  }

  @Before
  public void setUp() throws Exception
  {
    _db_handle = new AncatDB( "file:/Volumes/Daten/db/ancat", "sa", "" );
    
    if( !_db_handle.init())
      throw new DBException( "failed in opening ancat db..." );
  }

  @After
  public void tearDown() throws Exception
  {
    if( null != _db_handle )
      _db_handle.close();
  }
  
  @Test
  public void test_graphs_readTable()
  {
    GraphsTable gt = new GraphsTable( _db_handle );
    
    Vector<GraphDescription> descriptions = null;

    try
    {
      if( (descriptions = gt.readAll()) == null )
      {
        _logger.error( "failed to read table from hsql file" );
        fail( "failed to read table from hsql file" );
      }
      else
      {
        for( GraphDescription d : descriptions )
        {
          _logger.info( d );
        }
      }
    }
    catch( SQLException e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );
      
      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }
  }
  
  @Test
  public void test_graphs_singleSelect()
  {
    try
    {
    GraphsTable gt = new GraphsTable( _db_handle );
    
    GraphDescription d = gt.getById( 0 );
    _logger.info( d );
    }
    catch( Exception e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );
      
      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }
  }


  @Test
  public void test_graphs_countRows()
  {
    try
    {
      GraphsTable table = new GraphsTable( _db_handle );

      int rows = table.count();

      _logger.info( "count of found rows: " + rows );

    }
    catch( Exception e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );
      
      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }
  }

  @Test
  public void test_graphs_insertGraph()
  {
    try
    {
      
      GraphsTable table = new GraphsTable( _db_handle );

      GraphsTable.GraphDescription desc = new GraphsTable.GraphDescription();

      desc._description = "a test entry from the JUnit test";
      desc._configuration = "a test config entry from the JUnit test";
      desc._edge_table = "hint to non-existing table";
      desc._vertex_table = "hint to non existing table";

      table.insert( desc );

      _logger.info( "description has been inserted, now checking the identity" );

      if( desc._id == 0 )
      {
        fail( "identity value is still 0, error in execution of SQL statement?" );
      }
      else
      {
        _logger.info( "The new identity value is: " + desc._id );
      }

    }
    catch( Exception e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );
      
      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }

  }
  
  @Test
  public void test_getPrimaryKeys()
  {
    try
    {
      GraphsTable table = new GraphsTable( _db_handle );

      List<Integer> keys = table.listIdentities();

      for( Integer i : keys )
      {
        _logger.info( "key value := " + i );
      }
    }
    catch( Exception e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );

      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }
  }
  
  @Test
  public void test_graphs_delete()
  {
    try
    {
      
      GraphsTable table = new GraphsTable( _db_handle );

      GraphsTable.GraphDescription desc = new GraphsTable.GraphDescription();

      desc._description = "a test entry from the JUnit test";
      desc._configuration = "a test config entry from the JUnit test";
      desc._edge_table = "hint to non-existing table";
      desc._vertex_table = "hint to non existing table";

      table.insert( desc );

      _logger.info( "description has been inserted, now checking the identity" );

      if( desc._id == 0 )
      {
        fail( "identity value is still 0, error in execution of SQL statement?" );
      }
      else
      {
        _logger.info( "The new identity value is: " + desc._id );
      }
      
      table.delete( desc );
      
      if( desc._id != -1 )
      {
        fail( "It seems as if the entry wasn't removed" );
      }
      else
      {
        _logger.info( "test successful executed" );
      }

    }
    catch( Exception e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );
      
      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }
  }
  
  @Test
  public void test_graphs_update()
  {
    try
    {
      
      GraphsTable table = new GraphsTable( _db_handle );

      GraphsTable.GraphDescription desc = new GraphsTable.GraphDescription();

      desc._description = "a test entry from the JUnit test";
      desc._configuration = "a test config entry from the JUnit test";
      desc._edge_table = "hint to non-existing table";
      desc._vertex_table = "hint to non existing table";

      table.insert( desc );

      _logger.info( "description has been inserted, now checking the identity" );

      if( desc._id == 0 )
      {
        fail( "identity value is still 0, error in execution of SQL statement?" );
      }
      else
      {
        _logger.info( "The new identity value is: " + desc._id );
      }
      
      desc._description = "altered description";
      
      table.update( desc );
      
      GraphDescription desc2 = table.getById( (int)desc._id );
      
      table.delete( desc2 );
      
      if( !desc2._description.equals( "altered description" ))
      {
        fail( "execution of the test case failed!" );
      }
      else
      {
        _logger.info( "update operation was successful" );
      }

    }
    catch( Exception e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );
      
      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }
  }
  
  @Test
  public void test_vertices_insert()
  {
    try
    {
      VertexTable table = new VertexTable( _db_handle );

      VertexTable.Vertex v1 = new VertexTable.Vertex();
      v1._vertex_id = 1;

      _logger.info( "testing insert operation" );
      table.insert( v1 );

      if( v1._id == -1 )
      {
        fail( "inserting an vertex failed!" );
      }
      else
      {
        _logger.info( "insert operation was successful, now testing delete operation" );
        table.delete( v1 );
        
        if( v1._id == -1 )
          _logger.info( "deletion of object was successfull" );
        else
          fail( "Unable to delete object from database" );
      }
    }
    catch( Exception e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );

      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }
  }
  
  @Test
  public void test_vertices_complex()
  {
    try
    {
      VertexTable table = new VertexTable( _db_handle );
      
      int start_count = table.count();
      
      for( int i = 0; i < 10; i++ )
      {
        Vertex v = new Vertex();
        v._vertex_id = i;
        
        table.insert( v );
      }
      
      int end_count = table.count();
      
      if( end_count - start_count != 10 )
      {
        fail( "failed insert some objects, difference is " + (end_count - start_count ));
      }
      
      // get list of primary keys
      List<Integer> ids = table.listIdentities();
      
      _logger.info( "size of id list := " + ids.size());
      
      // update one element
      Vertex vu = table.getById( ids.get( 0 ));
      vu._vertex_id = 11111;
      table.update( vu );
      
      Vertex vur = table.getById( ids.get( 0 ));
      if( vur._vertex_id != 11111 )
      {
        fail( "update operation failed" );
      }
      else
      {
        _logger.info( "update operation succeeded!" );
      }
      
      // remove objects from table
      for( Integer id : ids )
      {
        Vertex v = new Vertex();
        v._id = id;
        
        table.delete( v );
      }
    }
    catch( Exception e )
    {
      _logger.error( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ) );

      fail( ErrorHandler.stackTrace2Buffer( e.getStackTrace() ).toString() );
    }
  }

}

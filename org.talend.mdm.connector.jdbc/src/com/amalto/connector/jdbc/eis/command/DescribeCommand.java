
package com.amalto.connector.jdbc.eis.command;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.amalto.connector.jdbc.eis.common.ExceptionHandler;
import com.amalto.connector.jdbc.eis.extractor.ResultSetExtractor;

/**
 * Provides logic for executing a command to describe a table, and to handle
 * the results.
 *
 */
public class DescribeCommand
  implements ResultSetExtractingCommand
{
  
  //----------------------------------------------------------------------------
  // Static variables
  //----------------------------------------------------------------------------
  
  private static Logger logger = Logger.getLogger(DescribeCommand.class);
  
  //----------------------------------------------------------------------------
  // Static methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Constants
  //----------------------------------------------------------------------------
  
  public static final String TABLE_NAME_KEY = "table_name";
  
  //----------------------------------------------------------------------------
  // Instance variables
  //----------------------------------------------------------------------------

  private Connection connection;
  private ExceptionHandler exceptionHandler;
  private ResultSetExtractor resultSetExtractor;
  private String tableName;
  
  //----------------------------------------------------------------------------
  // Constructors
  //----------------------------------------------------------------------------
  
  /**
   * Constructs a new instance of
   * <code>DescribeCommand</code>.
   */
  public DescribeCommand()
  {
  }
  
  //----------------------------------------------------------------------------
  // Interface implementations
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  // Implementation of interface Interface1
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Extends overrides
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  // Override of class Class1
  //----------------------------------------------------------------------------
  
  
  //----------------------------------------------------------------------------
  // Public methods exposed by this class
  //----------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public void execute()
  {
	//TODO to support more db
	ResultSet resultSet = null;  
    try
    {
      DatabaseMetaData metaData = this.connection.getMetaData();
      String findTableName = tableName;
      if(metaData.storesLowerCaseIdentifiers())
      {
        findTableName = tableName.toLowerCase();
      }
      else if(metaData.storesUpperCaseIdentifiers())
      {
        findTableName = tableName.toUpperCase();
      }
      resultSet = metaData.getColumns(null, null, findTableName, null);
      this.resultSetExtractor.setAttribute(TABLE_NAME_KEY, tableName);
      this.resultSetExtractor.extractData(resultSet);
    }
    catch(Exception e)
    {
      this.exceptionHandler.handleException(e);
      
    }finally{
		
		try {
			if(resultSet!=null)resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
  }

  /**
   * Sets the connection to be used to execute this command.
   *
   * @param connection the connection
   */
  public void setConnection(final Connection connection)
  {
    this.connection = connection;
  }

  /**
   * {@inheritDoc}
   */
  public void setExceptionHandler(final ExceptionHandler exceptionHandler)
  {
    this.exceptionHandler = exceptionHandler;
  }

  /**
   * {@inheritDoc}
   */
  public void setResultSetExtractor(final ResultSetExtractor resultSetExtractor)
  {
    this.resultSetExtractor = resultSetExtractor;
  }

  /**
   * Sets the name of the table that will be described by this command.
   *
   * @param tableName the table name
   */
  public void setTableName(final String tableName)
  {
    this.tableName = tableName;
  }
  
  //----------------------------------------------------------------------------
  // Protected abstract methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Protected methods for use by subclasses
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Other methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Member classes
  //----------------------------------------------------------------------------
  
}

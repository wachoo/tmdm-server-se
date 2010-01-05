
package com.amalto.connector.jdbc.eis.command;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.amalto.connector.jdbc.eis.common.ExceptionHandler;
import com.amalto.connector.jdbc.eis.extractor.ResultSetExtractor;

/**
 * Performs a show tables command and handles the results.
 *
 */
public class ShowTablesCommand
  implements ResultSetExtractingCommand
{
  
  //----------------------------------------------------------------------------
  // Static variables
  //----------------------------------------------------------------------------
  
  private static Logger logger = Logger.getLogger(ShowTablesCommand.class);
  
  //----------------------------------------------------------------------------
  // Static methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Constants
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Instance variables
  //----------------------------------------------------------------------------
  
  private Connection connection;
  private ExceptionHandler exceptionHandler;
  private ResultSetExtractor resultSetExtractor;

  //----------------------------------------------------------------------------
  // Constructors
  //----------------------------------------------------------------------------
  
  /**
   * Constructs a new instance of
   * <code>ShowTablesCommand</code>.
   */
  public ShowTablesCommand()
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
	ResultSet resultSet=null;  
    try
    {
      DatabaseMetaData metaData = this.connection.getMetaData();
      String schema = metaData.getUserName();
      String[] types = { "TABLE", "VIEW" };
      resultSet = metaData.getTables(null, schema, null, types);
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
   * Sets the connection for this command.
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


package com.amalto.connector.jdbc.eis.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

import com.amalto.connector.jdbc.eis.common.ExceptionHandler;
import com.amalto.connector.jdbc.eis.extractor.ResultSetExtractor;

/**
 * Performs a select command and handles the results.
 *
 */
public class SelectCommand
  implements ResultSetExtractingCommand
{

  //----------------------------------------------------------------------------
  // Static variables
  //----------------------------------------------------------------------------
  
  private static Logger logger = Logger.getLogger(SelectCommand.class);
  
  //----------------------------------------------------------------------------
  // Static methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Constants
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Instance variables
  //----------------------------------------------------------------------------

  private String command;
  private Connection connection;
  private ExceptionHandler exceptionHandler;
  private ResultSetExtractor resultSetExtractor;
  
  //----------------------------------------------------------------------------
  // Constructors
  //----------------------------------------------------------------------------
  
  /**
   * Constructs a new instance of
   * <code>SelectCommand</code>.
   */
  public SelectCommand()
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
	Statement statement = null;  
	ResultSet resultSet = null;
    try
    {
      //TODO change to prepared statement
      statement = this.connection.createStatement();
      resultSet = statement.executeQuery(this.command);
      this.resultSetExtractor.extractData(resultSet);
    }
    catch(SQLException e)
    {
      this.exceptionHandler.handleException(e);
      
    }finally{
    	
		try {
			if(resultSet!=null)resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			if(statement!=null)statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
  }

  /**
   * Sets the SQL select command for this command to execute.
   *
   * @param command the command
   */
  public void setCommand(final String command)
  {
    this.command = command;
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

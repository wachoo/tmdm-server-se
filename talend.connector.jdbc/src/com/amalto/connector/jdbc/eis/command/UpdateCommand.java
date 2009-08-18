
package com.amalto.connector.jdbc.eis.command;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

import com.amalto.connector.jdbc.eis.common.ExceptionHandler;
import com.amalto.connector.jdbc.eis.extractor.UpdateRowcountHandler;

/**
 * Performs a non-SELECT SQL command and handles the resulting rowcount.
 *
 */
public class UpdateCommand
  implements RowcountHandlingCommand
{
  
  //----------------------------------------------------------------------------
  // Static variables
  //----------------------------------------------------------------------------
  
  private static Logger logger = Logger.getLogger(UpdateCommand.class);
  
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
  private UpdateRowcountHandler rowcountHandler;
  
  //----------------------------------------------------------------------------
  // Constructors
  //----------------------------------------------------------------------------
  
  /**
   * Constructs a new instance of
   * <code>UpdateCommand</code>.
   */
  public UpdateCommand()
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
   * Executes the command, handling the results.
   */
  public void execute()
  {
	Statement statement = null;  
    try
    {
      statement = this.connection.createStatement();
      int rowCount = statement.executeUpdate(this.command);
      this.rowcountHandler.handleRowcount(rowCount);
    }
    catch(Exception e)
    {
      this.exceptionHandler.handleException(e);
      
    }finally{
		
		try {
			if(statement!=null)statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
  }

  /**
   * Sets the SQL command to be executed.
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
  public void setRowcountHandler(final UpdateRowcountHandler rowcountHandler)
  {
    this.rowcountHandler = rowcountHandler;
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

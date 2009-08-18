
package com.amalto.connector.jdbc.eis.command;

import com.amalto.connector.jdbc.eis.common.ExceptionHandler;
import com.amalto.connector.jdbc.eis.extractor.UpdateRowcountHandler;

/**
 * Defines operations for an update command that handle the resulting row count.
 *
 */
public interface RowcountHandlingCommand extends Command
{
  /**
   * Executes this command. The resulting rowcount is handled by the configured
   * {@link UpdateRowcountHandler row count handler}.
   */
  public void execute();
  
  /**
   * Sets the exception handler for this command.
   *
   * @param exceptionHandler the exception handler
   */
  public void setExceptionHandler(ExceptionHandler exceptionHandler);
  
  /**
   * Sets the update rowcount handler for this command.
   *
   * @param rowcountHandler the rowcount handler
   */
  public void setRowcountHandler(UpdateRowcountHandler rowcountHandler);
  
}

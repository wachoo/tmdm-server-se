
package com.amalto.connector.jdbc.eis.command;

import com.amalto.connector.jdbc.eis.common.ExceptionHandler;
import com.amalto.connector.jdbc.eis.extractor.ResultSetExtractor;

/**
 * Defines operations for a command that handles the result set with a
 * configurable extractor.
 *
 */
public interface ResultSetExtractingCommand
  extends Command
{
  /**
   * Executes this command. Results are handled by the configured
   * {@link ResultSetExtractor result set extractor}.
   */
  public void execute();
  
  /**
   * Sets the exception handler for this command.
   *
   * @param exceptionHandler the exception handler
   */
  public void setExceptionHandler(ExceptionHandler exceptionHandler);
  
  /**
   * Sets the result set extractor for this command.
   *
   * @param resultSetExtractor the result set extractor
   */
  public void setResultSetExtractor(ResultSetExtractor resultSetExtractor);
}

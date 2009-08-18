
package com.amalto.connector.jdbc.eis.common;

/**
 * Defines operations for handling an exception.
 *
 */
public interface ExceptionHandler
{
  /**
   * Handles the specified exception.
   *
   * @param e the exception to be handled
   */
  public void handleException(Exception e);
  
}

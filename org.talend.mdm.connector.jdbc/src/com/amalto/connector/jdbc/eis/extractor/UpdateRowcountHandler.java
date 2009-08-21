
package com.amalto.connector.jdbc.eis.extractor;

/**
 * Defines operations for handling the rowcount from a non-SELECT SQL command.
 *
 */
public interface UpdateRowcountHandler
{
  /**
   * Handles the rowcount from the SQL update query. This includes any 
   * non-SELECT statements.
   *
   * @param rowCount the row count from the update
   */ 
  public void handleRowcount(int rowCount);
}

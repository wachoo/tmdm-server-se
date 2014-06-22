package com.amalto.connector.jdbc.eis.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Defines the operations for extracting data from a result set.
 *
 */
public interface ResultSetExtractor
{
  /**
   * Extracts data from the result set. Implementing classes are expected to
   * step through each row of the result set and handle the rows as desired.
   *
   * @param resultSet the result set
   * @return the extracted result set
   * @throws SQLException if an error occurs
   */
  public Object extractData(ResultSet resultSet) throws SQLException;
  
  /**
   * Sets the attribute value with the specified key.
   *
   * @param key the attribute key
   * @param value the attribute value
   */
  public void setAttribute(String key, Object value);
}

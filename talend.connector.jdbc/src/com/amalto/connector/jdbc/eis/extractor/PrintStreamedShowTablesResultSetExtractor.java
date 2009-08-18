package com.amalto.connector.jdbc.eis.extractor;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.amalto.connector.jdbc.eis.common.BasicPrintStreamed;

/**
 * Implemention of {@link ResultSetExtractor} that writes the results of a
 * show tables command to the object's print streams.
 *
 */
public class PrintStreamedShowTablesResultSetExtractor
  extends BasicPrintStreamed
  implements ResultSetExtractor
{
  
  //----------------------------------------------------------------------------
  // Static variables
  //----------------------------------------------------------------------------
  
  private static Logger logger = Logger.getLogger(PrintStreamedShowTablesResultSetExtractor.class);
  
  //----------------------------------------------------------------------------
  // Static methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Constants
  //----------------------------------------------------------------------------
  
  private static final int CATALOG_NAME_INDEX = 1;
  private static final int SCHEMA_NAME_INDEX = 2;
  private static final int TABLE_NAME_INDEX = 3;

  private static final int META_DATA_TABLE_NAME = 3;

  //----------------------------------------------------------------------------
  // Instance variables
  //----------------------------------------------------------------------------

  private Map attributes;
  private int catalogNameColumnSize;
  private DatabaseMetaData databaseMetaData;
  private String fieldSeparator = "|";
  private String headerSeparator = "+";
  private int rowCount = 0;
  private int schemaNameColumnSize;
  private int tableNameColumnSize;
  
  //----------------------------------------------------------------------------
  // Constructors
  //----------------------------------------------------------------------------
  
  /**
   * Constructs a new instance of
   * <code>PrintStreamedShowTablesResultSetExtractor</code>.
   */
  public PrintStreamedShowTablesResultSetExtractor()
  {
    this.attributes = new HashMap();
  }
  
  //----------------------------------------------------------------------------
  // Interface implementations
  //----------------------------------------------------------------------------
  //----------------------------------------------------------------------------
  // Implementation of interface ResultSetExtractor
  //----------------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  public Object extractData(final ResultSet resultSet) throws SQLException
  {
    ResultSetMetaData metaData = resultSet.getMetaData();
    if(this.databaseMetaData != null)
    {
      this.tableNameColumnSize = this.databaseMetaData.getMaxTableNameLength();
    }
    else
    {
      this.tableNameColumnSize = metaData.getColumnDisplaySize(META_DATA_TABLE_NAME);
    }
    if(logger.isDebugEnabled())
    {
      logger.debug("table name column size: " + this.tableNameColumnSize);
    }
    this.rowCount = 0;
    while(resultSet.next())
    {
      if(this.rowCount == 0)
      {
        this.writeHeader();
      }
      ++this.rowCount;
      if(logger.isDebugEnabled())
      {
        logger.debug("row: " + this.rowCount);
      }
      String catalogName = resultSet.getString(CATALOG_NAME_INDEX);
      String schemaName = resultSet.getString(SCHEMA_NAME_INDEX);
      String tableName = resultSet.getString(TABLE_NAME_INDEX);
      if(logger.isDebugEnabled())
      {
        logger.debug("table name: " + (tableName == null ? "null" : tableName));
      }
      this.print(this.fieldSeparator);
      this.print(StringUtils.rightPad(tableName, tableNameColumnSize));
      this.println(this.fieldSeparator);
    }
    if(this.rowCount > 0)
    {
      this.writeFooter();
    }
    else
    {
      this.writeNoTables();
    }
    return null;
  }
  
  /**
   * {@inheritDoc}
   */
  public void setAttribute(final String key, final Object value)
  {
    this.attributes.put(key, value);
  }
  
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
   * Sets the database metadata for use by this instance.
   *
   * @param databaseMetaData databaseMetaData
   */
  public void setDatabaseMetaData(final DatabaseMetaData databaseMetaData)
  {
    this.databaseMetaData = databaseMetaData;
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

  private void writeNoTables()
  {
    this.println("No tables found");
  }
  
  /**
   * Writes the footer to the print streams.
   */
  public void writeFooter()
  {
    this.writeHeaderLine();
    this.println("");
    this.println(this.rowCount + " row(s)");
    this.println("");
  }

  /**
   * Writes the header to the print streams.
   */
  public void writeHeader()
  {
    this.println("");
    this.writeHeaderLine();
    this.print(this.fieldSeparator);
    this.print(StringUtils.rightPad("table", this.tableNameColumnSize));
    this.println(this.fieldSeparator);
    this.writeHeaderLine();
  }

  private void writeHeaderLine()
  {
    this.print(this.headerSeparator);
    this.print(StringUtils.repeat("-", this.tableNameColumnSize));
    this.println(this.headerSeparator);
  }

  //----------------------------------------------------------------------------
  // Member classes
  //----------------------------------------------------------------------------
  
}

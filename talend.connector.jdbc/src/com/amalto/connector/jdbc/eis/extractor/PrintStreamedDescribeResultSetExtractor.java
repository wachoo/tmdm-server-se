
package com.amalto.connector.jdbc.eis.extractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.amalto.connector.jdbc.eis.command.DescribeCommand;
import com.amalto.connector.jdbc.eis.common.BasicPrintStreamed;

/**
 * An implementation of the {@link ResultSetExtractor} that writes the results
 * of a describe command to the provided print streams.
 *
 */
public class PrintStreamedDescribeResultSetExtractor
  extends BasicPrintStreamed
  implements ResultSetExtractor
{
  
  //----------------------------------------------------------------------------
  // Static variables
  //----------------------------------------------------------------------------
  
  private static Logger logger = Logger.getLogger("DEBUG." + PrintStreamedDescribeResultSetExtractor.class.getName());
  
  //----------------------------------------------------------------------------
  // Static methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Constants
  //----------------------------------------------------------------------------
  
  private static final int COLUMN_COUNT = 4;
  private static final int META_DATA_COLUMN_NAME_INDEX = 4;
  private static final int META_DATA_COLUMN_TYPE_INDEX = 6;
  private static final int META_DATA_DEFAULT_VALUE_INDEX = 13;
  
  private static final int NULL_VALUE_DISPLAY_SIZE = 4;
  private static final int DEFAULT_VALUE_MINIMUM_DISPLAY_SIZE = 7;
  
  private static final int DISPLAY_COLUMN_NAME_INDEX = 0;
  private static final int DISPLAY_COLUMN_TYPE_INDEX = 1;
  private static final int DISPLAY_NULLABLE_INDEX = 2;
  private static final int DISPLAY_DEFAULT_VALUE_INDEX = 3;
  
  private static final int COLUMN_NAME_INDEX = 4;
  private static final int DATA_TYPE_INDEX = 5;
  private static final int COLUMN_TYPE_INDEX = 6;
  private static final int COLUMN_SIZE_INDEX = 7;
  private static final int DECIMAL_DIGITS_INDEX = 9;
  private static final int DEFAULT_VALUE_INDEX = 13;
  private static final int NULLABLE_INDEX = 18;
  
  //----------------------------------------------------------------------------
  // Instance variables
  //----------------------------------------------------------------------------

  private Map attributes;
  private int columnCount;
  private int columnNameColumnSize;
  private int columnTypeColumnSize;
  private int defaultValueColumnSize;
  private int[] displaySize;
  private String fieldSeparator = "|";
  private String headerSeparator = "+";
  private int[] justify;
  private String[] label;
  
  //----------------------------------------------------------------------------
  // Constructors
  //----------------------------------------------------------------------------
  
  /**
   * Constructs a new instance of
   * <code>PrintStreamedDescribeResultSetExtractor</code>.
   */
  public PrintStreamedDescribeResultSetExtractor()
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
    if(resultSet == null)
    {
      throw new IllegalArgumentException("resultSet is null");
    }
    ResultSetMetaData metaData = resultSet.getMetaData();
    if(logger.isDebugEnabled())
    {
      logger.debug("result set meta data " + (metaData == null ? "is" : "is not") + " null");
    }
    
    RowSetDynaClass rsdc = new RowSetDynaClass(resultSet);
    if(logger.isDebugEnabled())
    {
      logger.debug("row set dyna class " + (rsdc == null ? "is" : "is not") + " null");
    }
    DynaProperty[] properties = rsdc.getDynaProperties();
    if(logger.isDebugEnabled())
    {
      logger.debug("properties length: " + properties.length);
      for(int i = 0; i < properties.length; i++)
      {
        logger.debug("property name: " + properties[i].getName() + " class: " + properties[i].getType());
      }
    }
    this.configureOutput(metaData, rsdc);
    if(logger.isDebugEnabled())
    {
      logger.debug("output has been configured");
    }
    
    boolean found = false;
    boolean needsHeader = true;
    boolean needsFooter = true;
    
    List rows = rsdc.getRows();
    if(logger.isDebugEnabled())
    {
      logger.debug("row count: " + rows.size());
    }
    
    for(Iterator iter = rows.iterator(); iter.hasNext();)
    {
      found = true;
      if(needsHeader)
      {
        this.writeHeader();
        needsHeader = false;
      }
      DynaBean row = (DynaBean) iter.next();
      if(logger.isDebugEnabled())
      {
        logger.debug("row values");
        for(int i = 0; i < properties.length; i++)
        {
          logger.debug("property '" + properties[i].getName() + "': " + row.get(properties[i].getName()));
        }
      }
      
      int columnSize = -1;
      if(row.get("column_size") != null)
      {
        columnSize = ((Number) row.get("column_size")).intValue();
      }
      if(logger.isDebugEnabled())
      {
        logger.debug("column size: " + columnSize);
      }
      int decimalDigits = -1;
      if(row.get("decimal_digits") != null)
      {
        decimalDigits = ((Number) row.get("decimal_digits")).intValue();
      }
      if(logger.isDebugEnabled())
      {
        logger.debug("decimal digits: " + decimalDigits);
      }
      int dataType = ((Number) row.get("data_type")).intValue();
      String columnName = (String) row.get("column_name");
      String columnType = (String) row.get("type_name");
      String defaultValue = (String) row.get("column_def");
      String isNullable = (String) row.get("is_nullable");
      if(logger.isDebugEnabled())
      {
        logger.debug("column name: " + columnName);
        logger.debug("column type: " + columnType);
        logger.debug("default value: " + (defaultValue == null ? "null" : defaultValue));
        logger.debug("is nullable: " + isNullable);
      }
      this.print(this.getFieldSeparator());
      this.print(StringUtils.rightPad(columnName, this.displaySize[DISPLAY_COLUMN_NAME_INDEX]));
      this.print(this.getFieldSeparator());
      if(dataType == Types.DATE)
      {
        
      }
      else if(dataType == Types.TIME ||
              dataType == Types.TIMESTAMP
             )
      {
        columnType += "(" + columnSize + ")";
      }
      else
      {
        if(decimalDigits <= 0)
        {
          columnType += "(" + columnSize + ")";
        }
        else
        {
          columnType += "(" + columnSize + "," + decimalDigits + ")";
        }
      }
      this.print(StringUtils.rightPad(columnType, this.displaySize[DISPLAY_COLUMN_TYPE_INDEX]));
      this.print(this.getFieldSeparator());
      this.print(StringUtils.rightPad(isNullable, this.displaySize[DISPLAY_NULLABLE_INDEX]));
      this.print(this.getFieldSeparator());
      this.print(StringUtils.rightPad(defaultValue == null ? "NULL" : defaultValue, this.displaySize[DISPLAY_DEFAULT_VALUE_INDEX]));
      this.println(this.getFieldSeparator());
    }
    if(found)
    {
      this.writeFooter();
    }
    else
    {
      String tableName = (String) this.attributes.get(DescribeCommand.TABLE_NAME_KEY);
      if(!StringUtils.isEmpty(tableName))
      {
        this.print("Table or view '");
        this.print(tableName);
        this.println("' does not exist");
      }
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

  //----------------------------------------------------------------------------
  // Protected abstract methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Protected methods for use by subclasses
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Other methods
  //----------------------------------------------------------------------------

  private void configureOutput(final ResultSetMetaData metaData, final RowSetDynaClass rsdc)
    throws SQLException
  {
    if(metaData == null)
    {
      throw new IllegalArgumentException("metaData is null");
    }
    if(rsdc == null)
    {
      throw new IllegalArgumentException("rsds is null");
    }
    this.columnCount = COLUMN_COUNT;
    
    this.displaySize = new int[COLUMN_COUNT];
    this.displaySize[DISPLAY_COLUMN_NAME_INDEX] = metaData.getColumnDisplaySize(META_DATA_COLUMN_NAME_INDEX); // column name display size
    this.displaySize[DISPLAY_COLUMN_TYPE_INDEX] = metaData.getColumnDisplaySize(META_DATA_COLUMN_TYPE_INDEX); // column type display size
    this.displaySize[DISPLAY_NULLABLE_INDEX] = NULL_VALUE_DISPLAY_SIZE; // is nullable display size
    defaultValueColumnSize = metaData.getColumnDisplaySize(META_DATA_DEFAULT_VALUE_INDEX);
    if(defaultValueColumnSize < DEFAULT_VALUE_MINIMUM_DISPLAY_SIZE)
    {
      defaultValueColumnSize = DEFAULT_VALUE_MINIMUM_DISPLAY_SIZE;
    }
    this.displaySize[DISPLAY_DEFAULT_VALUE_INDEX] = defaultValueColumnSize;
    this.label = new String[]
    {   "Column Name"
      , "Data Type"
      , "Null"
      , "Default"
    };
    // Calculate more accurate column type size based on column type value
    // and column size value
    List rows = rsdc.getRows();
    if(logger.isDebugEnabled())
    {
      logger.debug("rows size: " + (rows == null ? "null" : "" + rows.size()));
    }
    for(Iterator iter = rows.iterator(); iter.hasNext();)
    {
      DynaBean row = (DynaBean) iter.next();
      if(logger.isDebugEnabled())
      {
        logger.debug("row == null? " + (row == null));
      }
      String typeName = (String) row.get("type_name");
      if(logger.isDebugEnabled())
      {
        logger.debug("type name: " + typeName);
      }
      int dataType = -1;
      if(row.get("data_type") != null)
      {
        dataType = ((Number) row.get("data_type")).intValue();
      }
      int columnSize = -1;
      if(row.get("column_size") != null)
      {
        columnSize = ((Number) row.get("column_size")).intValue();
      }
      int decimalDigits = -1;
      if(row.get("decimal_digits") != null)
      {
        decimalDigits = ((Number) row.get("decimal_digits")).intValue();
      }
      if(logger.isDebugEnabled())
      {
        logger.debug("data type: " + dataType);
        logger.debug("column size: " + columnSize);
        logger.debug("decimal digits: " + decimalDigits);
      }
      int columnSizeLength = 0;
      if(dataType == Types.TIME ||
         dataType == Types.TIMESTAMP
         )
      {
        columnSizeLength = Integer.toString(columnSize).length();
      }
      else
      {
        if(decimalDigits <= 0)
        {
          columnSizeLength = Integer.toString(columnSize).length();
        }
        else
        {
          columnSizeLength = Integer.toString(columnSize).length() + 1 + Integer.toString(decimalDigits).length();
        }
      }
      columnSizeLength += 2;
      columnSizeLength += typeName.length();
      if(this.displaySize[DISPLAY_COLUMN_TYPE_INDEX] < columnSizeLength)
      {
        this.displaySize[DISPLAY_COLUMN_TYPE_INDEX] = columnSizeLength;
      }
    }
    if(logger.isDebugEnabled())
    {
      logger.debug("calculated column type display size: " + this.displaySize[DISPLAY_COLUMN_TYPE_INDEX]);
    }
  }
  
  private void configureOutput(final ResultSetMetaData metaData) throws SQLException
  {
    this.columnCount = COLUMN_COUNT;
    
    this.displaySize = new int[COLUMN_COUNT];
    this.displaySize[DISPLAY_COLUMN_NAME_INDEX] = metaData.getColumnDisplaySize(META_DATA_COLUMN_NAME_INDEX); // column name display size
    this.displaySize[DISPLAY_COLUMN_TYPE_INDEX] = metaData.getColumnDisplaySize(META_DATA_COLUMN_TYPE_INDEX); // column type display size
    this.displaySize[DISPLAY_NULLABLE_INDEX] = NULL_VALUE_DISPLAY_SIZE; // is nullable display size
    defaultValueColumnSize = metaData.getColumnDisplaySize(META_DATA_DEFAULT_VALUE_INDEX);
    if(defaultValueColumnSize < DEFAULT_VALUE_MINIMUM_DISPLAY_SIZE)
    {
      defaultValueColumnSize = DEFAULT_VALUE_MINIMUM_DISPLAY_SIZE;
    }
    this.displaySize[DISPLAY_DEFAULT_VALUE_INDEX] = defaultValueColumnSize;
    this.label = new String[]
    {   "Column Name"
      , "Data Type"
      , "Null"
      , "Default"
    };
  }
  
  private String getFieldSeparator()
  {
    return this.fieldSeparator;
  }
  
  private String getHeaderSeparator()
  {
    return this.headerSeparator;
  }
  
  private void writeHeader()
  {
    this.println("");
    this.writeHeaderLine();

    for(int i = 1; i <= columnCount; i++)
    {
      this.print(this.getFieldSeparator());
      
      this.print(StringUtils.rightPad(this.label[i-1], this.displaySize[i-1]));
    }
    this.println(this.getFieldSeparator());
    
    this.writeHeaderLine();
  }
  
  private void writeHeaderLine()
  {
    for(int i = 1; i <= this.columnCount; i++)
    {
      this.print(this.getHeaderSeparator());
      this.print(StringUtils.repeat("-", this.displaySize[i-1]));
    }
    this.println(this.getHeaderSeparator());
  }

  private void writeFooter()
  {
    this.writeHeaderLine();
    this.println("");
  }

  //----------------------------------------------------------------------------
  // Member classes
  //----------------------------------------------------------------------------
  
}

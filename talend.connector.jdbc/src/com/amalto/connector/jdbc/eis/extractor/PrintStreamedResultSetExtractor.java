
package com.amalto.connector.jdbc.eis.extractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.amalto.connector.jdbc.eis.common.BasicPrintStreamed;

/**
 * Implementation of {@link ResultSetExtractor} that writes the result set to
 * the object's print streams.
 *
 */
public class PrintStreamedResultSetExtractor 
  extends BasicPrintStreamed
  implements ResultSetExtractor
{
  
  //----------------------------------------------------------------------------
  // Static variables
  //----------------------------------------------------------------------------
  
  private static Logger logger = Logger.getLogger(PrintStreamedResultSetExtractor.class);
  
  //----------------------------------------------------------------------------
  // Static methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Constants
  //----------------------------------------------------------------------------

  private static final int JUSTIFY_LEFT = 1;
  private static final int JUSTIFY_RIGHT = 2;
  private static final int MINIMUM_NULL_DISPLAY_SIZE = 4;
  private static final int TIMESTAMP_DISPLAY_SIZE = 21;

  
  //----------------------------------------------------------------------------
  // Instance variables
  //----------------------------------------------------------------------------

  private int columnCount;
  private int[] columnTypes;
  private int[] displaySizes;
  private int[] justifys;
  private String[] labels;
  private String headerSeparator = "+";
  private String fieldSeparator = "|";
  
  //----------------------------------------------------------------------------
  // Constructors
  //----------------------------------------------------------------------------
  
  /**
   * Constructs a new instance of
   * <code>PrintStreamedResultSetExtractor</code>.
   */
  public PrintStreamedResultSetExtractor()
  {
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
    this.columnCount = metaData.getColumnCount();
    this.configureOutput(metaData);
    this.println("");

    int rowCount = 0;
    if(this.columnCount > 0)
    {
      // Write the column headers
      this.writeHeader();
      
      // Write the rows
      while(resultSet.next())
      {
        ++rowCount;
        for(int i = 1; i <= this.columnCount; i++)
        {
          this.print(this.getFieldSeparator());
          int displaySize = this.displaySizes[i-1];
          switch(this.columnTypes[i-1])
          {
            /*
            case Types.TIMESTAMP:
            {
              Timestamp value = resultSet.getTimestamp(i);
              if(this.justify[i-1] == JUSTIFY_LEFT)
              {
                this.print(StringUtils.rightPad(value.toString(), displaySize));
              }
              else
              {
                this.print(StringUtils.leftPad(value.toString(), displaySize));
              }
              break;
            }
             */
            case Types.ARRAY:
            case Types.BINARY:
            case Types.BLOB:
            case Types.CLOB:
            case Types.DATALINK:
            case Types.DISTINCT:
            case Types.JAVA_OBJECT:
            case Types.LONGVARBINARY:
            case Types.LONGVARCHAR:
            case Types.NULL:
            case Types.OTHER:
            case Types.REF:
            case Types.STRUCT:
            case Types.VARBINARY:
            {
              this.print(StringUtils.rightPad(" ", displaySize));
              break;
            }
            default:
            {
              Object value = resultSet.getObject(i);
              if(resultSet.wasNull())
              {
                if(displaySize >= MINIMUM_NULL_DISPLAY_SIZE)
                {
                  this.print(StringUtils.rightPad("NULL", displaySize));
                }
                else
                {
                  this.print(StringUtils.repeat(" ", displaySize));
                }
              }
              else
              {
                if(this.justifys[i-1] == JUSTIFY_LEFT)
                {
                  this.print(StringUtils.rightPad(value.toString(), displaySize));
                }
                else
                {
                  this.print(StringUtils.leftPad(value.toString(), displaySize));
                }
              }
            }
          }
        }
        this.println(this.getFieldSeparator());
      }

      this.writeFooter();
    }
    this.println(rowCount + " row(s)");
    this.println("");
    
    return null;
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

  private void configureOutput(final ResultSetMetaData metaData) throws SQLException
  {
    this.displaySizes = new int[this.columnCount];
    this.justifys = new int[this.columnCount];
    this.labels = new String[this.columnCount];
    this.columnTypes = new int[this.columnCount];
    for(int i = 1; i <= this.columnCount; i++)
    {
      int columnType = metaData.getColumnType(i);
      this.columnTypes[i-1] = columnType;
      
      String label = metaData.getColumnLabel(i);
      this.labels[i-1] = label;
      
      int displaySize = metaData.getColumnDisplaySize(i);
      if(columnType == Types.TIMESTAMP)
      {
        displaySize = TIMESTAMP_DISPLAY_SIZE;
      }
      this.displaySizes[i-1] = (displaySize > label.length() ? displaySize : label.length());
      
      this.justifys[i-1] = JUSTIFY_LEFT;
      switch(columnType)
      {
        case Types.BIGINT:
        case Types.DECIMAL:
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.INTEGER:
        case Types.NUMERIC:
        case Types.REAL:
        case Types.SMALLINT:
        {
          this.justifys[i-1] = JUSTIFY_RIGHT;
          break;
        }
        default:
        {
          this.justifys[i-1] = JUSTIFY_LEFT;
          break;
        }
      }
      
      String columnTypeName = metaData.getColumnTypeName(i);
    }
  }

  private void writeFooter()
  {
    this.writeHeaderLine();
    this.println("");
  }
  
  private void writeHeader()
  {
    this.writeHeaderLine();

    for(int i = 1; i <= columnCount; i++)
    {
      this.print(this.getFieldSeparator());
      
      this.print(StringUtils.rightPad(this.labels[i-1], this.displaySizes[i-1]));
    }
    this.println(this.getFieldSeparator());
    
    this.writeHeaderLine();
  }
  
  private void writeHeaderLine()
  {
    for(int i = 1; i <= this.columnCount; i++)
    {
      this.print(this.getHeaderSeparator());
      this.print(StringUtils.repeat("-", this.displaySizes[i-1]));
    }
    this.println(this.getHeaderSeparator());
  }

  private String getFieldSeparator()
  {
    return this.fieldSeparator;
  }
  
  private String getHeaderSeparator()
  {
    return this.headerSeparator;
  }

  public void setAttribute(String key, Object value) {
		// TODO Auto-generated method stub
		
  }
  
  //----------------------------------------------------------------------------
  // Member classes
  //----------------------------------------------------------------------------
  
}

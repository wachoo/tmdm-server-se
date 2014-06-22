
package com.amalto.connector.jdbc.eis.common;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A basic implementation of the {@link PrintStreamed} interface.
 *
 */
public class BasicPrintStreamed implements PrintStreamed
{
  
  //----------------------------------------------------------------------------
  // Static variables
  //----------------------------------------------------------------------------
  
  private static Logger logger = Logger.getLogger(BasicPrintStreamed.class);
  
  //----------------------------------------------------------------------------
  // Static methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Constants
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Instance variables
  //----------------------------------------------------------------------------

  private List printStreams;
  
  private StringBuffer localBuffer;
  
  //----------------------------------------------------------------------------
  // Constructors
  //----------------------------------------------------------------------------
  
  /**
   * Constructs a new instance of
   * <code>AbstractPrintStreamed</code>.
   */
  public BasicPrintStreamed()
  {
    this.printStreams = new ArrayList();
    localBuffer =new StringBuffer();
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
  public void addPrintStream(final PrintStream printStream)
  {
    if(!this.printStreams.contains(printStream))
    {
      this.printStreams.add(printStream);
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void removePrintStream(final PrintStream printStream)
  {
    this.printStreams.remove(printStream);
  }
  
  //----------------------------------------------------------------------------
  // Protected abstract methods
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------------------------
  // Protected methods for use by subclasses
  //----------------------------------------------------------------------------

  /**
   * Prints the specified line to this object's print streams.
   *
   * @param line the line to print
   */
  protected void print(final String line)
  {
    for(Iterator iter = this.streamIterator(); iter.hasNext();)
    {
      PrintStream printStream = (PrintStream) iter.next();
      printStream.print(line);
      localBuffer.append(line);
    }
  }
  
  /**
   * Prints the specified line to this object's print streams.
   *
   * @param line the line to print
   */
  protected void println(final String line)
  {
    for(Iterator iter = this.streamIterator(); iter.hasNext();)
    {
      PrintStream printStream = (PrintStream) iter.next();
      printStream.println(line);
      localBuffer.append(line).append("\n");
    }
  }

  /**
   * Returns an iterator of this object's print streams.
   *
   * @return the iterator
   */
  protected Iterator streamIterator()
  {
    return this.printStreams.iterator();
  }


  
  //----------------------------------------------------------------------------
  // Other methods
  //----------------------------------------------------------------------------
  public StringBuffer getLocalBuffer() {
		return localBuffer;
  }
  
  public void flushLocalBuffer() {
	   this.localBuffer=null;
	   this.localBuffer=new StringBuffer();
   }
  //----------------------------------------------------------------------------
  // Member classes
  //----------------------------------------------------------------------------
  
}

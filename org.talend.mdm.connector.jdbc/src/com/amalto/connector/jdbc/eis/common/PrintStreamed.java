
package com.amalto.connector.jdbc.eis.common;

import java.io.PrintStream;

/**
 * Defines operations for adding and removing print streams for an object to
 * print to.
 *
 */
public interface PrintStreamed
{
  /**
   * Adds the specified print stream to this object's print streams.
   *
   * @param printStream the print stream to be added
   */
  public void addPrintStream(PrintStream printStream);
  
  /**
   * Removes the specified print stream to this object's print streams.
   *
   * @param printStream the print stream to be added
   */
  public void removePrintStream(PrintStream printStream);
}

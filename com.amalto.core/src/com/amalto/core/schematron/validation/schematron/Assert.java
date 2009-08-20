/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/Assert.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation.schematron;

/**
 * Represents a Schematron assert element 
 * 
 * example:
 * <assert test="count(ear)=2">A <name/> element should contain two <emph>ear</emph> elements.</assert> 
 *
 * @author  Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class Assert
{
  
  private String test_;
  private String message_;
  private String diagnostics_;
 
  /**
   * Returns the test attribute 
   */
  public String getTest()
  {
    return test_;
  }
  
  /**
   * Sets the test attribute 
   */
  public void setTest( String newTest )
  {
    test_ = newTest;
  }
  
  /**
   * Returns the message for to the element 
   */
  public String getMessage()
  {
    return message_;
  }
  
  /**
   * Sets the message for to the element 
   */
  public void setMessage( String newMessage )
  {
    message_ = newMessage;
  }

  
  /**
   * Returns the diagnostics list 
   */
  public String getDiagnostics()
  {
    return diagnostics_;
  }
  
  /**
   * Sets the diagnostics list 
   */
  public void setDiagnostics( String newDiagnostics )
  {
    diagnostics_ = newDiagnostics;
  }
  
  
}

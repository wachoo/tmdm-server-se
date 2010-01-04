/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/ActivePattern.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.validation.schematron;



/**
 * Represents a Schematron phase 
 * <active pattern="some"> element
 *
 * @author  Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class ActivePattern
{
  
  private String pattern_;
  

  /**
   * Returns the active pattern name
   */
  public String getPattern()
  {
    return pattern_;
  }
  
  /**
   * Sets the active pattern name
   */
  public void setPattern( String pattern )
  {
    pattern_ = pattern;
  }
  
}

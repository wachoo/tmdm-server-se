/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/Rule.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation.schematron;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a Schematron rule element 
 * 
 * From the Schematron specification:
 *
 * example:
 * <rule context="dog">
 *   <assert test="count(ear) = 2">A 'dog' element should contain two 'ear' elements.</assert>
 *   <report test="bone">This dog has a bone.</report>
 * </rule>
 *
 * @author  Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class Rule 
{

  private String context_;
  private ArrayList asserts_ = new ArrayList ();
  private ArrayList reports_ = new ArrayList ();

  

  /**
   * Returns the context of the pattern
   */
  public String getContext()
  {
    return context_;
  }
  
  /**
   * Sets the context of the pattern
   */
  public void setContext( String newContext )
  {
    context_ = newContext;
  }
        
  /**
   * Returns the list of the assertion rules 
   */
  public List getAssert()
  {
    return asserts_;
  }
  
  /**
   * Sets the the list of the assertion rules 
   */
  public void setAssert(Collection newAsserts)
  {
    asserts_ = new ArrayList();
    asserts_.addAll ( newAsserts );
  }
  
  /**
   * Add an assert rule 
   */
  public void addAssert(Assert a)
  {
    asserts_.add ( a );
  }
  
  
  /**
   * Returns the list of the report rules 
   */
  public List getReport()
  {
    return reports_;
  }
  
  /**
   * Sets the list of the report rules 
   */
  public void setReport(Collection newReports)
  {
    reports_ = new ArrayList();
    reports_.addAll ( newReports );
  }
  
  /**
   * Add a report rule 
   */
  public void addReport(Report r)
  {
    reports_.add ( r );
  }
  
      
}

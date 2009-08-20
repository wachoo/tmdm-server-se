/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/Pattern.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
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
 * Represents a Schematron pattern 
 * 
 * @author  Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class Pattern
{
  
  private String name_;
  private String id_;
  private ArrayList rules_ = new ArrayList ();
  

  /**
   * Returns the id of the pattern
   */
  public String getId()
  {
    return id_;
  }
  
  /**
   * Sets the id of the pattern
   */
  public void setId( String newId )
  {
    id_ = newId;
  }
     
  /**
   * Returns the name of the pattern
   */
  public String getName()
  {
    return name_;
  }
  
  /**
   * Sets the name of the pattern
   */
  public void setName( String newName )
  {
    name_ = newName;
  }
      
  /**
   * Returns the list of rules 
   */
  public List getRule()
  {
    return rules_;
  }
  
  /**
   * Sets the list of rules 
   */
  public void setRule(Collection newRules)
  {
    rules_ = new ArrayList();
    rules_.addAll ( newRules );
  }
  
  /**
   * Add a rule to the list 
   */
  public void addRule(Rule r)
  {
    rules_.add ( r );
  }
  
  
}

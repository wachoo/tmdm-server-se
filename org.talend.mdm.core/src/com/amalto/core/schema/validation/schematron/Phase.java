/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/Phase.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schema.validation.schematron;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Represents a Schematron phase element
 *
 * Example:
 * <phase id="basicValidation">
 *   <active pattern="text" />
 *   <active pattern="tables" />
 *   <active pattern="attributePresence" />
 * </phase>
 *
 *
 * @author  Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class Phase
{
  
  private String id_;
  private ArrayList active_ = new ArrayList ();
  

  /**
   * Returns the id of the phase
   */
  public String getId()
  {
    return id_;
  }
  
  /**
   * Sets the id of the phase
   */
  public void setId( String newId )
  {
    id_ = newId;
  }
      
  /**
   * Returns the list of active patterns
   */
  public List getActive()
  {
    return active_;
  }
  
  /**
   * Sets the list of active patterns
   */
  public void setActive(Collection newActivePatterns)
  {
    active_ = new ArrayList();
    active_.addAll ( newActivePatterns );
  }
  
  /**
   * Add a pattern to the list of active patterns
   */
  public void addActive(ActivePattern p)
  {
    active_.add ( p );
  }
  
}

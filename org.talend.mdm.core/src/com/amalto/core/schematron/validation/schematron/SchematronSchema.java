/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/SchematronSchema.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
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

import com.amalto.core.schematron.validation.Schema;
import com.amalto.core.schematron.validation.Validator;


/**
 * Represents a Schematron Schema 
 *
 * Specification:
 * http://www.ascc.net/xml/resource/schematron/Schematron2000.html
 * 
 * @author  Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class SchematronSchema implements Schema
{
  
  private String title_;
  private ArrayList patterns_ = new ArrayList();
  private ArrayList phases_  = new ArrayList();
  
   /**
   * Returns the message for to the element 
   */
  public String getTitle()
  {
    return title_;
  }
  
  /**
   * Sets the message for to the element 
   */
  public void setTitle( String newTitle )
  {
    title_ = newTitle;
  }
    
  /**
   * Returns a list of the patterns which
   * contain messages that failed during validation
   */
  public List getPattern()
  {
    return patterns_;
  }
  
  /**
   * Sets the list of the patterns which
   * contain messages that failed during validation
   */
  public void setPattern(Collection newPatterns)
  {
    patterns_ = new ArrayList();
    patterns_.addAll ( newPatterns );
  }
  
  /**
   * Add a pattern to the list
   */
  public void addPattern(Pattern p)
  {
    patterns_.add ( p );
  }
  
    
  /**
   * Returns the list of schema phases 
   */
  public List getPhase()
  {
    return phases_;
  }
  
  /**
   * Sets the list of schema phases 
   */
  public void setPhase(Collection newPhases)
  {
    phases_ = new ArrayList();
    phases_.addAll ( newPhases );
  }
  
  /**
   * Add a pattern to the list
   */
  public void addPhase(Phase p)
  {
    phases_.add ( p );
  }
  
  public Validator newValidator () throws InstantiationException
  {
    return new SchematronValidator( this );
  }  

}

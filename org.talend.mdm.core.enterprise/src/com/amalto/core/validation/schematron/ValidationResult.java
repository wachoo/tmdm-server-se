/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/ValidationResult.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.validation.schematron;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.amalto.core.validation.Violation;

/**
 * Represents the result of a Schematron validation process
 * 
 * <validationResult>
 *   list of <pattern> elements with <rule> subelements
 * </validationResult>
 *
 * @author  Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class ValidationResult
{
  
  private ArrayList patterns_ = new ArrayList();
  
 
  /**
   * Returns a list of the patterns which
   * contain rules that failed during validation
   */
  public List getPattern()
  {
    return patterns_;
  }
  
  /**
   * Sets the list of the patterns which
   * contain rules that failed during validation
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
  
  public boolean isEmpty ()
  {
    return patterns_.isEmpty ();
  }
  
  /**
   * adds all errors to a sorted list
   * Key is XPath of each error location
   *
   * @todo implement this method !!!
   * @return SortedSet
   */
  public List toList()
  {
    
    if ( isEmpty() ) return Collections.EMPTY_LIST;
    
    List violations = new LinkedList();
    
    Iterator piter = getPattern().iterator();
    while (piter.hasNext ())
    {
      Pattern pattern = (Pattern)piter.next ();
      // System.out.println("Pattern name: " + pattern.getName() + ", id: " + pattern.getId() );
      Iterator ruleIter = pattern.getRule().iterator();
      while (ruleIter.hasNext ())
      {
        Rule rule = (Rule)ruleIter.next ();
        // System.out.println("    Rule name: " + rule.getContext() );

        Iterator assertIter = rule.getAssert().iterator();
        while (assertIter.hasNext ())
        {
          Assert assertion = (Assert)assertIter.next ();

          // add the next assert to the violations set
          Violation v = new Violation();
          v.setPath( rule.getContext() );
          v.setMessage( assertion.getMessage() );
          violations.add( v );
          // System.out.println("        Assert test: " + assertion.getTest() + ", message: " + assertion.getMessage() );
        }

        Iterator reportIter = rule.getReport().iterator();
        while (reportIter.hasNext ())
        {
          Report report = (Report)reportIter.next ();

          // add the next report to the violations set
          Violation v = new Violation();
          v.setPath( rule.getContext() );
          v.setMessage( report.getMessage() );
          violations.add( v );
          // System.out.println("        Report test: " + report.getTest() + ", message: " + report.getMessage() );
        }
      }
    }
    return violations;
  }
  
}

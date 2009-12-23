/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/SchematronValidator.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation.schematron;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.w3c.dom.*;


import com.amalto.core.schematron.validation.Validator;
import com.amalto.core.util.Util;

/**
 * An object representing a single Schematron schema, used to validate
 * multiple XML instances.
 *
 * This implementation can validate JavaBeans and DOM documents.
 * It is based exclusively on the JXPath library from the Jakarta Commons project.
 * See http://jakarta.apache.org/commons/
 *
 * @author Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class SchematronValidator implements Validator
{

  
  /** The schema instance for this Validator
   * It is initialized once when a new Validator instance
   * is created and used multiple times for validating
   * different JavaBeans/DOM objects against the schema
   */
  private SchematronSchema schema_;

  /**
   * lookup map, with phase id keys.
   * Used for efficiency when validating by phase
   */
  private Map phaseMap_ = new HashMap();
  
  
  /**
   * the schema name space prefix used in the schema document
   */
  private String schemaPrefix_;
  
  /**
   * the default schema name space prefix 
   */
  private String defaultSchemaPrefix_ = "sch";

  /*
   * Schematron Phase property
   */
  private String phaseProperty_  = null;
  
  
  /*
   * private logger
   */
  private Logger logger = setupLogger();
  
    
  
  //
  // Constructors
  //

  
  /**
   * Constructs a new Validator object for a given Schematron schema.
   *
   * @param schema 
   *        The Schematron schema
   */
  public SchematronValidator (SchematronSchema schema)
  {
    schema_ = schema;
    preparePhaseMap();
  }




  //
  // helper methods for the constructors
  //

  /**
   * initialize logger
   */
  protected Logger setupLogger()
  {
    Logger logger = Logger.getLogger( "XmlForm" );
    return logger;
  }
  

  protected void preparePhaseMap()
  {
    Map patternMap = new HashMap();
    
    Iterator ptiter = schema_.getPattern().iterator();
    while (ptiter.hasNext())
    {
      Pattern pattern = (Pattern) ptiter.next();
      patternMap.put( pattern.getId(), pattern );
    }
    
    Iterator phiter = schema_.getPhase().iterator();
    while (phiter.hasNext())
    {
      Phase phase = (Phase) phiter.next();
      List activePatterns = new ArrayList();
      phaseMap_.put( phase.getId(), activePatterns );
      
      Iterator activeIter = phase.getActive().iterator();
      while (activeIter.hasNext())
      {
        ActivePattern active = (ActivePattern) activeIter.next();
        activePatterns.add( patternMap.get( active.getPattern() ) );
      }
    }
    
    
  }

  //
  // public methods
  //




  /**
   * Performs validation of the passed JavaBean or DOM object.
   *
   * This method tries to find the "phase" attribute
   * and runs the active patterns for the phase.
   * If phase not found, the method will try to match all patterns
   *
   *
   * @param jbean The JavaBean or DOM object to be validated.
   * @param props Properties which control different aspects of the 
   * validation process. This method only looks for the phase property.
   * Another implementation may use other.
   *
   * @return A Result object which represents the result
   *         of the validation.
   */
  public List validate( Object jbean ) 
  {
    List patterns = null;
    if (phaseProperty_ != null) 
    {
      patterns = getPatternsForPhase( phaseProperty_ );
      logger.fine(" Validating for phase: " + phaseProperty_);
    }
    else
    {
      patterns = schema_.getPattern();
      logger.fine(" Validating all patterns. No phase provided ");
    }
    
    ValidationResult vres = new ValidationResult();

    if (patterns != null)
    {
      // create the JXPathContext
      // which will be used to validate each rule
      JXPathContext jxpContext = JXPathContext.newContext( jbean );

      Iterator iter = patterns.iterator ();
      while (iter.hasNext ())
      {
        Pattern resultPattern = evalPattern( jxpContext, (Pattern) iter.next());
        // if the resultPattern is null,
        // then it passed successfully
        if ( resultPattern != null) vres.addPattern( resultPattern );
      }
    }
    
    return vres.toList();
  }

  /**
   * return the list of patterns listed
   * as <active/> elements of <phase/>
   *
   * @param phase name of the phase
   * @return List of patterns
   */
  protected List getPatternsForPhase( String phase )
  {
    return (List) phaseMap_.get( phase );
  }
  
  /** 
  * Returns pattern with rules which failed during validation.
  * The context attribute of each rule in the result pattern
  * contains the exact location of the failed element
  * unlike the context attribute of the original pattern which
  * is an XSLT production pattern
  *
  * @param jxpContext The JXPathContext being validated
  * @param pattern The production schema pattern to be evaluated
  * @return pattern with rules wich failed during validation.
  */
  protected Pattern evalPattern( JXPathContext jxpContext, Pattern pattern)
  {
    // copy attributes
    Pattern resultPattern = new Pattern();
    resultPattern.setName( pattern.getName() );
    resultPattern.setId( pattern.getId() );

    // evaluate rules
    Iterator iter = pattern.getRule().iterator();
    while (iter.hasNext())
    {
      List failedRules = evalRule(jxpContext, (Rule) iter.next () );
      // if there were failed rules
      // add them to the list of other failed rules
      if (failedRules.size () > 0) 
      {
        failedRules.addAll ( resultPattern.getRule() );
        resultPattern.setRule ( failedRules );
      }
    }
    
    // if there are no failed rules return null
    if (resultPattern.getRule().size() == 0) return null;
    else return resultPattern;
  }
  
  
  /** 
  * Returns rules with asserts or reports which failed during validation.
  * The context attribute of each rule in the result pattern
  * contains the exact location of the failed element
  * unlike the context attribute of the original pattern which
  * is an XSLT production pattern
  *
  * @param jxpContext The JXPath context being validated
  * @param rule The original pattern rule to be evaluated
  * @return pattern with rules wich failed during validation.
  */
  protected List evalRule( JXPathContext jxpContext, Rule rule )
  {
    Iterator pointerIter = jxpContext.iteratePointers( rule.getContext() );
    List failedRules = new ArrayList();
    
    while ( pointerIter.hasNext() )
    {

      Pointer ptr = (Pointer) pointerIter.next ();

      // prepare result Rule
      Rule nextFailedRule = new Rule();
      nextFailedRule.setContext( ptr.asPath() );

      // switch to the context of the rule
      //JXPathContext localJxpContext = JXPathContext.newContext( jxpContext, ptr.getValue() );
      JXPathContext localJxpContext = jxpContext.getRelativeContext(ptr);
      // evaluate asserts
      Iterator assertIter = rule.getAssert().iterator();
      while (assertIter.hasNext())
      {
        Assert assertion = (Assert) assertIter.next();
        // if an assert test fails, then it should be added
        // to the result
        boolean passed = evalTest( localJxpContext, assertion.getTest() );
        if (!passed) 
        {
           parseMsg(assertion, localJxpContext);
           nextFailedRule.addAssert ( assertion );
        }
      }
      
      // evaluate reports
      Iterator reportIter = rule.getReport().iterator();
      while (reportIter.hasNext())
      {
        Report report = (Report) reportIter.next();
        // if a report test passes, then it should be added
        // to the result
        boolean passed = evalTest( localJxpContext, report.getTest() );
        if (passed) 
        {
           parseMsg(report, localJxpContext);
           nextFailedRule.addReport ( report );
        }
      }
      
      // if the nextFailedRule is non empty,
      // then add it to the list of failed rules
      if (nextFailedRule.getAssert().size() > 0 || nextFailedRule.getReport().size() > 0)
      {
        failedRules.add( nextFailedRule );
      }
    }
    
    return failedRules;
  }
  
  /**
   * parse message and replace name element with element of instance.
   * @param assertion
   * @param localJxpContext
   */
  private void parseMsg(Assert assertion, JXPathContext localJxpContext) {
     try {
        Document dom = Util.parse("<msg>" + assertion.getMessage() + "</msg>");
        NodeList nlist = dom.getFirstChild().getChildNodes();
        
        for(int i = 0; i < nlist.getLength(); i++) {
           Node node = nlist.item(i);
           
           if("name".equals(node.getNodeName())) {
              Node nameNode = node.getAttributes().getNamedItem("path");
              String xpath = nameNode != null ? nameNode.getNodeValue() : ".";
              node.setTextContent(
                 ((Node)localJxpContext.getPointer(xpath).getNode()).getNodeName());
           }
        }
        
        assertion.setMessage(Util.nodeToString(dom));
     }
     catch(Exception e) {
        e.printStackTrace();
     }
  }

  
  /** 
  * Test an XPath expression in a context
   *
  * @param jxpContext The JXPath context being validated
  * @param String The XPath expression
  * @return boolean result of evaluation
  */
  protected boolean evalTest( JXPathContext jxpContext, String test )
  {
    Boolean passed = (Boolean) jxpContext.getValue( test, Boolean.class);
    return passed.booleanValue ();
  }
  
  /**
   * @param property name
   * @return the property value
   * @throws IllegalArgumentException when the property is not supported
   */
  public java.lang.Object getProperty (java.lang.String property) throws java.lang.IllegalArgumentException
  {
    if (property.equals ( Validator.PROPERTY_PHASE ) ) return phaseProperty_;
    else throw new IllegalArgumentException(" Property " + property + " is not supported");
  }  
  
  /**
   * @param property name
   * @param value property value
   * @throws IllegalArgumentException when the property is not supported
   */
  public void setProperty (java.lang.String property, java.lang.Object value) throws java.lang.IllegalArgumentException
  {
    if ( property.equals ( Validator.PROPERTY_PHASE ) && ( value == null || (value instanceof String)) ) 
    {
      phaseProperty_ = (String) value;
    }
    else 
      throw new IllegalArgumentException(" Property " + property + " is not supported or value is invalid");
  }
  
}

/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/Validator.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.validation;

import java.util.List;

/**
 *
 * Created on Sat, April 6, 2002
 *
 * @author  ivelin@apache.org
 */
public interface Validator
{

  /**
   * Validates an instance against a schema and returns a set of errors.
   *
   * Validator is not thread safe and is not re-entrant.
   *
   * @param instance The instance can be either a DOM node or a JavaBean.
   * @return SortedSet of ValidityViolation(s). The set is sorted by
ValidityViolation.getPath()
   * 
   * @throws RuntimeException if the validation process fails
   * Should not happen in a normal environment.
   */
  List validate(Object instance);

  /**
   * This property can be used for partial document validation.
   * The concept is borrowed from the Schematron schema
   * Not all schemas support partial validation
   */
  public String PROPERTY_PHASE = "http://xml.apache.org/cocoon/validator/phase";

  /**
   * @param property name
   * @param value property value
   * @throws IllegalArgumentException when the property is not supported
   */
  public void setProperty(java.lang.String property, java.lang.Object value)
                 throws java.lang.IllegalArgumentException;
  
  /**
   * @param property name
   * @return the property value
   * @throws IllegalArgumentException when the property is not supported
   */
  public java.lang.Object getProperty(java.lang.String property)
                 throws java.lang.IllegalArgumentException;
  
                 
                 
}

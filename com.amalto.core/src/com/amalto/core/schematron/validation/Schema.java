/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/Schema.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation;

/**
 *
 * Created on Sat, April 6, 2002
 *
 * @author  ivelin@apache.org
 */
public interface Schema
{
  public Validator newValidator() throws InstantiationException;
}

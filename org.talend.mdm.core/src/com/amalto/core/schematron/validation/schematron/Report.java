/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/schematron/Report.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation.schematron;

/**
 * Represents a Schematron report element 
 *
 * <report test="when">message</report> 
 * is equivalent to
 * <assert test="not(when)">message</assert> 
 * 
 * example:
 * <report test="bone">This dog has a bone.</report>
 *
 * @author  Ivelin Ivanov, ivelin@acm.org, ivelin@iname.com
 */
public class Report extends Assert
{
}

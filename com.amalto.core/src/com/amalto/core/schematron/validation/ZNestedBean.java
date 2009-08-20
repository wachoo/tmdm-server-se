/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/ZNestedTestBean.java,v 1.1 2003/04/19 16:18:24 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/04/19 16:18:24 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation;

  /**
   * just a test bean
   */
  public class ZNestedBean 
    {
	  private int value;
	  public ZNestedBean(int v){
		  value=v;
	  }
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	  
    }

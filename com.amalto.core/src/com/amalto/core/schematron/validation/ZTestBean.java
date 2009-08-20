/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/ZTestBean.java,v 1.2 2003/04/19 16:18:25 ivelin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/04/19 16:18:25 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation;

import java.util.ArrayList;

  /**
   * just a test bean
   */
public class ZTestBean {
  private String name = "dog";
  private String scope = "galaxy";
  private int count = 0;
  private int AA=30;
  private int BB=20;
  




  
  public int getAA() {
	return AA;
}
public void setAA(int aa) {
	AA = aa;
}
public int getBB() {
	return BB;
}
public void setBB(int bb) {
	BB = bb;
}
public String getName() {
    return name;
  }
  public void setName(String newName) {
    name = newName;
  }

  public void setScope(String newScope) {
    scope = newScope;
  }

  public String getScope() {
    return scope;
  }


  public int getCount() {
    return count;
  }

  public void incrementCount() {
    count++;
  }
  
  }

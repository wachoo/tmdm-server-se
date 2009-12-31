/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/Violation.java,v 1.2 2003/04/19 16:18:25 ivelin Exp $
 * $Revision: 1.2 $
 * $Date: 2003/04/19 16:18:25 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schema.validation;

/**
 * Encapsulates an error condition which was triggered
 * by a violation of the document validity during
 * validation
 * 
 * @author  ivelin@apache.org
 */
public class Violation implements java.lang.Comparable
{
  
    public Violation()
    {
    }

    public Violation(String xpath, String message)
    {
      xpath_ = xpath;
      message_ = message;
    }
  
    public Violation(String message)
    {
      this("",message);
    }

    /** 
    * @return the XPath location of the Violation
    */
    public String getPath()
    {
      return xpath_;
    }
  
    /** 
    * set the XPath location of the Violation
    */
    public void setPath( String xpath )
    {
      xpath_ = xpath;
    }

    
    /**
     * @return the error message
     */
    public String getMessage ()
    {
      return message_;
    }    
    
    /**
     * set the error message
     */
    public void setMessage ( String message )
    {
      message_ = message;
    }    

    public boolean equals(Object obj)    
    {
      if (obj == null) return false;
      if (obj == this) return true;
      if ( !(obj instanceof Violation) ) 
          throw new java.lang.IllegalArgumentException( "Can only compare to a Violation object" );
      Violation v = (Violation) obj;
      if 
        (
        getPath().equals ( v.getPath() ) 
        && 
        getMessage().equals ( v.getMessage() ) 
        )
        return true;
      else return false;
    }

    public int hashCode()    
    {
      return (getPath().hashCode () ^ getMessage().hashCode());
    }

    public int compareTo(Object obj)    
    {
      if (obj == null) return 1;
      if (obj == this) return 0;
      if ( !(obj instanceof Violation) ) 
          throw new java.lang.IllegalArgumentException( "Can only compare to a Violation object" );
      Violation v = (Violation) obj;
      int primaryResult = getPath().compareTo ( v.getPath () );
      if (primaryResult != 0) return primaryResult;
      else 
      {
        if (getMessage () == null)
        {
          if (v.getMessage() == null) return 0;
          else return -1;
        }
        else return (getMessage().compareTo( v.getMessage () ) );
      }
    }
    
    private String xpath_;
    private String message_;
  
}

/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/SchemaFactory.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation;

import org.xml.sax.InputSource;

/**
 * 
 * Responsible for creating new instances of Schemas
 * for different Schema languages
 *
 * @author  ivelin@apache.org
 */
public abstract class SchemaFactory
{

  public static String NAMESPACE_SCHEMATRON = "http://www.ascc.net/xml/schematron";
  
  /** Creates a new instance of ValidatorFactory */
  public SchemaFactory ()
  {
  }
  
  /**
   * This method creates an instance of a ValidatorFactory
   * using the JDK 1.3 META-INF/services mechanism.
   * The idea is borrowed from JARV
   * http://iso-relax.sourceforge.net/apiDoc/org/iso_relax/verifier/VerifierFactory.html
   *
   * @param ns the namespace of the schema language
   * @return ValidatorFactory
   * @throws InstantiationException when a factory could not be created
   */
  public static SchemaFactory lookup(java.lang.String ns)
                                   throws InstantiationException
  {
    // currently hardcoded implementation for Schematron
    // until another schema validator is implemented
    /* @todo create SchematronValidatorFactory */  
    if ( ns.equals ( NAMESPACE_SCHEMATRON ) )
      return new com.amalto.core.schematron.validation.schematron.SchematronFactory();
    return null;
  }


  /**
   * Loads and compiles a Schema instance
   *
   * @param InputSource the SAX input source containing the Schema document
   * @return Schema the compiled schema instance
   * @throws InstantiationException when the Schema could not be loaded or compiled
   */  
  public abstract Schema compileSchema(InputSource is)
                              throws InstantiationException;

}

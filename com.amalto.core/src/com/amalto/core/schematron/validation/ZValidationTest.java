/* 
 * $Header: /cvsroot/freebuilder/XMLForm/src/org/xmlform/validation/ZValidationTest.java,v 1.1 2003/03/28 14:11:10 ivelin Exp $
 * $Revision: 1.1 $
 * $Date: 2003/03/28 14:11:10 $
 *
 * ====================================================================
 * This is Open Source Software, distributed
 * under the Apache Software License, Version 1.1
 */

package com.amalto.core.schematron.validation;

// Java classes
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * 
 * Test class for the Validation API 
 *
 * Takes 2 command line arguments.
 *
 * First is the location of a Schematron Schema file
 * Second is the validation phase to use
 *
 */
public class ZValidationTest
{
  
   /**
    * Method main
    */
   public static void main(String args[])
      throws Exception
   {    
      System.out.println("\n=== Java based Schematron validation ===");
    
//      if (args.length < 1) {
//         System.err.println("Usage: java Schematron <schema.xml> " +
//                            "[phase] ");
//         return;
//      }

      // use custom schema
      File file = new File( "I:/MDM/learn/schematron/XMLForm/src/org/xmlform/validation/zxmlform-sch-report-test.xml" );
      if ( !file.exists () ) throw new Exception("Error: schema file not found !");
     InputStream istrm = new FileInputStream ( file );
     InputSource is = new InputSource ( istrm );
     SchemaFactory schf = SchemaFactory.lookup( SchemaFactory.NAMESPACE_SCHEMATRON );
     Schema sch = schf.compileSchema( is );
     Validator validator = sch.newValidator();

     // set preprocessor parameters 
      //if (args.length > 1)
        // validator.setProperty(Validator.PROPERTY_PHASE, "New");
      
	    DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    Document document = parser.parse(new File("I:/MDM/learn/schematron/XMLForm/src/org/xmlform/validation/test.xml"));
	    
      ZTestBean tbean = new ZTestBean();
      List violations = null;
      
      // measure validation speed
//      long time = System.currentTimeMillis ();
//      int i = 0;
//      for (; i < 100; i++)
//      {
//        // perform validation
        
//      }
      //violations = validator.validate( tbean );
      violations=validator.validate(document.getDocumentElement());
      //time = System.currentTimeMillis () - time;
      //System.out.println( "\nValidation performance:");
      //System.out.println( " validate() executed " + i + " times for a total of " + time + " ms");
      //System.out.println( "Avarage validation time: " + (time/i) + " ms " );

      // everything ok?
      if (violations == null) 
      {
        System.out.println("\nValidation ok, no messages generated");
      }
      else {
        System.out.println("Validation encountered errors. Messages :");
        Iterator viter = violations.iterator();
        while (viter.hasNext ())
        {
          Violation v = (Violation) viter.next();
          System.out.println("Validity violation path: " + v.getPath() + ", message: " + v.getMessage() );
        }
      }

      System.out.println("\n=== Schematron validation done ===");
   } 
   
   
}

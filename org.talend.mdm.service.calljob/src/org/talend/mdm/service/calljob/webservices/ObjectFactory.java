
// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.mdm.service.calljob.webservices;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the talend.core.transformer.plugin.v2.tiscall.webservices package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: talend.core.transformer.plugin.v2.tiscall.webservices
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ArrayOfXsdString }
     * 
     */
    public ArrayOfXsdString createArrayOfXsdString() {
        return new ArrayOfXsdString();
    }

    /**
     * Create an instance of {@link Args }
     * 
     */
    public Args createArgs() {
        return new Args();
    }

    /**
     * Create an instance of {@link RunJobReturn }
     * 
     */
    public RunJobReturn createRunJobReturn() {
        return new RunJobReturn();
    }

}
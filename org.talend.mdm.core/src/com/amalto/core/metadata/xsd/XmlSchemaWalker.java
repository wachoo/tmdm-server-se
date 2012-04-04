/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.metadata.xsd;

import org.apache.ws.commons.schema.*;

import java.util.Iterator;

/**
 *
 */
public class XmlSchemaWalker {

    public static void walk(XmlSchemaCollection collection, XmlSchemaVisitor visitor) {
        XmlSchema[] xmlSchemas = collection.getXmlSchemas();
        for (XmlSchema xmlSchema : xmlSchemas) {
            visitor.visitSchema(xmlSchema);
        }
    }

    public static void walk(XmlSchema xmlSchema, XmlSchemaVisitor visitor) {
        // Visit element first (create MDM entity types)
        XmlSchemaObjectTable elements = xmlSchema.getElements();
        Iterator allElements = elements.getValues();
        while (allElements.hasNext()) {
            XmlSchemaElement element = (XmlSchemaElement) allElements.next();
            walk(element, visitor);
        }
        // Visit remaining types (sometimes used in case of inheritance by entity types).
        XmlSchemaObjectTable items = xmlSchema.getSchemaTypes();
        Iterator types = items.getValues();
        while (types.hasNext()) {
            walk((XmlSchemaType) types.next(), visitor);
        }
    }

    public static void walk(XmlSchemaElement element, XmlSchemaVisitor visitor) {
        visitor.visitElement(element);
    }

    public static void walk(XmlSchemaType type, XmlSchemaVisitor visitor) {
        if (type instanceof XmlSchemaSimpleType) {
            walk(((XmlSchemaSimpleType) type), visitor);
        } else if (type instanceof XmlSchemaComplexType) {
            walk(((XmlSchemaComplexType) type), visitor);
        } else {
            throw new IllegalArgumentException("Not supported XML Schema type: " + type.getClass().getName());
        }
    }

    private static void walk(XmlSchemaSimpleType xmlSchemaType, XmlSchemaVisitor visitor) {
        visitor.visitSimpleType(xmlSchemaType);
    }

    private static void walk(XmlSchemaComplexType xmlSchemaType, XmlSchemaVisitor visitor) {
        visitor.visitComplexType(xmlSchemaType);
    }

    public static void walk(XmlSchemaObject schemaObject, XmlSchemaVisitor visitor) {
        if (schemaObject instanceof XmlSchemaElement) {
            walk(((XmlSchemaElement) schemaObject), visitor);
        } else {
            throw new IllegalArgumentException("Not supported XML Schema type: " + schemaObject.getClass().getName());
        }
    }

}
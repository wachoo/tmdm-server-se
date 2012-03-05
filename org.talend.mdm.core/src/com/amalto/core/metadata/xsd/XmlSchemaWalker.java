package com.amalto.core.metadata.xsd;

import org.apache.ws.commons.schema.*;

import java.util.Iterator;

/**
 *
 */
public class XmlSchemaWalker {

    public static <T> T walk(XmlSchemaCollection collection, XmlSchemaVisitor<T> visitor) {
        T result = null;
        XmlSchema[] xmlSchemas = collection.getXmlSchemas();
        for (XmlSchema xmlSchema : xmlSchemas) {
            result = visitor.visitSchema(xmlSchema);
        }
        return result;
    }

    public static <T> T walk(XmlSchema xmlSchema, XmlSchemaVisitor<T> visitor) {
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

        return null;
    }

    public static <T> T walk(XmlSchemaElement element, XmlSchemaVisitor<T> visitor) {
        visitor.visitElement(element);
        return null;
    }

    public static <T> T walk(XmlSchemaType type, XmlSchemaVisitor<T> visitor) {
        if (type instanceof XmlSchemaSimpleType) {
            return walk(((XmlSchemaSimpleType) type), visitor);
        } else if (type instanceof XmlSchemaComplexType) {
            return walk(((XmlSchemaComplexType) type), visitor);
        } else {
            throw new IllegalArgumentException("Not supported XML Schema type: " + type.getClass().getName());
        }
    }

    private static <T> T walk(XmlSchemaSimpleType xmlSchemaType, XmlSchemaVisitor<T> visitor) {
        return visitor.visitSimpleType(xmlSchemaType);
    }

    private static <T> T walk(XmlSchemaComplexType xmlSchemaType, XmlSchemaVisitor<T> visitor) {
        return visitor.visitComplexType(xmlSchemaType);
    }

    public static <T> T walk(XmlSchemaObject schemaObject, XmlSchemaVisitor<T> visitor) {
        if (schemaObject instanceof XmlSchemaElement) {
            return walk(((XmlSchemaElement) schemaObject), visitor);
        } else {
            throw new IllegalArgumentException("Not supported XML Schema type: " + schemaObject.getClass().getName());
        }
    }

}
package com.amalto.core.metadata.xsd;

import java.util.Iterator;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 *
 */
public class XmlSchemaWalker {

    public static <T> T walk(XmlSchemaCollection collection, XmlSchemaVisitor<T> visitor) {
        T result = null;
        XmlSchema[] xmlSchemas = collection.getXmlSchemas();
        for (XmlSchema xmlSchema : xmlSchemas) {
            result = visitor.visit(xmlSchema);
        }
        return result;
    }

    public static <T> T walk(XmlSchema xmlSchema, XmlSchemaVisitor<T> visitor) {
        XmlSchemaObjectTable items = xmlSchema.getSchemaTypes();
        Iterator types = items.getValues();
        while (types.hasNext()) {
            walk((XmlSchemaType) types.next(), visitor);
        }

        XmlSchemaObjectTable elements = xmlSchema.getElements();
        Iterator allElements = elements.getValues();
        while (allElements.hasNext()) {
            XmlSchemaElement element = (XmlSchemaElement) allElements.next();
            walk(element, visitor);
        }

        return null;
    }

    public static <T> T walk(XmlSchemaElement element, XmlSchemaVisitor<T> visitor) {
        visitor.visit(element);
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

    public static <T> T walk(XmlSchemaSimpleType xmlSchemaType, XmlSchemaVisitor<T> visitor) {
        return visitor.visit(xmlSchemaType);
    }

    public static <T> T walk(XmlSchemaComplexType xmlSchemaType, XmlSchemaVisitor<T> visitor) {
        return visitor.visit(xmlSchemaType);
    }

    public static <T> T walk(XmlSchemaObject schemaObject, XmlSchemaVisitor<T> visitor) {
        if (schemaObject instanceof XmlSchemaElement) {
            return walk(((XmlSchemaElement) schemaObject), visitor);
        } else {
            throw new IllegalArgumentException("Not supported XML Schema type: " + schemaObject.getClass().getName());
        }
    }

}

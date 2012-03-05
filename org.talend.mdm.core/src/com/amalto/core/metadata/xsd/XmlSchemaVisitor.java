package com.amalto.core.metadata.xsd;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;

/**
 *
 */
public interface XmlSchemaVisitor<T> {

    T visitSchema(XmlSchema xmlSchema);

    T visitSimpleType(XmlSchemaSimpleType type);

    T visitComplexType(XmlSchemaComplexType type);

    T visitElement(XmlSchemaElement element);

}
package com.amalto.core.metadata.xsd;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;

/**
 *
 */
public interface XmlSchemaVisitor<T> {

    T visit(XmlSchema xmlSchema);

    T visit(XmlSchemaSimpleType type);

    T visit(XmlSchemaComplexType type);

    T visit(XmlSchemaElement element);

}

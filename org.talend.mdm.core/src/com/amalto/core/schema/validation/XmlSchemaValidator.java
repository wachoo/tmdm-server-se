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

package com.amalto.core.schema.validation;

import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class XmlSchemaValidator implements Validator {
    
    private static final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    
    private static final Map<String, Schema> schemaCache = new HashMap<String, Schema>();

    private final InputStream schemaAsStream;

    private final String dataModelName;

    private final Validator next;

    public XmlSchemaValidator(String dataModelName, InputStream schemaAsStream, Validator next) {
        this.schemaAsStream = schemaAsStream;
        this.dataModelName = dataModelName;
        this.next = next;
    }

    public void validate(Element element) {
        try {
            Schema parsedSchema = schemaCache.get(dataModelName);
            if (parsedSchema == null) {
                parsedSchema = schemaFactory.newSchema(new StreamSource(schemaAsStream));
                schemaCache.put(dataModelName, parsedSchema);
            }

            javax.xml.validation.Validator validator = parsedSchema.newValidator();
            validator.validate(new DOMSource(element));

            next.validate(element);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

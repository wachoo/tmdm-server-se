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

import com.amalto.core.util.SAXErrorHandler;
import com.amalto.core.util.ValidateException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;
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

    public void validate(Element element) throws ValidateException {
        Schema parsedSchema;
        synchronized (schemaCache) {
            try {
                parsedSchema = schemaCache.get(dataModelName);
                if (parsedSchema == null) {
                    parsedSchema = schemaFactory.newSchema(new StreamSource(schemaAsStream));
                    schemaCache.put(dataModelName, parsedSchema);
                }
            } catch (SAXException e) {
                throw new RuntimeException("Exception occurred during XML schema parsing.", e);
            }
        }

        javax.xml.validation.Validator validator = parsedSchema.newValidator();
        SAXErrorHandler errorHandler = new SAXErrorHandler();
        try {
            validator.setErrorHandler(errorHandler);
            validator.validate(new DOMSource(element));
        } catch (Exception e) {
            // All errors are expected to be handled by the error handler.
            throw new RuntimeException("Unexpected validation issue.", e);
        }

        String errors = errorHandler.getErrors();
        if (!errors.isEmpty()) {
            throw new ValidateException(errors);
        }

        next.validate(element);
    }

    public static void invalidateCache(String dataModelName) {
        synchronized (schemaCache) {
            schemaCache.remove(dataModelName);
        }
    }
}

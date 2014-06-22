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

package com.amalto.core.load.payload;

import com.amalto.core.load.Constants;
import com.amalto.core.load.context.StateContext;
import com.amalto.core.load.context.StateContextSAXWriter;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.*;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class FlushStorageXMLReader implements XMLReader {

    private final XMLStreamReader reader;

    private final StateContext context;

    private final Map<String, Boolean> features = new HashMap<String, Boolean>();

    private final Map<String, Object> properties = new HashMap<String, Object>();

    private EntityResolver resolver;

    private DTDHandler handler;

    private ContentHandler contentHandler;

    private ErrorHandler errorHandler;

    public FlushStorageXMLReader(XMLStreamReader reader, StateContext context) {
        this.reader = reader;
        this.context = context;
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return features.get(name) != null ? features.get(name) : false;
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        features.put(name, value);
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return properties.get(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        properties.put(name, value);
    }

    public void setEntityResolver(EntityResolver resolver) {
        this.resolver = resolver;
    }

    public EntityResolver getEntityResolver() {
        return resolver;
    }

    public void setDTDHandler(DTDHandler handler) {
        this.handler = handler;
    }

    public DTDHandler getDTDHandler() {
        return handler;
    }

    public void setContentHandler(ContentHandler handler) {
        contentHandler = handler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setErrorHandler(ErrorHandler handler) {
        errorHandler = handler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void parse(InputSource input) throws IOException, SAXException {
        if (contentHandler == null) {
            throw new IllegalStateException("Content handler has not been set.");
        }

        contentHandler.startDocument();

        // Payload
        contentHandler.startElement(StringUtils.EMPTY, Constants.PAYLOAD_ELEMENT, Constants.PAYLOAD_ELEMENT, Constants.EMPTY_ATTRIBUTES);
        try {
            context.getWriter().flush(contentHandler);
            context.setWriter(new StateContextSAXWriter(contentHandler));
            while (!context.hasFinishedPayload()) {
                context.parse(reader);
            }
            if (context.getDepth() == 1) {
                context.parse(reader);
            }
        } catch (Exception e) {
            throw new SAXException(e);
        }

        contentHandler.endDocument();
    }

    public void parse(String systemId) throws IOException, SAXException {
        throw new SAXException("Not supported");
    }
}

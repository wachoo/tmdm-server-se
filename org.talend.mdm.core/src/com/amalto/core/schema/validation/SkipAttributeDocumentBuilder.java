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

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SkipAttributeDocumentBuilder extends DocumentBuilder {

    private final SAXParserFactory saxParserFactory;

    private final DocumentBuilder documentBuilder;

    public SkipAttributeDocumentBuilder(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
        this.saxParserFactory = SAXParserFactory.newInstance();
    }

    @Override
    public Document parse(InputSource is) throws SAXException, IOException {
        try {
            Document document = newDocument();
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(is, new SkipAttributeHandler(document));
            return document;
        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public boolean isNamespaceAware() {
        return false;
    }

    @Override
    public boolean isValidating() {
        return false;
    }

    @Override
    public void setEntityResolver(EntityResolver er) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setErrorHandler(ErrorHandler eh) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Document newDocument() {
        return documentBuilder.newDocument();
    }

    @Override
    public DOMImplementation getDOMImplementation() {
        throw new UnsupportedOperationException();
    }

    private static class SkipAttributeHandler extends DefaultHandler {

        private final Document document;

        private final Stack<Element> elementStack = new Stack<Element>();

        private final Map<String, String> prefixDeclarations = new HashMap<String, String>();

        public SkipAttributeHandler(Document document) {
            this.document = document;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            Element element = document.createElementNS(uri, qName);

            for (int i = 0; i < attributes.getLength(); i++) {
                String qualifiedName = attributes.getQName(i);
                String prefix = StringUtils.substringBefore(qualifiedName, ":"); //$NON-NLS-1$
                String name = StringUtils.substringAfter(qualifiedName, ":"); //$NON-NLS-1$
                String value = attributes.getValue(i);

                if ("xmlns".equals(prefix)) { //$NON-NLS-1$
                    // Namespace declaration: keeps the prefix associated with the namespace URI.
                    prefixDeclarations.put(name, value);
                } else {
                    String namespaceURI = prefixDeclarations.get(prefix);
                    if (namespaceURI == null) {
                        throw new IllegalArgumentException("Prefix '" + prefix + "' isn't declared;");
                    }
                    // Takes care of XML schema instance (XSI) attributes (because they must be kept).
                    if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(namespaceURI)) {
                        Attr attribute = document.createAttributeNS(namespaceURI, qualifiedName);
                        attribute.setValue(value);
                        element.setAttributeNodeNS(attribute);
                    }
                    // Ignore everything else (e.g. 'tmdm'...).
                }
            }

            if (elementStack.empty()) {
                document.appendChild(element);
            }
            elementStack.push(element);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            Element newElement = elementStack.pop();

            if (!elementStack.empty()) {
                Element parent = elementStack.peek();
                parent.appendChild(newElement);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            Text textNode = document.createTextNode(new String(ch, start, length));
            elementStack.peek().appendChild(textNode);
        }
    }
}

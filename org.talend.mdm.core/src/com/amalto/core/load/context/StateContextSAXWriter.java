// ============================================================================
//
// Copyright (c) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.load.context;

import java.util.Map;
import java.util.Set;

import com.amalto.core.load.Constants;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 */
public class StateContextSAXWriter implements StateContextWriter {
    private final ContentHandler contentHandler;

    public StateContextSAXWriter(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void writeEndDocument() throws XMLStreamException, SAXException {
        contentHandler.endDocument();
    }

    public void writeEndElement(XMLStreamReader reader) throws XMLStreamException, SAXException {
        Map<String, String> prefixToNamespace = Utils.parseNamespace(reader);
        Set<Map.Entry<String, String>> entries = prefixToNamespace.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            contentHandler.endPrefixMapping(entry.getKey());
        }

        contentHandler.endElement(getURI(reader), reader.getLocalName(), reader.getName().getLocalPart());
    }

    public void writeCharacters(XMLStreamReader reader) throws XMLStreamException, SAXException {
        char[] characters = reader.getTextCharacters();
        int textStart = reader.getTextStart();
        char[] textCharacters = ArrayUtils.subarray(characters, textStart, textStart + reader.getTextLength());
        contentHandler.characters(textCharacters, 0, textCharacters.length);
    }

    public void writeStartElement(XMLStreamReader reader) throws XMLStreamException, SAXException {
        // Namespace parsing
        Map<String, String> prefixToNamespace = Utils.parseNamespace(reader);
        Set<Map.Entry<String, String>> entries = prefixToNamespace.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            contentHandler.startPrefixMapping(entry.getKey(), entry.getValue());
        }

        // Attribute parsing
        Attributes attributes = Utils.parseAttributes(reader);

        // Start new XML element
        contentHandler.startElement(getURI(reader),
                reader.getLocalName(),
                reader.getName().getLocalPart(),
                attributes);
    }

    public void flush(ContentHandler contentHandler) throws Exception {
        // Nothing to do
    }

    public void writeStartElement(String elementLocalName) throws Exception {
        contentHandler.startElement(StringUtils.EMPTY, elementLocalName, elementLocalName, Constants.EMPTY_ATTRIBUTES);
    }

    public void writeCharacters(String characters) throws Exception {
        char[] chars = characters.toCharArray();
        contentHandler.characters(chars, 0, chars.length);
    }

    public void writeEndElement(String elementLocalName) throws Exception {
        contentHandler.endElement(StringUtils.EMPTY, elementLocalName, elementLocalName);
    }

    private static String getURI(XMLStreamReader reader) {
        String namespaceURI = reader.getNamespaceURI();
        return namespaceURI == null ? StringUtils.EMPTY : namespaceURI; // SAX consumer (such as Qizx) doesn't like null NS.
    }
}

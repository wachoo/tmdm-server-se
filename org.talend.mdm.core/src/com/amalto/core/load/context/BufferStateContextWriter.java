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

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.amalto.core.load.process.PayloadProcessedElement;
import com.amalto.core.load.process.ProcessedCharacters;
import com.amalto.core.load.process.ProcessedEndElement;
import com.amalto.core.load.process.ProcessedStartElement;
import org.apache.commons.lang.ArrayUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 */
public class BufferStateContextWriter implements StateContextWriter {
    private final List<PayloadProcessedElement> processedElements = new ArrayList<PayloadProcessedElement>();

    public BufferStateContextWriter() {
    }

    public void writeEndDocument() throws XMLStreamException {
        processedElements.add(new PayloadProcessedElement() {
            public void flush(ContentHandler contentHandler) throws SAXException {
                contentHandler.endDocument();
            }
        });
    }

    public void writeEndElement(XMLStreamReader reader) throws XMLStreamException {
        ProcessedEndElement endElement = new ProcessedEndElement(reader.getNamespaceURI(),
                reader.getLocalName(),
                reader.getName().getLocalPart());
        processedElements.add(endElement);
    }

    public void writeCharacters(XMLStreamReader reader) throws XMLStreamException {
        char[] characters = reader.getTextCharacters();
        int textStart = reader.getTextStart();
        char[] textCharacters = ArrayUtils.subarray(characters, textStart, textStart + reader.getTextLength());
        processedElements.add(new ProcessedCharacters(textCharacters));
    }

    public void writeStartElement(XMLStreamReader reader) throws XMLStreamException {
        Attributes attributes = Utils.parseAttributes(reader);
        ProcessedStartElement startElement = new ProcessedStartElement(reader.getNamespaceURI(),
                reader.getLocalName(),
                reader.getName().getLocalPart(),
                attributes);
        processedElements.add(startElement);
    }

    public void flush(ContentHandler contentHandler) throws Exception {
        for (PayloadProcessedElement processedElement : processedElements) {
            processedElement.flush(contentHandler);
        }
        processedElements.clear();
    }

    public BufferStateContextWriter reset() {
        processedElements.clear();
        return this;
    }

}

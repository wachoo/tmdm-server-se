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

package com.amalto.core.storage.record;

import com.amalto.core.load.io.ResettableStringWriter;
import com.amalto.core.metadata.*;
import com.amalto.core.storage.record.metadata.DataRecordMetadataImpl;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.xml.sax.*;

import java.util.Stack;

// TODO Support inheritance + refactor
public class XmlSAXDataRecordReader implements DataRecordReader<XmlSAXDataRecordReader.Input> {

    public static class Input {
        final XMLReader reader;
        final InputSource input;

        public Input(XMLReader reader, InputSource input) {
            this.reader = reader;
            this.input = input;
        }
    }

    public DataRecord read(long revisionId, MetadataRepository repository, ComplexTypeMetadata type, Input input) {
        try {
            InputSource inputSource = input.input;
            XMLReader xmlReader = input.reader;

            DataRecordContentHandler handler = new DataRecordContentHandler(type);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(inputSource);

            DataRecord dataRecord = handler.getDataRecord();
            dataRecord.setRevisionId(revisionId);
            return dataRecord;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static class DataRecordContentHandler implements ContentHandler {

        private final Stack<ComplexTypeMetadata> currentType = new Stack<ComplexTypeMetadata>();

        private final Stack<String> currentElement = new Stack<String>();

        private final Stack<DataRecord> dataRecordStack = new Stack<DataRecord>();

        private final ResettableStringWriter charactersBuffer = new ResettableStringWriter();

        private final ComplexTypeMetadata mainType;

        private FieldMetadata field;

        private boolean hasMetUserElement = false;

        private boolean isReadingTimestamp = false;

        private boolean isReadingTaskId = false;

        private final DataRecord dataRecord;

        public DataRecordContentHandler(ComplexTypeMetadata type) {
            mainType = type;
            field = null;
            dataRecord = new DataRecord(type, new DataRecordMetadataImpl(0, null));
            dataRecordStack.push(dataRecord);
            hasMetUserElement = false;
        }

        public void setDocumentLocator(Locator locator) {
        }

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        public void endPrefixMapping(String prefix) throws SAXException {
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (!hasMetUserElement) {
                if (mainType.getName().equals(localName)) {
                    hasMetUserElement = true;
                    currentType.push(mainType);
                } else {
                    if ("t".equals(localName)) { //$NON-NLS-1$
                        isReadingTimestamp = true;
                    } else if ("taskId".equals(localName)) { //$NON-NLS-1$
                        isReadingTaskId = true;
                    }
                }
            } else {
                ComplexTypeMetadata lastType = currentType.peek();
                field = lastType.getField(localName);
                if (field == null) {
                    throw new IllegalArgumentException("Type '" + lastType.getName() + "' does not own field '" + localName + "'");
                }
                if (field instanceof ReferenceFieldMetadata) {
                    ComplexTypeMetadata referencedType = ((ReferenceFieldMetadata) field).getReferencedType();
                    dataRecordStack.push(dataRecordStack.peek());
                    currentElement.push(field.getName());
                    currentType.push(referencedType);
                    field = ((ReferenceFieldMetadata) field).getReferencedField();
                } else if (field.getType() instanceof ComplexTypeMetadata) {
                    if (field instanceof ContainedTypeFieldMetadata) {
                        DataRecord containedRecord = new DataRecord(((ContainedTypeFieldMetadata) field).getContainedType(), UnsupportedDataRecordMetadata.INSTANCE);
                        dataRecordStack.peek().set(field, containedRecord);
                        dataRecordStack.push(containedRecord);
                    }
                    currentType.push((ComplexTypeMetadata) field.getType());
                    currentElement.push(field.getName());
                }
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            String value = charactersBuffer.reset();
            if (hasMetUserElement && field != null) {
                if (!value.isEmpty()) {
                    dataRecordStack.peek().set(field, value.isEmpty() ? null : MetadataUtils.convert(value, field, currentType.peek()));
                }
            } else {
                if (isReadingTimestamp) {
                    dataRecordStack.peek().getRecordMetadata().setLastModificationTime(Long.parseLong(value));
                    isReadingTimestamp = false;
                } else if (isReadingTaskId) {
                    dataRecordStack.peek().getRecordMetadata().setTaskId(value.isEmpty() ? null : value);
                    isReadingTaskId = false;
                }
            }

            if (!currentElement.isEmpty() && localName.equals(currentElement.peek())) {
                currentType.pop();
                currentElement.pop();
                dataRecordStack.pop();
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            charactersBuffer.append(new String(ch, start, length).trim());
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
        }

        public void skippedEntity(String name) throws SAXException {
        }

        public DataRecord getDataRecord() {
            return dataRecord;
        }


    }
}

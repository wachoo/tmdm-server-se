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
import com.amalto.core.schema.validation.SkipAttributeDocumentBuilder;
import com.amalto.core.storage.record.metadata.DataRecordMetadataImpl;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.xml.sax.*;

import javax.xml.XMLConstants;
import java.util.Stack;

public class XmlSAXDataRecordReader implements DataRecordReader<XmlSAXDataRecordReader.Input> {

    public static class Input {

        private final XMLReader reader;

        private final InputSource input;

        public Input(XMLReader reader, InputSource input) {
            this.reader = reader;
            this.input = input;
        }
    }

    public DataRecord read(String revisionId, MetadataRepository repository, ComplexTypeMetadata type, Input input) {
        try {
            InputSource inputSource = input.input;
            XMLReader xmlReader = input.reader;
            DataRecordContentHandler handler = new DataRecordContentHandler(type, repository);
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

        private final Stack<TypeMetadata> currentType = new Stack<TypeMetadata>();

        private final Stack<DataRecord> dataRecordStack = new Stack<DataRecord>();

        private final ResettableStringWriter charactersBuffer = new ResettableStringWriter();

        private final ComplexTypeMetadata mainType;

        private final MetadataRepository repository;

        private FieldMetadata field;

        private boolean hasMetUserElement = false;

        private boolean isReadingTimestamp = false;

        private boolean isReadingTaskId = false;

        private final DataRecord dataRecord;

        public DataRecordContentHandler(ComplexTypeMetadata type, MetadataRepository repository) {
            mainType = type;
            this.repository = repository;
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
                field = ((ComplexTypeMetadata) currentType.peek()).getField(localName);
                if (field instanceof ReferenceFieldMetadata) {
                    ComplexTypeMetadata actualType = ((ReferenceFieldMetadata) field).getReferencedType();
                    String mdmType = attributes.getValue(SkipAttributeDocumentBuilder.TALEND_NAMESPACE, "type"); //$NON-NLS-1$
                    if (mdmType != null) {
                        actualType = repository.getComplexType(mdmType);
                    }
                    currentType.push(actualType);
                } else if (field instanceof ContainedTypeFieldMetadata) {
                    ComplexTypeMetadata actualType = ((ContainedTypeFieldMetadata) field).getContainedType();
                    String xsiType = attributes.getValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"); //$NON-NLS-1$
                    if (xsiType != null) {
                        actualType = (ComplexTypeMetadata) repository.getNonInstantiableType(repository.getUserNamespace(), xsiType);
                    }
                    DataRecord containedRecord = new DataRecord(actualType, UnsupportedDataRecordMetadata.INSTANCE);
                    dataRecordStack.peek().set(field, containedRecord);
                    dataRecordStack.push(containedRecord);
                    currentType.push(actualType);
                } else {
                    currentType.push(field.getType());
                }
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            String value = charactersBuffer.reset();
            if (hasMetUserElement && field != null) {
                if (!value.isEmpty()) {
                    dataRecordStack.peek().set(field, value.isEmpty() ? null : MetadataUtils.convert(value, field, currentType.peek()));
                }
                if (!currentType.isEmpty()) {
                    TypeMetadata typeMetadata = currentType.pop();
                    if (!(field instanceof ReferenceFieldMetadata) && typeMetadata instanceof ComplexTypeMetadata) {
                        dataRecordStack.pop();
                    }
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

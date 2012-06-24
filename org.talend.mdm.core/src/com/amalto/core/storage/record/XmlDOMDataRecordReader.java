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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.ContainedComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;
import com.amalto.core.storage.record.metadata.DataRecordMetadataImpl;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlDOMDataRecordReader implements DataRecordReader<Element> {

    public XmlDOMDataRecordReader() {
    }

    public DataRecord read(String dataClusterName, long revisionId, ComplexTypeMetadata type, Element element) {
        long lastModificationTime = 0;
        String taskId = null;

        NodeList timeStamp = element.getElementsByTagName("t"); //$NON-NLS-1$
        if (timeStamp.getLength() > 0) {
            lastModificationTime = Long.parseLong(timeStamp.item(0).getFirstChild().getNodeValue());
        }
        NodeList taskIdElement = element.getElementsByTagName("taskId"); //$NON-NLS-1$
        if (taskIdElement.getLength() > 0) {
            Node firstChild = taskIdElement.item(0).getFirstChild();
            taskId = firstChild == null ? null : firstChild.getNodeValue();
            if (taskId == null || taskId.isEmpty()) {
                taskId = null;
            }
        }
        DataRecordMetadata metadata = new DataRecordMetadataImpl(lastModificationTime, taskId);
        DataRecord dataRecord = new DataRecord(type, metadata);
        dataRecord.setRevisionId(revisionId);

        NodeList userPayloadElement = element.getElementsByTagName("p");
        Element singleUserPayloadElement = (Element) userPayloadElement.item(0);
        if (singleUserPayloadElement == null) {
            _read(dataRecord, type, element);
        } else {
            _read(dataRecord, type, (Element) singleUserPayloadElement.getElementsByTagName(type.getName()).item(0));
        }

        return dataRecord;
    }

    private void _read(DataRecord dataRecord, ComplexTypeMetadata type, Element element) {
        String tagName = element.getTagName();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node currentChild = children.item(i);
            if (currentChild instanceof Element) {
                Element child = (Element) currentChild;
                FieldMetadata field = type.getField(child.getTagName());
                if (field.getType() instanceof ContainedComplexTypeMetadata) {
                    ComplexTypeMetadata containedType = (ComplexTypeMetadata) field.getType();
                    DataRecord containedRecord = new DataRecord(containedType, UnsupportedDataRecordMetadata.INSTANCE);
                    dataRecord.set(field, containedRecord);
                    _read(containedRecord, containedType, child);
                } else {
                    _read(dataRecord, type, child);
                }
            } else if (currentChild instanceof Text && !tagName.equals(type.getName())) {
                FieldMetadata field = type.getField(tagName);
                String textContent = element.getFirstChild().getNodeValue();
                dataRecord.set(field, MetadataUtils.convert(textContent, field));
            }
        }
    }

}

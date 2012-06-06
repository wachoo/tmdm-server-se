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
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.storage.hibernate.HibernateClassWrapper;
import com.amalto.core.storage.hibernate.MappingMetadataRepository;
import com.amalto.core.storage.hibernate.enhancement.TypeMapping;
import com.amalto.core.storage.record.metadata.DataRecordMetadataImpl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ObjectDataRecordReader implements DataRecordReader<HibernateClassWrapper> {

    private final Collection<FieldMetadata> selectedFields;

    private MappingMetadataRepository metadataRepository;

    public ObjectDataRecordReader(List<FieldMetadata> selectedFields) {
        this.selectedFields = selectedFields;
    }

    public DataRecord read(String dataClusterName, long revisionId, ComplexTypeMetadata type, HibernateClassWrapper input) {
        TypeMapping typeMapping = (TypeMapping) type;
        metadataRepository = typeMapping.getMappingMetadataRepository();

        DataRecordMetadataImpl recordMetadata = new DataRecordMetadataImpl(input.timestamp(), input.taskId());
        DataRecord record = new DataRecord(typeMapping, recordMetadata);
        for (FieldMetadata selectedField : selectedFields) {
            set(record, input, selectedField);
        }
        record.setRevisionId(input.revision());
        return record;
    }

    private void set(DataRecord record, HibernateClassWrapper input, FieldMetadata selectedField) {
        ComplexTypeMetadata type = record.getType();
        try {
            Object value;
            if (selectedField.getContainingType().equals(type)) {
                // Minor optimization (could use path() there... but this is not necessary since we can directly look
                // up for the value in input).
                value = input.get(selectedField.getName());
            } else {
                value = input;
                List<FieldMetadata> path = MetadataUtils.path(type, selectedField);
                if (path.isEmpty()) {
                    // This means there's no support for selecting instances that do not have a path from "type" argument.
                    // ... but this should never happen.
                    throw new RuntimeException("No path found from type '" + type.getName() + "' to field '" + selectedField.getName() + "' (in type '" + selectedField.getContainingType().getName() + "')");
                }
                for (FieldMetadata fieldMetadata : path) {
                    // "Replay" the path to get value of the selected field.
                    if (value != null) {
                        value = ((HibernateClassWrapper) value).get(fieldMetadata.getName());
                    } else {
                        break;
                    }
                }
            }

            if (!(selectedField instanceof ReferenceFieldMetadata)) {
                setRecordValue(record, selectedField, value);
            } else {
                ReferenceFieldMetadata referenceFieldMetadata = (ReferenceFieldMetadata) selectedField;

                if (value != null) {
                    TypeMapping referencedTypeMapping = metadataRepository.getMapping(referenceFieldMetadata.getReferencedType());
                    if (referencedTypeMapping == null) {
                        throw new IllegalStateException("Type '" + referenceFieldMetadata.getReferencedType().getName() + "' does not have a mapping.");
                    }

                    if (!referenceFieldMetadata.isMany()) {
                        Object fkValue = ((HibernateClassWrapper) value).get(referencedTypeMapping.getFlatten(referenceFieldMetadata.getReferencedField()).getName());
                        setRecordValue(record, selectedField, fkValue);
                    } else {
                        List<FieldMetadata> keyFields = referencedTypeMapping.getKeyFields();
                        for (Object o : ((List) value)) {
                            List fkValue = new LinkedList();
                            for (FieldMetadata keyField : keyFields) {
                                // TODO This is strange behavior (it may happen that types here are already flatten).
                                FieldMetadata flatten = referencedTypeMapping.getFlatten(keyField);
                                if (flatten == null) {
                                    flatten = keyField;
                                }
                                fkValue.add(((HibernateClassWrapper) o).get(flatten.getName()));
                            }
                            setRecordValue(record, selectedField, fkValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create data record from result object", e);
        }
    }

    private static void setRecordValue(DataRecord record, FieldMetadata selectedField, Object value) {
        TypeMapping typeMapping = (TypeMapping) record.getType();
        FieldMetadata userField = typeMapping.getUser(selectedField);
        if (userField != null) {
            record.set(userField, value);
        }
    }
}

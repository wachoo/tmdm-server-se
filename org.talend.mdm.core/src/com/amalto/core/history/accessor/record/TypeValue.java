/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history.accessor.record;

import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;
import org.talend.mdm.commmon.metadata.TypeMetadata;

class TypeValue implements Setter, Getter {

    static Setter SET = new TypeValue();

    static Getter GET = new TypeValue();

    @Override
    public void set(DataRecord record, PathElement element, String value) {
        if (value == null) {
            return;
        }
        if (record != null) {
            if (element.field instanceof ReferenceFieldMetadata) {
                DataRecord dataRecord = (DataRecord) record.get(element.field);
                if (dataRecord == null) {
                    dataRecord = new DataRecord(((ReferenceFieldMetadata) element.field).getReferencedType(), UnsupportedDataRecordMetadata.INSTANCE);
                    record.set(element.field, dataRecord);
                    record = dataRecord;
                } else {
                    record = (DataRecord) record.get(element.field);
                }
            }
            if (!value.isEmpty()) {
                ComplexTypeMetadata type = record.getType();
                if (!value.equals(record.getType().getName())) {
                    ComplexTypeMetadata newType = getSubType(type, value);
                    if (newType != null) {
                        record.setType(newType);
                    } else {
                        for (TypeMetadata superType : type.getSuperTypes()) {
                            if (value.equals(superType.getName())) {
                                newType = (ComplexTypeMetadata) superType;
                                break;
                            }
                        }
                        if (newType != null) {
                            record.setType(newType);
                        } else {
                            throw new IllegalArgumentException("Type '" + value + "' is not in inheritance tree accessible from '" + type.getName() + "'");
                        }
                    }
                }
            }
        }
    }

    private ComplexTypeMetadata getSubType(ComplexTypeMetadata type, String typeName) {
        for (ComplexTypeMetadata subType : type.getSubTypes()) {
            if (typeName.equals(subType.getName())) {
                return subType;
            }
            ComplexTypeMetadata subTypeLookup = getSubType(subType, typeName);
            if (subTypeLookup != null) {
                return subTypeLookup;
            }
        }
        return null;
    }

    @Override
    public String get(DataRecord record, PathElement element) {
        if (element.field instanceof ReferenceFieldMetadata) {
            DataRecord dataRecord = (DataRecord) record.get(element.field);
            return dataRecord.getType().getName();
        } else {
            return record.getType().getName();
        }
    }
}

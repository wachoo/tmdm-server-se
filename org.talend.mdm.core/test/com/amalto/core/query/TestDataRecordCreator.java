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

package com.amalto.core.query;

import com.amalto.core.metadata.MetadataUtils;
import org.talend.mdm.commmon.metadata.*;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.commons.lang.NotImplementedException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Stack;

class TestDataRecordCreator extends DefaultMetadataVisitor<DataRecord> {

    private Stack<DataRecord> records = new Stack<DataRecord>();

    private int currentId;

    @Override
    public DataRecord visit(ComplexTypeMetadata complexType) {
        records.push(new DataRecord(complexType, UnsupportedDataRecordMetadata.INSTANCE));
        super.visit(complexType);
        return records.peek();
    }

    @Override
    public DataRecord visit(ReferenceFieldMetadata referenceField) {
        ComplexTypeMetadata referencedType = referenceField.getReferencedType();
        DataRecord newRecord = new DataRecord(referencedType, UnsupportedDataRecordMetadata.INSTANCE);
        for (FieldMetadata keyField : referencedType.getKeyFields()) {
            newRecord.set(keyField, createSimpleValue(keyField));
        }
        records.peek().set(referenceField, newRecord);
        return records.peek();
    }

    @Override
    public DataRecord visit(ContainedTypeFieldMetadata containedField) {
        DataRecord record = new DataRecord(containedField.getContainedType(), UnsupportedDataRecordMetadata.INSTANCE);
        records.peek().set(containedField, record);
        records.push(record);
        super.visit(containedField);
        return records.pop();
    }

    @Override
    public DataRecord visit(SimpleTypeFieldMetadata simpleField) {
        records.peek().set(simpleField, createSimpleValue(simpleField));
        return records.peek();
    }

    @Override
    public DataRecord visit(EnumerationFieldMetadata enumField) {
        records.peek().set(enumField, createSimpleValue(enumField));
        return records.peek();
    }

    private Object createSimpleValue(FieldMetadata field) {
        if (field.isKey()) {
            TypeMetadata type = MetadataUtils.getSuperConcreteType(field.getType());
            if ("string".equals(type.getName())) {
                return String.valueOf(currentId++);
            } else if ("int".equals(type.getName())
                    || "integer".equals(type.getName())
                    || "unsignedInt".equals(type.getName())) {
                return currentId++;
            } else if ("unsignedShort".equals(type.getName())) {
                return ((short) currentId++);
            } else if ("boolean".equals(type.getName())) {
                return false;
            } else {
                throw new NotImplementedException("Support for key with type " + type.getName());
            }
        }

        // Move up the inheritance tree to find the "most generic" type (used when simple types inherits from XSD types,
        // in this case, the XSD type is interesting, not the custom one).
        TypeMetadata type = MetadataUtils.getSuperConcreteType(field.getType());
        Random random = new Random(System.currentTimeMillis());
        if (!(field instanceof ContainedTypeFieldMetadata)) { // Don't set contained (anonymous types) values
            if ("string".equals(type.getName())) {
                Object maxLength = field.getType().getData(MetadataRepository.DATA_MAX_LENGTH);
                if (maxLength != null) {
                    int i = Integer.parseInt(maxLength.toString());
                    String s = "" + Math.abs(random.nextLong());
                    if (s.length() > i) {
                        return s.substring(0, i);
                    } else {
                        return s;
                    }
                } else {
                    return "" + random.nextLong();
                }
            } else if ("integer".equals(type.getName())
                    || "positiveInteger".equals(type.getName())
                    || "negativeInteger".equals(type.getName())
                    || "nonNegativeInteger".equals(type.getName())
                    || "nonPositiveInteger".equals(type.getName())
                    || "int".equals(type.getName())
                    || "unsignedInt".equals(type.getName())) {
                return random.nextInt();
            } else if ("date".equals(type.getName())) {
                return new Timestamp(random.nextInt());
            } else if ("dateTime".equals(type.getName())) {
                return new Timestamp(random.nextInt());
            } else if ("boolean".equals(type.getName())) {
                return random.nextBoolean();
            } else if ("decimal".equals(type.getName())) {
                return new BigDecimal(random.nextInt());
            } else if ("float".equals(type.getName())) {
                return random.nextFloat();
            } else if ("long".equals(type.getName()) || "unsignedLong".equals(type.getName())) {
                return random.nextLong();
            } else if ("anyURI".equals(type.getName())) {
                return "http://www.talend.com";
            } else if ("short".equals(type.getName()) || "unsignedShort".equals(type.getName())) {
                return new Short(String.valueOf(random.nextInt() % 255));
            } else if ("QName".equals(type.getName())) {
                return "Qname";
            } else if ("base64Binary".equals(type.getName())) {
                return "EF56AE";
            } else if ("hexBinary".equals(type.getName())) {
                return "EF56AE";
            } else if ("byte".equals(type.getName()) || "unsignedByte".equals(type.getName())) {
                return (byte) (random.nextInt() % 2);
            } else if ("double".equals(type.getName()) || "unsignedDouble".equals(type.getName())) {
                return random.nextDouble();
            } else if ("duration".equals(type.getName()) || "time".equals(type.getName())) {
                return new Timestamp(random.nextInt());
            } else {
                throw new NotImplementedException("No support for type '" + type.getName() + "'");
            }
        } else {
            return null;
        }
    }

}

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

import org.talend.mdm.commmon.metadata.MetadataUtils;
import org.talend.mdm.commmon.metadata.*;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.commons.lang.NotImplementedException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;

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
            if (Types.STRING.equals(type.getName())) {
                return "1";
            } else if (Types.INT.equals(type.getName())
                    || Types.INTEGER.equals(type.getName())
                    || Types.UNSIGNED_INT.equals(type.getName())) {
                return 1;
            } else if (Types.UNSIGNED_SHORT.equals(type.getName())) {
                return ((short) currentId++);
            } else if (Types.BOOLEAN.equals(type.getName())) {
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
            if (Types.STRING.equals(type.getName())) {
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
                    return field.getPath();
                }
            } else if (Types.INTEGER.equals(type.getName())
                    || Types.POSITIVE_INTEGER.equals(type.getName())
                    || Types.NEGATIVE_INTEGER.equals(type.getName())
                    || Types.NON_NEGATIVE_INTEGER.equals(type.getName())
                    || Types.NON_POSITIVE_INTEGER.equals(type.getName())
                    || Types.INTEGER.equals(type.getName())
                    || Types.UNSIGNED_INT.equals(type.getName())
                    || Types.INT.equals(type.getName())) {
                return random.nextInt();
            } else if (Types.DATE.equals(type.getName())) {
                return new Timestamp(random.nextInt());
            } else if (Types.DATETIME.equals(type.getName())) {
                return new Timestamp(random.nextInt());
            } else if (Types.BOOLEAN.equals(type.getName())) {
                return random.nextBoolean();
            } else if (Types.DECIMAL.equals(type.getName())) {
                return new BigDecimal(random.nextInt());
            } else if (Types.FLOAT.equals(type.getName())) {
                return random.nextFloat();
            } else if (Types.LONG.equals(type.getName()) || Types.UNSIGNED_LONG.equals(type.getName())) {
                return random.nextLong();
            } else if (Types.ANY_URI.equals(type.getName())) {
                return "http://www.talend.com";
            } else if (Types.SHORT.equals(type.getName()) || Types.UNSIGNED_SHORT.equals(type.getName())) {
                return new Short(String.valueOf(random.nextInt() % 255));
            } else if (Types.QNAME.equals(type.getName())) {
                return "Qname";
            } else if (Types.BASE64_BINARY.equals(type.getName())) {
                return "EF56AE";
            } else if (Types.HEX_BINARY.equals(type.getName())) {
                return "EF56AE";
            } else if (Types.BYTE.equals(type.getName()) || Types.UNSIGNED_BYTE.equals(type.getName())) {
                return (byte) (random.nextInt() % 2);
            } else if (Types.DOUBLE.equals(type.getName()) || Types.UNSIGNED_DOUBLE.equals(type.getName())) {
                return random.nextDouble();
            } else if (Types.DURATION.equals(type.getName()) || Types.TIME.equals(type.getName())) {
                return new Timestamp(random.nextInt());
            } else {
                throw new NotImplementedException("No support for type '" + type.getName() + "'");
            }
        } else {
            return null;
        }
    }

}

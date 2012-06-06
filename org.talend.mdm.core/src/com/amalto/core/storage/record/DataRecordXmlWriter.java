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

import com.amalto.core.metadata.*;
import com.amalto.core.query.user.DateConstant;
import com.amalto.core.query.user.DateTimeConstant;
import com.amalto.core.query.user.TimeConstant;
import com.amalto.core.storage.hibernate.enhancement.TypeMapping;

import java.io.*;
import java.util.List;

public class DataRecordXmlWriter implements DataRecordWriter {

    public void write(DataRecord dataRecord, OutputStream output) throws IOException {
        TypeMapping type = (TypeMapping) dataRecord.getType();
        Writer out = new BufferedWriter(new OutputStreamWriter(output));

        type.toUser();
        type.accept(new DataRecordPrinter(out, dataRecord, type));
        out.flush();
    }

    private class DataRecordPrinter extends DefaultMetadataVisitor<Void> {

        private final Writer out;

        private final DataRecord dataRecord;

        private final TypeMapping type;

        public DataRecordPrinter(Writer out, DataRecord dataRecord, TypeMapping type) {
            this.out = out;
            this.dataRecord = dataRecord;
            this.type = type;
        }

        @Override
        public Void visit(ComplexTypeMetadata complexType) {
            try {
                out.write("<" + complexType.getName() + ">");
                super.visit(complexType);
                out.write("</" + complexType.getName() + ">");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        public Void visit(ReferenceFieldMetadata referenceField) {
            handleReferenceElement(referenceField, dataRecord, type, out);
            return null;
        }

        @Override
        public Void visit(SimpleTypeFieldMetadata simpleField) {
            handleElement(simpleField, dataRecord, out);
            return null;
        }

        @Override
        public Void visit(EnumerationFieldMetadata enumField) {
            handleElement(enumField, dataRecord, out);
            return null;
        }

        @Override
        public Void visit(ContainedTypeFieldMetadata containedField) {
            try {
                out.write("<" + containedField.getName() + ">");
                containedField.getContainedType().accept(this);
                out.write("</" + containedField.getName() + ">");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private void handleElement(FieldMetadata field, DataRecord dataRecord, Writer output) {
            try {
                Object value = dataRecord.get(field);
                if (value != null) {
                    if (!field.isMany()) {
                        output.write("<" + field.getName() + ">");
                        if ("date".equals(field.getType().getName())) {
                            synchronized (DateConstant.DATE_FORMAT) {
                                output.write((DateConstant.DATE_FORMAT).format(value));
                            }
                        } else if ("dateTime".equals(field.getType().getName())) {
                            synchronized (DateTimeConstant.DATE_FORMAT) {
                                output.write((DateTimeConstant.DATE_FORMAT).format(value));
                            }
                        } else if ("time".equals(field.getType().getName())) {
                            synchronized (TimeConstant.TIME_FORMAT) {
                                output.write((TimeConstant.TIME_FORMAT).format(value));
                            }
                        } else {
                            output.write(value.toString());
                        }
                        output.write("</" + field.getName() + ">");
                    } else {
                        List valueAsList = (List) value;
                        for (Object currentValue : valueAsList) {
                            output.write("<" + field.getName() + ">");
                            output.write(currentValue.toString());
                            output.write("</" + field.getName() + ">");
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void handleReferenceElement(ReferenceFieldMetadata field, DataRecord dataRecord, TypeMapping mapping, Writer output) {
            try {
                Object value = dataRecord.get(field);
                if (value != null) {
                    if (!field.isMany()) {
                        output.write("<" + field.getName() + ">");
                        output.write("[" + value.toString() + "]");
                        output.write("</" + field.getName() + ">");
                    } else {
                        List valueAsList = (List) value;
                        for (Object currentValue : valueAsList) {
                            output.write("<" + field.getName() + ">");
                            if (currentValue instanceof List) { // Composite id
                                StringBuilder builder = new StringBuilder();
                                for (Object o : ((List) currentValue)) {
                                    builder.append('[').append(String.valueOf(o)).append(']');
                                }
                                output.write(builder.toString());
                            } else { // Simple id
                                output.write("[" + currentValue.toString() + "]");
                            }
                            output.write("</" + field.getName() + ">");
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

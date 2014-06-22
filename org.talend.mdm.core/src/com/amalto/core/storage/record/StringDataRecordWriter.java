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
import com.amalto.core.storage.hibernate.TypeMapping;
import org.apache.commons.lang.NotImplementedException;

import java.io.*;
import java.lang.reflect.Method;
import java.util.*;

public class StringDataRecordWriter implements DataRecordWriter {

    private boolean hasProcessedColumnNames = false;

    private static class Node {
        final List<Node> children = new LinkedList<Node>();

        final Object value;

        final FieldMetadata field;

        boolean isVisited = false;

        Node(FieldMetadata field, Object value) {
            this.value = value;
            this.field = field;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public void write(DataRecord record, OutputStream output) throws IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$
        ComplexTypeMetadata type = record.getType();

        Collection<FieldMetadata> fields = type.getFields();
        if (!hasProcessedColumnNames) {
            Iterator<FieldMetadata> fieldsIterator = fields.iterator();
            while (fieldsIterator.hasNext()) {
                out.append(fieldsIterator.next().getName());
                if (fieldsIterator.hasNext()) {
                    out.append(',');
                }
            }
            hasProcessedColumnNames = true;
            out.append('\n');
        }

        List<DataRecord> records = buildCartesianProduct(record);
        for (DataRecord dataRecord : records) {
            internalWrite(dataRecord, out);
        }
        out.flush();
    }

    public void write(DataRecord record, Writer writer) throws IOException {
        throw new NotImplementedException();
    }

    private void internalWrite(final DataRecord record, final Writer out) throws IOException {
        List<FieldMetadata> fields = record.getType().getFields();
        final Iterator<FieldMetadata> fieldsIterator = fields.iterator();
        while (fieldsIterator.hasNext()) {
            FieldMetadata field = fieldsIterator.next();
            field.accept(new DefaultMetadataVisitor<Void>() {
                @Override
                public Void visit(SimpleTypeFieldMetadata simpleField) {
                    try {
                        Object value = record.get(simpleField);
                        if (value != null) {
                            out.append(String.valueOf(value));
                        }
                        return null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public Void visit(ReferenceFieldMetadata referenceField) {
                    try {
                        Object value = record.get(referenceField);
                        if (value != null) {
                            out.append(String.valueOf(getReferencedFieldValue(referenceField, value)));
                        }
                        return null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            if (fieldsIterator.hasNext()) {
                out.append(',');
            }
        }
        out.append('\n');
    }

    private static Object getReferencedFieldValue(ReferenceFieldMetadata field, Object value) {
        FieldMetadata referencedField = field.getReferencedField();
        try {
            Method getter = value.getClass().getMethod("get" + referencedField.getName()); //$NON-NLS-1$
            return getter.invoke(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO Not really efficient way to perform cartesian product (both run time and memory)
    private List<DataRecord> buildCartesianProduct(DataRecord record) {
        // Find many fields
        List<FieldMetadata> collectionFields = new LinkedList<FieldMetadata>();
        for (FieldMetadata fieldMetadata : record.getType().getFields()) {
            if (fieldMetadata.isMany()) {
                collectionFields.add(fieldMetadata);
            }
        }

        if (collectionFields.isEmpty()) {
            // No collection field, returns the record
            return Collections.singletonList(record);
        } else {
            // Transforms collection fields into single valued fields.
            ComplexTypeMetadata type = record.getType();
            final ComplexTypeMetadata transformedType = (ComplexTypeMetadata) type.copyShallow();
            type.accept(new DefaultMetadataVisitor<Void>() {
                @Override
                public Void visit(SimpleTypeFieldMetadata simpleField) {
                    transformedType.addField(new SimpleTypeFieldMetadata(transformedType, simpleField.isKey(), false, simpleField.isMandatory(), simpleField.getName(), simpleField.getType(), simpleField.getWriteUsers(), simpleField.getHideUsers()));
                    return null;
                }

                @Override
                public Void visit(ReferenceFieldMetadata referenceField) {
                    transformedType.addField(new ReferenceFieldMetadata(transformedType, false, false, referenceField.isMandatory(), referenceField.getName(), referenceField.getReferencedField().getContainingType(), referenceField.getReferencedField(), referenceField.getForeignKeyInfoField(), referenceField.isFKIntegrity(), referenceField.allowFKIntegrityOverride(), referenceField.getType(), referenceField.getWriteUsers(), referenceField.getHideUsers()));
                    return null;
                }

                @Override
                public Void visit(EnumerationFieldMetadata enumField) {
                    transformedType.addField(new EnumerationFieldMetadata(transformedType, enumField.isKey(), false, enumField.isMandatory(), enumField.getName(), enumField.getType(), enumField.getWriteUsers(), enumField.getHideUsers()));
                    return null;
                }
            });

            // Build cartesian product
            Node node = new Node(null, null);
            for (FieldMetadata collectionField : collectionFields) {
                List list = (List) record.get(collectionField);
                if (list != null) {
                    addTo(node, collectionField, list);
                }
            }

            List<DataRecord> allRecords = new LinkedList<DataRecord>();
            Set<FieldMetadata> setFields = record.getSetFields();
            List<List<Node>> results = getResults(node);

            // Creates data records for each result of cartesian product.
            DataRecord recordCopy;
            for (List<Node> result : results) {
                recordCopy = new DataRecord(transformedType, record.getRecordMetadata());
                for (Node cartesianProductResult : result) {
                    for (FieldMetadata setField : setFields) {
                        if (!setField.isMany()) {
                            recordCopy.set(transformedType.getField(setField.getName()), record.get(setField));
                        } else {
                            recordCopy.set(transformedType.getField(cartesianProductResult.field.getName()), cartesianProductResult.value);
                        }
                    }
                }
                allRecords.add(recordCopy);
            }

            return allRecords;
        }

    }

    private static List<List<Node>> getResults(Node node) {
        Stack<Node> toProcess = new Stack<Node>();

        List<List<Node>> results = new LinkedList<List<Node>>();
        toProcess.push(node);
        while (!toProcess.isEmpty()) {
            Node currentNode = toProcess.peek();
            currentNode.isVisited = true;

            if (currentNode.children.isEmpty()) {
                LinkedList<Node> newList = new LinkedList<Node>(toProcess);
                newList.removeFirst();
                results.add(newList);
                toProcess.pop();
            } else {
                Iterator<Node> iterator = currentNode.children.iterator();
                boolean hasUnvisitedChildren = false;
                while (iterator.hasNext()) {
                    Node nextNode = iterator.next();
                    if (!nextNode.isVisited) {
                        toProcess.push(nextNode);
                        hasUnvisitedChildren = true;
                        break;
                    }
                }
                if (!hasUnvisitedChildren) {
                    toProcess.pop();
                }
            }
        }

        return results;
    }

    private static void addTo(Node node, FieldMetadata field, List<Object> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Objects argument can not be null");
        }

        if (node.children.isEmpty()) {
            for (Object object : objects) {
                node.children.add(new Node(field, object));
            }
        } else {
            for (Node child : node.children) {
                addTo(child, field, objects);
            }
        }
    }


}

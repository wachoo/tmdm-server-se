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

package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.*;
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.ScrollableResults;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class ProjectionIterator extends CloseableIterator<DataRecord> {

    private static final Logger LOGGER = Logger.getLogger(ProjectionIterator.class);

    private final Iterator iterator;

    private final List<TypedExpression> selectedFields;

    private final Set<EndOfResultsCallback> callbacks;

    public ProjectionIterator(Iterator<Object> iterator, List<TypedExpression> selectedFields, Set<EndOfResultsCallback> callbacks) {
        this.iterator = iterator;
        this.selectedFields = selectedFields;
        this.callbacks = callbacks;
    }

    public ProjectionIterator(final ScrollableResults results, List<TypedExpression> selectedFields, Set<EndOfResultsCallback> callbacks) {
        this(new Iterator<Object>() {
            public boolean hasNext() {
                return results.next();
            }

            public Object next() {
                return results.get();
            }

            public void remove() {
            }
        }, selectedFields, callbacks);
    }

    public boolean hasNext() {
        boolean hasNext = iterator.hasNext();
        if (!hasNext) {
            notifyCallbacks();
        }
        return hasNext;
    }

    private void notifyCallbacks() {
        for (EndOfResultsCallback callback : callbacks) {
            try {
                callback.onEndOfResults();
            } catch (Throwable t) {
                LOGGER.error(t);
            }
        }
    }

    public DataRecord next() {
        DataRecord record;
        try {
            final ComplexTypeMetadata explicitProjectionType = new ComplexTypeMetadataImpl(StringUtils.EMPTY, Storage.PROJECTION_TYPE, false);
            record = new DataRecord(explicitProjectionType, UnsupportedDataRecordMetadata.INSTANCE);
            Object[] values;

            Object next = iterator.next();
            if (next instanceof Object[]) {
                values = (Object[]) next;
            } else {
                values = new Object[]{next};
            }

            int i = 0;
            for (Object value : values) {
                TypedExpression typedExpression = selectedFields.get(i++);
                FieldMetadata field = typedExpression.accept(new VisitorAdapter<FieldMetadata>() {
                    boolean isAlias = false;

                    private SimpleTypeFieldMetadata createField(String typeName, String fieldName) {
                        SimpleTypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName);
                        return new SimpleTypeFieldMetadata(explicitProjectionType, false, false, false, fieldName, fieldType, Collections.<String>emptyList(), Collections.<String>emptyList());
                    }

                    @Override
                    public FieldMetadata visit(Count count) {
                        // Do nothing, count is expected to be nested in a com.amalto.core.query.user.Alias.
                        return null;
                    }

                    @Override
                    public FieldMetadata visit(Alias alias) {
                        isAlias = true;
                        alias.getTypedExpression().accept(this);
                        return createField(alias.getTypeName(), alias.getAliasName());
                    }

                    @Override
                    public FieldMetadata visit(StringConstant constant) {
                        return null;
                    }

                    @Override
                    public FieldMetadata visit(Timestamp timestamp) {
                        if (!isAlias) {
                            return createField(Timestamp.TIMESTAMP_TYPE_NAME, Storage.METADATA_TIMESTAMP);
                        }
                        return null;
                    }

                    @Override
                    public FieldMetadata visit(TaskId taskId) {
                        if (!isAlias) {
                            return createField(TaskId.TASK_ID_TYPE_NAME, Storage.METADATA_TASK_ID);
                        }
                        return null;
                    }

                    @Override
                    public FieldMetadata visit(Revision revision) {
                        if (!isAlias) {
                            return createField(Revision.REVISION_TYPE_NAME, Storage.METADATA_REVISION_ID);
                        }
                        return null;
                    }

                    @Override
                    public FieldMetadata visit(Field field) {
                        if (!isAlias) {
                            return field.getFieldMetadata();
                        }
                        return null;
                    }
                });
                explicitProjectionType.addField(field);
                record.set(field, value);
            }
            explicitProjectionType.freeze();
        } catch (Exception e) {
            notifyCallbacks();
            throw new RuntimeException(e);
        }
        return record;
    }

    public void remove() {
    }

    public void close() throws IOException {
        notifyCallbacks();
    }
}

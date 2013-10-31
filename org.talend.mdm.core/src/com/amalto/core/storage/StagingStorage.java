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

package com.amalto.core.storage;

import com.amalto.core.query.user.Expression;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;
import com.amalto.core.storage.task.StagingConstants;
import com.amalto.core.storage.transaction.StorageTransaction;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import java.util.*;

public class StagingStorage implements Storage {

    private static final StagingUpdateAction defaultUpdateAction = new StagingUpdateAction(StagingConstants.NEW, true);

    private final Storage delegate;

    private final Map<String, StagingUpdateAction> updateActions = new HashMap<String, StagingUpdateAction>();

    public StagingStorage(Storage delegate) {
        if (delegate.getType() != StorageType.STAGING) {
            throw new IllegalArgumentException("Storage is not a staging storage (is a " + delegate.getType() + ").");
        }
        this.delegate = delegate;
        // Initialize staging update actions
        updateActions.put(StagingConstants.NEW, new StagingUpdateAction(StagingConstants.NEW, true));
        updateActions.put(StagingConstants.SUCCESS_MERGE_CLUSTERS, new StagingUpdateAction(StagingConstants.NEW, true));
        updateActions.put(StagingConstants.SUCCESS_MERGED_RECORD_TO_RESOLVE, new StagingUpdateAction(StagingConstants.SUCCESS_MERGED_RECORD_TO_RESOLVE, false));
        updateActions.put(StagingConstants.SUCCESS_VALIDATE, new StagingUpdateAction(StagingConstants.SUCCESS_MERGED_RECORD, false));
        updateActions.put(StagingConstants.DELETED, new StagingUpdateAction(StagingConstants.DELETED, false));
        updateActions.put(StagingConstants.FAIL_VALIDATE_VALIDATION, new StagingUpdateAction(StagingConstants.SUCCESS_MERGED_RECORD, false));
        updateActions.put(StagingConstants.FAIL_VALIDATE_CONSTRAINTS, new StagingUpdateAction(StagingConstants.SUCCESS_MERGED_RECORD, false));
        updateActions.put(StagingConstants.SUCCESS_MERGED_RECORD, new StagingUpdateAction(StagingConstants.SUCCESS_MERGED_RECORD, false));
        updateActions.put(StagingConstants.TASK_RESOLVED_RECORD, new StagingUpdateAction(StagingConstants.TASK_RESOLVED_RECORD, false));
        updateActions.put(StagingConstants.NEED_REMATCH, new StagingUpdateAction(StagingConstants.NEED_REMATCH, false));
    }

    @Override
    public Storage asInternal() {
        return delegate.asInternal();
    }

    @Override
    public int getCapabilities() {
        return delegate.getCapabilities();
    }

    @Override
    public StorageTransaction newStorageTransaction() {
        return delegate.newStorageTransaction();
    }

    @Override
    public void init(DataSource dataSource) {
        delegate.init(dataSource);
    }

    @Override
    public void prepare(MetadataRepository repository, Set<Expression> optimizedExpressions, boolean force, boolean dropExistingData) {
        delegate.prepare(repository, optimizedExpressions, force, dropExistingData);
    }

    @Override
    public void prepare(MetadataRepository repository, boolean dropExistingData) {
        delegate.prepare(repository, dropExistingData);
    }

    @Override
    public MetadataRepository getMetadataRepository() {
        return delegate.getMetadataRepository();
    }

    @Override
    public StorageResults fetch(Expression userQuery) {
        return delegate.fetch(userQuery);
    }

    @Override
    public void update(DataRecord record) {
        update(Collections.singleton(record));
    }

    @Override
    public void update(Iterable<DataRecord> records) {
        final TransformIterator iterator = new TransformIterator(records.iterator(), new Transformer() {
            @Override
            public Object transform(Object input) {
                DataRecord dataRecord = (DataRecord) input;
                DataRecordMetadata metadata = dataRecord.getRecordMetadata();
                Map<String, String> recordProperties = metadata.getRecordProperties();
                // Update on a record in staging reset all its match&merge information.
                StagingUpdateAction updateAction = updateActions.get(recordProperties.get(METADATA_STAGING_STATUS));
                if (updateAction == null) {
                    updateAction = defaultUpdateAction; // Covers cases where update action isn't specified.
                }
                recordProperties.put(Storage.METADATA_STAGING_STATUS, updateAction.value());
                recordProperties.put(Storage.METADATA_STAGING_ERROR, StringUtils.EMPTY);
                if (updateAction.resetTaskId()) {
                    metadata.setTaskId(null);
                }
                return dataRecord;
            }
        });
        Iterable<DataRecord> transformedRecords = new Iterable<DataRecord>() {
            @Override
            public Iterator<DataRecord> iterator() {
                return iterator;
            }
        };
        delegate.update(transformedRecords);
    }

    @Override
    public void delete(Expression userQuery) {
        StorageResults records = delegate.fetch(userQuery);
        for (DataRecord record : records) {
            record.getRecordMetadata().getRecordProperties().put(Storage.METADATA_STAGING_STATUS, StagingConstants.DELETED);
            delegate.update(record);
        }
    }

    @Override
    public void delete(DataRecord record) {
        delegate.delete(record);
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void close(boolean dropExistingData) {
        delegate.close(dropExistingData);
    }

    @Override
    public void begin() {
        delegate.begin();
    }

    @Override
    public void commit() {
        delegate.commit();
    }

    @Override
    public void rollback() {
        delegate.rollback();
    }

    @Override
    public void end() {
        delegate.end();
    }

    @Override
    public void reindex() {
        delegate.reindex();
    }

    @Override
    public Set<String> getFullTextSuggestion(String keyword, FullTextSuggestion mode, int suggestionSize) {
        return delegate.getFullTextSuggestion(keyword, mode, suggestionSize);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public DataSource getDataSource() {
        return delegate.getDataSource();
    }

    @Override
    public StorageType getType() {
        return delegate.getType();
    }

    private static class StagingUpdateAction {

        private final String value;

        private final boolean resetTaskId;

        StagingUpdateAction(String value, boolean resetTaskId) {
            this.value = value;
            this.resetTaskId = resetTaskId;
        }

        public String value() {
            return value;
        }

        public boolean resetTaskId() {
            return resetTaskId;
        }
    }
}

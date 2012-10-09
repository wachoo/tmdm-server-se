package com.amalto.core.storage.task;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.Select;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;

import java.util.Map;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class MergeTask extends MetadataRepositoryTask {

    private int recordsCount;

    MergeTask(Storage storage, MetadataRepository repository) {
        super(storage, repository);
    }

    @Override
    public String toString() {
        return "MERGE CLUSTERS"; //$NON-NLS-1$
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type) {
        Select query = from(type).where(eq(status(), StagingConstants.SUCCESS_IDENTIFIED_CLUSTERS)).getSelect();
        StorageResults records = storage.fetch(query);
        try {
            recordsCount += records.getCount();
        } finally {
            records.close();
        }
        return new SingleThreadedTask(type.getName(), storage, query, new MergeClosure(storage));
    }

    @Override
    public int getRecordCount() {
        return recordsCount;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    private static class MergeClosure implements Closure {
        private final Storage storage;

        public MergeClosure(Storage storage) {
            this.storage = storage;
        }

        public void begin() {
            storage.begin();
        }

        public boolean execute(DataRecord stagingRecord) {
            Map<String, String> recordProperties = stagingRecord.getRecordMetadata().getRecordProperties();
            recordProperties.put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_MERGE_CLUSTERS);
            storage.update(stagingRecord);
            return true;
        }

        public void end() {
            storage.commit();
        }

        public Closure copy() {
            return new MergeClosure(storage);
        }
    }
}

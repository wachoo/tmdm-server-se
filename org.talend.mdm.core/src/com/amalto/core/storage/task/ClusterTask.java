package com.amalto.core.storage.task;

import com.amalto.core.storage.transaction.Transaction;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import com.amalto.core.query.user.Select;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;

import java.util.Map;
import java.util.UUID;

import static com.amalto.core.query.user.UserQueryBuilder.*;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class ClusterTask extends MetadataRepositoryTask {

    private int recordsCount;

    ClusterTask(Storage storage, Transaction transaction, MetadataRepository repository, ClosureExecutionStats stats) {
        super(transaction, storage, repository, stats);
    }

    @Override
    public String toString() {
        return "CLUSTER IDENTIFICATION"; //$NON-NLS-1$
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type) {
        Select query = from(type).where(or(eq(status(), "0"), isNull(status()))).getSelect();
        StorageResults records = storage.fetch(query); // Expects an active transaction here
        try {
            recordsCount += records.getCount();
        } finally {
            records.close();
        }
        return new SingleThreadedTask(transaction,
                type.getName(),
                storage,
                query,
                new ClusterClosure(storage),
                stats);
    }

    @Override
    public int getRecordCount() {
        return recordsCount;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    private static class ClusterClosure implements Closure {

        private final Storage storage;

        public ClusterClosure(Storage storage) {
            this.storage = storage;
        }

        public void begin() {
        }

        public void execute(DataRecord stagingRecord, ClosureExecutionStats stats) {
            String taskId = UUID.randomUUID().toString();
            DataRecordMetadata recordMetadata = stagingRecord.getRecordMetadata();
            Map<String, String> recordProperties = recordMetadata.getRecordProperties();
            recordMetadata.setTaskId(taskId);
            recordProperties.put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_IDENTIFIED_CLUSTERS);
            storage.update(stagingRecord);
        }

        @Override
        public void cancel() {
        }

        public void end(ClosureExecutionStats stats) {
        }

        public Closure copy() {
            return new ClusterClosure(storage);
        }
    }
}

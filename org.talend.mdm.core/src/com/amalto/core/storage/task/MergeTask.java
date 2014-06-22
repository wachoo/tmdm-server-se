package com.amalto.core.storage.task;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.OrderBy;
import com.amalto.core.query.user.Select;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.amalto.core.query.user.UserQueryBuilder.*;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class MergeTask extends MetadataRepositoryTask {

    private int recordsCount;

    MergeTask(Storage storage, MetadataRepository repository, ClosureExecutionStats stats) {
        super(storage, repository, stats);
    }

    @Override
    public String toString() {
        return "MERGE CLUSTERS"; //$NON-NLS-1$
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type) {
        Select query = from(type)
                .where(eq(status(), StagingConstants.SUCCESS_IDENTIFIED_CLUSTERS))
                .orderBy(taskId(), OrderBy.Direction.ASC)
                .getSelect();
        StorageResults records = storage.fetch(query);
        try {
            recordsCount += records.getCount();
        } finally {
            records.close();
        }
        return new SingleThreadedTask(type.getName(), storage, query, new MergeClosure(storage), stats);
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

        private final LinkedList<DataRecord> groupRecord = new LinkedList<DataRecord>();

        public MergeClosure(Storage storage) {
            this.storage = storage;
        }

        public void begin() {
            storage.begin();
        }

        public void execute(DataRecord stagingRecord, ClosureExecutionStats stats) {
            if (!groupRecord.isEmpty()) {
                String lastTaskId = groupRecord.getLast().getRecordMetadata().getTaskId();
                String currentTaskId = stagingRecord.getRecordMetadata().getTaskId();
                if ((currentTaskId == null || "null".equals(currentTaskId)) || !lastTaskId.equals(currentTaskId)) { //$NON-NLS-1$ // Update status of last record of group.
                    setGoldenRecord(groupRecord);
                    groupRecord.clear();
                }
            }
            groupRecord.add(stagingRecord);
        }

        @Override
        public void cancel() {
        }

        private void setGoldenRecord(List<DataRecord> stagingRecords) {
            if (stagingRecords.size() == 1) {
                DataRecord stagingRecord = stagingRecords.get(0);
                DataRecordMetadata recordMetadata = stagingRecord.getRecordMetadata();
                Map<String, String> recordProperties = recordMetadata.getRecordProperties();
                recordProperties.put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_MERGED_RECORD);
                storage.update(stagingRecord);
            } else {
                for (DataRecord stagingRecord : stagingRecords) {
                    DataRecordMetadata recordMetadata = stagingRecord.getRecordMetadata();
                    Map<String, String> recordProperties = recordMetadata.getRecordProperties();
                    recordProperties.put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_MERGE_CLUSTER_TO_RESOLVE);
                    storage.update(stagingRecord);
                }
            }
        }

        public void end(ClosureExecutionStats stats) {
            if (!groupRecord.isEmpty()) {
                setGoldenRecord(groupRecord);
            }
            storage.commit();
        }

        public Closure copy() {
            return new MergeClosure(storage);
        }
    }
}

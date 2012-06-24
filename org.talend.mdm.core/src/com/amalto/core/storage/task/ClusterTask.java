package com.amalto.core.storage.task;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.Select;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.record.DataRecord;

import java.util.Map;
import java.util.UUID;

import static com.amalto.core.query.user.UserQueryBuilder.from;
import static com.amalto.core.query.user.UserQueryBuilder.isEmpty;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class ClusterTask extends MetadataRepositoryTask {

    ClusterTask(Storage storage, MetadataRepository repository) {
        super(storage, repository);
    }

    @Override
    public String toString() {
        return "CLUSTER IDENTIFICATION";
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type) {
        Select query = from(type).where(isEmpty(status())).getSelect();
        return new SingleThreadedTask(type.getName(), storage, query, new ClusterClosure(storage));
    }

    private static class ClusterClosure implements Closure {

        private Storage storage;

        public ClusterClosure(Storage storage) {
            this.storage = storage;
        }

        public void begin() {
            storage.begin();
        }

        public void execute(DataRecord record) {
            Map<String, String> recordProperties = record.getRecordMetadata().getRecordProperties();
            recordProperties.put(Storage.METADATA_TASK_ID, UUID.randomUUID().toString());
            recordProperties.put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_IDENTIFIED_CLUSTERS);
            storage.update(record);
        }

        public void end() {
            storage.commit();
        }

        public Closure copy() {
            return new ClusterClosure(storage);
        }
    }
}

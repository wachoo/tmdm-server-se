package com.amalto.core.storage.task;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.Select;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.enhancement.TypeMappingRepository;
import com.amalto.core.storage.record.DataRecord;

import java.util.Map;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class MergeTask extends MetadataRepositoryTask {

    MergeTask(Storage storage, MetadataRepository repository) {
        super(storage, repository);
    }

    @Override
    public String toString() {
        return "MERGE CLUSTERS";
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type) {
        Select query = from(type).where(eq(status(), StagingConstants.SUCCESS_IDENTIFIED_CLUSTERS)).getSelect();
        return new SingleThreadedTask(type.getName(), storage, query, new MergeClosure(storage));
    }

    private static class MergeClosure implements Closure {
        private Storage storage;

        public MergeClosure(Storage storage) {
            this.storage = storage;
        }

        public void begin() {
            storage.begin();
        }

        public void execute(DataRecord record) {
            Map<String,String> recordProperties = record.getRecordMetadata().getRecordProperties();
            recordProperties.put(TypeMappingRepository.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_MERGE_CLUSTERS);
            storage.update(record);
        }

        public void end() {
            storage.commit();
        }

        public Closure copy() {
            return new MergeClosure(storage);
        }
    }
}

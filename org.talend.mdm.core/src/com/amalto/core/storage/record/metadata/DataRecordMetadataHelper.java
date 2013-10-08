package com.amalto.core.storage.record.metadata;

import com.amalto.core.query.user.metadata.StagingBlockKey;
import com.amalto.core.query.user.metadata.StagingError;
import com.amalto.core.query.user.metadata.StagingSource;
import com.amalto.core.query.user.metadata.StagingStatus;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.record.DataRecordReader;

import java.util.Map;

/**
 *
 */
public class DataRecordMetadataHelper {

    private DataRecordMetadataHelper() {
    }

    public static void setMetadataValue(DataRecordMetadata metadata, String metadataProperty, String value) {
        Map<String, String> properties = metadata.getRecordProperties();
        if(StagingError.STAGING_ERROR_ALIAS.equals(metadataProperty)) {
            properties.put(Storage.METADATA_STAGING_ERROR, value);
        } else if(StagingSource.STAGING_SOURCE_ALIAS.equals(metadataProperty)) {
            properties.put(Storage.METADATA_STAGING_SOURCE, value);
        } else if(StagingStatus.STAGING_STATUS_ALIAS.equals(metadataProperty)) {
            properties.put(Storage.METADATA_STAGING_STATUS, value);
        } else if (StagingBlockKey.STAGING_BLOCK_ALIAS.equals(metadataProperty)) {
            properties.put(Storage.METADATA_STAGING_BLOCK_KEY, value);
        } else if (DataRecordReader.TASK_ID.equals(metadataProperty)) {
            metadata.setTaskId(value);
        } else {
            throw new UnsupportedOperationException("Metadata parameter '" + metadataProperty + "' is not supported.");
        }
    }
}

package com.amalto.core.query.user.metadata;

import java.util.Map;

import com.amalto.core.storage.record.DataRecord;

class PropertyReader implements MetadataField.Reader {

    private final String propertyName;

    public PropertyReader(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public Object readValue(DataRecord record) {
        Map<String, String> recordProperties = record.getRecordMetadata().getRecordProperties();
        if (recordProperties != null) {
            return recordProperties.get(propertyName);
        }
        return null;
    }
}

package org.talend.mdm.storage.inmemory.matcher;

import org.apache.commons.lang.NotImplementedException;

import com.amalto.core.query.user.Predicate;
import org.talend.mdm.storage.StagingStorage;
import org.talend.mdm.storage.record.DataRecord;

public class BuiltInBlockKeyMatcher implements Matcher {

    private final Predicate predicate;

    private final String value;

    public BuiltInBlockKeyMatcher(Predicate predicate, String value) {
        this.predicate = predicate;
        this.value = value;
    }

    @Override
    public boolean match(DataRecord record) {
        Object recordValue = record.getRecordMetadata().getRecordProperties().get(StagingStorage.METADATA_STAGING_BLOCK_KEY);
        if (recordValue == null) {
            return false;
        }
        if (predicate == Predicate.CONTAINS) {
            return recordValue.toString().indexOf(value) > 0;
        } else if (predicate == Predicate.EQUALS) {
            return recordValue.toString().equals(value);
        } else if (predicate == Predicate.STARTS_WITH) {
            return recordValue.toString().indexOf(value) == 0;
        } else {
            throw new NotImplementedException();
        }
    }
}

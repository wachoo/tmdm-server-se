package com.amalto.core.storage.inmemory.matcher;

import com.amalto.core.storage.record.DataRecord;

/**
 *
 */
public interface Matcher {

    boolean match(DataRecord record);

}

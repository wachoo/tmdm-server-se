package com.amalto.core.storage.inmemory.matcher;

import com.amalto.core.storage.record.DataRecord;

public class MatchAll implements Matcher {
    @Override
    public boolean match(DataRecord record) {
        return true;
    }
}

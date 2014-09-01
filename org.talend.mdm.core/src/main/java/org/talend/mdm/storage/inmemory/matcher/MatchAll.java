package org.talend.mdm.storage.inmemory.matcher;

import org.talend.mdm.storage.record.DataRecord;

public class MatchAll implements Matcher {
    @Override
    public boolean match(DataRecord record) {
        return true;
    }
}

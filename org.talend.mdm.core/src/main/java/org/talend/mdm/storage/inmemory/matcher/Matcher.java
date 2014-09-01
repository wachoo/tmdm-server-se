package org.talend.mdm.storage.inmemory.matcher;

import org.talend.mdm.storage.record.DataRecord;

/**
 *
 */
public interface Matcher {

    boolean match(DataRecord record);

}

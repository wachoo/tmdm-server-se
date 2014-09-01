package org.talend.mdm.storage;

import org.talend.mdm.storage.hibernate.CloseableIterator;
import org.talend.mdm.storage.record.DataRecord;

import java.io.IOException;

/**
*
*/
public class EmptyIterator implements CloseableIterator<DataRecord> {
    public static final CloseableIterator<DataRecord> INSTANCE = new EmptyIterator();

    private EmptyIterator() {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public DataRecord next() {
        return null;
    }

    @Override
    public void remove() {
    }
}

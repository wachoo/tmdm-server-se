package com.amalto.core.storage;

import com.amalto.core.storage.CloseableIterator;
import com.amalto.core.storage.record.DataRecord;

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

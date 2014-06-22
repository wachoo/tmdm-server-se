package com.amalto.core.storage;

import java.io.IOException;

import com.amalto.core.storage.hibernate.CloseableIterator;
import com.amalto.core.storage.record.DataRecord;

public class EmptyIterator extends CloseableIterator<DataRecord> {
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
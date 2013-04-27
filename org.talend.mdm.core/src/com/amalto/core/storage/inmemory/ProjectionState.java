package com.amalto.core.storage.inmemory;

import com.amalto.core.storage.EmptyIterator;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.CloseableIterator;
import com.amalto.core.storage.record.DataRecord;

/**
*
*/
class ProjectionState implements State {
    @Override
    public CloseableIterator<DataRecord> process(Storage storage, InMemoryJoinNode node) {
        return EmptyIterator.INSTANCE; // TODO
    }
}

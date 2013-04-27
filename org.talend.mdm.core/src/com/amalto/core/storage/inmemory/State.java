package com.amalto.core.storage.inmemory;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.CloseableIterator;
import com.amalto.core.storage.record.DataRecord;

/**
*
*/
interface State {
    CloseableIterator<DataRecord> process(Storage storage, InMemoryJoinNode node);
}

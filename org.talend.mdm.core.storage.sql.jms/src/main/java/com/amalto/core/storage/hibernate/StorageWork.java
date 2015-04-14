package com.amalto.core.storage.hibernate;

import java.io.Serializable;

import org.hibernate.search.backend.Work;
import org.hibernate.search.backend.WorkType;

class StorageWork<T> extends Work<T> {

    private final String storageName;

    public StorageWork(Class<T> entityType, Serializable id, WorkType type, String storageName) {
        super(entityType, id, type);
        this.storageName = storageName;
    }

    public String getStorageName() {
        return storageName;
    }
}

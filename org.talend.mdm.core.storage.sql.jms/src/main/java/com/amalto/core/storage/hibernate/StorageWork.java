package com.amalto.core.storage.hibernate;

import java.io.Serializable;

import org.hibernate.search.backend.WorkType;

class StorageWork<T> implements Serializable {

    private final Class<T> entityType;

    private final Serializable id;

    private final WorkType type;

    private final String storageName;

    public StorageWork(Class<T> entityType, Serializable id, WorkType type, String storageName) {
        this.entityType = entityType;
        this.id = id;
        this.type = type;
        this.storageName = storageName;
    }

    public String getStorageName() {
        return storageName;
    }

    public Class<T> getEntityType() {
        return entityType;
    }

    public Serializable getId() {
        return id;
    }

    public WorkType getType() {
        return type;
    }
}

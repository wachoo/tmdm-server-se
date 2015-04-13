package com.amalto.core.storage.hibernate;

import org.hibernate.annotations.common.reflection.XMember;
import org.hibernate.search.backend.Work;
import org.hibernate.search.backend.WorkType;

import java.io.Serializable;

public class MDMWork<T> extends Work<T> {

    private final String storageName;

    public MDMWork(T entity, Serializable id, WorkType type, String storageName) {
        super(entity, id, type);
        this.storageName = storageName;
    }

    public MDMWork(Class<T> entityType, Serializable id, WorkType type, String storageName) {
        super(entityType, id, type);
        this.storageName = storageName;
    }

    public MDMWork(T entity, XMember idGetter, WorkType type, String storageName) {
        super(entity, idGetter, type);
        this.storageName = storageName;
    }

    public String getStorageName() {
        return storageName;
    }
}

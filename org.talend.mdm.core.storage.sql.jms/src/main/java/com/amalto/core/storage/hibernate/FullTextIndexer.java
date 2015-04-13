package com.amalto.core.storage.hibernate;

import java.io.Serializable;

import org.hibernate.Hibernate;
import org.hibernate.event.AbstractEvent;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.backend.Work;
import org.hibernate.search.backend.WorkType;
import org.hibernate.search.event.FullTextIndexEventListener;

import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;

public class FullTextIndexer extends FullTextIndexEventListener {

    @Override
    protected <T> void processWork(T entity, Serializable id, WorkType workType, AbstractEvent event) {
        Work<T> work = new Work<T>(entity, id, workType);
        Class<T> entityClass = work.getEntityClass() != null ? work.getEntityClass() : Hibernate.getClass(work.getEntity());
        if (entityClass.getAnnotation(Indexed.class) != null) {
            StorageAdmin admin = ServerContext.INSTANCE.get().getStorageAdmin();
            String[] containers = admin.getAll(null);
            for (String container : containers) {
                Storage storage = admin.get(container, StorageType.MASTER, null);
                // Storage might be hidden, call asInternal() to get actual storage.
                Storage internal = storage.asInternal();
                if (internal instanceof HibernateStorage) {
                    final HibernateStorage hibernateStorage = (HibernateStorage) internal;
                    StorageClassLoader classLoader = hibernateStorage.getClassLoader();
                    if (classLoader.knownTypes.containsKey(entityClass)) {
                        JMSHolder.addWorkToQueue(entityClass, id, storage.getName(), workType);
                        break;
                    }
                }
            }
        }
        super.processWork(entity, id, workType, event);
    }

}

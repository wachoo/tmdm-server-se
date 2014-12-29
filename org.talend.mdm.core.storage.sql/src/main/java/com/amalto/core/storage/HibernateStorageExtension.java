package com.amalto.core.storage;

import com.amalto.core.server.StorageExtension;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.inmemory.MemoryStorage;

import java.util.UUID;

public class HibernateStorageExtension implements StorageExtension {
    @Override
    public boolean accept(DataSource dataSource) {
        return dataSource instanceof RDBMSDataSource;
    }

    @Override
    public Storage create(String storageName, StorageType storageType) {
        return new HibernateStorage(storageName, storageType);
    }

    @Override
    public Storage createTemporary(StorageType storageType) {
        return new MemoryStorage(UUID.randomUUID().toString(), storageType);
    }
}

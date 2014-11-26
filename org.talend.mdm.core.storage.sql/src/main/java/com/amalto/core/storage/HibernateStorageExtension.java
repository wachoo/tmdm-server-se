package com.amalto.core.storage;

import com.amalto.core.server.StorageExtension;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import com.amalto.core.storage.hibernate.HibernateStorage;

public class HibernateStorageExtension implements StorageExtension {
    @Override
    public boolean accept(DataSourceDefinition definition, StorageType storageType) {
        return definition.get(storageType) instanceof RDBMSDataSource;
    }

    @Override
    public Storage create(String storageName, StorageType storageType) {
        return new HibernateStorage(storageName, storageType);
    }
}

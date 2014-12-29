/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.server;

import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import org.apache.commons.lang.NotImplementedException;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSourceFactory;
import com.amalto.core.storage.hibernate.HibernateStorage;

public class MockServerLifecycle implements ServerLifecycle {

    @Override
    public Server createServer() {
        MDMConfiguration.getConfiguration().setProperty(DataSourceFactory.DB_DATASOURCES, MockServer.getDatasourcesFilePath());
        return new MockServer();
    }

    @Override
    public void destroyServer(Server server) {
        // nothing to do
    }

    @Override
    public StorageAdmin createStorageAdmin() {
        return new StorageAdminImpl();
    }

    @Override
    public void destroyStorageAdmin(StorageAdmin storageAdmin) {
        storageAdmin.close();
    }

    @Override
    public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
        return MockMetadataRepositoryAdmin.INSTANCE;
    }

    @Override
    public void destroyMetadataRepositoryAdmin(MetadataRepositoryAdmin metadataRepositoryAdmin) {
        metadataRepositoryAdmin.close();
    }

    @Override
    public Storage createStorage(String storageName, StorageType storageType, DataSourceDefinition definition) {
        HibernateStorage storage = new HibernateStorage(storageName, storageType);
        storage.init(definition);
        return storage;
    }

    @Override
    public Storage createTemporaryStorage(DataSource dataSource, StorageType storageType) {
        throw new NotImplementedException();
    }

    @Override
    public void destroyStorage(Storage storage, boolean dropExistingData) {
        storage.close(true);
    }

}

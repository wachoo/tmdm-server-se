/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.server;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSourceFactory;
import com.amalto.core.storage.hibernate.HibernateStorage;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

public class MockServerLifecycle implements ServerLifecycle {

    public Server createServer() {
        MDMConfiguration.getConfiguration().setProperty(DataSourceFactory.DB_DATASOURCES, "datasources.xml");
        return new MockServer();
    }

    public void destroyServer(Server server) {
    }

    public StorageAdmin createStorageAdmin() {
        return new StorageAdminImpl();
    }

    public void destroyStorageAdmin(StorageAdmin storageAdmin) {
        storageAdmin.close();
    }

    public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
        return new MockMetadataRepositoryAdmin();
    }

    public void destroyMetadataRepositoryAdmin(MetadataRepositoryAdmin metadataRepositoryAdmin) {
        metadataRepositoryAdmin.close();
    }

    public Storage createStorage(String storageName, String dataSourceName, StorageType storageType) {
        return new HibernateStorage(storageName, storageType);
    }

    public void destroyStorage(Storage storage) {
        storage.close();
    }

}

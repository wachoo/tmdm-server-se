// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.server;

import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceFactory;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Map;

public class MockServer implements Server {

    private final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

    private StorageAdmin storageAdmin;

    private MetadataRepositoryAdmin metadataRepositoryAdmin;

    public MockServer() {
    }

    public DataSource getDataSource(String dataSourceName, String container) {
        return DataSourceFactory.getInstance().getDataSource(MockServer.class.getResourceAsStream("datasources.xml"), dataSourceName, container);
    }

    public StorageAdmin getStorageAdmin() {
        if (storageAdmin == null) {
            storageAdmin = ServerContext.INSTANCE.getLifecycle().createStorageAdmin();
        }
        return storageAdmin;
    }

    public MetadataRepositoryAdmin getMetadataRepositoryAdmin() {
        if (metadataRepositoryAdmin == null) {
            metadataRepositoryAdmin = ServerContext.INSTANCE.getLifecycle().createMetadataRepositoryAdmin();
        }
        return metadataRepositoryAdmin;
    }

    public MBeanServer getMBeanServer() {
        return platformMBeanServer;
    }

    public void close() {
        ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
        if (metadataRepositoryAdmin != null) {
            lifecycle.destroyMetadataRepositoryAdmin(metadataRepositoryAdmin);
        }
        if (storageAdmin != null) {
            lifecycle.destroyStorageAdmin(storageAdmin);
        }
    }

    public void init() {
    }
}

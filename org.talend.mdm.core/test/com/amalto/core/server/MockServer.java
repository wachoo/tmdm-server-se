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

import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.datasource.DataSourceFactory;
import org.apache.commons.lang.NotImplementedException;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

public class MockServer implements Server {

    private final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

    private final DataSourceFactory dataSourceFactory = DataSourceFactory.getInstance();

    private StorageAdmin storageAdmin;

    private MetadataRepositoryAdmin metadataRepositoryAdmin;

    public MockServer() {
    }

    @Override
    public boolean hasDataSource(String dataSourceName, String container, StorageType type) {
        boolean isDataSourceDefinitionPresent = dataSourceFactory.hasDataSource(MockServer.class.getResourceAsStream("datasources.xml"), dataSourceName);
        if (isDataSourceDefinitionPresent) {
            DataSourceDefinition dataSource = dataSourceFactory.getDataSource(dataSourceName, container);
            switch (type) {
                case MASTER:
                    return dataSource.getMaster() != null;
                case STAGING:
                    return dataSource.getStaging() != null;
                default:
                    throw new NotImplementedException("Not supported: " + type);
            }
        }
        return isDataSourceDefinitionPresent;
    }

    public DataSource getDataSource(String dataSourceName, String container, StorageType type) {
        DataSourceDefinition configuration = dataSourceFactory.getDataSource(MockServer.class.getResourceAsStream("datasources.xml"), dataSourceName, container);
        switch (type) {
            case MASTER:
                return configuration.getMaster();
            case STAGING:
                if (!configuration.hasStaging()) {
                    throw new IllegalArgumentException("Datasource '" + dataSourceName + "' does not declare a staging area.");
                }
                return configuration.getStaging();
            default:
                throw new NotImplementedException("Not supported: " + type);
        }
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

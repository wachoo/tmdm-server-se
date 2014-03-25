// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang.NotImplementedException;

import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.datasource.DataSourceFactory;

@SuppressWarnings("nls")
public class MockServer implements Server {

    private final DataSourceFactory dataSourceFactory = DataSourceFactory.getInstance();

    private StorageAdmin storageAdmin;

    private MetadataRepositoryAdmin metadataRepositoryAdmin;

    private static final String DATASOURCES_FILE = "com/amalto/core/server/datasources-test.xml";

    public MockServer() {
    }

    static String getDatasourcesFilePath() {
        URL url = MockServer.class.getClassLoader().getResource(DATASOURCES_FILE);
        junit.framework.Assert.assertNotNull(url);
        return new File(url.getFile()).getAbsolutePath();
    }

    static InputStream getDatasourcesInputStream() {
        InputStream is = MockServer.class.getClassLoader().getResourceAsStream(DATASOURCES_FILE);
        junit.framework.Assert.assertNotNull(is);
        return is;
    }

    @Override
    public boolean hasDataSource(String dataSourceName, String container, StorageType type) {
        boolean isDataSourceDefinitionPresent = dataSourceFactory.hasDataSource(getDatasourcesInputStream(), dataSourceName);
        if (isDataSourceDefinitionPresent) {
            DataSourceDefinition dataSource = dataSourceFactory.getDataSource(dataSourceName, container, null);
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

    @Override
    public DataSource getDataSource(String dataSourceName, String container, StorageType type) {
        return getDataSource(dataSourceName, container, null, type);
    }

    @Override
    public DataSource getDataSource(String dataSourceName, String container, String revisionId, StorageType type) {
        DataSourceDefinition configuration = dataSourceFactory.getDataSource(getDatasourcesInputStream(), dataSourceName,
                container, revisionId);
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

    @Override
    public StorageAdmin getStorageAdmin() {
        if (storageAdmin == null) {
            storageAdmin = ServerContext.INSTANCE.getLifecycle().createStorageAdmin();
        }
        return storageAdmin;
    }

    @Override
    public MetadataRepositoryAdmin getMetadataRepositoryAdmin() {
        if (metadataRepositoryAdmin == null) {
            metadataRepositoryAdmin = ServerContext.INSTANCE.getLifecycle().createMetadataRepositoryAdmin();
        }
        return metadataRepositoryAdmin;
    }

    @Override
    public void close() {
        ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
        if (metadataRepositoryAdmin != null) {
            lifecycle.destroyMetadataRepositoryAdmin(metadataRepositoryAdmin);
        }
        if (storageAdmin != null) {
            lifecycle.destroyStorageAdmin(storageAdmin);
        }
    }

    @Override
    public void init() {
        // nothing to do
    }
}

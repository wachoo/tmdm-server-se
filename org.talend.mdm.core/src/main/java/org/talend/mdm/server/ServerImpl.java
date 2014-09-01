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

package org.talend.mdm.server;

import org.talend.mdm.storage.StorageType;
import org.talend.mdm.storage.datasource.DataSourceDefinition;
import org.talend.mdm.storage.datasource.DataSourceFactory;
import org.talend.mdm.storage.transaction.TransactionManager;
import org.apache.commons.lang.NotImplementedException;

class ServerImpl implements Server {

    private final DataSourceFactory dataSourceFactory = DataSourceFactory.getInstance();

    private MDMTransactionManager transactionManager;

    private StorageAdmin storageAdmin;

    private MetadataRepositoryAdmin metadataRepositoryAdmin;

    ServerImpl() {
    }

    @Override
    public boolean hasDataSource(String dataSourceName, String container, StorageType type) {
        boolean isDataSourceDefinitionPresent = dataSourceFactory.hasDataSource(dataSourceName);
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
        return false;
    }

    @Override
    public DataSourceDefinition getDefinition(String dataSourceName, String container) {
        return getDefinition(dataSourceName, container, null);
    }

    @Override
    public DataSourceDefinition getDefinition(String dataSourceName, String container, String revisionId) {
        return dataSourceFactory.getDataSource(dataSourceName, container, revisionId);
    }

    public StorageAdmin getStorageAdmin() {
        if (storageAdmin == null) {
            ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
            storageAdmin = lifecycle.createStorageAdmin();
        }
        return storageAdmin;
    }

    public MetadataRepositoryAdmin getMetadataRepositoryAdmin() {
        if (metadataRepositoryAdmin == null) {
            ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
            metadataRepositoryAdmin = lifecycle.createMetadataRepositoryAdmin();
        }
        return metadataRepositoryAdmin;
    }

    public void close() {
        ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
        lifecycle.destroyStorageAdmin(storageAdmin);
        lifecycle.destroyMetadataRepositoryAdmin(metadataRepositoryAdmin);
    }

    public void init() {
        transactionManager = new MDMTransactionManager();
        transactionManager.init();
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

}

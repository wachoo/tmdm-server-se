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

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.datasource.DataSourceFactory;
import com.amalto.core.util.Util;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Map;

class ServerImpl implements Server {

    private static final Logger LOGGER = Logger.getLogger(ServerImpl.class);

    private final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

    private final DataSourceFactory dataSourceFactory = DataSourceFactory.getInstance();

    private StorageAdmin storageAdmin;

    private MetadataRepositoryAdmin metadataRepositoryAdmin;

    ServerImpl() {
    }

    @Override
    public boolean hasDataSource(String dataSourceName, String container, StorageType type) {
        boolean isDataSourceDefinitionPresent = dataSourceFactory.hasDataSource(dataSourceName);
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
        DataSourceDefinition configuration = dataSourceFactory.getDataSource(dataSourceName, container);
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

    public MBeanServer getMBeanServer() {
        return platformMBeanServer;
    }

    public void close() {
        ServerLifecycle lifecycle = ServerContext.INSTANCE.getLifecycle();
        lifecycle.destroyStorageAdmin(storageAdmin);
        lifecycle.destroyMetadataRepositoryAdmin(metadataRepositoryAdmin);
    }

    public void init() {
        if (MDMConfiguration.getConfiguration().get(DataSourceFactory.DB_DATASOURCES) == null) {
            LOGGER.warn("Server is not configured for SQL storage.");
            return;
        }

        try {
            LOGGER.info("Creating SQL storage for containers...");
            DataClusterCtrlLocal dataClusterControl = Util.getDataClusterCtrlLocal();
            Collection<DataClusterPOJOPK> allContainers = dataClusterControl.getDataClusterPKs(".*"); //$NON-NLS-1$
            StorageAdmin serverStorageAdmin = getStorageAdmin();

            Map<String, XSystemObjects> xDataClustersMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
            for (DataClusterPOJOPK container : allContainers) {
                if (!xDataClustersMap.containsKey(container.getUniqueId())) {
                    try {
                        LOGGER.info("Creating SQL storage for container '" + container.getUniqueId() + "'...");
                        serverStorageAdmin.create(container.getUniqueId(), container.getUniqueId(), Storage.DEFAULT_DATA_SOURCE_NAME);
                        LOGGER.info("Created SQL storage for container '" + container.getUniqueId() + "'.");
                    } catch (Exception e) {
                        LOGGER.error("Could not create SQL storage for container '" + container.getUniqueId() + "'.", e);
                    }
                }
                // TODO Enable this for TMDM-4507: Migrate update report to SQL storage
                /*
                else if(XSystemObjects.DC_UPDATE_PREPORT.getName().equals(container.getUniqueId())) {
                    try {
                        LOGGER.info("Creating SQL storage for update reports...");
                        serverStorageAdmin.create(container.getUniqueId(), container.getUniqueId(), Storage.DEFAULT_DATA_SOURCE_NAME);
                        LOGGER.info("Created SQL storage for update reports.");
                    } catch (Exception e) {
                        LOGGER.error("Could not create SQL storage for update reports.", e);
                    }
                }
                */
            }
            LOGGER.info("Done.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

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

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.DataSourceFactory;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StorageAdminImpl implements StorageAdmin {

    private final Map<String, Storage> storages = new HashMap<String, Storage>();

    private Logger LOGGER = Logger.getLogger(StorageAdminImpl.class);

    // Default value is "false" (meaning the storage will not remove existing data).
    private static final boolean autoClean = Boolean.valueOf(MDMConfiguration.getConfiguration().getProperty("db.autoClean", "false"));

    public String[] getAll(String revisionID) {
        Set<String> allStorageNames = storages.keySet();
        return allStorageNames.toArray(new String[allStorageNames.size()]);
    }

    public void delete(String revisionID, String storageName) {
        Storage storage = storages.get(storageName);
        ServerContext.INSTANCE.getLifecycle().destroyStorage(storage);
        storages.remove(storageName);
    }

    public void deleteAll(String revisionID) {
        for (String clusterName : new HashSet<String>(storages.keySet())) {
            delete(revisionID, clusterName);
        }
    }

    public Storage create(String dataModelName, String storageName, String dataSourceName) {
        if (MDMConfiguration.getConfiguration().get(DataSourceFactory.DB_DATASOURCES) == null) {
            LOGGER.warn("Configuration does not allow creation of SQL storage for '" + dataModelName + "'.");
            return null;
        }
        String actualStorageName = StringUtils.substringBefore(storageName, STAGING_SUFFIX);
        String actualDataModelName = StringUtils.substringBefore(dataModelName, STAGING_SUFFIX);
        try {
            Storage masterDataModelStorage = internalCreateStorage(actualDataModelName, actualStorageName, dataSourceName, StorageType.MASTER);
            if (!XSystemObjects.DC_UPDATE_PREPORT.getName().equalsIgnoreCase(actualStorageName)) { //TODO would be better to decide whether a staging area should be created or not in a method.
                boolean hasDataSource = ServerContext.INSTANCE.get().hasDataSource(dataSourceName, actualStorageName, StorageType.STAGING);
                if (hasDataSource) {
                    internalCreateStorage(actualDataModelName + STAGING_SUFFIX, actualStorageName, dataSourceName, StorageType.STAGING);
                }
            }
            return masterDataModelStorage;
        } catch (Exception e) {
            throw new RuntimeException("Could not create storage '" + actualStorageName + "' with data model '" + dataModelName + "'.", e);
        }
    }

    // Returns null if storage can not be created (e.g. because of missing data source configuration).
    private Storage internalCreateStorage(String dataModelName, String storageName, String dataSourceName, StorageType storageType) {
        ServerContext instance = ServerContext.INSTANCE;
        DataSource dataSource = instance.get().getDataSource(dataSourceName, storageName, storageType);
        if (dataSource instanceof RDBMSDataSource) {
            // May get request for "StorageName/Concept", but for SQL it does not make any sense.
            // See com.amalto.core.storage.StorageWrapper.createCluster()
            storageName = StringUtils.substringBefore(storageName, "/"); //$NON-NLS-1$
            if (exist(null, storageName, storageType)) {
                LOGGER.warn("Storage for '" + storageName + "' already exists. It needs to be deleted before it can be recreated.");
                return get(storageName);
            }
            dataModelName = StringUtils.substringBefore(dataModelName, "/"); //$NON-NLS-1$
            if (storageType == StorageType.STAGING && !dataModelName.endsWith(STAGING_SUFFIX)) {
                dataModelName += STAGING_SUFFIX;
            }
            // Replace all container name, so re-read the configuration.
            dataSource = instance.get().getDataSource(dataSourceName, storageName, storageType);
        }
        if (!instance.get().hasDataSource(dataSourceName, storageName, storageType)) {
            LOGGER.warn("Can not initialize " + storageType + " storage for '" + storageName + "': data source '" + dataSourceName + "' configuration is incomplete.");
            return null;
        }
        Storage dataModelStorage = instance.getLifecycle().createStorage(storageName, dataSourceName, storageType);
        dataModelStorage.init(dataSource);
        MetadataRepositoryAdmin metadataRepositoryAdmin = instance.get().getMetadataRepositoryAdmin();
        boolean hasDataModel = metadataRepositoryAdmin.exist(dataModelName);
        if (!hasDataModel) {
            throw new UnsupportedOperationException("Data model '" + dataModelName + "' must exist before container '" + storageName + "' can be created.");
        }
        MetadataRepository metadataRepository = metadataRepositoryAdmin.get(dataModelName);
        Set<FieldMetadata> indexedFields = metadataRepositoryAdmin.getIndexedFields(dataModelName);
        try {
            dataModelStorage.prepare(metadataRepository, indexedFields, hasDataModel, autoClean);
        } catch (Exception e) {
            throw new RuntimeException("Could not create storage for container '" + storageName + "' (" + storageType + ") using data model '" + dataModelName + "'.", e);
        }
        if (storageType == StorageType.MASTER) {
            storages.put(storageName, dataModelStorage);
        } else {
            storages.put(storageName + STAGING_SUFFIX, dataModelStorage);
        }
        return dataModelStorage;
    }

    public boolean exist(String revision, String storageName, StorageType storageType) {
        Storage storage = storages.get(storageName);
        if (storageType == StorageType.STAGING && !storageName.endsWith(STAGING_SUFFIX)) {
            storage = storages.get(storageName + STAGING_SUFFIX);
        }
        return storage != null && storage.getType() == storageType;
    }

    public void close() {
        deleteAll(null);
    }

    public Storage get(String storageName) {
        Storage storage = storages.get(storageName);
        if (storage == null) {
            // May get request for "StorageName/Concept", but for SQL it does not make any sense.
            storageName = StringUtils.substringBefore(storageName, "/"); //$NON-NLS-1$
            storage = storages.get(storageName);
            if (storage != null && !(storage.getDataSource() instanceof RDBMSDataSource)) {
                throw new IllegalStateException("Expected a SQL storage for '" + storageName + "' but got a '" + storage.getClass().getName() + "'.");
            }
        }
        return storage;
    }
}

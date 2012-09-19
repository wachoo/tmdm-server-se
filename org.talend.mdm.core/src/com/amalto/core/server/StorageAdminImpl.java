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
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import java.util.*;

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
        if (exist(null, storageName)) {
            LOGGER.warn("Storage for '" + storageName + "' already exist. It needs to be deleted before it can be recreated.");
            return get(storageName);
        }
        try {
            Storage masterDataModelStorage = internalCreateStorage(dataModelName, storageName, dataSourceName, StorageType.MASTER);
            storages.put(storageName, masterDataModelStorage);
            if (!XSystemObjects.DC_UPDATE_PREPORT.getName().equalsIgnoreCase(storageName)) { //TODO would be better to decide whether a staging area should be created or not in a method.
                Storage stagingDataModelStorage = internalCreateStorage(dataModelName + STAGING_SUFFIX, storageName, dataSourceName, StorageType.STAGING);
                if (stagingDataModelStorage != null) {
                    storages.put(storageName + STAGING_SUFFIX, stagingDataModelStorage);
                }
            }
            return masterDataModelStorage;
        } catch (Exception e) {
            throw new RuntimeException("Could not create storage '" + storageName + "' with data model '" + dataModelName + "'.", e);
        }
    }

    // Returns null if storage can not be created (e.g. because of missing datasource configuration).
    private Storage internalCreateStorage(String dataModelName, String storageName, String dataSourceName, StorageType storageType) {
        ServerContext instance = ServerContext.INSTANCE;
        if (!instance.get().hasDataSource(dataSourceName, storageName, storageType)) {
            LOGGER.warn("Can not initialize " + storageType + " storage for '" + storageName + "': data source '" + dataSourceName + "' configuration is incomplete.") ;
            return null;
        }
        Storage dataModelStorage = instance.getLifecycle().createStorage(storageName, dataSourceName, storageType);
        DataSource dataSource = instance.get().getDataSource(dataSourceName, storageName, storageType);
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
            throw new RuntimeException("Could not create storage for container '" + storageName + "' using data model '" + dataModelName + "'.");
        }
        return dataModelStorage;
    }

    public boolean exist(String revision, String storageName) {
        return storages.containsKey(storageName);
    }

    public void close() {
        deleteAll(null);
    }

    public Storage get(String storageName) {
        return storages.get(storageName);
    }
}

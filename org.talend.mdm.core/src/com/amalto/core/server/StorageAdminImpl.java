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
import com.amalto.core.storage.datasource.DataSourceFactory;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

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
            Storage stagingDataModelStorage = internalCreateStorage(dataModelName, storageName, dataSourceName, StorageType.STAGING);
            storages.put(storageName, masterDataModelStorage);
            storages.put(storageName + STAGING_SUFFIX, stagingDataModelStorage);
            return masterDataModelStorage;
        } catch (Exception e) {
            throw new RuntimeException("Data cluster creation error for data model", e);
        }
    }

    private Storage internalCreateStorage(String dataModelName, String storageName, String dataSourceName, StorageType storageType) {
        Storage dataModelStorage = ServerContext.INSTANCE.getLifecycle().createStorage(storageName, dataSourceName, storageType);
        dataModelStorage.init(dataSourceName);

        MetadataRepositoryAdmin metadataRepositoryAdmin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
        boolean hasDataModel = metadataRepositoryAdmin.exist(dataModelName);
        if (!hasDataModel) {
            throw new UnsupportedOperationException("Data model '" + dataModelName + "' must exist before container '" + storageName + "' can be created.");
        }
        MetadataRepository metadataRepository = metadataRepositoryAdmin.get(dataModelName);
        Set<FieldMetadata> indexedFields = metadataRepositoryAdmin.getIndexedFields(dataModelName);
        dataModelStorage.prepare(metadataRepository, indexedFields, hasDataModel, autoClean);
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

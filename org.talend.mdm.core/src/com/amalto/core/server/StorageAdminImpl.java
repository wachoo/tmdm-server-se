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

import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.HibernateStorage;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StorageAdminImpl implements StorageAdmin {

    private final Map<String, Storage> storages = new HashMap<String, Storage>();

    private Logger LOGGER = Logger.getLogger(StorageAdminImpl.class);

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

    public Storage create(String revisionID, String dataModelName, String storageName, String dataSourceName) {
        // No support for other revision then "HEAD".
        if (revisionID != null && !"HEAD".equals(revisionID)) {
            throw new NotImplementedException("No support for revisions such as '" + revisionID + "'");
        }

        if (exist(null, storageName)) {
            LOGGER.warn("Storage for '" + storageName + "' already exist. It needs to be deleted before it can be recreated.");
            return get(storageName);
        }

        try {
            Storage masterDataModelStorage = internalCreateStorage(dataModelName, storageName, dataSourceName, HibernateStorage.StorageType.MASTER);
            Storage stagingDataModelStorage = internalCreateStorage(dataModelName, storageName, dataSourceName, HibernateStorage.StorageType.STAGING);

            storages.put(storageName, masterDataModelStorage);
            storages.put(storageName + STAGING_PREFIX, stagingDataModelStorage);

            return masterDataModelStorage;
        } catch (Exception e) {
            throw new RuntimeException("Data cluster creation error for data model", e);
        }
    }

    private Storage internalCreateStorage(String dataModelName, String storageName, String dataSourceName, HibernateStorage.StorageType storageType) {
        Storage dataModelStorage = ServerContext.INSTANCE.getLifecycle().createStorage(storageName, dataSourceName, storageType);
        dataModelStorage.init(dataSourceName);

        MetadataRepositoryAdmin metadataRepositoryAdmin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
        boolean hasDataModel = metadataRepositoryAdmin.exist(dataModelName);
        if (!hasDataModel) {
            throw new UnsupportedOperationException("Data model '" + dataModelName + "' must exist before container '" + storageName + "' can be created.");
        }
        MetadataRepository metadataRepository = metadataRepositoryAdmin.get(dataModelName);
        dataModelStorage.prepare(metadataRepository, hasDataModel, false);
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

/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.server.lifecycle.tomcat;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import com.amalto.core.server.*;
import com.amalto.core.storage.*;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import com.amalto.core.storage.dispatch.CompositeStorage;
import com.amalto.core.storage.hibernate.HibernateStorage;

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

public class TomcatServerLifecycle implements ServerLifecycle {

    private static final Logger LOGGER = Logger.getLogger(TomcatServerLifecycle.class);

    private static Storage defaultWrap(Storage storage) {
        storage = new CacheStorage(new SecuredStorage(storage, new SecuredStorage.UserDelegator() {

            public boolean hide(FieldMetadata field) {
                return false;
            }

            public boolean hide(ComplexTypeMetadata type) {
                return false;
            }
        }));
        storage = new StorageLogger(storage);
        return storage;
    }

    @Override
    public Server createServer() {
        LOGGER.info("Using MDM in Tomcat context.");
        return new TomcatServer();
    }

    @Override
    public void destroyServer(Server server) {
    }

    @Override
    public StorageAdmin createStorageAdmin() {
        return new StorageAdminImpl();
    }

    @Override
    public void destroyStorageAdmin(StorageAdmin storageAdmin) {
        storageAdmin.close();
    }

    @Override
    public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
        return new TomcatMetadataRepositoryAdmin();
    }

    @Override
    public void destroyMetadataRepositoryAdmin(MetadataRepositoryAdmin metadataRepositoryAdmin) {
        metadataRepositoryAdmin.close();
    }

    public Storage createStorage(String storageName, StorageType storageType, DataSourceDefinition definition) {
        List<Storage> storageForDispatch = new LinkedList<Storage>();
        // Adds a JDBC storage
        // TODO Move Hibernate storage to an extension?
        if (definition.get(storageType) instanceof RDBMSDataSource) {
            Storage storage = new HibernateStorage(storageName, storageType);
            storage.init(definition);
            storage = defaultWrap(storage);
            storageForDispatch.add(storage);
        }
        // Invoke extensions for storage extensions
        ServiceLoader<StorageExtension> extensions = ServiceLoader.load(StorageExtension.class);
        for (StorageExtension extension : extensions) {
            if (extension.accept(definition, storageType)) {
                Storage extensionStorage = extension.create(storageName, storageType);
                extensionStorage.init(definition);
                storageForDispatch.add(defaultWrap(extensionStorage));
            } else {
                LOGGER.debug("Extension '" + extension + "' is not eligible for datasource '" + definition + "'.");
            }
        }
        // Create actual storage
        int size = storageForDispatch.size();
        if (size > 1) {
            return new CompositeStorage(storageForDispatch.toArray(new Storage[size]));
        } else {
            return storageForDispatch.get(0); // Don't wrap in composite if there's no extension
        }
    }

    public void destroyStorage(Storage storage, boolean dropExistingData) {
        if (storage != null) {
            storage.close(dropExistingData);
        }
    }

}

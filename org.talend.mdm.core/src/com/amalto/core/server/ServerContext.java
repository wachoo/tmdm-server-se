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

import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageLogger;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.dispatch.CompositeStorage;
import com.amalto.core.storage.hibernate.HibernateStorage;

public class ServerContext {

    public static final ServerContext INSTANCE = new ServerContext();

    private Server server;

    private ServerLifecycle serverLifecycle;

    private ServerContext() {
    }

    public synchronized void close() {
        if (serverLifecycle != null) {
            serverLifecycle.destroyServer(server);
        }
        server = null;
    }

    public synchronized Server get() {
        // Uncomment line below to enable JMX administration.
        // return get(new JMXLifecycle(new DefaultServerLifecycle()));
        return get(new DefaultServerLifecycle());
    }

    public synchronized Server get(ServerLifecycle lifecycle) {
        if (server == null) {
            init(lifecycle);
        }
        return server;
    }

    public ServerLifecycle getLifecycle() {
        return serverLifecycle;
    }

    private void init(ServerLifecycle lifecycle) {
        // Wrap the lifecycle with a implementation that creates/removes JMX MBeans.
        serverLifecycle = lifecycle;
        server = serverLifecycle.createServer();
        server.init();
    }

    private static class DefaultServerLifecycle implements ServerLifecycle {

        protected static final Logger LOGGER = Logger.getLogger(DefaultServerLifecycle.class);

        private static Storage defaultWrap(Storage storage) {
            storage = new SecuredStorage(storage, new SecuredStorage.UserDelegator() {

                public boolean hide(FieldMetadata field) {
                    return false;
                }

                public boolean hide(ComplexTypeMetadata type) {
                    return false;
                }
            });
            storage = new StorageLogger(storage);
            return storage;
        }

        public Server createServer() {
            return new ServerImpl();
        }

        public void destroyServer(Server server) {
            if (server != null) {
                server.close();
            }
        }

        public StorageAdmin createStorageAdmin() {
            return new StorageAdminImpl();
        }

        public void destroyStorageAdmin(StorageAdmin storageAdmin) {
            if (storageAdmin != null) {
                storageAdmin.close();
            }
        }

        public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
            return new MetadataRepositoryAdminImpl();
        }

        public void destroyMetadataRepositoryAdmin(MetadataRepositoryAdmin metadataRepositoryAdmin) {
            if (metadataRepositoryAdmin != null) {
                metadataRepositoryAdmin.close();
            }
        }

        public Storage createStorage(String storageName, StorageType storageType, DataSourceDefinition definition) {
            List<Storage> storageForDispatch = new LinkedList<Storage>();
            // Adds a JDBC storage
            // TODO Move Hibernate storage to an extension?
            Storage storage = new HibernateStorage(storageName, storageType);
            storage.init(definition);
            storage = defaultWrap(storage);
            storageForDispatch.add(storage);
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
}

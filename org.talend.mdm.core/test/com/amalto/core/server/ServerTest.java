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

import java.util.Collections;

import com.amalto.core.storage.StorageType;
import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;

@SuppressWarnings("nls")
public class ServerTest extends TestCase {

    @Override
    public void tearDown() throws Exception {
        ServerContext.INSTANCE.close();
    }

    @Override
    public void setUp() throws Exception {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
    }

    public void testStorageInitialization() throws Exception {
        Server server = ServerContext.INSTANCE.get();
        assertNotNull(server);

        String metadataRepositoryId = "../query/metadata.xsd";
        MetadataRepository metadataRepository = server.getMetadataRepositoryAdmin().get(metadataRepositoryId);
        assertNotNull(metadataRepository);

        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        Storage storage = storageAdmin.create(metadataRepositoryId, "Storage", StorageType.MASTER, "H2-DS1", null);
        assertNotNull(storage);

        ComplexTypeMetadata person = metadataRepository.getComplexType("Person");
        assertNotNull(person);

        UserQueryBuilder qb = UserQueryBuilder.from(person);
        storage.begin();
        StorageResults fetch = storage.fetch(qb.getSelect());
        assertNotNull(fetch);

        try {
            assertTrue(fetch.getCount() >= 0); // Execute this just to check initialization was ok.
        } finally {
            fetch.close();
        }
        storage.commit();
    }

    public void testStorageRestart() throws Exception {
        Server server = ServerContext.INSTANCE.get();
        assertNotNull(server);

        String metadataRepositoryId = "../query/metadata.xsd";
        MetadataRepository metadataRepository = server.getMetadataRepositoryAdmin().get(metadataRepositoryId);
        assertNotNull(metadataRepository);

        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        Storage storage = storageAdmin.create(metadataRepositoryId, "Storage", StorageType.MASTER, "H2-DS1", null);
        assertNotNull(storage);

        storage.prepare(metadataRepository, Collections.<Expression> emptySet(), true, true);
    }

    // Temporary exclusion, see TMDM-5825
    /*
     * public void testStorageReindex() throws Exception { Server server = ServerContext.INSTANCE.get();
     * assertNotNull(server);
     * 
     * String metadataRepositoryId = "../query/metadata.xsd"; MetadataRepository metadataRepository =
     * server.getMetadataRepositoryAdmin().get(metadataRepositoryId); assertNotNull(metadataRepository);
     * 
     * StorageAdmin storageAdmin = server.getStorageAdmin(); assertNotNull(storageAdmin); Storage storage =
     * storageAdmin.create(metadataRepositoryId, "Storage", "H2-Default", null); assertNotNull(storage);
     * 
     * storage.reindex(); }
     */

    public void testCreateWithSlash() throws Exception {
        Server server = ServerContext.INSTANCE.get();
        assertNotNull(server);

        String storageName = "Storage";
        MetadataRepository metadataRepository = server.getMetadataRepositoryAdmin().get(storageName);
        assertNotNull(metadataRepository);

        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        Storage storage = null;
        Storage storage2 = null;
        try {
            storage = storageAdmin.create(storageName + "/MyTypeNameThatShouldBeSkipped", "Storage", StorageType.MASTER, "H2-DS1", null);
            assertNotNull(storage);
            assertEquals(storageName, storage.getName());

            storage2 = storageAdmin.create(storageName, "Storage", StorageType.MASTER, "H2-DS1", null);
            assertSame(storage2, storage);
        } finally {
            if (storage != null) {
                storage.close();
            }
            if (storage2 != null) {
                storage2.close(); // Also test if consecutive close() calls don't raise exception.
            }
        }
    }

    public void testFailedInit() throws Exception {
        ServerContext.INSTANCE.close(); // Setup set the wrong lifecycle
        ServerLifecycle serverLifecycle = new MockServerLifecycle() {

            @Override
            public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
                return new MockMetadataRepositoryAdmin() {

                    @Override
                    public boolean exist(String metadataRepositoryId) {
                        return false;
                    }

                    @Override
                    public MetadataRepository get(String metadataRepositoryId) {
                        return null;
                    }
                };
            }
        };
        Server server = ServerContext.INSTANCE.get(serverLifecycle);
        assertNotNull(server);

        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        try {
            storageAdmin.create("dataModelNameThatFailsFirstTime", "Storage", StorageType.MASTER, "H2-DS1", null);
            fail("Expected exception because data model does not exist.");
        } catch (Exception e) {
            // Expected
        }
    }

    public void testStorageDrop() throws Exception {
        Server server = ServerContext.INSTANCE.get();
        assertNotNull(server);
        // Create a storage
        String metadataRepositoryId = "../query/metadata.xsd";
        MetadataRepository metadataRepository = server.getMetadataRepositoryAdmin().get(metadataRepositoryId);
        assertNotNull(metadataRepository);
        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        Storage storage = storageAdmin.create(metadataRepositoryId, "Storage", StorageType.MASTER, "H2-DS1", null);
        assertNotNull(storage);
        ComplexTypeMetadata person = metadataRepository.getComplexType("Person");
        assertNotNull(person);
        UserQueryBuilder qb = UserQueryBuilder.from(person);
        storage.begin();
        StorageResults fetch = storage.fetch(qb.getSelect());
        assertNotNull(fetch);
        try {
            assertTrue(fetch.getCount() >= 0); // Execute this just to check initialization was ok.
        } finally {
            fetch.close();
            storage.commit();
        }
        // Destroy storage (and data).
        storageAdmin.delete("Storage", StorageType.MASTER, null, true);
        // Re create a storage.
        storage = storageAdmin.create(metadataRepositoryId, "Storage", StorageType.MASTER, "H2-DS1", null);
        assertNotNull(storage);
        qb = UserQueryBuilder.from(person);
        storage.begin();
        fetch = storage.fetch(qb.getSelect());
        assertNotNull(fetch);
        try {
            assertTrue(fetch.getCount() >= 0); // Execute this just to check initialization was ok.
        } finally {
            fetch.close();
            storage.commit();
        }
    }

}

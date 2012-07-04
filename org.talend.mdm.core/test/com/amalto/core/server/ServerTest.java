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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import junit.framework.TestCase;

import java.util.Collections;

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
        Storage storage = storageAdmin.create(null, metadataRepositoryId, "Storage", "H2-DS1");

        ComplexTypeMetadata person = metadataRepository.getComplexType("Person");
        assertNotNull(person);

        UserQueryBuilder qb = UserQueryBuilder.from(person);
        StorageResults fetch = storage.fetch(qb.getSelect());
        assertNotNull(fetch);

        try {
            assertTrue(fetch.getCount() >= 0); // Execute this just to check initialization was ok.
        } finally {
            fetch.close();
        }
    }

    public void testStorageRestart() throws Exception {
        Server server = ServerContext.INSTANCE.get();
        assertNotNull(server);

        String metadataRepositoryId = "../query/metadata.xsd";
        MetadataRepository metadataRepository = server.getMetadataRepositoryAdmin().get(metadataRepositoryId);
        assertNotNull(metadataRepository);

        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        Storage storage = storageAdmin.create(null, metadataRepositoryId, "Storage", "H2-DS1");

        storage.prepare(metadataRepository, Collections.<FieldMetadata>emptySet(), true, true);
    }

    public void testStorageReindex() throws Exception {
        Server server = ServerContext.INSTANCE.get();
        assertNotNull(server);

        String metadataRepositoryId = "../query/metadata.xsd";
        MetadataRepository metadataRepository = server.getMetadataRepositoryAdmin().get(metadataRepositoryId);
        assertNotNull(metadataRepository);

        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        Storage storage = storageAdmin.create(null, metadataRepositoryId, "Storage", "H2-DS1");

        storage.reindex();
    }

    public void testFailedInit() throws Exception {
        ServerLifecycle serverLifecycle = new MockServerLifecycle() {
            boolean isFirstCall = true;
            
            @Override
            public MetadataRepositoryAdmin createMetadataRepositoryAdmin() {
                return new MockMetadataRepositoryAdmin() {
                    @Override
                    public MetadataRepository get(String metadataRepositoryId) {
                        if (isFirstCall) {
                            isFirstCall = false;
                            return super.get(metadataRepositoryId);
                        } else {
                            return super.get("../query/metadata.xsd");
                        }
                    }
                };
            }
        };
        Server server = ServerContext.INSTANCE.get(serverLifecycle);
        assertNotNull(server);

        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        try {
            storageAdmin.create(null, "dataModelNameThatFailsFirstTime", "Storage", "H2-DS1");
            fail("Expected exception because data model does not exist.");
        } catch (Exception e) {
            // Expected
        }

        Storage storage = null;
        try {
            storage = storageAdmin.create(null, "dataModelNameThatFailsFirstTime", "Storage", "H2-DS1");
            fail();
        } catch (Exception e) {
            if (storage != null) {
                storage.close();
            }
            // Expected
        }

    }
}

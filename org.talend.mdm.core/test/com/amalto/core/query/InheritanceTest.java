/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.HibernateStorage;
import junit.framework.TestCase;

import java.util.List;

public class InheritanceTest extends TestCase {

    public void testTypeOrdering() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(InheritanceTest.class.getResourceAsStream("inheritance.xsd"));

        List<ComplexTypeMetadata> sortedList = MetadataUtils.sortTypes(repository);
        String[] expectedOrder = {"B", "A", "C"};
        int i = 0;
        for (ComplexTypeMetadata sortedType : sortedList) {
            assertEquals(expectedOrder[i++], sortedType.getName());
        }
    }

    // Disable because it has side effect to the other tests.
    public void __testStorageInit() throws Exception {
        System.out.println("Setting up MDM server environment...");
        MockServerLifecycle lifecycle = new MockServerLifecycle();
        ServerContext.INSTANCE.get(lifecycle);
        System.out.println("MDM server environment set.");

        MetadataRepository repository = new MetadataRepository();
        repository.load(InheritanceTest.class.getResourceAsStream("inheritance.xsd"));

        Storage s = new HibernateStorage("inheritance", HibernateStorage.StorageType.MASTER);
        try {
            s.init(StorageTestCase.DATABASE + "-Default");
            s.prepare(repository, true);
        } finally {
            s.close();
            lifecycle.destroyServer(ServerContext.INSTANCE.get());
        }
    }
}

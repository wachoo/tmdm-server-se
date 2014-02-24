/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.adapt;

import com.amalto.core.metadata.compare.CompareTest;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.hibernate.HibernateStorage;
import junit.framework.TestCase;
import org.talend.mdm.commmon.metadata.MetadataRepository;

public class StorageAdaptTest extends TestCase {

    @Override
    public void setUp() throws Exception {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
    }

    public void test1() throws Exception {
        // Test preparation
        Storage storage = new HibernateStorage("Test", StorageType.MASTER);
        storage.init(ServerContext.INSTANCE.get().getDefinition("H2-Default", "Test"));
        MetadataRepository repository = new MetadataRepository();
        repository.load(CompareTest.class.getResourceAsStream("schema1_1.xsd"));
        storage.prepare(repository, true);
        // Actual test
        MetadataRepository newRepository = new MetadataRepository();
        newRepository.load(CompareTest.class.getResourceAsStream("schema1_2.xsd"));
        storage.adapt(newRepository, true);
        storage.close(true);
    }
}

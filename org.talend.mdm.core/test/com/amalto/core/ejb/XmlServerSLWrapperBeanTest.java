// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.ejb;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.MetadataRepository;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;

@SuppressWarnings("nls")
public class XmlServerSLWrapperBeanTest extends TestCase {
    
    private XmlServerSLWrapperBean xmlServerSLWrapperBean;

    @Override
    public void tearDown() throws Exception {
        ServerContext.INSTANCE.close();
    }

    @Override
    public void setUp() throws Exception {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        xmlServerSLWrapperBean = new XmlServerSLWrapperBean();
    }

    public void testSupportStaging() {

        Server server = ServerContext.INSTANCE.get();
        assertNotNull(server);

        String metadataRepositoryId = "../query/metadata.xsd";
        MetadataRepository metadataRepository = server.getMetadataRepositoryAdmin().get(metadataRepositoryId);
        assertNotNull(metadataRepository);

        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);

        Storage storage = storageAdmin.create(metadataRepositoryId, "Storage", "H2-DS2", null);
        assertNotNull(storage);

        Storage newStorage = storageAdmin.create(metadataRepositoryId, "NewStorage", "H2-DS1", null);
        assertNotNull(newStorage);

        boolean isSupport = xmlServerSLWrapperBean.supportStaging(null);
        assertFalse(isSupport);

        isSupport = xmlServerSLWrapperBean.supportStaging("NewStorage");
        assertFalse(isSupport);

        isSupport = xmlServerSLWrapperBean.supportStaging("StorageThatDoesNotExist");
        assertFalse(isSupport);

        isSupport = xmlServerSLWrapperBean.supportStaging("Storage");
        assertTrue(isSupport);

    }

}

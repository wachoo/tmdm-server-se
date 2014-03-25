// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.storage;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.query.StorageTestCase;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.hibernate.HibernateStorage;

public class StorageWrapperTest extends TestCase {

    public StorageWrapperTest() {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
    }

    public void testGetDocumentAsString() throws Exception {
        final MetadataRepository repository = prepareMetadata("Product.xsd"); //$NON-NLS-1$
        final Storage storage = prepareStorage(repository);

        StorageWrapper wrapper = new StorageWrapper() {

            @Override
            protected Storage getStorage(String dataClusterName) {
                return storage;
            }

            @Override
            protected Storage getStorage(String dataClusterName, String revisionId) {
                return storage;
            }
        };
        String xml = "<ii><c>Product</c><n>Product</n><dmn>Product</dmn><i>333</i><t>1372654669313</t><taskId></taskId><p> <Product><Id>333</Id><Name>333</Name><Description>333</Description><Price>333</Price></Product></p></ii>"; //$NON-NLS-1$
        wrapper.start("Product");
        {
            wrapper.putDocumentFromString(xml, "Product.Product.333", "Product", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        wrapper.commit("Product");
        String item = wrapper.getDocumentAsString(null, "Product", "Product.Product.333"); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(item);
        assertTrue(item.contains("<i>333</i>")); //$NON-NLS-1$

        xml = "<ii><c>Product</c><n>Product</n><dmn>Product</dmn><i>33&amp;44</i><t>1372654669313</t><taskId></taskId><p> <Product><Id>33&amp;44</Id><Name>333</Name><Description>333</Description><Price>333</Price></Product></p></ii>"; //$NON-NLS-1$
        wrapper.start("Product");
        {
            wrapper.putDocumentFromString(xml, "Product.Product.33&44", "Product", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        wrapper.commit("Product");
        item = wrapper.getDocumentAsString(null, "Product", "Product.Product.33&44"); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(item);
        assertTrue(item.contains("<i>33&amp;44</i>")); //$NON-NLS-1$

        xml = "<ii><c>Product</c><n>Product</n><dmn>Product</dmn><i>&quot;555&lt;666&gt;444&quot;</i><t>1372654669313</t><taskId></taskId><p> <Product><Id>&quot;555&lt;666&gt;444&quot;</Id><Name>333</Name><Description>333</Description><Price>333</Price></Product></p></ii>"; //$NON-NLS-1$
        wrapper.start("Product");
        {
            wrapper.putDocumentFromString(xml, "Product.Product.\"555<666>444\"", "Product", null); //$NON-NLS-1$ //$NON-NLS-2$
        }
        wrapper.commit("Product");
        item = wrapper.getDocumentAsString(null, "Product", "Product.Product.\"555<666>444\""); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(item);
        assertTrue(item.contains("<i>&quot;555&lt;666&gt;444&quot;</i>")); //$NON-NLS-1$
    }

    private MetadataRepository prepareMetadata(String dataModelFile) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(StorageWrapperTest.class.getResourceAsStream(dataModelFile));
        return repository;
    }

    private Storage prepareStorage(MetadataRepository repository) {
        Storage storage = new HibernateStorage("Product"); //$NON-NLS-1$
        storage.init(ServerContext.INSTANCE.get().getDefinition(StorageTestCase.DATABASE + "-Default", "MDM")); //$NON-NLS-1$//$NON-NLS-2$
        storage.prepare(repository, true);
        return storage;
    }
}

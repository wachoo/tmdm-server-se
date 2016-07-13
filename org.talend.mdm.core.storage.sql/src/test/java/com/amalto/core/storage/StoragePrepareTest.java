// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import static com.amalto.core.query.user.UserQueryBuilder.from;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.query.StorageTestCase;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.MockMetadataRepositoryAdmin;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.MockStorageAdmin;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.XmlStringDataRecordReader;

public class StoragePrepareTest extends TestCase {

    protected static MockUserDelegator userSecurity = new MockUserDelegator();
    protected static final String STORAGE_NAME = "Test";

    public StoragePrepareTest() {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
    }
    
    private MetadataRepository prepareMetadata(String xsd) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(StoragePrepareTest.class.getResourceAsStream(xsd));
        return repository;
    }

    private Storage prepareStorage(String name, MetadataRepository repository) {
        Storage storage = new HibernateStorage(name);
        storage.init(ServerContext.INSTANCE.get().getDefinition(StorageTestCase.DATABASE + "-Default", "MDM")); //$NON-NLS-1$//$NON-NLS-2$
        storage.prepare(repository, true);
        return storage;
    }
    
    public void test1_CompositeIdAndContainedType() {
        String[] userKeys = { "NumeroBdd", "BddSource", "NomApplication", "IdMDM" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String[] dbKeys = { "x_numerobdd", "x_bddsource", "x_nomapplication", "x_idmdm" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        MetadataRepository repository = prepareMetadata("StoragePrepare_1.xsd"); //$NON-NLS-1$
        ComplexTypeMetadata userType = repository.getComplexType("XrefAgence"); //$NON-NLS-1$
        // assert user type
        assertNotNull(userType);
        assertEquals(4, userType.getKeyFields().size());
        int i = 0;
        for (FieldMetadata keyField : userType.getKeyFields()) {
            assertEquals(userKeys[i++], keyField.getName());
        }
        // assert database type
        HibernateStorage storage = (HibernateStorage) prepareStorage("Test1", repository); //$NON-NLS-1$
        ComplexTypeMetadata dbType = storage.getTypeEnhancer().getMappings().getMappingFromUser(userType).getDatabase();
        assertNotNull(dbType);
        assertEquals(4, dbType.getKeyFields().size());
        int j = 0;
        for (FieldMetadata keyField : dbType.getKeyFields()) {
            assertEquals(dbKeys[j++], keyField.getName());
        }
    }

    public void testStorageAfterFetchClassLoader() {
        Storage storage = new SecuredStorage(new HibernateStorage("Goods", StorageType.MASTER), userSecurity);//$NON-NLS-1$
        MetadataRepository repository = new MetadataRepository();
        repository.load(StoragePrepareTest.class.getResourceAsStream("GoodsDecimal.xml"));//$NON-NLS-1$
        MockMetadataRepositoryAdmin.INSTANCE.register("Goods", repository);//$NON-NLS-1$

        storage.init(getDatasource("H2-DS3"));//    //$NON-NLS-1$
        storage.prepare(repository, Collections.<Expression> emptySet(), true, true);
        ((MockStorageAdmin) ServerContext.INSTANCE.get().getStorageAdmin()).register(storage);

        storage.begin();
        ComplexTypeMetadata goods = repository.getComplexType("Goods");//$NON-NLS-1$
        UserQueryBuilder qb = from(goods);
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
            for (DataRecord result : results) {
                break;
            }
        } finally {
            results.close();
        }
        storage.end();
        ClassLoader StorageClassLoader = (ClassLoader) Thread.currentThread().getContextClassLoader();

        List<DataRecord> records = new ArrayList<DataRecord>();
        DataRecordReader<String> factory = new XmlStringDataRecordReader();
        records.add(factory.read(repository, goods, "<Goods><Id>1</Id><Price>12.00</Price></Goods>")); //$NON-NLS-1$
        try {
            storage.begin();
            storage.update(records);
            storage.commit();
        } finally {
            storage.end();
        }

        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
            for (DataRecord result : results) {
                break;
            }

        } finally {
            results.close();
        }
        storage.end();
        ClassLoader StorageClassLoader2 = (ClassLoader) Thread.currentThread().getContextClassLoader();
        assertEquals(StorageClassLoader, StorageClassLoader2);
    }

    protected static DataSourceDefinition getDatasource(String dataSourceName) {
        return ServerContext.INSTANCE.get().getDefinition(dataSourceName, "MDM");
    }
    protected static class MockUserDelegator implements SecuredStorage.UserDelegator {

        boolean isActive = true;

        public void setActive(boolean active) {
            isActive = active;
        }

        @Override
        public boolean hide(FieldMetadata field) {
            return isActive && field.getHideUsers().contains("System_Users");
        }

        @Override
        public boolean hide(ComplexTypeMetadata type) {
            return isActive && type.getHideUsers().contains("System_Users");
        }
    }
}

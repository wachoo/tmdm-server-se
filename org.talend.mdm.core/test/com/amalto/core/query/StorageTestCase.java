/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.query;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.hibernate.HibernateStorage;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.util.Collections;

@SuppressWarnings("nls")
public class StorageTestCase extends TestCase {

    private static Logger LOG = Logger.getLogger(StorageTestCase.class);

    protected static final Storage storage;

    protected static final MetadataRepository repository;

    protected static final ComplexTypeMetadata product;

    protected static final ComplexTypeMetadata productFamily;

    protected static final ComplexTypeMetadata supplier;

    protected static final ComplexTypeMetadata type;

    protected static final ComplexTypeMetadata person;

    protected static final ComplexTypeMetadata address;

    protected static final ComplexTypeMetadata country;

    protected static final ComplexTypeMetadata a;

    protected static final ComplexTypeMetadata b;

    protected static final ComplexTypeMetadata c;

    protected static final ComplexTypeMetadata d;

    protected static final ComplexTypeMetadata updateReport;

    protected static TestUserDelegator userSecurity = new TestUserDelegator();

    public static final String DATABASE = "H2";

    static {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing storage for tests...");
        storage = new SecuredStorage(new HibernateStorage("MDM"), userSecurity);
        repository = new MetadataRepository();
        repository.load(StorageQueryTest.class.getResourceAsStream("metadata.xsd"));

        type = repository.getComplexType("TypeA");
        person = repository.getComplexType("Person");
        address = repository.getComplexType("Address");
        country = repository.getComplexType("Country");
        product = repository.getComplexType("Product");
        productFamily = repository.getComplexType("ProductFamily");
        supplier = repository.getComplexType("Supplier");
        a = repository.getComplexType("A");
        b = repository.getComplexType("B");
        c = repository.getComplexType("C");
        d = repository.getComplexType("D");
        updateReport = repository.getComplexType("Update");

        storage.init(getDatasource(DATABASE + "-Default"));
        storage.prepare(repository, Collections.singleton(person.getField("firstname")), true, true);
        LOG.info("Storage prepared.");
    }

    protected static DataSource getDatasource(String dataSourceName) {
        return ServerContext.INSTANCE.get().getDataSource(dataSourceName, "MDM", StorageType.MASTER);
    }

    public void test() throws Exception {
        // Just there so JUnit does not complain about a test case that has no test.
    }

    protected static class TestUserDelegator implements SecuredStorage.UserDelegator {

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

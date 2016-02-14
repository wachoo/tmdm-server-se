// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.save;

import static com.amalto.core.query.user.UserQueryBuilder.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.utils.json.JSONException;
import org.talend.utils.json.JSONObject;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.metadata.ClassRepository;
import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.datamodel.DataModelPOJO;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.OrderBy;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.SaverSession.Committer;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.save.context.StorageSaverSource;
import com.amalto.core.server.MockMetadataRepositoryAdmin;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.MockStorageAdmin;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import com.amalto.core.util.XtentisException;

public class RecordValidationTest extends TestCase {

    private static Logger LOG = Logger.getLogger(RecordValidationTest.class);

    protected static final Storage systemStorage;

    protected static final Storage masterStorage;

    protected static final Storage stagingStorage;

    protected static final MetadataRepository systemRepository;

    protected static final MetadataRepository productRepository;

    protected static MockUserDelegator userSecurity = new MockUserDelegator();

    protected static final ComplexTypeMetadata product;

    protected static final ComplexTypeMetadata store;

    protected static final ComplexTypeMetadata productFamily;

    public static final String DATASOURCE = "H2-Default";

    static {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing system storage");
        systemStorage = new SecuredStorage(new HibernateStorage("__SYSTEM", StorageType.SYSTEM), userSecurity);
        systemRepository = buildSystemRepository();
        MockMetadataRepositoryAdmin.INSTANCE.register("__SYSTEM", systemRepository);
        systemStorage.init(getDatasource(DATASOURCE));
        systemStorage.prepare(systemRepository, Collections.<Expression> emptySet(), true, true);
        ((MockStorageAdmin) ServerContext.INSTANCE.get().getStorageAdmin()).register(systemStorage);
        LOG.info("System storage prepared");

        LOG.info("Preparing master storage");
        masterStorage = new SecuredStorage(new HibernateStorage("Product", StorageType.MASTER), userSecurity);
        productRepository = new MetadataRepository();
        productRepository.load(RecordValidationTest.class.getResourceAsStream("../storage/Product.xsd"));
        MockMetadataRepositoryAdmin.INSTANCE.register("Product", productRepository);
        product = productRepository.getComplexType("Product");
        store = productRepository.getComplexType("Store");
        productFamily = productRepository.getComplexType("ProductFamily");
        masterStorage.init(getDatasource(DATASOURCE));
        masterStorage.prepare(productRepository, Collections.<Expression> emptySet(), true, true);
        ((MockStorageAdmin) ServerContext.INSTANCE.get().getStorageAdmin()).register(masterStorage);
        LOG.info("Master storage prepared");

        LOG.info("Preparing staging storage");
        stagingStorage = new SecuredStorage(new HibernateStorage("Product", StorageType.STAGING), userSecurity);
        stagingStorage.init(getDatasource(DATASOURCE));
        stagingStorage.prepare(productRepository, Collections.<Expression> emptySet(), true, true);
        ((MockStorageAdmin) ServerContext.INSTANCE.get().getStorageAdmin()).register(stagingStorage);
        LOG.info("Staging storage prepared");

        BeanDelegatorContainer.createInstance();
    }

    // Create test data of Product & Store
    private void createData() {
        DataRecordReader<String> factory = new XmlStringDataRecordReader();
        List<DataRecord> allRecords = new LinkedList<DataRecord>();
        String xmlStore = "<Store><Id>1</Id><Address>address</Address><Lat>1.0</Lat><Long>1.0</Long></Store>";
        String xmlProduct = "<Product><Id>1</Id><Name>Product</Name><Description>Product Description</Description><Features><Sizes/><Colors/></Features><Price>2.00</Price><Stores><Store>[1]</Store></Stores></Product>";
        allRecords.add(factory.read(productRepository, store, xmlStore));
        allRecords.add(factory.read(productRepository, product, xmlProduct));
        try {
            masterStorage.begin();
            masterStorage.update(allRecords);
            masterStorage.commit();
        } finally {
            masterStorage.end();
        }
        try {
            stagingStorage.begin();
            stagingStorage.update(allRecords);
            stagingStorage.commit();
        } finally {
            stagingStorage.end();
        }
    }

    protected static DataSourceDefinition getDatasource(String dataSourceName) {
        return ServerContext.INSTANCE.get().getDefinition(dataSourceName, "MDM");
    }

    @SuppressWarnings("rawtypes")
    private static ClassRepository buildSystemRepository() {
        ClassRepository repository = new ClassRepository();
        Class[] objectsToParse = new Class[ObjectPOJO.OBJECT_TYPES.length];
        int i = 0;
        for (Object[] objects : ObjectPOJO.OBJECT_TYPES) {
            objectsToParse[i++] = (Class) objects[1];
        }
        repository.load(objectsToParse);
        String[] models = new String[] {"/com/amalto/core/initdb/data/datamodel/CONF"};
        for (String model : models) {
            InputStream builtInStream = RecordValidationTest.class.getResourceAsStream(model);
            if (builtInStream == null) {
                throw new RuntimeException("Built in model '" + model + "' cannot be found.");
            }
            try {
                DataModelPOJO modelPOJO = ObjectPOJO.unmarshal(DataModelPOJO.class, IOUtils.toString(builtInStream, "UTF-8"));
                repository.load(new ByteArrayInputStream(modelPOJO.getSchema().getBytes("UTF-8")));
            } catch (Exception e) {
                throw new RuntimeException("Could not parse builtin data model '" + model + "'.", e);
            } finally {
                try {
                    builtInStream.close();
                } catch (IOException e) {
                    // Ignored
                }
            }
        }
        return repository;
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

    protected static class MockAdmin extends ILocalUser {

        @Override
        public ILocalUser getILocalUser() throws XtentisException {
            return this;
        }

        @Override
        public HashSet<String> getRoles() {
            HashSet<String> roleSet = new HashSet<String>();
            roleSet.add("System_Admin");
            roleSet.add("administration");
            return roleSet;
        }

        @Override
        public String getUsername() {
            return "administrator";
        }

        @Override
        public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException {
            return true;
        }
    }

    protected static class MockUser extends ILocalUser {

        @Override
        public ILocalUser getILocalUser() throws XtentisException {
            return this;
        }

        @Override
        public HashSet<String> getRoles() {
            HashSet<String> roleSet = new HashSet<String>();
            roleSet.add("System_Interactive");
            return roleSet;
        }

        @Override
        public String getUsername() {
            return "user";
        }

        @Override
        public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException {
            return false;
        }
    }

    protected static class MockSaverSource extends StorageSaverSource {

        private final MetadataRepository repository;

        private final boolean isAdmin;

        public MockSaverSource(MetadataRepository repository, boolean isAdmin) {
            this.repository = repository;
            this.isAdmin = isAdmin;
        }

        @Override
        public String getUserName() {
            if(isAdmin) {
                return "administrator";
            } else {
                return "user";
            }
        }

        @Override
        public Set<String> getCurrentUserRoles() {
            if(isAdmin) {
                HashSet<String> roleSet = new HashSet<String>();
                roleSet.add("System_Admin");
                roleSet.add("administration");
                return roleSet;
            } else {
                HashSet<String> roleSet = new HashSet<String>();
                roleSet.add("System_Interactive");
                return roleSet;
            }
        }

        @Override
        public InputStream getSchema(String dataModelName) {
            return RecordValidationTest.class.getResourceAsStream("../storage/Product.xsd");
        }

        @Override
        public synchronized MetadataRepository getMetadataRepository(String dataModelName) {
            return repository;
        }

        @Override
        public void routeItem(String dataCluster, String typeName, String[] id) {
            // nothing to do
        }

    }

    // Clean test data of Product & Store
    private void cleanData() {
        try {
            masterStorage.begin();
            {
                UserQueryBuilder qb = from(product);
                masterStorage.delete(qb.getSelect());

                qb = from(store);
                masterStorage.delete(qb.getSelect());
            }
            masterStorage.commit();
        } finally {
            masterStorage.end();
        }
        try {
            stagingStorage.begin();
            {
                UserQueryBuilder qb = from(product);
                stagingStorage.delete(qb.getSelect());

                qb = from(store);
                stagingStorage.delete(qb.getSelect());
            }
            stagingStorage.commit();
        } finally {
            stagingStorage.end();
        }
    }

    // Simulate the Record Validation API of DataService#validateRecord() to test RecordValidationContext, RecordValidationCommitter, etc.
    private static JSONObject validateRecord(String storageName, boolean isStaging, boolean isAdmin, String documentXml) throws Exception {
        ILocalUser localUser = isAdmin ? new MockAdmin() : new MockUser();
        String dataCluster = isStaging ? storageName + "#STAGING" : storageName;
        DataRecord.ValidateRecord.set(true);
        boolean isValid = true;
        String message = StringUtils.EMPTY;

        SaverSession session = SaverSession.newSession(new MockSaverSource(productRepository, isAdmin));
        Committer committer = new RecordValidationCommitter();
        DocumentSaverContext context = session.getContextFactory().createValidation(dataCluster, storageName, new ByteArrayInputStream(documentXml.getBytes("UTF-8")));
        DocumentSaver saver = context.createSaver();
        try {
            session.begin(dataCluster, committer);
            saver.save(session, context);
            BeanDelegatorContainer.getInstance().setDelegatorInstancePool(Collections.<String, Object> singletonMap("LocalUser", localUser));
            session.end(committer);
        } catch (Exception e) {
            isValid = false;
            message = getRootException(e).getMessage();
        } finally {
            session.abort(committer);
            DataRecord.ValidateRecord.remove();
        }

        JSONObject result = new JSONObject();
        try {
            result.put("isValid", isValid); //$NON-NLS-1$
            result.put("message", message); //$NON-NLS-1$
        } catch (JSONException e) {
            throw new RuntimeException("Unable to build the record validation result.", e); //$NON-NLS-1$
        }
        return result;
    }

    // Create test ProductFamily with AutoIncrement ID
    private static void createFamily(String storageName, boolean isStaging,  String documentXml) throws Exception {
        String dataCluster = isStaging ? storageName + "#STAGING" : storageName;
        SaverSession session = SaverSession.newSession(new MockSaverSource(productRepository, true));
        DocumentSaverContext context = session.getContextFactory().createValidation(dataCluster, storageName, new ByteArrayInputStream(documentXml.getBytes("UTF-8")));
        DocumentSaver saver = context.createSaver();
        try {
            session.begin(dataCluster);
            saver.save(session, context);
            BeanDelegatorContainer.getInstance().setDelegatorInstancePool(Collections.<String, Object> singletonMap("LocalUser", new MockAdmin()));
            session.end();
        } catch (Exception e) {
            session.abort();
            throw e;
        }
    }

    protected static Throwable getRootException(Throwable e) {
        Throwable root = e;
        while(root != null && root.getCause() != null && root.getCause().getMessage() != null) {
            root = root.getCause();
        }
        return root;
    }

    // MASTER, get Schema error, STAGING won't validate Schema, no error
    public void testSchemaValidation() throws Exception {
        String xmlForSchema1 = "<ProductFamily><Name>Test Product Family</Name><ChangeStatus>NotValid</ChangeStatus></ProductFamily>";
        String xmlForSchema2 = "<ProductFamily><Name>Test Product Family</Name><ChangeStatus>Approved</ChangeStatus></ProductFamily>";
        // MASTER
        JSONObject resp = validateRecord("Product", false, true, xmlForSchema1);
        assertFalse(resp.getBoolean("isValid"));// FAIL
        assertTrue(resp.getString("message").contains("[Error] :-1:-1: cvc-type.3.1.3: The value 'NotValid' of element 'ChangeStatus' is not valid."));
        resp = validateRecord("Product", false, true, xmlForSchema2);
        assertTrue(resp.getBoolean("isValid"));// FAIL
        // STAGING
        resp = validateRecord("Product", true, true, xmlForSchema1);
        assertTrue(resp.getBoolean("isValid"));// PASS, doesn't do schema validation
        resp = validateRecord("Product", true, true, xmlForSchema2);
        assertTrue(resp.getBoolean("isValid"));// PASS
    }

    // MASTER, CREATE will get Schema error, UPDATE will get convert error, STAGING will both get convert error
    public void testValueTypeValidation() throws Exception {
        createData();

        String xmlForValue1 = "<Product><Id>1</Id><Name>Test Product 1</Name><Description>Test Product Description</Description><Features><Sizes/><Colors/></Features><Price>a2.00</Price></Product>";
        String xmlForValue2 = "<Product><Id>2</Id><Name>Test Product 2</Name><Description>Test Product Description</Description><Features><Sizes/><Colors/></Features><Price>a2.00</Price></Product>";
        String xmlForValue3 = "<Product><Id>3</Id><Name>Test Product 1</Name><Description>Test Product Description</Description><Features><Sizes/><Colors/></Features><Price>3.00</Price></Product>";
        //MASTER
        JSONObject resp = validateRecord("Product", false, true, xmlForValue1);
        assertFalse(resp.getBoolean("isValid"));// FAIL
        assertTrue(resp.getString("message").equals("'a2.00' is not a number."));// UPDATE will return value error
        resp = validateRecord("Product", false, true, xmlForValue2);
        assertFalse(resp.getBoolean("isValid"));// FAIL
        assertTrue(resp.getString("message").contains("[Error] :-1:-1: cvc-datatype-valid.1.2.1: 'a2.00' is not a valid value for 'decimal'."));// CREATE will return schema error
        resp = validateRecord("Product", false, true, xmlForValue3);
        assertTrue(resp.getBoolean("isValid"));// PASS
        //STAGING
        resp = validateRecord("Product", true, true, xmlForValue1);
        assertFalse(resp.getBoolean("isValid"));// FAIL
        assertTrue(resp.getString("message").equals("'a2.00' is not a number."));// UPDATE will return value error
        resp = validateRecord("Product", true, true, xmlForValue2);
        assertFalse(resp.getBoolean("isValid"));// FAIL
        assertTrue(resp.getString("message").equals("'a2.00' is not a number."));// CREATE will return value error
        resp = validateRecord("Product", true, true, xmlForValue3);
        assertTrue(resp.getBoolean("isValid"));// PASS

        cleanData();
    }

    // MASTER & STAGING will both validate the existence of FK
    public void testForeignKeyValidation() throws Exception {
        createData();

        String xmlForFK1 = "<Product><Id>1</Id><Name>Product</Name><Description>Product Description</Description><Features><Sizes/><Colors/></Features><Price>2.00</Price><Stores><Store>[1]</Store></Stores></Product>";
        String xmlForFK2 = "<Product><Id>2</Id><Name>Product</Name><Description>Product Description</Description><Features><Sizes/><Colors/></Features><Price>2.00</Price><Stores><Store>[2]</Store></Stores></Product>";
        // MASTER
        JSONObject resp = validateRecord("Product", false, true, xmlForFK1); // exists FK
        assertTrue(resp.getBoolean("isValid"));// PASS
        resp = validateRecord("Product", false, true, xmlForFK2); // not exist
        assertFalse(resp.getBoolean("isValid"));// FAIL
        assertTrue(resp.getString("message").equals("Invalid foreign key: [org.talend.mdm.storage.hibernate.Store#2] doesn't exist."));
        // STAGING
        resp = validateRecord("Product", true, true, xmlForFK1); // exists FK
        assertTrue(resp.getBoolean("isValid"));// PASS
        resp = validateRecord("Product", true, true, xmlForFK2); // not exist
        assertFalse(resp.getBoolean("isValid"));// FAIL
        assertTrue(resp.getString("message").equals("Invalid foreign key: [org.talend.mdm.storage.hibernate.Store#2] doesn't exist."));

        cleanData();
    }

    // MASTER & STAGING will both validate the security
    public void testSecurityValidation() throws Exception {
        String xmlForSecurity= "<ProductFamily><Name>Test Product Family</Name><ChangeStatus>Approved</ChangeStatus></ProductFamily>";
        // MASTER
        JSONObject resp = validateRecord("Product", false, false, xmlForSecurity);// 'user' can't write ProductFamily
        assertFalse(resp.getBoolean("isValid"));//  FAIL
        assertTrue(resp.getString("message").equals("User 'user' is not allowed to write to type 'ProductFamily'."));
        // STAGING
        resp = validateRecord("Product", true, false, xmlForSecurity); // 'user' can't write ProductFamily
        assertFalse(resp.getBoolean("isValid"));// FAIL
        assertTrue(resp.getString("message").equals("User 'user' is not allowed to write to type 'ProductFamily'."));
    }

    // Validate record contains AutoIncrement won't affect the value stored in system
    public void testNoAutoIncrementImpactValidation() throws Exception {
        String xmlFamily1 = "<ProductFamily><Name>test_product_family_1</Name><ChangeStatus>Approved</ChangeStatus></ProductFamily>";
        String xmlFamily2 = "<ProductFamily><Name>test_product_family_2</Name><ChangeStatus>Approved</ChangeStatus></ProductFamily>";
        String xmlForAutoIncrement= "<ProductFamily><Name>Test Product Family</Name><ChangeStatus>Approved</ChangeStatus></ProductFamily>";
        FieldMetadata id = productFamily.getField("Id");
        FieldMetadata name = productFamily.getField("Name");
        UserQueryBuilder qb = from(productFamily).orderBy(id, OrderBy.Direction.DESC);
        // MASTER
        createFamily("Product", false, xmlFamily1);
        validateRecord("Product", false, false, xmlForAutoIncrement);
        createFamily("Product", false, xmlFamily2);
        StorageResults results = masterStorage.fetch(qb.getSelect());
        try {
            assertEquals(2, results.getSize());
            int id1 = 0;
            int id2 = 0;
            for (DataRecord result : results) {
                if("test_product_family_2".equals(result.get(name))) {
                    id2 = Integer.parseInt((String)result.get(id));
                } else {
                    id1 = Integer.parseInt((String)result.get(id));
                }
            }
            assertTrue((id2 - id1) == 1);
            masterStorage.delete(qb.getSelect());
        } finally {
            results.close();
        }
        // STAGING
        createFamily("Product", true, xmlFamily1);
        validateRecord("Product", true, false, xmlForAutoIncrement);
        createFamily("Product", true, xmlFamily2);
        results = stagingStorage.fetch(qb.getSelect());
        try {
            assertEquals(2, results.getSize());
            int id1 = 0;
            int id2 = 0;
            for (DataRecord result : results) {
                if("test_product_family_2".equals(result.get(name))) {
                    id2 = Integer.parseInt((String)result.get(id));
                } else {
                    id1 = Integer.parseInt((String)result.get(id));
                }
            }
            assertTrue((id2 - id1) == 1);
            stagingStorage.delete(qb.getSelect());
        } finally {
            results.close();
        }
    }
}

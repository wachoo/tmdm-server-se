/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.query;

import static com.amalto.core.query.user.UserQueryBuilder.contains;
import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;
import static com.amalto.core.query.user.UserQueryBuilder.startsWith;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.amalto.core.initdb.InitDBUtil;
import com.amalto.core.metadata.ClassRepository;
import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.datamodel.DataModelPOJO;
import com.amalto.core.objects.menu.MenuEntryPOJO;
import com.amalto.core.objects.menu.MenuPOJO;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.DispatchWrapper;
import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.SystemStorageWrapper;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.hibernate.TypeMappingStrategy;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.SystemDataRecordXmlWriter;
import com.amalto.core.storage.record.XmlDOMDataRecordReader;
import com.amalto.core.storage.record.XmlSAXDataRecordReader;
import com.amalto.core.storage.record.XmlStringDataRecordReader;

public class SystemStorageTest extends TestCase {

    private static Logger LOG = Logger.getLogger(StorageTestCase.class);

    private static Collection<String> getConfigFiles() throws Exception {
        URL data = InitDBUtil.class.getResource("data");
        List<String> result = new ArrayList<String>();
        if("jar".equals(data.getProtocol())){
            JarURLConnection connection = (JarURLConnection)data.openConnection();
            JarEntry entry = connection.getJarEntry();
            JarFile file = connection.getJarFile();
            Enumeration<JarEntry> entries = file.entries();
            while(entries.hasMoreElements()){
                JarEntry e = entries.nextElement();
                if(e.getName().startsWith(entry.getName()) && !e.isDirectory()){
                    result.add(IOUtils.toString(file.getInputStream(e)));
                }
            }
        }
        else {
            Collection<File> files = FileUtils.listFiles(new File(data.toURI()), new IOFileFilter() {

                @Override
                public boolean accept(File file) {
                    return true;
                }

                @Override
                public boolean accept(File file, String s) {
                    return true;
                }
            }, new IOFileFilter() {

                @Override
                public boolean accept(File file) {
                    return !".svn".equals(file.getName());
                }

                @Override
                public boolean accept(File file, String s) {
                    return !".svn".equals(file.getName());
                }
            });
            for(File f : files){
                result.add(IOUtils.toString(new FileInputStream(f)));
            }
        }
        return result;
    }
    
    private InputStream prepareInputStream(InputStream stream) throws Exception {
        String content = IOUtils.toString(stream);
        ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
        return bais;
    }

    protected static DataSourceDefinition getDatasource(String dataSourceName) {
        return ServerContext.INSTANCE.get().getDefinition(dataSourceName, "MDM");
    }

    public void testSystemRepository() throws Exception {
        ClassRepository repository = buildRepository();
        assertEquals(ObjectPOJO.OBJECT_TYPES.length, repository.getUserComplexTypes().size());
    }

    public void testInternalClusterNames() throws Exception {
        String[] expectedInternalClusters = new String[] { "", "SearchTemplate", "MDMDomainObjects",
                "MDMItemsTrash", "MDMItemImages", "PROVISIONING", "CONF", "amaltoOBJECTSTransformerV2", //$NON-NLS-1$
                "amaltoOBJECTSFailedRoutingOrderV2", //$NON-NLS-1$
                "amaltoOBJECTSCompletedRoutingOrderV2", //$NON-NLS-1$
                "amaltoOBJECTSCustomForm", //$NON-NLS-1$
                "amaltoOBJECTSjcaadapters", //$NON-NLS-1$
                "amaltoOBJECTSRoutingEngineV2", //$NON-NLS-1$
                "amaltoOBJECTSRoutingRule", //$NON-NLS-1$
                "amaltoOBJECTSSynchronizationItem", //$NON-NLS-1$
                "amaltoOBJECTSSynchronizationPlan", //$NON-NLS-1$
                "amaltoOBJECTSservices", //$NON-NLS-1$
                "amaltoOBJECTSTransformerPluginV2", //$NON-NLS-1$
                "amaltoOBJECTSroutingorders", //$NON-NLS-1$
                "amaltoOBJECTSUniverse", //$NON-NLS-1$
                "amaltoOBJECTSVersioningSystem", //$NON-NLS-1$
                "amaltoOBJECTSroutingqueues", //$NON-NLS-1$
                "amaltoOBJECTSroutingservices", //$NON-NLS-1$
                "amaltoOBJECTSStoredProcedure", //$NON-NLS-1$
                "amaltoOBJECTSSynchronizationObject", //$NON-NLS-1$
                "amaltoOBJECTSVersionSystem", //$NON-NLS-1$
                "amaltoOBJECTSMenu", //$NON-NLS-1$
                "amaltoOBJECTSActiveRoutingOrderV2", //$NON-NLS-1$
                "amaltoOBJECTSDataCluster", //$NON-NLS-1$
                "amaltoOBJECTSLicense", //$NON-NLS-1$
                "amaltoOBJECTSRole", //$NON-NLS-1$
                "amaltoOBJECTSDataModel", //$NON-NLS-1$
                "amaltoOBJECTSBackgroundJob", //$NON-NLS-1$
                "amaltoOBJECTSView", //$NON-NLS-1$
                "amaltoOBJECTSConfigurationinfo" //$NON-NLS-1$
        };
        Set<String> internalClusterNames = DispatchWrapper.getInternalClusterNames();
        for (String expectedInternalCluster : expectedInternalClusters) {
            assertTrue("Expected " + expectedInternalCluster, internalClusterNames.contains(expectedInternalCluster));
        }
    }

    private ClassRepository buildRepository() {
        ClassRepository repository = new ClassRepository();
        Class[] objectsToParse = new Class[ObjectPOJO.OBJECT_TYPES.length];
        int i = 0;
        for (Object[] objects : ObjectPOJO.OBJECT_TYPES) {
            objectsToParse[i++] = (Class) objects[1];
        }
        repository.load(objectsToParse);
        return repository;
    }

    public void testGetAllDocumentUniqueId() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");
        // Build a system storage
        ClassRepository repository = buildRepository();
        // Additional setup to get User type in repository
        String[] models = new String[] { "/com/amalto/core/initdb/data/datamodel/PROVISIONING" //$NON-NLS-1$
        };
        for (String model : models) {
            InputStream builtInStream = this.getClass().getResourceAsStream(model);
            if (builtInStream == null) {
                throw new RuntimeException("Built in model '" + model + "' cannot be found.");
            }
            try {
                DataModelPOJO modelPOJO = ObjectPOJO.unmarshal(DataModelPOJO.class, IOUtils.toString(builtInStream, "UTF-8")); //$NON-NLS-1$
                repository.load(new ByteArrayInputStream(modelPOJO.getSchema().getBytes("UTF-8"))); //$NON-NLS-1$
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
        LOG.info("Preparing storage for tests...");
        final Storage storage = new SecuredStorage(new HibernateStorage("MDM", StorageType.SYSTEM), SecuredStorage.UNSECURED);
        storage.init(getDatasource("H2-Default"));
        storage.prepare(repository, Collections.<Expression> emptySet(), true, true);
        LOG.info("Storage prepared.");
        // Wraps it to test wrapper methods
        SystemStorageWrapper wrapper = new SystemStorageWrapper() {
            @Override
            protected Storage getStorage(String dataClusterName) {
                return storage;
            }
        };
        // Test method
        final String[] emptyIds = wrapper.getAllDocumentsUniqueID("PROVISIONING");
        assertEquals(0, emptyIds.length);
        // Add a user (parse a user XML from a 5.0 install)
        XmlDOMDataRecordReader reader = new XmlDOMDataRecordReader();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(SystemStorageTest.class.getResourceAsStream("SystemStorageTest_1.xml"));
        Element element = (Element) document.getElementsByTagName("User").item(0);
        final DataRecord user = reader.read(repository, repository.getComplexType("User"), element);
        storage.begin();
        storage.update(user);
        storage.commit();
        // Test method again (should be one user now)
        final String[] ids = wrapper.getAllDocumentsUniqueID("PROVISIONING");
        assertEquals(1, ids.length);
        assertEquals("PROVISIONING.User.a", ids[0]);
    }

    public void testStorageInit() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing storage for tests...");
        Storage storage = new SecuredStorage(new HibernateStorage("MDM", StorageType.SYSTEM), SecuredStorage.UNSECURED);
        ClassRepository repository = buildRepository();
        storage.init(getDatasource("H2-Default"));
        storage.prepare(repository, Collections.<Expression> emptySet(), true, true);
        LOG.info("Storage prepared.");
    }

    public void testDOMParsing() throws Exception {
        Collection<String> files = getConfigFiles();
        ClassRepository repository = buildRepository();

        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        int error = 0;
        for (String fis1 : files) {
            //FileInputStream fis1 = new FileInputStream(file);
            String typeName;
            Document document;
            try {
                document = documentBuilder.parse(new ByteArrayInputStream(fis1.getBytes()));
                typeName = document.getDocumentElement().getNodeName();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ComplexTypeMetadata complexType = repository.getComplexType(typeName);
            if (complexType == null) {
                //System.out.println("Ignore: " + file);
                continue;
            }
            try {
                dataRecordReader.read(repository, complexType, document.getDocumentElement());
            } catch (Exception e) {
                error++;
            }
        }
        assertEquals(0, error);
    }

    public void testSAXParsing() throws Exception {
        Collection<String> files = getConfigFiles();
        ClassRepository repository = buildRepository();

        DataRecordReader<XmlSAXDataRecordReader.Input> dataRecordReader = new XmlSAXDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        XMLReader reader = XMLReaderFactory.createXMLReader();
        int error = 0;
        for (String fis1: files) {
            try {
                String typeName;
                Document document;
                document = documentBuilder.parse(new ByteArrayInputStream(fis1.getBytes()));
                typeName = document.getDocumentElement().getNodeName();
                ComplexTypeMetadata complexType = repository.getComplexType(typeName);
                if (complexType == null) {
                    continue;
                }
                dataRecordReader.read(repository, complexType, new XmlSAXDataRecordReader.Input(reader, new InputSource(new ByteArrayInputStream(fis1.getBytes()))));
            } catch (Exception e) {
                error++;
            }
        }
        assertEquals(0, error);
    }

    public void testStringParsing() throws Exception {
        Collection<String> files = getConfigFiles();
        ClassRepository repository = buildRepository();

        DataRecordReader<String> dataRecordReader = new XmlStringDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        int error = 0;
        for (String fis1 : files) {
            String typeName;
            Document document;
            try {
                document = documentBuilder.parse(new ByteArrayInputStream(fis1.getBytes()));
                typeName = document.getDocumentElement().getNodeName();
                ComplexTypeMetadata complexType = repository.getComplexType(typeName);
                if (complexType == null) {
                    continue;
                }
                dataRecordReader.read(repository, complexType, fis1);
            } catch (Exception e) {
                e.printStackTrace();
                error++;
            }
        }
        assertEquals(0, error);
    }

    public void testClobQuery() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing storage for tests...");
        HibernateStorage hibernateStorage = new HibernateStorage("MDM", StorageType.SYSTEM) {

            @Override
            protected TypeMappingStrategy getMappingStrategy() {
                return TypeMappingStrategy.SCATTERED_CLOB;
            }

            @Override
            public void adapt(MetadataRepository newRepository, boolean force) {
            }
        };
        Storage storage = new SecuredStorage(hibernateStorage, SecuredStorage.UNSECURED);
        ClassRepository repository = buildRepository();
        storage.init(getDatasource("RDBMS-1-NO-FT"));
        storage.prepare(repository, Collections.<Expression> emptySet(), true, true);
        LOG.info("Storage prepared.");
        // Test CONTAINS
        ComplexTypeMetadata type = repository.getComplexType("failed-routing-order-v2-pOJO");
        UserQueryBuilder qb = from(type).where(contains(type.getField("message"), "test"));
        storage.begin();
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
        } finally {
            results.close();
            storage.commit();
        }
        // Test EQUALS
        qb = from(type).where(eq(type.getField("message"), "test"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
        } finally {
            results.close();
            storage.commit();
        }
        // Test STARTS_WITH
        qb = from(type).where(startsWith(type.getField("message"), "test"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
        } finally {
            results.close();
            storage.commit();
        }
    }

    public void testClobQueryWithFT() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing storage for tests...");
        HibernateStorage hibernateStorage = new HibernateStorage("MDM", StorageType.SYSTEM) {

            @Override
            protected TypeMappingStrategy getMappingStrategy() {
                return TypeMappingStrategy.SCATTERED_CLOB;
            }

            @Override
            public void adapt(MetadataRepository newRepository, boolean force) {
            }
        };
        Storage storage = new SecuredStorage(hibernateStorage, SecuredStorage.UNSECURED);
        ClassRepository repository = buildRepository();
        storage.init(getDatasource("H2-Default"));
        storage.prepare(repository, Collections.<Expression> emptySet(), true, true);
        LOG.info("Storage prepared.");
        // Test CONTAINS
        ComplexTypeMetadata type = repository.getComplexType("failed-routing-order-v2-pOJO");
        UserQueryBuilder qb = from(type).where(contains(type.getField("message"), "test"));
        storage.begin();
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
        } finally {
            results.close();
            storage.commit();
        }
        // Test EQUALS
        qb = from(type).where(eq(type.getField("message"), "test"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
        } finally {
            results.close();
            storage.commit();
        }
        // Test STARTS_WITH
        qb = from(type).where(startsWith(type.getField("message"), "test"));
        storage.begin();
        results = storage.fetch(qb.getSelect());
        try {
            assertEquals(0, results.getCount());
        } finally {
            results.close();
            storage.commit();
        }
    }

    public void testStorageInitPopulate() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing storage for tests...");
        Storage storage = new SecuredStorage(new HibernateStorage("MDM", StorageType.SYSTEM), SecuredStorage.UNSECURED);
        ClassRepository repository = buildRepository();
        storage.init(getDatasource("H2-Default"));
        storage.prepare(repository, Collections.<Expression>emptySet(), true, true);
        LOG.info("Storage prepared.");

        Collection<String> files = getConfigFiles();

        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        int error = 0;
        int ignore = 0;
        List<DataRecord> records = new LinkedList<DataRecord>();
        Set<ComplexTypeMetadata> presentTypes = new HashSet<ComplexTypeMetadata>();
        for (String fis1 : files) {
            //FileInputStream fis1 = new FileInputStream(file);
            String typeName;
            Document document;
            try {
                document = documentBuilder.parse(new ByteArrayInputStream(fis1.getBytes()));
                typeName = document.getDocumentElement().getNodeName();
                ComplexTypeMetadata complexType = repository.getComplexType(typeName);
                if (complexType == null) {
                    ignore++;
                    continue;
                }
                presentTypes.add(complexType);
                records.add(dataRecordReader.read(repository, complexType, document.getDocumentElement()));
            } catch (Exception e) {
                e.printStackTrace();
                error++;
            }
        }
        assertEquals(0, error);

        storage.begin();
        storage.update(records);
        storage.commit();

        int total = 0;
        storage.begin();
        try {
            for (ComplexTypeMetadata presentType : presentTypes) {
                UserQueryBuilder qb = from(presentType);
                StorageResults results = storage.fetch(qb.getSelect());
                try {
                    total += results.getCount();
                    SystemDataRecordXmlWriter writer = new SystemDataRecordXmlWriter(
                            (ClassRepository) storage.getMetadataRepository(), presentType);
                    for (DataRecord result : results) {
                        StringWriter stringWriter = new StringWriter();
                        if ("menu-pOJO".equals(presentType.getName())) {
                            writer.write(result, stringWriter);
                            MenuPOJO menuPOJO = ObjectPOJO.unmarshal(MenuPOJO.class, stringWriter.toString());
                            assertNotNull(menuPOJO);
                            for (MenuEntryPOJO menuEntry : menuPOJO.getMenuEntries()) {
                                assertNotNull(menuEntry.getApplication());
                                assertTrue(!menuEntry.getDescriptions().isEmpty());
                            }
                        }
                        if ("data-model-pOJO".equals(presentType.getName())) {
                            writer.write(result, stringWriter);
                            DataModelPOJO dataModelPOJO = ObjectPOJO.unmarshal(DataModelPOJO.class, stringWriter.toString());
                            assertNotNull(dataModelPOJO.getSchema());
                        }
                    }
                } finally {
                    results.close();
                }
            }
        } finally {
            storage.commit();
        }
        assertEquals(files.size() - ignore, total);
    }

    public void test50UserParse() throws Exception {
        ClassRepository repository = buildRepository();
        // Additional setup to get User type in repository
        String[] models = new String[] { "/com/amalto/core/initdb/data/datamodel/PROVISIONING" //$NON-NLS-1$
        };
        for (String model : models) {
            InputStream builtInStream = this.getClass().getResourceAsStream(model);
            if (builtInStream == null) {
                throw new RuntimeException("Built in model '" + model + "' cannot be found.");
            }
            try {
                DataModelPOJO modelPOJO = ObjectPOJO.unmarshal(DataModelPOJO.class, IOUtils.toString(builtInStream, "UTF-8")); //$NON-NLS-1$
                repository.load(new ByteArrayInputStream(modelPOJO.getSchema().getBytes("UTF-8"))); //$NON-NLS-1$
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
        // Parse a user XML from a 5.0 install
        XmlDOMDataRecordReader reader = new XmlDOMDataRecordReader();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(SystemStorageTest.class.getResourceAsStream("SystemStorageTest_1.xml"));
        Element element = (Element) document.getElementsByTagName("User").item(0);
        reader.read(repository, repository.getComplexType("User"), element);
    }

    public void testUserInformationWithRoles() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");
        LOG.info("Preparing storage for tests...");
        Storage storage = new SecuredStorage(new HibernateStorage("MDM", StorageType.SYSTEM), SecuredStorage.UNSECURED);
        ClassRepository repository = buildRepository();
        // Additional setup to get User type in repository
        String[] models = new String[] { "/com/amalto/core/initdb/data/datamodel/PROVISIONING" //$NON-NLS-1$
        };
        for (String model : models) {
            InputStream builtInStream = this.getClass().getResourceAsStream(model);
            if (builtInStream == null) {
                throw new RuntimeException("Built in model '" + model + "' cannot be found.");
            }
            try {
                DataModelPOJO modelPOJO = ObjectPOJO.unmarshal(DataModelPOJO.class, IOUtils.toString(builtInStream, "UTF-8")); //$NON-NLS-1$
                repository.load(new ByteArrayInputStream(modelPOJO.getSchema().getBytes("UTF-8"))); //$NON-NLS-1$
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
        storage.init(getDatasource("H2-Default"));
        storage.prepare(repository, Collections.<Expression> emptySet(), true, true);
        LOG.info("Storage prepared.");
        // Create users
        DataRecordReader<Element> dataRecordReader = new XmlDOMDataRecordReader();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        List<DataRecord> records = new LinkedList<DataRecord>();
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(SystemStorageTest.class.getResourceAsStream("SystemStorageTest_2.xml")); //$NON-NLS-1$
        Element element = (Element) document.getElementsByTagName("User").item(0); //$NON-NLS-1$
        records.add(dataRecordReader.read(repository, repository.getComplexType("User"), element)); //$NON-NLS-1$ //$NON-NLS-2$
        document = builder.parse(SystemStorageTest.class.getResourceAsStream("SystemStorageTest_3.xml")); //$NON-NLS-1$
        element = (Element) document.getElementsByTagName("User").item(0); //$NON-NLS-1$
        records.add(dataRecordReader.read(repository, repository.getComplexType("User"), element)); //$NON-NLS-1$ //$NON-NLS-2$
        // Commit users
        storage.begin();
        storage.update(records);
        storage.commit();
        // Query test
        storage.begin();
        try {
            ComplexTypeMetadata user = repository.getComplexType("User"); //$NON-NLS-1$
            UserQueryBuilder qb = from(user);
            StorageResults results = storage.fetch(qb.getSelect());
            assertEquals(2, results.getCount());
            try {
                java.util.Iterator<DataRecord> it = results.iterator();
                int count = 0;
                while (it.hasNext()) {
                    count++;
                    DataRecord next = it.next();
                    Object list = next.get("roles/role");
                    assertTrue(list instanceof List);
                    assertEquals(2, ((List) list).size());
                }
                assertEquals(2, count);
            } finally {
                results.close();
            }

        } finally {
            storage.commit();
        }
    }

}

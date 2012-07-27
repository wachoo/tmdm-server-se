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

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.*;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;
import com.amalto.core.storage.task.*;
import com.amalto.core.util.OutputReport;
import com.amalto.core.util.XtentisException;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.talend.mdm.commmon.util.core.ICoreConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.*;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

@SuppressWarnings("nls")
public class StagingAreaTest extends TestCase {

    private static Logger LOG = Logger.getLogger(StagingAreaTest.class);

    private static final int COUNT = 50;

    private Storage origin;

    private Storage destination;

    private ComplexTypeMetadata person;

    private MetadataRepository repository;

    private MetadataRepository stagingRepository;

    private ComplexTypeMetadata address;

    private ComplexTypeMetadata country;

    private ClassLoader contextClassLoader;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        contextClassLoader = Thread.currentThread().getContextClassLoader();

        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        repository = new MetadataRepository();
        repository.load(StorageQueryTest.class.getResourceAsStream("metadata.xsd"));

        stagingRepository = new MetadataRepository();
        stagingRepository.load(Server.class.getResourceAsStream("stagingInternalTypes.xsd"));
        stagingRepository.load(StorageQueryTest.class.getResourceAsStream("metadata.xsd"));

        person = repository.getComplexType("Person");
        address = repository.getComplexType("Address");
        country = repository.getComplexType("Country");

        origin = new HibernateStorage("Origin", StorageType.STAGING);
        destination = new HibernateStorage("Destination", StorageType.MASTER);

        origin.init("H2-Staging-DS1");
        origin.prepare(stagingRepository, true);
        destination.init("H2-Master-DS2");
        destination.prepare(repository, true);
    }

    private void generateData(boolean validData) {
        DataRecordReader<String> factory = new XmlStringDataRecordReader();
        List<DataRecord> allRecords = new LinkedList<DataRecord>();

        allRecords.clear();
        for (int i = 0; i < COUNT; i++) {
            allRecords.add(factory.read(1, repository, country, newCountry(i, validData)));
        }
        try {
            origin.begin();
            origin.update(allRecords);
            origin.commit();
        } finally {
            origin.end();
        }

        allRecords.clear();
        for (int i = 0; i < COUNT; i++) {
            allRecords.add(factory.read(1, repository, address, newAddress(i, i % 2 != 0, validData)));
        }
        try {
            origin.begin();
            origin.update(allRecords);
            origin.commit();
        } finally {
            origin.end();
        }

        allRecords.clear();
        for (int i = 0; i < COUNT; i++) {
            allRecords.add(factory.read(1, repository, person, newPerson(i, validData)));
        }
        try {
            origin.begin();
            origin.update(allRecords);
            origin.commit();
        } finally {
            origin.end();
        }
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        try {
            destination.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        try {
            origin.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

    public void testStaging() throws Exception {
        generateData(true);

        Select select = UserQueryBuilder.from(person).getSelect();
        Select selectEmptyTaskId = UserQueryBuilder.from(person).where(isEmpty(taskId())).getSelect();
        assertEquals(0, destination.fetch(selectEmptyTaskId).getCount());
        assertEquals(0, destination.fetch(select).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(country).getSelect()).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(address).getSelect()).getCount());

        SaverSource source = new TestSaverSource(destination, repository, "metadata.xsd");
        SaverSession.Committer committer = new TestCommitter(destination);
        StagingConfiguration config = new StagingConfiguration(origin, stagingRepository, repository, source, committer, destination);
        TaskExecutor stagingTask = TaskFactory.createStagingTask(config, new TaskFactory.ExecutionConfiguration() {
            @Override
            public Trigger createTrigger() {
                return new SimpleTrigger(String.valueOf(System.currentTimeMillis()), "group");
            }
        });
        TaskSubmitter.getInstance().submitAndWait(stagingTask);

        assertEquals(0, destination.fetch(selectEmptyTaskId).getCount());
        assertEquals(COUNT, destination.fetch(select).getCount());
        assertEquals(COUNT, destination.fetch(UserQueryBuilder.from(country).getSelect()).getCount());
        assertEquals(COUNT, destination.fetch(UserQueryBuilder.from(address).getSelect()).getCount());
        assertEquals(COUNT, origin.fetch(UserQueryBuilder.from(person).getSelect()).getCount());
        assertEquals(COUNT,
                origin.fetch(UserQueryBuilder.from(person).where(eq(status(), StagingConstants.SUCCESS_VALIDATE)).getSelect())
                        .getCount());

        UserQueryBuilder qb = UserQueryBuilder.from(stagingRepository.getComplexType("TALEND_TASK_EXECUTION"));
        StorageResults results = origin.fetch(qb.getSelect());
        try {
            assertEquals(1, results.getCount());
        } finally {
            results.close();
        }
    }

    public void testCancel() throws Exception {
        generateData(true);

        Select select = UserQueryBuilder.from(person).getSelect();
        Select selectEmptyTaskId = UserQueryBuilder.from(person).where(isEmpty(taskId())).getSelect();
        assertEquals(0, destination.fetch(selectEmptyTaskId).getCount());
        assertEquals(0, destination.fetch(select).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(country).getSelect()).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(address).getSelect()).getCount());

        SaverSource source = new TestSaverSource(destination, repository, "metadata.xsd");
        SaverSession.Committer committer = new TestCommitter(destination);
        Task stagingTask = new StagingTask(TaskSubmitter.getInstance(), origin, stagingRepository, repository, source, committer, destination, UUID.randomUUID().toString());
        TaskSubmitter.getInstance().submit(stagingTask);

        Thread.sleep(200);
        stagingTask.cancel();

        assertEquals(0, destination.fetch(selectEmptyTaskId).getCount());
        assertEquals(0, destination.fetch(select).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(country).getSelect()).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(address).getSelect()).getCount());
        assertEquals(COUNT, origin.fetch(UserQueryBuilder.from(person).getSelect()).getCount());
        assertEquals(0,
                origin.fetch(UserQueryBuilder.from(person).where(eq(status(), StagingConstants.SUCCESS_VALIDATE)).getSelect())
                        .getCount());
    }

    public void testWithValidationErrors() throws Exception {
        generateData(false);

        Select select = UserQueryBuilder.from(person).getSelect();
        Select selectEmptyTaskId = UserQueryBuilder.from(person).where(isEmpty(taskId())).getSelect();
        assertEquals(0, destination.fetch(selectEmptyTaskId).getCount());
        assertEquals(0, destination.fetch(select).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(country).getSelect()).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(address).getSelect()).getCount());

        SaverSource source = new TestSaverSource(destination, repository, "metadata.xsd");
        SaverSession.Committer committer = new TestCommitter(destination);
        Task stagingTask = new StagingTask(TaskSubmitter.getInstance(), origin, stagingRepository, repository, source, committer, destination, UUID.randomUUID().toString());
        TaskSubmitter.getInstance().submitAndWait(stagingTask);

        assertEquals(0, destination.fetch(selectEmptyTaskId).getCount());
        assertEquals(0, destination.fetch(select).getCount());
        assertEquals(COUNT, destination.fetch(UserQueryBuilder.from(country).getSelect()).getCount());
        assertEquals(0, destination.fetch(UserQueryBuilder.from(address).getSelect()).getCount());
        assertEquals(COUNT, origin.fetch(UserQueryBuilder.from(person).getSelect()).getCount());
        assertEquals(
                COUNT,
                origin.fetch(
                        UserQueryBuilder.from(person).where(eq(status(), StagingConstants.FAIL_VALIDATE_VALIDATION)).getSelect())
                        .getCount());
    }

    private static class TestSaverSource implements SaverSource {

        private final Storage storage;

        private final MetadataRepository repository;

        private final String fileName;

        private TestSaverSource(Storage storage, MetadataRepository repository, String fileName) {
            this.storage = storage;
            this.repository = repository;
            this.fileName = fileName;
        }

        @Override
        public InputStream get(String dataClusterName, String typeName, String revisionId, String[] key) {
            Select select = selectById(typeName, key);
            StorageResults dataRecords = storage.fetch(select);
            try {
                if (dataRecords.getCount() > 0) {
                    DataRecordWriter writer = new DataRecordXmlWriter();
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    try {
                        writer.write(dataRecords.iterator().next(), output);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return new ByteArrayInputStream(output.toByteArray());
                } else {
                    return null;
                }
            } finally {
                dataRecords.close();
            }
        }

        private Select selectById(String typeName, String[] key) {
            ComplexTypeMetadata type = repository.getComplexType(typeName);
            UserQueryBuilder qb = UserQueryBuilder.from(type);
            int i = 0;
            for (FieldMetadata keyField : type.getKeyFields()) {
                qb.where(UserQueryBuilder.eq(keyField, key[i]));
            }
            return qb.getSelect();
        }

        @Override
        public boolean exist(String dataCluster, String typeName, String revisionId, String[] key) {
            Select select = selectById(typeName, key);
            StorageResults dataRecords = storage.fetch(select);
            try {
                return dataRecords.getCount() > 0;
            } finally {
                dataRecords.close();
            }
        }

        @Override
        public MetadataRepository getMetadataRepository(String dataModelName) {
            return repository;
        }

        @Override
        public InputStream getSchema(String dataModelName) {
            return StorageQueryTest.class.getResourceAsStream(fileName);
        }

        @Override
        public String getUniverse() {
            return storage.getName();
        }

        @Override
        public OutputReport invokeBeforeSaving(DocumentSaverContext context, MutableDocument updateReportDocument) {
            return null;
        }

        @Override
        public Set<String> getCurrentUserRoles() {
            return Collections.singleton(ICoreConstants.ADMIN_PERMISSION);
        }

        @Override
        public String getUserName() {
            return ICoreConstants.ADMIN_PERMISSION;
        }

        @Override
        public boolean existCluster(String revisionID, String dataClusterName) {
            return true;
        }

        @Override
        public String getConceptRevisionID(String typeName) {
            return "HEAD";
        }

        @Override
        public void resetLocalUsers() {
        }

        @Override
        public void initAutoIncrement() {
        }

        @Override
        public void routeItem(String dataCluster, String typeName, String[] id) {
        }

        @Override
        public void invalidateTypeCache(String dataModelName) {
        }

        @Override
        public void saveAutoIncrement() {
        }

        @Override
        public String nextAutoIncrementId(String universe, String dataCluster, String conceptName) {
            return "0";
        }
    }

    private class TestCommitter implements SaverSession.Committer {

        private final Storage storage;

        private final XmlDOMDataRecordReader reader;

        private TestCommitter(Storage storage) {
            this.storage = storage;
            reader = new XmlDOMDataRecordReader();
        }

        @Override
        public void begin(String dataCluster) {
            storage.begin();
        }

        @Override
        public void commit(String dataCluster) {
            storage.commit();
            storage.end();
        }

        @Override
        public void save(ItemPOJO item, String revisionId) {
            try {
                ComplexTypeMetadata complexType = repository.getComplexType(item.getProjection().getTagName());
                DataRecord dataRecord = reader.read(1, repository, complexType, item.getProjection());
                DataRecordMetadata recordMetadata = dataRecord.getRecordMetadata();
                recordMetadata.setLastModificationTime(item.getInsertionTime());
                recordMetadata.setTaskId(item.getTaskId());
                storage.update(dataRecord);
            } catch (XtentisException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void rollback(String dataCluster) {
            storage.rollback();
        }
    }

    private String newAddress(int id1, boolean id2, boolean validData) {
        if (validData) {
            return "<Address><Id>" + id1 + "</Id><enterprise>" + id2
                    + "</enterprise><Street>Street1</Street><ZipCode>10000</ZipCode><City>City</City><country>[" + id1
                    + "]</country></Address>";
        } else {
            return "<Address><Id>"
                    + id1
                    + "</Id><enterprise>"
                    + id2
                    + "</enterprise><Street>Street1</Street><ZipCode>test</ZipCode><City>City</City><country>[-1]</country></Address>";
        }
    }

    private String newCountry(int id, boolean validData) {
        if (validData) {
            return "<Country><id>"
                    + id
                    + "</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France</name></Country>";
        } else {
            return "<Country><id>"
                    + id
                    + "</id><creationDate>2010-10-10</creationDate><creationTime>2010-10-10T00:00:01</creationTime><name>France</name></Country>";
        }
    }

    private String newPerson(int id, boolean validData) {
        if (validData) {
            return "<Person><id>"
                    + id
                    + "</id><score>130000.00</score><lastname>Dupond</lastname><middlename>John</middlename><firstname>Julien</firstname><addresses><address>["
                    + id
                    + "]["
                    + String.valueOf(id % 2 != 0)
                    + "]</address><address>[1][false]</address></addresses><age>10</age><Status>Employee</Status><Available>true</Available></Person>";
        } else {
            return "<Person><id>"
                    + id
                    + "</id><score>130000.00</score><lastname>Dupond</lastname><middlename>John</middlename><firstname>Julien</firstname><addresses><address>["
                    + id
                    + "]["
                    + String.valueOf(id % 2 == 0)
                    + "]</address><address>[1][false]</address></addresses><age>10</age><Status>Employee</Status><Available>true</Available></Person>";
        }
    }
}

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
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class StorageIsolationTest extends TestCase {
    private final Map<String, Storage> nameToStorage = new HashMap<String, Storage>();

    static {
        System.out.println("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        System.out.println("MDM server environment set.");
    }

    public void testNothing() throws Exception {
        // TODO Test fails in build, fix this.
    }

    public void __test1() throws Exception {
        MetadataRepository repository1 = prepareMetadata("StorageIsolationTest_1.xsd");
        MetadataRepository repository2 = prepareMetadata("StorageIsolationTest_2.xsd");
        assertNotSame(repository1, repository2);

        ComplexTypeMetadata type1 = repository1.getComplexType("StorageIsolationTest1");
        ComplexTypeMetadata type2 = repository2.getComplexType("StorageIsolationTest1");
        assertNotSame(type1, type2);

        MainTestRunnable runnable1 = new MainTestRunnable(repository1, StorageTestCase.DATABASE + "-DS1", "MDM1", "StorageIsolationTest1", 300, "ValueMDM1", "ValueMDM2");
        Thread t1 = new Thread(runnable1);

        MainTestRunnable runnable2 = new MainTestRunnable(repository2, StorageTestCase.DATABASE + "-DS2", "MDM2", "StorageIsolationTest1", 500, "ValueMDM2", "ValueMDM1");
        Thread t2 = new Thread(runnable2);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Run1: " + runnable1.getActualInstanceNumber());
        System.out.println("Run2: " + runnable2.getActualInstanceNumber());

        assertEquals(300, runnable1.getActualInstanceNumber());
        assertEquals(500, runnable2.getActualInstanceNumber());
        assertEquals(0, runnable1.getActualFullTextResults());
        assertEquals(0, runnable2.getActualFullTextResults());
        assertTrue(runnable1.getChaosMonkey().isSuccess());
        assertTrue(runnable2.getChaosMonkey().isSuccess());
    }

    private MetadataRepository prepareMetadata(String dataModelFile) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(StorageFullTextTest.class.getResourceAsStream(dataModelFile));
        return repository;
    }

    private Storage createStorage(String storageName) {
        synchronized (nameToStorage) {
            if (nameToStorage.containsKey(storageName)) {
                return nameToStorage.get(storageName);
            } else {
                Storage storage = new HibernateStorage(storageName);
                nameToStorage.put(storageName, storage);
                return storage;
            }
        }
    }

    private class ChaosMonkeyRunnable implements Runnable {

        private final MetadataRepository repository;

        private final String storageName;

        private boolean isSuccess = true;

        private ChaosMonkeyRunnable(MetadataRepository repository, String storageName) {
            this.repository = repository;
            this.storageName = storageName;
        }

        public void run() {
            Storage storage = createStorage(storageName);
            try {
                int millis = new Random().nextInt(1000);
                System.out.println("Chaos monkey for " + storageName + " sleeping for " + millis + " ms.");
                Thread.sleep(millis);
                System.out.println("Chaos monkey for " + storageName + " is reinitializing storage!");
                storage.prepare(repository, Collections.<FieldMetadata>emptySet(), true, true);
                System.out.println("Chaos monkey for " + storageName + " has finished storage reinitialization.");
                isSuccess = true;
            } catch (Exception e) {
                isSuccess = false;
            }
        }

        public boolean isSuccess() {
            return isSuccess;
        }
    }

    private class MainTestRunnable implements Runnable {

        private final MetadataRepository repository;

        private final String dataSourceName;

        private final String storageName;

        private final String typeName;

        private final int instanceNumber;

        private final String valueText;

        private final String valueNotToBeFound;
        
        private int actualInstanceNumber;
        
        private int actualFullTextResults;

        private final ChaosMonkeyRunnable chaosMonkey;

        private MainTestRunnable(MetadataRepository repository, String dataSourceName, String storageName, String typeName, int instanceNumber, String valueText, String valueNotToBeFound) {
            this.repository = repository;
            this.dataSourceName = dataSourceName;
            this.storageName = storageName;
            this.typeName = typeName;
            this.instanceNumber = instanceNumber;
            this.valueText = valueText;
            this.valueNotToBeFound = valueNotToBeFound;
            chaosMonkey = new ChaosMonkeyRunnable(repository, storageName);
        }

        public void run() {
            ComplexTypeMetadata type = repository.getComplexType(typeName);
            Storage storage = createStorage(storageName);
            System.out.println("Main test for " + storageName + " initialization...");
            storage.init(dataSourceName);
            storage.prepare(repository, false);
            System.out.println("Main test for " + storageName + "  initialization done.");

            Thread cmt1 = new Thread(chaosMonkey);
            cmt1.start();

            StorageResults results = null;
            try {
                DataRecordReader<String> factory = new XmlStringDataRecordReader();

                storage.begin();
                for (int i = 0; i < instanceNumber; i++) {
                    DataRecord record = factory.read(1, repository, type, "<" + typeName + "><Id>" + i + "</Id><field>" + valueText + "</field></" + typeName + ">");
                    storage.update(record);
                }
                storage.commit();
                storage.end();

                UserQueryBuilder qb = UserQueryBuilder.from(type).select(type.getField("field"));
                results = storage.fetch(qb.getSelect());
                actualInstanceNumber = results.getCount();

                qb = UserQueryBuilder.from(type).where(UserQueryBuilder.fullText(valueNotToBeFound));
                results = storage.fetch(qb.getSelect());
                actualFullTextResults = results.getCount();
            } catch (Exception e) {
                e.printStackTrace();
                actualInstanceNumber = -1;
            } finally {
                if (results != null) {
                    results.close();
                }
                storage.close();
                System.out.println("Main test for " + storageName + " closed storage.");
            }
        }

        public int getActualInstanceNumber() {
            return actualInstanceNumber;
        }

        public int getActualFullTextResults() {
            return actualFullTextResults;
        }

        public ChaosMonkeyRunnable getChaosMonkey() {
            return chaosMonkey;
        }
    }
}

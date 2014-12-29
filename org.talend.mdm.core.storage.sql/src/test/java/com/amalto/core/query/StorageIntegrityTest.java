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

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.XmlStringDataRecordReader;

@SuppressWarnings("nls")
public class StorageIntegrityTest extends TestCase {

    public StorageIntegrityTest() {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
    }

    public void test1() throws Exception {
        MetadataRepository repository = prepareMetadata("StorageIntegrityTest_1.xsd");
        ComplexTypeMetadata type = repository.getComplexType("Metadata3_Main_1");
        Storage storage = prepareStorage(repository);

        try {
            DataRecordReader<String> factory = new XmlStringDataRecordReader();
            DataRecord record = factory.read(repository, type,
                    "<Metadata3_Main_1><Id>1</Id><ref>[999]</ref></Metadata3_Main_1>");
            storage.begin();
            storage.update(record);
            try {
                storage.commit();
                fail("Expected fail due to FK constraint.");
            } catch (Exception e) {
                // Expected
            } finally {
                storage.end();
            }
        } finally {
            storage.close();
        }
    }

    public void test2() throws Exception {
        MetadataRepository repository = prepareMetadata("StorageIntegrityTest_2.xsd");
        ComplexTypeMetadata type = repository.getComplexType("Metadata3_Main_2");
        Storage storage = prepareStorage(repository);

        try {
            DataRecordReader<String> factory = new XmlStringDataRecordReader();
            DataRecord record = factory.read(repository, type,
                    "<Metadata3_Main_2><Id>1</Id><ref>[999]</ref></Metadata3_Main_2>");
            try {
                storage.begin();
                storage.update(record);
                storage.commit(); // Should not fail (integrity constraint is switched off in this model).
            } finally {
                storage.end();
            }
        } finally {
            storage.close();
        }
    }

    public void test3() throws Exception {
        MetadataRepository repository = prepareMetadata("StorageIntegrityTest_3.xsd");
        ComplexTypeMetadata type = repository.getComplexType("Metadata3_Main_3");
        Storage storage = prepareStorage(repository);

        try {
            DataRecordReader<String> factory = new XmlStringDataRecordReader();
            DataRecord record = factory.read(repository, type,
                    "<Metadata3_Main_3><Id>1</Id><ref>[999]</ref><ref>[1000]</ref></Metadata3_Main_3>");
            storage.begin();
            storage.update(record);
            try {
                storage.commit();
                fail("Expected fail due to FK constraint.");
            } catch (Exception e) {
                // Expected
            } finally {
                storage.end();
            }
        } finally {
            storage.close();
        }
    }

    public void test4() throws Exception {
        MetadataRepository repository = prepareMetadata("StorageIntegrityTest_4.xsd");
        ComplexTypeMetadata type = repository.getComplexType("Metadata3_Main_4");
        Storage storage = prepareStorage(repository);

        try {
            DataRecordReader<String> factory = new XmlStringDataRecordReader();
            DataRecord record = factory.read(repository, type,
                    "<Metadata3_Main_4><Id>1</Id><ref>[999]</ref><ref>[1000]</ref></Metadata3_Main_4>");
            try {
                storage.begin();
                storage.update(record);
                storage.commit(); // Should not fail (integrity constraint is switched off in this model).
            } finally {
                storage.end();
            }
        } finally {
            storage.close();
        }
    }

    public void test5() throws Exception {
        MetadataRepository repository = prepareMetadata("StorageIntegrityTest_5.xsd");
        ComplexTypeMetadata type = repository.getComplexType("Metadata3_Main_5");
        Storage storage = prepareStorage(repository);

        try {
            DataRecordReader<String> factory = new XmlStringDataRecordReader();
            DataRecord record = factory.read(repository, type, "<Metadata3_Main_5><Id>1</Id><ref/></Metadata3_Main_5>");
            storage.begin();
            try {
                storage.update(record);
                storage.commit();
                fail("Expected fail due to NOT NULL constraint.");
            } catch (Exception e) {
                // Expected
            } finally {
                storage.end();
            }
        } finally {
            storage.close();
        }
    }

    public void test6() throws Exception {
        MetadataRepository repository = prepareMetadata("StorageIntegrityTest_6.xsd");
        ComplexTypeMetadata type = repository.getComplexType("Metadata3_Referenced_6");
        Storage storage = prepareStorage(repository);

        try {
            DataRecordReader<String> factory = new XmlStringDataRecordReader();
            DataRecord record = factory.read(repository, type,
                    "<Metadata3_Referenced_6><Id>1</Id><field/></Metadata3_Referenced_6>");
            storage.begin();
            try {
                storage.update(record);
                storage.commit();
                fail("Expected fail due to NOT NULL constraint.");
            } catch (Exception e) {
                // Expected
            } finally {
                storage.end();
            }
        } finally {
            storage.close();
        }
    }

    public void test7() throws Exception {
        MetadataRepository repository = prepareMetadata("StorageIntegrityTest_6.xsd");
        ComplexTypeMetadata type = repository.getComplexType("Metadata3_Referenced_6");
        Storage storage = prepareStorage(repository);

        try {
            DataRecordReader<String> factory = new XmlStringDataRecordReader();
            DataRecord record = factory.read(repository, type,
                    "<Metadata3_Referenced_6><Id>1</Id><field/></Metadata3_Referenced_6>");
            storage.begin();
            try {
                storage.update(record);
                storage.commit();
                fail("Expected fail due to NOT NULL constraint.");
            } catch (Exception e) {
                storage.rollback();
            }

            record = factory.read(repository, type,
                    "<Metadata3_Referenced_6><Id>1</Id><field>Field Value</field></Metadata3_Referenced_6>");
            storage.begin();
            storage.update(record);
            storage.commit();
        } finally {
            storage.close();
        }
    }

    private MetadataRepository prepareMetadata(String dataModelFile) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(StorageFullTextTest.class.getResourceAsStream(dataModelFile));
        return repository;
    }

    private Storage prepareStorage(MetadataRepository repository) {
        Storage storage = new HibernateStorage("MDMStorageIntegrityTest");
        storage.init(ServerContext.INSTANCE.get().getDefinition(StorageTestCase.DATABASE + "-DS1", "MDM"));
        storage.prepare(repository, true);
        return storage;
    }
}

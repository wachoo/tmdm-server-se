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
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

public class InheritanceTest extends TestCase {

    public void testTypeOrdering() throws Exception {
        MetadataRepository repository = new MetadataRepository();
        repository.load(InheritanceTest.class.getResourceAsStream("inheritance.xsd"));

        List<ComplexTypeMetadata> sortedList = MetadataUtils.sortTypes(repository);
        String[] expectedOrder = {"B", "D", "A", "C"};
        int i = 0;
        for (ComplexTypeMetadata sortedType : sortedList) {
            assertEquals(expectedOrder[i++], sortedType.getName());
        }
    }

    // Disable because it has side effect to the other tests.
    public void __testStorageInit() throws Exception {
        System.out.println("Setting up MDM server environment...");
        MockServerLifecycle lifecycle = new MockServerLifecycle();
        ServerContext.INSTANCE.get(lifecycle);
        System.out.println("MDM server environment set.");

        MetadataRepository repository = new MetadataRepository();
        repository.load(InheritanceTest.class.getResourceAsStream("inheritance.xsd"));


        Storage s = new HibernateStorage("inheritance", HibernateStorage.StorageType.MASTER);
        try {
            s.init(StorageTestCase.DATABASE + "-Default");
            s.prepare(repository, true);

            DataRecordReader<String> factory = new XmlStringDataRecordReader();

            ComplexTypeMetadata a = repository.getComplexType("A");
            ComplexTypeMetadata b = repository.getComplexType("B");
            ComplexTypeMetadata c = repository.getComplexType("C");
            ComplexTypeMetadata d = repository.getComplexType("D");

            List<DataRecord> allRecords = new LinkedList<DataRecord>();
            allRecords.add(factory.read("MDM", 1, repository, b, "<B><id>1</id><textB>TextB</textB></B>"));
            allRecords.add(factory.read("MDM", 1, repository, d, "<D><id>2</id><textB>TextBD</textB><textD>TextDD</textD></D>"));
            allRecords.add(factory.read("MDM", 1, repository, a, "<A xmlns:tmdm=\"http://www.talend.com/mdm\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>1</id><refB tmdm:type=\"B\">[1]</refB><textA>TextA</textA><nestedB xsi:type=\"Nested\"><text>Text</text></nestedB></A>"));
            allRecords.add(factory.read("MDM", 1, repository, c, "<C xmlns:tmdm=\"http://www.talend.com/mdm\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>2</id><refB tmdm:type=\"D\">[2]</refB><textA>TextAC</textA><nestedB xsi:type=\"SubNested\"><text>Text</text><subText>SubText</subText></nestedB><textC>TextCC</textC></C>"));

            try {
                s.begin();
                s.update(allRecords);
                s.commit();
            } finally {
                s.end();
            }

            UserQueryBuilder qb = UserQueryBuilder.from(c);
            StorageResults results = s.fetch(qb.getSelect());
            DataRecordXmlWriter writer = new DataRecordXmlWriter();
            for (DataRecord result : results) {
                writer.write(result, System.out);
            }
        } finally {
            s.close();
            lifecycle.destroyServer(ServerContext.INSTANCE.get());
        }
    }
}

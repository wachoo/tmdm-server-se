/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */
package com.amalto.core.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.objects.datacluster.DataClusterPOJOPK;
import com.amalto.core.query.StorageQueryTest;
import com.amalto.core.server.api.Item;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;


/**
 * created by John on Jan 7, 2016
 * Detailled comment
 *
 */
@SuppressWarnings("nls")
public class DefaultItemTest extends TestCase {
    
    @Override
    public void tearDown() throws Exception {
        ServerContext.INSTANCE.close();
    }

    @Override
    public void setUp() throws Exception {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
    }
    
    public void testCount() throws Exception {
        Server server = ServerContext.INSTANCE.get();
        assertNotNull(server);

        MetadataRepository repository = new MetadataRepository();
        repository.load(StorageQueryTest.class.getResourceAsStream("metadata.xsd"));
        MockMetadataRepositoryAdmin.INSTANCE.register("DStar", repository);
                
        StorageAdmin storageAdmin = server.getStorageAdmin();
        assertNotNull(storageAdmin);
        Storage storage = storageAdmin.create("DStar", "DStar", StorageType.MASTER, "H2-DS1");
        assertNotNull(storage);

        ComplexTypeMetadata person = repository.getComplexType("Person");
        assertNotNull(person);
        
        DataRecordReader<String> factory = new XmlStringDataRecordReader();
        List<DataRecord> allRecords = new LinkedList<DataRecord>();
        
        allRecords.add(factory.read(repository, person,
                        "<Person><id>3</id><score>200000.00</score><lastname>Leblanc</lastname><middlename>John</middlename><firstname>Juste</firstname><age>30</age><Status>Friend</Status></Person>"));
        allRecords.add(factory.read(repository, person,
                        "<Person><id>4</id><score>200000.00</score><lastname>Leblanc</lastname><middlename>John</middlename><firstname>Julien</firstname><age>30</age><Status>Friend</Status></Person>"));
        storage.begin();
        storage.update(allRecords);
        storage.commit();

        Item item = new DefaultItem();
        long count = item.count(new DataClusterPOJOPK("DStar"), "Person", null, 0);
        assertTrue(count == 2);
        
        ArrayList<IWhereItem> conditions = new ArrayList<IWhereItem>();
        conditions.add(new WhereCondition("Person/firstname",
                WhereCondition.EQUALS, "Juste", "NONE"));
        conditions.add(new WhereCondition("Person/age",
                WhereCondition.EQUALS, "30", "NONE"));
        IWhereItem whereItem = new WhereAnd(conditions);
        count = item.count(new DataClusterPOJOPK("DStar"), "Person", whereItem, 0);
        assertTrue(count == 1);
    }

}

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

import java.util.Collections;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.query.user.Expression;
import com.amalto.core.save.RecordValidationTest.MockUserDelegator;
import com.amalto.core.save.generator.AutoIncrementGenerator;
import com.amalto.core.server.MockMetadataRepositoryAdmin;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.MockStorageAdmin;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.hibernate.HibernateStorage;

@SuppressWarnings("nls")
public class AutoIncrementTest extends TestCase {
    
    private static final Logger LOG = Logger.getLogger(RecordValidationTest.class);
    
    protected static final Storage masterStorage;
    
    protected static final MetadataRepository testAIRepository;
    
    protected static MockUserDelegator userSecurity = new MockUserDelegator();
    
    public static final String DATASOURCE = "H2-Fulltext";

    static {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");
        
        LOG.info("Preparing master storage");
        masterStorage = new SecuredStorage(new HibernateStorage("TestAI", StorageType.MASTER), userSecurity);
        testAIRepository = new MetadataRepository();
        testAIRepository.load(RecordValidationTest.class.getResourceAsStream("../save/AutoIncrement.xsd"));
        MockMetadataRepositoryAdmin.INSTANCE.register("TestAI", testAIRepository);
        masterStorage.init(ServerContext.INSTANCE.get().getDefinition(DATASOURCE, "TestAI"));
        masterStorage.prepare(testAIRepository, Collections.<Expression> emptySet(), true, true);
        ((MockStorageAdmin) ServerContext.INSTANCE.get().getStorageAdmin()).register(masterStorage);
        LOG.info("Master storage prepared");
    }

    // C extends B extends A, the concept for AutoIncrement should all be "A"
    public void testGetConceptForAutoIncrement() throws Exception {
        String[] conceptNames = { "A.Id", "B.Id", "C.Id", "A", "B", "C" };
        for (String conceptName : conceptNames) {
            assertEquals("A", AutoIncrementGenerator.getConceptForAutoIncrement("TestAI", conceptName));
        }
    }

}

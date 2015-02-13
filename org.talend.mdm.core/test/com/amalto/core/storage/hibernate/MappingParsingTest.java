// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.storage.hibernate;

import static com.amalto.core.query.user.UserQueryBuilder.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.query.StorageTestCase;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.datasource.DataSourceDefinition;
import com.amalto.core.storage.datasource.RDBMSDataSource;

public class MappingParsingTest extends TestCase {

    Logger LOGGER = Logger.getLogger(HibernateStorage.class);

    public MappingParsingTest() {
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOGGER.setLevel(Level.TRACE);
    }

    public void testInheritanceIndexLength() throws Exception {
        InputStream expectedDataInputStream = null;
        InputStream resultDataInputStream = null;
        final MetadataRepository repository = prepareMetadata("MappingParsingTest_1.xsd"); //$NON-NLS-1$
        prepareStorage(repository);
        expectedDataInputStream = this.getClass().getResourceAsStream("MappingParsingResult.dll"); //$NON-NLS-1$
        assertNotNull(expectedDataInputStream);
        BufferedReader expectedBufferedReader = new BufferedReader(new InputStreamReader(expectedDataInputStream));
        String tempString;
        StringBuffer expectedStringBuffer = new StringBuffer();
        while ((tempString = expectedBufferedReader.readLine()) != null) {
            expectedStringBuffer.append(tempString);
        }

        String jbossServerTempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
        String filename = jbossServerTempDir + File.separator + "MappingParsingTest_MASTER_H2.ddl"; //$NON-NLS-1$
        resultDataInputStream = new FileInputStream(new File(filename));
        assertNotNull(resultDataInputStream);
        BufferedReader resultBufferedReader = new BufferedReader(new InputStreamReader(resultDataInputStream));
        StringBuffer resultStringBuffer = new StringBuffer();
        while ((tempString = resultBufferedReader.readLine()) != null) {
            resultStringBuffer.append(tempString);
        }
        assertEquals(expectedStringBuffer.toString(), resultStringBuffer.toString());
    }

    private MetadataRepository prepareMetadata(String dataModelFile) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(MappingParsingTest.class.getResourceAsStream(dataModelFile));
        return repository;
    }

    private Storage prepareStorage(MetadataRepository repository) {
        ComplexTypeMetadata partyType = repository.getComplexType("Party"); //$NON-NLS-1$
        List<Expression> indexedExpressions = new LinkedList<Expression>();
        indexedExpressions.add(UserQueryBuilder.from(partyType).where(isNull(partyType.getField("PartyType"))).getExpression()); //$NON-NLS-1$
        indexedExpressions.add(UserQueryBuilder.from(partyType).where(isNull(partyType.getField("SixtID"))).getExpression()); //$NON-NLS-1$
        Storage storage = new HibernateStorage("MappingParsingTest"); //$NON-NLS-1$
        DataSourceDefinition definition = ServerContext.INSTANCE.get().getDefinition(StorageTestCase.DATABASE + "-DS1", "MDM"); //$NON-NLS-1$//$NON-NLS-2$
        RDBMSDataSource masterDataSource = (RDBMSDataSource) definition.getMaster();
        masterDataSource.setNameMaxLength(30);
        RDBMSDataSource stagingDataSource = (RDBMSDataSource) definition.getStaging();
        stagingDataSource.setNameMaxLength(30);
        storage.init(definition);
        storage.prepare(repository, new HashSet<Expression>(indexedExpressions), true, true);
        return storage;
    }
}

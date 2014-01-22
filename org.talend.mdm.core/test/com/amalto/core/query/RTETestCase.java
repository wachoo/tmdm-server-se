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

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.query.user.UserQueryDumpConsole;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RTETestCase extends TestCase {

    private static Logger LOG = Logger.getLogger(RTETestCase.class);

    private final InputStream resourceAsStream;

    public RTETestCase() {
        resourceAsStream = RTETestCase.class.getResourceAsStream("veolia.xsd");
    }

    public void testModel() throws Exception {
        // Here so JUnit does not complain about a test case with no test.
    }

    // Disables this test for test suite execution
    public void __test() throws Exception {
        LOG.info("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        LOG.info("MDM server environment set.");

        LOG.info("Preparing storage for tests...");
        Storage storage = new HibernateStorage("MDM");
        MetadataRepository repository = new MetadataRepository();
        repository.load(resourceAsStream);

        storage.init(ServerContext.INSTANCE.get().getDefinition("RDBMS-1", "MDM"));
        storage.prepare(repository, true);
        LOG.info("Storage prepared.");

        Collection<ComplexTypeMetadata> types = MetadataUtils.sortTypes(repository);
        List<Expression> failedExpressions = new LinkedList<Expression>();
        int numberOfQueries = 0;
        try {
            int failedCreation = 0;
            for (ComplexTypeMetadata type : types) {
                if (type.getKeyFields().size() > 0) {
                    boolean commitSuccess = false;
                    try {
                        storage.begin();
                        storage.update(type.accept(new TestDataRecordCreator()));
                        storage.commit();
                        commitSuccess = true;
                    } catch (Exception e) {
                        failedCreation++;
                        storage.rollback();
                    }

                    if (commitSuccess) {
                        List<Expression> expressions = new LinkedList<Expression>();
                        expressions.add(UserQueryBuilder.from(type).getSelect());
                        expressions.addAll(type.accept(new SimpleQueryGenerator()));
                        expressions.addAll(type.accept(new OrderByQueryGenerator()));
                        expressions.addAll(type.accept(new JoinQueryGenerator()));
                        numberOfQueries += expressions.size();
                        System.out.print("Running " + expressions.size() + " queries on '" + type.getName() + "'");
                        for (Expression expression : expressions) {
                            try {
                                StorageResults results = storage.fetch(expression);
                                for (DataRecord result : results) {
                                    assertNotNull(result);
                                }
                                System.out.print('.');
                            } catch (Exception e) {
                                System.out.print('!');
                                failedExpressions.add(expression);
                            }
                        }
                        System.out.println("");
                    }
                }
            }
            System.out.println("Failed creations: " + failedCreation + "/" + types.size());
            System.out.println("Failed queries: " + failedExpressions.size() + "/" + numberOfQueries);
            for (Expression failedExpression : failedExpressions) {
                failedExpression.accept(new UserQueryDumpConsole());
            }
            //assertEquals(0, failedCreation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

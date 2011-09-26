// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.server.util;

import org.talend.mdm.webapp.base.client.exception.ParserException;
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;
import org.talend.mdm.webapp.base.server.i18n.BaseMessagesImpl;

import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class CommonUtilTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        BaseMessagesFactory.setMessages(new BaseMessagesImpl());
    }
    
    @Override
    protected void tearDown() throws Exception {
        BaseMessagesFactory.setMessages(null);
    }

    public void testContainsSearch() {
        String criteria = "(MyEntity/id CONTAINS \\(test\\(\\)\\))";
        try {
            WSWhereItem wsWhereItem = CommonUtil.buildWhereItems(criteria);

            assertNotNull(wsWhereItem);
            WSWhereAnd whereAnd = wsWhereItem.getWhereAnd();
            assertNotNull(whereAnd);
            assertEquals(1, whereAnd.getWhereItems().length);
            WSWhereItem whereItem = whereAnd.getWhereItems()[0];
            assertNotNull(whereItem);
            WSWhereCondition condition = whereItem.getWhereCondition();
            assertNotNull(condition);

            assertEquals("MyEntity/id", condition.getLeftPath());
            assertEquals("CONTAINS", condition.getOperator().getValue());
            assertEquals("(test())", condition.getRightValueOrPath());
            assertEquals("NONE", condition.getStringPredicate().getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testContainsSearch2() {
        String criteria = "(MyEntity/id CONTAINS \\(test \\(\\))"; // NON-NLS
        try {
            WSWhereItem wsWhereItem = CommonUtil.buildWhereItems(criteria);

            assertNotNull(wsWhereItem);
            WSWhereAnd whereAnd = wsWhereItem.getWhereAnd();
            assertNotNull(whereAnd);
            assertEquals(1, whereAnd.getWhereItems().length);
            WSWhereItem whereItem = whereAnd.getWhereItems()[0];
            assertNotNull(whereItem);
            WSWhereCondition condition = whereItem.getWhereCondition();
            assertNotNull(condition);

            assertEquals("MyEntity/id", condition.getLeftPath());
            assertEquals("CONTAINS", condition.getOperator().getValue());
            assertEquals("(test ()", condition.getRightValueOrPath());
            assertEquals("NONE", condition.getStringPredicate().getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testContainsSearch3() {
        String criteria = "(MyEntity/id CONTAINS test\\))";
        try {
            WSWhereItem wsWhereItem = CommonUtil.buildWhereItems(criteria);

            assertNotNull(wsWhereItem);
            WSWhereAnd whereAnd = wsWhereItem.getWhereAnd();
            assertNotNull(whereAnd);
            assertEquals(1, whereAnd.getWhereItems().length);
            WSWhereItem whereItem = whereAnd.getWhereItems()[0];
            assertNotNull(whereItem);
            WSWhereCondition condition = whereItem.getWhereCondition();
            assertNotNull(condition);

            assertEquals("MyEntity/id", condition.getLeftPath());
            assertEquals("CONTAINS", condition.getOperator().getValue());
            assertEquals("test)", condition.getRightValueOrPath());
            assertEquals("NONE", condition.getStringPredicate().getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testBlocks() {
        String criteria = "((MyEntity/id CONTAINS test) AND (MyEntity/id2 EQUALS 1))";
        try {
            WSWhereItem wsWhereItem = CommonUtil.buildWhereItems(criteria);

            assertNotNull(wsWhereItem);
            WSWhereAnd whereAnd = wsWhereItem.getWhereAnd();
            assertNotNull(whereAnd);
            assertEquals(2, whereAnd.getWhereItems().length);
            {
                WSWhereItem whereItem = whereAnd.getWhereItems()[0];
                assertNotNull(whereItem);
                WSWhereAnd nestedAnd = whereItem.getWhereAnd();
                assertNotNull(nestedAnd);
                WSWhereCondition condition = nestedAnd.getWhereItems()[0].getWhereCondition();
                assertNotNull(condition);

                assertEquals("MyEntity/id", condition.getLeftPath());
                assertEquals("CONTAINS", condition.getOperator().getValue());
                assertEquals("test", condition.getRightValueOrPath());
                assertEquals("NONE", condition.getStringPredicate().getValue());
            }
            {
                WSWhereItem whereItem = whereAnd.getWhereItems()[1];
                assertNotNull(whereItem);
                WSWhereAnd nestedAnd = whereItem.getWhereAnd();
                assertNotNull(nestedAnd);
                WSWhereCondition condition = nestedAnd.getWhereItems()[0].getWhereCondition();
                assertNotNull(condition);

                assertEquals("MyEntity/id2", condition.getLeftPath());
                assertEquals("EQUALS", condition.getOperator().getValue());
                assertEquals("1", condition.getRightValueOrPath());
                assertEquals("NONE", condition.getStringPredicate().getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testBlocks2() {
        String criteria = "((MyEntity/id CONTAINS test\\(\\)\\)) AND (MyEntity/id2 EQUALS 1))";
        try {
            WSWhereItem wsWhereItem = CommonUtil.buildWhereItems(criteria);

            assertNotNull(wsWhereItem);
            WSWhereAnd whereAnd = wsWhereItem.getWhereAnd();
            assertNotNull(whereAnd);
            assertEquals(2, whereAnd.getWhereItems().length);
            {
                WSWhereItem whereItem = whereAnd.getWhereItems()[0];
                assertNotNull(whereItem);
                WSWhereAnd nestedAnd = whereItem.getWhereAnd();
                assertNotNull(nestedAnd);
                WSWhereCondition condition = nestedAnd.getWhereItems()[0].getWhereCondition();
                assertNotNull(condition);

                assertEquals("MyEntity/id", condition.getLeftPath());
                assertEquals("CONTAINS", condition.getOperator().getValue());
                assertEquals("test())", condition.getRightValueOrPath());
                assertEquals("NONE", condition.getStringPredicate().getValue());
            }
            {
                WSWhereItem whereItem = whereAnd.getWhereItems()[1];
                assertNotNull(whereItem);
                WSWhereAnd nestedAnd = whereItem.getWhereAnd();
                assertNotNull(nestedAnd);
                WSWhereCondition condition = nestedAnd.getWhereItems()[0].getWhereCondition();
                assertNotNull(condition);

                assertEquals("MyEntity/id2", condition.getLeftPath());
                assertEquals("EQUALS", condition.getOperator().getValue());
                assertEquals("1", condition.getRightValueOrPath());
                assertEquals("NONE", condition.getStringPredicate().getValue());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void testError() {
        String criteria = "(MyEntity/id CONTAINS test";
        try {
            CommonUtil.buildWhereItems(criteria);
            fail("Exception was expected (unclosed parenthesis");
        } catch (Exception e) {
            // Expected
        }
    }

    public void testError2() {
        String criteria = "(MyEntity/id OEIOD test)";
        try {
            CommonUtil.buildWhereItems(criteria);
            fail("Exception was expected (predicate doesn't exist)");
        } catch (Exception e) {
            // Expected
        }
    }

    public void testError3() {
        String criteria = "()";
        try {
            CommonUtil.buildWhereItems(criteria);
            fail("Exception was expected (no expression)");
        } catch (Exception e) {
            // Expected
        }
    }

    public void testError4() {
        String criteria = "MyEntity/id CONTAINS *";
        try {
            CommonUtil.buildWhereItems(criteria);
        } catch (Exception e) {
            fail();
        }
    }

    public void testError5() {
        String criteria = "blahblah";
        try {
            CommonUtil.buildWhereItems(criteria);
        } catch (ParserException e) {
            // OK
        } catch (Exception e) {
            fail();
        }

    }
}

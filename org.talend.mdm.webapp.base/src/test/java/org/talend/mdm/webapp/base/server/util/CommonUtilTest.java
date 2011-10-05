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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.webapp.base.client.exception.ParserException;
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;
import org.talend.mdm.webapp.base.client.model.Criteria;
import org.talend.mdm.webapp.base.client.model.SimpleCriterion;
import org.talend.mdm.webapp.base.server.i18n.BaseMessagesImpl;

import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;

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

    public void testJoinStrings() {
        List<String> testee = new ArrayList<String>();
        testee.add("NJ01");
        testee.add("#@$.");
        testee.add("\\/.");
        assertEquals("NJ01.#@$..\\/.", CommonUtil.joinStrings(testee, "."));
    }

    public void testBuildWhereItems() throws Exception {
        String inputString = "Agency/Id EQUALS *";
        WSWhereItem wi = CommonUtil.buildWhereItem(inputString);
        assertNotNull(wi);
        WSWhereAnd wa = wi.getWhereAnd();
        assertNotNull(wa);
        WSWhereItem[] wis = wa.getWhereItems();
        assertNotNull(wis);
        assertEquals(1, wis.length);
        WSWhereCondition wcond = wis[0].getWhereCondition();
        assertNotNull(wcond);
        assertEquals("Agency/Id", wcond.getLeftPath());
        assertEquals("*", wcond.getRightValueOrPath());
        assertEquals(WSWhereOperator.EQUALS, wcond.getOperator());

    }

    public void testBuildWhereItemsByCriteria() throws Exception {
        Criteria input = new SimpleCriterion("Agent/Id", "EQUALS", "*");
        WSWhereItem wi = CommonUtil.buildWhereItemsByCriteria(input);
        assertNotNull(wi);
        WSWhereAnd wa = wi.getWhereAnd();
        assertNotNull(wa);
        WSWhereItem[] wis = wa.getWhereItems();
        assertNotNull(wis);
        assertEquals(1, wis.length);
        WSWhereCondition wcond = wis[0].getWhereCondition();
        assertNotNull(wcond);
        assertEquals("Agent/Id", wcond.getLeftPath());
        assertEquals("*", wcond.getRightValueOrPath());
        assertEquals(WSWhereOperator.EQUALS, wcond.getOperator());

        // try to search one not existing
        Criteria input2 = new SimpleCriterion("Agentsss../Id", "EQUALS", "*");
        WSWhereItem wi2 = CommonUtil.buildWhereItemsByCriteria(input2);
        assertNotNull(wi2);
        WSWhereAnd wa2 = wi2.getWhereAnd();
        assertNotNull(wa2);
        WSWhereItem[] wis2 = wa2.getWhereItems();
        assertNotNull(wis2);
        assertEquals(1, wis2.length);
        WSWhereCondition wcond2 = wis2[0].getWhereCondition();
        assertNotNull(wcond2);
        assertEquals("Agentsss../Id", wcond2.getLeftPath());
        assertEquals("*", wcond2.getRightValueOrPath());
        assertEquals(WSWhereOperator.EQUALS, wcond2.getOperator());
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

    public void testContainsSearch3() throws Exception {
        String criteria = "(MyEntity/id CONTAINS test\\))";
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
    }

    public void testBlocks() throws Exception {
        String criteria = "((MyEntity/id CONTAINS test) AND (MyEntity/id2 EQUALS 1))";
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
    }

    public void testBlocks2() throws Exception {
        String criteria = "((MyEntity/id CONTAINS test\\(\\)\\)) AND (MyEntity/id2 EQUALS 1))";
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

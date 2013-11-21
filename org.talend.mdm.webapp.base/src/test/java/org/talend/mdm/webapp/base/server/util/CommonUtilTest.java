// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import com.amalto.webapp.util.webservices.WSWhereOr;

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
        WSWhereOr wa = wi.getWhereOr();
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
        WSWhereOr wa = wi.getWhereOr();
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
        WSWhereOr wa2 = wi2.getWhereOr();
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
            WSWhereOr whereAnd = wsWhereItem.getWhereOr();
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
            WSWhereOr whereAnd = wsWhereItem.getWhereOr();
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
        WSWhereOr whereAnd = wsWhereItem.getWhereOr();
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
            WSWhereOr nestedAnd = whereItem.getWhereOr();
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
            WSWhereOr nestedAnd = whereItem.getWhereOr();
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
            WSWhereOr nestedAnd = whereItem.getWhereOr();
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
            WSWhereOr nestedAnd = whereItem.getWhereOr();
            assertNotNull(nestedAnd);
            WSWhereCondition condition = nestedAnd.getWhereItems()[0].getWhereCondition();
            assertNotNull(condition);

            assertEquals("MyEntity/id2", condition.getLeftPath());
            assertEquals("EQUALS", condition.getOperator().getValue());
            assertEquals("1", condition.getRightValueOrPath());
            assertEquals("NONE", condition.getStringPredicate().getValue());
        }
    }

    public void testSlashes() throws Exception {
        String criteria = "(MyEntity/id CONTAINS H/F Sundbyvester)";
        WSWhereItem wsWhereItem = CommonUtil.buildWhereItems(criteria);

        assertNotNull(wsWhereItem);
        WSWhereOr whereAnd = wsWhereItem.getWhereOr();
        assertNotNull(whereAnd);
        assertEquals(1, whereAnd.getWhereItems().length);
        {
            WSWhereItem whereItem = whereAnd.getWhereItems()[0];
            assertNotNull(whereItem);
            WSWhereCondition condition = whereItem.getWhereCondition();
            assertNotNull(condition);

            assertEquals("MyEntity/id", condition.getLeftPath());
            assertEquals("CONTAINS", condition.getOperator().getValue());
            // assertEquals("H/F Sundbyvester", condition.getRightValueOrPath());
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

    public void testBuildCriteriaByIds() {
        String[] keys = null;
        String[] ids = null;
        assertNull(CommonUtil.buildCriteriaByIds(keys, ids));

        keys = new String[1];
        ids = new String[1];
        keys[0] = "Product/Id";
        ids[0] = "10001";
        String criteria = CommonUtil.buildCriteriaByIds(keys, ids);
        assertEquals("Product/Id EQUALS 10001", criteria);

        keys = new String[2];
        ids = new String[2];
        keys[0] = "Product/Id";
        keys[1] = "Product/Name";
        ids[0] = "10001";
        ids[1] = "Test";
        criteria = CommonUtil.buildCriteriaByIds(keys, ids);
        assertEquals("((Product/Id EQUALS 10001) AND (Product/Name EQUALS Test))", criteria);
    }

    public void testExtractFKRefValue() {
        String ids = "[1][2]";
        String[] id = CommonUtil.extractFKRefValue(ids, "en");
        assertEquals("1", id[0]);
        assertEquals("2", id[1]);
    }

    public void testSplitString() {
        String s = "Small;Medium;Large";
        List<String> result = CommonUtil.splitString(s, ';');
        assertEquals(3, result.size());
        assertTrue(result.contains("Small"));
        assertTrue(result.contains("Medium"));
        assertTrue(result.contains("Large"));
        result = CommonUtil.splitString(s, '|');
        assertEquals(1, result.size());
        assertEquals(s, result.get(0));

        s = "Small|Medium|Large";
        result = CommonUtil.splitString(s, '|');
        assertEquals(3, result.size());
        assertTrue(result.contains("Small"));
        assertTrue(result.contains("Medium"));
        assertTrue(result.contains("Large"));
        result = CommonUtil.splitString(s, ';');
        assertEquals(1, result.size());
        assertEquals(s, result.get(0));
    }
}

/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query;

import com.amalto.core.query.user.*;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;

import java.util.Arrays;
import java.util.List;

import static com.amalto.core.query.user.UserQueryBuilder.*;

/**
 *
 */
public class UserQueryTest extends StorageTestCase {

    public void testArguments() throws Exception {
        // FROM
        try {
            from(null);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // ORDER BY
        try {
            from(product).orderBy(null, OrderBy.Direction.ASC);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // START
        try {
            from(product).start(-1);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }


        // WHERE
        try {
            from(product).where(null);
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            from(product).where(eq(((TypedExpression) null), null));
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testSelect() throws Exception {
        // Query
        UserQueryBuilder qb = from(product);

        Select select = qb.getSelect();
        assertNotNull(select);
        assertEquals(product, select.getTypes().get(0));
        assertNull(select.getOrderBy());
        assertNull(select.getCondition());
        assertEquals(0, select.getPaging().getStart());
        assertEquals(Integer.MAX_VALUE, select.getPaging().getLimit());
        assertTrue(select.getJoins().isEmpty());
        assertEquals("1", select.getRevisionId());
    }

    public void testOrderBy() throws Exception {
        // Query
        UserQueryBuilder qb = from(product).orderBy(product.getField("Id"), OrderBy.Direction.ASC);

        Select select = qb.getSelect();
        assertNotNull(select);
        assertEquals(product, select.getTypes().get(0));

        assertNotNull(select.getOrderBy());
        assertEquals("Id", select.getOrderBy().getField().getFieldMetadata().getName());

        assertNull(select.getCondition());
        assertEquals(0, select.getPaging().getStart());
        assertEquals(Integer.MAX_VALUE, select.getPaging().getLimit());
        assertTrue(select.getJoins().isEmpty());
        assertEquals("1", select.getRevisionId());
    }

    public void testPaging() throws Exception {
        // Query
        UserQueryBuilder qb = from(product)
                .start(10)
                .limit(50);

        Select select = qb.getSelect();
        assertNotNull(select);
        assertEquals(product, select.getTypes().get(0));
        assertNull(select.getOrderBy());
        assertNull(select.getCondition());
        assertEquals(10, select.getPaging().getStart());
        assertEquals(50, select.getPaging().getLimit());
        assertTrue(select.getJoins().isEmpty());
        assertEquals("1", select.getRevisionId());
    }

    public void testCondition() throws Exception {
        // Query
        UserQueryBuilder qb = from(product)
                .where(eq(product.getField("Id"), "test"));

        Select select = qb.getSelect();
        assertNotNull(select);
        assertEquals(product, select.getTypes().get(0));
        assertNull(select.getOrderBy());

        assertNotNull(select.getCondition());
        Compare condition = (Compare) select.getCondition();
        assertEquals(Predicate.EQUALS, condition.getPredicate());
        assertEquals("test", ((StringConstant) condition.getRight()).getValue());
        assertEquals("Id", ((Field) condition.getLeft()).getFieldMetadata().getName());

        assertEquals(0, select.getPaging().getStart());
        assertEquals(Integer.MAX_VALUE, select.getPaging().getLimit());
        assertTrue(select.getJoins().isEmpty());
        assertEquals("1", select.getRevisionId());
    }

    public void testCount() throws Exception {
        // Query
        UserQueryBuilder qb = from(product)
                .select(count())
                .where(eq(product.getField("Id"), "test"));

        Select select = qb.getSelect();
        assertNotNull(select);
        assertEquals(product, select.getTypes().get(0));
        assertNull(select.getOrderBy());

        assertNotNull(select.getCondition());
        Compare condition = (Compare) select.getCondition();
        assertEquals(Predicate.EQUALS, condition.getPredicate());
        assertEquals("test", ((StringConstant) condition.getRight()).getValue());
        assertEquals("Id", ((Field) condition.getLeft()).getFieldMetadata().getName());

        assertEquals(0, select.getPaging().getStart());
        assertEquals(Integer.MAX_VALUE, select.getPaging().getLimit());
        assertTrue(select.getJoins().isEmpty());
        assertEquals("1", select.getRevisionId());
    }

    public void testFromWhereItems() throws Exception {
        // Query
        List<IWhereItem> items = Arrays.<IWhereItem>asList(
                new WhereCondition("Product/Name", WhereCondition.CONTAINS, "test", WhereCondition.PRE_NONE),
                new WhereCondition("Product/Id", WhereCondition.CONTAINS, "0", WhereCondition.PRE_NONE));
        IWhereItem whereItem = new WhereAnd(items);

        UserQueryBuilder qb = from(product);
        qb.where(UserQueryHelper.buildCondition(qb, whereItem, repository));
        Select select = qb.getSelect();

        assertEquals(product, select.getTypes().get(0));
        Condition condition = select.getCondition();
        assertTrue(condition instanceof BinaryLogicOperator);
        BinaryLogicOperator logicOperator = (BinaryLogicOperator) condition;
        Predicate predicate = logicOperator.getPredicate();
        assertEquals(Predicate.AND, predicate);
        assertTrue(logicOperator.getLeft() instanceof Compare);
        assertTrue(logicOperator.getRight() instanceof Compare);

        // Query
        items = Arrays.<IWhereItem>asList(new WhereCondition("Product/ShortDescription", WhereCondition.CONTAINS, "test", WhereCondition.PRE_NONE));
        whereItem = new WhereAnd(items);

        qb = from(product);
        qb.where(UserQueryHelper.buildCondition(qb, whereItem, repository));
        select = qb.getSelect();

        assertEquals(product, select.getTypes().get(0));
        condition = select.getCondition();
        assertTrue(condition instanceof Compare);

        // Query
        items = Arrays.<IWhereItem>asList(new WhereCondition("Product/LongDescription", WhereCondition.EQUALS, "test", WhereCondition.PRE_NONE));
        whereItem = new WhereAnd(items);

        qb = from(product);
        qb.where(UserQueryHelper.buildCondition(qb, whereItem, repository));
        select = qb.getSelect();

        assertEquals(product, select.getTypes().get(0));
        condition = select.getCondition();
        assertTrue(condition instanceof Compare);
    }

    public void testJoinFail() throws Exception {
        // Make a join that does not fail to ensure join is correctly working.
        from(supplier).join(product.getField("Family"), productFamily.getField("Id"));

        try {
            from(supplier)
                    .join(supplier.getField("Address"), address.getField("Id"));
            fail("Address is a composite id. Cannot join on only one field");
        } catch (Exception e) {
            // Expected
        }

        // This one is expected to succeed
        from(supplier).join(supplier.getField("Address"));
    }
}

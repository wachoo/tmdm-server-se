// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.journal.sever.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.webapp.base.server.util.Constants;
import org.talend.mdm.webapp.journal.server.util.Util;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;

import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSGetItemsSort;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.extjs.gxt.ui.client.Style.SortDir;

/**
 * created by talend2 on 2013-1-29 Detailled comment
 * 
 */
@SuppressWarnings("nls")
public class UtilTest extends TestCase {
    
    private JournalSearchCriteria criteria;   

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        criteria = new JournalSearchCriteria();
        criteria.setEntity("TestModel"); //$NON-NLS-1$
        criteria.setStartDate(new Date()); //$NON-NLS-1$
        criteria.setKey("1"); //$NON-NLS-1$
        criteria.setOperationType(UpdateReportPOJO.OPERATION_TYPE_CREATE);
        criteria.setSource("genericUI"); //$NON-NLS-1$
        criteria.setStrict(true);
    }


    public void testBuildWhereItems() throws Exception {

        List<WSWhereItem> conditions = Util.buildWhereItems(criteria);
        for (WSWhereItem whereItem : conditions) {
            WSWhereCondition condition = whereItem.getWhereCondition();
            if ("Concept".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(WSWhereOperator.EQUALS,condition.getOperator()); //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "TestModel"); //$NON-NLS-1$
            } else if ("Key".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(WSWhereOperator.EQUALS,condition.getOperator()); //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "1"); //$NON-NLS-1$
            } else if ("Source".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(WSWhereOperator.EQUALS,condition.getOperator()); //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "genericUI"); //$NON-NLS-1$
            } else if ("OperationType".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), UpdateReportPOJO.OPERATION_TYPE_CREATE);
            } else if ("TimeInMillis".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(WSWhereOperator.GREATER_THAN_OR_EQUAL, condition.getOperator());
            }
        }
        
        criteria.setStrict(false);
        conditions = Util.buildWhereItems(criteria);
        for (WSWhereItem whereItem : conditions) {
            WSWhereCondition condition = whereItem.getWhereCondition();
            if ("Concept".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(WSWhereOperator.CONTAINS,condition.getOperator()); //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "TestModel"); //$NON-NLS-1$
            } else if ("Key".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(WSWhereOperator.CONTAINS,condition.getOperator()); //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "1"); //$NON-NLS-1$
            } else if ("Source".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(WSWhereOperator.CONTAINS,condition.getOperator()); //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "genericUI"); //$NON-NLS-1$
            } else if ("OperationType".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), UpdateReportPOJO.OPERATION_TYPE_CREATE);
            } else if ("TimeInMillis".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(WSWhereOperator.GREATER_THAN_OR_EQUAL, condition.getOperator());
            }
        }        
    }
    
    public void testBuildGetItem() {
        List<WSWhereItem> conditions = Util.buildWhereItems(criteria);
        WSGetItems item = Util.buildGetItem(conditions, 0, 20);
        assertEquals("Update", item.getConceptName());
        assertNotNull(item.getWhereItem());
        assertEquals(true, item.getTotalCountOnFirstResult().booleanValue());
        assertEquals(0, item.getSkip());
        assertEquals(20, item.getMaxItems());
    }
    
    public void testBuildGetItemsSort() {
        List<WSWhereItem> conditions = Util.buildWhereItems(criteria);
        String sort = "ASC";
        if (SortDir.ASC.equals(SortDir.findDir(sort))) {
            sort = Constants.SEARCH_DIRECTION_ASC;
        } else if (SortDir.DESC.equals(SortDir.findDir(sort))) {
            sort = Constants.SEARCH_DIRECTION_DESC;
        }
        assertEquals(Constants.SEARCH_DIRECTION_ASC, sort);
        WSGetItemsSort itemSort = Util.buildGetItemsSort(conditions, 0, 20, sort, "key");
        assertNotNull(itemSort.getConceptName());
        assertEquals(true, itemSort.getTotalCountOnFirstResult().booleanValue());
        assertEquals(0, itemSort.getSkip());
        assertEquals(20, itemSort.getMaxItems());
        assertEquals(Constants.SEARCH_DIRECTION_ASC,itemSort.getSort());
        assertEquals("Update/Key",itemSort.getDir());
    }
    
    public void testGetOrderXPath() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method method = Util.class.getDeclaredMethod("getOrderXPath", String.class); //$NON-NLS-1$
        method.setAccessible(true);
        Object returnValue = method.invoke(null, new Object[] { "dataContainer" }); //$NON-NLS-1$ 
        assertEquals("Update/DataCluster", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "dataModel" }); //$NON-NLS-1$ 
        assertEquals("Update/DataModel", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "entity" }); //$NON-NLS-1$ 
        assertEquals("Update/Concept", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "key" }); //$NON-NLS-1$ 
        assertEquals("Update/Key", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "revisionId" }); //$NON-NLS-1$ 
        assertEquals("Update/RevisionID", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "operationType" }); //$NON-NLS-1$ 
        assertEquals("Update/OperationType", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "operationTime" }); //$NON-NLS-1$ 
        assertEquals("Update/TimeInMillis", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "source" }); //$NON-NLS-1$ 
        assertEquals("Update/Source", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "userName" }); //$NON-NLS-1$ 
        assertEquals("Update/UserName", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "otherField1" }); //$NON-NLS-1$ 
        assertEquals("Update/otherField1", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "otherField2" }); //$NON-NLS-1$ 
        assertEquals("Update/otherField2", returnValue); //$NON-NLS-1$
        returnValue = method.invoke(null, new Object[] { "" }); //$NON-NLS-1$
        assertNull(returnValue);
        returnValue = method.invoke(null, new Object[] { null }); //$NON-NLS-1$
        assertNull(returnValue);
    }
    
}

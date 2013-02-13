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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.webapp.journal.server.util.Util;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;

import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;

/**
 * created by talend2 on 2013-1-29 Detailled comment
 * 
 */
@SuppressWarnings("nls")
public class UtilTest extends TestCase {

    public void testBuildWhereItems() throws Exception {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JournalSearchCriteria criteria = new JournalSearchCriteria();
        criteria.setEntity("TestModel"); //$NON-NLS-1$
        criteria.setStartDate(dateFormat.parse("2012-07-01")); //$NON-NLS-1$
        criteria.setEndDate(dateFormat.parse("2012-09-30")); //$NON-NLS-1$
        criteria.setKey("1"); //$NON-NLS-1$
        criteria.setOperationType("CREATE"); //$NON-NLS-1$
        criteria.setSource("genericUI"); //$NON-NLS-1$

        List<WSWhereItem> conditions = Util.buildWhereItems(criteria, true);

        for (WSWhereItem whereItem : conditions) {
            WSWhereCondition condition = whereItem.getWhereCondition();
            if ("Concept".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "TestModel"); //$NON-NLS-1$
            } else if ("Key".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "1"); //$NON-NLS-1$
            } else if ("Source".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "genericUI"); //$NON-NLS-1$
            } else if ("OperationType".equals(condition.getLeftPath())) { //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), "CREATE"); //$NON-NLS-1$
            } else if ("TimeInMillis".equals(condition.getLeftPath()) && WSWhereOperator.GREATER_THAN_OR_EQUAL.equals(condition.getOperator())) { //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), String.valueOf(dateFormat.parse("2012-7-1").getTime())); //$NON-NLS-1$
            } else if ("TimeInMillis".equals(condition.getLeftPath()) && WSWhereOperator.LOWER_THAN.equals(condition.getOperator())) { //$NON-NLS-1$
                assertEquals(condition.getRightValueOrPath(), String.valueOf(dateFormat.parse("2012-10-1").getTime())); //$NON-NLS-1$
            } else {
                assertFalse(true);
            }

        }
    }

}

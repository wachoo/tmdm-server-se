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
package org.talend.mdm.webapp.shared.util;

import org.talend.mdm.webapp.base.shared.util.CommonUtil;

import junit.framework.TestCase;


/**
 * created by talend2 on 2013-8-1
 * Detailled comment
 *
 */
public class CommonUtilTest extends TestCase {
    
    public void testEscape() {
        String target = "12.23@34$45%56^67&78*89("; //$NON-NLS-1$
        assertEquals("12%2e23%4034%2445%2556%5e67%2678%2a89%28", CommonUtil.escape(target)); //$NON-NLS-1$
        target = "abc.dbc.dgc"; //$NON-NLS-1$
        assertEquals("abc%2edbc%2edgc", CommonUtil.escape(target)); //$NON-NLS-1$
        target = "db/.dfe/cdfser.dfsdf"; //$NON-NLS-1$
        assertEquals("db%2f%2edfe%2fcdfser%2edfsdf", CommonUtil.escape(target)); //$NON-NLS-1$
    }
    
    public void testUnescape() {
        String target = "12%2e23%4034%2445%2556%5e67%2678%2a89%28"; //$NON-NLS-1$
        assertEquals("12.23@34$45%56^67&78*89(", CommonUtil.unescape(target)); //$NON-NLS-1$
        target = "abc%2edbc%2edgc"; //$NON-NLS-1$
        assertEquals("abc.dbc.dgc", CommonUtil.unescape(target)); //$NON-NLS-1$
        target = "db%2f%2edfe%2fcdfser%2edfsdf"; //$NON-NLS-1$
        assertEquals("db/.dfe/cdfser.dfsdf", CommonUtil.unescape(target)); //$NON-NLS-1$
    }

}

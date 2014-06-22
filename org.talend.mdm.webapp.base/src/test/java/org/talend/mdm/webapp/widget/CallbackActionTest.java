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
package org.talend.mdm.webapp.widget;

import org.talend.mdm.webapp.base.client.widget.Callback;
import org.talend.mdm.webapp.base.client.widget.CallbackAction;

import junit.framework.TestCase;


public class CallbackActionTest extends TestCase {
    
    boolean status = false;
    final String result = "successful"; //$NON-NLS-1$
    
    public void testDoAction(){
        CallbackAction.getInstance().putAction("testAcion", new Callback() { //$NON-NLS-1$

            public void doAction(Object value,String concept,Boolean isClose) { 
                assertEquals(value, result);
                action();
            }
        });   
        CallbackAction.getInstance().doAction("testAcion", result,"Prodcut",false); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(status);        
    }

    public void action(){
        status = true;
    }
}

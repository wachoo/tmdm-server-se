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
package org.talend.mdm.webapp.base.client;

import org.talend.mdm.webapp.base.client.exception.ServiceException;

import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("nls")
public class SessionAwareAsyncCallbackGWTTest extends GWTTestCase {

    public void testOnFailure() {
        final String flag = "failed";
        new SessionAwareAsyncCallback<Object>() {

            @Override
            public void onSuccess(Object arg0) {
                               
            }

            @Override
            protected void doOnFailure(Throwable caught) {
                assertNotNull(caught);
                assertEquals(flag, caught.getMessage());
            }
            
        }.doOnFailure(new ServiceException(flag));
    }
    
    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.base.TestBase";
    }

}

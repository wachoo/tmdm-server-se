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
package org.talend.mdm.webapp.itemsbrowser2.server.util;

import org.talend.mdm.webapp.itemsbrowser2.server.ItemsBrowserConfiguration;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeData;

import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.XtentisPort;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class CommonUtil {
    
    /**
     * DOC HSHU Comment method "getPort".
     * @return
     * @throws XtentisWebappException
     */
    public static XtentisPort getPort() throws XtentisWebappException {
        if (!ItemsBrowserConfiguration.isStandalone()) {
            return com.amalto.webapp.core.util.Util.getPort();
        } else {
            return com.amalto.webapp.core.util.Util.getPort(
                    FakeData.MDM_DEFAULT_ENDPOINTADDRESS,
                    FakeData.MDM_DEFAULT_USERNAME,
                    FakeData.MDM_DEFAULT_PASSWORD,
                    com.amalto.webapp.core.util.Util._FORCE_WEB_SERVICE_);
        }
    }
    
    public static boolean isEmpty(String s){
        if (s == null)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }
}

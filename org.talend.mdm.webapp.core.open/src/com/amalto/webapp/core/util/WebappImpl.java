// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util;

import java.util.Map;

import com.amalto.webapp.core.util.dwr.WebappInfo;

public class WebappImpl implements Webapp {

    public void getInfo(WebappInfo info, String language) {
        return;
    }
    
    public Map<String, String> getProductInfo() {
        return null;
    }

    public int getTaskMsg() {
        return 0;
    }

    public boolean isExpired() throws Exception {
        return isExpired(null);
    }

    public boolean isExpired(String language) throws Exception {
        return false;
    }

    public boolean isShowMsg() throws Exception {
        return false;
    }

    public boolean isDataSteWardShip() throws Exception {
        return false;
    }

    public boolean isEnterpriseVersion() {
        return false;
    }
}

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
package com.amalto.webapp.core.util;

import java.util.Map;

import com.amalto.webapp.core.util.dwr.WebappInfo;

public class WebappImpl implements Webapp {

    @Override
    public void getInfo(WebappInfo info, String language) {
        return;
    }

    @Override
    public Map<String, String> getProductInfo() {
        return null;
    }

    @Override
    public int getWorkflowTaskMsg() {
        return 0;
    }

    @Override
    public boolean isExpired() throws Exception {
        return isExpired(null);
    }

    @Override
    public boolean isExpired(String language) throws Exception {
        return false;
    }

    @Override
    public boolean isShowMsg() throws Exception {
        return false;
    }

    @Override
    public boolean isDataSteWardShip() throws Exception {
        return false;
    }

    @Override
    public boolean isEnterpriseVersion() {
        return false;
    }

    @Override
    public String getLicenseWarning(String language) throws Exception {
        return null;
    }
}

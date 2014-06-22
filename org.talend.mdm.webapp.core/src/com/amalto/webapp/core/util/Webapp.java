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

public interface Webapp {

    public void getInfo(WebappInfo info, String language);

    public Map<String, String> getProductInfo();

    public int getTaskMsg();

    public boolean isExpired() throws Exception;

    public boolean isExpired(String language) throws Exception;

    public boolean isShowMsg() throws Exception;

    public boolean isDataSteWardShip() throws Exception;

    public boolean isEnterpriseVersion();

    public static final Webapp INSTANCE = WebappFactory.createWebapp();

    public static final class WebappFactory {

        private WebappFactory() {
        }

        private static Webapp createWebapp() {
            try {
                return (Webapp) Class.forName(Webapp.class.getName() + "Impl").newInstance(); //$NON-NLS-1$
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

}
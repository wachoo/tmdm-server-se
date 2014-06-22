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
package org.talend.mdm.webapp.general.gwt;

import javax.servlet.http.HttpSession;

import com.amalto.webapp.core.bean.Configuration.ConfigurationContext;

public class GWTConfigurationContext implements ConfigurationContext {

    public HttpSession getSession() {
        HttpSession session;
        GwtWebContext ctx = GwtWebContextFactory.get();
        if (ctx != null) {
            session = ctx.getSession();
        } else {
            session = null;
        }
        return session;
    }
}

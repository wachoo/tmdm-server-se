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
package com.amalto.core.util;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jboss.system.ServiceMBeanSupport;

public class JULLog4jService extends ServiceMBeanSupport implements JULLog4jServiceMBean {

    private boolean removeExistingHandlers = true;

    @Override
    protected void startService() throws Exception {
        Logger rootLogger = LogManager.getLogManager().getLogger(""); //$NON-NLS-1$
        int log4jLevel = org.apache.log4j.LogManager.getRootLogger().getLevel().toInt();
        JULLog4jHandler.JULToLog4j(rootLogger, log4jLevel, removeExistingHandlers);
    }

    @Override
    public boolean isRemoveExistingHandlers() {
        return removeExistingHandlers;
    }

    @Override
    public void setRemoveExistingHandlers(boolean removeExistingHandlers) {
        this.removeExistingHandlers = removeExistingHandlers;
    }

}

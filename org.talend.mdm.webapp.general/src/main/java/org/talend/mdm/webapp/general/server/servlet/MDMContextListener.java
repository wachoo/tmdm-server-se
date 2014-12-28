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
package org.talend.mdm.webapp.general.server.servlet;

import java.io.File;
import java.io.FileNotFoundException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.util.ResourceUtils;
import org.springframework.web.util.ServletContextPropertyUtils;
import org.springframework.web.util.WebUtils;

public class MDMContextListener implements ServletContextListener {

    public static final String ROOT_LOCATION_PARAM = "mdmRootLocation";

    public static final String ROOT_LOCATION_KEY = "mdm.root";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        String location = servletContext.getInitParameter(ROOT_LOCATION_PARAM);
        String resolvedLocation = ServletContextPropertyUtils.resolvePlaceholders(location, servletContext);
        servletContext.log("Initializing MDM root folder from [" + resolvedLocation + "]");
        try {
            if (!ResourceUtils.isUrl(resolvedLocation)) {
                resolvedLocation = WebUtils.getRealPath(servletContext, resolvedLocation);
            }
            File file = ResourceUtils.getFile(resolvedLocation);
            if (!file.exists()) {
                throw new FileNotFoundException("MDM Root folder [" + resolvedLocation + "] not found");
            }
            System.setProperty(ROOT_LOCATION_KEY, file.getAbsolutePath());
            servletContext.log("Set MDM root system property: '" + ROOT_LOCATION_KEY + "' = [" + file.getAbsolutePath() + "]");
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Invalid '" + ROOT_LOCATION_PARAM + "' parameter", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.getProperties().remove(ROOT_LOCATION_KEY);
    }

}

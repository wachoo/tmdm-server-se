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
package com.amalto.core.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.amalto.core.objects.Plugin;
import com.amalto.core.objects.Service;

public class PluginRegistry {

    public static final Logger LOGGER = Logger.getLogger(PluginRegistry.class);

    private static PluginRegistry instance;

    private PluginFactory pluginFactory;

    @Autowired
    private ListableBeanFactory listableBeanFactory;

    private PluginRegistry() {
    }

    public static synchronized PluginRegistry createInstance() {
        if (instance != null) {
            throw new IllegalStateException();
        }
        instance = new PluginRegistry();
        return instance;
    }

    public static synchronized PluginRegistry getInstance() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    public Plugin getPlugin(String pluginName) {
        try {
            return pluginFactory.getPlugin(pluginName);
        } catch (NoSuchBeanDefinitionException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No such plugin '" + pluginName + "'.", e);
            }
            return null;
        }
    }

    public Service getService(String serviceName) {
        try {
            return pluginFactory.getService(serviceName);
        } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("No such service '" + serviceName + "'.", e);
            }
            return null;
        }
    }

    public void setPluginFactory(PluginFactory pluginFactory) {
        this.pluginFactory = pluginFactory;
    }

    public ListableBeanFactory getListableBeanFactory() {
        return this.listableBeanFactory;
    }
}

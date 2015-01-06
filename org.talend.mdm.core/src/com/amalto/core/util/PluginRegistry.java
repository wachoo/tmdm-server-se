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

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.amalto.core.objects.Plugin;

public class PluginRegistry {

    private static PluginRegistry instance;

    private PluginFactory pluginFactory;

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

    private PluginRegistry() {
    }

    public Plugin getPlugin(String pluginName) {
        return pluginFactory.getPlugin(pluginName);
    }

    public void setPluginFactory(PluginFactory pluginFactory) {
        this.pluginFactory = pluginFactory;
    }
}

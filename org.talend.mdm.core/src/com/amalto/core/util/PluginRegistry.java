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

import com.amalto.core.objects.Plugin;
import com.amalto.core.objects.Service;

public class PluginRegistry {

    private static PluginRegistry instance;

    private PluginFactory         pluginFactory;

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
        return pluginFactory.getPlugin(pluginName);
    }

    public Service getService(String serviceName) {
        return pluginFactory.getService(serviceName);
    }

    public void setPluginFactory(PluginFactory pluginFactory) {
        this.pluginFactory = pluginFactory;
    }
}

/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.datasource;

public class DataSourceDefinition {

    private final DataSource master;

    private final DataSource staging;

    private final DataSource system;

    public DataSourceDefinition(DataSource master, DataSource staging, DataSource system) {
        this.master = master;
        this.staging = staging;
        this.system = system;
    }

    public DataSource getMaster() {
        return master;
    }

    public boolean hasStaging() {
        return staging != null;
    }

    public DataSource getStaging() {
        return staging;
    }

    public boolean hasSystem() {
        return system != null;
    }

    public DataSource getSystem() {
        return system;
    }
}

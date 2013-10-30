/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.datasource;

import java.util.Arrays;

public class DataSourceDefinition {

    private final DataSource master;

    private final DataSource staging;

    private final DataSource system;

    public DataSourceDefinition(DataSource master, DataSource staging, DataSource system) {
        this.master = master;
        this.staging = staging;
        this.system = system;
        // Compute isShared status
        DataSource[] dataSources = new DataSource[] {master, staging, system};
        boolean[][] isShared = computeSharedStatus(dataSources);
        if (master != null) {
            master.setShared(isShared(isShared[0]));
        }
        if (staging != null) {
            staging.setShared(isShared(isShared[1]));
        }
        if (system != null) {
            system.setShared(isShared(isShared[2]));
        }
    }

    private static boolean[][] computeSharedStatus(DataSource[] dataSources) {
        boolean isShared[][] = new boolean[dataSources.length][dataSources.length];
        int i = 0;
        for (DataSource dataSource : dataSources) {
            int j = 0;
            if (dataSource == null) {
                Arrays.fill(isShared[i], false);
            } else {
                for (DataSource source : dataSources) {
                    if (i != j) { // Don't count a self equals as a "share".
                        isShared[i][j] = dataSource.equals(source);
                    }
                    j++;
                }
            }
            i++;
        }
        return isShared;
    }

    private boolean isShared(boolean[] booleans) {
        for (boolean isShared : booleans) {
            if (isShared) {
                return true;
            }
        }
        return false;
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

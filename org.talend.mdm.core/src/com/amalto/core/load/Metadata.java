/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load;

import org.apache.commons.lang.StringUtils;

/**
 *
 */
public class Metadata {
    private final String dmr = StringUtils.EMPTY;
    private final String sp = StringUtils.EMPTY;
    private final String taskId = null;

    private String id = StringUtils.EMPTY;
    private String container = StringUtils.EMPTY;
    private String name = StringUtils.EMPTY;
    private String dmn = StringUtils.EMPTY;
    private String dataClusterName;

    public Metadata() {
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDmn(String dmn) {
        this.dmn = dmn;
    }

    public String getId() {
        return id;
    }

    public String getContainer() {
        return container;
    }

    public String getName() {
        return name;
    }

    public String getDMR() {
        return dmr;
    }

    public String getSP() {
        return sp;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getVersion() {
        return String.valueOf(System.currentTimeMillis());
    }

    public String getDMN() {
        return dmn;
    }

    public void setId(String id) {
        if (this.id == null) {
            this.id = id.trim();
        } else {
            this.id += ':' + id.trim();
        }
    }

    public void reset() {
        this.id = null;
    }

    public String getDataClusterName() {
        return dataClusterName;
    }

    public void setDataClusterName(String dataClusterName) {
        this.dataClusterName = dataClusterName;
    }
}

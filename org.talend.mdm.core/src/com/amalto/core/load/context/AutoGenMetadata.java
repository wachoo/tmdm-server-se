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

package com.amalto.core.load.context;

import com.amalto.core.load.Metadata;

/**
 *
 */
class AutoGenMetadata extends Metadata {
    private final Metadata metadata;

    private final String[] idPaths;

    private final AutoIdGenerator generator;

    private String[] autoGenId;

    AutoGenMetadata(Metadata metadata, String[] idPaths, AutoIdGenerator generator) {
        this.metadata = metadata;
        this.idPaths = idPaths;
        this.generator = generator;
    }

    private static String[] generateId(Metadata metadata, String[] idPaths, AutoIdGenerator generator) {
        for (String idPath : idPaths) {
            metadata.setId(idPath, generator.generateId(metadata.getDataClusterName(), metadata.getName(), idPath));
        }
        return metadata.getId();
    }

    @Override
    public String[] getId() {
        if (autoGenId == null) {
            autoGenId = generateId(metadata, idPaths, generator);
        }
        return autoGenId;
    }

    @Override
    public void setId(String idElementName, String id) {
        throw new UnsupportedOperationException("AutoGen id is read-only");
    }

    @Override
    public void setContainer(String container) {
        metadata.setContainer(container);
    }

    @Override
    public void setName(String name) {
        metadata.setName(name);
    }

    @Override
    public void setDmn(String dmn) {
        metadata.setDmn(dmn);
    }

    @Override
    public String getContainer() {
        return metadata.getContainer();
    }

    @Override
    public String getName() {
        return metadata.getName();
    }

    @Override
    public String getDMR() {
        return metadata.getDMR();
    }

    @Override
    public String getTaskId() {
        return metadata.getTaskId();
    }

    @Override
    public String getSP() {
        return metadata.getSP();
    }

    @Override
    public String getVersion() {
        return metadata.getVersion();
    }

    @Override
    public void reset() {
        metadata.reset();
        autoGenId = null;
    }

    @Override
    public String getDMN() {
        return metadata.getDMN();
    }

    @Override
    public String getDataClusterName() {
        return metadata.getDataClusterName();
    }

    @Override
    public void setDataClusterName(String dataClusterName) {
        metadata.setDataClusterName(dataClusterName);
    }
}

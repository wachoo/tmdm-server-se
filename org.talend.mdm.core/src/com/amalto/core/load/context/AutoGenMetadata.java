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

    private final AutoIdGenerator generator;

    AutoGenMetadata(Metadata metadata, AutoIdGenerator generator) {
        this.metadata = metadata;
        this.generator = generator;
    }

    @Override
    public String[] getId() {
        return new String[]{generator.generateAutoId(metadata.getDataClusterName(), metadata.getName())};
    }

    @Override
    public void setId(String id) {
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
    }

    @Override
    public String getDMN() {
        return metadata.getDMN();
    }
}

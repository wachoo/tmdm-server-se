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
package org.talend.mdm.webapp.base.client.rest.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StagingContainerModel implements IsSerializable, Serializable {

    private static final long serialVersionUID = -9135215288938203541L;

    private String dataContainer;

    private String dataModel;

    private int invalidRecords;

    private int totalRecords;

    private int validRecords;

    private int waitingValidationRecords;

    public StagingContainerModel() {
        super();
    }

    public String getDataContainer() {
        return dataContainer;
    }

    public void setDataContainer(String dataContainer) {
        this.dataContainer = dataContainer;
    }

    public String getDataModel() {
        return dataModel;
    }

    public void setDataModel(String dataModel) {
        this.dataModel = dataModel;
    }

    public int getInvalidRecords() {
        return invalidRecords;
    }

    public void setInvalidRecords(int invalidRecords) {
        this.invalidRecords = invalidRecords;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getValidRecords() {
        return validRecords;
    }

    public void setValidRecords(int validRecords) {
        this.validRecords = validRecords;
    }

    public int getWaitingValidationRecords() {
        return waitingValidationRecords;
    }

    public void setWaitingValidationRecords(int waitingValidationRecords) {
        this.waitingValidationRecords = waitingValidationRecords;
    }
}
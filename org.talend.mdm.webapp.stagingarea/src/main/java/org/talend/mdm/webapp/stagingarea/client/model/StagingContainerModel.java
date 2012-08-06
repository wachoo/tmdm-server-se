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
package org.talend.mdm.webapp.stagingarea.client.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public class StagingContainerModel implements IsSerializable, Serializable {

    private static final long serialVersionUID = -9135215288938203541L;

    private String data_container;

    private String data_model;

    private int invalid_records;

    private int total_records;

    private int valid_records;

    private int waiting_validation_records;
    
    public StagingContainerModel() {
        super();
    }

    public String getData_container() {
        return data_container;
    }

    public void setData_container(String data_container) {
        this.data_container = data_container;
    }

    public String getData_model() {
        return data_model;
    }

    public void setData_model(String data_model) {
        this.data_model = data_model;
    }

    public int getInvalid_records() {
        return invalid_records;
    }

    public void setInvalid_records(int invalid_records) {
        this.invalid_records = invalid_records;
    }

    public int getTotal_records() {
        return total_records;
    }

    public void setTotal_records(int total_records) {
        this.total_records = total_records;
    }

    public int getValid_records() {
        return valid_records;
    }

    public void setValid_records(int valid_records) {
        this.valid_records = valid_records;
    }

    public int getWaiting_validation_records() {
        return waiting_validation_records;
    }

    public void setWaiting_validation_records(int waiting_validation_records) {
        this.waiting_validation_records = waiting_validation_records;
    }
}
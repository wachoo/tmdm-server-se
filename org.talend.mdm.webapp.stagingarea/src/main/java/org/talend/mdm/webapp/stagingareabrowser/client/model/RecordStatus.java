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
package org.talend.mdm.webapp.stagingareabrowser.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum RecordStatus implements IsSerializable {
    
    New_Record(000),
    Identify_Success(201),
    Merge_Success(202),
    Validation_Success(203),
    Identify_Fail(401),
    Merge_Fail(402),
    Model_Validation_Fail(403),
    FK_Constrain_Fail(404),
    Unknown(999);
    
    private int statusCode;

    RecordStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    
    public int getStatusCode() {
        return statusCode;
    }

    public static RecordStatus newStatus(int statusCode) {

        switch (statusCode) {
        case 000:
            return New_Record;
        case 201:
            return Identify_Success;
        case 202:
            return Merge_Success;
        case 203:
            return Validation_Success;
        case 401:
            return Identify_Fail;
        case 402:
            return Merge_Fail;
        case 403:
            return Model_Validation_Fail;
        case 404:
            return FK_Constrain_Fail;
        default:
            return Unknown;
        }


    }


}

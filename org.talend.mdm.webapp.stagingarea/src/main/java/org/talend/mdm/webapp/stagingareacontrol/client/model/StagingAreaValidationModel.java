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
package org.talend.mdm.webapp.stagingareacontrol.client.model;

import java.io.Serializable;
import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

public class StagingAreaValidationModel extends BaseModelData implements IsSerializable, Serializable {

    private static final long serialVersionUID = 3782403942844388546L;

    public StagingAreaValidationModel() {

    }

    public String getId() {
        return get("id"); //$NON-NLS-1$
    }

    public void setId(String id) {
        set("id", id); //$NON-NLS-1$
    }

    public Date getStartDate() {
        return get("start_date"); //$NON-NLS-1$
    }

    public void setStartDate(Date startDate) {
        set("start_date", startDate); //$NON-NLS-1$
    }

    public int getProcessedRecords() {
        return get("processed_records") == null ? 0 : ((Integer) get("processed_records")).intValue(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void setProcessedRecords(int processedRecords) {
        set("processed_records", processedRecords); //$NON-NLS-1$
    }

    public int getInvalidRecords() {
        return get("invalid_records") == null ? 0 : ((Integer) get("invalid_records")).intValue(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void setInvalidRecords(int invalidRecords) {
        set("invalid_records", invalidRecords); //$NON-NLS-1$
    }

    public int getTotalRecord() {
        return get("total_record") == null ? 0 : ((Integer) get("total_record")).intValue(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void setTotalRecord(int totalRecord) {
        set("total_record", totalRecord); //$NON-NLS-1$
    }

}

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

import java.util.Date;

public class StagingAreaExecutionModel extends StagingAreaValidationModel {

    private static final long serialVersionUID = -123265293808187925L;

    public StagingAreaExecutionModel() {

    }

    public Date getEndDate() {
        return get("end_date"); //$NON-NLS-1$
    }

    public void setEndDate(Date endDate) {
        set("end_date", endDate); //$NON-NLS-1$
    }

}

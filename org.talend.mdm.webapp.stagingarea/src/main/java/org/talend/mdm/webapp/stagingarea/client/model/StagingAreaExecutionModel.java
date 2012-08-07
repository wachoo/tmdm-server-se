package org.talend.mdm.webapp.stagingarea.client.model;

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

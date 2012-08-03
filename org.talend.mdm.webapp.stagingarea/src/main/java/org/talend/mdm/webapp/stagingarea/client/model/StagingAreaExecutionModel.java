package org.talend.mdm.webapp.stagingarea.client.model;

import java.io.Serializable;
import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;


public class StagingAreaExecutionModel extends BaseModelData implements IsSerializable, Serializable {

    private static final long serialVersionUID = 3782403942844388546L;

    public StagingAreaExecutionModel() {

    }

    public StagingAreaExecutionModel(String id, Date start_date, Date end_date, Integer performance, Integer processed_records,
            Integer record_left, Date elapsed_time) {
        this();
    }

    public String getId() {
        return get("id"); //$NON-NLS-1$
    }

    public void setId(String id) {
        set("id", id); //$NON-NLS-1$
    }

    public Date getStart_date() {
        return get("start_date"); //$NON-NLS-1$
    }

    public void setStart_date(Date startDate) {
        set("start_date", startDate); //$NON-NLS-1$
    }

    public Date getEnd_date() {
        return get("end_date"); //$NON-NLS-1$
    }

    public void setEnd_date(Date endDate) {
        set("end_date", endDate); //$NON-NLS-1$
    }

    public Integer getPerformance() {
        return get("performance"); //$NON-NLS-1$
    }

    public void setPerformance(Integer performance) {
        set("performance", performance); //$NON-NLS-1$
    }

    public Integer getProcessed_records() {
        return get("processed_records"); //$NON-NLS-1$
    }

    public void setProcessed_records(Integer processedRecords) {
        set("processed_records", processedRecords); //$NON-NLS-1$
    }

    public Integer getRecord_left() {
        return get("record_left"); //$NON-NLS-1$
    }

    public void setRecord_left(Integer recordLeft) {
        set("record_left", recordLeft); //$NON-NLS-1$
    }

    public Date getElapsed_time() {
        return get("elapsed_time"); //$NON-NLS-1$
    }

    public void setElapsed_time(Date elapsedTime) {
        set("elapsed_time", elapsedTime); //$NON-NLS-1$
    }


}

/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.task.staging;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "task")
public class TaskStatus {

    private String id;

    private String dataContainer;

    private String executionType;

    private String timeInterval;

    private Date nextRunDate;

    public TaskStatus() {
    }

    public TaskStatus(String id, String dataContainer, String executionType, String timeInterval, Date nextRunDate) {
        this.id = id;
        this.dataContainer = dataContainer;
        this.executionType = executionType;
        this.timeInterval = timeInterval;
        this.nextRunDate = nextRunDate;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return "STAGING";
    }

    @XmlElement(name = "data_container")
    public String getDataContainer() {
        return dataContainer;
    }

    @XmlElement(name = "execution_type")
    public String getExecutionType() {
        return executionType;
    }

    @XmlElement(name = "time_interval")
    public String getTimeInterval() {
        return timeInterval;
    }

    @XmlElement(name = "next_run_date")
    public Date getNextRunDate() {
        return nextRunDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDataContainer(String dataContainer) {
        this.dataContainer = dataContainer;
    }

    public void setExecutionType(String executionType) {
        this.executionType = executionType;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void setNextRunDate(Date nextRunDate) {
        this.nextRunDate = nextRunDate;
    }
}

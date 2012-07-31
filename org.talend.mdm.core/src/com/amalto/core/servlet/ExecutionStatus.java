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

package com.amalto.core.servlet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "execution")
public class ExecutionStatus {

    private String id;

    private int processedRecords;

    private String startDate;

    private String endDate;
    private String runningTime;

    private int recordLeft;

    private double performance;

    public ExecutionStatus() {
    }

    public ExecutionStatus(String id, int processedRecords, String startDate, String endDate, String runningTime, int recordLeft, double performance) {
        this.id = id;
        this.processedRecords = processedRecords;
        this.startDate = startDate;
        this.endDate = endDate;
        this.runningTime = runningTime;
        this.recordLeft = recordLeft;
        this.performance = performance;
    }

    public ExecutionStatus(String id) {
        this.id = id;
    }

    @XmlElement(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name = "processed_records")
    public int getProcessedRecords() {
        return processedRecords;
    }

    public void setProcessedRecords(int processedRecords) {
        this.processedRecords = processedRecords;
    }

    @XmlElement(name = "start_date")
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @XmlElement(name = "end_date")
    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @XmlElement(name = "elapsed_time")
    public String getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(String runningTime) {
        this.runningTime = runningTime;
    }

    @XmlElement(name = "record_left")
    public int getRecordLeft() {
        return recordLeft;
    }

    public void setRecordLeft(int recordLeft) {
        this.recordLeft = recordLeft;
    }

    @XmlElement(name = "performance")
    public double getPerformance() {
        return performance;
    }

    public void setPerformance(double performance) {
        this.performance = performance;
    }
}

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

package com.amalto.core.storage.task;

import org.apache.commons.lang.NotImplementedException;
import org.quartz.*;
import org.quartz.utils.Key;

import java.util.Date;

class TaskTrigger extends Trigger {

    private final Task task;

    private final Trigger delegate;

    private TaskTrigger(Trigger delegate, Task task) {
        this.task = task;
        this.delegate = delegate;
    }

    public static Trigger decorate(Trigger delegate, Task task) {
        return new TaskTrigger(delegate, task);
    }

    public Task getTask() {
        return task;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void setName(String name) {
        delegate.setName(name);
    }

    @Override
    public String getGroup() {
        return delegate.getGroup();
    }

    @Override
    public void setGroup(String group) {
        delegate.setGroup(group);
    }

    @Override
    public String getJobName() {
        return delegate.getJobName();
    }

    @Override
    public void setJobName(String jobName) {
        delegate.setJobName(jobName);
    }

    @Override
    public String getJobGroup() {
        return delegate.getJobGroup();
    }

    @Override
    public void setJobGroup(String jobGroup) {
        delegate.setJobGroup(jobGroup);
    }

    @Override
    public String getFullName() {
        return delegate.getFullName();
    }

    @Override
    public Key getKey() {
        return delegate.getKey();
    }

    @Override
    public String getFullJobName() {
        return delegate.getFullJobName();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    @Override
    public void setDescription(String description) {
        delegate.setDescription(description);
    }

    @Override
    public void setVolatility(boolean volatility) {
        delegate.setVolatility(volatility);
    }

    @Override
    public void setCalendarName(String calendarName) {
        delegate.setCalendarName(calendarName);
    }

    @Override
    public String getCalendarName() {
        return delegate.getCalendarName();
    }

    @Override
    public JobDataMap getJobDataMap() {
        return delegate.getJobDataMap();
    }

    @Override
    public void setJobDataMap(JobDataMap jobDataMap) {
        delegate.setJobDataMap(jobDataMap);
    }

    @Override
    public boolean isVolatile() {
        return delegate.isVolatile();
    }

    @Override
    public int getPriority() {
        return delegate.getPriority();
    }

    @Override
    public void setPriority(int priority) {
        delegate.setPriority(priority);
    }

    @Override
    public void addTriggerListener(String name) {
        delegate.addTriggerListener(name);
    }

    @Override
    public boolean removeTriggerListener(String name) {
        return delegate.removeTriggerListener(name);
    }

    @Override
    public String[] getTriggerListenerNames() {
        return delegate.getTriggerListenerNames();
    }

    @Override
    public void clearAllTriggerListeners() {
        delegate.clearAllTriggerListeners();
    }

    @Override
    public void triggered(Calendar calendar) {
        delegate.triggered(calendar);
    }

    @Override
    public Date computeFirstFireTime(Calendar calendar) {
        return delegate.computeFirstFireTime(calendar);
    }

    @Override
    public int executionComplete(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        return delegate.executionComplete(jobExecutionContext, e);
    }

    @Override
    public boolean mayFireAgain() {
        return delegate.mayFireAgain();
    }

    @Override
    public Date getStartTime() {
        return delegate.getStartTime();
    }

    @Override
    public void setStartTime(Date date) {
        delegate.setStartTime(date);
    }

    @Override
    public void setEndTime(Date date) {
        delegate.setEndTime(date);
    }

    @Override
    public Date getEndTime() {
        return delegate.getEndTime();
    }

    @Override
    public Date getNextFireTime() {
        return delegate.getNextFireTime();
    }

    @Override
    public Date getPreviousFireTime() {
        return delegate.getPreviousFireTime();
    }

    @Override
    public Date getFireTimeAfter(Date date) {
        return delegate.getFireTimeAfter(date);
    }

    @Override
    public Date getFinalFireTime() {
        return delegate.getFinalFireTime();
    }

    @Override
    public void setMisfireInstruction(int misfireInstruction) {
        delegate.setMisfireInstruction(misfireInstruction);
    }

    @Override
    public boolean validateMisfireInstruction(int i) {
        throw new NotImplementedException();
    }

    @Override
    public int getMisfireInstruction() {
        return delegate.getMisfireInstruction();
    }

    @Override
    public void updateAfterMisfire(Calendar calendar) {
        delegate.updateAfterMisfire(calendar);
    }

    @Override
    public void updateWithNewCalendar(Calendar calendar, long l) {
        delegate.updateWithNewCalendar(calendar, l);
    }

    @Override
    public void validate() throws SchedulerException {
        delegate.validate();
    }

    @Override
    public void setFireInstanceId(String id) {
        delegate.setFireInstanceId(id);
    }

    @Override
    public String getFireInstanceId() {
        return delegate.getFireInstanceId();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public int compareTo(Object obj) {
        return delegate.compareTo(obj);
    }
}

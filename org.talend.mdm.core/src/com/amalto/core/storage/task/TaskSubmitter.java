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

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.simpl.SimpleJobFactory;
import org.quartz.spi.TriggerFiredBundle;

public class TaskSubmitter {

    private final static TaskSubmitter instance = new TaskSubmitter();

    private final Scheduler scheduler;

    private TaskSubmitter() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            // TODO Seems to be a bad idea to change any existing JobFactory.
            scheduler.setJobFactory(new SimpleJobFactory() {
                public Job newJob(TriggerFiredBundle triggerFiredBundle) throws SchedulerException {
                    Trigger trigger = triggerFiredBundle.getTrigger();
                    if (trigger instanceof TaskTrigger) {
                        return ((TaskTrigger) trigger).getTask();
                    } else {
                        return super.newJob(triggerFiredBundle);
                    }
                }
            });
            scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public static TaskSubmitter getInstance() {
        return instance;
    }

    public void submit(Task task) {
        JobDetail detail = new JobDetail(task.getId(), "group", Task.class);
        try {
            scheduler.scheduleJob(detail, new TaskTrigger(task));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void submitAndWait(Task task) {
        JobDetail detail = new JobDetail(task.getId(), "group", Task.class);
        try {
            TaskTrigger taskTrigger = new TaskTrigger(task);
            scheduler.scheduleJob(detail, taskTrigger);
            taskTrigger.waitForCompletion();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class TaskTrigger extends SimpleTrigger {

        private final Task task;

        public TaskTrigger(Task task) {
            super(task.getId(), "group");
            this.task = task;
        }

        public Task getTask() {
            return task;
        }

        public void waitForCompletion() throws InterruptedException {
            task.waitForCompletion();
        }
    }
}

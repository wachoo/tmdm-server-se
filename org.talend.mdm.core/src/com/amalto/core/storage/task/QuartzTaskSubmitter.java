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

class QuartzTaskSubmitter implements TaskSubmitter {

    private final static TaskSubmitter instance = new QuartzTaskSubmitter();

    private final Scheduler scheduler;

    QuartzTaskSubmitter() {
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

    @Override
    public void submit(Task task) {
        SimpleTrigger trigger = new SimpleTrigger(task.getId(), "group");
        submit(task, trigger);
    }

    @Override
    public void submitAndWait(Task task) {
        SimpleTrigger trigger = new SimpleTrigger(task.getId(), "group");
        submit(task, trigger);
        try {
            task.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException("Exception occurred during wait for task's end.", e);
        }
    }

    void submit(Task task, Trigger trigger) {
        JobDetail detail = new JobDetail(task.getId(), "group", Task.class); //$NON-NLS-1$
        try {
            scheduler.scheduleJob(detail, TaskTrigger.decorate(trigger, task));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}

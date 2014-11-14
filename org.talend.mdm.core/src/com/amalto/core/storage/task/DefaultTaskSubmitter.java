/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.task;

public class DefaultTaskSubmitter implements TaskSubmitter {
    @Override
    public void submit(Task task) {
        new Thread(task).start();
    }

    @Override
    public void submitAndWait(Task task) {
        task.run();
        try {
            task.waitForCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException("Task did not successfully completed.", e);
        }
    }
}

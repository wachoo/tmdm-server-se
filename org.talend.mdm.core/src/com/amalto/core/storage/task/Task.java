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

import org.quartz.Job;

/**
 *
 */
public interface Task extends Job {

    String getId();
    
    double getRecordCount();
    
    double getCurrentPerformance();

    double getMinPerformance();

    double getMaxPerformance();

    void cancel();

    void waitForCompletion() throws InterruptedException;
}

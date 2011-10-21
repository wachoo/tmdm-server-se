// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.jobox.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.amalto.core.jobox.JobInfo;
import com.amalto.core.jobox.util.JoboxException;

public class JobInvoke extends JobInvoker {

    public JobInvoke(JobInfo jobInfo) {
        super(jobInfo);
    }

    @Override
    protected String[][] getReturn(Method runJobMethod, Object jobInstance, Object parameter) throws JoboxException {
        try {
            String[][] result;
            Object returnObject = runJobMethod.invoke(jobInstance, (Object) parameter);
            result = (String[][]) returnObject;
            return result;
        } catch (Exception e) {
            throw new JoboxException(e);
        }
    }

    @Override
    protected void prepareJobInstance(Object jobInstance, Map<String, String> parameters) {
        // Nothing to prepare in this implementation
    }
}

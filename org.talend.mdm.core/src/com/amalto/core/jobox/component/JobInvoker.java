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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.JobInfo;
import com.amalto.core.jobox.JobInvokeConfig;
import com.amalto.core.jobox.util.JobNotFoundException;
import com.amalto.core.jobox.util.JoboxException;
import com.amalto.core.jobox.util.MissingMainClassException;

/**
 *
 */
public abstract class JobInvoker {

    private final JobInfo jobInfo;

    public JobInvoker(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }

    public String[][] call() throws JoboxException {
        return call(Collections.<String, String>emptyMap());
    }

    /**
     * @param parameters Input values for job execution
     * @return Result of job execution
     * @throws com.amalto.core.jobox.util.JoboxException
     *          In case of call error.
     */
    public String[][] call(Map<String, String> parameters) {
        String[][] result;

        try {
            if (jobInfo.getMainClass() == null) {
                throw new MissingMainClassException();
            }

            Class jobClass = JobContainer.getUniqueInstance().getJobClass(jobInfo);
            Method runJobMethod = jobClass.getMethod("runJob", String[].class);//$NON-NLS-1$

            Map<String, String> paramMap = jobInfo.getDefaultParamMap();
            // merge with default map
            if (parameters != null) {
                for (String inputParamName : parameters.keySet()) {
                    String inputParamValue = parameters.get(inputParamName);
                    paramMap.put(inputParamName, inputParamValue);
                }
            }

            List<String> params = new ArrayList<String>();
            for (String paramName : paramMap.keySet()) {
                String paramValue = paramMap.get(paramName);
                if (paramName != null && paramValue != null) {
                    params.add("--context_param" + " " + paramName + "=" + paramValue); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                }
            }

            Object jobInstance = jobClass.newInstance();
            prepareJobInstance(jobInstance, parameters);

            String[] parameter = params.toArray(new String[params.size()]);
            result = getReturn(runJobMethod, jobInstance, parameter);
        } catch (JobNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new JoboxException(e.getCause().getLocalizedMessage(), e);
        }

        return result;
    }

    protected abstract String[][] getReturn(Method runJobMethod, Object jobInstance, Object parameter) throws JoboxException;

    protected abstract void prepareJobInstance(Object jobInstance, Map<String, String> parameters) throws JoboxException;
}

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

import java.lang.reflect.Method;
import java.util.Map;

import com.amalto.core.jobox.JobInfo;
import com.amalto.core.jobox.util.JoboxException;
import org.apache.log4j.Logger;
import org.dom4j.Document;

public class MDMJobInvoker extends JobInvoker {

    public static final String SET_MDM_INPUT_MESSAGE_METHOD = "setMDMInputMessage";

    public static final String GET_MDM_OUTPUT_MESSAGE_METHOD = "getMDMOutputMessage";

    public static final String EXCHANGE_XML_PARAMETER = "__talend__exchangeXML__";

    public static final Logger LOGGER = Logger.getLogger(MDMJobInvoker.class);

    public MDMJobInvoker(JobInfo jobInfo) {
        super(jobInfo);
    }

    @Override
    public String[][] call() throws JoboxException {
        throw new JoboxException(new UnsupportedOperationException("Caller must provide exchange message."));
    }

    @Override
    protected String[][] getReturn(Method runJobMethod, Object jobInstance, Object parameter) throws JoboxException {
        try {
            runJobMethod.invoke(jobInstance, (Object) parameter);

            Method getMDMOutputMethod = jobInstance.getClass().getMethod(GET_MDM_OUTPUT_MESSAGE_METHOD);
            Document result = (Document) getMDMOutputMethod.invoke(jobInstance);
            if (result == null) {
                LOGGER.warn("Job '" + jobInstance.getClass().getName() + "' returned null as result of " + GET_MDM_OUTPUT_MESSAGE_METHOD + "(). Please check job because this is unexpected.");
                return new String[0][0];
            } else {
                String[] resultAsString = {result.asXML()};
                return new String[][]{resultAsString};
            }
        } catch (Exception e) {
            throw new JoboxException(e);
        }
    }

    @Override
    protected void prepareJobInstance(Object jobInstance, Map<String, String> parameters) throws JoboxException {
        try {
            Method setMDMInputMethod = jobInstance.getClass().getMethod(SET_MDM_INPUT_MESSAGE_METHOD, String.class);
            if (parameters.containsKey(EXCHANGE_XML_PARAMETER)) {
                setMDMInputMethod.invoke(jobInstance, parameters.get(EXCHANGE_XML_PARAMETER));
            } else {
                throw new IllegalArgumentException("Parameter " + EXCHANGE_XML_PARAMETER + " is expected for invocation but was not found."); //$NON-NLS-1$
            }
        } catch (Exception e) {
            throw new JoboxException(e);
        }
    }
}

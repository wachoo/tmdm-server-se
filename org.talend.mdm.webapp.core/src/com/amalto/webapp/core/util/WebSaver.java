// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.jobox.util.JobNotFoundException;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.save.context.SaverContextFactory;
import com.amalto.core.util.BeforeSavingErrorException;
import com.amalto.core.util.BeforeSavingFormatException;
import com.amalto.core.util.CVCException;
import com.amalto.core.util.OutputReportMissingException;
import com.amalto.core.util.RoutingException;
import com.amalto.core.util.SchematronValidateException;
import com.amalto.core.util.ValidateException;
import com.amalto.core.webservice.WSItemPK;
import com.amalto.core.webservice.WSPutItemWithReport;

/**
 * DOC talend2 class global comment. Detailled comment
 */
public class WebSaver {

    // validate not null field is null
    public static final String VALIDATE_EXCEPTION_MESSAGE = "save_validationrule_fail"; //$NON-NLS-1$

    // don't exist job was called
    public static final String JOBNOTFOUND_EXCEPTION_MESSAGE = "save_failure_job_notfound"; //$NON-NLS-1$

    // don't exist job was called
    public static final String JOBOX_EXCEPTION_MESSAGE = "save_failure_job_ox"; //$NON-NLS-1$

    // call by core util.defaultValidate,but it will be wrap as ValidateException
    public static final String CVC_EXCEPTION_MESSAGE = "save_fail_cvc_exception"; //$NON-NLS-1$

    public static final String SAVE_EXCEPTION_MESSAGE = "save_fail"; //$NON-NLS-1$

    // define beforesaving report in error level
    public static final String SAVE_PROCESS_BEFORESAVING_FAILURE_MESSAGE = "save_failure_beforesaving_validate_error"; //$NON-NLS-1$ 

    // define beforesaving report xml format error in studio
    public static final String BEFORESAVING_FORMATE_ERROR_MESSAGE = "save_failure_beforesaving_format_error"; //$NON-NLS-1$ 

    // define trigger to excute process error in studio
    public static final String ROUTING_ERROR_MESSAGE = "save_success_rounting_fail"; //$NON-NLS-1$

    public static final String OUTPUT_REPORT_MISSING_ERROR_MESSAGE = "output_report_missing"; //$NON-NLS-1$

    String dataClusterName;

    String dataModelName;

    SaverSession session;

    public WebSaver() {

    }

    public WebSaver(String dataClusterName, String dataModelName, SaverSession session) {
        this.dataClusterName = dataClusterName;
        this.dataModelName = dataModelName;
        this.session = session;
    }

    public WSItemPK saveItemWithReport(WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
        DocumentSaver saver;
        String xmlString = wsPutItemWithReport.getWsPutItem().getXmlString();
        boolean isUpdate = wsPutItemWithReport.getWsPutItem().getIsUpdate();
        try {
            saver = this.saveItemWithReport(xmlString, !isUpdate, wsPutItemWithReport.getSource(),
                    wsPutItemWithReport.getInvokeBeforeSaving());
            wsPutItemWithReport.setSource(saver.getBeforeSavingMessage());
            session.end();
            String[] savedId = saver.getSavedId();
            String conceptName = saver.getSavedConceptName();
            return new WSItemPK(wsPutItemWithReport.getWsPutItem().getWsDataClusterPK(), conceptName, savedId);
        } catch (Exception exception) {
            throw handleException(exception);
        }
    }

    protected DocumentSaver saveItemWithReport(String xmlString, boolean isReplace, String changeSource, boolean beforeSaving)
            throws UnsupportedEncodingException {
        SaverContextFactory contextFactory = session.getContextFactory();
        DocumentSaverContext context = contextFactory.create(dataClusterName, dataModelName, changeSource,
                new ByteArrayInputStream(xmlString.getBytes("UTF-8")), //$NON-NLS-1$
                isReplace, true, // Always validate
                true, // Always generate an update report
                beforeSaving, XSystemObjects.DC_PROVISIONING.getName().equals(dataClusterName));
        DocumentSaver saver = context.createSaver();
        saver.save(session, context);
        return saver;
    }

    public static RemoteException handleException(Throwable throwable) {
        WebCoreException webCoreException;
        if (WebCoreException.class.isInstance(throwable)) {
            webCoreException = (WebCoreException) throwable;
        } else if (ValidateException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(VALIDATE_EXCEPTION_MESSAGE, throwable);
        } else if (SchematronValidateException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(VALIDATE_EXCEPTION_MESSAGE, throwable.getMessage(), WebCoreException.INFO);
        } else if (CVCException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(CVC_EXCEPTION_MESSAGE, throwable);
        } else if (JobNotFoundException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(JOBNOTFOUND_EXCEPTION_MESSAGE, throwable);
        } else if (com.amalto.core.jobox.util.JoboxException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(JOBOX_EXCEPTION_MESSAGE, throwable);
        } else if (BeforeSavingErrorException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(SAVE_PROCESS_BEFORESAVING_FAILURE_MESSAGE, throwable);
        } else if (BeforeSavingFormatException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(BEFORESAVING_FORMATE_ERROR_MESSAGE, throwable);
            webCoreException.setClient(true);
        } else if (OutputReportMissingException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(OUTPUT_REPORT_MISSING_ERROR_MESSAGE, throwable);
        } else if (RoutingException.class.isInstance(throwable)) {
            webCoreException = new WebCoreException(ROUTING_ERROR_MESSAGE, throwable);
        } else {
            if (throwable.getCause() != null) {
                return handleException(throwable.getCause());
            } else {
                webCoreException = new WebCoreException(SAVE_EXCEPTION_MESSAGE, throwable);
            }
        }
        return new RemoteException("", webCoreException); //$NON-NLS-1$
    }
}

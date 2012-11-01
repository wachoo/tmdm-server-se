package com.amalto.webapp.core.util;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.amalto.core.jobox.util.JobNotFoundException;
import com.amalto.core.save.SaveException;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.util.BeforeSavingErrorException;
import com.amalto.core.util.BeforeSavingFormatException;
import com.amalto.core.util.CVCException;
import com.amalto.core.util.OutputReportMissingException;
import com.amalto.core.util.RoutingException;
import com.amalto.core.util.ValidateException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSPutItemWithReport;

/**
 * DOC talend2 class global comment. Detailled comment
 */
public class WebSaverTest extends TestCase {

    List exceptionList = new ArrayList();

    List titleList = new ArrayList();

    List messageList = new ArrayList();

    RuntimeException currentException;

    WebSaver saver;

    public void init() {

        saver = new TestWebSaver();
        exceptionList.add(new RuntimeException(new ValidateException("ValidateException"))); //$NON-NLS-1$
        exceptionList.add(new RuntimeException(new CVCException("CVCException"))); //$NON-NLS-1$
        exceptionList.add(new RuntimeException(new JobNotFoundException("job", "0.1"))); //$NON-NLS-1$ //$NON-NLS-2$       
        exceptionList.add(new RuntimeException(new BeforeSavingFormatException("BeforeSavingFormatException"))); //$NON-NLS-1$ 
        exceptionList.add(new RuntimeException(new RoutingException("RoutingException"))); //$NON-NLS-1$
        exceptionList.add(new RuntimeException(new OutputReportMissingException("OutputReportMissingException"))); //$NON-NLS-1$
        exceptionList.add(new RuntimeException(new com.amalto.core.jobox.util.JoboxException("JoboxException"))); //$NON-NLS-1$

        // beforesaving exception
        BeforeSavingErrorException beforesavingException = new BeforeSavingErrorException("Error beforesaving"); //$NON-NLS-1$
        SaveException saveException = new SaveException("beforesaing xml", beforesavingException);
        exceptionList.add(saveException);

        titleList.add(WebSaver.VALIDATE_EXCEPTION_MESSAGE);
        titleList.add(WebSaver.CVC_EXCEPTION_MESSAGE);
        titleList.add(WebSaver.JOBNOTFOUND_EXCEPTION_MESSAGE);
        titleList.add(WebSaver.BEFORESAVING_FORMATE_ERROR_MESSAGE);
        titleList.add(WebSaver.ROUTING_ERROR_MESSAGE);
        titleList.add(WebSaver.OUTPUT_REPORT_MISSING_ERROR_MESSAGE);
        titleList.add(WebSaver.JOBOX_EXCEPTION_MESSAGE);
        titleList.add(WebSaver.SAVE_PROCESS_BEFORESAVING_FAILURE_MESSAGE);

        messageList.add("ValidateException"); //$NON-NLS-1$
        messageList.add("CVCException"); //$NON-NLS-1$
        messageList.add("job 0.1"); //$NON-NLS-1$
        messageList.add("BeforeSavingFormatException"); //$NON-NLS-1$
        messageList.add("RoutingException"); //$NON-NLS-1$
        messageList.add("OutputReportMissingException"); //$NON-NLS-1$
        messageList.add("JoboxException"); //$NON-NLS-1$
        messageList.add("Error beforesaving"); //$NON-NLS-1$  
    }

    public void testSaveItemWithReport() {
        init();
        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK("Product"); //$NON-NLS-1$
        WSDataModelPK wsDataModelPK = new WSDataModelPK("Product"); //$NON-NLS-1$
        WSPutItem wsPutItem = new WSPutItem(wsDataClusterPK, "", wsDataModelPK, false); //$NON-NLS-1$
        WSPutItemWithReport wsPutItemWithReport = new WSPutItemWithReport(wsPutItem, "", false); //$NON-NLS-1$

        // testing catch Exception
        for (int i = 0; i < exceptionList.size(); i++) {
            currentException = (RuntimeException) exceptionList.get(i);
            try {
                saver.saveItemWithReport(wsPutItemWithReport);
            } catch (RemoteException exception) {
                WebCoreException webCoreException = (WebCoreException) exception.getCause();
                assertEquals(titleList.get(i), webCoreException.getTitle());
                assertEquals(messageList.get(i), webCoreException.getCause().getLocalizedMessage());
            }
        }
    }

    public class TestWebSaver extends WebSaver {

        public TestWebSaver() {
            super();
        }

        @Override
        protected DocumentSaver saveItemWithReport(String xmlString, SaverSession session, boolean isReplace,
                String dataClusterName, String dataModelName, String changeSource, boolean beforeSaving)
                throws UnsupportedEncodingException {
            throw currentException;
        }
    }
}

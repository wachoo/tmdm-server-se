package com.amalto.core.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.load.action.DefaultLoadAction;
import com.amalto.core.load.action.LoadAction;
import com.amalto.core.load.action.OptimizedLoadAction;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.TimeMeasure;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author starkey
 *
 *
 */

public class LoadServlet extends HttpServlet {

    private static final String PARAMETER_CLUSTER = "cluster"; //$NON-NLS-1$
    private static final String PARAMETER_CONCEPT = "concept"; //$NON-NLS-1$
    private static final String PARAMETER_DATAMODEL = "datamodel"; //$NON-NLS-1$
    private static final String PARAMETER_VALIDATE = "validate"; //$NON-NLS-1$
    private static final String PARAMETER_SMARTPK = "smartpk"; //$NON-NLS-1$
    private static final String PARAMETER_ITEMDATA = "itemdata"; //$NON-NLS-1$

    private static final Logger log = Logger.getLogger(LoadServlet.class);

    private static final Map<String, XSDKey> typeNameToKeyDef = new HashMap<String, XSDKey>();
    public static final String PARAMETER_ACTION = "action"; //$NON-NLS-1$

    /**
     * Constructor
     */
    public LoadServlet() {
        super();
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); //$NON-NLS-1$
        response.setContentType("text/html; charset=\"UTF-8\""); //$NON-NLS-1$
        response.setCharacterEncoding("UTF-8"); //$NON-NLS-1$

        // configure writer depending on logger configuration.
        PrintWriter writer = configureWriter(response);
        writer.write("<html><body>"); //$NON-NLS-1$
        writer.write(
                "<p><b>Load datas into MDM</b><br/>" +
                        "Check jboss/server/default/log/server.log or the jboss console output to determine when load is completed</b></p>"
        );

        String dataClusterName = request.getParameter(PARAMETER_CLUSTER);
        String typeName = request.getParameter(PARAMETER_CONCEPT);
        String dataModelName = request.getParameter(PARAMETER_DATAMODEL);
        boolean needValidate = Boolean.valueOf(request.getParameter(PARAMETER_VALIDATE));
        boolean needAutoGenPK = Boolean.valueOf(request.getParameter(PARAMETER_SMARTPK));
        String action = request.getParameter(PARAMETER_ACTION);
        if (action == null || action.length() == 0)
            action = getServletConfig().getInitParameter(PARAMETER_ACTION);

        // We support only load as action here
        if (!"load".equalsIgnoreCase(action)) { //$NON-NLS-1$
            throw new ServletException(new UnsupportedOperationException("Action '" + action + "' isn't supported"));
        }

        LoadAction loadAction = getLoadAction(dataClusterName, typeName, dataModelName, needValidate, needAutoGenPK);
        if (needValidate && !loadAction.supportValidation()) {
            throw new ServletException(new UnsupportedOperationException("XML Validation isn't supported"));
        }
        if (needAutoGenPK && !loadAction.supportAutoGenPK()) {
            throw new ServletException(new UnsupportedOperationException("Autogen pk isn't supported"));
        }

        // Get xml server and key information
        XmlServerSLWrapperLocal server;
        XSDKey keyMetadata;
        try {
            keyMetadata = getTypeKey(dataModelName, typeName);
            server = Util.getXmlServerCtrlLocal();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        // Start parsing/loading
        try {
            if (server.supportTransaction()) {
                server.start();
            }

            loadAction.load(request, keyMetadata, server);

            // Commit changes
            try {
                if (server.supportTransaction()) {
                    server.commit();
                }
            } catch (Exception commitException) {
                throw new ServletException("Commit failed with errors", commitException);
            }
        } catch (Throwable throwable) {
            if (server.supportTransaction()) {
                try {
                    server.rollback();
                } catch (Exception rollbackException) {
                    log.error("Ignoring rollback exception", rollbackException);
                }
            }
            throw new ServletException(throwable);
        }

        writer.write("</body></html>"); //$NON-NLS-1$
    }

    protected static LoadAction getLoadAction(String dataClusterName, String typeName, String dataModelName, boolean needValidate, boolean needAutoGenPK) {
        // Activate optimizations only if Qizx is used.
        Object dbType = MDMConfiguration.getConfiguration().get("xmldb.type"); //$NON-NLS-1$
        boolean isUsingQizx = dbType != null && EDBType.QIZX.getName().equals(dbType.toString());

        LoadAction loadAction;
        if (needValidate || !isUsingQizx) {
            loadAction = new DefaultLoadAction(dataClusterName, typeName, dataModelName, needValidate, needAutoGenPK);
        } else {
            loadAction = new OptimizedLoadAction(dataClusterName, typeName, needAutoGenPK);
        }

        if (log.isDebugEnabled()) {
            log.debug("Load action selected for load: " + loadAction.getClass().getName() + "(isUsingQizx: " + isUsingQizx //$NON-NLS-1$ //$NON-NLS-2$
                    + " / needValidate:" + needValidate + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return loadAction;
    }

    /**
     * @deprecated doPut should be used instead of this method !
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * @deprecated doPut should be used instead of this method !
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException,
            IOException {

        req.setCharacterEncoding("UTF-8"); //$NON-NLS-1$

        String action = req.getParameter(PARAMETER_ACTION);
        if (action == null || action.length() == 0)
            action = getServletConfig().getInitParameter(PARAMETER_ACTION);

        resp.setContentType("text/html; charset=\"UTF-8\""); //$NON-NLS-1$
        resp.setCharacterEncoding("UTF-8"); //$NON-NLS-1$

        // configure writer depending on logger configuration.
        PrintWriter writer = configureWriter(resp);
        writer.write("<html><body>"); //$NON-NLS-1$

        String timeMeasureTag = System.currentTimeMillis() + ": Bulk load items";
        ResultLogger resultLogger = new ResultLogger();

        try {
            if ("load".equals(action)) { //$NON-NLS-1$
                writer.write(
                        "<p><b>Load datas into MDM</b><br/>" +
                                "Check jboss/server/default/log/server.log or the jboss console output to determine when load is completed</b></p>"
                );

                TimeMeasure.begin(timeMeasureTag);
                resultLogger.logTimeMeasureBegin(timeMeasureTag);


                String cluster = null;
                String concept = null;
                String datamodel = null;
                List<String> itemdatas = new ArrayList<String>();

                boolean needAutoGenPK = false;
                boolean needValidate = false;

                Enumeration<String> parameterNames = req.getParameterNames();
                for (; parameterNames.hasMoreElements();) {
                    String parameterName = parameterNames.nextElement();

                    if (parameterName.equals(PARAMETER_CLUSTER)) {
                        cluster = req.getParameter(parameterName);
                        continue;
                    }

                    if (parameterName.equals(PARAMETER_CONCEPT)) {
                        concept = req.getParameter(parameterName);
                        continue;
                    }

                    if (parameterName.equals(PARAMETER_DATAMODEL)) {
                        datamodel = req.getParameter(parameterName);
                        continue;
                    }

                    //optional
                    if (parameterName.equals(PARAMETER_VALIDATE)) {
                        String validate = req.getParameter(parameterName);
                        needValidate = (validate != null && validate.equals("true")) ? true : false; //$NON-NLS-1$
                        continue;
                    }

                    //optional
                    if (parameterName.equals(PARAMETER_SMARTPK)) {
                        String smartpk = req.getParameter(parameterName);
                        needAutoGenPK = (smartpk != null && smartpk.equals("true")) ? true : false; //$NON-NLS-1$
                        continue;
                    }

                    if (parameterName.startsWith(PARAMETER_ITEMDATA)) {
                        itemdatas.add(req.getParameter(parameterName));
                    }
                }


                DataClusterPOJOPK clusterPK = new DataClusterPOJOPK(cluster);


                DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(datamodel));
                String schemaString = dataModel.getSchema();
                Document schema = Util.parseXSD(schemaString);
                XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(schema, concept);

                resultLogger.logTimeMeasureStep(timeMeasureTag, "Parse schema", TimeMeasure.step(timeMeasureTag, "Parse schema")); //$NON-NLS-1$ //$NON-NLS-2$
                //each item
                XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
                boolean transactionSupported = server.supportTransaction();

                try {
                    if (transactionSupported)
                        server.start();

                    for (int i = 0; i < itemdatas.size(); i++) {

                        try {
                            String xmldata = itemdatas.get(i);

                            if (xmldata == null || xmldata.trim().length() == 0)
                                continue;

                            Element root = Util.parse(xmldata).getDocumentElement();

                            // get key values
                            // support UUID or auto-increase temporarily
                            String[] ids = null;
                            if (!needAutoGenPK) {
                                ids = com.amalto.core.util.Util.getKeyValuesFromItem(root, conceptKey);
                            } else {

                                if (Util.getUUIDNodes(schemaString, concept).size() > 0) { // check uuid key exists

                                    Node n = Util.processUUID(root, schemaString, cluster, concept);

                                    // get key values
                                    ids = com.amalto.core.util.Util.getKeyValuesFromItem((Element) n, conceptKey);
                                    // reset item projection
                                    xmldata = Util.nodeToString(n);

                                }
                            }

                            ItemPOJO itemPOJO = new ItemPOJO(clusterPK, concept, ids, System.currentTimeMillis(), xmldata);

                            // validate
                            if (schemaString != null && needValidate)
                                Util.validate(itemPOJO.getProjection(), schemaString);

                            if (datamodel != null && datamodel.length() > 0)
                                itemPOJO.setDataModelName(datamodel);

                            //When doing bulk load, disable cache
                            itemPOJO.store(false);
                        } catch (Exception e) {
                            resultLogger.logErrorMessage(e.getLocalizedMessage());
                            if (transactionSupported)
                                throw e;
                            log.error(e.getMessage(), e);
                        }

                        if ((i + 1) % 1000 == 0) {
                            String stepName = "Loaded " + ((i + 1) / 1000) + "k"; //$NON-NLS-1$ //$NON-NLS-2$
                            resultLogger.logTimeMeasureStep(timeMeasureTag, stepName, TimeMeasure.step(timeMeasureTag, stepName));
                        }
                    }

                    if (transactionSupported)
                        server.commit();

                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (transactionSupported) {
                        try {
                            server.rollback();
                        } catch (Exception rollbackException) {
                            log.error(rollbackException.getMessage(), rollbackException);
                        }
                    }
                } finally {
                    if (transactionSupported) {
                        try {
                            server.end();
                        } catch (Exception endException) {
                            log.error(endException.getMessage(), endException);
                        }
                    }
                }
                resultLogger.logTimeMeasureEnd(timeMeasureTag, TimeMeasure.end(timeMeasureTag));

                writer.write("<p>" + resultLogger.print()); //$NON-NLS-1$

            } else {
                writer.write("<p><b>Unknown action: </b>" + action + "<br/>"); //$NON-NLS-1$ //$NON-NLS-2$

            }

        } catch (Exception e) {
            writer.write("<h1>An error occured: " + e.getLocalizedMessage() + "</h1>"); //$NON-NLS-1$ //$NON-NLS-2$
            TimeMeasure.end(timeMeasureTag);
            log.error(e.getMessage(), e);
        }
        writer.write("</body></html>"); //$NON-NLS-1$
    }


    private XSDKey getTypeKey(String dataModelName, String typeName) throws Exception {
        XSDKey xsdKey = typeNameToKeyDef.get(dataModelName + typeName);

        if (xsdKey == null) {
            if (log.isDebugEnabled()) {
                log.debug("Caching id for type '" + typeName + "' in data model '" + dataModelName + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(dataModelName));
            String schemaString = dataModel.getSchema();
            Document schema = Util.parseXSD(schemaString);
            XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(schema, typeName);

            if (conceptKey != null) {
                String keysAsString = ""; //$NON-NLS-1$
                for (String currentField : conceptKey.getFields()) {
                    keysAsString += ' ' + currentField;
                }
                if (log.isDebugEnabled()) {
                    log.debug("Key for entity '" + typeName + "' : " + keysAsString); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } else {
                log.error("No key definition for entity '" + typeName + "'.");
                throw new RuntimeException("No key definition for entity '" + typeName + "'.");
            }
            xsdKey = conceptKey;

            // Use dataModelName in key in case 1+ data models share the type name
            typeNameToKeyDef.put(dataModelName + typeName, xsdKey);
        }

        return xsdKey;
    }

    /**
     * Returns a writer that does not print anything if logger hasn't DEBUG level.
     *
     * @param resp A servlet response output.
     * @return The same {@link HttpServletResponse} if debug <b>is</b> enabled, or a no-op one if debug is disabled.
     * @throws IOException Thrown by {@link javax.servlet.http.HttpServletResponse#getWriter()}.
     */
    private static PrintWriter configureWriter(HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        if (log.isDebugEnabled()) {
            return writer;
        } else {
            return new PrintWriter(writer) {
                @Override
                public void write(int c) {
                    // Nothing to do (debug isn't enabled)
                }

                @Override
                public void write(char[] buf, int off, int len) {
                    // Nothing to do (debug isn't enabled)
                }

                @Override
                public void write(char[] buf) {
                    // Nothing to do (debug isn't enabled)
                }

                @Override
                public void write(String s, int off, int len) {
                    // Nothing to do (debug isn't enabled)
                }

                @Override
                public void write(String s) {
                    // Nothing to do (debug isn't enabled)
                }
            };
        }

    }


    /**
     * Represents an element on the stack.
     */
    private static class ResultLogger {

        private StringBuffer resultLogger = null;

        public ResultLogger() {
            this.resultLogger = new StringBuffer();
        }

        public void logTimeMeasure(String msg) {

            resultLogger.append(System.currentTimeMillis() + " [TimeMeasure]:" + msg + "</br>"); //$NON-NLS-1$ //$NON-NLS-2$

        }


        private void logTimeMeasureBegin(String timerId) {

            logTimeMeasure("Start '" + timerId + "' ..."); //$NON-NLS-1$ //$NON-NLS-2$

        }

        private void logTimeMeasureEnd(String timerId, long totalElapsedTime) {

            logTimeMeasure("End '" + timerId + "', total elapsed time: " + totalElapsedTime + " ms "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        }

        public void logTimeMeasureStep(String timerId, String stepName, long stepCount) {
            logTimeMeasure("&nbsp;&nbsp;&nbsp;&nbsp;-> '" + timerId + "', step name '" + stepName //$NON-NLS-1$ //$NON-NLS-2$
                    + "', elapsed time since previous step: " + stepCount + " ms "); //$NON-NLS-1$ //$NON-NLS-2$
        }

        public void logErrorMessage(String msg) {

            resultLogger.append(System.currentTimeMillis() + " [ErrorMessage]:" + msg + "</br>");  //$NON-NLS-1$ //$NON-NLS-2$

        }

        public String print() {
            return resultLogger.toString();
        }
    }

}
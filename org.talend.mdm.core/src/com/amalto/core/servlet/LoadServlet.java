package com.amalto.core.servlet;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.load.action.DefaultLoadAction;
import com.amalto.core.load.action.LoadAction;
import com.amalto.core.load.action.OptimizedLoadAction;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;


/**
 * @author starkey
 */

public class LoadServlet extends HttpServlet {

    private static final String PARAMETER_CLUSTER = "cluster"; //$NON-NLS-1$

    private static final String PARAMETER_CONCEPT = "concept"; //$NON-NLS-1$

    private static final String PARAMETER_DATAMODEL = "datamodel"; //$NON-NLS-1$

    private static final String PARAMETER_VALIDATE = "validate"; //$NON-NLS-1$

    private static final String PARAMETER_SMARTPK = "smartpk"; //$NON-NLS-1$

    private static final Logger log = Logger.getLogger(LoadServlet.class);

    private static final Map<String, XSDKey> typeNameToKeyDef = new HashMap<String, XSDKey>();

    private static final String PARAMETER_ACTION = "action"; //$NON-NLS-1$

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
        response.setContentType("text/html; charset=UTF-8"); //$NON-NLS-1$
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
        if (action == null || action.length() == 0) {
            action = getServletConfig().getInitParameter(PARAMETER_ACTION);
        }

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
                    server.end();
                }
            } catch (Exception commitException) {
                throw new ServletException("Commit failed with errors", commitException);
            }

            // End the load (might persist counter state in case of autogen pk
            loadAction.endLoad(server);
        } catch (Throwable throwable) {
            if (server.supportTransaction()) {
                try {
                    server.rollback();
                } catch (Exception rollbackException) {
                    log.error("Ignoring rollback exception", rollbackException);
                }
            }
            throw new ServletException(throwable);
        } finally {
            if (server.supportTransaction()) {
                try {
                    server.end();
                } catch (Exception endException) {
                    log.error("Ignoring end call exception", endException);
                }
            }
        }

        writer.write("</body></html>"); //$NON-NLS-1$
    }

    protected LoadAction getLoadAction(String dataClusterName, String typeName, String dataModelName, boolean needValidate, boolean needAutoGenPK) {
        // Test if the data cluster actually exists
        DataClusterPOJO dataCluster = getDataCluster(dataClusterName);
        if (dataCluster == null) {
            throw new IllegalArgumentException("Data cluster '" + dataClusterName + "' does not exist.");
        }

        // Activate optimizations only if Qizx is used.
        Object dbType = MDMConfiguration.getConfiguration().get("xmldb.type"); //$NON-NLS-1$
        boolean isUsingQizx = dbType != null && EDBType.QIZX.getName().equals(dbType.toString());

        LoadAction loadAction;
        if (needValidate || !isUsingQizx) {
            loadAction = new DefaultLoadAction(dataClusterName, typeName, dataModelName, needValidate, needAutoGenPK);
        } else {
            loadAction = new OptimizedLoadAction(dataClusterName, typeName, dataModelName, needAutoGenPK);
        }

        if (log.isDebugEnabled()) {
            log.debug("Load action selected for load: " + loadAction.getClass().getName() + "(isUsingQizx: " + isUsingQizx //$NON-NLS-1$ //$NON-NLS-2$
                    + " / needValidate:" + needValidate + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return loadAction;
    }

    protected DataClusterPOJO getDataCluster(String dataClusterName) {
        DataClusterPOJO dataCluster;
        try {
            DataClusterCtrlLocal dataClusterCtrlLocal = Util.getDataClusterCtrlLocal();
            DataClusterPOJOPK dataClusterPOJOPK = new DataClusterPOJOPK(dataClusterName);
            dataCluster = dataClusterCtrlLocal.getDataCluster(dataClusterPOJOPK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dataCluster;
    }

    private XSDKey getTypeKey(String dataModelName, String typeName) throws Exception {
        XSDKey xsdKey = typeNameToKeyDef.get(dataModelName + typeName);

        if (xsdKey == null) {
            synchronized (typeNameToKeyDef) {
                if (log.isDebugEnabled()) {
                    log.debug("Caching id for type '" + typeName + "' in data model '" + dataModelName + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(dataModelName));
                String schemaString = dataModel.getSchema();
                Document schema = Util.parseXSD(schemaString);
                XSDKey conceptKey = Util.getBusinessConceptKey(schema, typeName);

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
}
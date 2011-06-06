/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package talend.webapp.v3.updatereport.servlet;

import com.amalto.core.history.Document;
import com.amalto.core.history.DocumentHistory;
import com.amalto.core.history.DocumentHistoryFactory;
import com.amalto.core.history.DocumentHistoryNavigator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class DocumentHistoryServlet extends HttpServlet {

    private static final String DATE_PARAMETER = "date"; //$NON-NLS-1$

    private static final String DATA_CLUSTER_PARAMETER = "dataCluster"; //$NON-NLS-1$

    private static final String DATA_MODEL_PARAMETER = "dataModel"; //$NON-NLS-1$

    private static final String CONCEPT_PARAMETER = "concept"; //$NON-NLS-1$

    private static final String REVISION_PARAMETER = "revision"; //$NON-NLS-1$

    private static final String ACTION_PARAMETER = "action"; //$NON-NLS-1$

    private static final String KEY_PARAMETER = "key"; //$NON-NLS-1$

    private static final String CURRENT_ACTION = "current"; //$NON-NLS-1$

    private static final String BEFORE_ACTION = "before";  //$NON-NLS-1$

    private static final Logger logger = Logger.getLogger(DocumentHistoryServlet.class);

    private static final DocumentHistory factory = DocumentHistoryFactory.getInstance().create();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Parameters parameters = getParameters(req);

        ServletOutputStream outputStream = resp.getOutputStream();
        Date historyDate = new Date(parameters.getDate());

        DocumentHistoryNavigator navigator = getHistoryNavigator(parameters);

        // Now does the actual writing to client
        resp.setContentType("text/xml");
        outputStream.println("<history>"); //$NON-NLS-1$
        {
            // Go to date history
            navigator.previous(historyDate);

            // Get the one before the action and the one right after
            Document document = new EmptyDocument();
            if (CURRENT_ACTION.equalsIgnoreCase(parameters.getAction())) {
                document = navigator.current();
            } else if (BEFORE_ACTION.equalsIgnoreCase(parameters.getAction())) {
                if (navigator.hasPrevious()) {
                    document = navigator.previous();
                } else {
                    logger.warn("No previous state for document before date '" + historyDate + "'.");
                }
            } else {
                throw new ServletException(new IllegalArgumentException("Action '" + parameters.getAction() + " is not supported."));
            }

            // Write directly the document content w/o using the xml writer (it's already XML).
            outputStream.print(document.getAsString());
        }
        outputStream.println("</history>"); //$NON-NLS-1$
        outputStream.flush();
    }

    private DocumentHistoryNavigator getHistoryNavigator(Parameters parameters) {
        return factory.getHistory(parameters.getDataClusterName(),
                parameters.getDataModelName(),
                parameters.getConceptName(),
                parameters.getId(),
                parameters.getRevisionId());
    }

    private Parameters getParameters(HttpServletRequest req) {
        Map parameters = req.getParameterMap();

        Long date = Long.parseLong(getParameterString(parameters, DATE_PARAMETER, true));
        String dataClusterName = getParameterString(parameters, DATA_CLUSTER_PARAMETER, true);
        String dataModelName = getParameterString(parameters, DATA_MODEL_PARAMETER, true);
        String conceptName = getParameterString(parameters, CONCEPT_PARAMETER, true);
        String revisionId = getParameterString(parameters, REVISION_PARAMETER, false);
        String action = getParameterString(parameters, ACTION_PARAMETER, true);
        String[] id = new String[]{getParameterString(parameters, KEY_PARAMETER, true)}; // TODO Support composite key

        return new Parameters(date, dataClusterName, dataModelName, conceptName, id, revisionId, action);
    }

    private String getParameterString(Map parameters, String parameter, boolean isRequired) {
        Object value = parameters.get(parameter);
        if (isRequired && (value == null || value.toString().trim().isEmpty())) {
            throw new IllegalArgumentException("Parameter '" + parameter + "' is required.");
        }

        if (value instanceof String[]) {
            return ((String[]) value)[0];
        } else {
            return value == null ? null : value.toString();
        }
    }

    private static class Parameters {
        private long date;
        private String dataClusterName;
        private String dataModelName;
        private String conceptName;
        private String[] id;
        private String revisionId;
        private String action;

        private Parameters(long date, String dataClusterName, String dataModelName, String conceptName, String[] id, String revisionId, String action) {
            this.date = date;
            this.dataClusterName = dataClusterName;
            this.dataModelName = dataModelName;
            this.conceptName = conceptName;
            this.id = id;
            this.revisionId = revisionId;
            this.action = action;
        }

        public long getDate() {
            return date;
        }

        public String getDataClusterName() {
            return dataClusterName;
        }

        public String getDataModelName() {
            return dataModelName;
        }

        public String getConceptName() {
            return conceptName;
        }

        public String[] getId() {
            return id;
        }

        public String getRevisionId() {
            return revisionId;
        }

        public String getAction() {
            return action;
        }
    }

    private static class EmptyDocument implements Document {
        public String getAsString() {
            return StringUtils.EMPTY;
        }

        public boolean isCreated() {
            return false;
        }

        public boolean isDeleted() {
            return false;
        }
    }
}


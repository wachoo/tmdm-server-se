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

import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.amalto.core.history.DocumentHistory;
import com.amalto.core.history.DocumentHistoryFactory;

/**
 *
 */
abstract class AbstractDocumentHistoryServlet extends HttpServlet {
    protected static final String DATE_PARAMETER = "date"; //$NON-NLS-1$

    protected static final String DATA_CLUSTER_PARAMETER = "dataCluster"; //$NON-NLS-1$

    protected static final String DATA_MODEL_PARAMETER = "dataModel"; //$NON-NLS-1$

    protected static final String CONCEPT_PARAMETER = "concept"; //$NON-NLS-1$

    protected static final String REVISION_PARAMETER = "revision"; //$NON-NLS-1$

    protected static final String ACTION_PARAMETER = "action"; //$NON-NLS-1$

    protected static final String KEY_PARAMETER = "key"; //$NON-NLS-1$
    
    protected static final String IDS_PARAMETER = "ids"; //$NON-NLS-1$

    protected static final String CURRENT_ACTION = "current"; //$NON-NLS-1$

    protected static final String PREVIOUS_ACTION = "before";  //$NON-NLS-1$

    protected static final String NEXT_ACTION = "next";  //$NON-NLS-1$

    protected static final DocumentHistory factory = DocumentHistoryFactory.getInstance().create();

    protected Parameters getParameters(HttpServletRequest req) {
        Map parameters = req.getParameterMap();

        Long date = Long.parseLong(getParameterString(parameters, DATE_PARAMETER, true));
        String dataClusterName = getParameterString(parameters, DATA_CLUSTER_PARAMETER, true);
        String dataModelName = getParameterString(parameters, DATA_MODEL_PARAMETER, true);
        String conceptName = getParameterString(parameters, CONCEPT_PARAMETER, true);
        String revisionId = getParameterString(parameters, REVISION_PARAMETER, false);
        String action = getParameterString(parameters, ACTION_PARAMETER, true);
        // Web UI sends key values separated by '.' character
        String[] id = getParameterString(parameters, KEY_PARAMETER, true).split("\\.");
        String ids = getParameterString(parameters, IDS_PARAMETER, false);
        
        return new Parameters(date, dataClusterName, dataModelName, conceptName, id, revisionId, action, ids);
    }

    protected String getParameterString(Map parameters, String parameter, boolean isRequired) {
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
}

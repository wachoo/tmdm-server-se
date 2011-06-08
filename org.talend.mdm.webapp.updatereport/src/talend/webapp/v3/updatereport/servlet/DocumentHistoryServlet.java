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
import com.amalto.core.history.DocumentHistoryNavigator;
import com.amalto.core.history.EmptyDocument;
import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 *
 */
public class DocumentHistoryServlet extends AbstractDocumentHistoryServlet {

    private static final Logger logger = Logger.getLogger(DocumentHistoryServlet.class);

    private static final Map<Parameters, DocumentHistoryNavigator> cache = new LRUMap(50);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Parameters parameters = getParameters(req);

        ServletOutputStream outputStream = resp.getOutputStream();
        Date historyDate = new Date(parameters.getDate());

        DocumentHistoryNavigator navigator = factory.getHistory(parameters.getDataClusterName(),
                parameters.getDataModelName(),
                parameters.getConceptName(),
                parameters.getId(),
                parameters.getRevisionId());

        // Now does the actual writing to client
        resp.setContentType("text/xml");
        outputStream.println("<history>"); //$NON-NLS-1$
        {
            // Go to date history
            navigator.goTo(historyDate);

            // Get the one before the action and the one right after
            Document document = new EmptyDocument();
            if (CURRENT_ACTION.equalsIgnoreCase(parameters.getAction())) {
                document = navigator.current();
            } else if (PREVIOUS_ACTION.equalsIgnoreCase(parameters.getAction())) {
                if (navigator.hasPrevious()) {
                    document = navigator.previous();
                } else {
                    logger.warn("No previous state for document before date '" + historyDate + "'.");
                }
            } else if (NEXT_ACTION.equalsIgnoreCase(parameters.getAction())) {
                if (navigator.hasNext()) {
                    document = navigator.next();
                } else {
                    logger.warn("No next state for document after date '" + historyDate + "'.");
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
}


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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.amalto.core.history.Document;
import com.amalto.core.history.DocumentHistoryNavigator;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.EmptyDocument;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;

/**
 *
 */
public class DocumentHistoryServlet extends AbstractDocumentHistoryServlet {

    private static final Logger logger = Logger.getLogger(DocumentHistoryServlet.class);

    private static final MetadataRepository metadataRepository = new MetadataRepository();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Parameters parameters = getParameters(req);
        PrintWriter outputStream = resp.getWriter();
        Date historyDate = new Date(parameters.getDate());

        String typeName = parameters.getConceptName();
        String dataClusterName = parameters.getDataClusterName();
        String dataModelName = parameters.getDataModelName();
        DocumentHistoryNavigator navigator = factory.getHistory(dataClusterName,
                dataModelName,
                typeName,
                parameters.getId(),
                parameters.getRevisionId());

        TypeMetadata documentTypeMetadata;
        synchronized (metadataRepository) {
            documentTypeMetadata = metadataRepository.getType(typeName);
            if (documentTypeMetadata == null) {

                try {
                    // Initialize type metadata information
                    DataModelPOJO dataModel = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(dataModelName));
                    if (dataModel == null) {
                        throw new IllegalArgumentException("Data model '" + typeName + "' does not exist.");
                    }

                    String schemaString = dataModel.getSchema();
                    metadataRepository.load(new ByteArrayInputStream(schemaString.getBytes("UTF-8")));  //$NON-NLS-1$

                    // Tries to load the type information again
                    documentTypeMetadata = metadataRepository.getType(typeName);
                    if (documentTypeMetadata == null) {
                        throw new IllegalArgumentException("Cannot find type information for type '" + typeName + "' in data cluster '" + dataClusterName + "', in data model '" + dataModelName + "'");
                    }
                } catch (Exception e) {
                    throw new ServletException("Could not initialize type information", e);
                }
            }
        }

        // Now does the actual writing to client
        resp.setContentType("text/xml;charset=UTF-8"); //$NON-NLS-1$
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

            // Resolve foreign key info (if any)
            DocumentTransformer transformer = new ForeignKeyInfoTransformer(documentTypeMetadata, dataClusterName);
            Document transformedDocument = document.transform(transformer);
            // Write directly the document content w/o using the xml writer (it's already XML).
            outputStream.print(transformedDocument.exportToString());
        }
        outputStream.println("</history>"); //$NON-NLS-1$
        outputStream.flush();
    }

}


/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.history.Document;
import com.amalto.core.history.DocumentHistoryNavigator;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.EmptyDocument;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;

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
        
        List<String> differList = this.getDifferList(parameters);
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
            outputStream.print(this.geneDifferStr(transformedDocument.exportToString(), parameters.getConceptName(), differList));
        }
        outputStream.println("</history>"); //$NON-NLS-1$
        outputStream.flush();
    }

    private String geneDifferStr(String xmlStr, String conceptName, List<String> differList){
        try {
            org.w3c.dom.Document doc = com.amalto.webapp.core.util.Util.parse(xmlStr);
            for(String path : differList){
                NodeList nodeList = com.amalto.webapp.core.util.Util.getNodeList(doc, "/" + conceptName + "/" + path); //$NON-NLS-1$ //$NON-NLS-2$
                if(nodeList.getLength() > 0){
                    for(int i = 0; i < nodeList.getLength(); i++){
                        Node node = nodeList.item(i);
                        String content = node.getTextContent();
                        node.setTextContent(content + "[#Diff#]"); //$NON-NLS-1$
                    }
                }
            }
            return com.amalto.webapp.core.util.Util.convertDocument2String(doc, false);            
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return xmlStr;
    }
    
    private List<String> getDifferList(Parameters parameters) {
        List<String> differList = new ArrayList<String>();
        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
        String conceptName = "Update"; //$NON-NLS-1$
        String[] idss = parameters.getIds().split("\\."); //$NON-NLS-1$
        WSGetItem wsGetItem = new WSGetItem(new WSItemPK(wsDataClusterPK, conceptName, idss));
        WSItem wsItem;
        try {
            wsItem = com.amalto.webapp.core.util.Util.getPort().getItem(wsGetItem);
            String content = wsItem.getContent();
            if (content != null && content.length() > 0) {
                org.w3c.dom.Document doc = com.amalto.webapp.core.util.Util.parse(content);
                NodeList ls = com.amalto.webapp.core.util.Util.getNodeList(doc, "/Update/Item"); //$NON-NLS-1$
                if (ls.getLength() > 0) {
                    for (int i = 0; i < ls.getLength(); i++) {
                        String path = Util.getFirstTextNode(doc, "/Update/Item[" + (i + 1) + "]/path"); //$NON-NLS-1$//$NON-NLS-2$
                        String elementPath = path.replaceAll("\\[\\d+\\]$", ""); //$NON-NLS-1$//$NON-NLS-2$
                        differList.add(elementPath);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        
        return differList;
    }
}


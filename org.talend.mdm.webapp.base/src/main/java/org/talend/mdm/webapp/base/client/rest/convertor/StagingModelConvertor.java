// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.client.rest.convertor;

import java.io.IOException;

import org.restlet.client.Response;
import org.restlet.client.ext.xml.DomRepresentation;
import org.restlet.client.representation.EmptyRepresentation;
import org.talend.mdm.webapp.base.client.rest.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.base.client.rest.model.StagingAreaValidationModel;
import org.talend.mdm.webapp.base.client.rest.model.StagingContainerModel;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class StagingModelConvertor {

    public static StagingContainerModel response2StagingContainerModel(Response response) throws IOException {
        if (response == null || response.getEntity() == null || response.getEntity() instanceof EmptyRepresentation) {
            return null;
        }

        StagingContainerModel stagingContainerModel = new StagingContainerModel();
        DomRepresentation rep = new DomRepresentation(response.getEntity());
        NodeList list = rep.getDocument().getDocumentElement().getChildNodes();
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeValue = node.getFirstChild().getNodeValue();
                    if (node.getNodeName().equals("data_container")) { //$NON-NLS-1$
                        stagingContainerModel.setDataContainer(nodeValue);
                    } else if (node.getNodeName().equals("data_model")) { //$NON-NLS-1$
                        stagingContainerModel.setDataModel(nodeValue);
                    } else if (node.getNodeName().equals("invalid_records")) { //$NON-NLS-1$
                        stagingContainerModel.setInvalidRecords(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                    } else if (node.getNodeName().equals("total_records")) { //$NON-NLS-1$
                        stagingContainerModel.setTotalRecords(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                    } else if (node.getNodeName().equals("valid_records")) { //$NON-NLS-1$
                        stagingContainerModel.setValidRecords(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                    } else if (node.getNodeName().equals("waiting_validation_records")) { //$NON-NLS-1$
                        stagingContainerModel.setWaitingValidationRecords(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                    }
                }

            }
        }
        return stagingContainerModel;
    }

    public static StagingAreaValidationModel response2StagingAreaValidationModel(Response response) throws IOException {
        if (response == null || response.getEntity() == null || response.getEntity() instanceof EmptyRepresentation) {
            return null;
        }

        StagingAreaValidationModel model = new StagingAreaValidationModel();
        // Get the representation as an XmlRepresentation
        DomRepresentation rep = new DomRepresentation(response.getEntity());
        // Loop on the nodes to retrieve the node names and text content.
        NodeList nodes = rep.getDocument().getDocumentElement().getChildNodes();
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz"; //$NON-NLS-1$
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeValue = node.getFirstChild().getNodeValue();
                if (node.getNodeName().equals("id")) { //$NON-NLS-1$
                    model.setId(nodeValue);
                } else if (node.getNodeName().equals("invalid_records")) { //$NON-NLS-1$
                    model.setInvalidRecords(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                } else if (node.getNodeName().equals("processed_records")) { //$NON-NLS-1$
                    model.setProcessedRecords(nodeValue == null ? 0 : (int) Double.parseDouble(nodeValue));
                } else if (node.getNodeName().equals("start_date")) { //$NON-NLS-1$
                    model.setStartDate(nodeValue == null ? null : DateTimeFormat.getFormat(pattern).parse(nodeValue));
                } else if (node.getNodeName().equals("total_record")) { //$NON-NLS-1$
                    model.setTotalRecord(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                }
            }
        }
        return model;
    }

    public static StagingAreaExecutionModel response2StagingAreaExecutionModel(Response response) throws IOException {
        if (response == null || response.getEntity() == null || response.getEntity() instanceof EmptyRepresentation) {
            return null;
        }

        StagingAreaExecutionModel model = new StagingAreaExecutionModel();
        // Get the representation as an XmlRepresentation
        DomRepresentation rep = new DomRepresentation(response.getEntity());
        // Loop on the nodes to retrieve the node names and text content.
        NodeList nodes = rep.getDocument().getDocumentElement().getChildNodes();
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz"; //$NON-NLS-1$
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                String nodeValue = node.getFirstChild().getNodeValue();
                if (node.getNodeName().equals("end_date")) { //$NON-NLS-1$
                    model.setEndDate(nodeValue == null ? null : DateTimeFormat.getFormat(pattern).parse(nodeValue));
                } else if (node.getNodeName().equals("id")) { //$NON-NLS-1$
                    model.setId(nodeValue);
                } else if (node.getNodeName().equals("invalid_records")) { //$NON-NLS-1$
                    model.setInvalidRecords(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                } else if (node.getNodeName().equals("processed_records")) { //$NON-NLS-1$
                    model.setProcessedRecords(nodeValue == null ? 0 : (int) Double.parseDouble(nodeValue));// FIXME why
                                                                                                           // mock data
                                                                                                           // is double
                } else if (node.getNodeName().equals("start_date")) { //$NON-NLS-1$
                    model.setStartDate(nodeValue == null ? null : DateTimeFormat.getFormat(pattern).parse(nodeValue));
                } else if (node.getNodeName().equals("total_record")) { //$NON-NLS-1$
                    model.setTotalRecord(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                }
            }
        }
        return model;
    }

}

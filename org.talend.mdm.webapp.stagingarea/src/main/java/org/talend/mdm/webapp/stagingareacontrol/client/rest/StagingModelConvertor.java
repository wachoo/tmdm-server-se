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
package org.talend.mdm.webapp.stagingareacontrol.client.rest;

import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaValidationModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class StagingModelConvertor {

    public static StagingContainerModel convertNodeListToStagingContainerModel(NodeList nodeList) {
        StagingContainerModel stagingContainerModel = null;
        if (nodeList != null) {
            stagingContainerModel = new StagingContainerModel();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
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

    public static StagingAreaValidationModel convertNodeListToStagingAreaValidationModel(NodeList nodeList) {
        StagingAreaValidationModel stagingAreaValidationModel = null;
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz"; //$NON-NLS-1$
        if (nodeList != null) {
            stagingAreaValidationModel = new StagingAreaValidationModel();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeValue = node.getFirstChild().getNodeValue();
                    if (node.getNodeName().equals("id")) { //$NON-NLS-1$
                        stagingAreaValidationModel.setId(nodeValue);
                    } else if (node.getNodeName().equals("invalid_records")) { //$NON-NLS-1$
                        stagingAreaValidationModel.setInvalidRecords(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                    } else if (node.getNodeName().equals("processed_records")) { //$NON-NLS-1$
                        stagingAreaValidationModel.setProcessedRecords(nodeValue == null ? 0 : (int) Double
                                .parseDouble(nodeValue));
                    } else if (node.getNodeName().equals("start_date")) { //$NON-NLS-1$
                        stagingAreaValidationModel.setStartDate(nodeValue == null ? null : DateTimeFormat.getFormat(pattern)
                                .parse(nodeValue));
                    } else if (node.getNodeName().equals("total_record")) { //$NON-NLS-1$
                        stagingAreaValidationModel.setTotalRecord(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                    }
                }
            }
        }

        return stagingAreaValidationModel;
    }

    public static StagingAreaExecutionModel convertNodeListToStagingAreaExecutionModel(NodeList nodeList) {
        StagingAreaExecutionModel stagingAreaExecutionModel = null;
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSz"; //$NON-NLS-1$
        if (nodeList != null) {
            stagingAreaExecutionModel = new StagingAreaExecutionModel();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeValue = node.getFirstChild().getNodeValue();
                    if (node.getNodeName().equals("end_date")) { //$NON-NLS-1$
                        stagingAreaExecutionModel.setEndDate(nodeValue == null ? null : DateTimeFormat.getFormat(pattern).parse(
                                nodeValue));
                    } else if (node.getNodeName().equals("id")) { //$NON-NLS-1$
                        stagingAreaExecutionModel.setId(nodeValue);
                    } else if (node.getNodeName().equals("invalid_records")) { //$NON-NLS-1$
                        stagingAreaExecutionModel.setInvalidRecords(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                    } else if (node.getNodeName().equals("processed_records")) { //$NON-NLS-1$
                        stagingAreaExecutionModel
                                .setProcessedRecords(nodeValue == null ? 0 : (int) Double.parseDouble(nodeValue));
                    } else if (node.getNodeName().equals("start_date")) { //$NON-NLS-1$
                        stagingAreaExecutionModel.setStartDate(nodeValue == null ? null : DateTimeFormat.getFormat(pattern)
                                .parse(nodeValue));
                    } else if (node.getNodeName().equals("total_record")) { //$NON-NLS-1$
                        stagingAreaExecutionModel.setTotalRecord(nodeValue == null ? 0 : Integer.parseInt(nodeValue));
                    }
                }
            }
        }
        return stagingAreaExecutionModel;
    }
}

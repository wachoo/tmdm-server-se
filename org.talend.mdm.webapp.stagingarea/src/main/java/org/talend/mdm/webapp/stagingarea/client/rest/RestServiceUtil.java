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
package org.talend.mdm.webapp.stagingarea.client.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.Uniform;
import org.restlet.client.data.MediaType;
import org.restlet.client.data.Method;
import org.restlet.client.ext.xml.DomRepresentation;
import org.restlet.client.resource.ClientResource;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingarea.client.model.StagingContainerModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class RestServiceUtil {

    public static final String BASE_URL = (GWT.isScript() ? GWT.getHostPageBaseURL().replaceAll("/general/secure", "") : //$NON-NLS-1$ //$NON-NLS-2$
            GWT.getHostPageBaseURL().replaceAll(GWT.getModuleName() + "/", "")) //$NON-NLS-1$ //$NON-NLS-2$
            + "datamanager/services/tasks/staging"; //$NON-NLS-1$
    
    /**
     * get default stagingContainerModel
     * 
     * @param callback
     */
    public static void getDefaultStagingContainerModel(final SessionAwareAsyncCallback<StagingContainerModel> callback) {
        ClientResource client = new ClientResource(Method.GET, BASE_URL);
        client.setOnResponse(new Uniform() {

            public void handle(Request request, Response response) {
                try {
                    callback.onSuccess(getStagingContainerModel(response));
                } catch (Exception e) {
                    callback.onFailure(e);
                }

            }
        });
        client.get(MediaType.TEXT_XML);
    }

    /**
     * get stagingContainerModel by dataModelUrl
     * 
     * @param dataModelUrl mock data /TestDataContainer/?model=TestDataModel
     * @param callback
     */
    public static void getStagingContainerModelByUrl(String dataModelUrl,
            final SessionAwareAsyncCallback<StagingContainerModel> callback) {
        // mock data dataModelUrl = /TestDataContainer/?model=TestDataModel
        ClientResource client = new ClientResource(Method.GET, BASE_URL + dataModelUrl);
        client.setOnResponse(new Uniform() {

            public void handle(Request request, Response response) {
                try {
                    callback.onSuccess(getStagingContainerModel(response));
                } catch (Exception e) {
                    callback.onFailure(e);
                }

            }
        });
        client.get(MediaType.TEXT_XML);
    }

    private static StagingContainerModel getStagingContainerModel(Response response) throws IOException {
        StagingContainerModel stagingContainerModel = new StagingContainerModel();
        DomRepresentation rep = new DomRepresentation(response.getEntity());
        NodeList list = rep.getDocument().getDocumentElement().getChildNodes();
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeValue = node.getFirstChild().getNodeValue();
                    if (node.getNodeName().equals("data_container")) { //$NON-NLS-1$
                        stagingContainerModel.setData_container(nodeValue);
                    } else if (node.getNodeName().equals("data_model")) { //$NON-NLS-1$
                        stagingContainerModel.setData_model(nodeValue);
                    } else if (node.getNodeName().equals("invalid_records")) { //$NON-NLS-1$
                        stagingContainerModel.setInvalid_records(Integer.parseInt(nodeValue));
                    } else if (node.getNodeName().equals("total_records")) { //$NON-NLS-1$
                        stagingContainerModel.setTotal_records(Integer.parseInt(nodeValue));
                    } else if (node.getNodeName().equals("valid_records")) { //$NON-NLS-1$
                        stagingContainerModel.setValid_records(Integer.parseInt(nodeValue));
                    } else if (node.getNodeName().equals("waiting_validation_records")) { //$NON-NLS-1$
                        stagingContainerModel.setWaiting_validation_records(Integer.parseInt(nodeValue));
                    }
                }

            }
        }
        return stagingContainerModel;
    }

    /**
     * get stagingAreaExecutionModels by paging parameter
     * 
     * @param start
     * @param pageSize
     * @param callback
     */
    public static void getStagingAreaExecutionModelsWithPaging(int start, int pageSize,
            final SessionAwareAsyncCallback<List<StagingAreaExecutionModel>> callback) {
        String url = BASE_URL + "/TestDataContainer/execs/?start=" + start + "&size=" + pageSize; //$NON-NLS-1$ //$NON-NLS-2$
        ClientResource client = new ClientResource(Method.GET, url);
        client.setOnResponse(new Uniform() {

            public void handle(Request request, Response response) {
                try {
                    final List<String> stagingIds = new ArrayList<String>();
                    DomRepresentation rep = new DomRepresentation(response.getEntity());
                    NodeList list = rep.getDocument().getDocumentElement().getChildNodes();
                    if (list != null) {
                        for (int i = 0; i < list.getLength(); i++) {
                            Node node = list.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE)
                                stagingIds.add(node.getFirstChild().getNodeValue());
                        }
                    }
                    final List<StagingAreaExecutionModel> models = new ArrayList<StagingAreaExecutionModel>();
                    for (String stagingId : stagingIds) {
                        getStagingAreaExecutionModel(stagingId, new SessionAwareAsyncCallback<StagingAreaExecutionModel>() {
                            public void onSuccess(StagingAreaExecutionModel result) {
                                models.add(result);
                                if (models.size() == stagingIds.size())
                                    callback.onSuccess(models);
                            }
                            protected void doOnFailure(Throwable caught) {
                                super.doOnFailure(caught);
                            }
                        });
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.get(MediaType.TEXT_XML);
    }

    /**
     * get stagingAreaExecutionModel by stagingId<br>
     * when stagingId=current, return current stagingAreaExecutionModel<br>
     * @param stagingId
     * @param callback
     */
    public static void getStagingAreaExecutionModel(String stagingId,
            final SessionAwareAsyncCallback<StagingAreaExecutionModel> callback) {
        String url = BASE_URL + "/TestDataContainer/execs/" + stagingId; //$NON-NLS-1$
        ClientResource client = new ClientResource(Method.GET, url);
        client.setOnResponse(new Uniform() {

            public void handle(Request request, Response response) {
                try {
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
                                model.setEnd_date(DateTimeFormat.getFormat(pattern).parse(nodeValue));
                            } else if (node.getNodeName().equals("id")) { //$NON-NLS-1$
                                model.setId(nodeValue);
                            } else if (node.getNodeName().equals("invalid_records")) { //$NON-NLS-1$
                                model.setRecord_left(Integer.parseInt(nodeValue));
                            } else if (node.getNodeName().equals("processed_records")) { //$NON-NLS-1$
                                model.setProcessed_records((int) Double.parseDouble(nodeValue));// TODO why mock data is
                                                                                                // double type
                            } else if (node.getNodeName().equals("start_date")) { //$NON-NLS-1$
                                model.setStart_date(DateTimeFormat.getFormat(pattern).parse(nodeValue));
                            } else if (node.getNodeName().equals("total_record")) { //$NON-NLS-1$
                                // model.s
                            }
                        }
                    }
                    callback.onSuccess(model);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.get(MediaType.TEXT_XML);
    }

    /**
     * Post StagingTask
     * 
     * @param dataModelUrl mock data /TestDataContainer/?model=TestDataModel
     * @param entity
     * @param callback
     */
    public static void postStagingTask(String dataModelUrl, Object entity, final SessionAwareAsyncCallback<Boolean> callback) {
        // mock data /TestDataContainer/?model=TestDataModel
        ClientResource client = new ClientResource(Method.POST, BASE_URL + dataModelUrl);
        client.setOnResponse(new Uniform() {

            public void handle(Request request, Response response) {
                try {
                    callback.onSuccess(response.getStatus().isSuccess());
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.post(entity, MediaType.TEXT_XML);
    }

    /**
     * Delete StagingTask
     * 
     * @param dataModelUrl mock data /TestDataContainer/execs/current
     * @param callback
     */
    public static void deleteStagingTask(String dataModelUrl, final SessionAwareAsyncCallback<Boolean> callback) {
        // mock data /TestDataContainer/execs/current
        ClientResource client = new ClientResource(Method.DELETE, BASE_URL + dataModelUrl);
        client.setOnResponse(new Uniform() {

            public void handle(Request request, Response response) {
                try {
                    callback.onSuccess(response.getStatus().isSuccess());
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.delete(MediaType.TEXT_XML);
    }

}

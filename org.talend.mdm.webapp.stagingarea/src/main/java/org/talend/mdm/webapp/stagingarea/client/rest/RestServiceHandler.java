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

import java.util.ArrayList;
import java.util.List;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.Method;
import org.restlet.client.ext.xml.DomRepresentation;
import org.restlet.client.representation.StringRepresentation;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaValidationModel;
import org.talend.mdm.webapp.stagingarea.client.model.StagingContainerModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class RestServiceHandler {

    private static final String SEPARATE = "/"; //$NON-NLS-1$

    public static final String BASE_URL = (GWT.isScript() ? GWT.getHostPageBaseURL().replaceAll("/general/secure", "") : //$NON-NLS-1$ //$NON-NLS-2$
            GWT.getHostPageBaseURL().replaceAll(GWT.getModuleName() + SEPARATE, "")) //$NON-NLS-1$
            + "datamanager/services/tasks/staging"; //$NON-NLS-1$

    private ClientResourceWrapper client;

    public RestServiceHandler() {
        client = new ClientResourceWrapper();
    }

    public void setClient(ClientResourceWrapper client) {
        this.client = client;
    }

    /**
     * Get default staging container summary
     * 
     * @param callback
     */
    public void getDefaultStagingContainerSummary(final SessionAwareAsyncCallback<StagingContainerModel> callback) {

        client.init(Method.GET, BASE_URL);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                try {
                    callback.onSuccess(StagingModelConvertor.response2StagingContainerModel(response));
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.request();
    }

    /**
     * Get staging container summary
     * 
     * @param dataContainer staging container
     * @param dataModel staging container metadata model
     * @param callback
     */
    public void getStagingContainerSummary(String dataContainer, String dataModel,
            final SessionAwareAsyncCallback<StagingContainerModel> callback) {

        if (dataContainer == null || dataModel == null)
            throw new IllegalArgumentException();

        StringBuilder uri = new StringBuilder().append(BASE_URL).append(SEPARATE).append(dataContainer).append(SEPARATE)
                .append("?model=").append(dataModel); //$NON-NLS-1$

        client.init(Method.GET, uri.toString());
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                try {
                    callback.onSuccess(StagingModelConvertor.response2StagingContainerModel(response));
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.request();

    }

    /**
     * Read list of executions, if start=-1 or size=-1 without paging
     * 
     * @param dataContainer
     * @param exeId
     * @param callback
     */
    public void getStagingAreaExecutionIds(String dataContainer, int start, int pageSize,
            final SessionAwareAsyncCallback<List<String>> callback) {

        String url = BASE_URL + "/" + dataContainer + "/execs/"; //$NON-NLS-1$ //$NON-NLS-2$
        if (start != -1 && pageSize != -1)
            url += ("?start=" + start + "&size=" + pageSize);//$NON-NLS-1$ //$NON-NLS-2$

        client.init(Method.GET, url);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                try {
                    final List<String> exeIds = new ArrayList<String>();
                    DomRepresentation rep = new DomRepresentation(response.getEntity());
                    NodeList list = rep.getDocument().getDocumentElement().getChildNodes();
                    if (list != null) {
                        for (int i = 0; i < list.getLength(); i++) {
                            Node node = list.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE)
                                exeIds.add(node.getFirstChild().getNodeValue());
                        }
                    }
                    callback.onSuccess(exeIds);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.request();

    }

    /**
     * Get execution details by Id
     * 
     * @param dataContainer
     * @param exeId
     * @param callback
     */
    public void getStagingAreaExecution(String dataContainer, String exeId,
            final SessionAwareAsyncCallback<StagingAreaExecutionModel> callback) {

        String url = BASE_URL + "/" + dataContainer + "/execs/" + exeId; //$NON-NLS-1$ //$NON-NLS-2$
        client.init(Method.GET, url);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                try {
                    StagingAreaExecutionModel model = StagingModelConvertor.response2StagingAreaExecutionModel(response);
                    callback.onSuccess(model);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.request();

    }

    /**
     * Get StagingArea execution models by paging parameter
     * 
     * @param dataContainer
     * @param start
     * @param pageSize
     * @param callback
     */
    public void getStagingAreaExecutionsWithPaging(final String dataContainer, int start, int pageSize,
            final SessionAwareAsyncCallback<List<StagingAreaExecutionModel>> callback) {

        String url = BASE_URL + "/" + dataContainer + "/execs/?start=" + start + "&size=" + pageSize; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        client.init(Method.GET, url);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                try {
                    final List<String> exeIds = new ArrayList<String>();
                    DomRepresentation rep = new DomRepresentation(response.getEntity());
                    NodeList list = rep.getDocument().getDocumentElement().getChildNodes();
                    if (list != null) {
                        for (int i = 0; i < list.getLength(); i++) {
                            Node node = list.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE)
                                exeIds.add(node.getFirstChild().getNodeValue());
                        }
                    }
                    final List<StagingAreaExecutionModel> models = new ArrayList<StagingAreaExecutionModel>();
                    for (String exeId : exeIds) {
                        getStagingAreaExecution(dataContainer, exeId, new SessionAwareAsyncCallback<StagingAreaExecutionModel>() {

                            public void onSuccess(StagingAreaExecutionModel result) {
                                models.add(result);
                                if (models.size() == exeIds.size())
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
        client.request();

    }

    /**
     * Get new validation task status
     * 
     * @param dataContainer
     * @param callback
     */
    public void getValidationTaskStatus(String dataContainer, final SessionAwareAsyncCallback<StagingAreaValidationModel> callback) {

        String url = BASE_URL + "/" + dataContainer + "/execs/current"; //$NON-NLS-1$ //$NON-NLS-2$
        client.init(Method.GET, url);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                try {
                    StagingAreaValidationModel model = StagingModelConvertor.response2StagingAreaValidationModel(response);
                    callback.onSuccess(model);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.request();

    }

    /**
     * Run a validation task
     * 
     * @param dataContainer
     * @param dataModel
     * @param entity
     * @param callback
     */
    public void runValidationTask(String dataContainer, String dataModel, Object entity,
            final SessionAwareAsyncCallback<String> callback) {

        if (dataContainer == null || dataModel == null)
            throw new IllegalArgumentException();

        StringBuilder uri = new StringBuilder().append(BASE_URL).append(SEPARATE).append(dataContainer).append(SEPARATE)
                .append("?model=").append(dataModel); //$NON-NLS-1$

        client.init(Method.POST, uri.toString());
        client.setPostEntity(entity);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {

                try {
                    String taskId = null;
                    if (response.getEntity() != null) {
                        StringRepresentation rep = (StringRepresentation) response.getEntity();
                        taskId = rep.getText();
                    }
                    callback.onSuccess(taskId);
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.request();

    }

    /**
     * Cancel current validation task
     * 
     * @param dataContainer
     * @param callback
     */
    public void cancelValidationTask(String dataContainer, final SessionAwareAsyncCallback<Boolean> callback) {

        String url = BASE_URL + "/" + dataContainer + "/execs/current"; //$NON-NLS-1$ //$NON-NLS-2$
        client.init(Method.DELETE, url);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                try {
                    callback.onSuccess(response.getStatus().isSuccess());
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
        client.request();

    }

}

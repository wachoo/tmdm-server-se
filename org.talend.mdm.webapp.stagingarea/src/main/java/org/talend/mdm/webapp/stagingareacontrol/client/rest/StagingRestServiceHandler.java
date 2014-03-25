// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.MediaType;
import org.restlet.client.data.Method;
import org.restlet.client.ext.xml.DomRepresentation;
import org.restlet.client.representation.InputRepresentation;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.rest.ClientResourceWrapper;
import org.talend.mdm.webapp.base.client.rest.ResourceSessionAwareCallbackHandler;
import org.talend.mdm.webapp.base.client.rest.RestServiceHelper;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaValidationModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class StagingRestServiceHandler {

    private final DateTimeFormat DEFAULT_DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss");//$NON-NLS-1$

    private String restServiceUrl = RestServiceHelper.BASE_URL + "core/services/tasks/staging"; //$NON-NLS-1$

    private ClientResourceWrapper client;

    private static StagingRestServiceHandler handler;

    private StagingRestServiceHandler() {
        client = new ClientResourceWrapper();
    }

    public static StagingRestServiceHandler get() {
        if (handler == null) {
            handler = new StagingRestServiceHandler();
        }
        return handler;
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
        client.init(Method.GET, restServiceUrl);
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                StagingContainerModel model = StagingModelConvertor.response2StagingContainerModel(response);
                callback.onSuccess(model);

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

        if (dataContainer == null || dataModel == null) {
            throw new IllegalArgumentException();
        }

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataContainer).append(RestServiceHelper.SEPARATOR);
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("model", dataModel); //$NON-NLS-1$

        client.init(Method.GET, uri.toString(), parameterMap);
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                StagingContainerModel model = StagingModelConvertor.response2StagingContainerModel(response);
                callback.onSuccess(model);

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

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataContainer).append("/execs/"); //$NON-NLS-1$
        Map<String, String> parameterMap = new HashMap<String, String>();
        if (start != -1 && pageSize != -1) {
            parameterMap.put("start", String.valueOf(start)); //$NON-NLS-1$
            parameterMap.put("size", String.valueOf(pageSize)); //$NON-NLS-1$
        }
        client.init(Method.GET, uri.toString(), parameterMap);
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                final List<String> exeIds = new ArrayList<String>();
                DomRepresentation rep = new DomRepresentation(response.getEntity());
                NodeList list = rep.getDocument().getDocumentElement().getChildNodes();
                if (list != null) {
                    for (int i = 0; i < list.getLength(); i++) {
                        Node node = list.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            exeIds.add(node.getFirstChild().getNodeValue());
                        }
                    }
                }
                callback.onSuccess(exeIds);

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

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataContainer).append("/execs/").append(exeId); //$NON-NLS-1$
        client.init(Method.GET, uri.toString());
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                StagingAreaExecutionModel model = StagingModelConvertor.response2StagingAreaExecutionModel(response);
                callback.onSuccess(model);

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
    public void getStagingAreaExecutionsWithPaging(final String dataContainer, int start, int pageSize, Date before,
            final SessionAwareAsyncCallback<List<StagingAreaExecutionModel>> callback) {

        // build URI
        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataContainer).append("/execs/"); //$NON-NLS-1$ 
        Map<String, String> parameterMap = new HashMap<String, String>();
        if (start != -1 && pageSize != -1) {
            parameterMap.put("start", String.valueOf(start)); //$NON-NLS-1$
            parameterMap.put("size", String.valueOf(pageSize)); //$NON-NLS-1$
        }
        if (before != null) {
            parameterMap.put("before", DEFAULT_DATE_FORMAT.format(before)); //$NON-NLS-1$
        }
        client.init(Method.GET, uri.toString(), parameterMap);
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                final Map<String, StagingAreaExecutionModel> exeIds = new LinkedHashMap<String, StagingAreaExecutionModel>();
                DomRepresentation rep = new DomRepresentation(response.getEntity());
                NodeList list = rep.getDocument().getDocumentElement().getChildNodes();
                if (list != null) {
                    for (int i = 0; i < list.getLength(); i++) {
                        Node node = list.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            exeIds.put(node.getFirstChild().getNodeValue(), null);
                        }
                    }
                }
                if (exeIds.size() == 0) {
                    callback.onSuccess(new ArrayList<StagingAreaExecutionModel>());
                    return;
                }
                final int[] counter = new int[1];
                for (final String exeId : exeIds.keySet()) {
                    getStagingAreaExecution(dataContainer, exeId, new SessionAwareAsyncCallback<StagingAreaExecutionModel>() {

                        @Override
                        public void onSuccess(StagingAreaExecutionModel result) {
                            exeIds.put(exeId, result);
                            counter[0]++;
                            if (counter[0] == exeIds.size()) {
                                callback.onSuccess(new ArrayList<StagingAreaExecutionModel>(exeIds.values()));
                            }
                        }

                        @Override
                        protected void doOnFailure(Throwable caught) {
                            counter[0]++;
                            alertStagingError(caught);
                        }
                    });
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

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataContainer).append("/execs/current"); //$NON-NLS-1$
        client.init(Method.GET, uri.toString());
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                StagingAreaValidationModel model = StagingModelConvertor.response2StagingAreaValidationModel(response);
                callback.onSuccess(model);

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

        if (dataContainer == null || dataModel == null) {
            throw new IllegalArgumentException();
        }

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataContainer).append(RestServiceHelper.SEPARATOR);
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("model", dataModel); //$NON-NLS-1$
        client.init(Method.POST, uri.toString(), parameterMap);
        client.setPostEntity(entity);
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                String taskId = null;
                if (response.getEntity() != null) {
                    InputRepresentation rep = (InputRepresentation) response.getEntity();
                    taskId = rep.getText();
                }
                callback.onSuccess(taskId);

            }
        });
        client.request(MediaType.APPLICATION_XML);

    }

    /**
     * Cancel current validation task
     * 
     * @param dataContainer
     * @param callback
     */
    public void cancelValidationTask(String dataContainer, final SessionAwareAsyncCallback<Boolean> callback) {

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataContainer).append("/execs/current"); //$NON-NLS-1$
        client.init(Method.DELETE, uri.toString());
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {
                try {
                    callback.onSuccess(response.getStatus().isSuccess());
                } catch (Exception e) {
                    alertStagingError(e);
                }
            }
        });
        client.request();

    }

    public void countStagingAreaExecutions(final String dataContainer, StagingAreaExecutionModel criteria,
            final SessionAwareAsyncCallback<Integer> callback) {

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataContainer).append("/execs/count"); //$NON-NLS-1$
        Map<String, String> parameterMap = new HashMap<String, String>();
        if (criteria != null && criteria.getStartDate() != null) {
            parameterMap.put("before", DEFAULT_DATE_FORMAT.format(criteria.getStartDate())); //$NON-NLS-1$
        }
        // do request
        client.init(Method.GET, uri.toString(), parameterMap);
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                int count = 0;
                if (response.getEntity() != null && response.getEntity().getText() != null) {
                    try {
                        count = Integer.parseInt(response.getEntity().getText());
                    } catch (NumberFormatException e) {
                        count = Integer.MAX_VALUE;
                    }
                }
                callback.onSuccess(new Integer(count));

            }
        });
        client.request(MediaType.TEXT_PLAIN);

    }
}

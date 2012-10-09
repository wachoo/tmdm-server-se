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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.MediaType;
import org.restlet.client.data.Method;
import org.restlet.client.ext.xml.DomRepresentation;
import org.restlet.client.representation.InputRepresentation;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.stagingareacontrol.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.stagingareacontrol.client.i18n.StagingareaMessages;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaValidationModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class RestServiceHandler {

    private static final String SEPARATE = "/"; //$NON-NLS-1$

    public static final String BASE_URL = (GWT.isScript() ? GWT.getHostPageBaseURL().replaceAll("/general/secure", "") : //$NON-NLS-1$ //$NON-NLS-2$
            GWT.getHostPageBaseURL().replaceAll(GWT.getModuleName() + SEPARATE, "")) //$NON-NLS-1$
            + "datamanager/services/tasks/staging"; //$NON-NLS-1$

    public static final DateTimeFormat DEFAULT_DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");//$NON-NLS-1$

    private ClientResourceWrapper client;

    private static RestServiceHandler handler;

    private StagingareaMessages messages = MessagesFactory.getMessages();

    private RestServiceHandler() {
        client = new ClientResourceWrapper();
    }

    public static RestServiceHandler get() {
        if (handler == null) {
            handler = new RestServiceHandler();
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

        client.init(Method.GET, BASE_URL);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                StagingContainerModel model;
                try {
                    model = StagingModelConvertor.response2StagingContainerModel(response);
                } catch (Exception e) {
                    MessageBox.alert(null, messages.rest_exception(), null);
                    return;
                }
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

        if (dataContainer == null || dataModel == null)
            throw new IllegalArgumentException();

        StringBuilder uri = new StringBuilder().append(BASE_URL).append(SEPARATE).append(dataContainer).append(SEPARATE)
                .append("?model=").append(dataModel); //$NON-NLS-1$

        client.init(Method.GET, uri.toString());
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                StagingContainerModel model;
                try {
                    model = StagingModelConvertor.response2StagingContainerModel(response);
                } catch (Exception e) {
                    MessageBox.alert(null, messages.rest_exception(), null);
                    return;
                }
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

        String url = BASE_URL + "/" + dataContainer + "/execs/"; //$NON-NLS-1$ //$NON-NLS-2$
        if (start != -1 && pageSize != -1)
            url += ("?start=" + start + "&size=" + pageSize);//$NON-NLS-1$ //$NON-NLS-2$

        client.init(Method.GET, url);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                final List<String> exeIds = new ArrayList<String>();
                try {
                    DomRepresentation rep = new DomRepresentation(response.getEntity());
                    NodeList list = rep.getDocument().getDocumentElement().getChildNodes();
                    if (list != null) {
                        for (int i = 0; i < list.getLength(); i++) {
                            Node node = list.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE)
                                exeIds.add(node.getFirstChild().getNodeValue());
                        }
                    }
                } catch (Exception e) {
                    MessageBox.alert(null, messages.rest_exception(), null);
                    return;
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

        String url = BASE_URL + "/" + dataContainer + "/execs/" + exeId; //$NON-NLS-1$ //$NON-NLS-2$
        client.init(Method.GET, url);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                StagingAreaExecutionModel model;
                try {
                    model = StagingModelConvertor.response2StagingAreaExecutionModel(response);
                } catch (Exception e) {
                    MessageBox.alert(null, messages.rest_exception(), null);
                    return;
                }
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
        StringBuilder uri = new StringBuilder(BASE_URL + SEPARATE + dataContainer + "/execs/");//$NON-NLS-1$
        StringBuilder parameters=new StringBuilder();
        if(start!=-1&&pageSize!=-1)
            parameters.append("start=").append(start).append("&size=").append(pageSize);//$NON-NLS-1$ //$NON-NLS-2$
        if(before!=null)
            parameters.append("&before=").append(DEFAULT_DATE_FORMAT.format(before));//$NON-NLS-1$
        if (parameters.length() > 0)
            uri.append("?").append(parameters.toString());//$NON-NLS-1$

        client.init(Method.GET, uri.toString());
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
                    MessageBox.alert(null, messages.rest_exception(), null);
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
                StagingAreaValidationModel model;
                try {
                    model = StagingModelConvertor.response2StagingAreaValidationModel(response);
                } catch (Exception e) {
                    MessageBox.alert(null, messages.rest_exception(), null);
                    return;
                }
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

        if (dataContainer == null || dataModel == null)
            throw new IllegalArgumentException();

        StringBuilder uri = new StringBuilder().append(BASE_URL).append(SEPARATE).append(dataContainer).append(SEPARATE)
                .append("?model=").append(dataModel); //$NON-NLS-1$

        client.init(Method.POST, uri.toString());
        client.setPostEntity(entity);
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                String taskId = null;
                try {
                    if (response.getEntity() != null) {
                        InputRepresentation rep = (InputRepresentation) response.getEntity();
                        taskId = rep.getText();
                    }

                } catch (Exception e) {
                    MessageBox.alert(null, messages.rest_exception(), null);
                    return;
                }
                callback.onSuccess(taskId);
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
                boolean isSuccess;
                try {
                    isSuccess = response.getStatus().isSuccess();
                } catch (Exception e) {
                    MessageBox.alert(null, messages.rest_exception(), null);
                    return;
                }
                callback.onSuccess(isSuccess);
            }
        });
        client.request();

    }
    
    public void countStagingAreaExecutions(final String dataContainer, StagingAreaExecutionModel criteria,
            final SessionAwareAsyncCallback<Integer> callback) {
                
        // build URI
        StringBuilder uri = new StringBuilder(BASE_URL + SEPARATE + dataContainer + "/execs/count");//$NON-NLS-1$
        StringBuilder parameters=new StringBuilder();
        if (criteria != null && criteria.getStartDate() != null)
            parameters.append("&before=").append(DEFAULT_DATE_FORMAT.format(criteria.getStartDate()));//$NON-NLS-1$
        if (parameters.length() > 0)
            uri.append("?").append(parameters.toString());//$NON-NLS-1$
        
        // do request
        client.init(Method.GET, uri.toString());
        client.setCallback(new ResourceCallbackHandler() {

            public void process(Request request, Response response) {
                int count = 0;
                try {
                    if (response.getEntity() != null && response.getEntity().getText() != null) {
                        try {
                            count = Integer.parseInt(response.getEntity().getText());
                        } catch (NumberFormatException e) {
                            count = Integer.MAX_VALUE;
                        }
                    }
                } catch (Exception e) {
                    MessageBox.alert(null, messages.rest_exception(), null);
                    return;
                }
                callback.onSuccess(new Integer(count));
            }
        });
        client.request(MediaType.TEXT_PLAIN);

    }

}

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

import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
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

    public static final DateTimeFormat DEFAULT_DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd'T'hh:mm:ss");//$NON-NLS-1$

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
                try {
                    StagingContainerModel model = StagingModelConvertor.response2StagingContainerModel(response);
                    callback.onSuccess(model);
                } catch (Exception e) {
                    alertStagingError(e);
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
                    StagingContainerModel model = StagingModelConvertor.response2StagingContainerModel(response);
                    callback.onSuccess(model);
                } catch (Exception e) {
                    alertStagingError(e);
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
                    alertStagingError(e);
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
                    alertStagingError(e);
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
                    if (exeIds.size() == 0) {
                        callback.onSuccess(models);
                        return;
                    }
                    for (String exeId : exeIds) {
                        getStagingAreaExecution(dataContainer, exeId, new SessionAwareAsyncCallback<StagingAreaExecutionModel>() {

                            public void onSuccess(StagingAreaExecutionModel result) {
                                models.add(result);
                                if (models.size() == exeIds.size())
                                    callback.onSuccess(models);
                            }

                            protected void doOnFailure(Throwable caught) {
                                alertStagingError(caught);
                            }
                        });
                    }
                } catch (Exception e) {
                    alertStagingError(e);
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
                    alertStagingError(e);
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
                        InputRepresentation rep = (InputRepresentation) response.getEntity();
                        taskId = rep.getText();
                    }
                    callback.onSuccess(taskId);
                } catch (Exception e) {
                    alertStagingError(e);
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
                    alertStagingError(e);
                }
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
                try {
                    int count = 0;
                    if (response.getEntity() != null && response.getEntity().getText() != null) {
                        try {
                            count = Integer.parseInt(response.getEntity().getText());
                        } catch (NumberFormatException e) {
                            count = Integer.MAX_VALUE;
                        }
                    }
                    callback.onSuccess(new Integer(count));
                } catch (Exception e) {
                    alertStagingError(e);
                }
            }
        });
        client.request(MediaType.TEXT_PLAIN);

    }

    private void alertStagingError(Throwable e) {
        String errorTitle = messages.staging_area_error();
        String errorDetail;
        if (e.getMessage() == null || e.getMessage().trim().length() == 0) {
            errorDetail = messages.staging_area_exception();
        } else {
            errorDetail = messages.staging_area_exception() + "</br>" + messages.underlying_cause() //$NON-NLS-1$
                    + "<div style='width:300px; height:80px; overflow:auto; margin-top: 5px; margin-left: 50px; border: dashed 1px #777777;'>" //$NON-NLS-1$
                    + Format.htmlEncode(e.getMessage()) + "</div>"; //$NON-NLS-1$
        }
        Dialog dialog = MessageBox.alert(errorTitle, errorDetail, null).getDialog();
        dialog.setWidth(400);
        dialog.center();
    }
}

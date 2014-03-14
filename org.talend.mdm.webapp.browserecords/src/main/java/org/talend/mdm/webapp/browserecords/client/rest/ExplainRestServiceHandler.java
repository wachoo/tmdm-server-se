// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.MediaType;
import org.restlet.client.data.Method;
import org.restlet.client.ext.json.JsonRepresentation;
import org.restlet.client.representation.StringRepresentation;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.rest.ClientResourceWrapper;
import org.talend.mdm.webapp.base.client.rest.ResourceSessionAwareCallbackHandler;
import org.talend.mdm.webapp.base.client.rest.RestServiceHelper;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class ExplainRestServiceHandler {

    private String restServiceUrl = RestServiceHelper.BASE_URL + "datamanager/services/tasks/matching/explain"; //$NON-NLS-1$

    final private String ROOT_NAME = "groups"; //$NON-NLS-1$

    final private String DISPLAY_NAME = "name"; //$NON-NLS-1$

    final private String VALUE_SEPARATOR = " : "; //$NON-NLS-1$

    private ClientResourceWrapper client;

    private static ExplainRestServiceHandler handler;

    private ExplainRestServiceHandler() {
        client = new ClientResourceWrapper();
    }

    public static ExplainRestServiceHandler get() {
        if (handler == null) {
            handler = new ExplainRestServiceHandler();
        }
        return handler;
    }

    public void setClient(ClientResourceWrapper client) {
        this.client = client;
    }

    public void explainGroupResult(String dataCluster, String concept, String groupId,
            final SessionAwareAsyncCallback<BaseTreeModel> callback) {
        if (dataCluster == null || dataCluster.isEmpty() || concept == null || concept.isEmpty() || groupId == null
                || groupId.isEmpty()) {
            throw new IllegalArgumentException();
        }
        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataCluster).append(RestServiceHelper.SEPARATOR)
                .append("groups").append(RestServiceHelper.SEPARATOR); //$NON-NLS-1$
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("type", concept); //$NON-NLS-1$
        parameterMap.put("group", groupId); //$NON-NLS-1$
        client.init(Method.GET, uri.toString(), parameterMap);
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {
                BaseTreeModel result = null;
                JsonRepresentation jsonRepresentation = RestServiceHelper.getJsonRepresentationFromResponse(response);
                if (jsonRepresentation != null) {
                    result = buildTreeModelFromJsonRepresentation(jsonRepresentation);
                }
                callback.onSuccess(result);
            }
        });
        client.request();
    }

    public void simulateMatch(String dataCluster, String concept, String ids,
            final SessionAwareAsyncCallback<BaseTreeModel> callback) {
        if (dataCluster == null || dataCluster.isEmpty() || concept == null || concept.isEmpty()) {
            throw new IllegalArgumentException();
        }
        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append(dataCluster).append(RestServiceHelper.SEPARATOR)
                .append("records").append(RestServiceHelper.SEPARATOR); //$NON-NLS-1$
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("type", concept); //$NON-NLS-1$

        client.init(Method.POST, uri.toString(), parameterMap);
        client.setPostEntity(new StringRepresentation(ids, MediaType.TEXT_PLAIN));
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {
                BaseTreeModel result = null;
                JsonRepresentation jsonRepresentation = RestServiceHelper.getJsonRepresentationFromResponse(response);
                if (jsonRepresentation != null) {
                    result = buildTreeModelFromJsonRepresentation(jsonRepresentation);
                }
                callback.onSuccess(result);
            }
        });
        client.request(MediaType.TEXT_PLAIN);
    }

    public void compareRecords(String dataModel, String concept, String recordXml,
            final SessionAwareAsyncCallback<BaseTreeModel> callback) {
        if (dataModel == null || dataModel.isEmpty() || concept == null || concept.isEmpty()) {
            throw new IllegalArgumentException();
        }
        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR);
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("model", dataModel); //$NON-NLS-1$
        parameterMap.put("type", concept); //$NON-NLS-1$
        client.init(Method.POST, uri.toString(), parameterMap);
        client.setPostEntity(new StringRepresentation(recordXml, MediaType.APPLICATION_XML));
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {
                BaseTreeModel result = null;
                JsonRepresentation jsonRepresentation = RestServiceHelper.getJsonRepresentationFromResponse(response);
                if (jsonRepresentation != null) {
                    result = buildTreeModelFromJsonRepresentation(jsonRepresentation);
                }
                callback.onSuccess(result);
            }
        });
        client.request(MediaType.APPLICATION_XML);
    }

    private BaseTreeModel buildTreeModelFromJsonRepresentation(JsonRepresentation representation) throws IOException {
        BaseTreeModel rootModel = new BaseTreeModel();
        rootModel.set(DISPLAY_NAME, ROOT_NAME);
        JSONObject jsonObject = representation.getJsonObject();
        JSONValue jsonValue = jsonObject.get(ROOT_NAME);
        retriveNode(jsonValue, rootModel);
        return rootModel;
    }

    private void retriveNode(JSONValue jsonValue, BaseTreeModel parent) {
        if (jsonValue.isArray() != null) {
            JSONArray array = jsonValue.isArray();
            for (int i = 0; i < array.size(); i++) {
                JSONValue value = array.get(i);
                String content = getValue(value);
                if (content != null) {
                    addChildNode(content, parent);
                }
                retriveNode(value, parent);
            }
        } else if (jsonValue.isObject() != null) {
            JSONObject object = jsonValue.isObject();
            Set<String> keySet = object.keySet();
            for (String key : keySet) {
                JSONValue value = object.get(key);
                String content = getValue(value);
                if (content != null) {
                    addChildNode(key + VALUE_SEPARATOR + content, parent);
                } else {
                    BaseTreeModel treeModel = addChildNode(key, parent);
                    retriveNode(value, treeModel);
                }
            }
        }
    }

    private BaseTreeModel addChildNode(String content, BaseTreeModel parent) {
        BaseTreeModel treeModel = new BaseTreeModel();
        treeModel.set(DISPLAY_NAME, content);
        parent.add(treeModel);
        return treeModel;
    }

    private String getValue(JSONValue jsonValue) {
        String value = null;
        if (jsonValue.isString() != null) {
            value = jsonValue.isString().toString().replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
        } else if (jsonValue.isBoolean() != null) {
            value = jsonValue.isBoolean().toString();
        } else if (jsonValue.isNumber() != null) {
            value = jsonValue.isNumber().toString();
        }
        return value;
    }
}

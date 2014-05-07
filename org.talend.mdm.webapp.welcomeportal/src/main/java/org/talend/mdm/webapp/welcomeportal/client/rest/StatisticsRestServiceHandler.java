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
package org.talend.mdm.webapp.welcomeportal.client.rest;

import org.restlet.client.Request;
import org.restlet.client.Response;
import org.restlet.client.data.MediaType;
import org.restlet.client.data.Method;
import org.restlet.client.ext.json.JsonRepresentation;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.rest.ClientResourceWrapper;
import org.talend.mdm.webapp.base.client.rest.ResourceSessionAwareCallbackHandler;
import org.talend.mdm.webapp.base.client.rest.RestServiceHelper;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;

/**
 * created by glzhou
 * 
 */
public class StatisticsRestServiceHandler {

    private String restServiceUrl = RestServiceHelper.BASE_URL + "datamanager/services/system/stats"; //$NON-NLS-1$

    private ClientResourceWrapper client;

    private static StatisticsRestServiceHandler handler;

    private StatisticsRestServiceHandler() {
        client = new ClientResourceWrapper();
    }

    public static StatisticsRestServiceHandler getInstance() {
        if (handler == null) {
            handler = new StatisticsRestServiceHandler();
        }
        return handler;
    }

    public void setClient(ClientResourceWrapper client) {
        this.client = client;
    }

    /**
     * Get entity count summary for a container
     * 
     * @param callback
     */
    public void getContainerDataStats(String dataContainer, final SessionAwareAsyncCallback<JSONArray> callback) {
        if (dataContainer == null) {
            throw new IllegalArgumentException("Data container required"); //$NON-NLS-1$
        }

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append("data").append(RestServiceHelper.SEPARATOR) //$NON-NLS-1$
                .append(dataContainer);
        client.init(Method.GET, uri.toString());
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                JsonRepresentation jsonRepresentation = RestServiceHelper.getJsonRepresentationFromResponse(response);
                if (jsonRepresentation != null) {
                    JSONValue jsonValue = jsonRepresentation.getJsonObject().get("data"); //$NON-NLS-1$
                    callback.onSuccess(jsonValue.isArray());
                }

            }
        });
        client.request(MediaType.APPLICATION_JSON);
    }

    public void getContainerJournalStats(String dataContainer, final SessionAwareAsyncCallback<JSONArray> callback) {
        if (dataContainer == null) {
            throw new IllegalArgumentException("Data container required"); //$NON-NLS-1$
        }

        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append("journal").append(RestServiceHelper.SEPARATOR) //$NON-NLS-1$
                .append(dataContainer);
        client.init(Method.GET, uri.toString());
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                JsonRepresentation jsonRepresentation = RestServiceHelper.getJsonRepresentationFromResponse(response);
                if (jsonRepresentation != null) {
                    JSONValue jsonValue = jsonRepresentation.getJsonObject().get("journal"); //$NON-NLS-1$
                    callback.onSuccess(jsonValue.isArray());
                }

            }
        });
        client.request(MediaType.APPLICATION_JSON);

    }

    public void getRoutingEventStats(final SessionAwareAsyncCallback<JSONArray> callback) {
        StringBuilder uri = new StringBuilder();
        uri.append(restServiceUrl).append(RestServiceHelper.SEPARATOR).append("events"); //$NON-NLS-1$
        client.init(Method.GET, uri.toString());
        client.setCallback(new ResourceSessionAwareCallbackHandler() {

            @Override
            public void doProcess(Request request, Response response) throws Exception {

                JsonRepresentation jsonRepresentation = RestServiceHelper.getJsonRepresentationFromResponse(response);
                if (jsonRepresentation != null) {
                    JSONValue jsonValue = jsonRepresentation.getJsonObject().get("events"); //$NON-NLS-1$
                    callback.onSuccess(jsonValue.isArray());
                }

            }
        });
        client.request(MediaType.APPLICATION_JSON);
        
    }
}

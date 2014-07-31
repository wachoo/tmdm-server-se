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
package org.talend.mdm.webapp.browserecords.client.rest;

import org.restlet.client.Response;
import org.restlet.client.data.MediaType;
import org.restlet.client.data.Method;
import org.restlet.client.ext.json.JsonRepresentation;
import org.talend.mdm.webapp.base.client.rest.ClientResourceWrapper;
import org.talend.mdm.webapp.base.client.rest.ResourceCallbackHandler;

import com.google.gwt.json.client.JSONParser;

public class ClientResourceMockWrapper extends ClientResourceWrapper {

    private ResourceCallbackHandler callbackHandler;

    public ClientResourceMockWrapper() {
        super();
    }

    @Override
    public void setCallback(ResourceCallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    @Override
    public void request(MediaType mediaType) {
        request();
    }

    @Override
    public void request() {

        Response response = new Response(null);
        if (method.equals(Method.GET) && uri.matches("^.+/datamanager/services/tasks/matching/explain/Product/groups/\\?.+$")) { //$NON-NLS-1$
            String jsonString = "{\"groups\":[{\"group\":[{\"result\":[{\"id\":\"b3c570ae-d988-4536-a92e-6f2a33834253\"},{\"confidence\":1},{\"related_ids\":[\"Id 4\",\"Id 3\",\"Id 2\",\"Id 1\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"N-ValueN-ValueN-ValueN-Value\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"Id 1\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Id 4\"},{\"field\":\"Name\"},{\"fieldValue\":\"N-Value1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"Id 4\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Id 1\"},{\"field\":\"Name\"},{\"fieldValue\":\"N-Value4\"},{\"value\":4},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Id 2\"},{\"field\":\"Name\"},{\"fieldValue\":\"N-ValueN-Value\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"Id 3\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Id 2\"},{\"field\":\"Name\"},{\"fieldValue\":\"N-Value3\"},{\"value\":3},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"Id 2\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Id 4\"},{\"field\":\"Name\"},{\"fieldValue\":\"N-ValueN-Value2\"},{\"value\":2},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Id 3\"},{\"field\":\"Name\"},{\"fieldValue\":\"N-Value\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]}]}]}]}"; //$NON-NLS-1$
            JsonRepresentation representation = new JsonRepresentation(MediaType.TEXT_XML, JSONParser.parse(jsonString));
            response.setEntity(representation);
        } else if (method.equals(Method.POST)
                && uri.matches("^.+/datamanager/services/tasks/matching/explain/Product/records/\\?.+$")) { //$NON-NLS-1$
            String jsonString = "{\"groups\":[{\"group\":[{\"result\":[{\"id\":\"null\"},{\"confidence\":1},{\"related_ids\":[\"Simulate2\",\"Simulate3\",\"Simulate1\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"111\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"Simulate1\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Simulate2\"},{\"field\":\"Name\"},{\"fieldValue\":\"1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"Simulate2\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Simulate1\"},{\"field\":\"Name\"},{\"fieldValue\":\"1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Simulate3\"},{\"field\":\"Name\"},{\"fieldValue\":\"1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"Simulate3\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"Simulate2\"},{\"field\":\"Name\"},{\"fieldValue\":\"1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]}]}]}]}"; //$NON-NLS-1$
            JsonRepresentation representation = new JsonRepresentation(MediaType.TEXT_XML, JSONParser.parse(jsonString));
            response.setEntity(representation);
        }
        this.callbackHandler.process(null, response);
    }
}

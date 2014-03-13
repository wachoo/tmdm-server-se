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
            String jsonString = "{\"groups\":[{\"group\":[{\"result\":[{\"id\":\"8\"},{\"confidence\":1},{\"related_ids\":[\"9\",\"8\",\"7\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"C\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"7\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"7\"},{\"field\":\"Name\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"8\"}]},{\"detail\":[{\"id\":\"9\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"7\"},{\"field\":\"Name\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]}]}]}]}"; //$NON-NLS-1$
            JsonRepresentation representation = new JsonRepresentation(MediaType.TEXT_XML, JSONParser.parse(jsonString));
            response.setEntity(representation);
        } else if (method.equals(Method.POST) && uri.matches("^.+/datamanager/services/tasks/matching/explain/\\?.+$")) { //$NON-NLS-1$
            String jsonString = "{\"groups\":[{\"group\":[{\"result\":[{\"id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"confidence\":1},{\"related_ids\":[\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"C\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"74849595-27be-4b48-9238-cca56c6e6658\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"17cd4337-6073-4778-a50d-748247249a99\"}]}]}]},{\"group\":[{\"result\":[{\"id\":\"74849595-27be-4b48-9238-cca56c6e6658\"},{\"confidence\":1},{\"related_ids\":[\"74849595-27be-4b48-9238-cca56c6e6658\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"B\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"74849595-27be-4b48-9238-cca56c6e6658\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"17cd4337-6073-4778-a50d-748247249a99\"}]}]}]},{\"group\":[{\"result\":[{\"id\":\"17cd4337-6073-4778-a50d-748247249a99\"},{\"confidence\":1},{\"related_ids\":[\"17cd4337-6073-4778-a50d-748247249a99\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"D\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"74849595-27be-4b48-9238-cca56c6e6658\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"11e18f27-2a8c-4ef8-b80f-5ba27d8fd103\"},{\"field\":\"Name\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]}]},{\"detail\":[{\"id\":\"17cd4337-6073-4778-a50d-748247249a99\"}]}]}]}]}"; //$NON-NLS-1$
            JsonRepresentation representation = new JsonRepresentation(MediaType.TEXT_XML, JSONParser.parse(jsonString));
            response.setEntity(representation);
        }
        this.callbackHandler.process(null, response);
    }

}

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
            String jsonString = "{\"groups\":[{\"group\":[{\"result\":[{\"id\":\"169190a3-fa04-4d50-9924-8cbdee4d12a2\"},{\"confidence\":1},{\"related_ids\":[\"3\",\"2\",\"1\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"G1G1G1\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"1\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"169190a3-fa04-4d50-9924-8cbdee4d12a2\"},{\"field\":\"Name\"},{\"fieldValue\":\"G1G1G1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"G1\"}]}]}]},{\"detail\":[{\"id\":\"2\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"169190a3-fa04-4d50-9924-8cbdee4d12a2\"},{\"field\":\"Name\"},{\"fieldValue\":\"G1G1G1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"G1\"}]}]}]},{\"detail\":[{\"id\":\"3\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"169190a3-fa04-4d50-9924-8cbdee4d12a2\"},{\"field\":\"Name\"},{\"fieldValue\":\"G1G1G1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"G1\"}]}]}]}]}]}]}"; //$NON-NLS-1$
            JsonRepresentation representation = new JsonRepresentation(MediaType.TEXT_XML, JSONParser.parse(jsonString));
            response.setEntity(representation);
        } else if (method.equals(Method.POST)
                && uri.matches("^.+/datamanager/services/tasks/matching/explain/Product/records/\\?.+$")) { //$NON-NLS-1$
            String jsonString = "{\"groups\":[{\"group\":[{\"result\":[{\"id\":\"7e370430-9a79-40ab-a15c-ac8f44bb5b13\"},{\"confidence\":1},{\"related_ids\":[\"G2\",\"G1\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"group1group1\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"G1\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"G4\"},{\"field\":\"Name\"},{\"fieldValue\":\"group2\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"G2\"},{\"field\":\"Name\"},{\"fieldValue\":\"group1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"group1\"}]}]}]},{\"detail\":[{\"id\":\"G2\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"G4\"},{\"field\":\"Name\"},{\"fieldValue\":\"group2\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"G1\"},{\"field\":\"Name\"},{\"fieldValue\":\"group1\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"G3\"},{\"field\":\"Name\"},{\"fieldValue\":\"group2group2\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"group1group1\"}]}]}]}]}]},{\"group\":[{\"result\":[{\"id\":\"027d186a-efa8-4cc5-b1f3-db90e3b6075b\"},{\"confidence\":1},{\"related_ids\":[\"G4\",\"G3\"]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"group2group2\"}]}]}]},{\"details\":[{\"detail\":[{\"id\":\"G4\"},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"G1\"},{\"field\":\"Name\"},{\"fieldValue\":\"group1\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"G2\"},{\"field\":\"Name\"},{\"fieldValue\":\"group1\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"G3\"},{\"field\":\"Name\"},{\"fieldValue\":\"group2\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"group2\"}]}]}]},{\"detail\":[{\"id\":\"G3\"},{\"match\":[{\"is_match\":true},{\"scores\":[{\"score\":[{\"pair_id\":\"G4\"},{\"field\":\"Name\"},{\"fieldValue\":\"group2\"},{\"value\":1},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"match\":[{\"is_match\":false},{\"scores\":[{\"score\":[{\"pair_id\":\"G2\"},{\"field\":\"Name\"},{\"fieldValue\":\"group1group1\"},{\"value\":0},{\"algorithm\":\"Exact\"},{\"threshold\":1}]}]}]},{\"values\":[{\"value\":[{\"field\":\"Name\"},{\"value\":\"group2group2\"}]}]}]}]}]}]}"; //$NON-NLS-1$
            JsonRepresentation representation = new JsonRepresentation(MediaType.TEXT_XML, JSONParser.parse(jsonString));
            response.setEntity(representation);
        }
        this.callbackHandler.process(null, response);
    }
}

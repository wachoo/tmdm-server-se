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
package org.talend.mdm.webapp.stagingareacontrol.client.view;

import org.restlet.client.Response;
import org.restlet.client.data.MediaType;
import org.restlet.client.data.Method;
import org.restlet.client.data.Status;
import org.restlet.client.engine.io.StringInputStream;
import org.restlet.client.ext.xml.DomRepresentation;
import org.restlet.client.representation.InputRepresentation;
import org.talend.mdm.webapp.base.client.rest.ClientResourceWrapper;
import org.talend.mdm.webapp.base.client.rest.ResourceCallbackHandler;

import com.google.gwt.xml.client.XMLParser;

@SuppressWarnings("nls")
public class ResourceMockWrapper extends ClientResourceWrapper {

    private ResourceCallbackHandler callbackHandler;

    private boolean runValidationTask = false;

    public ResourceMockWrapper() {
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

        if (method.equals(Method.GET) && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/\\?model=TestDataModel$")) {

            StringBuilder sb = new StringBuilder();
            sb.append("<staging> ");
            sb.append(" <data_container>TestDataContainer</data_container> ");
            sb.append(" <data_model>TestDataModel</data_model> ");
            sb.append(" <invalid_records>1000</invalid_records> ");
            sb.append(" <total_records>10000</total_records> ");
            sb.append(" <valid_records>8000</valid_records> ");
            sb.append(" <waiting_validation_records>1000</waiting_validation_records> ");
            sb.append("</staging> ");
            String messageXml = sb.toString();
            DomRepresentation representation = new DomRepresentation(MediaType.TEXT_XML, XMLParser.parse(messageXml));
            response.setEntity(representation);
        } else if (method.equals(Method.GET) && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/execs/current\\?.+$")) {

            if (runValidationTask) {
                StringBuilder sb = new StringBuilder();
                sb.append("<execution> ");
                sb.append("<id>100b8d26-02d5-49ea-adad-bf56a6057be5</id> ");
                sb.append("<invalid_records>5</invalid_records> ");
                sb.append("<processed_records>10.0</processed_records> ");
                sb.append("<start_date>2012-08-09T11:59:40.183+08:00</start_date> ");
                sb.append("<total_record>10000</total_record> ");
                sb.append("</execution> ");
                String messageXml = sb.toString();
                DomRepresentation representation = new DomRepresentation(MediaType.TEXT_XML, XMLParser.parse(messageXml));
                response.setEntity(representation);
            } else {
                // String message = "0506effb-38e3-413d-994f-46cf017b7668";
                // StringInputStream stringStream = new StringInputStream(message);
                // InputRepresentation representation = new InputRepresentation(stringStream);
                response.setEntity(null);
            }

        } else if (method.equals(Method.POST)
                && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/\\?model=TestDataModel$")) {
            String message = "100b8d26-02d5-49ea-adad-bf56a6057be5";
            StringInputStream stringStream = new StringInputStream(message);
            InputRepresentation representation = new InputRepresentation(stringStream);
            response.setEntity(representation);
            runValidationTask = true;
        } else if (method.equals(Method.DELETE)
                && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/execs/current\\?.+$")) {
            response.setStatus(Status.SUCCESS_OK);
            runValidationTask = false;
        }

        callbackHandler.process(null, response);
    }
}

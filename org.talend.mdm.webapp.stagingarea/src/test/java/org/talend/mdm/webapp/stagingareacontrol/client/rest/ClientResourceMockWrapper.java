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
        if (method.equals(Method.GET) && uri.matches("^.+/core/services/tasks/staging$")) {

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

        } else if (method.equals(Method.GET) && uri.matches("^.+/core/services/tasks/staging/Product/\\?.+$")) {

            StringBuilder sb = new StringBuilder();
            sb.append("<staging> ");
            sb.append(" <data_container>Product</data_container> ");
            sb.append(" <data_model>Product</data_model> ");
            sb.append(" <invalid_records>800</invalid_records> ");
            sb.append(" <total_records>7000</total_records> ");
            sb.append(" <valid_records>5000</valid_records> ");
            sb.append(" <waiting_validation_records>1200</waiting_validation_records> ");
            sb.append("</staging> ");
            String messageXml = sb.toString();
            DomRepresentation representation = new DomRepresentation(MediaType.TEXT_XML, XMLParser.parse(messageXml));
            response.setEntity(representation);

        } else if (method.equals(Method.GET) && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/execs/(\\?.+)?$")) {

            String messageXml = "<executions><execution>fa011993-648f-48b3-9e4d-9c71de82f91a</execution><execution>4ad4e1c7-7769-45c1-90ad-16b54aa0262b</execution></executions>";
            DomRepresentation representation = new DomRepresentation(MediaType.TEXT_XML, XMLParser.parse(messageXml));
            response.setEntity(representation);

        } else if (method.equals(Method.GET)
                && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/execs/fa011993-648f-48b3-9e4d-9c71de82f91a$")) {

            StringBuilder sb = new StringBuilder();
            // sb.append("<?xml version=”1.0″ encoding=”UTF-8″ standalone=”yes”?> ");
            sb.append("<execution> ");
            sb.append(" <end_date>2012-08-02T11:20:07.188+02:00</end_date> ");
            sb.append(" <id>fa011993-648f-48b3-9e4d-9c71de82f91a</id> ");
            sb.append(" <invalid_records>973</invalid_records> ");
            sb.append(" <processed_records>772.0</processed_records> ");
            sb.append(" <start_date>2012-08-02T11:20:07.188+02:00</start_date> ");
            sb.append(" <total_record>772</total_record> ");
            sb.append("</execution> ");
            String messageXml = sb.toString();
            DomRepresentation representation = new DomRepresentation(MediaType.TEXT_XML, XMLParser.parse(messageXml));
            response.setEntity(representation);

        } else if (method.equals(Method.GET) && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/execs/current$")) {
            StringBuilder sb = new StringBuilder();
            sb.append("<execution> ");
            sb.append(" <id>1ad084c1-5f70-4b89-aeef-613e7e44f134</id> ");
            sb.append(" <invalid_records>5</invalid_records> ");
            sb.append(" <processed_records>10.0</processed_records> ");
            sb.append(" <start_date>2012-08-02T11:20:16.887+02:00</start_date> ");
            sb.append(" <total_record>10000</total_record> ");
            sb.append("</execution> ");
            String messageXml = sb.toString();
            DomRepresentation representation = new DomRepresentation(MediaType.TEXT_XML, XMLParser.parse(messageXml));
            response.setEntity(representation);

        } else if (method.equals(Method.POST) && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/\\?.+$")) {
            String message = "1ad084c1-5f70-4b89-aeef-613e7e44f134";
            StringInputStream stringStream = new StringInputStream(message);
            InputRepresentation representation = new InputRepresentation(stringStream);
            response.setEntity(representation);
        } else if (method.equals(Method.DELETE)
                && uri.matches("^.+/core/services/tasks/staging/TestDataContainer/execs/current$")) {
            response.setStatus(Status.SUCCESS_OK);
        }

        this.callbackHandler.process(null, response);
    }

}

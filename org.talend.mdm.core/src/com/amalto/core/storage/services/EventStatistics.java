/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.services;

import static com.amalto.core.query.user.UserQueryBuilder.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONWriter;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.amalto.core.query.user.Expression;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;

@Path("/system/stats/events")
public class EventStatistics {

    private static final Logger LOGGER = Logger.getLogger(EventStatistics.class);

    private final String CALLJOB_SERVICE_JNDI_NAME = "amalto/local/service/callJob"; //$NON-NLS-1$

    private final String CALLPROCESS_SERVICE_JNDI_NAME = "amalto/local/service/callprocess"; //$NON-NLS-1$

    private final String WORKFLOW_SERVICE_JNDI_NAME = "amalto/local/service/workflow"; //$NON-NLS-1$

    private final String WORKFLOWCONTEXT_SERVICE_JNDI_NAME = "amalto/local/service/workflowcontext"; //$NON-NLS-1$

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventStatistics() {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage system = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM, null);
        if (system == null) {
            LOGGER.debug("Could not find system storage. Statistics is not supported for XMLDB"); //$NON-NLS-1$
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        // Build statistics
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        MetadataRepository repository = system.getMetadataRepository();
        try {
            system.begin();
            writer.object().key("events"); //$NON-NLS-1$
            {
                writer.array();
                {
                    // Failed events
                    ComplexTypeMetadata failedRoutingOrder = repository.getComplexType("failed-routing-order-v2-pOJO"); //$NON-NLS-1$
                    writeTo(system, failedRoutingOrder, writer, "failed"); //$NON-NLS-1$
                    // Completed events
                    ComplexTypeMetadata completedRoutingOrder = repository.getComplexType("completed-routing-order-v2-pOJO"); //$NON-NLS-1$
                    writeTo(system, completedRoutingOrder, writer, "completed"); //$NON-NLS-1$
                }
                writer.endArray();
            }
            writer.endObject();
            system.commit();
        } catch (JSONException e) {
            system.rollback();
            throw new RuntimeException("Could not provide statistics.", e);
        }
        return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(stringWriter.toString())
                .header("Access-Control-Allow-Origin", "*").build(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void writeTo(Storage system, ComplexTypeMetadata routingOrderType, JSONWriter writer, String categoryName)
            throws JSONException {
        FieldMetadata parameters = routingOrderType.getField("service-parameters"); //$NON-NLS-1$
        FieldMetadata jndiNameField = routingOrderType.getField("service-jNDI"); //$NON-NLS-1$
        Expression routingNames = from(routingOrderType).select(alias(distinct(parameters), parameters.getName()))
                .select(jndiNameField).cache().getExpression();
        writer.object().key(categoryName);
        {
            writer.array();
            {
                StorageResults routingNameResults = system.fetch(routingNames);
                try {
                    XMLReader reader = XMLReaderFactory.createXMLReader();
                    for (DataRecord routingNameResult : routingNameResults) {
                        // Get the URL called by event
                        String parameter = String.valueOf(routingNameResult.get(parameters));
                        String jndiName = String.valueOf(routingNameResult.get(jndiNameField));
                        String key = null;
                        // TMDM-7324: handle different possible values.
                        if (jndiName.equals(CALLJOB_SERVICE_JNDI_NAME)) {
                            ParameterReader handler = new ParameterReader("url"); //$NON-NLS-1$
                            reader.setContentHandler(handler);
                            reader.parse(new InputSource(new StringReader(parameter)));
                            String url = handler.getParameterValue();
                            try {
                                key = (new URI(url)).getHost();
                            } catch (URISyntaxException e) {
                                LOGGER.warn("Could not get information from '" + url + "'", e);
                                key = url; // As fallback, put the whole parameter content.
                            }
                        } else if (jndiName.equals(CALLPROCESS_SERVICE_JNDI_NAME)) {
                            key = parameter.replace("process=", StringUtils.EMPTY); //$NON-NLS-1$
                        } else if (jndiName.equals(WORKFLOW_SERVICE_JNDI_NAME)
                                || jndiName.equals(WORKFLOWCONTEXT_SERVICE_JNDI_NAME)) {
                            ParameterReader handler = new ParameterReader("processId"); //$NON-NLS-1$
                            reader.setContentHandler(handler);
                            reader.parse(new InputSource(new StringReader(parameter)));
                            key = handler.getParameterValue();
                        }
                        if (key != null && !key.isEmpty()) {
                            // Count the number of similar events
                            Expression routingNameCount = from(routingOrderType).select(alias(count(), "count")) //$NON-NLS-1$
                                    .where(eq(parameters, parameter)).limit(1).cache().getExpression();
                            StorageResults failedCountResult = system.fetch(routingNameCount);
                            try {
                                // ... and write count to result
                                writer.object().key(key).value(failedCountResult.iterator().next().get("count")).endObject(); //$NON-NLS-1$
                            } finally {
                                failedCountResult.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Could not build event statistics for '" + categoryName + "' events", e);
                } finally {
                    routingNameResults.close();
                }
            }
            writer.endArray();
        }
        writer.endObject();
    }

    private static class ParameterReader extends DefaultHandler {

        private final String parameterName;

        private final StringWriter parameterValue;

        boolean accumulate;

        public ParameterReader(String parameterName) {
            this.parameterName = parameterName;
            this.parameterValue = new StringWriter();
            accumulate = false;
        }

        public String getParameterValue() {
            return parameterValue.toString();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (parameterName.equals(localName)) {
                accumulate = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (parameterName.equals(localName)) {
                accumulate = false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (accumulate) {
                for (int i = 0; i < length; i++) {
                    parameterValue.append(ch[start + i]);
                }
            }
        }
    }
}

/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.services;

import static com.amalto.core.query.user.UserQueryBuilder.*;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONWriter;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.query.user.Expression;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;

@Path("/system/stats/events")//$NON-NLS-1$
public class EventStatistics {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventStatistics() {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage system = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM, null);
        if (system == null) {
            throw new IllegalStateException("Could not find system storage.");
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
        FieldMetadata nameField = routingOrderType.getField("name"); //$NON-NLS-1$
        Expression routingNames = from(routingOrderType).select(distinct(nameField)).limit(1).cache().getExpression();
        writer.object().key(categoryName);
        {
            writer.array();
            {
                StorageResults routingNameResults = system.fetch(routingNames);
                try {
                    for (DataRecord routingNameResult : routingNameResults) {
                        String name = String.valueOf(routingNameResult.get(nameField));
                        Expression routingNameCount = from(routingOrderType).select(alias(count(), "count")) //$NON-NLS-1$
                                .where(eq(nameField, name)).limit(1).cache().getExpression();
                        StorageResults failedCountResult = system.fetch(routingNameCount);
                        try {
                            writer.object().key(name).value(failedCountResult.iterator().next().get("count")); //$NON-NLS-1$
                        } finally {
                            failedCountResult.close();
                        }
                    }
                } finally {
                    routingNameResults.close();
                }
            }
            writer.endArray();
        }
        writer.endObject();
    }
}

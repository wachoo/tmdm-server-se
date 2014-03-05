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
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONWriter;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.TimeSlicer;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;

@Path("/system/stats/journal")//$NON-NLS-1$
public class JournalStatistics {

    private static final int DEFAULT_SLICE_STEP = 1;

    private static final TimeUnit DEFAULT_SLICE_UNIT = TimeUnit.HOURS;

    private static void writeStatsTo(Storage storage, UserQueryBuilder query, String statName, JSONWriter writer)
            throws JSONException {
        Expression expression = query.getExpression();
        Iterator<TimeSlicer.Slice> slices = TimeSlicer.slice(expression, storage, DEFAULT_SLICE_STEP, DEFAULT_SLICE_UNIT);
        writer.array();
        {
            while (slices.hasNext()) {
                writer.object();
                {
                    TimeSlicer.Slice slice = slices.next();
                    writer.key("from").value(slice.getLowerBound()); //$NON-NLS-1$
                    writer.key("to").value(slice.getUpperBound()); //$NON-NLS-1$
                    StorageResults results = storage.fetch(slice.getExpression()); // Expects a live transaction here.
                    try {
                        writer.key(statName).value(results.iterator().next().get("count")); //$NON-NLS-1$
                    } finally {
                        results.close();
                    }
                }
                writer.endObject();
            }
        }
        writer.endArray();
    }

    @GET
    @Path("{container}")
    public String getJournalStatistics(@PathParam("container") String containerName) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage dataStorage = storageAdmin.get(containerName, StorageType.MASTER, null);
        if (dataStorage == null) {
            throw new IllegalArgumentException("Container '" + containerName + "' does not exist.");
        }
        Storage updateReportStorage = storageAdmin.get(XSystemObjects.DC_UPDATE_PREPORT.getName(), StorageType.MASTER, null);
        ComplexTypeMetadata updateType = updateReportStorage.getMetadataRepository().getComplexType("Update");//$NON-NLS-1$
        // Build statistics
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        MetadataRepository repository = dataStorage.getMetadataRepository();
        try {
            writer.object();
            {
                writer.array();
                {
                    for (ComplexTypeMetadata type : repository.getUserComplexTypes()) {
                        writer.object();
                        {
                            // Starts stats for type
                            writer.key(type.getName());
                            writer.array();
                            {
                                // Write create stats
                                writer.key("creations");
                                UserQueryBuilder createQuery = from(updateType).select(alias(count(), "count")) //$NON-NLS-1$
                                        .where(and(eq(updateType.getField("Type"), type.getName()), //$NON-NLS-1$
                                                eq(updateType.getField("action"), "update") //$NON-NLS-1$ //$NON-NLS-2$
                                        )).cache();
                                writeStatsTo(updateReportStorage, createQuery, "create", writer); //$NON-NLS-1$
                                // Write update stats
                                writer.key("updates");
                                UserQueryBuilder updateQuery = from(updateType).select(alias(count(), "count")) //$NON-NLS-1$
                                        .where(and(eq(updateType.getField("Type"), type.getName()), //$NON-NLS-1$
                                                eq(updateType.getField("action"), "update") //$NON-NLS-1$ //$NON-NLS-2$
                                        )).cache();
                                writeStatsTo(updateReportStorage, updateQuery, "update", writer); //$NON-NLS-1$
                            }
                            writer.endArray();
                        }
                        writer.endObject();
                    }
                }
                writer.endArray();
            }
            writer.endObject();
        } catch (JSONException e) {
            throw new RuntimeException("Could not provide statistics.", e);
        }
        return stringWriter.toString();
    }
}

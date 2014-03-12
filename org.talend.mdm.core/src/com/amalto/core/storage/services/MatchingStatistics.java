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
import static com.amalto.core.query.user.UserStagingQueryBuilder.groupSize;

import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.TypedExpression;
import com.amalto.core.storage.StagingStorage;
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

@Path("/system/stats/matching")//$NON-NLS-1$
public class MatchingStatistics {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{container}")
    public Response getMatchingStatistics(@PathParam("container")
    String containerName) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage dataStorage = storageAdmin.get(containerName, StorageType.STAGING, null);
        if (dataStorage == null) {
            throw new IllegalArgumentException("Container '" + containerName + "' does not exist.");
        }
        // Build statistics
        StringWriter stringWriter = new StringWriter();
        JSONWriter writer = new JSONWriter(stringWriter);
        MetadataRepository repository = dataStorage.getMetadataRepository();
        try {
            dataStorage.begin();
            writer.object().key("matching");
            {
                writer.array();
                {
                    for (ComplexTypeMetadata type : repository.getUserComplexTypes()) {
                        if ("TALEND_TASK_EXECUTION".equals(type.getName())) {
                            continue;
                        }
                        writer.object();
                        {
                            FieldMetadata keyField = type.getKeyFields().iterator().next();
                            Expression count = from(type)
                                    .select(alias(count(), "count")).where(gt(groupSize(), "2"))
                                    .where(eq(keyField, taskId()))
                                    .cache().getExpression(); //$NON-NLS-1$
                            StorageResults typeCount = dataStorage.fetch(count);
                            long countValue = 0;
                            for (DataRecord record : typeCount) {
                                countValue = (Long) record.get("count"); //$NON-NLS-1$
                            }
                            // Starts stats for type
                            writer.key(type.getName()).value(countValue);
                        }
                        writer.endObject();
                    }
                }
                writer.endArray();
            }
            writer.endObject();
            dataStorage.commit();
        } catch (JSONException e) {
            dataStorage.rollback();
            throw new RuntimeException("Could not provide statistics.", e);
        }
        return Response.ok().type(MediaType.APPLICATION_JSON_TYPE).entity(stringWriter.toString())
                        .header("Access-Control-Allow-Origin", "*").build(); //$NON-NLS-1$ //$NON-NLS-2$
    }
}

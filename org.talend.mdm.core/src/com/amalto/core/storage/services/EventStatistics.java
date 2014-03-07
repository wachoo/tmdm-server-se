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
import javax.ws.rs.PathParam;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONWriter;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
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
    public String getEventStatistics() {
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
            writer.object();
            {
                writer.array();
                {
                    for (ComplexTypeMetadata type : repository.getUserComplexTypes()) {
                        writer.object();
                        {
                            Expression count = from(type).select(alias(count(), "count")).cache().getExpression(); //$NON-NLS-1$
                            StorageResults typeCount = system.fetch(count);
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
        } catch (JSONException e) {
            throw new RuntimeException("Could not provide statistics.", e);
        }
        return stringWriter.toString();
    }
}

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

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.amalto.core.server.MetadataRepositoryAdmin;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.compare.Change;
import org.talend.mdm.commmon.metadata.compare.Compare;
import org.talend.mdm.commmon.metadata.compare.ImpactAnalyzer;

import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;

@Path("/system/models")//$NON-NLS-1$
public class SystemModels {

    private static final Logger LOGGER = Logger.getLogger(SystemModels.class);

    private final Storage systemStorage;

    private final ComplexTypeMetadata dataModelType;

    public SystemModels() {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        systemStorage = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM, null);
        MetadataRepository repository = systemStorage.getMetadataRepository();
        dataModelType = repository.getComplexType("data-model-pOJO"); //$NON-NLS-1$
    }

    @GET
    @Path("{model}")//$NON-NLS-1$
    public String getSchema(@PathParam("model")//$NON-NLS-1$
            String modelName) {
        UserQueryBuilder qb = from(dataModelType).where(eq(dataModelType.getField("name"), modelName)); //$NON-NLS-1$
        systemStorage.begin();
        try {
            StorageResults results = systemStorage.fetch(qb.getSelect());
            Iterator<DataRecord> iterator = results.iterator();
            String modelContent = StringUtils.EMPTY;
            if (iterator.hasNext()) {
                DataRecord model = iterator.next();
                if (iterator.hasNext()) {
                    throw new IllegalStateException("Found multiple data models for '" + modelName + "'.");
                }
                modelContent = String.valueOf(model.get("schema")); //$NON-NLS-1$
                return modelContent;
            }
            systemStorage.commit();
            return modelContent;
        } catch (Exception e) {
            systemStorage.rollback();
            throw new RuntimeException("Could not update data model.", e);
        }
    }

    @PUT
    @Path("{model}")//$NON-NLS-1$
    public void updateModel(@PathParam("model")//$NON-NLS-1$
            String modelName, @QueryParam("force")//$NON-NLS-1$
            boolean force, InputStream dataModel) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        MetadataRepositoryAdmin metadataRepositoryAdmin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
        Storage storage = storageAdmin.get(modelName, StorageType.MASTER, null);
        if (storage == null) {
            throw new IllegalArgumentException("Container '" + modelName + "' does not exist.");
        }
        String content;
        try {
            content = IOUtils.toString(dataModel, "UTF-8"); //$NON-NLS-1$
        } catch (IOException e) {
            throw new RuntimeException("Could not read data model from body.", e);
        }
        // Compare new data model with existing data model
        MetadataRepository previousRepository = storage.getMetadataRepository();
        MetadataRepository newRepository = new MetadataRepository();
        newRepository.load(new ByteArrayInputStream(content.getBytes()));
        // Ask the storage to adapt its structure following the changes
        storage.adapt(newRepository, force);
        // Update the system storage with the new data model (if there was any change).
        Compare.DiffResults diffResults = Compare.compare(previousRepository, newRepository);
        if (!diffResults.getActions().isEmpty()) {
            UserQueryBuilder qb = from(dataModelType).where(eq(dataModelType.getField("name"), modelName)); //$NON-NLS-1$
            systemStorage.begin();
            try {
                StorageResults results = systemStorage.fetch(qb.getSelect());
                Iterator<DataRecord> iterator = results.iterator();
                if (iterator.hasNext()) {
                    // Updates the data model in system db
                    DataRecord model = iterator.next();
                    model.set(dataModelType.getField("schema"), content); //$NON-NLS-1$
                    if (iterator.hasNext()) {
                        throw new IllegalStateException("Found multiple data models for '" + modelName + "'.");
                    }
                    systemStorage.update(model);
                    // Forces update in metadata repository admin
                    metadataRepositoryAdmin.remove(modelName);
                    metadataRepositoryAdmin.get(modelName);
                }
                systemStorage.commit();
            } catch (Exception e) {
                systemStorage.rollback();
                throw new RuntimeException("Could not update data model.", e);
            }
        }
    }

    @POST
    @Path("{model}")//$NON-NLS-1$
    public String analyzeModelChange(@PathParam("model")//$NON-NLS-1$
            String modelName, InputStream dataModel) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage storage = storageAdmin.get(modelName, StorageType.MASTER, null);
        if (storage == null) {
            LOGGER.warn("Container '" + modelName + "' does not exist. Skip impact analyzing for model change.");
            return StringUtils.EMPTY;
        }
        // Compare new data model with existing data model
        MetadataRepository previousRepository = storage.getMetadataRepository();
        MetadataRepository newRepository = new MetadataRepository();
        newRepository.load(dataModel);
        Compare.DiffResults diffResults = Compare.compare(previousRepository, newRepository);
        // Analyzes impacts on the select storage
        Map<ImpactAnalyzer.Impact, List<Change>> impacts = storage.getImpactAnalyzer().analyzeImpacts(diffResults);
        // Serialize results to XML
        StringWriter resultAsXml = new StringWriter();
        XMLStreamWriter writer = null;
        try {
            writer = XMLOutputFactory.newFactory().createXMLStreamWriter(resultAsXml);
            writer.writeStartElement("result"); //$NON-NLS-1$
            {
                for (Map.Entry<ImpactAnalyzer.Impact, List<Change>> category : impacts.entrySet()) {
                    writer.writeStartElement(category.getKey().name().toLowerCase());
                    List<Change> changes = category.getValue();
                    for (Change change : changes) {
                        writer.writeStartElement("change"); //$NON-NLS-1$
                        {
                            writer.writeStartElement("message"); //$NON-NLS-1$
                            {
                                writer.writeCharacters(change.getMessage());
                            }
                            writer.writeEndElement();
                        }
                        writer.writeEndElement();
                    }
                    writer.writeEndElement();
                }
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                } catch (XMLStreamException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Could not flush XML content.", e);
                    }
                }
            }
        }
        return resultAsXml.toString();
    }
}

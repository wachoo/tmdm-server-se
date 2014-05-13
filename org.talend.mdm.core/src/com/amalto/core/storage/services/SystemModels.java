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

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

import javax.ws.rs.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadataImpl;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.compare.Change;
import org.talend.mdm.commmon.metadata.compare.Compare;
import org.talend.mdm.commmon.metadata.compare.ImpactAnalyzer;

import com.amalto.commons.core.datamodel.synchronization.DMUpdateEvent;
import com.amalto.commons.core.datamodel.synchronization.DataModelChangeNotifier;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.SaverSession;
import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.util.XtentisException;

@Path("/system/models")
public class SystemModels {

    private static final Logger LOGGER = Logger.getLogger(SystemModels.class);

    private final Storage systemStorage;

    private final ComplexTypeMetadata dataModelType;

    public SystemModels() {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        systemStorage = storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, StorageType.SYSTEM, null);
        if (systemStorage != null) {
            MetadataRepository repository = systemStorage.getMetadataRepository();
            dataModelType = repository.getComplexType("data-model-pOJO"); //$NON-NLS-1$
        } else {
            LOGGER.warn("No system storage available."); //$NON-NLS-1$
            dataModelType = new ComplexTypeMetadataImpl(StringUtils.EMPTY, StringUtils.EMPTY, true);
        }
    }

    private static void reloadDataModel(String modelName) {
        // Invalidate data model from object cache
        ObjectPOJO.invalidateCache(null, DataModelPOJO.class,
                new DataModelPOJOPK(StringUtils.substringBeforeLast(modelName, "#"))); //$NON-NLS-1$
        // Force update in metadata repository admin
        MetadataRepositoryAdmin metadataRepositoryAdmin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
        metadataRepositoryAdmin.update(modelName);
        // Invalidate concept session
        SaverSession session = SaverSession.newSession();
        session.getSaverSource().invalidateTypeCache(modelName);
        session.end();
    }

    @GET
    @Path("{model}")//$NON-NLS-1$
    public String getSchema(@PathParam("model")//$NON-NLS-1$
            String modelName) {
        if (!isSystemStorageAvailable()) {
            return StringUtils.EMPTY;
        }
        UserQueryBuilder qb = from(dataModelType).where(eq(dataModelType.getField("name"), modelName)); //$NON-NLS-1$
        systemStorage.begin();
        try {
            StorageResults results = systemStorage.fetch(qb.getSelect());
            Iterator<DataRecord> iterator = results.iterator();
            String modelContent = StringUtils.EMPTY;
            if (iterator.hasNext()) {
                DataRecord model = iterator.next();
                if (iterator.hasNext()) {
                    throw new IllegalStateException("Found multiple data models for '" + modelName + "'."); //$NON-NLS-1$ //$NON-NLS-2$
                }
                modelContent = String.valueOf(model.get("schema")); //$NON-NLS-1$
                return modelContent;
            }
            systemStorage.commit();
            return modelContent;
        } catch (Exception e) {
            systemStorage.rollback();
            throw new RuntimeException("Could not update data model.", e); //$NON-NLS-1$
        }
    }

    @PUT
    @Path("{model}")//$NON-NLS-1$
    public void updateModel(@PathParam("model")//$NON-NLS-1$
            String modelName, @QueryParam("force")//$NON-NLS-1$
            boolean force, InputStream dataModel) {
        if (!isSystemStorageAvailable()) { // If no system storage is available, store new schema version.
            try {
                DataModelPOJO dataModelPOJO = new DataModelPOJO(modelName);
                dataModelPOJO.setSchema(IOUtils.toString(dataModel));
                dataModelPOJO.store();
                return;
            } catch (IOException e) {
                throw new RuntimeException("Could not fully read new data model.", e); //$NON-NLS-1$
            } catch (XtentisException e) {
                throw new RuntimeException("Could not store new data model.", e); //$NON-NLS-1$
            }
        }
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage storage = storageAdmin.get(modelName, StorageType.MASTER, null);
        if (storage == null) {
            throw new IllegalArgumentException("Container '" + modelName + "' does not exist."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Parses new data model version for comparison with existing
        MetadataRepository previousRepository = storage.getMetadataRepository();
        MetadataRepository newRepository = new MetadataRepository();
        String content;
        try {
            content = IOUtils.toString(dataModel, "UTF-8"); //$NON-NLS-1$
            newRepository.load(new ByteArrayInputStream(content.getBytes("UTF-8"))); //$NON-NLS-1$
        } catch (IOException e) {
            throw new RuntimeException("Could not read data model from body.", e); //$NON-NLS-1$
        }
        // Ask the storage to adapt its structure following the changes
        storage.adapt(newRepository, force);
        if (storageAdmin.supportStaging(modelName)) {
            Storage stagingStorage = storageAdmin.get(modelName, StorageType.STAGING, null);
            if (stagingStorage != null) {
                stagingStorage.adapt(newRepository, force);
            } else {
                LOGGER.warn("No SQL staging storage defined for data model '" + modelName //$NON-NLS-1$
                        + "'. No SQL staging storage to update."); //$NON-NLS-1$
            }
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Storage '" + modelName + "' does not support staging (forbidden by storage admin)."); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
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
                        throw new IllegalStateException("Found multiple data models for '" + modelName + "'."); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    systemStorage.update(model);
                }
                systemStorage.commit();
                reloadDataModel(modelName);
            } catch (Exception e) {
                systemStorage.rollback();
                throw new RuntimeException("Could not update data model.", e); //$NON-NLS-1$
            }
        }
        // synchronize with outer agents
        DataModelChangeNotifier dmUpdateEventNotifier = new DataModelChangeNotifier();
        dmUpdateEventNotifier.addUpdateMessage(new DMUpdateEvent(modelName, null));
        dmUpdateEventNotifier.sendMessages();
    }

    private boolean isSystemStorageAvailable() {
        return !dataModelType.getName().isEmpty();
    }

    @POST
    @Path("{model}")//$NON-NLS-1$
    public String analyzeModelChange(@PathParam("model")//$NON-NLS-1$
            String modelName, InputStream dataModel) {
        Map<ImpactAnalyzer.Impact, List<Change>> impacts;
        if (!isSystemStorageAvailable()) {
            impacts = new EnumMap<ImpactAnalyzer.Impact, List<Change>>(ImpactAnalyzer.Impact.class);
            for (ImpactAnalyzer.Impact impact : impacts.keySet()) {
                impacts.put(impact, Collections.<Change> emptyList());
            }
        } else {
            StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
            Storage storage = storageAdmin.get(modelName, StorageType.MASTER, null);
            if (storage == null) {
                LOGGER.warn("Container '" + modelName + "' does not exist. Skip impact analyzing for model change."); //$NON-NLS-1$//$NON-NLS-2$
                return StringUtils.EMPTY;
            }
            // Compare new data model with existing data model
            MetadataRepository previousRepository = storage.getMetadataRepository();
            MetadataRepository newRepository = new MetadataRepository();
            newRepository.load(dataModel);
            Compare.DiffResults diffResults = Compare.compare(previousRepository, newRepository);
            // Analyzes impacts on the select storage
            impacts = storage.getImpactAnalyzer().analyzeImpacts(diffResults);
        }
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
                        LOGGER.debug("Could not flush XML content.", e); //$NON-NLS-1$
                    }
                }
            }
        }
        return resultAsXml.toString();
    }
}

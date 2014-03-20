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

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.compare.Change;
import org.talend.mdm.commmon.metadata.compare.Compare;
import org.talend.mdm.commmon.metadata.compare.ImpactAnalyzer;

import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;

@Path("/system/models") //$NON-NLS-1$
public class SystemModels {

    private static final Logger LOGGER = Logger.getLogger(SystemModels.class);

    @GET
    @Path("{model}") //$NON-NLS-1$
    public String getSchema(@PathParam("model") //$NON-NLS-1$
    String modelName) {
        throw new UnsupportedOperationException("Get a data model content isn't currently supported.");
    }

    @PUT
    @Path("{model}") //$NON-NLS-1$
    public void updateModel(@PathParam("model") //$NON-NLS-1$
    String modelName, @QueryParam("force")
    boolean force, InputStream dataModel) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage storage = storageAdmin.get(modelName, StorageType.MASTER, null);
        if (storage == null) {
            throw new IllegalArgumentException("Container '" + modelName + "' does not exist.");
        }
        // Compare new data model with existing data model
        MetadataRepository newRepository = new MetadataRepository();
        newRepository.load(dataModel);
        // Ask the storage to adapt its structure following the changes
        storage.adapt(newRepository, force);
    }

    @POST
    @Path("{model}") //$NON-NLS-1$
    public String analyzeModelChange(@PathParam("model") //$NON-NLS-1$
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

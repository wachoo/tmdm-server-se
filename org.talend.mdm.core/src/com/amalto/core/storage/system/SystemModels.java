/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.system;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.compare.Change;
import org.talend.mdm.commmon.metadata.compare.Compare;
import org.talend.mdm.commmon.metadata.compare.ImpactAnalyzer;

import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;

@Path("/system/models")
public class SystemModels {

    @GET
    @Path("{model}")
    public String getSchema(@PathParam("model")
    String modelName) {
        throw new UnsupportedOperationException("Get a data model content isn't currently supported.");
    }

    @PUT
    @Path("{model}")
    public void updateModel(@PathParam("model")
    String modelName, @QueryParam("force")
    boolean force, InputStream dataModel) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage storage = storageAdmin.get(modelName, StorageType.MASTER, null);
        if (storage == null) {
            throw new IllegalArgumentException("Container '" + modelName + "' does not exist.");
        }
        // Compare new data model with existing data model
        MetadataRepository previousRepository = storage.getMetadataRepository();
        MetadataRepository newRepository = new MetadataRepository();
        newRepository.load(dataModel);
        Compare.DiffResults diffResults = Compare.compare(previousRepository, newRepository);
        // Ask the storage to adapt its structure following the changes
        storage.adapt(diffResults, force);
    }

    @POST
    @Path("{model}")
    public String analyzeModelChange(@PathParam("model")
    String modelName, InputStream dataModel) {
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage storage = storageAdmin.get(modelName, StorageType.MASTER, null);
        if (storage == null) {
            throw new IllegalArgumentException("Container '" + modelName + "' does not exist.");
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
        try {
            XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(resultAsXml);
            writer.writeStartElement("result"); //$NON-NLS-1$
            {
                for (Map.Entry<ImpactAnalyzer.Impact, List<Change>> category : impacts.entrySet()) {
                    writer.writeStartElement(category.getKey().name().toLowerCase());
                    for (List<Change> changes : impacts.values()) {
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
                    }
                    writer.writeEndElement();
                }
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return resultAsXml.toString();
    }
}

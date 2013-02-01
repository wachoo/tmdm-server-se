/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage;

import com.amalto.core.metadata.ClassRepository;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.record.*;
import com.amalto.xmlserver.interfaces.XmlServerException;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

public class SystemStorageWrapper extends StorageWrapper {

    public static final String SYSTEM_PREFIX = "amaltoOBJECTS";

    public static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    public SystemStorageWrapper() {
        // Create "system" storage
        Server server = ServerContext.INSTANCE.get();
        server.getStorageAdmin().create(StorageAdmin.SYSTEM_STORAGE,
                StorageAdmin.SYSTEM_STORAGE,
                Storage.DEFAULT_DATA_SOURCE_NAME,
                null);
    }

    private ComplexTypeMetadata getType(String clusterName, Storage storage, String uniqueId) {
        MetadataRepository repository = storage.getMetadataRepository();
        if (clusterName.startsWith(SYSTEM_PREFIX)) {
            return repository.getComplexType(ClassRepository.format(clusterName.substring(SYSTEM_PREFIX.length()) + "POJO"));
        }
        if (XSystemObjects.DC_MDMITEMSTRASH.getName().equals(clusterName)) {
            return repository.getComplexType("dropped-item-pOJO"); //$NON-NLS-1$
        }
        // MIGRATION.completed.record
        return repository.getComplexType(getTypeName(uniqueId));
    }

    @Override
    protected Storage getStorage(String dataClusterName) {
        return storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, null);
    }

    @Override
    protected Storage getStorage(String dataClusterName, String revisionId) {
        return storageAdmin.get(StorageAdmin.SYSTEM_STORAGE, null);
    }

    @Override
    public long deleteCluster(String revisionID, String clusterName) throws XmlServerException {
        return 0;
    }

    @Override
    public String[] getAllClusters(String revisionID) throws XmlServerException {
        return new String[0];
    }

    @Override
    public long deleteAllClusters(String revisionID) throws XmlServerException {
        return 0;
    }

    @Override
    public long createCluster(String revisionID, String clusterName) throws XmlServerException {
        return 0;
    }

    @Override
    public boolean existCluster(String revision, String cluster) throws XmlServerException {
        return true;
    }

    @Override
    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException {
        Storage storage = getStorage(clusterName, revisionID);
        ComplexTypeMetadata type = getType(clusterName, storage, null);
        if (type != null) {
            FieldMetadata keyField = type.getKeyFields().get(0);
            UserQueryBuilder qb = from(type).select(keyField);
            StorageResults results = storage.fetch(qb.getSelect());
            try {
                String[] ids = new String[results.getCount()];
                int i = 0;
                for (DataRecord result : results) {
                    ids[i++] = String.valueOf(result.get(keyField));
                }
                return ids;
            } finally {
                results.close();
            }
        } else {
            return new String[0];
        }
    }

    @Override
    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            DataRecordReader<Element> reader = new XmlDOMDataRecordReader();
            Storage storage = getStorage(clusterName, revisionID);
            ComplexTypeMetadata type = getType(clusterName, storage, uniqueID);
            if (type == null) {
                return -1; // TODO
            }
            MetadataRepository repository = storage.getMetadataRepository();
            DataRecord record = reader.read(revisionID, repository, type, root);
            storage.update(record);
        }
        return System.currentTimeMillis() - start;
    }

    @Override
    public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            Storage storage = getStorage(dataClusterName);
            ComplexTypeMetadata type = getType(dataClusterName, storage, input.getPublicId());
            if (type == null) {
                return -1; // TODO
            }
            DataRecordReader<XmlSAXDataRecordReader.Input> reader = new XmlSAXDataRecordReader();
            XmlSAXDataRecordReader.Input readerInput = new XmlSAXDataRecordReader.Input(docReader, input);
            DataRecord record = reader.read(revisionId, storage.getMetadataRepository(), type, readerInput);
            storage.update(record);
        }
        return System.currentTimeMillis() - start;
    }

    @Override
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
        return putDocumentFromString(xmlString, uniqueID, clusterName, revisionID, null);
    }

    @Override
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID, String documentType) throws XmlServerException {
        try {
            InputSource source = new InputSource(new StringReader(xmlString));
            Document document = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().parse(source);
            return putDocumentFromDOM(document.getDocumentElement(), uniqueID, clusterName, revisionID);
        } catch (Exception e) {
            throw new XmlServerException(e);
        }
    }

    @Override
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException {
        return getDocumentAsString(revisionID, clusterName, uniqueID, "UTF-8");
    }

    @Override
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XmlServerException {
        if (encoding == null) {
            encoding = "UTF-8"; //$NON-NLS-1$
        }
        Storage storage = getStorage(clusterName);
        ComplexTypeMetadata type = getType(clusterName, storage, uniqueID);
        if (type == null) {
            return null; // TODO
        }
        boolean isUserFormat = uniqueID.indexOf('.') > 0;
        String documentUniqueId = isUserFormat ? StringUtils.substringAfterLast(uniqueID, ".") : uniqueID;
        UserQueryBuilder qb = from(type).where(eq(type.getKeyFields().get(0), documentUniqueId));
        StorageResults records = storage.fetch(qb.getSelect());
        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
        try {
            Iterator<DataRecord> iterator = records.iterator();
            // Enforce root element name in case query returned instance of a subtype.
            DataRecordWriter dataRecordXmlWriter = isUserFormat ?  new DataRecordXmlWriter(type) : new SystemDataRecordXmlWriter((ClassRepository) storage.getMetadataRepository(), type);
            if (iterator.hasNext()) {
                DataRecord result = iterator.next();
                if (isUserFormat) {
                    String[] splitUniqueId = uniqueID.split("\\."); //$NON-NLS-1$
                    long timestamp = result.getRecordMetadata().getLastModificationTime();
                    String taskId = result.getRecordMetadata().getTaskId();
                    byte[] start = ("<ii><c>" + clusterName + "</c><dmn>" + clusterName + "</dmn><dmr/><sp/><t>" + timestamp + "</t><taskId>" + taskId + "</taskId><i>" + splitUniqueId[2] + "</i><p>").getBytes(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                    output.write(start);
                }
                dataRecordXmlWriter.write(result, output);
                if (iterator.hasNext()) {
                    throw new IllegalStateException("Expected only 1 result.");
                }
                if (isUserFormat) {
                    byte[] end = ("</p></ii>").getBytes(); //$NON-NLS-1$
                    output.write(end);
                }
                output.flush();
                return new String(output.toByteArray(), encoding);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new XmlServerException(e);
        } finally {
            records.close();
        }
    }

    @Override
    public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException {
        Storage storage = getStorage(clusterName);
        ComplexTypeMetadata type = getType(clusterName, storage, uniqueID);
        if (type == null) {
            return 0; // TODO
        }
        long start = System.currentTimeMillis();
        {
            UserQueryBuilder qb = from(type).where(eq(type.getKeyFields().get(0), uniqueID));
            storage.delete(qb.getSelect());
        }
        return System.currentTimeMillis() - start;
    }
}

/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.Server;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.record.*;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.XmlServerException;
import org.apache.commons.lang.NotImplementedException;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.*;

public class StorageWrapper implements IXmlServerSLWrapper {

    private static final DataRecordWriter WRITER = new DataRecordXmlWriter();

    private final StorageAdmin storageAdmin;

    private final MetadataRepositoryAdmin metadataRepositoryAdmin;

    public StorageWrapper() {
        Server server = ServerContext.INSTANCE.get();
        storageAdmin = server.getStorageAdmin();
        metadataRepositoryAdmin = server.getMetadataRepositoryAdmin();
    }

    private static Select getSelectTypeById(ComplexTypeMetadata type, String[] splitUniqueId) {
        UserQueryBuilder qb = UserQueryBuilder.from(type);
        List<FieldMetadata> keyFields = type.getKeyFields();

        if (splitUniqueId.length < (2 + keyFields.size())) {
            StringBuilder builder = new StringBuilder();
            for (String currentId : splitUniqueId) {
                builder.append(currentId).append('.');
            }
            throw new IllegalArgumentException("Id '" + builder.toString() + "' does not contain all required values for key of type '" + type.getName() + "'.");
        }

        int currentIndex = 2;
        for (FieldMetadata keyField : keyFields) {
            qb = qb.where(eq(keyField, splitUniqueId[currentIndex++]));
        }
        return qb.getSelect();
    }

    private static String getTypeName(String uniqueID) {
        char[] chars = uniqueID.toCharArray();
        StringBuilder typeName = new StringBuilder();
        boolean isType = false;
        for (char currentChar : chars) {
            if ('.' == currentChar) {
                if (isType) {
                    break;
                } else {
                    isType = true;
                }
            } else {
                if (isType) {
                    typeName.append(currentChar);
                }
            }
        }
        return typeName.toString();
    }

    public boolean isUpAndRunning() {
        return true;
    }

    public String[] getAllClusters(String revisionID) throws XmlServerException {
        return storageAdmin.getAll(revisionID);
    }

    public long deleteCluster(String revisionID, String clusterName) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            storageAdmin.delete(revisionID, clusterName);
            metadataRepositoryAdmin.remove(clusterName);
        }
        return System.currentTimeMillis() - start;
    }

    public long deleteAllClusters(String revisionID) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            storageAdmin.deleteAll(revisionID);
        }
        return System.currentTimeMillis() - start;
    }

    public long createCluster(String revisionID, String clusterName) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            storageAdmin.create(revisionID, clusterName, clusterName, Storage.DEFAULT_DATA_SOURCE_NAME);
        }
        return System.currentTimeMillis() - start;
    }

    public boolean existCluster(String revision, String cluster) throws XmlServerException {
        return storageAdmin.exist(revision, cluster);
    }

    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
        throw new NotImplementedException();
    }

    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID, String documentType) throws XmlServerException {
        throw new NotImplementedException();
    }

    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
        return putDocumentFromString(xmlString, uniqueID, clusterName, revisionID, null);
    }

    private final DataRecordReader<String> xmlStringReader = new XmlStringDataRecordReader();

    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID, String documentType) throws XmlServerException {
        String typeName = getTypeName(uniqueID);
        long start = System.currentTimeMillis();
        {
            DataRecord record = xmlStringReader.read(clusterName, parseRevisionId(revisionID), metadataRepositoryAdmin.get(clusterName).getComplexType(typeName), xmlString);

            Storage storage = storageAdmin.get(clusterName);
            try {
                storage.update(record);
            } catch (Exception e) {
                throw new XmlServerException(e);
            }
        }
        return System.currentTimeMillis() - start;
    }

    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
        String typeName = getTypeName(uniqueID);
        long start = System.currentTimeMillis();
        {
            DataRecordReader<Element> reader = new XmlDOMDataRecordReader();
            DataRecord record = reader.read(clusterName, parseRevisionId(revisionID), metadataRepositoryAdmin.get(clusterName).getComplexType(typeName), root);
            storageAdmin.get(clusterName).update(record);
        }
        return System.currentTimeMillis() - start;
    }

    private static long parseRevisionId(String revisionID) {
        // TODO Ensure support of all revision id format generated by MDM.
        if ("HEAD".equals(revisionID) || revisionID == null) {
            return 1;
        }
        return Long.parseLong(revisionID);
    }

    public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId) throws XmlServerException {
        String typeName = getTypeName(input.getPublicId());
        long start = System.currentTimeMillis();
        {
            DataRecordReader<XmlSAXDataRecordReader.Input> reader = new XmlSAXDataRecordReader();
            XmlSAXDataRecordReader.Input readerInput = new XmlSAXDataRecordReader.Input(docReader, input);
            MetadataRepository repository = metadataRepositoryAdmin.get(dataClusterName);
            DataRecord record = reader.read(dataClusterName, parseRevisionId(revisionId), repository.getComplexType(typeName), readerInput);
            Storage storage = storageAdmin.get(dataClusterName);
            if (storage == null) {
                throw new XmlServerException("Data cluster '" + dataClusterName + "' does not exist.");
            }
            storage.update(record);
        }
        return System.currentTimeMillis() - start;
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException {
        // TODO Web UI sends incomplete ids (in case of save of instance with auto increment). Fix caller code in IXtentisRMIPort.
        if (uniqueID.split("\\.").length < 3) {
            System.out.println("");
            return null;
        }
        return getDocumentAsString(revisionID, clusterName, uniqueID, "UTF-8");
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XmlServerException {
        if (encoding == null) {
            encoding = "UTF-8";
        }

        String[] splitUniqueId = uniqueID.split("\\.");

        MetadataRepository repository = metadataRepositoryAdmin.get(clusterName);
        String typeName = splitUniqueId[1];
        ComplexTypeMetadata type = repository.getComplexType(typeName);

        Select select = getSelectTypeById(type, splitUniqueId);
        Iterable<DataRecord> records = storageAdmin.get(clusterName).fetch(select);

        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);

        try {
            Iterator<DataRecord> iterator = records.iterator();
            if (iterator.hasNext()) {
                DataRecord result = iterator.next();
                long timestamp = result.getRecordMetadata().getLastModificationTime();
                String taskId = result.getRecordMetadata().getTaskId();
                byte[] start = ("<ii><c>" + clusterName + "</c><dmn>" + clusterName + "</dmn><dmr/><sp/><t>" + timestamp + "</t><taskId>" + taskId + "</taskId><i>" + splitUniqueId[2] + "</i><p>").getBytes();
                output.write(start);

                WRITER.write(result, output);
                if (iterator.hasNext()) {
                    throw new IllegalStateException("Expected only 1 result."); //$NON-NLS-1$
                }

                byte[] end = ("</p></ii>").getBytes();
                output.write(end);
                output.flush();

                return new String(output.toByteArray(), encoding);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new XmlServerException(e);
        }
    }

    public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException {
        try {
            return getDocumentAsString(revisionID, clusterName, uniqueID).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new XmlServerException(e);
        }
    }

    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    public long deleteDocument(String revisionID, String clusterName, final String uniqueID, String documentType) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            String typeName = getTypeName(uniqueID);
            String[] splitUniqueID = uniqueID.split("\\.");

            Storage storage = storageAdmin.get(clusterName);
            ComplexTypeMetadata type = metadataRepositoryAdmin.get(clusterName).getComplexType(typeName);
            Select select = getSelectTypeById(type, splitUniqueID);

            try {
                storage.begin();
                storage.delete(select);
                storage.commit();
            } catch (Exception e) {
                storage.rollback();
                throw new XmlServerException(e);
            } finally {
                storage.end();
            }
        }
        return System.currentTimeMillis() - start;
    }

    public int deleteXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName, String objectRootElementName, IWhereItem whereItem) throws XmlServerException {
        // Storage does not handle MDM internal data, thus supporting this method seems useless.
        throw new UnsupportedOperationException();
    }

    public int deleteItems(LinkedHashMap<String, String> conceptPatternsToRevisionID, LinkedHashMap<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem) throws XmlServerException {
        throw new NotImplementedException();
    }

    public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID, String targetclusterName) throws XmlServerException {
        throw new NotImplementedException();
    }

    public long countItems(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem) throws XmlServerException {
        throw new NotImplementedException();
    }

    public long countXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, IWhereItem whereItem) throws XmlServerException {
        // Storage does not handle MDM internal data, thus supporting this method seems useless.
        throw new UnsupportedOperationException();
    }

    public String getItemsQuery(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getItemsQuery(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit, boolean totalCountOnfirstRow, Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getXtentisObjectsQuery(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID, LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit, boolean totalCountOnfirstRow) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap<String, String[]> pivotWithKeys, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, String[] indexPaths, IWhereItem whereItem, String[] pivotDirections, String[] indexDirections, int start, int limit) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getChildrenItemsQuery(String clusterName, String conceptName, String[] PKXpaths, String FKXpath, String labelXpath, String fatherPK, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, IWhereItem whereItem, int start, int limit) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters, int start, int limit, boolean withTotalCount) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XmlServerException {
        String clusterName = criteria.getClusterName();
        MetadataRepository repository = metadataRepositoryAdmin.get(clusterName);
        Storage storage = storageAdmin.get(clusterName);

        int totalCount = 0;
        List<String> itemPKResults = new LinkedList<String>();
        String typeName = criteria.getConceptName();
        if (typeName != null) {
            totalCount += getTypeItems(criteria, itemPKResults, repository.getComplexType(typeName), storage);
        } else {
            Collection<ComplexTypeMetadata> types = repository.getUserComplexTypes();
            for (ComplexTypeMetadata type : types) {
                if (itemPKResults.size() < criteria.getMaxItems()) {
                    // TODO Lower skip as you iterate over types.
                    totalCount += getTypeItems(criteria, itemPKResults, type, storage);
                }
            }
        }
        itemPKResults.add(0, "<totalCount>" + totalCount + "</totalCount>");
        return itemPKResults;
    }

    private static int getTypeItems(ItemPKCriteria criteria, List<String> itemPKResults, ComplexTypeMetadata type, Storage storage) throws XmlServerException {
        // Build base query
        UserQueryBuilder qb = from(type)
                .select(alias(timestamp(), "timestamp"))
                .select(alias(taskId(), "taskid"))
                .selectId(type)
                .limit(criteria.getMaxItems())
                .start(criteria.getSkip());

        // TODO How does inheritance work in this case???
        // Filter by keys: expected format here: $EntityTypeName/Path/To/Field$[id_for_lookup]
        String keysKeywords = criteria.getKeysKeywords();
        if (keysKeywords != null && !keysKeywords.isEmpty()) {
            char[] chars = keysKeywords.toCharArray();
            StringBuilder path = new StringBuilder();
            StringBuilder id = new StringBuilder();
            StringBuilder idForLookup = new StringBuilder();
            int dollarCount = 0;
            int slashCount = 0;
            for (char current : chars) {
                switch (current) {
                    case '$':
                        dollarCount++;
                        break;
                    case '/':
                        slashCount++;
                    default:
                        if (dollarCount == 0) {
                            id.append(current);
                        } else if (dollarCount >= 2) {
                            idForLookup.append(current);
                        } else if (slashCount > 0) {
                            if (slashCount != 1 || '/' != current) {
                                path.append(current);
                            }
                        }
                }
            }
            if (path.toString().isEmpty() || idForLookup.toString().isEmpty()) {
                throw new IllegalArgumentException("Keys keyword argument '" + keysKeywords + "' did not match expected format $EntityTypeName/Path/To/Field$[id_for_lookup]");
            }
            qb.where(eq(type.getField(path.toString()), idForLookup.toString()));
            if (!id.toString().isEmpty()) {
                // TODO Implement compound key support
                qb.where(eq(type.getKeyFields().get(0), id.toString()));
            }
        }

        // Filter by timestamp
        if (criteria.getFromDate() > 0) {
            qb.where(gte(timestamp(), String.valueOf(criteria.getFromDate())));
        }
        if (criteria.getToDate() > 0) {
            qb.where(lte(timestamp(), String.valueOf(criteria.getToDate())));
        }

        // Content keywords
        String contentKeywords = criteria.getContentKeywords();
        if (contentKeywords != null) {
            if (!criteria.isCompoundKeyKeywords()) {
                List<FieldMetadata> keyFields = type.getKeyFields();
                if (keyFields.size() > 1) {
                    throw new IllegalArgumentException("Expected type '" + type.getName() + "' to contain only 1 key field.");
                }
                String uniqueKeyFieldName = keyFields.get(0).getName();
                qb.where(contains(type.getField(uniqueKeyFieldName), contentKeywords));
            } else {
                // TODO Implement compound key support
            }

            if (criteria.isUseFTSearch()) {
                qb.where(fullText(contentKeywords));
            }
        }

        StorageResults results = storage.fetch(qb.getSelect());
        DataRecordWriter writer = new ItemPKCriteriaResultsWriter(type.getName(), itemPKResults, type);
        for (DataRecord result : results) {
            try {
                writer.write(result, (OutputStream) null);
            } catch (IOException e) {
                throw new XmlServerException(e);
            }
        }
        return results.getCount();
    }

    public void clearCache() {
    }

    public boolean supportTransaction() {
        return true;
    }

    public void start(String dataClusterName) throws XmlServerException {
        Storage storage = storageAdmin.get(dataClusterName);
        storage.begin();
    }

    public void commit(String dataClusterName) throws XmlServerException {
        Storage storage = storageAdmin.get(dataClusterName);
        storage.commit();
    }

    public void rollback(String dataClusterName) throws XmlServerException {
        Storage storage = storageAdmin.get(dataClusterName);
        storage.rollback();
    }

    public void end(String dataClusterName) throws XmlServerException {
        Storage storage = storageAdmin.get(dataClusterName);
        storage.end();
    }

    public void close() throws XmlServerException {
        storageAdmin.close();
    }

    public List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws XmlServerException {
        MetadataRepository repository = metadataRepositoryAdmin.get(dataCluster);
        Iterator<ComplexTypeMetadata> types = repository.getUserComplexTypes().iterator();

        if (types.hasNext()) {
            UserQueryBuilder qb = from(types.next());
            while (types.hasNext()) {
                qb.and(types.next());
            }
            qb.where(fullText(keyword));
            qb.start(start);
            qb.limit(end);

            List<String> resultsAsXmlStrings = new LinkedList<String>();
            StorageResults results = storageAdmin.get(dataCluster).fetch(qb.getSelect());
            DataRecordWriter writer = new FullTextResultsWriter(keyword);
            try {
                resultsAsXmlStrings.add(String.valueOf(results.getCount()));
                for (DataRecord result : results) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    writer.write(result, output);
                    output.flush();
                    resultsAsXmlStrings.add(output.toString());
                }
                return resultsAsXmlStrings;
            } catch (IOException e) {
                throw new XmlServerException(e);
            }

        } else {
            return Collections.emptyList();
        }
    }

    public void exportDocuments(String revisionId, String clusterName, int start, int end, boolean includeMetadata, OutputStream outputStream) throws XmlServerException {
        // No support for bulk export when using SQL storages (this could be in HibernateStorage but would require to define new API).
        throw new NotImplementedException("No support for bulk export.");
    }

}

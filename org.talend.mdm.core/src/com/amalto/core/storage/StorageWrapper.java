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

import com.amalto.core.load.io.ResettableStringWriter;
import com.amalto.core.metadata.*;
import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.query.user.UserQueryHelper;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import com.amalto.core.storage.record.*;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.XmlServerException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
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

    private final DataRecordReader<String> xmlStringReader = new XmlStringDataRecordReader();

    private StorageAdmin storageAdmin;

    public StorageWrapper() {
    }

    private static Select getSelectTypeById(ComplexTypeMetadata type, String revisionId, String[] splitUniqueId) {
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
            qb.where(eq(keyField, splitUniqueId[currentIndex++]));
        }
        if (!"Update".equals(type.getName()) && !"TALEND_TASK_EXECUTION".equals(type.getName())) { // TODO Not good: add a method on type to tell whether type supports revisions or not.
            qb.where(eq(revision(), String.valueOf(parseRevisionId(revisionId))));
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
        return getStorageAdmin() != null;
    }

    public String[] getAllClusters(String revisionID) throws XmlServerException {
        return getStorageAdmin().getAll(revisionID);
    }

    public long deleteCluster(String revisionID, String clusterName) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            getStorageAdmin().delete(revisionID, clusterName);
        }
        return System.currentTimeMillis() - start;
    }

    public long deleteAllClusters(String revisionID) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            getStorageAdmin().deleteAll(revisionID);
        }
        return System.currentTimeMillis() - start;
    }

    public long createCluster(String revisionID, String clusterName) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            getStorageAdmin().create(clusterName, clusterName, Storage.DEFAULT_DATA_SOURCE_NAME);
        }
        return System.currentTimeMillis() - start;
    }

    public boolean existCluster(String revision, String cluster) throws XmlServerException {
        return getStorageAdmin().exist(revision, cluster);
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

    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID, String documentType) throws XmlServerException {
        String typeName = getTypeName(uniqueID);
        long start = System.currentTimeMillis();
        {
            Storage storage = getStorage(clusterName);
            MetadataRepository repository = storage.getMetadataRepository();
            DataRecord record = xmlStringReader.read(parseRevisionId(revisionID), repository, repository.getComplexType(typeName), xmlString);
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
            Storage storage = getStorage(clusterName);
            MetadataRepository repository = storage.getMetadataRepository();
            DataRecord record = reader.read(parseRevisionId(revisionID), repository, repository.getComplexType(typeName), root);
            storage.update(record);
        }
        return System.currentTimeMillis() - start;
    }

    private static long parseRevisionId(String revisionID) {
        if ("HEAD".equals(revisionID) || revisionID == null || revisionID.isEmpty()) { //$NON-NLS-1$
            return 1;
        }
        return Long.parseLong(revisionID);
    }

    public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId) throws XmlServerException {
        String typeName = getTypeName(input.getPublicId());
        long start = System.currentTimeMillis();
        {
            Storage storage = getStorage(dataClusterName);
            if (storage == null) {
                throw new XmlServerException("Data cluster '" + dataClusterName + "' does not exist.");
            }
            DataRecordReader<XmlSAXDataRecordReader.Input> reader = new XmlSAXDataRecordReader();
            XmlSAXDataRecordReader.Input readerInput = new XmlSAXDataRecordReader.Input(docReader, input);
            MetadataRepository repository = storage.getMetadataRepository();
            DataRecord record = reader.read(parseRevisionId(revisionId), repository, repository.getComplexType(typeName), readerInput);
            storage.update(record);
        }
        return System.currentTimeMillis() - start;
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException {
        // TODO Web UI sends incomplete ids (in case of save of instance with auto increment). Fix caller code in IXtentisRMIPort.
        if (uniqueID.split("\\.").length < 3) { //$NON-NLS-1$
            return null;
        }
        return getDocumentAsString(revisionID, clusterName, uniqueID, "UTF-8"); //$NON-NLS-1$
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XmlServerException {
        if (encoding == null) {
            encoding = "UTF-8"; //$NON-NLS-1$
        }

        String[] splitUniqueId = uniqueID.split("\\."); //$NON-NLS-1$

        Storage storage = getStorage(clusterName);
        MetadataRepository repository = storage.getMetadataRepository();
        String typeName = splitUniqueId[1];
        ComplexTypeMetadata type = repository.getComplexType(typeName);

        Select select = getSelectTypeById(type, revisionID, splitUniqueId);
        StorageResults records = storage.fetch(select);

        ByteArrayOutputStream output = new ByteArrayOutputStream(1024);

        try {
            Iterator<DataRecord> iterator = records.iterator();
            if (iterator.hasNext()) {
                DataRecord result = iterator.next();
                long timestamp = result.getRecordMetadata().getLastModificationTime();
                String taskId = result.getRecordMetadata().getTaskId();
                byte[] start = ("<ii><c>" + clusterName + "</c><dmn>" + clusterName + "</dmn><dmr/><sp/><t>" + timestamp + "</t><taskId>" + taskId + "</taskId><i>" + splitUniqueId[2] + "</i><p>").getBytes(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                output.write(start);

                WRITER.write(result, output);
                if (iterator.hasNext()) {
                    throw new IllegalStateException("Expected only 1 result.");
                }

                byte[] end = ("</p></ii>").getBytes(); //$NON-NLS-1$
                output.write(end);
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

    public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException {
        try {
            return getDocumentAsString(revisionID, clusterName, uniqueID).getBytes("UTF-8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            throw new XmlServerException(e);
        }
    }

    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException {
        List<String> uniqueIds = new LinkedList<String>();
        Storage storage = getStorage(clusterName);
        MetadataRepository repository = storage.getMetadataRepository();
        Collection<ComplexTypeMetadata> typeToQuery;
        if(clusterName.contains("/")) {
            String typeName = StringUtils.substringAfter(clusterName, "/");
            ComplexTypeMetadata complexType = repository.getComplexType(typeName);
            if (complexType == null) {
                throw new IllegalArgumentException("Type '" + typeName + "' does not exist in container '" + storage.getName() + "'");
            }
            typeToQuery = Collections.singletonList(complexType);
        } else {
            typeToQuery = MetadataUtils.sortTypes(repository);
        }
        for (ComplexTypeMetadata currentType : typeToQuery) {
            UserQueryBuilder qb = from(currentType).selectId(currentType);
            StorageResults results = storage.fetch(qb.getSelect());
            for (DataRecord result : results) {
                Iterator<FieldMetadata> setFields = result.getSetFields().iterator();
                StringBuilder builder = new StringBuilder();
                builder.append(clusterName).append('.').append(currentType.getName()).append('.');
                while (setFields.hasNext()) {
                    builder.append(String.valueOf(result.get(setFields.next())));
                    if (setFields.hasNext()) {
                        builder.append('.');
                    }
                }
                uniqueIds.add(builder.toString());
            }
        }
        return uniqueIds.toArray(new String[uniqueIds.size()]);
    }

    public long deleteDocument(String revisionID, String clusterName, final String uniqueID, String documentType) throws XmlServerException {
        long start = System.currentTimeMillis();
        {
            String typeName = getTypeName(uniqueID);
            String[] splitUniqueID = uniqueID.split("\\."); //$NON-NLS-1$
            Storage storage = getStorage(clusterName);
            ComplexTypeMetadata type = storage.getMetadataRepository().getComplexType(typeName);
            Select select = getSelectTypeById(type, revisionID, splitUniqueID);
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
        if (conceptName == null) {
            throw new IllegalArgumentException("Concept name can not be null.");
        }
        String clusterName = null;
        for (Map.Entry<String, String> entry : conceptPatternsToClusterName.entrySet()) {
            if (conceptName.matches(entry.getKey())) {
                clusterName = entry.getValue();
                break;
            }
        }
        if (clusterName == null) {
            throw new IllegalArgumentException("Could not find cluster name for concept '" + conceptName + "'.");
        }
        String revisionId = null;
        for (Map.Entry<String, String> entry : conceptPatternsToRevisionID.entrySet()) {
            if (conceptName.matches(entry.getKey())) {
                revisionId = entry.getValue();
                break;
            }
        }
        Storage storage = getStorage(clusterName);
        MetadataRepository repository = storage.getMetadataRepository();
        ComplexTypeMetadata type = repository.getComplexType(conceptName);
        if (type == null) {
            throw new IllegalArgumentException("Type '" + conceptName + "' does not exist in cluster '" + storage.getName() + "'.");
        }
        UserQueryBuilder qb = from(type);
        qb.where(UserQueryHelper.buildCondition(qb, whereItem, repository));
        qb.where(eq(revision(), String.valueOf(parseRevisionId(revisionId))));
        StorageResults records = storage.fetch(qb.getSelect());
        int count;
        try {
            count = records.getCount();
        } finally {
            records.close();
        }
        try {
            storage.begin();
            storage.delete(qb.getSelect());
            storage.commit();
            return count;
        } catch (Exception e) {
            storage.rollback();
            throw new XmlServerException(e);
        } finally {
            storage.end();
        }
    }

    public long moveDocumentById(String sourceRevisionID, String sourceClusterName, String uniqueID, String targetRevisionID, String targetClusterName) throws XmlServerException {
        throw new NotImplementedException();
    }

    public long countItems(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem) throws XmlServerException {
        if (conceptName == null) {
            throw new IllegalArgumentException("Concept name can not be null.");
        }
        String clusterName = null;
        for (Map.Entry<String, String> entry : conceptPatternsToClusterName.entrySet()) {
            if (conceptName.matches(entry.getKey())) {
                clusterName = entry.getValue();
                break;
            }
        }
        if (clusterName == null) {
            throw new IllegalArgumentException("Could not find cluster name for concept '" + conceptName + "'.");
        }
        String revisionId = null;
        for (Map.Entry<String, String> entry : conceptPatternsToRevisionID.entrySet()) {
            if (conceptName.matches(entry.getKey())) {
                revisionId = entry.getValue();
                break;
            }
        }
        Storage storage = getStorage(clusterName);
        MetadataRepository repository = storage.getMetadataRepository();
        UserQueryBuilder qb = from(repository.getComplexType(conceptName));
        UserQueryHelper.buildCondition(qb, whereItem, repository);
        qb.where(eq(revision(), String.valueOf(parseRevisionId(revisionId))));
        StorageResults results = storage.fetch(qb.getSelect());
        try {
            return results.getCount();
        } finally {
            results.close();
        }
    }

    public long countXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, IWhereItem whereItem) throws XmlServerException {
        // Storage does not handle MDM internal data, thus supporting this method seems useless.
        throw new UnsupportedOperationException();
    }

    public String getItemsQuery(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getItemsQuery(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit, boolean totalCountOnFirstRow, Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getXtentisObjectsQuery(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID, LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit, boolean totalCountOnFirstRow) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap<String, String[]> pivotWithKeys, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, String[] indexPaths, IWhereItem whereItem, String[] pivotDirections, String[] indexDirections, int start, int limit) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public String getChildrenItemsQuery(String clusterName, String conceptName, String[] PKXPaths, String FKXpath, String labelXpath, String fatherPK, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, IWhereItem whereItem, int start, int limit) throws XmlServerException {
        // Don't support query text stuff
        throw new UnsupportedOperationException();
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XmlServerException {
        return runQuery(revisionID, clusterName, query, parameters, 0, 0, false);
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters, int start, int limit, boolean withTotalCount) throws XmlServerException {
        Storage storage = storageAdmin.get(clusterName);
         // replace parameters in the procedure
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                String param = parameters[i];
                query = query.replaceAll("([^\\\\])%" + i + "([^\\d])", "$1" + param + "$2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }
        }
        UserQueryBuilder qb = from(query);
        StorageResults results = storage.fetch(qb.getExpression());
        ArrayList<String> resultsAsString = new ArrayList<String>(results.getSize() + 1);
        ResettableStringWriter writer = new ResettableStringWriter();
        DataRecordWriter xmlWriter = new DataRecordXmlWriter("result"); //$NON-NLS-1$
        try {
            for (DataRecord result : results) {
                xmlWriter.write(result, writer);
                resultsAsString.add(writer.toString());
                writer.reset();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create query results", e);
        }
        return resultsAsString;
    }

    public List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XmlServerException {
        String clusterName = criteria.getClusterName();
        Storage storage = getStorage(clusterName);
        MetadataRepository repository = storage.getMetadataRepository();

        int totalCount = 0;
        List<String> itemPKResults = new LinkedList<String>();
        String typeName = criteria.getConceptName();
        if (typeName != null) {
            // TODO: This is bad implementation (using parameter itemPKResults to get results).
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
        itemPKResults.add(0, "<totalCount>" + totalCount + "</totalCount>");  //$NON-NLS-1$ //$NON-NLS-2$
        return itemPKResults;
    }

    private static int getTypeItems(ItemPKCriteria criteria, List<String> itemPKResults, ComplexTypeMetadata type, Storage storage) throws XmlServerException {
        // Build base query
        UserQueryBuilder qb = from(type)
                .select(alias(timestamp(), "timestamp")) //$NON-NLS-1$
                .select(alias(taskId(), "taskid")) //$NON-NLS-1$
                .selectId(type)
                .limit(criteria.getMaxItems())
                .start(criteria.getSkip());

        // Filter by keys: expected format here: $EntityTypeName/Path/To/Field$[id_for_lookup]
        String keysKeywords = criteria.getKeysKeywords();
        if (keysKeywords != null && !keysKeywords.isEmpty()) {
            if (!criteria.isCompoundKeyKeywords()) {
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
                if (path.toString().isEmpty()) {
                    // TODO Implement compound key support
                    qb.where(eq(type.getKeyFields().get(0), id.toString()));
                } else {
                    qb.where(contains(type.getField(path.toString()), idForLookup.toString()));
                }
            } else {
                List<FieldMetadata> keyFields = type.getKeyFields();
                if (keyFields.size() > 1) {
                    throw new IllegalArgumentException("Expected type '" + type.getName() + "' to contain only 1 key field.");
                }
                String uniqueKeyFieldName = keyFields.get(0).getName();
                qb.where(contains(type.getField(uniqueKeyFieldName), keysKeywords));
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
            if (criteria.isUseFTSearch()) {
                qb.where(fullText(contentKeywords));
            } else {
                Condition condition = null;
                for (FieldMetadata field : type.getFields()) {
                    if (MetadataUtils.isValueAssignable(contentKeywords, field.getType().getName())) {
                        if (!(field instanceof ContainedTypeFieldMetadata)) {
                            if (condition == null) {
                                condition = contains(field, contentKeywords);
                            } else {
                                condition = or(condition, contains(field, contentKeywords));
                            }
                        }
                    }
                }
                qb.where(condition);
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
        Storage storage = getStorage(dataClusterName);
        storage.begin();
    }

    private StorageAdmin getStorageAdmin() {
        if (storageAdmin == null) {
            storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        }
        return storageAdmin;
    }

    public void commit(String dataClusterName) throws XmlServerException {
        Storage storage = getStorage(dataClusterName);
        storage.commit();
    }

    private Storage getStorage(String dataClusterName) {
        Storage storage = getStorageAdmin().get(dataClusterName);
        if (storage == null) {
            if (dataClusterName.contains("/")) { //$NON-NLS-1$
                String dataCluster = StringUtils.substringBefore(dataClusterName, "/"); //$NON-NLS-1$
                storage = getStorageAdmin().get(dataCluster);
                if (storage != null && storage.getDataSource() instanceof RDBMSDataSource) {
                    throw new IllegalStateException("'" + dataClusterName + "' did not match any existing storage, but '"
                            + dataCluster + "' did (and is not a SQL storage).");
                }
            }
            throw new IllegalStateException("Data container '" + dataClusterName + "' does not exist.");
        }
        return storage;
    }

    public void rollback(String dataClusterName) throws XmlServerException {
        Storage storage = getStorage(dataClusterName);
        storage.rollback();
    }

    public void end(String dataClusterName) throws XmlServerException {
        Storage storage = getStorage(dataClusterName);
        storage.end();
    }

    public void close() throws XmlServerException {
        getStorageAdmin().close();
    }

    public List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws XmlServerException {
        Storage storage = getStorage(dataCluster);
        MetadataRepository repository = storage.getMetadataRepository();
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
            StorageResults results = storage.fetch(qb.getSelect());
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

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

package com.amalto.core.server;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.XmlServerException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.OutputStream;
import java.util.*;

public class DefaultXmlServer implements XmlServer {

    private static final Logger LOG = Logger.getLogger(DefaultXmlServer.class);

    private static String SERVER_CLASS;

    static {
        SERVER_CLASS = MDMConfiguration.getConfiguration().getProperty("xmlserver.class"); //$NON-NLS-1$
        if ((SERVER_CLASS == null) || SERVER_CLASS.length() == 0) {
            SERVER_CLASS = "com.amalto.xmldb.XmldbSLWrapper"; //$NON-NLS-1$
        }
    }

    private final IXmlServerSLWrapper server;

    public DefaultXmlServer() {
        try {
            server = (IXmlServerSLWrapper) Class.forName(SERVER_CLASS).newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot find server class '" + SERVER_CLASS + "'.", e);
        }
    }

    public boolean isUpAndRunning() {
        if (LOG.isTraceEnabled()) {
            LOG.trace("isUpAndRunning() "); //$NON-NLS-1$
        }
        return server.isUpAndRunning();
    }

    public String[] getAllClusters(String revisionID) throws XtentisException {
        try {
            return server.getAllClusters(revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public void clearCache() throws XtentisException {
        server.clearCache();
    }

    public long deleteCluster(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.deleteCluster(revisionID, clusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long deleteAllclusterNames(String revisionID) throws XtentisException {
        try {
            return server.deleteAllClusters(revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long createCluster(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.createCluster(revisionID, clusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID) throws XtentisException {
        try {
            return server.putDocumentFromFile(fileName, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long putDocumentFromFile(
            String fileName,
            String uniqueID,
            String clusterName,
            String revisionID,
            String documentType
    ) throws XtentisException {
        try {
            return server.putDocumentFromFile(fileName, uniqueID, clusterName, revisionID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }


    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID) throws XtentisException {
        try {
            return server.putDocumentFromString(xmlString, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long putDocumentFromString(
            String string,
            String uniqueID,
            String clusterName,
            String revisionID,
            String documentType
    ) throws XtentisException {
        try {
            return server.putDocumentFromString(string, uniqueID, clusterName, revisionID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XtentisException {
        try {
            return server.putDocumentFromDOM(root, uniqueID, clusterName, revisionID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId) throws com.amalto.core.util.XtentisException {
        try {
            return server.putDocumentFromSAX(dataClusterName, docReader, input, revisionId);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XtentisException {
        try {
            return server.getDocumentAsString(revisionID, clusterName, uniqueID);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public boolean existCluster(String revision, String cluster) throws XtentisException {
        try {
            if (cluster.endsWith(StorageAdmin.STAGING_SUFFIX)) {
                cluster = StringUtils.substringBeforeLast(cluster, "#");
            }
            return server.existCluster(revision, cluster);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XtentisException {
        try {
            return server.getDocumentAsString(revisionID, clusterName, uniqueID, encoding);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType) throws XtentisException {
        try {
            return server.getDocumentBytes(revisionID, clusterName, uniqueID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XtentisException {
        try {
            return server.getAllDocumentsUniqueID(revisionID, clusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long deleteDocument(String revisionID, String clusterName, String uniqueID) throws XtentisException {
        return deleteDocument(revisionID, clusterName, uniqueID, IXmlServerSLWrapper.TYPE_DOCUMENT);
    }

    public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType) throws XtentisException {
        try {
            return server.deleteDocument(revisionID, clusterName, uniqueID, documentType);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public int deleteXtentisObjects(
            HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName,
            String objectRootElementName,
            IWhereItem whereItem
    ) throws XtentisException {
        try {
            return server.deleteXtentisObjects(objectRootElementNameToRevisionID, objectRootElementNameToClusterName, objectRootElementName, whereItem);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public int deleteItems(
            LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName,
            String conceptName,
            IWhereItem whereItem
    ) throws XtentisException {
        try {
            return server.deleteItems(conceptPatternsToRevisionID, conceptPatternsToClusterName, conceptName, whereItem);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID, String targetclusterName) throws XtentisException {
        try {
            return server.moveDocumentById(sourceRevisionID, sourceclusterName, uniqueID, targetRevisionID, targetclusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long countItems(
            LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName,
            String conceptName,
            IWhereItem whereItem
    ) throws XtentisException {
        try {
            return server.countItems(conceptPatternsToRevisionID, conceptPatternsToClusterName, conceptName, whereItem);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public long countXtentisObjects(
            HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName,
            String mainObjectRootElementName,
            IWhereItem whereItem
    ) throws XtentisException {
        try {
            return server.countXtentisObjects(objectRootElementNameToRevisionID, objectRootElementNameToClusterName, mainObjectRootElementName, whereItem);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XtentisException {
        try {
            return server.runQuery(revisionID, clusterName, query, parameters);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName,
                                      String query, String[] parameters, final int start, final int limit, final boolean withTotalCount)
            throws XtentisException {
        try {
            return server.runQuery(revisionID, clusterName, query, parameters, start, limit, withTotalCount);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XtentisException {
        try {
            return server.getItemPKsByCriteria(criteria);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public String getXtentisObjectsQuery(
            HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName,
            String mainObjectRootElementName,
            ArrayList<String> viewableObjectElements,
            IWhereItem whereItem,
            String orderBy,
            String direction,
            int start,
            int limit
    ) throws XtentisException {
        try {
            return server.getXtentisObjectsQuery(
                    objectRootElementNameToRevisionID,
                    objectRootElementNameToClusterName,
                    mainObjectRootElementName,
                    viewableObjectElements,
                    whereItem,
                    orderBy,
                    direction,
                    start,
                    limit
            );
        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Objects Query ", e);
        }
    }

    public String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID,
                                         LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
                                         ArrayList<String> viewableObjectElements, IWhereItem whereItem, String orderBy, String direction, int start,
                                         int limit, boolean withTotalCount) throws XtentisException {
        try {
            return server.getXtentisObjectsQuery(objectRootElementNameToRevisionID, objectRootElementNameToClusterName,
                    mainObjectRootElementName, viewableObjectElements, whereItem, orderBy, direction, start, limit,
                    withTotalCount);
        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Objects Query ", e);
        }
    }

    public String getItemsQuery(
            LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName,
            String forceMainPivot,
            ArrayList<String> viewableFullPaths,
            IWhereItem whereItem,
            String orderBy,
            String direction,
            int start,
            int limit,
            int spellThreshold
    ) throws XtentisException {
        try {
            String q = server.getItemsQuery(
                    conceptPatternsToRevisionID,
                    conceptPatternsToClusterName,
                    forceMainPivot,
                    viewableFullPaths,
                    whereItem,
                    orderBy,
                    direction,
                    start,
                    limit
            );
            if (LOG.isDebugEnabled())
                LOG.debug("getQuery():\n " + q); //$NON-NLS-1$
            return q;
        } catch (Exception e) {
            throw new XtentisException("Unable to build the query", e);
        }
    }

    public String getItemsQuery(
            LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName,
            String forceMainPivot,
            ArrayList<String> viewableFullPaths,
            IWhereItem whereItem,
            String orderBy,
            String direction,
            int start,
            int limit,
            int spellThreshold,
            boolean firstTotalCount,
            Map<String, ArrayList<String>> metaDataTypes
    ) throws XtentisException {
        try {
            String q = server.getItemsQuery(
                    conceptPatternsToRevisionID,
                    conceptPatternsToClusterName,
                    forceMainPivot,
                    viewableFullPaths,
                    whereItem,
                    orderBy,
                    direction,
                    start,
                    limit,
                    firstTotalCount,
                    metaDataTypes
            );
            if (LOG.isDebugEnabled()) {
                LOG.debug("getQuery():\n " + q); //$NON-NLS-1$
            }
            return q;
        } catch (Exception e) {
            throw new XtentisException("Unable to build the query", e);
        }
    }

    public String getPivotIndexQuery(
            String clusterName,
            String mainPivotName,
            LinkedHashMap<String, String[]> pivotWithKeys,
            LinkedHashMap<String, String> itemsRevisionIDs,
            String defaultRevisionID,
            String[] indexPaths,
            IWhereItem whereItem,
            String[] pivotDirections,
            String[] indexDirections,
            int start,
            int limit
    ) throws XtentisException {
        try {
            return server.getPivotIndexQuery(
                    clusterName,
                    mainPivotName,
                    pivotWithKeys,
                    itemsRevisionIDs,
                    defaultRevisionID,
                    indexPaths,
                    whereItem,
                    pivotDirections,
                    indexDirections,
                    start,
                    limit
            );
        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Objects Query", e);
        }
    }

    public String getChildrenItemsQuery(
            String clusterName,
            String conceptName,
            String[] PKXpaths,
            String FKXpath,
            String labelXpath,
            String fatherPK,
            LinkedHashMap<String, String> itemsRevisionIDs,
            String defaultRevisionID,
            IWhereItem whereItem,
            int start,
            int limit
    ) throws XtentisException {
        try {
            return server.getChildrenItemsQuery(
                    clusterName,
                    conceptName,
                    PKXpaths,
                    FKXpath,
                    labelXpath,
                    fatherPK,
                    itemsRevisionIDs,
                    defaultRevisionID,
                    whereItem,
                    start,
                    limit);

        } catch (XmlServerException e) {
            throw new XtentisException("Unable to get the Xtentis Items Query", e);
        }
    }

    public boolean supportTransaction() {
        return server.supportTransaction();
    }

    public void start(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.start(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public void commit(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.commit(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public void rollback(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.rollback(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public void end(String dataClusterName) throws com.amalto.core.util.XtentisException {
        try {
            server.end(dataClusterName);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public void close() throws com.amalto.core.util.XtentisException {
        try {
            server.close();
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws com.amalto.core.util.XtentisException {
        try {
            return server.globalSearch(dataCluster, keyword, start, end);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public void exportDocuments(String revisionId, String clusterName, int start, int end, boolean includeMetadata, OutputStream outputStream) throws com.amalto.core.util.XtentisException {
        try {
            server.exportDocuments(revisionId, clusterName, start, end, includeMetadata, outputStream);
        } catch (XmlServerException e) {
            throw new XtentisException(e);
        }
    }

    public boolean supportStaging(String dataCluster) {
        if (dataCluster == null || dataCluster.trim().length() == 0) {
            return false;
        }
        Server server = ServerContext.INSTANCE.get();
        Storage storage = server.getStorageAdmin().get(dataCluster, StorageType.STAGING, null);
        return storage != null;
    }
}

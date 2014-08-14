/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage;

import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.XmlServerException;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.OutputStream;
import java.util.*;

class NoOpWrapper implements IXmlServerSLWrapper{

    private static final Logger LOGGER = Logger.getLogger(NoOpWrapper.class);

    static {
        LOGGER.warn("--- Using No op wrapper! ---");
    }

    public boolean isUpAndRunning() {
        return true;
    }

    public String[] getAllClusters(String revisionID) throws XmlServerException {
        return new String[0];
    }

    public long deleteCluster(String revisionID, String clusterName) throws XmlServerException {
        return 0;  
    }

    public long deleteAllClusters(String revisionID) throws XmlServerException {
        return 0;  
    }

    public long createCluster(String revisionID, String clusterName) throws XmlServerException {
        return 0;  
    }

    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
        return 0;  
    }

    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID, String documentType) throws XmlServerException {
        return 0;  
    }

    public boolean existCluster(String revision, String cluster) throws XmlServerException {
        return true;
    }

    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
        return 0;  
    }

    public long putDocumentFromString(String string, String uniqueID, String clusterName, String revisionID, String documentType) throws XmlServerException {
        return 0;  
    }

    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XmlServerException {
        return 0;  
    }

    public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId) throws XmlServerException {
        return 0;  
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException {
        return "";
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XmlServerException {
        return "";
    }

    public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException {
        return new byte[0];
    }

    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException {
        return new String[0];
    }

    public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException {
        return 0;  
    }

    public int deleteXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName, String objectRootElementName, IWhereItem whereItem) throws XmlServerException {
        return 0;  
    }

    public int deleteItems(LinkedHashMap<String, String> conceptPatternsToRevisionID, LinkedHashMap<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem) throws XmlServerException {
        return 0;  
    }

    public long moveDocumentById(String sourceRevisionID, String sourceClusterName, String uniqueID, String targetRevisionID, String targetClusterName) throws XmlServerException {
        return 0;  
    }

    public long countItems(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem) throws XmlServerException {
        return 0;  
    }

    public long countXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, IWhereItem whereItem) throws XmlServerException {
        return 0;  
    }

    public String getItemsQuery(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit) throws XmlServerException {
        return null;  
    }

    public String getItemsQuery(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit, boolean totalCountOnFirstRow, Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        return null;  
    }

    public String getXtentisObjectsQuery(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit) throws XmlServerException {
        return null;  
    }

    public String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID, LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName, ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit, boolean totalCountOnFirstRow) throws XmlServerException {
        return null;  
    }

    public String getPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap<String, String[]> pivotWithKeys, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, String[] indexPaths, IWhereItem whereItem, String[] pivotDirections, String[] indexDirections, int start, int limit) throws XmlServerException {
        return null;  
    }

    public String getChildrenItemsQuery(String clusterName, String conceptName, String[] PKXPaths, String FKXpath, String labelXpath, String fatherPK, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, IWhereItem whereItem, int start, int limit) throws XmlServerException {
        return null;  
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XmlServerException {
        return null;  
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters, int start, int limit, boolean withTotalCount) throws XmlServerException {
        return null;  
    }

    public List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XmlServerException {
        return null;  
    }

    public void clearCache() {
        
    }

    public boolean supportTransaction() {
        return true;
    }

    public void start(String dataClusterName) throws XmlServerException {
        
    }

    public void commit(String dataClusterName) throws XmlServerException {
        
    }

    public void rollback(String dataClusterName) throws XmlServerException {
        
    }

    public void end(String dataClusterName) throws XmlServerException {
        
    }

    public void close() throws XmlServerException {
        
    }

    public List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws XmlServerException {
        return null;  
    }

    public void exportDocuments(String revisionId, String clusterName, int start, int end, boolean includeMetadata, OutputStream outputStream) throws XmlServerException {
        
    }
}

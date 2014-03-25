// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.xmlserver.interfaces;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * Xtentis performs all calls to the DataBase via this interface<br>
 * which must be implemented by supported DataBases
 * 
 * @author Bruno Grieder
 * 
 */
public interface IXmlServerSLWrapper {

    /** An XML document stored in the DB */
    public final static String TYPE_DOCUMENT = "DOCUMENT"; //$NON-NLS-1$
    
    /** A blob/bytes stored in the DB */
    public final static String TYPE_BINARY = "BINARY"; //$NON-NLS-1$
    
    /** Sort by ascending order in queries */
    public final static String ORDER_ASCENDING = "ascending"; //$NON-NLS-1$
    
    /** Sort by descending order in queries */
    public final static String ORDER_DESCENDING = "descending"; //$NON-NLS-1$

    
    /***************************************************************************
     * 
     * LIFECYCLE
     * 
     **************************************************************************/
    /**
     * Is the XML Database Server up?
     */
    public boolean isUpAndRunning();
    
    

    /***************************************************************************
     * 
     * C L U S T E R S
     * 
     **************************************************************************/

    /**
     * Get all clusters for a particular revision
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * 
     * @throws XmlServerException
     */
    public String[] getAllClusters(String revisionID) throws XmlServerException;
    
    /**
     * Delete a cluster for particular revision
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param clusterName
     *          The name of the cluster
     * @return the milliseconds to perform the operation
     * @throws XmlServerException
     * 
     */
    public long deleteCluster(String revisionID, String clusterName) throws XmlServerException;
    
    /**
     * Delete All clusters for a particular revision
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the milliseconds to perform the operation
     * @throws XmlServerException
     * 
     */
    public long deleteAllClusters(String revisionID) throws XmlServerException;
    

    /**
     * Create a cluster for a particular revision
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param clusterName
     *          The name of the cluster
     * @return the milliseconds to perform the operation
     * @throws XmlServerException
     * 
     */
    public long createCluster(String revisionID, String clusterName) throws XmlServerException;


    
    /***************************************************************************
     * 
     * D O C U M E N T S
     * 
     **************************************************************************/

    
    /**
     * Reads a document from a file and stores it in the DB 
     * @param fileName
     *          The full path of the file
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the milliseconds to perform the operation
     * @throws XmlServerException
     */
    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID) throws XmlServerException;
    
    /**
     * Reads a document from a file and stores it in the DB
     * @param fileName
     *          The full path of the file
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param documentType
     *          "DOCUMENT" for and XML document, "BINARY" otherwise
     * @return the milliseconds to perform the operation
     * @throws XmlServerException
     */
    public long putDocumentFromFile(
        String fileName, 
        String uniqueID, 
        String clusterName,
        String revisionID,
        String documentType
    ) throws XmlServerException;
    
    /**
     * 
     * @param cluster
     * @return
     * @throws XmlServerException
     */
    public boolean existCluster(String revision,String cluster)throws XmlServerException;
    /**
     * Read a document from s String an store it in the DB as "DOCUMENT"
     * @param xmlString
     *          The xml string
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the time to store the document
     * @throws XmlServerException
     */
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID) throws XmlServerException;
    
    /**
     * Read a document from a String and store it in the DB
     * @param string
     *          The string to store
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param documentType
     *          "DOCUMENT" for and XML document, "BINARY" otherwise
     * @return the time to store the document
     * @throws XmlServerException
     */
    public long putDocumentFromString(
        String string, 
        String uniqueID, 
        String clusterName, 
        String revisionID,
        String documentType 
    ) throws XmlServerException;

    
    /**
     * Stores a document from its DOM root element
     * @param root
     *          The DOM root element
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the time to store the document
     * @throws XmlServerException
     */
    public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID) throws XmlServerException;

    /**
     * Load a document using a SAX parser.
     *
     * @param dataClusterName The unique ID of the cluster
     * @param docReader A SAX reader
     * @param input A SAX input
     * @param revisionId The revision id (<code>null</code> for head).
     * @throws XmlServerException If anything goes wrong in underlying storage
     */
    public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId) throws XmlServerException;

    /**
     * Gets an XML document from the DB<br>
     * The XML instruction will have an encoding specified as UTF-16
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the document as A string
     * @throws XmlServerException
     */
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException;
    
    /**
     * Gets an XML document from the DB<br>
     * The XML instruction will have the encoding specified in the encoding parameter<br>
     * If encoding is null, the document will not have an XML instruction
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param encoding
     *          The encoding of the XML instruction (e.g. UTF-16, UTF-8, etc...).
     * @return the document as A string
     * @throws XmlServerException
     */
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding) throws XmlServerException;
        
    /**
     * Gets the bytes of a document from the DB<br>
     * For an xml "DOCUMENT" type, the bytes will be encoded using UTF-16
     * 
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @param documentType
     *          "DOCUMENT" for and XML document, "BINARY" otherwise
     * @return the document as A string
     * @throws XmlServerException
     */
    public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException;
    
    
    /**
     * The list of documents unique ids in a cluster of a particular revision
     * 
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the list of document unique IDs
     * @throws XmlServerException
     */
    public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException;

    
    /**
     * Delete a document
     * @param uniqueID
     *          The unique ID of the document
     * @param clusterName
     *          The unique ID of the cluster
     * @param revisionID
     *          The ID of the revision, <code>null</code> for the head
     * @return the time to perform the delete
     * @throws XmlServerException
     */
    public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType) throws XmlServerException;
    

    /**
     * Delete Xtentis Objects matching a particular condition<br> 
     * @param objectRootElementNameToRevisionID
     *          A map that gives the revision ID of an Object XML Root Element Name
     * @param objectRootElementNameToClusterName
     *          An ordered map that gives the cluster name of an Object XML Root Element Name
     * @param objectRootElementName
     *          The objectType (its name)
     * @param whereItem
     *          The condition
     * @return the number of items deleted
     * @throws XmlServerException
     */
    public int deleteXtentisObjects(
        HashMap<String, String> objectRootElementNameToRevisionID,
        HashMap<String, String> objectRootElementNameToClusterName,
        String objectRootElementName,
        IWhereItem whereItem
    ) throws XmlServerException;

    
    /**
     * Delete Items matching a particular condition<br> 
     * @param conceptPatternsToClusterName
     *          An ordered map that gives the cluster name of a Concept when matching the first pattern found
     * @param conceptPatternsToRevisionID
     *          An ordered map that gives the revision ID of a Concept when matching the first pattern found
     * @param conceptName
     *          The Concept of the items being deleted
     * @param whereItem
     *          The condition
     * @return the number of items deleted
     * @throws XmlServerException
     */
    public int deleteItems(
        LinkedHashMap<String, String> conceptPatternsToRevisionID,
        LinkedHashMap<String, String> conceptPatternsToClusterName,
        String conceptName, 
        IWhereItem whereItem
    ) throws XmlServerException;

    
    /**
     * Move a document between two clusters of particular revision
     * @param uniqueID
     *          The unique ID of the document
     * @param sourceclusterName
     *          The unique ID of the source cluster
     * @param sourceRevisionID
     *          The ID of the source revision
     * @param targetclusterName
     *          The unique ID of the target cluster
     * @param targetRevisionID
     *          The ID of the target revision
     * @return the time to perform the move
     * @throws XmlServerException
     */
    public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID, String targetclusterName) throws XmlServerException;


    
    /**
     * Count Items based on conditions
     *
     * @param conceptPatternsToRevisionID
     *          A map that gives the revision ID of a pattern matching a concept name Concept (isItemQuery is true) or Xtentis Object (isItemQuery is false)
     * @param conceptPatternsToClusterName
     *          An ordered map that gives the cluster name of a Concept when matching the first pattern found
     * @param conceptName
     *          The name of the concept
     * @param whereItem
     *          The condition to apply
     * @return the number of items meeting the conditions
     * @throws XmlServerException
     */
    public long countItems(
        Map<String, String> conceptPatternsToRevisionID,
        Map<String, String> conceptPatternsToClusterName,
        String conceptName,
        IWhereItem whereItem
    ) throws XmlServerException;
    
    /**
     * Count Xtentis Objects based on conditions
     * @param objectRootElementNameToRevisionID
     *          A map that gives the revision ID of an Xtentis Object based on its XML Root Element Name 
     * @param objectRootElementNameToClusterName
     *          An ordered map that gives the cluster name of an Object based on its XML Root Element Name
     * @param mainObjectRootElementName
     *          An optional object XML root element name that will serve as the main pivot<br/>
     *          If not specified, the pivots will be in ordered of those in the viewableBusinessElements
     * @param whereItem 
     *          The condition to apply
     * @return the number of items meeting the conditions
     * @throws XmlServerException
     */
    public long countXtentisObjects(
        HashMap<String, String> objectRootElementNameToRevisionID,
        HashMap<String, String> objectRootElementNameToClusterName,
        String mainObjectRootElementName,
        IWhereItem whereItem
    ) throws XmlServerException;
    
    /**
     * Builds an Items query in the native language of the DB (for instance XQuery) based on conditions
     *
     * @param conceptPatternsToRevisionID
     *          A map that gives the revision ID of a pattern matching a concept name Concept (isItemQuery is true) or Xtentis Object (isItemQuery is false)
     * @param conceptPatternsToClusterName
     *          An ordered map that gives the cluster name of a Concept when matching the first pattern found
     * @param forceMainPivot
     *          An optional pivot that will appear first in the list of pivots in the query<br>:
     *          This allows forcing cartesian products: for instance Order Header vs Order Line
     * @param viewableFullPaths
     *          The xPaths of the elements and their sub elements that constitute the top elements of the returned documents
     * @param whereItem
     *          The condition to apply
     * @param orderBy
     *          The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction
     *          If orderBy is not <code>null</code>, the direction. One of {@link #ORDER_ASCENDING} or {@link #ORDER_DESCENDING}
     * @param start
     *          The index of the first element to return (start at 0)
     * @param limit
     *          The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no limit
     * @return the XQuery in the native language of the database
     * @throws XmlServerException
     * @deprecated
     */
    public String getItemsQuery(
        Map<String, String> conceptPatternsToRevisionID,
        Map<String, String> conceptPatternsToClusterName,
        String forceMainPivot,
        ArrayList<String> viewableFullPaths,    
        IWhereItem whereItem,
        String orderBy,
        String direction,
        int start,
        int limit
    ) throws XmlServerException;
    
    /**
     * Builds an Items query in the native language of the DB (for instance XQuery) based on conditions
     *
     * @param conceptPatternsToRevisionID
     * @param conceptPatternsToClusterName
     * @param forceMainPivot
     * @param viewableFullPaths
     * @param whereItem
     * @param orderBy
     * @param direction
     * @param start
     * @param limit
     * @param totalCountOnfirstRow
     * @param metaDataTypes
     * @return the XQuery in the native language of the database
     * @throws XmlServerException
     * @deprecated
     */
    public String getItemsQuery(
            Map<String, String> conceptPatternsToRevisionID,
            Map<String, String> conceptPatternsToClusterName,
            String forceMainPivot,
            ArrayList<String> viewableFullPaths,    
            IWhereItem whereItem,
            String orderBy,
            String direction,
            int start,
            long limit,
            boolean totalCountOnfirstRow,
            Map<String, ArrayList<String>> metaDataTypes
    ) throws XmlServerException;
    
    /**
     * Builds a query in the native language of the DB (for instance XQuery) based on conditions
     * @param objectRootElementNameToRevisionID
     *          A map that gives the revision ID of an Xtentis Object based on its XML Root Element Name 
     * @param objectRootElementNameToClusterName
     *          An ordered map that gives the cluster name of an Object based on its XML Root Element Name
     * @param mainObjectRootElementName
     *          An optional object XML root element name that will serve as the main pivot<br/>
     *          If not specified, the pivots will be in ordered of those in the viewableBusinessElements
     * @param viewableFullPaths 
     *          The xPaths of the elements and their sub elements that constitute the top elements of the returned documents
     * @param whereItem 
     *          The condition to apply
     * @param orderBy
     *          The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction
     *          If orderBy is not <code>null</code>, the direction. One of {@link #ORDER_ASCENDING} or {@link #ORDER_DESCENDING}
     * @param start
     *          The index of the first element to return (start at 0)
     * @param limit
     *          The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no limit
     * @return the XQuery in the native language of the database
     * @throws XmlServerException
     * @deprecated
     */
    public String getXtentisObjectsQuery(
        HashMap<String, String> objectRootElementNameToRevisionID,
        HashMap<String, String> objectRootElementNameToClusterName,
        String mainObjectRootElementName,
        ArrayList<String> viewableFullPaths,
        IWhereItem whereItem,
        String orderBy,
        String direction,
        int start,
        int limit
    ) throws XmlServerException;
    
    /**
     * Builds a query in the native language of the DB (for instance XQuery) based on conditions
     * @param objectRootElementNameToRevisionID
     * @param objectRootElementNameToClusterName
     * @param mainObjectRootElementName
     * @param viewableFullPaths
     * @param whereItem
     * @param orderBy
     * @param direction
     * @param start
     * @param limit
     * @param totalCountOnfirstRow
     * @return the XQuery in the native language of the database
     * @throws XmlServerException
     * @deprecated
     */
    public String getXtentisObjectsQuery(
            LinkedHashMap<String, String> objectRootElementNameToRevisionID,
            LinkedHashMap<String, String> objectRootElementNameToClusterName,
            String mainObjectRootElementName,
            ArrayList<String> viewableFullPaths,
            IWhereItem whereItem,
            String orderBy,
            String direction,
            int start,
            long limit,
            boolean totalCountOnfirstRow
        ) throws XmlServerException;
    
    /**
     * @param clusterName
     * @param mainPivotName
     * @param pivotWithKeys
     * @param indexPaths
     * @param whereItem
     * @param pivotDirections
     * @param indexDirections
     * @param start
     * @param limit
     * @return
     * @throws XmlServerException
     * @deprecated
     */
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
    ) throws XmlServerException;
    
    
    
    /**
     * @param clusterName
     * @param conceptName
     * @param PKXpaths
     * @param FKXpath
     * @param labelXpath
     * @param fatherPK
     * @param itemsRevisionIDs
     * @param defaultRevisionID
     * @param whereItem
     * @param start
     * @param limit
     * @return
     * @throws XmlServerException
     * @deprecated
     */
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
    ) throws XmlServerException;
    
    /**
     * Performs a query on the db with optional parameters<br>
     * The parameters are specified as %n in the query where n is the parameter number starting with 0
     * @param revisionID
     *          The ID of the revision, <code>null</code> to run the query from the head
     * @param clusterName
     *          The unique ID of the cluster, <code>null</code> to run the query from the head
     * @param query 
     *          The query in the native language
     * @param parameters 
     *          The parameter values to replace the %n in the query before execution
     * @return the result of the Query as a Collection of Strings
     * @throws XmlServerException
     */
    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XmlServerException;
    
    
    public ArrayList<String> runQuery(String revisionID, String clusterName,
            String query, String[] parameters, final int start, final int limit, final boolean withTotalCount)
            throws XmlServerException ;
    /**
     * 
     * Retrieves a list of PKs using provided criteria
     * @param criteria
     * @return
     * @throws XmlServerException
     */
    public List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XmlServerException;
    
    /**
     * clear the item cache
     */
    public void clearCache();
    
    public boolean supportTransaction();
    public void start(String dataClusterName) throws XmlServerException;
    public void commit(String dataClusterName) throws XmlServerException;
    public void rollback(String dataClusterName) throws XmlServerException;
    public void end(String dataClusterName) throws XmlServerException;
    public void close() throws XmlServerException;
    public List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws XmlServerException;
    public void exportDocuments(String revisionId, String clusterName, int start, int end, boolean includeMetadata, OutputStream outputStream) throws XmlServerException;
}
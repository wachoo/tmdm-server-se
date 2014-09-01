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

package org.talend.mdm.server.api;

import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.OutputStream;
import java.util.*;

/**
 *
 */
public interface XmlServer {
    /**
     * Is the server up?
     */
    boolean isUpAndRunning();

    /**
     * Get all clusters for a particular revision
     *
     * @param revisionID The ID of the revision, <code>null</code> for the head
     * @return the list of cluster IDs
     */
    String[] getAllClusters(String revisionID) throws XtentisException;

    /**
     * clear the item cache
     */
    void clearCache() throws XtentisException;

    /**
     * Delete a cluster for particular revision
     *
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @param clusterName The name of the cluster
     * @return the milliseconds to perform the operation
     */
    long deleteCluster(String revisionID, String clusterName) throws XtentisException;

    /**
     * Delete All clusters for a particular revision
     *
     * @param revisionID The ID of the revision, <code>null</code> for the head
     * @return the milliseconds to perform the operation
     */
    long deleteAllclusterNames(String revisionID) throws XtentisException;

    /**
     * Create a cluster for a particular revision
     *
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @param clusterName The name of the cluster
     * @return the milliseconds to perform the operation
     */
    long createCluster(String revisionID, String clusterName) throws XtentisException;

    /**
     * Reads a document from a file and stores it in the DB
     *
     * @param fileName    The full path of the file
     * @param uniqueID    The unique ID of the document
     * @param clusterName The unique ID of the cluster
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @return the milliseconds to perform the operation
     */
    long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID)
            throws XtentisException;

    /**
     * Reads a document from a file and stores it in the DB
     *
     * @param fileName                The full path of the file
     * @param uniqueID                The unique ID of the document
     * @param clusterName             The unique ID of the cluster
     * @param revisionID              The ID of the revision, <code>null</code> for the head
     * @param documentType="DOCUMENT"
     * @return the milliseconds to perform the operation
     */
    long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID, String documentType)
            throws XtentisException;

    /**
     * Read a document from s String an store it in the DB as "DOCUMENT"
     *
     * @param xmlString   The xml string
     * @param uniqueID    The unique ID of the document
     * @param clusterName The unique ID of the cluster
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @return the time to store the document
     */
    long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID)
            throws XtentisException;

    /**
     * Read a document from a String and store it in the DB
     *
     * @param string                  The string to store
     * @param uniqueID                The unique ID of the document
     * @param clusterName             The unique ID of the cluster
     * @param revisionID              The ID of the revision, <code>null</code> for the head
     * @param documentType="DOCUMENT"
     * @return the time to store the document
     */
    long putDocumentFromString(String string, String uniqueID, String clusterName, String revisionID, String documentType)
            throws XtentisException;

    /**
     * Stores a document from its DOM root element
     *
     * @param root        The DOM root element
     * @param uniqueID    The unique ID of the document
     * @param clusterName The unique ID of the cluster
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @return the time to store the document
     */
    long putDocumentFromDOM(org.w3c.dom.Element root, String uniqueID, String clusterName, String revisionID)
            throws XtentisException;

    /**
     * Load a document using a SAX parser.
     *
     * @param dataClusterName The unique ID of the cluster
     * @param docReader       A SAX reader
     * @param input           A SAX input
     * @param revisionId      The revision id (<code>null</code> for head).
     * @throws XtentisException If anything goes wrong in underlying storage
     */
    long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId)
            throws XtentisException;

    /**
     * Gets an XML document from the DB<br>
     * The XML instruction will have an encoding specified as UTF-16
     *
     * @param uniqueID    The unique ID of the document
     * @param clusterName The unique ID of the cluster
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @return the document as A string
     */
    String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XtentisException;

    boolean existCluster(String revision, String cluster) throws XtentisException;

    /**
     * Gets an XML document from the DB<br>
     * The XML instruction will have the encoding specified in the encoding parameter<br>
     * If encoding is null, the document will not have an XML instruction
     *
     * @param uniqueID    The unique ID of the document
     * @param clusterName The unique ID of the cluster
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @param encoding    The encoding of the XML instruction (e.g. UTF-16, UTF-8, etc...).
     * @return the document as A string
     */
    String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding)
            throws XtentisException;

    /**
     * Gets the bytes of a document from the DB<br>
     * For an xml "DOCUMENT" type, the bytes will be encoded using UTF-16 The XML instruction will have an encoding
     * specified as UTF-16
     *
     * @param uniqueID                The unique ID of the document
     * @param clusterName             The unique ID of the cluster
     * @param revisionID              The ID of the revision, <code>null</code> for the head
     * @param documentType="DOCUMENT"
     * @return the document as A string
     */
    byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType)
            throws XtentisException;

    /**
     * The list of documents unique ids in a cluster of a particular revision
     *
     * @param clusterName The unique ID of the cluster
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @return the list of document unique IDs
     */
    String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XtentisException;

    /**
     * Delete an XML document
     *
     * @param uniqueID    The unique ID of the document
     * @param clusterName The unique ID of the cluster
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @return the time to perform the delete
     */
    long deleteDocument(String revisionID, String clusterName, String uniqueID) throws XtentisException;

    /**
     * Delete a document
     *
     * @param uniqueID    The unique ID of the document
     * @param clusterName The unique ID of the cluster
     * @param revisionID  The ID of the revision, <code>null</code> for the head
     * @return the time to perform the delete
     */
    long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType)
            throws XtentisException;

    /**
     * Delete Xtentis Objects matching a particular condition<br>
     *
     * @param objectRootElementNameToRevisionID
     *                              A map that gives the revision ID of an Object XML Root Element Name
     * @param objectRootElementNameToClusterName
     *                              An ordered map that gives the cluster name of an Object XML Root
     *                              Element Name
     * @param objectRootElementName The objectType (its name)
     * @param whereItem             The condition
     * @return the number of items deleted
     */
    int deleteXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
                             HashMap<String, String> objectRootElementNameToClusterName,
                             String objectRootElementName,
                             com.amalto.xmlserver.interfaces.IWhereItem whereItem) throws XtentisException;

    /**
     * Delete Items matching a particular condition<br>
     *
     * @param conceptPatternsToClusterName An ordered map that gives the cluster name of a Concept when matching the
     *                                     first pattern found
     * @param conceptPatternsToRevisionID  An ordered map that gives the revision ID of a Concept when matching the first
     *                                     pattern found
     * @param conceptName                  The Concept of the items being deleted
     * @param whereItem                    The condition
     * @return the number of items deleted
     */
    int deleteItems(LinkedHashMap<String, String> conceptPatternsToRevisionID,
                    LinkedHashMap<String, String> conceptPatternsToClusterName,
                    String conceptName,
                    com.amalto.xmlserver.interfaces.IWhereItem whereItem) throws XtentisException;

    /**
     * Move a document between two clusters of particular revision
     *
     * @param uniqueID          The unique ID of the document
     * @param sourceclusterName The unique ID of the source cluster
     * @param sourceRevisionID  The ID of the source revision
     * @param targetclusterName The unique ID of the target cluster
     * @param targetRevisionID  The ID of the target revision
     * @return the time to perform the move
     */
    long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID,
                          String targetclusterName) throws XtentisException;

    /**
     * Count Items based on conditions
     *
     * @param conceptPatternsToRevisionID  A map that gives the revision ID of a pattern matching a concept name Concept
     *                                     (isItemQuery is true) or Xtentis Object (isItemQuery is false)
     * @param conceptPatternsToClusterName An ordered map that gives the cluster name of a Concept when matching the
     *                                     first pattern found
     * @param conceptName                  The name of the concept
     * @param whereItem                    The condition to apply
     * @return the number of items meeting the conditions
     * @throws com.amalto.core.util.XtentisException
     *
     */
    long countItems(LinkedHashMap<String, String> conceptPatternsToRevisionID,
                    LinkedHashMap<String, String> conceptPatternsToClusterName,
                    String conceptName,
                    com.amalto.xmlserver.interfaces.IWhereItem whereItem) throws XtentisException;

    /**
     * Count Xtentis Objects based on conditions
     *
     * @param objectRootElementNameToRevisionID
     *                                  A map that gives the revision ID of an Xtentis Object based on its XML
     *                                  Root Element Name
     * @param objectRootElementNameToClusterName
     *                                  An ordered map that gives the cluster name of an Object based on its
     *                                  XML Root Element Name
     * @param mainObjectRootElementName An optional object XML root element name that will serve as the main pivot<br/>
     *                                  If not specified, the pivots will be in ordered of those in the viewableBusinessElements
     * @param whereItem                 The condition to apply
     * @return the number of items meeting the conditions
     * @throws com.amalto.core.util.XtentisException
     *
     */
    long countXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
                             HashMap<String, String> objectRootElementNameToClusterName,
                             String mainObjectRootElementName,
                             com.amalto.xmlserver.interfaces.IWhereItem whereItem) throws XtentisException;

    /**
     * Performs a query on the db with optional parameters<br>
     * The parameters are specified as %n in the query where n is the parameter number starting with 0
     *
     * @param revisionID  The ID of the revision, <code>null</code> to run from the head
     * @param clusterName The unique ID of the cluster, <code>null</code> to run from the head of the revision ID
     * @param query       The query in the native language
     * @param parameters  The parameter values to replace the %n in the query before execution
     * @return the result of the Query as a Collection of Strings
     */
    ArrayList runQuery(String revisionID, String clusterName, String query, String[] parameters) throws XtentisException;

    /**
     * Performs a query on the db with optional parameters<br>
     * The parameters are specified as %n in the query where n is the parameter number starting with 0
     *
     * @param revisionID  The ID of the revision, <code>null</code> to run from the head
     * @param clusterName The unique ID of the cluster, <code>null</code> to run from the head of the revision ID
     * @param query       The query in the native language
     * @param parameters  The parameter values to replace the %n in the query before execution
     * @return the result of the Query as a Collection of Strings
     */
    ArrayList runQuery(String revisionID, String clusterName, String query, String[] parameters, int start, int limit,
                       boolean withTotalCount) throws XtentisException;

    /**
     * Builds a query in the native language of the DB (for instance XQuery) based on conditions
     *
     * @param objectRootElementNameToRevisionID
     *                                  A map that gives the revision ID of an Object XML Root Element Name
     * @param objectRootElementNameToClusterName
     *                                  An ordered map that gives the cluster name of an Object XML Root
     *                                  Element Name
     * @param viewableObjectElements    The full XPath (starting with the Object root element name) of the elements and
     *                                  their sub elements that constitute the top elements of the returned documents
     * @param mainObjectRootElementName An optional object type that will serve as the main pivot<br/>
     *                                  If not specified, the pivots will be in ordered of those in the viewableObjectElements
     * @param whereItem                 The condition to apply
     * @param orderBy                   The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction                 If orderBy is not <code>null</code>, the direction. One of
     * @param start                     The index of the first element to return (start at 0)
     * @param limit                     The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no
     *                                  limit
     * @return the XQuery in the native language of the database
     * @throws com.amalto.core.util.XtentisException
     * @deprecated These method does not allow enough loose coupling with underlying storage (prefer {@link com.amalto.core.delegator.IItemCtrlDelegator#xPathsSearch(com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK, String, java.util.ArrayList, com.amalto.xmlserver.interfaces.IWhereItem, int, String, String, int, int, boolean)}).
     */
    String getXtentisObjectsQuery(HashMap<String, String> objectRootElementNameToRevisionID, HashMap<String, String> objectRootElementNameToClusterName,
                                  String mainObjectRootElementName, ArrayList<String> viewableObjectElements,
                                  com.amalto.xmlserver.interfaces.IWhereItem whereItem, String orderBy, String direction, int start, int limit)
            throws XtentisException;

    /**
     * Builds a query in the native language of the DB (for instance XQuery) based on conditions
     *
     * @param objectRootElementNameToRevisionID
     *                                  A map that gives the revision ID of an Object XML Root Element Name
     * @param objectRootElementNameToClusterName
     *                                  An ordered map that gives the cluster name of an Object XML Root
     *                                  Element Name
     * @param viewableObjectElements    The full XPath (starting with the Object root element name) of the elements and
     *                                  their sub elements that constitute the top elements of the returned documents
     * @param mainObjectRootElementName An optional object type that will serve as the main pivot<br/>
     *                                  If not specified, the pivots will be in ordered of those in the viewableObjectElements
     * @param whereItem                 The condition to apply
     * @param orderBy                   The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction                 If orderBy is not <code>null</code>, the direction. One of
     * @param start                     The index of the first element to return (start at 0)
     * @param limit                     The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no
     *                                  limit
     * @param withTotalCount            whether get totalCount
     * @return the XQuery in the native language of the database
     * @throws com.amalto.core.util.XtentisException
     * @deprecated These method does not allow enough loose coupling with underlying storage (prefer {@link com.amalto.core.delegator.IItemCtrlDelegator#xPathsSearch(com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK, String, java.util.ArrayList, com.amalto.xmlserver.interfaces.IWhereItem, int, String, String, int, int, boolean)}).     *
     */
    String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID,
                                  LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
                                  ArrayList<String> viewableObjectElements, IWhereItem whereItem, String orderBy, String direction, int start,
                                  int limit, boolean withTotalCount) throws XtentisException;

    /**
     * Builds an Items query in the native language of the DB (for instance XQuery) based on conditions
     *
     * @param conceptPatternsToRevisionID  A map that gives the revision ID of a pattern matching a concept name Concept
     *                                     (isItemQuery is true) or Xtentis Object (isItemQuery is false)
     * @param conceptPatternsToClusterName An ordered map that gives the cluster name of a Concept when matching the
     *                                     first pattern found
     * @param forceMainPivot               An optional pivot that will appear first in the list of pivots in the query<br>
     *                                     : This allows forcing cartesian products: for instance Order Header vs Order Line
     * @param viewableFullPaths            The Full xPaths (starting with concept name) of the elements and their sub elements that
     *                                     constitute the top elements of the returned documents
     * @param whereItem                    The condition to apply
     * @param orderBy                      The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction                    If orderBy is not <code>null</code>, the direction. One of
     * @param start                        The index of the first element to return (start at 0)
     * @param limit                        The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no
     *                                     limit
     * @param spellThreshold               Spell check the whereItem if threshold is greater than zero. The setting is ignored is this
     *                                     not an item query.
     * @return the xquery in the native language of the db
     * @deprecated These method does not allow enough loose coupling with underlying storage (prefer {@link com.amalto.core.delegator.IItemCtrlDelegator#xPathsSearch(com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK, String, java.util.ArrayList, com.amalto.xmlserver.interfaces.IWhereItem, int, String, String, int, int, boolean)}).
     */
    String getItemsQuery(LinkedHashMap<String, String> conceptPatternsToRevisionID, LinkedHashMap<String, String> conceptPatternsToClusterName,
                         String forceMainPivot, ArrayList<String> viewableFullPaths, IWhereItem whereItem,
                         String orderBy, String direction, int start, int limit, int spellThreshold) throws XtentisException;

    /**
     * Builds an Items query in the native language of the DB (for instance XQuery) based on conditions
     *
     * @param conceptPatternsToRevisionID  A map that gives the revision ID of a pattern matching a concept name Concept
     *                                     (isItemQuery is true) or Xtentis Object (isItemQuery is false)
     * @param conceptPatternsToClusterName An ordered map that gives the cluster name of a Concept when matching the
     *                                     first pattern found
     * @param forceMainPivot               An optional pivot that will appear first in the list of pivots in the query<br>
     *                                     : This allows forcing cartesian products: for instance Order Header vs Order Line
     * @param viewableFullPaths            The Full xPaths (starting with concept name) of the elements and their sub elements that
     *                                     constitute the top elements of the returned documents
     * @param whereItem                    The condition to apply
     * @param orderBy                      The path of the element to order by. <code>null</code> to avoid ordering
     * @param direction                    If orderBy is not <code>null</code>, the direction. One of
     * @param start                        The index of the first element to return (start at 0)
     * @param limit                        The index of the last element to search. A negative value or {@value Integer#MAX_VALUE} means no
     *                                     limit
     * @param spellThreshold               Spell check the whereItem if threshold is greater than zero. The setting is ignored is this
     *                                     not an item query.
     * @param metaDataTypes                Additional information about element's types.
     * @return the xquery in the native language of the db
     * @deprecated These method does not allow enough loose coupling with underlying storage (prefer {@link com.amalto.core.delegator.IItemCtrlDelegator#xPathsSearch(com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK, String, java.util.ArrayList, com.amalto.xmlserver.interfaces.IWhereItem, int, String, String, int, int, boolean)}).
     */
    String getItemsQuery(LinkedHashMap<String, String> conceptPatternsToRevisionID,
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
                         Map<String, ArrayList<String>> metaDataTypes)
            throws XtentisException;

    /**
     * @deprecated These method does not allow enough loose coupling with underlying storage (prefer {@link com.amalto.core.delegator.IItemCtrlDelegator#xPathsSearch(com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK, String, java.util.ArrayList, com.amalto.xmlserver.interfaces.IWhereItem, int, String, String, int, int, boolean)}).
     */
    String getPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap<String, String[]> pivotWithKeys,
                              LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, String[] indexPaths,
                              IWhereItem whereItem, String[] pivotDirections, String[] indexDirections, int start,
                              int limit) throws XtentisException;

    /**
     * @deprecated These method does not allow enough loose coupling with underlying storage (prefer {@link com.amalto.core.delegator.IItemCtrlDelegator#xPathsSearch(com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK, String, java.util.ArrayList, com.amalto.xmlserver.interfaces.IWhereItem, int, String, String, int, int, boolean)}).
     */
    String getChildrenItemsQuery(String clusterName, String conceptName, String[] PKXpaths, String FKXpath,
                                 String labelXpath, String fatherPK, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID,
                                 IWhereItem whereItem, int start, int limit) throws XtentisException;

    List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XtentisException;

    boolean supportTransaction();

    void start(String dataClusterName) throws XtentisException;

    void commit(String dataClusterName) throws XtentisException;

    void rollback(String dataClusterName) throws XtentisException;

    void end(String dataClusterName) throws XtentisException;

    void close() throws XtentisException;

    void exportDocuments(String revisionId, String clusterName, int start, int end, boolean includeMetadata, OutputStream outputStream) throws XtentisException;

    List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws XtentisException;

    boolean supportStaging(String dataCluster) throws XtentisException;
}

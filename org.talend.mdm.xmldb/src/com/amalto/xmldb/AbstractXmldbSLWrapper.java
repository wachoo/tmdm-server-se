// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.xmldb;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.w3c.dom.Node;

import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.XmlServerException;

public abstract class AbstractXmldbSLWrapper implements IXmlServerSLWrapper, IXmlServerEBJLifeCycle {

    private static Logger LOG = Logger.getLogger(AbstractXmldbSLWrapper.class);

    protected final QueryBuilder queryBuilder = newQueryBuilder();

    protected abstract QueryBuilder newQueryBuilder();

    /**
     * Generates an xml string from a node with or without the xml declaration (not pretty formatted)
     * 
     * @param n the node
     * @return the xml string
     * @throws TransformerException
     */
    public static String nodeToString(Node n, boolean omitXMLDeclaration) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        if (omitXMLDeclaration) {
            transformer.setOutputProperty("omit-xml-declaration", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            transformer.setOutputProperty("omit-xml-declaration", "no"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (LOG.isDebugEnabled()) {
            transformer.setOutputProperty("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        transformer.transform(new DOMSource(n), new StreamResult(sw));
        return sw.toString().replaceAll("\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public void clearCache() {
    }

    @Override
    public boolean isUpAndRunning() {
        return true;
    }

    @Override
    public long countItems(Map<String, String> conceptPatternsToRevisionID, Map<String, String> conceptPatternsToClusterName,
            String fullPath, IWhereItem whereItem) throws XmlServerException {

        StringBuilder xquery = new StringBuilder();
        try {
            // if the where Item is null, try to make a simplified x path query
            if (whereItem == null) {
                // get the concept
                String revisionID = queryBuilder.getRevisionID(conceptPatternsToRevisionID, fullPath);
                // determine cluster
                String clusterName = queryBuilder.getClusterName(conceptPatternsToClusterName, fullPath);

                // Replace for QueryBuilder
                xquery.append("count("); //$NON-NLS-1$
                xquery.append(queryBuilder.getXQueryCollectionName(revisionID, clusterName));
                xquery.append("/ii/p/"); //$NON-NLS-1$
                xquery.append(fullPath);
                xquery.append(")"); //$NON-NLS-1$
            } else {
                xquery.append("let $zcount := "); //$NON-NLS-1$
                xquery.append(getItemsQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, null,
                        new ArrayList<String>(Arrays.asList(fullPath)), whereItem, null, null, 0, -1));
                xquery.append("\n return count($zcount)"); //$NON-NLS-1$
            }

            ArrayList<String> results = runQuery(null, null, xquery.toString(), null);

            return Long.parseLong(results.get(0));

        } catch (Exception e) {
            String err = "Unable to count the elements using path " + fullPath;
            LOG.error(err, e);
            throw new XmlServerException(err);
        }
    }

    @Override
    public long countXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String objectFullPath, IWhereItem whereItem)
            throws XmlServerException {

        try {
            StringBuilder xquery = new StringBuilder();

            // if the where Item is null, try to make a simplified x path query
            if (whereItem == null) {
                // get the concept
                String rootElementName = QueryBuilder.getRootElementNameFromPath(objectFullPath);
                // determine revision
                String revisionID = objectRootElementNameToRevisionID.get(rootElementName);
                // determine cluster
                String clusterName = objectRootElementNameToClusterName.get(rootElementName);

                // Replace for QueryBuilder
                xquery.append("count("); //$NON-NLS-1$
                xquery.append(queryBuilder.getXQueryCollectionName(revisionID, clusterName));
                xquery.append("/"); //$NON-NLS-1$
                xquery.append(objectFullPath);
                xquery.append(")"); //$NON-NLS-1$ 
            } else {
                xquery.append("let $zcount := "); //$NON-NLS-1$
                xquery.append(getXtentisObjectsQuery(objectRootElementNameToRevisionID, objectRootElementNameToClusterName, null,
                        new ArrayList<String>(Arrays.asList(objectFullPath)), whereItem, null, null, 0, -1));
                xquery.append("\n return count($zcount)"); //$NON-NLS-1$
            }

            ArrayList<String> results = runQuery(null, null, xquery.toString(), null);

            return Long.parseLong(results.get(0));

        } catch (Exception e) {
            String err = "Unable to count the objects using path '" + objectFullPath + "'";
            LOG.error(err, e);
            throw new XmlServerException(err);
        }
    }

    @Override
    public String getChildrenItemsQuery(String clusterName, String conceptName, String[] PKXpaths, String FKXpath,
            String labelXpath, String fatherPK, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID,
            IWhereItem whereItem, int start, int limit) throws XmlServerException {

        try {

            StringBuilder xq = new StringBuilder();
            StringBuilder xqFor = new StringBuilder();
            StringBuilder xqWhere = new StringBuilder();
            StringBuilder xqOrderby = new StringBuilder();
            StringBuilder xqReturn = new StringBuilder();

            // for
            xqFor.append("for "); //$NON-NLS-1$
            String revisionID = CommonUtil.getConceptRevisionID(itemsRevisionIDs, defaultRevisionID, conceptName);// revision
            // issue
            // String revisionID=null;
            String collectionPath = CommonUtil.getPath(revisionID, clusterName);
            xqFor.append("$").append(conceptName).append(" in collection(\"").append(collectionPath).append("\")/ii/p/") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
                    .append(conceptName).append(" "); //$NON-NLS-1$

            // where
            xqWhere.append("where (1=1) "); //$NON-NLS-1$

            if (FKXpath != null) {
                fatherPK = (fatherPK == null ? "" : StringEscapeUtils.escapeXml(fatherPK)); //$NON-NLS-1$
                xqWhere.append(" and ($").append(FKXpath).append(" = '").append(fatherPK).append("'").append(" or $") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        .append(FKXpath).append("=concat('[','").append(fatherPK).append("',']')) "); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // build from WhereItem
            if (whereItem != null) {

                LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
                pivots.put(conceptName, conceptName);

                String appendWhere = queryBuilder.buildWhere(pivots, whereItem, false);

                if (appendWhere != null && appendWhere.length() > 0 && appendWhere.trim().length() != 0) {
                    xqWhere.append(" and "); //$NON-NLS-1$
                    xqWhere.append(appendWhere);
                }

                xqWhere.append(" "); //$NON-NLS-1$

            }

            // order by

            // return
            xqReturn.append("return "); //$NON-NLS-1$
            xqReturn.append("<result>"); //$NON-NLS-1$

            xqReturn.append("<result-key>"); //$NON-NLS-1$

            if (PKXpaths.length == 1) {
                xqReturn.append("{$").append(PKXpaths[0]).append("/text()}"); //$NON-NLS-1$ //$NON-NLS-2$
            } else if (PKXpaths.length > 1) {
                xqReturn.append("{concat("); //$NON-NLS-1$
                for (int l = 0; l < PKXpaths.length; l++) {
                    if (l > 0) {
                        xqReturn.append(","); //$NON-NLS-1$
                    }
                    xqReturn.append("'['").append(",$").append(PKXpaths[l]).append("/text()").append(",']'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                }
                xqReturn.append(")}"); //$NON-NLS-1$
            }
            xqReturn.append("</result-key>"); //$NON-NLS-1$

            if (labelXpath != null) {
                String[] toDisplayXpaths = labelXpath.split(","); //$NON-NLS-1$
                for (String toDisplayXpath : toDisplayXpaths) {
                    xqReturn.append("<result-label>"); //$NON-NLS-1$
                    xqReturn.append("{$").append(toDisplayXpath.trim()).append("/text()}"); //$NON-NLS-1$ //$NON-NLS-2$
                    xqReturn.append("</result-label>"); //$NON-NLS-1$
                }
            }

            xqReturn.append("</result>  "); //$NON-NLS-1$

            xq.append(xqFor).append(xqWhere).append(xqOrderby).append(xqReturn);
            String query = xq.toString();

            if (start >= 0 && limit > 0) {
                query = "let $list := \n" + query + "\n return subsequence($list," + (start + 1) + "," + limit + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }

            return query;

        } catch (Exception e) {
            String err = "Unable to build the getChildrenItems XQuery";
            LOG.error(err, e);
            throw new XmlServerException(err);
        }
    }

    @Override
    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException {
        return getDocumentAsString(revisionID, clusterName, uniqueID, "UTF-16"); //$NON-NLS-1$
    }

    public String getItemsUriQuery(Map<String, String> conceptPatternsToRevisionID,
            Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths,
            IWhereItem whereItem) throws XmlServerException {
        return queryBuilder.getUriQuery(true, conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot,
                viewableFullPaths, whereItem, false, null);
    }

    @Override
    public String getItemsQuery(Map<String, String> conceptPatternsToRevisionID,
            Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths,
            IWhereItem whereItem, String orderBy, String direction, int start, int limit) throws XmlServerException {
        return getItemsQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot, viewableFullPaths,
                whereItem, orderBy, direction, start, limit, false, null);
    }

    @Override
    public String getItemsQuery(Map<String, String> conceptPatternsToRevisionID,
            Map<String, String> conceptPatternsToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths,
            IWhereItem whereItem, String orderBy, String direction, int start, long limit, boolean totalCountOnfirstRow,
            Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        // Replace for QueryBuilder

        return queryBuilder.getQuery(true, conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot,
                viewableFullPaths, whereItem, orderBy, direction, start, limit, totalCountOnfirstRow, metaDataTypes);
    }

    @Override
    public String getPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap<String, String[]> pivotWithKeys,
            LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, String[] indexPaths, IWhereItem whereItem,
            String[] pivotDirections, String[] indexDirections, int start, int limit) throws XmlServerException {

        try {
            StringBuilder xq = new StringBuilder();
            StringBuilder xqFor = new StringBuilder();
            StringBuilder xqWhere = new StringBuilder();
            StringBuilder xqOrderby = new StringBuilder();
            StringBuilder xqReturn = new StringBuilder();

            HashSet<String> conceptMap = new HashSet<String>();
            String[] pivotPaths = new String[pivotWithKeys.size()];

            // parse pivotWithKeys
            int i = 0;
            for (Iterator<String> iterator = pivotWithKeys.keySet().iterator(); iterator.hasNext(); i++) {
                String pivot = iterator.next();
                pivotPaths[i] = pivot;
                String[] tmp = pivot.split("/"); //$NON-NLS-1$ // TODO maybe care about other cases, like '//'
                if (tmp.length > 0) {
                    conceptMap.add(tmp[0]);
                }
            }
            // for
            if (conceptMap.size() > 0) {
                xqFor.append("for "); //$NON-NLS-1$
                int j = 0;
                for (Iterator<String> iterator = conceptMap.iterator(); iterator.hasNext(); j++) {
                    String conceptName = iterator.next();
                    String revisionID = CommonUtil.getConceptRevisionID(itemsRevisionIDs, defaultRevisionID, conceptName);

                    String collectionPath = CommonUtil.getPath(revisionID, clusterName);
                    xqFor.append("$").append(conceptName).append(" in collection(\"").append(collectionPath).append("\")/ii/p/") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            .append(conceptName);

                    if (j < conceptMap.size() - 1) {
                        xqFor.append(", "); //$NON-NLS-1$
                    } else {
                        xqFor.append(" "); //$NON-NLS-1$
                    }
                }
            }

            // where
            if (pivotPaths.length > 0) {
                xqWhere.append("where (1=1) "); //$NON-NLS-1$ // ctoum 20100110
                if (pivotPaths.length > 1) {
                    for (int k = 0; k < pivotPaths.length - 1; k++) {
                        String[] k1keys = pivotWithKeys.get(pivotPaths[k + 1]);
                        if (k1keys.length == 1) {
                            xqWhere.append(" and ($").append(pivotPaths[k]).append("=$").append(k1keys[0]).append(" or $") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                    .append(pivotPaths[k]).append("=concat('[',$").append(k1keys[0]).append(",']')) "); //$NON-NLS-1$ //$NON-NLS-2$
                        } else if (k1keys.length > 1) {
                            xqWhere.append(" and $").append(pivotPaths[k]).append("=concat("); //$NON-NLS-1$ //$NON-NLS-2$
                            for (int l = 0; l < k1keys.length; l++) {
                                if (l > 0) {
                                    xqWhere.append(","); //$NON-NLS-1$
                                }
                                xqWhere.append("'['").append(",$").append(k1keys[l]).append(",']'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            }
                            xqWhere.append(") "); //$NON-NLS-1$
                        }
                    }
                }
            }

            // build from WhereItem
            if (whereItem != null) {
                // ctoum 20100110
                if (xqWhere.length() > 0) {
                    xqWhere.append(" and "); //$NON-NLS-1$
                }
                LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
                pivots.put(mainPivotName, mainPivotName);
                xqWhere.append(queryBuilder.buildWhere(pivots, whereItem, false));
                xqWhere.append(" "); //$NON-NLS-1$

            }

            // order by
            if (pivotPaths.length > 0) {
                xqOrderby.append("order by "); //$NON-NLS-1$
                for (int m = pivotPaths.length - 1; m > -1; m--) {
                    if (m < pivotPaths.length - 1) {
                        xqOrderby.append(","); //$NON-NLS-1$
                    }
                    // see 0016991, using the first element as pivot
                    xqOrderby.append("$").append(pivotPaths[m] + "[1]").append(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    // add direction
                    if (pivotDirections != null && pivotDirections.length > 0) {
                        xqOrderby.append(pivotDirections[m] == null ? "" : " " + pivotDirections[m] + " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                }

                for (int m = 0; m < indexPaths.length; m++) {
                    // see 0016991, using the first element as pivot
                    xqOrderby.append(",").append("$").append(indexPaths[m] + "[1]").append(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    // add direction
                    if (indexDirections != null && indexDirections.length > 0) {
                        xqOrderby.append(indexDirections[m] == null ? "" : " " + indexDirections[m] + " "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                }
            }

            // return
            if (pivotPaths.length > 0) {
                xqReturn.append("return "); //$NON-NLS-1$
                xqReturn.append("<result>"); //$NON-NLS-1$

                xqReturn.append("<result-pivot>"); //$NON-NLS-1$
                for (int n = pivotPaths.length - 1; n > -1; n--) {
                    xqReturn.append("{if ($").append(pivotPaths[n]).append(") then $").append(pivotPaths[n]).append(" else <") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            .append(parseNodeNameFromXpath(pivotPaths[n])).append("/>}"); //$NON-NLS-1$
                }
                xqReturn.append("</result-pivot>"); //$NON-NLS-1$

                xqReturn.append("<result-title>"); //$NON-NLS-1$
                for (String indexPath : indexPaths) {
                    xqReturn.append("{if ($").append(indexPath).append(") then $").append(indexPath).append(" else <") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            .append(parseNodeNameFromXpath(indexPath)).append("/>}"); //$NON-NLS-1$
                }
                xqReturn.append("</result-title>"); //$NON-NLS-1$

                xqReturn.append("<result-key>"); //$NON-NLS-1$
                String[] mainKeys = pivotWithKeys.get(pivotPaths[0]);
                for (String mainKey : mainKeys) {
                    xqReturn.append("{if ($").append(mainKey).append(") then $").append(mainKey).append(" else <") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            .append(parseNodeNameFromXpath(mainKey)).append("/>}"); //$NON-NLS-1$
                }
                xqReturn.append("</result-key>"); //$NON-NLS-1$

                xqReturn.append("</result>  "); //$NON-NLS-1$
            }

            xq.append(xqFor).append(xqWhere).append(xqOrderby).append(xqReturn);
            String query = xq.toString();

            if (start >= 0 && limit > 0) {
                query = "let $list := \n" + query + "\n return subsequence($list," + (start + 1) + "," + limit + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            }

            // replace (1=1) and to ""
            query = query.replaceAll("\\(1=1\\) and", ""); //$NON-NLS-1$ //$NON-NLS-2$
            // replace () and to ""
            query = query.replaceAll("\\(\\) and", ""); //$NON-NLS-1$ //$NON-NLS-2$
            return query;

        } catch (Exception e) {
            String err = "Unable to build the PivotIndex XQuery";
            LOG.error(err, e);
            throw new XmlServerException(err);
        }

    }

    protected String buildWhere(LinkedHashMap<String, String> pivots, IWhereItem whereItem,
            Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        return queryBuilder.buildWhere(pivots, whereItem, metaDataTypes);
    }

    private String parseNodeNameFromXpath(String input) {
        if (input == null) {
            return ""; //$NON-NLS-1$
        }

        String output = input;
        int pos = input.lastIndexOf("/"); //$NON-NLS-1$
        if (pos != -1) {
            output = input.substring(pos + 1);
        }
        return output;
    }

    @Override
    public String getXtentisObjectsQuery(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit)
            throws XmlServerException {

        LinkedHashMap<String, String> copyObjectRootElementNameToRevisionID = null;
        LinkedHashMap<String, String> copyObjectRootElementNameToClusterName = null;

        if (objectRootElementNameToRevisionID instanceof LinkedHashMap) {
            copyObjectRootElementNameToRevisionID = (LinkedHashMap<String, String>) objectRootElementNameToRevisionID;
        } else {
            copyObjectRootElementNameToRevisionID = new LinkedHashMap<String, String>();
            copyObjectRootElementNameToRevisionID.putAll(objectRootElementNameToRevisionID);
        }
        if (objectRootElementNameToClusterName instanceof LinkedHashMap) {
            copyObjectRootElementNameToClusterName = (LinkedHashMap<String, String>) objectRootElementNameToClusterName;
        } else {
            copyObjectRootElementNameToClusterName = new LinkedHashMap<String, String>();
            copyObjectRootElementNameToClusterName.putAll(objectRootElementNameToClusterName);
        }

        return getXtentisObjectsQuery(copyObjectRootElementNameToRevisionID, copyObjectRootElementNameToClusterName,
                mainObjectRootElementName, viewableFullPaths, whereItem, orderBy, direction, start, limit, false);
    }

    @Override
    public String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID,
            LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit,
            boolean totalCountOnfirstRow) throws XmlServerException {
        // Replace for QueryBuilder
        return queryBuilder.getQuery(false, objectRootElementNameToRevisionID, objectRootElementNameToClusterName,
                mainObjectRootElementName, viewableFullPaths, whereItem, orderBy, direction, start, limit, totalCountOnfirstRow,
                null);
    }

    @Override
    public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID,
            String targetclusterName) throws XmlServerException {
        String xml = getDocumentAsString(sourceRevisionID, sourceclusterName, uniqueID);
        if (xml == null) {
            return -1;
        }
        return putDocumentFromString(xml, uniqueID, targetclusterName, targetRevisionID);
    }

    @Override
    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID)
            throws XmlServerException {
        return putDocumentFromFile(fileName, uniqueID, clusterName, revisionID, IXmlServerSLWrapper.TYPE_DOCUMENT);
    }

    @Override
    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID)
            throws XmlServerException {
        return putDocumentFromString(xmlString, uniqueID, clusterName, revisionID, IXmlServerSLWrapper.TYPE_DOCUMENT);
    }

    @Override
    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters, int start,
            int limit, boolean withTotalCount) throws XmlServerException {
        return runQuery(revisionID, clusterName, query, parameters);
    }

    @Override
    public List<String> getItemPKsByCriteria(ItemPKCriteria criteria) throws XmlServerException {
        String revisionId = criteria.getRevisionId();
        String clusterName = criteria.getClusterName();
        String query = queryBuilder.buildPKsByCriteriaQuery(criteria);
        return runQuery(revisionId, clusterName, query, null);
    }

    @Override
    public boolean supportTransaction() {
        return false;
    }

    @Override
    public void start(String dataClusterName) throws XmlServerException {
        // NOOP
    }

    @Override
    public void commit(String dataClusterName) throws XmlServerException {
        // NOOP
    }

    @Override
    public void rollback(String dataClusterName) throws XmlServerException {
        // NOOP
    }

    @Override
    public void end(String dataClusterName) throws XmlServerException {
        // NOOP
    }

    @Override
    public List<String> globalSearch(String dataCluster, String keyword, int start, int end) throws XmlServerException {
        throw new UnsupportedOperationException(); // By default, this is not supported (see EE implementations of the
                                                   // interface).
    }

    @Override
    public void exportDocuments(String revisionId, String clusterName, int start, int end, boolean includeMetadata,
            OutputStream outputStream) throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doActivate() throws XmlServerException {
        // NOOP
    }

    @Override
    public void doCreate() throws XmlServerException {
        // NOOP
    }

    @Override
    public void doPassivate() throws XmlServerException {
        // NOOP
    }

    @Override
    public void doPostCreate() throws XmlServerException {
        // NOOP
    }

    @Override
    public void doRemove() throws XmlServerException {
        // NOOP
    }
}

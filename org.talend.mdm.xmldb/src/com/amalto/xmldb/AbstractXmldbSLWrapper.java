// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.w3c.dom.Node;

import com.amalto.commons.core.utils.XPathUtils;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
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

        if (LOG.isDebugEnabled())
            transformer.setOutputProperty("indent", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
        transformer.transform(new DOMSource(n), new StreamResult(sw));
        return sw.toString().replaceAll("\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void clearCache() {
    }

    public boolean isUpAndRunning() {
        return true;
    }

    public long countItems(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String fullPath, IWhereItem whereItem)
            throws XmlServerException {

        try {
            String xquery = null;

            // if the where Item is null, try to make a simplified x path query
            if (whereItem == null) {
                // get the concept
                String revisionID = QueryBuilder.getRevisionID(conceptPatternsToRevisionID, fullPath);
                // determine cluster
                String clusterName = QueryBuilder.getClusterName(conceptPatternsToRevisionID, conceptPatternsToClusterName,
                        fullPath);

                // Replace for QueryBuilder
                // xquery = "count("+getXQueryCollectionName(revisionID, clusterName)+"/ii/p/"+fullPath+")";
                xquery = "count(" + QueryBuilder.getXQueryCollectionName(revisionID, clusterName) + "/ii/p/" + fullPath + ")";
            } else {
                xquery = "let $zcount := ";
                xquery += getItemsQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, null, new ArrayList<String>(
                        Arrays.asList(new String[] { fullPath })), whereItem, null, null, 0, -1);
                xquery += "\n return count($zcount)";
            }

            ArrayList<String> results = runQuery(null, null, xquery, null);

            return Long.parseLong(results.get(0));

        } catch (Exception e) {
            String err = "Unable to count the elements using path " + fullPath;
            LOG.info(err, e);
            throw new XmlServerException(err);
        }
    }

    public long countXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
            HashMap<String, String> objectRootElementNameToClusterName, String objectFullPath, IWhereItem whereItem)
            throws XmlServerException {

        try {
            String xquery = null;

            // if the where Item is null, try to make a simplified x path query
            if (whereItem == null) {
                // get the concept
                // Replace for QueryBuilder
                // String rootElementName = getRootElementNameFromPath(objectFullPath);
                String rootElementName = QueryBuilder.getRootElementNameFromPath(objectFullPath);
                // determine revision
                String revisionID = objectRootElementNameToRevisionID.get(rootElementName);
                // determine cluster
                String clusterName = objectRootElementNameToClusterName.get(rootElementName);

                // Replace for QueryBuilder
                // xquery = "count("+getXQueryCollectionName(revisionID, clusterName)+"/"+objectFullPath+")";
                xquery = "count(" + QueryBuilder.getXQueryCollectionName(revisionID, clusterName) + "/" + objectFullPath + ")";
            } else {
                xquery = "let $zcount := ";
                xquery += getXtentisObjectsQuery(objectRootElementNameToRevisionID, objectRootElementNameToClusterName, null,
                        new ArrayList<String>(Arrays.asList(new String[] { objectFullPath })), whereItem, null, null, 0, -1);
                xquery += "\n return count($zcount)";
            }

            ArrayList<String> results = runQuery(null, null, xquery, null);

            return Long.parseLong(results.get(0));

        } catch (Exception e) {
            String err = "Unable to count the objects using path '" + objectFullPath + "'";
            LOG.info(err, e);
            throw new XmlServerException(err);
        }
    }

    public String getChildrenItemsQuery(String clusterName, String conceptName, String[] PKXpaths, String FKXpath,
            String labelXpath, String fatherPK, LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID,
            IWhereItem whereItem, int start, int limit) throws XmlServerException {

        try {

            String query = "";
            StringBuffer xq = new StringBuffer();
            StringBuffer xqFor = new StringBuffer();
            StringBuffer xqWhere = new StringBuffer();
            StringBuffer xqOrderby = new StringBuffer();
            StringBuffer xqReturn = new StringBuffer();

            // for
            xqFor.append("for ");
            String revisionID = CommonUtil.getConceptRevisionID(itemsRevisionIDs, defaultRevisionID, conceptName);// revision
            // issue
            // String revisionID=null;
            String collectionPath = CommonUtil.getPath(revisionID, clusterName);
            xqFor.append("$").append(conceptName).append(" in collection(\"").append(collectionPath).append("\")/ii/p/")
                    .append(conceptName).append(" ");

            // where
            xqWhere.append("where (1=1)");

            if (FKXpath != null) {
                fatherPK = (fatherPK == null ? "" : StringEscapeUtils.escapeXml(fatherPK));
                xqWhere.append(" and ($").append(FKXpath).append(" = '").append(fatherPK).append("'").append(" or $")
                        .append(FKXpath).append("=concat('[','").append(fatherPK).append("',']')) ");
            }

            // build from WhereItem
            if (whereItem != null) {

                LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
                pivots.put(conceptName, conceptName);

                String appendWhere = buildWhere(" ", pivots, whereItem, false);
                ;
                if (appendWhere != null && appendWhere.length() > 0 && !appendWhere.trim().equals("")) {
                    xqWhere.append(" and ");
                    xqWhere.append(appendWhere);
                }

                xqWhere.append(" ");

            }

            // order by

            // return
            xqReturn.append("return ");
            xqReturn.append("<result>");

            xqReturn.append("<result-key>");

            if (PKXpaths.length == 1) {
                xqReturn.append("{$").append(PKXpaths[0]).append("/text()}");
            } else if (PKXpaths.length > 1) {
                xqReturn.append("{concat(");
                for (int l = 0; l < PKXpaths.length; l++) {
                    if (l > 0)
                        xqReturn.append(",");
                    xqReturn.append("'['").append(",$").append(PKXpaths[l]).append("/text()").append(",']'");
                }
                xqReturn.append(")}");
            }
            xqReturn.append("</result-key>");

            xqReturn.append("<result-label>");
            xqReturn.append("{$").append(labelXpath).append("/text()}");
            xqReturn.append("</result-label>");

            xqReturn.append("</result>  ");

            xq.append(xqFor).append(xqWhere).append(xqOrderby).append(xqReturn);
            query = xq.toString();

            if (start >= 0 && limit > 0) {
                query = "let $list := \n" + query + "\n return subsequence($list," + (start + 1) + "," + limit + ")";
            }

            return query;

        } catch (Exception e) {
            String err = "Unable to build the getChildrenItems XQuery";
            LOG.info(err, e);
            throw new XmlServerException(err);
        }
    }

    public String getDocumentAsString(String revisionID, String clusterName, String uniqueID) throws XmlServerException {
        return getDocumentAsString(revisionID, clusterName, uniqueID, "UTF-16");
    }

    public String getItemsQuery(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String forceMainPivot,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, int limit)
            throws XmlServerException {
        return getItemsQuery(conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot, viewableFullPaths,
                whereItem, orderBy, direction, start, limit, false, null);
    }

    public String getItemsQuery(LinkedHashMap<String, String> conceptPatternsToRevisionID,
            LinkedHashMap<String, String> conceptPatternsToClusterName, String forceMainPivot,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit,
            boolean totalCountOnfirstRow, Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        // Replace for QueryBuilder
        return queryBuilder.getQuery(true, conceptPatternsToRevisionID, conceptPatternsToClusterName, forceMainPivot,
                viewableFullPaths, whereItem, orderBy, direction, start, limit, totalCountOnfirstRow, metaDataTypes);
    }

    public String getPivotIndexQuery(String clusterName, String mainPivotName, LinkedHashMap<String, String[]> pivotWithKeys,
            LinkedHashMap<String, String> itemsRevisionIDs, String defaultRevisionID, String[] indexPaths, IWhereItem whereItem,
            String[] pivotDirections, String[] indexDirections, int start, int limit) throws XmlServerException {

        try {

            String query = "";
            StringBuffer xq = new StringBuffer();
            StringBuffer xqFor = new StringBuffer();
            StringBuffer xqWhere = new StringBuffer();
            StringBuffer xqOrderby = new StringBuffer();
            StringBuffer xqReturn = new StringBuffer();

            HashSet<String> conceptMap = new HashSet<String>();
            String[] pivotPaths = new String[pivotWithKeys.size()];

            // parse pivotWithKeys
            int i = 0;
            for (Iterator<String> iterator = pivotWithKeys.keySet().iterator(); iterator.hasNext(); i++) {
                String pivot = iterator.next();
                pivotPaths[i] = pivot;
                String[] tmp = pivot.split("/");// TODO maybe care about other cases, like '//'
                if (tmp.length > 0)
                    conceptMap.add(tmp[0]);
            }
            // for
            if (conceptMap.size() > 0) {
                xqFor.append("for ");
                int j = 0;
                for (Iterator<String> iterator = conceptMap.iterator(); iterator.hasNext(); j++) {
                    String conceptName = iterator.next();
                    String revisionID = CommonUtil.getConceptRevisionID(itemsRevisionIDs, defaultRevisionID, conceptName);

                    String collectionPath = CommonUtil.getPath(revisionID, clusterName);
                    xqFor.append("$").append(conceptName).append(" in collection(\"").append(collectionPath).append("\")/ii/p/")
                            .append(conceptName);

                    if (j < conceptMap.size() - 1) {
                        xqFor.append(", ");
                    } else {
                        xqFor.append(" ");
                    }
                }
            }

            // where
            if (pivotPaths.length > 0) {
                xqWhere.append("where (1=1) "); // ctoum 20100110
                if (pivotPaths.length > 1) {
                    for (int k = 0; k < pivotPaths.length - 1; k++) {
                        String[] k1keys = pivotWithKeys.get(pivotPaths[k + 1]);
                        if (k1keys.length == 1) {
                            xqWhere.append(" and ($").append(pivotPaths[k]).append("=$").append(k1keys[0]).append(" or $")
                                    .append(pivotPaths[k]).append("=concat('[',$").append(k1keys[0]).append(",']')) ");
                        } else if (k1keys.length > 1) {
                            xqWhere.append(" and $").append(pivotPaths[k]).append("=concat(");
                            for (int l = 0; l < k1keys.length; l++) {
                                if (l > 0)
                                    xqWhere.append(",");
                                xqWhere.append("'['").append(",$").append(k1keys[l]).append(",']'");
                            }
                            xqWhere.append(") ");
                        }
                    }
                }
            }

            // build from WhereItem
            if (whereItem != null) {
                // ctoum 20100110
                if (xqWhere.length() > 0) {
                    xqWhere.append(" and ");
                }
                LinkedHashMap<String, String> pivots = new LinkedHashMap<String, String>();
                pivots.put(mainPivotName, mainPivotName);
                xqWhere.append(buildWhere(" ", pivots, whereItem, false));
                xqWhere.append(" ");

            }

            // order by
            if (pivotPaths.length > 0) {
                xqOrderby.append("order by ");
                for (int m = pivotPaths.length - 1; m > -1; m--) {
                    if (m < pivotPaths.length - 1)
                        xqOrderby.append(",");
                    // see 0016991, using the first element as pivot
                    xqOrderby.append("$").append(pivotPaths[m] + "[0]").append(" ");
                    // add direction
                    if (pivotDirections != null && pivotDirections.length > 0)
                        xqOrderby.append(pivotDirections[m] == null ? "" : " " + pivotDirections[m] + " ");
                }

                for (int m = 0; m < indexPaths.length; m++) {
                    // see 0016991, using the first element as pivot
                    xqOrderby.append(",").append("$").append(indexPaths[m] + "[0]").append(" ");
                    // add direction
                    if (indexDirections != null && indexDirections.length > 0)
                        xqOrderby.append(indexDirections[m] == null ? "" : " " + indexDirections[m] + " ");
                }
            }

            // return
            if (pivotPaths.length > 0) {
                xqReturn.append("return ");
                xqReturn.append("<result>");

                xqReturn.append("<result-pivot>");
                for (int n = pivotPaths.length - 1; n > -1; n--) {
                    xqReturn.append("{if ($").append(pivotPaths[n]).append(") then $").append(pivotPaths[n]).append(" else <")
                            .append(parseNodeNameFromXpath(pivotPaths[n])).append("/>}");
                }
                xqReturn.append("</result-pivot>");

                xqReturn.append("<result-title>");
                for (int n = 0; n < indexPaths.length; n++) {
                    xqReturn.append("{if ($").append(indexPaths[n]).append(") then $").append(indexPaths[n]).append(" else <")
                            .append(parseNodeNameFromXpath(indexPaths[n])).append("/>}");
                }
                xqReturn.append("</result-title>");

                xqReturn.append("<result-key>");
                String[] mainKeys = pivotWithKeys.get(pivotPaths[0]);
                for (int n = 0; n < mainKeys.length; n++) {
                    xqReturn.append("{if ($").append(mainKeys[n]).append(") then $").append(mainKeys[n]).append(" else <")
                            .append(parseNodeNameFromXpath(mainKeys[n])).append("/>}");
                }
                xqReturn.append("</result-key>");

                xqReturn.append("</result>  ");
            }

            xq.append(xqFor).append(xqWhere).append(xqOrderby).append(xqReturn);
            query = xq.toString();

            if (start >= 0 && limit > 0) {
                query = "let $list := \n" + query + "\n return subsequence($list," + (start + 1) + "," + limit + ")";
            }

            // replace (1=1) and to ""
            query = query.replaceAll("\\(1=1\\) and", "");
            // replace () and to ""
            query = query.replaceAll("\\(\\) and", "");
            return query;

        } catch (Exception e) {
            String err = "Unable to build the PivotIndex XQuery";
            LOG.info(err, e);
            throw new XmlServerException(err);
        }

    }

    private String buildWhere(String where, LinkedHashMap<String, String> pivots, IWhereItem whereItem,
            boolean useValueComparisons) throws XmlServerException {
        try {
            if (whereItem instanceof WhereLogicOperator) {
                Collection<IWhereItem> subItems = ((WhereLogicOperator) whereItem).getItems();
                if (subItems.size() == 0)
                    throw new XmlServerException("The logic operator must contain at least one element");
                if (subItems.size() == 1)
                    return // unnecessary AND or OR
                    buildWhere(where, pivots, subItems.iterator().next(), useValueComparisons);
                int i = 0;
                for (Iterator<IWhereItem> iter = subItems.iterator(); iter.hasNext();) {
                    IWhereItem item = iter.next();
                    if (++i > 1)
                        if (item instanceof WhereCondition) {
                            if (WhereCondition.PRE_OR.equals(((WhereCondition) item).getStringPredicate())) {
                                where = where + " or (";
                            } else {
                                where = where + " and (";
                            }
                        } else if (((WhereLogicOperator) whereItem).getType() == WhereLogicOperator.AND)
                            where += " and (";
                        else
                            where += " or (";
                    else
                        where += "(";
                    where = buildWhere(where, pivots, item, useValueComparisons) + ")";
                }// for
                return where;

            } else if (whereItem instanceof WhereCondition) {
                WhereCondition condition = (WhereCondition) whereItem;
                where += buildWhereCondition(condition, pivots, useValueComparisons);
                return where;
            } else {
                throw new XmlServerException("Unknown Where Type : " + whereItem.getClass().getName());
            }
        } catch (Exception e) {
            String err = "Unable to build the XQuery Where Clause " + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            throw new XmlServerException(err);
        }
    }

    /**
     * Build a where condition in XQuery using paths relative to the provided list of pivots
     */
    public String buildWhereCondition(WhereCondition wc, LinkedHashMap<String, String> pivots, boolean useValueComparisons)
            throws XmlServerException {
        try {

            // all this is EXIST specific

            String where = "";
            String operator = wc.getOperator();

            // numeric detection
            boolean isNum = false;
            boolean isXpathFunction = QueryBuilder.isValidatedFunction(wc.getRightValueOrPath());
            // handle case of String starting with a zero e.g. 00441065 or ending with . e.g. 12345.
            if (!(wc.getRightValueOrPath().matches(".*\\D") || wc.getRightValueOrPath().startsWith("0")
                    || wc.getRightValueOrPath().endsWith(".") || wc.getRightValueOrPath().startsWith("+") || wc
                    .getRightValueOrPath().startsWith("-"))) {
                try {
                    Double.parseDouble(wc.getRightValueOrPath().trim());
                    isNum = true;
                } catch (Exception e) {
                }
            }

            // String encoded = wc.getRightValueOrPath().replaceAll("\\&", "&amp;").replaceAll("<",
            // "&lt;").replaceAll(">", "&gt;");
            String encoded = isXpathFunction ? wc.getRightValueOrPath().trim() : StringEscapeUtils.escapeXml(wc
                    .getRightValueOrPath());
            // aiming modify convert "" & " " to *
            if (encoded != null && encoded.trim().length() == 0) {
                encoded = "*";
            }
            // handle empty case
            if (encoded != null && encoded.equals("null")) {
                encoded = "";
            }
            // change * to .*
            encoded = encoded.replaceAll("\\.\\*|\\*", "\\.\\*");
            if (".*".equals(encoded))
                return "";
            String factorPivots = getPathFromPivots(wc.getLeftPath(), pivots);
            if (operator.equals(WhereCondition.CONTAINS)) {
                String predicate = wc.getStringPredicate();
                // check if the left path is an attribute or an element
                String path = wc.getLeftPath();
                if (path.endsWith("/"))
                    path = path.substring(0, wc.getLeftPath().length() - 1);
                String[] nodes = path.split("/");
                boolean isAttribute = nodes[nodes.length - 1].startsWith("@");
                if ((predicate == null) || predicate.equals(WhereCondition.PRE_NONE)) {
                    if (isAttribute) {
                        where = queryBuilder.getMatchesMethod(factorPivots, encoded);
                    } else {
                        where = queryBuilder.buildContains(factorPivots, encoded, isXpathFunction);
                    }
                } else if (predicate.equals(WhereCondition.PRE_AND)) {
                    if (isAttribute) {
                        where = queryBuilder.getMatchesMethod(factorPivots, encoded);
                    } else {
                        where = queryBuilder.buildContains(factorPivots, encoded, isXpathFunction);
                    }
                } else if (predicate.equals(WhereCondition.PRE_EXACTLY)) {
                    where = factorPivots + " eq \"" + encoded + "\"";
                } else if (predicate.equals(WhereCondition.PRE_STRICTAND)) {
                    // where = "near("+factorPivots+", \""+encoded+"\",1)";
                    where = queryBuilder.getMatchesMethod(factorPivots, encoded);
                } else if (predicate.equals(WhereCondition.PRE_OR)) {
                    if (isAttribute) {
                        where = queryBuilder.getMatchesMethod(factorPivots, encoded);
                    } else {
                        if (isXpathFunction) {
                            where = " contains(" + factorPivots + " , " + encoded + ") ";
                        } else {
                            where = queryBuilder.getMatchesMethod(factorPivots, encoded);

                        }
                    }
                } else if (predicate.equals(WhereCondition.PRE_NOT)) {
                    if (isAttribute) {
                        where = "not " + queryBuilder.getMatchesMethod(factorPivots, encoded);
                    } else {
                        if (isXpathFunction) {
                            where = "not(" + " contains(" + factorPivots + " , " + encoded + ") " + ")";
                        } else {
                            where = "not(" + queryBuilder.getMatchesMethod(factorPivots, encoded) + ")";
                        }
                    }
                }

            } else if (operator.equals(WhereCondition.STRICTCONTAINS)) {
                // where = "near("+factorPivots+", \""+encoded+"\",1)";
                where = queryBuilder.getMatchesMethod(factorPivots, encoded);
            } else if (operator.equals(WhereCondition.STARTSWITH)) {
                if (isXpathFunction) {
                    where = "starts-with(" + factorPivots + ", " + encoded + ") ";
                } else {
                    // where = "near("+factorPivots+", \""+encoded+"*\",1)";
                    where = queryBuilder.getMatchesMethod(factorPivots, encoded);
                }
            } else if (operator.equals(WhereCondition.CONTAINS_TEXT_OF)) {
                // where = getPathFromPivots(wc.getRightValueOrPath(),pivots)+" = "+factorPivots; //JOIN error
                String factorRightPivot = XPathUtils.factor(encoded, pivots) + "";
                where = "contains(" + factorPivots + ", " + factorRightPivot + "/text()) ";

            } else if (operator.equals(WhereCondition.EQUALS)) {
                String useOpe = "eq";
                if (!useValueComparisons)
                    useOpe = WhereCondition.EQUALS;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.NOT_EQUALS)) {
                String useOpe = "ne";
                if (!useValueComparisons)
                    useOpe = WhereCondition.NOT_EQUALS;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.GREATER_THAN)) {
                String useOpe = "gt";
                if (!useValueComparisons)
                    useOpe = WhereCondition.GREATER_THAN;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) {
                String useOpe = "ge";
                if (!useValueComparisons)
                    useOpe = WhereCondition.GREATER_THAN_OR_EQUAL;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.LOWER_THAN)) {
                String useOpe = "lt";
                if (!useValueComparisons)
                    useOpe = WhereCondition.LOWER_THAN;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) {
                String useOpe = "le";
                if (!useValueComparisons)
                    useOpe = WhereCondition.LOWER_THAN_OR_EQUAL;
                if (isNum) {
                    where = "number(" + factorPivots + ") " + useOpe + " " + encoded;
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded;
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\"";
                }
            } else if (operator.equals(WhereCondition.EMPTY_NULL)) {
                String predicate = wc.getStringPredicate();
                if (predicate.equals(WhereCondition.PRE_NOT)) {
                    where = factorPivots + "[text()]";
                } else {
                    // ticket 18359, query empty node or node doesn't exist
                    where = "not(" + factorPivots + ") or " + factorPivots + "[not(text())]";
                }
            } else if (operator.equals(WhereCondition.NO_OPERATOR)) {
                where = factorPivots;
            }

            return where;

        } catch (Exception e) {
            String err = "Unable to build the Where Condition " + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            throw new XmlServerException(err);
        }

    }

    /**
     * Scans the pivots and build a relative path to one of the pivots using the absolute path provided.<br/>
     * If no pivot is found a new pivaot is created<br/>
     * <br/>
     * Say we have a pivot named pivot0 referencing <code>Country/name</code>, the path <code>Country/name/EN</code>
     * will become <code>$pivot0/EN</code>
     * 
     */
    private String getPathFromPivots(String bename, HashMap<String, String> pivots) throws XmlServerException {
        try {
            if (LOG.isTraceEnabled())
                LOG.trace("getPathFromPivots() " + bename + " - " + pivots.keySet());
            if (bename.startsWith("/"))
                bename = bename.substring(1);
            String beRoot = bename.split("/")[0];
            // find pivot
            Set<String> ps = pivots.keySet();
            String newPath = null;
            for (Iterator<String> iterator = ps.iterator(); iterator.hasNext();) {
                String pivot = iterator.next();
                String pivotRoot = pivot.split("/")[0];
                // aiming modify pivotRoot maybe ConceptName[condition], fix bug 0008980
                if (!beRoot.equals(pivotRoot)) {
                    Pattern p = Pattern.compile("(.*?)\\[.*\\]");
                    Matcher m = p.matcher(pivotRoot);
                    if (m.matches()) {
                        if (m.group(1).equals(beRoot)) {
                            int pos = bename.indexOf('/');
                            if (pos != -1) {
                                newPath = "$" + pivots.get(pivot) + bename.substring(pos);
                                break;
                            }
                        }
                    }
                }
                // end
                if (beRoot.equals(pivotRoot)) {
                    // found
                    newPath = "$" + pivots.get(pivot) + getPathFromPivot(pivot, bename);
                    break;
                }
            }
            if (newPath == null) {
                // add pivot
                String var = "pivot" + pivots.size();
                pivots.put(bename, var);
                newPath = "$" + var;
            }
            return newPath;
        } catch (Exception e) {
            String err = "Unable to get the path " + bename + " from the pivots" + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            throw new XmlServerException(err);
        }
    }

    /**
     * Build a relative path to the provided pivot using the absolute path provided.<br/>
     * <br/>
     * Say the pivot is referencing <code>Country/name</code>, the path <code>Country/name/EN</code> will become
     * <code>$pivot0/EN</code>
     * 
     */
    private String getPathFromPivot(String pivot, String path) throws XmlServerException {
        try {
            if ((pivot == null) || (path == null))
                return null;

            if (pivot.startsWith("/"))
                pivot = pivot.substring(1);
            if (pivot.endsWith("/"))
                pivot = pivot.substring(0, pivot.length() - 1);
            if (path.startsWith("/"))
                path = path.substring(1);
            if (path.endsWith("/"))
                path = path.substring(0, path.length() - 1);

            String[] pivotPaths = pivot.split("/");
            String[] pathPaths = path.split("/");

            if (!pivotPaths[0].equals(pathPaths[0]))
                return null;

            String newPath = "";
            int matching = 0;
            for (int i = 1; i < pivotPaths.length; i++) {
                if (i < pathPaths.length)
                    if (pivotPaths[i].equals(pathPaths[i]))
                        matching++;
                    else
                        newPath += "/..";
                else
                    newPath += "/..";
            }
            for (int i = matching + 1; i < pathPaths.length; i++) {
                newPath += "/" + pathPaths[i];
            }

            // fix for eXist bug that has *sometimes* difficulties with "grand parents" (../..)
            // e.g. x/../../y --> x/.././../y
            newPath = newPath.replaceAll("\\.\\./\\.\\.", ".././..");

            return newPath;

        } catch (Exception e) {
            String err = "Unable to get the path " + path + " from pivot " + pivot + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            throw new XmlServerException(err);
        }
    }

    private String parseNodeNameFromXpath(String input) {
        if (input == null)
            return "";

        String output = input;
        int pos = input.lastIndexOf("/");
        if (pos != -1) {
            output = input.substring(pos + 1);
        }
        return output;
    }

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

    public String getXtentisObjectsQuery(LinkedHashMap<String, String> objectRootElementNameToRevisionID,
            LinkedHashMap<String, String> objectRootElementNameToClusterName, String mainObjectRootElementName,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit,
            boolean totalCountOnfirstRow) throws XmlServerException {
        // Replace for QueryBuilder
        return queryBuilder.getQuery(false, objectRootElementNameToRevisionID, objectRootElementNameToClusterName,
                mainObjectRootElementName, viewableFullPaths, whereItem, orderBy, direction, start, limit, totalCountOnfirstRow,
                null);
    }

    public long moveDocumentById(String sourceRevisionID, String sourceclusterName, String uniqueID, String targetRevisionID,
            String targetclusterName) throws XmlServerException {
        String xml = getDocumentAsString(sourceRevisionID, sourceclusterName, uniqueID);
        if (xml == null)
            return -1;
        return putDocumentFromString(xml, uniqueID, targetclusterName, targetRevisionID);
    }

    public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID)
            throws XmlServerException {
        return putDocumentFromFile(fileName, uniqueID, clusterName, revisionID, IXmlServerSLWrapper.TYPE_DOCUMENT);
    }

    public long putDocumentFromString(String xmlString, String uniqueID, String clusterName, String revisionID)
            throws XmlServerException {
        long time = putDocumentFromString(xmlString, uniqueID, clusterName, revisionID, IXmlServerSLWrapper.TYPE_DOCUMENT);
        return time;
    }

    public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters, int start,
            int limit, boolean withTotalCount) throws XmlServerException {
        return runQuery(revisionID, clusterName, query, parameters);
    }

    public boolean supportTransaction() {
        return false;
    }

    public void start() throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    public void commit() throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    public void rollback() throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    public void end() throws XmlServerException {
        throw new UnsupportedOperationException();
    }

    public void doActivate() throws XmlServerException {
        // NOOP
    }

    public void doCreate() throws XmlServerException {
        // NOOP
    }

    public void doPassivate() throws XmlServerException {
        // NOOP
    }

    public void doPostCreate() throws XmlServerException {
        // NOOP
    }

    public void doRemove() throws XmlServerException {
        // NOOP
    }
}

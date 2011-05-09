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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amalto.commons.core.utils.XPathUtils;
import com.amalto.commons.core.utils.xpath.ri.Compiler;
import com.amalto.commons.core.utils.xpath.ri.compiler.Expression;
import com.amalto.commons.core.utils.xpath.ri.compiler.NodeNameTest;
import com.amalto.commons.core.utils.xpath.ri.compiler.Path;
import com.amalto.commons.core.utils.xpath.ri.compiler.Step;
import com.amalto.xmldb.util.PartialXQLPackage;
import com.amalto.xmldb.util.QueryBuilderContext;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.ItemPKCriteria;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
import com.amalto.xmlserver.interfaces.XmlServerException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

/**
 * An XML DB Implementation of the wrapper that works with eXist Open
 * 
 * @author Bruno Grieder
 */
public abstract class QueryBuilder {

    private static final Logger LOG = Logger.getLogger(QueryBuilder.class);

    /**
     * Builds the xQuery Return statement
     * 
     * @param viewableFullPaths
     * @param pivotsMap
     * @param totalCountOnFirstRow
     * @return A valid XQuery string
     */
    protected String getXQueryReturn(ArrayList<String> viewableFullPaths, LinkedHashMap<String, String> pivotsMap,
            boolean totalCountOnFirstRow) {

        int i = 0;
        boolean moreThanOneViewable = viewableFullPaths.size() > 1;
        StringBuilder xqReturn = new StringBuilder();
        if (moreThanOneViewable || totalCountOnFirstRow) {
            xqReturn.append("<result>"); //$NON-NLS-1$
        }

        for (String bename : viewableFullPaths) {
            // remove leading slashes
            if (bename.startsWith("/")) { //$NON-NLS-1$
                bename = bename.substring(1);
            }
            // compile the path
            Expression expression = XPathUtils.compileXPath(bename);
            // factor the root path
            factorFirstPivotInMap(pivotsMap, expression.toString());
            // factor the path
            Expression factoredPath = XPathUtils.factorExpression(expression, pivotsMap, true, true);

            if (moreThanOneViewable || totalCountOnFirstRow) {
                xqReturn.append("{"); //$NON-NLS-1$
            }

            if (expression instanceof Path) {
                // determine last Element Name (Step NodeTest) type and name
                Path viewablePath = (Path) expression;
                Step lastStep = viewablePath.getSteps()[viewablePath.getSteps().length - 1];
                if (lastStep.getNodeTest() instanceof NodeNameTest) {
                    String lastElementName = lastStep.getNodeTest().toString();
                    // hshu modified,because Mantis interprets the 'i' tag as a text formatting (italic)
                    if (lastElementName != null && lastElementName.equals("i")) { //$NON-NLS-1$
                        lastElementName = "xi"; //$NON-NLS-1$
                    }
                    if (lastStep.getAxis() == Compiler.AXIS_ATTRIBUTE) {
                        xqReturn.append("<").append(lastElementName).append(">{string(").append(factoredPath).append(")}</") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                .append(lastElementName).append(">"); //$NON-NLS-1$
                    } else {
                        xqReturn.append("let $element := ").append(factoredPath);
                        xqReturn.append(" return if (not(empty($element))) then $element").append(" else <") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                .append(lastElementName).append("/>"); //$NON-NLS-1$
                    }
                } else {
                    // /text() or /position(), etc....
                    if (moreThanOneViewable) {
                        // create an element
                        xqReturn.append("<viewable").append(i).append(">{").append(factoredPath).append("}</viewable") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                .append(i++).append(">"); //$NON-NLS-1$
                    } else {
                        // return the expression as such
                        xqReturn.append(factoredPath);
                    }
                }
            } else {
                // Constant, Variable Reference or Operation
                if (moreThanOneViewable) {
                    // create an element
                    xqReturn.append("<viewable").append(i).append(">{").append(factoredPath).append("}</viewable").append(i++) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            .append(">"); //$NON-NLS-1$
                } else {
                    // return the expression as such
                    xqReturn.append(factoredPath);
                }
            }
            if (moreThanOneViewable || totalCountOnFirstRow) {
                xqReturn.append("}"); //$NON-NLS-1$
            }
        }
        if (moreThanOneViewable || totalCountOnFirstRow) {
            xqReturn.append("</result>"); //$NON-NLS-1$
        }

        return xqReturn.toString();
    }

    protected void factorFirstPivotInMap(LinkedHashMap<String, String> pivotsMap, String viewablePath) {
        if (viewablePath != null && viewablePath.trim().length() > 0) {
            if (viewablePath.startsWith("/")) //$NON-NLS-1$
                viewablePath = viewablePath.substring(1);
            String thisRootElementName = getRootElementNameFromPath(viewablePath);
            if (thisRootElementName != null && thisRootElementName.length() != 0 && !pivotsMap.containsValue(thisRootElementName))
                XPathUtils.factorExpression(XPathUtils.compileXPath(thisRootElementName), pivotsMap, true, true);
        }
    }

    /**
     * Builds the xQuery Return statement for an Items Query
     * 
     *
     * @param pivotsMap
     * @return
     * @throws XmlServerException
     */
    protected String getXQueryFor(boolean isItemQuery, Map<String, String> rootElementNamesToRevisionID,
            Map<String, String> rootElementNamesToClusterName, Map<String, String> pivotsMap,
            PartialXQLPackage partialXQLPackage, QueryBuilderContext queryBuilderContext) throws XmlServerException {

        StringBuilder xqFor = new StringBuilder();
        // build for
        int i = 0;
        for (Iterator<String> iter = pivotsMap.keySet().iterator(); iter.hasNext(); i++) {
            String pivotName = iter.next();
            // get the path for this pivot
            String path = pivotsMap.get(pivotName);
            // get the concept
            String rootElementName = getRootElementNameFromPath(path);
            // determine revision
            String revisionID = null;
            if (isItemQuery) {
                revisionID = getRevisionID(rootElementNamesToRevisionID, path);
            } else {
                // object name, not a pattern --> direct match
                revisionID = rootElementNamesToRevisionID.get(rootElementName);
            }
            // determine cluster
            String clusterName = null;
            if (isItemQuery) {
                clusterName = getClusterName(rootElementNamesToClusterName, path);
            } else {
                // object name, not a pattern --> direct match
                clusterName = rootElementNamesToClusterName.get(rootElementName);
            }
            if (xqFor.length() == 0)
                xqFor.append("for "); //$NON-NLS-1$
            else
                xqFor.append(", "); //$NON-NLS-1$

            // FIXME:subsequence is not support for multi-pivots
            if (pivotsMap.size() == 1) {
                xqFor.append(pivotName);
                xqFor.append(" in subsequence($_leres"); //$NON-NLS-1$
                xqFor.append(i);
                xqFor.append("_,"); //$NON-NLS-1$
                xqFor.append(queryBuilderContext.getStart() + 1);
                xqFor.append(","); //$NON-NLS-1$
                xqFor.append(queryBuilderContext.getLimit());
                xqFor.append(")"); //$NON-NLS-1$
            } else {
                xqFor.append(pivotName);
                xqFor.append(" in $_leres"); //$NON-NLS-1$
                xqFor.append(i);
                xqFor.append("_"); //$NON-NLS-1$
            }

            StringBuilder xQueryCollectionName = new StringBuilder();
            xQueryCollectionName.append(getXQueryCollectionName(revisionID, clusterName));
            xQueryCollectionName.append("/"); //$NON-NLS-1$
            if (isItemQuery)
                xQueryCollectionName.append("/p/"); //$NON-NLS-1$
            xQueryCollectionName.append(path);
            partialXQLPackage.addForInCollection(pivotName, xQueryCollectionName.toString());
        }

        if (pivotsMap.size() == 1)
            partialXQLPackage.setUseSubsequenceFirst(true);

        return xqFor.toString();
    }

    /**
     * Build the Query Where clause
     * 
     * @param where
     * @param pivots
     * @param whereItem
     * @return
     * @throws XmlServerException
     */
    protected String buildWhere(LinkedHashMap<String, String> pivots, IWhereItem whereItem,
            Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        return buildWhere(new StringBuilder(), pivots, whereItem, metaDataTypes);
    }

    protected String buildWhere(LinkedHashMap<String, String> pivots, IWhereItem whereItem, boolean useValueComparisons)
            throws XmlServerException {
        return buildWhere(new StringBuilder(), pivots, whereItem, useValueComparisons);
    }

    private String buildWhere(StringBuilder where, LinkedHashMap<String, String> pivots, IWhereItem whereItem, Object object)
            throws XmlServerException {
        try {
            if (whereItem instanceof WhereLogicOperator) {
                Collection<IWhereItem> subItems = ((WhereLogicOperator) whereItem).getItems();
                if (subItems.size() == 0)
                    throw new XmlServerException("The logic operator must contain at least one element");
                if (subItems.size() == 1)
                    return // unnecessary AND or OR
                    buildWhere(where, pivots, subItems.iterator().next(), object);
                int i = 0;
                for (Iterator<IWhereItem> iter = subItems.iterator(); iter.hasNext();) {
                    IWhereItem item = iter.next();
                    if (i == 0)
                        where.append("("); //$NON-NLS-1$
                    buildWhere(where, pivots, item, object);
                    where.append(")");//$NON-NLS-1$
                    if (i < subItems.size() - 1)
                        if (item instanceof WhereCondition) {
                            if (WhereCondition.PRE_OR.equals(((WhereCondition) item).getStringPredicate())) {
                                where.append(" or ("); //$NON-NLS-1$
                            } else {
                                where.append(" and ("); //$NON-NLS-1$
                            }
                        } else if (((WhereLogicOperator) whereItem).getType() == WhereLogicOperator.AND)
                            where.append(" and ("); //$NON-NLS-1$
                        else
                            where.append(" or ("); //$NON-NLS-1$

                    i++;
                }// for
                return where.toString();

            } else if (whereItem instanceof WhereCondition) {
                WhereCondition condition = (WhereCondition) whereItem;
                if (object == null || object instanceof Map)
                    where.append(buildWhereCondition(condition, pivots, (Map<String, ArrayList<String>>) object));
                else if (object instanceof Boolean)
                    where.append(buildWhereCondition(condition, pivots, (Boolean) object));
                else
                    throw new IllegalArgumentException();
                return where.toString();
            } else {
                throw new XmlServerException("Unknown Where Type : " + whereItem.getClass().getName());
            }
        } catch (Exception e) {
            String err = "Unable to build the XQuery Where Clause : " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XmlServerException(err);
        }
    }

    protected String buildContains(String factorPivots, String encoded, boolean isFunction) {
        if ("*".equals(encoded) || ".*".equals(encoded)) { //$NON-NLS-1$ //$NON-NLS-2$
            return getMatchesMethod(factorPivots, "") + " or (empty(" + factorPivots + "/text())) "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } else if (isFunction) {
            return "contains(" + factorPivots + " , " + encoded + ") "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } else {
            // case insensitive aiming added
            return getMatchesMethod(factorPivots, encoded);
        }
    }

    /**
     * Build a where condition in XQuery using paths relative to the provided list of pivots
     */
    protected String buildWhereCondition(WhereCondition wc, LinkedHashMap<String, String> pivots,
            Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {
        try {

            // all this is shared by both EXIST and QIZX databases.
            String operator = wc.getOperator();

            // Parse (Right) Value argument,
            // detect if it is a numeric
            // and encode it to XML

            // The encoded argument
            String encoded = null;
            // numeric detection
            boolean isLeftPathNum = false;
            boolean isRightValueNum = false;
            boolean isXpathFunction = false;
            boolean isNum = false;
            if (wc.getRightValueOrPath() != null) {
                isXpathFunction = wc.isRightValueXPath() && isValidatedFunction(wc.getRightValueOrPath().trim());
                try {
                    Double.parseDouble(wc.getRightValueOrPath().trim());
                    isRightValueNum = true;
                } catch (Exception e) {
                    // Ignored. Means this isn't a num.
                }

                // TODO {Country/isoCode=[xsd:integer],

                // TODO {Country/isoCode=[xsd:integer],
                if (null != metaDataTypes) {
                    String leftPath = wc.getLeftPath();
                    List<String> types = metaDataTypes.get(leftPath);
                    if (types != null) {
                        String type = types.get(0);
                        if (type.indexOf("xsd:double") >= 0 || type.indexOf("xsd:float") >= 0 || type.indexOf("xsd:integer") >= 0 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                || type.indexOf("xsd:decimal") >= 0 || type.indexOf("xsd:byte") >= 0 //$NON-NLS-1$ //$NON-NLS-2$
                                || type.indexOf("xsd:int") >= 0 || type.indexOf("xsd:long") >= 0 //$NON-NLS-1$ //$NON-NLS-2$
                                || type.indexOf("xsd:negativeInteger") >= 0 || type.indexOf("xsd:nonNegativeInteger") >= 0 //$NON-NLS-1$ //$NON-NLS-2$
                                || type.indexOf("xsd:nonPositiveInteger") >= 0 || type.indexOf("xsd:positiveInteger") >= 0 //$NON-NLS-1$ //$NON-NLS-2$
                                || type.indexOf("xsd:short") >= 0 || type.indexOf("xsd:unsignedLong") >= 0 //$NON-NLS-1$ //$NON-NLS-2$
                                || type.indexOf("xsd:unsignedInt") >= 0 || type.indexOf("xsd:unsignedShort") >= 0 //$NON-NLS-1$ //$NON-NLS-2$
                                || type.indexOf("xsd:unsignedByte") >= 0) { //$NON-NLS-1$
                            isLeftPathNum = true;
                        }
                    }
                }                
                if(metaDataTypes==null){ //fix 0021067, if can't get metaDataTypes
                	isNum = isLeftPathNum || isRightValueNum;
                }else{
                	isNum = isLeftPathNum && isRightValueNum;
                }

                encoded = isXpathFunction ? wc.getRightValueOrPath().trim() : StringEscapeUtils.escapeXml(wc
                        .getRightValueOrPath());
                // aiming modify convert "" & " " to *
                if (encoded != null && encoded.trim().length() == 0) {
                    encoded = "*"; //$NON-NLS-1$
                }
                // change * to .*
                encoded = encoded.replaceAll("\\.\\*|\\*", "\\.\\*"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (".*".equals(encoded) && !operator.equals(WhereCondition.EMPTY_NULL)) //$NON-NLS-1$
                return ""; //$NON-NLS-1$
            // add modified on criteria
            if (wc.getLeftPath().startsWith("../../")) //$NON-NLS-1$
                return wc.getLeftPath() + " " + wc.getOperator() + " " + wc.getRightValueOrPath(); //$NON-NLS-1$ //$NON-NLS-2$ 
            factorFirstPivotInMap(pivots, wc.getLeftPath());
            String factorPivots = XPathUtils.factor(wc.getLeftPath(), pivots).toString();
            // see 0015004, if rightPath contains '/', we consider it as xpathFunction
            if (wc.getRightValueOrPath() != null && wc.isRightValueXPath() && wc.getRightValueOrPath().contains("/")) { //$NON-NLS-1$
                encoded = XPathUtils.factor(wc.getRightValueOrPath(), pivots).toString();
                isXpathFunction = true;
            }

            StringBuilder where = new StringBuilder();

            if (operator.equals(WhereCondition.CONTAINS)) {
                String predicate = wc.getStringPredicate();
                // check if the left path is an attribute or an element
                String path = wc.getLeftPath();
                if (path.endsWith("/")) //$NON-NLS-1$
                    path = path.substring(0, wc.getLeftPath().length() - 1);
                String[] nodes = path.split("/"); //$NON-NLS-1$
                boolean isAttribute = nodes[nodes.length - 1].startsWith("@"); //$NON-NLS-1$
                if ((predicate == null) || predicate.equals(WhereCondition.PRE_NONE)) {
                    if (isAttribute) {
                        where.append(getMatchesMethod(factorPivots, encoded));
                    } else {
                        where.append(buildContains(factorPivots, encoded, isXpathFunction));
                    }
                } else if (predicate.equals(WhereCondition.PRE_AND)) {
                    if (isAttribute) {
                        where.append(getMatchesMethod(factorPivots, encoded));
                    } else {
                        where.append(buildContains(factorPivots, encoded, isXpathFunction));
                    }
                } else if (predicate.equals(WhereCondition.PRE_EXACTLY)) {
                    if (isXpathFunction) {
                        where.append(factorPivots).append(" eq ").append(encoded); //$NON-NLS-1$
                    } else {
                        where.append(factorPivots).append(" eq \"").append(encoded).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                } else if (predicate.equals(WhereCondition.PRE_STRICTAND)) {
                    if (isXpathFunction) {
                        where.append("contains(").append(factorPivots).append(", ").append(encoded).append(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    } else {
                        where.append(getMatchesMethod(factorPivots, encoded));
                    }
                } else if (predicate.equals(WhereCondition.PRE_OR)) {
                    if (isAttribute) {
                        where.append(getMatchesMethod(factorPivots, encoded));
                    } else {
                        if (isXpathFunction) {
                            where.append(" contains(").append(factorPivots).append(" , ").append(encoded).append(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        } else {
                            where.append(getMatchesMethod(factorPivots, encoded));
                        }
                    }
                } else if (predicate.equals(WhereCondition.PRE_NOT)) {
                    if (isAttribute) {
                        where.append("not ").append(getMatchesMethod(factorPivots, encoded)); //$NON-NLS-1$
                    } else {
                        if (isXpathFunction) {
                            where.append("not( contains(").append(factorPivots).append(" , ").append(encoded).append(") )"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        } else {
                            where.append("not(").append(getMatchesMethod(factorPivots, encoded)).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                }

            } else if (operator.equals(WhereCondition.FULLTEXTSEARCH)) {
                where.append(getFullTextQueryString(wc.getRightValueOrPath().trim()));
            } else if (operator.equals(WhereCondition.STRICTCONTAINS)) {
                if (isXpathFunction) {
                    where.append("starts-with(").append(factorPivots).append(", ").append(encoded).append(") "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                } else {
                    where.append(getMatchesMethod(factorPivots, encoded));
                }
            } else if (operator.equals(WhereCondition.STARTSWITH)) {
                if (isXpathFunction) {
                    where.append("starts-with(").append(factorPivots).append(", ").append(encoded).append(") "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                } else {
                    where.append(getMatchesMethod(factorPivots, encoded));
                }
            } else if (operator.equals(WhereCondition.CONTAINS_TEXT_OF)) {

                // FIXME:ASSUME the pivots are the same?
                String factorRightPivot = XPathUtils.factor(encoded, pivots).toString();
                where.append("contains(").append(factorPivots).append(", ").append(factorRightPivot).append("/text()) "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            } else if (operator.equals(WhereCondition.JOINS)) {

                // FIXME:ASSUME the pivots are the same?
                String factorRightPivot = XPathUtils.factor(encoded, pivots).toString();
                where.append(factorPivots).append(" JOINS ").append(factorRightPivot); //$NON-NLS-1$

            } else if (operator.equals(WhereCondition.EQUALS)) {
                if (isNum) {
                    if(useNumberFunction()) {
                        where.append("number(").append(factorPivots).append(") eq ").append(encoded); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        where.append(factorPivots).append(" = ").append(encoded); //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where.append(factorPivots).append("= ").append(encoded); //$NON-NLS-1$
                } else {
                    where.append(factorPivots).append(" eq \"").append(encoded).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } else if (operator.equals(WhereCondition.NOT_EQUALS)) {
                if (isNum) {
                    if(useNumberFunction()) {
                        where.append("number(").append(factorPivots).append(") ne ").append(encoded); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        where.append(factorPivots).append(" != ").append(encoded); //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where.append(factorPivots).append(" != ").append(encoded); //$NON-NLS-1$
                } else {
                    where.append(factorPivots).append(" ne \"").append(encoded).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } else if (operator.equals(WhereCondition.GREATER_THAN)) {
                if (isNum) {
                    if (useNumberFunction()) {
                        where.append("number(").append(factorPivots).append(") gt ").append(encoded); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        where.append(factorPivots).append(" > ").append(encoded); //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where.append(factorPivots).append("> ").append(encoded); //$NON-NLS-1$
                } else {
                    where.append(factorPivots).append(" gt \"").append(encoded).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } else if (operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) {
                if (isNum) {
                    if (useNumberFunction()) {
                        where.append("number(").append(factorPivots).append(") ge ").append(encoded); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        where.append(factorPivots).append(" >= ").append(encoded); //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where.append(factorPivots).append(" >= ").append(encoded); //$NON-NLS-1$
                } else {
                    where.append(factorPivots).append(" ge \"").append(encoded).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } else if (operator.equals(WhereCondition.LOWER_THAN)) {
                if (isNum) {
                    if (useNumberFunction()) {
                        where.append("number(").append(factorPivots).append(") lt ").append(encoded); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        where.append(factorPivots).append(" < ").append(encoded); //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where.append(factorPivots).append(" < ").append(encoded); //$NON-NLS-1$
                } else {
                    where.append(factorPivots).append(" lt \"").append(encoded).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } else if (operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) {
                if (isNum) {
                    if (useNumberFunction()) {
                        where.append("number(").append(factorPivots).append(") le ").append(encoded); //$NON-NLS-1$ //$NON-NLS-2$
                    } else {
                        where.append(factorPivots).append(" <= ").append(encoded); //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where.append(factorPivots).append(" <= ").append(encoded); //$NON-NLS-1$
                } else {
                    where.append(factorPivots).append(" le \"").append(encoded).append("\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            } else if (operator.equals(WhereCondition.EMPTY_NULL)) {
                String predicate = wc.getStringPredicate();
                if (predicate.equals(WhereCondition.PRE_NOT)) {
                    where.append(factorPivots).append("[text()]"); //$NON-NLS-1$
                } else {
                    // ticket 18359, query empty node or node doesn't exist
                    where.append("not(").append(factorPivots).append(") or ").append(factorPivots).append("[not(text())]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else if (operator.equals(WhereCondition.NO_OPERATOR)) {
                where.append(factorPivots);
            }

            return where.toString();

        } catch (Exception e) {
            String err = "Unable to build the Where Condition : " + e.getLocalizedMessage();
            LOG.error(err, e);
            throw new XmlServerException(err);
        }

    }

    /**
     * @return <code>true</code> is the XQuery number() function should be used in numeric comparison, <code>false</code> otherwise.
     * @see #buildWhereCondition(com.amalto.xmlserver.interfaces.WhereCondition, java.util.LinkedHashMap, boolean)
     * @see #buildWhereCondition(com.amalto.xmlserver.interfaces.WhereCondition, java.util.LinkedHashMap, Map)
     */
    protected abstract boolean useNumberFunction();

    /**
     * Build a where condition in XQuery using paths relative to the provided list of pivots
     */
    protected String buildWhereCondition(WhereCondition wc, LinkedHashMap<String, String> pivots, boolean useValueComparisons)
            throws XmlServerException {
        try {

            // all this is EXIST specific

            String operator = wc.getOperator();

            // numeric detection
            boolean isNum = false;
            boolean isXpathFunction = QueryBuilder.isValidatedFunction(wc.getRightValueOrPath());
            // handle case of String starting with a zero e.g. 00441065 or ending with . e.g. 12345.
            if (!(wc.getRightValueOrPath().matches(".*\\D") || wc.getRightValueOrPath().startsWith("0") //$NON-NLS-1$ //$NON-NLS-2$
                    || wc.getRightValueOrPath().endsWith(".") || wc.getRightValueOrPath().startsWith("+") || wc //$NON-NLS-1$ //$NON-NLS-2$
                    .getRightValueOrPath().startsWith("-"))) { //$NON-NLS-1$
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
                encoded = "*"; //$NON-NLS-1$
            }
            // handle empty case
            if (encoded != null && encoded.equals("null")) { //$NON-NLS-1$
                encoded = ""; //$NON-NLS-1$
            }
            // change * to .*
            encoded = encoded.replaceAll("\\.\\*|\\*", "\\.\\*"); //$NON-NLS-1$ //$NON-NLS-2$
            if (".*".equals(encoded)) //$NON-NLS-1$
                return ""; //$NON-NLS-1$
            String where;
            String factorPivots = getPathFromPivots(wc.getLeftPath(), pivots);
            if (operator.equals(WhereCondition.CONTAINS)) {
                String predicate = wc.getStringPredicate();
                // check if the left path is an attribute or an element
                String path = wc.getLeftPath();
                if (path.endsWith("/")) //$NON-NLS-1$
                    path = path.substring(0, wc.getLeftPath().length() - 1);
                String[] nodes = path.split("/"); //$NON-NLS-1$
                boolean isAttribute = nodes[nodes.length - 1].startsWith("@"); //$NON-NLS-1$
                if ((predicate == null) || predicate.equals(WhereCondition.PRE_NONE)) {
                    if (isAttribute) {
                        where = getMatchesMethod(factorPivots, encoded);
                    } else {
                        where = buildContains(factorPivots, encoded, isXpathFunction);
                    }
                } else if (predicate.equals(WhereCondition.PRE_AND)) {
                    if (isAttribute) {
                        where = getMatchesMethod(factorPivots, encoded);
                    } else {
                        where = buildContains(factorPivots, encoded, isXpathFunction);
                    }
                } else if (predicate.equals(WhereCondition.PRE_EXACTLY)) {
                    where = factorPivots + " eq \"" + encoded + "\""; //$NON-NLS-1$ //$NON-NLS-2$
                } else if (predicate.equals(WhereCondition.PRE_STRICTAND)) {
                    // where = "near("+factorPivots+", \""+encoded+"\",1)";
                    where = getMatchesMethod(factorPivots, encoded);
                } else if (predicate.equals(WhereCondition.PRE_OR)) {
                    if (isAttribute) {
                        where = getMatchesMethod(factorPivots, encoded);
                    } else {
                        if (isXpathFunction) {
                            where = " contains(" + factorPivots + " , " + encoded + ") "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        } else {
                            where = getMatchesMethod(factorPivots, encoded);

                        }
                    }
                } else if (predicate.equals(WhereCondition.PRE_NOT)) {
                    if (isAttribute) {
                        where = "not " + getMatchesMethod(factorPivots, encoded); //$NON-NLS-1$
                    } else {
                        if (isXpathFunction) {
                            where = "not(" + " contains(" + factorPivots + " , " + encoded + ") " + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                        } else {
                            where = "not(" + getMatchesMethod(factorPivots, encoded) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                } else
                    where = null;

            } else if (operator.equals(WhereCondition.STRICTCONTAINS)) {
                // where = "near("+factorPivots+", \""+encoded+"\",1)";
                where = getMatchesMethod(factorPivots, encoded);
            } else if (operator.equals(WhereCondition.STARTSWITH)) {
                if (isXpathFunction) {
                    where = "starts-with(" + factorPivots + ", " + encoded + ") "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                } else {
                    // where = "near("+factorPivots+", \""+encoded+"*\",1)";
                    where = getMatchesMethod(factorPivots, encoded);
                }
            } else if (operator.equals(WhereCondition.CONTAINS_TEXT_OF)) {
                // where = getPathFromPivots(wc.getRightValueOrPath(),pivots)+" = "+factorPivots; //JOIN error
                String factorRightPivot = XPathUtils.factor(encoded, pivots) + ""; //$NON-NLS-1$
                where = "contains(" + factorPivots + ", " + factorRightPivot + "/text()) "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            } else if (operator.equals(WhereCondition.EQUALS)) {
                String useOpe = "eq"; //$NON-NLS-1$
                if (!useValueComparisons)
                    useOpe = WhereCondition.EQUALS;
                if (isNum) {
                    if (useNumberFunction()) {
                        where = "number(" + factorPivots + ") " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    } else {
                        where = factorPivots + " = " + encoded; //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else if (operator.equals(WhereCondition.NOT_EQUALS)) {
                String useOpe = "ne"; //$NON-NLS-1$
                if (!useValueComparisons)
                    useOpe = WhereCondition.NOT_EQUALS;
                if (isNum) {
                    if (useNumberFunction()) {
                        where = "number(" + factorPivots + ") " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    } else {
                        where = factorPivots + " != " + encoded; //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else if (operator.equals(WhereCondition.GREATER_THAN)) {
                String useOpe = "gt"; //$NON-NLS-1$
                if (!useValueComparisons)
                    useOpe = WhereCondition.GREATER_THAN;
                if (isNum) {
                    if (useNumberFunction()) {
                        where = "number(" + factorPivots + ") " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    } else {
                        where = factorPivots + " > " + encoded; //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else if (operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) {
                String useOpe = "ge"; //$NON-NLS-1$
                if (!useValueComparisons)
                    useOpe = WhereCondition.GREATER_THAN_OR_EQUAL;
                if (isNum) {
                    if (useNumberFunction()) {
                        where = "number(" + factorPivots + ") " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    } else {
                        where = factorPivots + " >= " + encoded; //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else if (operator.equals(WhereCondition.LOWER_THAN)) {
                String useOpe = "lt"; //$NON-NLS-1$
                if (!useValueComparisons)
                    useOpe = WhereCondition.LOWER_THAN;
                if (isNum) {
                    if (useNumberFunction()) {
                        where = "number(" + factorPivots + ") " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    } else {
                        where = factorPivots + " < " + encoded; //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else if (operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) {
                String useOpe = "le"; //$NON-NLS-1$
                if (!useValueComparisons)
                    useOpe = WhereCondition.LOWER_THAN_OR_EQUAL;
                if (isNum) {
                    if (useNumberFunction()) {
                        where = "number(" + factorPivots + ") " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    } else {
                        where = factorPivots + " <= " + encoded; //$NON-NLS-1$
                    }
                } else if (isXpathFunction) {
                    where = factorPivots + " " + useOpe + " " + encoded; //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    where = factorPivots + " " + useOpe + " \"" + encoded + "\""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else if (operator.equals(WhereCondition.EMPTY_NULL)) {
                String predicate = wc.getStringPredicate();
                if (predicate.equals(WhereCondition.PRE_NOT)) {
                    where = factorPivots + "[text()]"; //$NON-NLS-1$
                } else {
                    // ticket 18359, query empty node or node doesn't exist
                    where = "not(" + factorPivots + ") or " + factorPivots + "[not(text())]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            } else if (operator.equals(WhereCondition.NO_OPERATOR)) {
                where = factorPivots;
            } else
                where = null;

            return where;

        } catch (Exception e) {
            String err = "Unable to build the Where Condition : " + e.getLocalizedMessage();
            LOG.error(err, e);
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
                LOG.trace("getPathFromPivots() " + bename + " - " + pivots.keySet()); //$NON-NLS-1$ //$NON-NLS-2$
            if (bename.startsWith("/")) //$NON-NLS-1$
                bename = bename.substring(1);
            String beRoot = bename.split("/")[0]; //$NON-NLS-1$
            // find pivot
            Set<String> ps = pivots.keySet();
            String newPath = null;
            for (Iterator<String> iterator = ps.iterator(); iterator.hasNext();) {
                String pivot = iterator.next();
                String pivotRoot = pivot.split("/")[0]; //$NON-NLS-1$
                // aiming modify pivotRoot maybe ConceptName[condition], fix bug 0008980
                if (!beRoot.equals(pivotRoot)) {
                    Pattern p = Pattern.compile("(.*?)\\[.*\\]"); //$NON-NLS-1$
                    Matcher m = p.matcher(pivotRoot);
                    if (m.matches()) {
                        if (m.group(1).equals(beRoot)) {
                            int pos = bename.indexOf('/');
                            if (pos != -1) {
                                newPath = '$' + pivots.get(pivot) + bename.substring(pos);
                                break;
                            }
                        }
                    }
                }
                // end
                if (beRoot.equals(pivotRoot)) {
                    // found
                    newPath = '$' + pivots.get(pivot) + getPathFromPivot(pivot, bename);
                    break;
                }
            }
            if (newPath == null) {
                // add pivot
                String var = "pivot" + pivots.size(); //$NON-NLS-1$
                pivots.put(bename, var);
                newPath = '$' + var;
            }
            return newPath;
        } catch (Exception e) {
            String err = "Unable to get the path " + bename + " from the pivots" + ": " + e.getLocalizedMessage();
            LOG.error(err, e);
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

            if (pivot.startsWith("/")) //$NON-NLS-1$
                pivot = pivot.substring(1);
            if (pivot.endsWith("/")) //$NON-NLS-1$
                pivot = pivot.substring(0, pivot.length() - 1);
            if (path.startsWith("/")) //$NON-NLS-1$
                path = path.substring(1);
            if (path.endsWith("/")) //$NON-NLS-1$
                path = path.substring(0, path.length() - 1);

            String[] pivotPaths = pivot.split("/"); //$NON-NLS-1$
            String[] pathPaths = path.split("/"); //$NON-NLS-1$

            if (!pivotPaths[0].equals(pathPaths[0]))
                return null;

            String newPath = ""; //$NON-NLS-1$
            int matching = 0;
            for (int i = 1; i < pivotPaths.length; i++) {
                if (i < pathPaths.length)
                    if (pivotPaths[i].equals(pathPaths[i]))
                        matching++;
                    else
                        newPath += "/.."; //$NON-NLS-1$
                else
                    newPath += "/.."; //$NON-NLS-1$
            }
            for (int i = matching + 1; i < pathPaths.length; i++) {
                newPath += '/' + pathPaths[i];
            }

            // fix for eXist bug that has *sometimes* difficulties with "grand parents" (../..)
            // e.g. x/../../y --> x/.././../y
            newPath = newPath.replaceAll("\\.\\./\\.\\.", ".././.."); //$NON-NLS-1$ //$NON-NLS-2$

            return newPath;

        } catch (Exception e) {
            String err = "Unable to get the path " + path + " from pivot " + pivot + ": " + e.getLocalizedMessage();
            LOG.info(err, e);
            throw new XmlServerException(err);
        }
    }

    /**
     * check if is validated function.
     */
    public static boolean isValidatedFunction(String value) {
        Pattern pattern = Pattern.compile("\\S+\\((\\S,*)*\\)$"); //$NON-NLS-1$
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * Builds an XQuery
     * 
     *
     * @param isItemQuery
     * @param objectRootElementNamesToRevisionID
     * @param objectRootElementNamesToClusterName
     * @param forceMainPivot
     * @param viewableFullPaths
     * @param whereItem
     * @param orderBy
     * @param direction
     * @param start
     * @param limit
     * @return
     * @throws XmlServerException
     */
    public String getQuery(boolean isItemQuery, Map<String, String> objectRootElementNamesToRevisionID,
            Map<String, String> objectRootElementNamesToClusterName, String forceMainPivot,
            ArrayList<String> viewableFullPaths, IWhereItem whereItem, String orderBy, String direction, int start, long limit,
            boolean withTotalCountOnFirstRow, Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException {

        try {
            // build Pivots Map
            LinkedHashMap<String, String> pivotsMap = new LinkedHashMap<String, String>();
            if (forceMainPivot != null)
                pivotsMap.put("$pivot0", forceMainPivot); //$NON-NLS-1$

            if (start < 0 || limit < 0 || limit == Integer.MAX_VALUE) {
                start = 0;
                limit = Integer.MAX_VALUE;
            }
            PartialXQLPackage partialXQLPackage = new PartialXQLPackage();
            QueryBuilderContext queryBuilderContext = new QueryBuilderContext();
            queryBuilderContext.setStart(start);
            queryBuilderContext.setLimit(limit);
            // build return statement
            String xqReturn = getXQueryReturn(viewableFullPaths, pivotsMap, withTotalCountOnFirstRow);

            // build from WhereItem
            String xqWhere;
            if (whereItem == null)
                xqWhere = ""; //$NON-NLS-1$
            else {
                xqWhere = buildWhere(pivotsMap, whereItem, metaDataTypes);
            }
            partialXQLPackage.setXqWhere(xqWhere);

            // build order by
            String xqOrderBy;
            if (orderBy == null) {
                xqOrderBy = ""; //$NON-NLS-1$
            } else {
                factorFirstPivotInMap(pivotsMap, orderBy);
                xqOrderBy = "order by " + XPathUtils.factor(orderBy, pivotsMap) + (direction == null ? "" : " " + direction); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            partialXQLPackage.setXqOrderBy(xqOrderBy);
            // Get For
            String xqFor = getXQueryFor(isItemQuery, objectRootElementNamesToRevisionID, objectRootElementNamesToClusterName,
                    pivotsMap, partialXQLPackage, queryBuilderContext);

            StringBuilder rawQueryStringBuffer = new StringBuilder();
            rawQueryStringBuffer.append(xqFor);

            // add joinkeys
            partialXQLPackage.resetPivotWhereMap();
            List<String> joinKeys = partialXQLPackage.getJoinKeys();
            String joinString;
            if (joinKeys.size() != 0) {
                joinString = buildWhereJoin(joinKeys);
                if (joinString.length() == 0)
                    joinString = null;
            } else
                joinString = null;
            partialXQLPackage.setUseJoin(joinString != null);
            if (joinString != null)
                rawQueryStringBuffer.append('\n').append(joinString);

            if (!partialXQLPackage.isUseGlobalOrderBy())
                if (xqOrderBy.length() != 0)
                    rawQueryStringBuffer.append('\n').append(xqOrderBy);
            rawQueryStringBuffer.append("\nreturn " + xqReturn); //$NON-NLS-1$
            String rawQuery = rawQueryStringBuffer.toString();

            // Determine Query based on number of results an counts
            String query = getPagingString(withTotalCountOnFirstRow, partialXQLPackage, start, limit, rawQuery);

            // create a intermediate line for subsequence

            StringBuffer firstLets = new StringBuffer();
            LinkedHashMap<String, String> forInCollectionMap = partialXQLPackage.getForInCollectionMap();
            partialXQLPackage.resetPivotWhereMap();
            Map<String, String> pivotWhereMap = partialXQLPackage.getPivotWhereMap();
            int i = 0;
            for (Iterator<String> iterator = forInCollectionMap.keySet().iterator(); iterator.hasNext(); i++) {
                String root = iterator.next();
                String expr = forInCollectionMap.get(root);
                if (pivotWhereMap.get(root) != null && pivotWhereMap.get(root).length() > 0)
                    expr = expr + " [ " + pivotWhereMap.get(root) + " ] "; //$NON-NLS-1$ //$NON-NLS-2$
                else if (xqWhere.indexOf("../../t") > -1) {// add modified condition if there is no pivotWhereMap  //$NON-NLS-1$
                    expr = expr + " [ " + xqWhere + " ] "; //$NON-NLS-1$ //$NON-NLS-2$
                }
                if (partialXQLPackage.isUseGlobalOrderBy())
                    expr = partialXQLPackage.genOrderByWithFirstExpr(expr);
                firstLets.append("let $_leres").append(i).append("_ := ").append(expr).append(" \n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            query = (firstLets.toString() + query);

            // replace () and to ""
            query = query.replaceAll(" \\(\\) and", " "); //$NON-NLS-1$ //$NON-NLS-2$
            query = query.replaceAll(" and \\(\\)", " "); //$NON-NLS-1$ //$NON-NLS-2$
            query = query.replaceAll("\\(\\(\\) and", "( "); //$NON-NLS-1$ //$NON-NLS-2$

            if (LOG.isDebugEnabled()) {
                LOG.debug("query:\n"); //$NON-NLS-1$
                LOG.debug(query);
            }
            return query;

        } catch (XmlServerException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to build the Item XQuery";
            LOG.error(err, e);
            throw new XmlServerException(err);
        }
    }

    protected String getCountExpr(PartialXQLPackage partialXQLPackage) {

        StringBuffer countExpr = new StringBuffer();
        if (partialXQLPackage.isUseJoin()) {
            countExpr.append("count($_page_)"); //$NON-NLS-1$
            return countExpr.toString();
        }
        countExpr.append("count($_leres0_)"); //$NON-NLS-1$
        int size = partialXQLPackage.getForInCollectionMap().size();
        if (size > 1) {
            for (int i = 1; i < size; i++) {
                countExpr.append("*").append("count($_leres").append(i).append("_)"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
        return countExpr.toString();
    }

    protected String buildWhereJoin(List<String> joinKeys) {
        LinkedHashMap<String, ArrayList<String>> fkMaps = new LinkedHashMap<String, ArrayList<String>>();
        for (String joinkey : joinKeys) {
            String[] items = joinkey.split("\\b(or|and)\\b"); //$NON-NLS-1$
            for (String item : items) {
                String key = item.trim();
                if (key.matches("\\((.*?)\\)")) //$NON-NLS-1$
                    key = key.replaceFirst("\\((.*?)\\)", "$1"); //$NON-NLS-1$ //$NON-NLS-2$
                String[] splits = key.split(WhereCondition.JOINS);
                if (splits.length == 2) {
                    String fk = splits[0].trim().replace("(", "").replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    String rightV = splits[1].trim().replace("(", "").replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    ArrayList<String> value = fkMaps.get(fk);
                    if (value == null) {
                        value = new ArrayList<String>();
                        fkMaps.put(fk, value);
                    }
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append("concat(\"[\",").append(rightV).append(",\"]\")\n"); //$NON-NLS-1$ //$NON-NLS-2$
                    value.add(sb1.toString());
                }
            }
        }

        StringBuffer where = new StringBuffer();
        if (fkMaps.size() > 0)
            where.append(" where "); //$NON-NLS-1$
        int count = 0;
        for (Entry<String, ArrayList<String>> entry : fkMaps.entrySet()) {
            where.append(entry.getKey());
            // see 0015254: Resolve FK info on search
            where.append("[not(.) or not(text()) or .]"); //$NON-NLS-1$
            // results
            ArrayList<String> vars = entry.getValue();
            String keyvalue;
            if (vars.size() == 1) {
                keyvalue = vars.get(0);
            } else {
                keyvalue = "concat("; //$NON-NLS-1$
                for (int k = 0; k < vars.size(); k++) {
                    if (k < vars.size() - 1) {
                        keyvalue += vars.get(k) + ',';
                    } else {
                        keyvalue += vars.get(k);
                    }
                }
                keyvalue += ')';
            }

            where.append('=').append(keyvalue);
            if (count < fkMaps.size() - 1) {
                where.append(" and "); //$NON-NLS-1$
            }
            count++;
        }
        return where.toString();
    }

    public String getClusterName(Map<String, String> conceptPatternsToClusterName, String fullPath) {
        String conceptName = getRootElementNameFromPath(fullPath);
        // determine cluster
        String clusterName = null;
        Set<String> patterns = conceptPatternsToClusterName.keySet();
        for (String pattern : patterns) {
            if (conceptName.matches(pattern)) {
                clusterName = conceptPatternsToClusterName.get(pattern);
                break;
            }
        }
        return clusterName;
    }

    public String getRevisionID(Map<String, String> conceptPatternsToRevisionID, String fullPath) {
        String conceptName = getRootElementNameFromPath(fullPath);
        // determine revision
        String revisionID = null;
        Set<String> patterns = conceptPatternsToRevisionID.keySet();
        for (String pattern : patterns) {
            if (conceptName.matches(pattern)) {
                revisionID = conceptPatternsToRevisionID.get(pattern);
                break;
            }
        }
        return revisionID;
    }

    /**
     * Determine the collection name based on the revision ID and Cluster Name
     * @param revisionID Revision Id
     * @param clusterName Cluster name
     * @return The xquery collection name that stores the XML documents
     *
     * @throws com.amalto.xmlserver.interfaces.XmlServerException In case of unexpected error.
     * */
    public String getXQueryCollectionName(String revisionID, String clusterName) throws XmlServerException {
        String collectionPath = getPath(revisionID, clusterName);
        if (collectionPath == null || collectionPath.length() == 0)
            return ""; //$NON-NLS-1$

        String encoded;
        try {
            encoded = URLEncoder.encode(collectionPath, "utf-8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException unlikely) {
            String err = "Unable to encode the collection path '" + collectionPath + "'. UTF-8 is not suported ?!?!";
            throw new XmlServerException(err);
        }
        // java.net.URLEncoder encodes space (' ') as a plus sign ('+'),
        // instead of %20 thus it will not be decoded properly by eXist when the
        // request is parsed. Therefore replace all '+' by '%20'.
        // If there would have been any plus signs in the original string, they would
        // have been encoded by URLEncoder.encode()
        // control = control.replace("+", "%20");//only works with JDK 1.5
        encoded = encoded.replaceAll("\\+", "%20"); //$NON-NLS-1$ //$NON-NLS-2$
        // %2F seems to be useless
        encoded = encoded.replaceAll("%2F", "/"); //$NON-NLS-1$ //$NON-NLS-2$

        return "collection(\"" + encoded + "\")"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /***********************************************************************
     *
     * Helper Methods
     *
     ***********************************************************************/

    /**
     * 
     * @param revisionID
     * @param clusterName
     * @return
     */
    public static String getPath(String revisionID, String clusterName) {
        return CommonUtil.getPath(revisionID, clusterName);
    }

    public static boolean isHead(String revisionID) {
        if (revisionID != null)
            revisionID = revisionID.replaceAll("\\[HEAD\\]|HEAD", ""); //$NON-NLS-1$ //$NON-NLS-2$
        return (revisionID == null || revisionID.length() == 0);
    }

    private static Pattern pathWithoutConditions = Pattern.compile("(.*?)[\\[|/].*"); //$NON-NLS-1$

    /**
     * Returns the first part - eg. the concept - from the path
     * 
     * @param path
     * @return the Concept
     */
    public static String getRootElementNameFromPath(String path) {
        if (!path.endsWith("/")) //$NON-NLS-1$
            path += "/"; //$NON-NLS-1$
        Matcher m = pathWithoutConditions.matcher(path);
        if (m.matches())
            return m.group(1);
        return null;
    }

    protected String getXQueryMatchFunction() {
        return "matches"; //$NON-NLS-1$
    }

    protected String getMatchesMethod(String sourceStr, String matchStr) {
        StringBuilder result = new StringBuilder();
        result.append(' ');
        result.append(getXQueryMatchFunction());
        result.append('(');
        result.append(sourceStr);
        result.append(", \""); //$NON-NLS-1$
        result.append(matchStr);
        result.append(".*\" ,\"i\") "); //$NON-NLS-1$
        return result.toString();
    }

    /**
     * FullText Query String
     * 
     * @return
     */
    protected abstract String getFullTextQueryString(String queryStr);

    /**
     * Default Paging
     * 
     * @return
     */
    public String getPagingString(boolean withTotalCountOnFirstRow, PartialXQLPackage partialXQLPackage, long start, long limit,
            String rawQuery) {
        boolean subsequence = (start >= 0 && limit >= 0 && limit != Integer.MAX_VALUE);
        StringBuilder query = new StringBuilder();
        if (subsequence) {
            if (!partialXQLPackage.isUseSubsequenceFirst()) {
                if (withTotalCountOnFirstRow) {
                    query.append("let $_page_ :=\n"); //$NON-NLS-1$
                    query.append(rawQuery);
                    query.append("\nreturn (<totalCount>{").append(getCountExpr(partialXQLPackage)); //$NON-NLS-1$
                    query.append("}</totalCount>, subsequence($_page_,"); //$NON-NLS-1$
                    query.append(start + 1).append(',').append(limit);
                    query.append("))"); //$NON-NLS-1$
                } else {
                    query.append("let $_page_ :=\n"); //$NON-NLS-1$
                    query.append(rawQuery);
                    query.append("\nreturn subsequence($_page_,"); //$NON-NLS-1$
                    query.append(start + 1).append(',').append(limit);
                    query.append(")"); //$NON-NLS-1$
                }
            } else {
                if (withTotalCountOnFirstRow) {
                    query.append("let $_page_ :=\n"); //$NON-NLS-1$
                    query.append(rawQuery);
                    query.append("\nreturn (<totalCount>{").append(getCountExpr(partialXQLPackage)); //$NON-NLS-1$
                    query.append("}</totalCount>, $_page_)"); //$NON-NLS-1$
                } else {
                    query.append(rawQuery);
                }
            }
        } else {
            if (withTotalCountOnFirstRow) {
                query.append("let $_page_ :=\n"); //$NON-NLS-1$
                query.append(rawQuery);
                query.append("\nreturn (<totalCount>{").append(getCountExpr(partialXQLPackage)); //$NON-NLS-1$
                query.append("}</totalCount>, $_page_)"); //$NON-NLS-1$
            } else {
                query.append(rawQuery);
            }
        }
        return query.toString();
    }

    protected String buildPKsByCriteriaQuery(ItemPKCriteria criteria) {
        String revisionId = criteria.getRevisionId();
        String clusterName = criteria.getClusterName();
        String collectionpath = CommonUtil.getPath(revisionId, clusterName);
        String matchesStr = getXQueryMatchFunction();

        StringBuilder query = new StringBuilder();
        query.append("let $allres := collection(\""); //$NON-NLS-1$
        query.append(collectionpath);
        query.append("\")/ii"); //$NON-NLS-1$

        String wsContentKeywords = criteria.getContentKeywords();
        boolean useFTSearch = criteria.isUseFTSearch();
        if (!useFTSearch && wsContentKeywords != null && wsContentKeywords.length() != 0)
            query.append("[").append(matchesStr).append("(./p/* , '").append(wsContentKeywords).append("')]");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        Long fromDate = criteria.getFromDate().longValue();
        if (fromDate > 0)
            query.append("[./t >= ").append(fromDate).append("]"); //$NON-NLS-1$ //$NON-NLS-2$

        Long toDate = criteria.getToDate().longValue();
        if (toDate > 0)
            query.append("[./t <= ").append(toDate).append("]"); //$NON-NLS-1$ //$NON-NLS-2$

        String keyKeywords = criteria.getKeysKeywords();
        if (keyKeywords != null && keyKeywords.length() != 0) {
            if (!criteria.isCompoundKeyKeywords())
                query.append('[').append(matchesStr).append("(./i , '").append(keyKeywords).append("')]"); //$NON-NLS-1$ //$NON-NLS-2$
            else {
                // keyKeywords of the form 'key$xpath$fkvalue'
                // fkvalue of the form '[fk1@fk2@fk3....]'
                int valueIndex = keyKeywords.lastIndexOf("$"); //$NON-NLS-1$
                String fkvalue = (valueIndex == -1) ? null : keyKeywords.substring(valueIndex + 1);
                int keyIndex = (valueIndex == -1) ? -1 : keyKeywords.indexOf("$"); //$NON-NLS-1$
                String fkxpath = (keyIndex == -1) ? null : keyKeywords.substring(keyIndex + 1, valueIndex);
                String key = (keyIndex == -1) ? null : keyKeywords.substring(0, keyIndex);

                if (key != null && key.length() != 0)
                    query.append("[").append(matchesStr).append("(./i , '").append(key).append("')]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                if (fkxpath != null && fkxpath.length() != 0 && fkvalue != null && fkvalue.length() != 0) {
                    //fkvalue can be composite
                    fkvalue = StringUtils.replace(fkvalue, "@", "]["); //$NON-NLS-1$ //$NON-NLS-2$
                    query.append("[").append("./p//" + fkxpath + " eq '").append(fkvalue).append("']"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                }
            }
        }

        String wsConceptName = criteria.getConceptName();
        if (useFTSearch && wsContentKeywords != null && wsContentKeywords.length() != 0) {
            if (MDMConfiguration.isExistDb()) {
                String concept = wsConceptName != null ? "p/" + wsConceptName : "."; //$NON-NLS-1$ //$NON-NLS-2$
                query.append("[ft:query(").append(concept).append(",\"").append(wsContentKeywords).append("\")]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            } else {
                query.append("[. contains text \"").append(wsContentKeywords).append("\"] "); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        if (wsConceptName != null)
            query.append("[./n eq '").append(wsConceptName).append("']"); //$NON-NLS-1$ //$NON-NLS-2$

        int start = criteria.getSkip();
        int limit = criteria.getMaxItems();

        query.append("\nlet $res := for $ii in subsequence($allres, ").append(start + 1).append(",").append(limit) //$NON-NLS-1$ //$NON-NLS-2$
                .append(")\n"); //$NON-NLS-1$
        query.append("return <r>{$ii/t}{$ii/taskId}{$ii/n}<ids>{$ii/i}</ids></r>\n"); //$NON-NLS-1$

        // Determine Query based on number of results an counts
        query.append("return (<totalCount>{count($allres)}</totalCount>, $res)"); //$NON-NLS-1$

        if (LOG.isDebugEnabled())
            LOG.debug(query);
        return query.toString();
    }
}

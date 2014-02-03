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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.amalto.xmldb.util.PartialXQLPackage;
import com.amalto.xmldb.util.QueryBuilderContext;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
import com.amalto.xmlserver.interfaces.WhereOr;
import com.amalto.xmlserver.interfaces.XmlServerException;

public class ExistQueryBuilder extends QueryBuilder {

    private static final Logger LOG = Logger.getLogger(ExistQueryBuilder.class);

    @Override
    protected boolean useNumberFunction() {
        return true;
    }

    @Override
    public String getFullTextQueryString(String queryStr) {
        StringBuilder ftQueryBuilder = new StringBuilder();
        ftQueryBuilder.append("ft:query(.,\""); //$NON-NLS-1$
        ftQueryBuilder.append(StringEscapeUtils.escapeXml(queryStr.trim()));
        ftQueryBuilder.append("\")"); //$NON-NLS-1$
        return ftQueryBuilder.toString();
    }

    @Override
    public String getUriQuery(boolean isItemQuery, Map<String, String> objectRootElementNamesToRevisionID,
            Map<String, String> objectRootElementNamesToClusterName, String forceMainPivot, ArrayList<String> viewableFullPaths,
            IWhereItem whereItem, boolean withTotalCountOnFirstRow, Map<String, ArrayList<String>> metaDataTypes)
            throws XmlServerException {
        try {
            // build Pivots Map
            LinkedHashMap<String, String> pivotsMap = new LinkedHashMap<String, String>();
            if (forceMainPivot != null) {
                pivotsMap.put("$pivot0", forceMainPivot); //$NON-NLS-1$
            }

            PartialXQLPackage partialXQLPackage = new PartialXQLPackage();
            QueryBuilderContext queryBuilderContext = new QueryBuilderContext();

            String xqReturn = "base-uri($pivot0)"; //$NON-NLS-1$

            // build from WhereItem
            String xqWhere;
            if (whereItem == null) {
                xqWhere = StringUtils.EMPTY;
            } else {
                xqWhere = buildWhere(pivotsMap, whereItem, metaDataTypes);
            }
            partialXQLPackage.setXqWhere(xqWhere);

            // We don't *need* the "for" part, but this has side effect and populate
            // partialXQLPackage.getForInCollectionMap()... so call it anyway
            getXQueryFor(isItemQuery, objectRootElementNamesToRevisionID, objectRootElementNamesToClusterName, pivotsMap,
                    partialXQLPackage, queryBuilderContext);

            StringBuilder rawQueryStringBuffer = new StringBuilder();
            LinkedHashMap<String, String> forInCollectionMap = partialXQLPackage.getForInCollectionMap();
            partialXQLPackage.resetPivotWhereMap();
            Map<String, String> pivotWhereMap = partialXQLPackage.getPivotWhereMap();

            Set<Map.Entry<String, String>> entries = forInCollectionMap.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            String mainLoopVariable = null;
            Map<String, String> joinVariables = new HashMap<String, String>();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                String expr = entry.getValue();
                String root = entry.getKey();
                if (pivotWhereMap.get(root) != null && pivotWhereMap.get(root).length() > 0) {
                    expr = expr + " [ " + pivotWhereMap.get(root) + " ] "; //$NON-NLS-1$ //$NON-NLS-2$
                } else if (xqWhere.contains("../../t")) { // add modified condition if there is no pivotWhereMap  //$NON-NLS-1$
                    expr = expr + " [ " + xqWhere + " ] "; //$NON-NLS-1$ //$NON-NLS-2$
                }

                if (mainLoopVariable == null) {
                    rawQueryStringBuffer.append("for ").append(root).append(" in ").append(expr); //$NON-NLS-1$ //$NON-NLS-2$
                    mainLoopVariable = root;
                } else {
                    joinVariables.put(root, expr);
                }
            }

            // add joinkeys
            partialXQLPackage.resetPivotWhereMap();
            List<String> joinKeys = partialXQLPackage.getJoinKeys();
            Set<String> notEmptyForeignFields = Collections.emptySet();
            if (joinKeys.size() > 0) {
                notEmptyForeignFields = addJoinVariables(rawQueryStringBuffer, joinVariables, joinKeys, pivotsMap, whereItem);
                partialXQLPackage.setUseJoin(true);
            } else {
                partialXQLPackage.setUseJoin(false);
            }

            for (String notEmptyForeignField : notEmptyForeignFields) {
                rawQueryStringBuffer.append("\nwhere not(empty(").append(notEmptyForeignField).append("))"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            // TMDM-2368 Iterates over the different values of foreign key field(s).
            if (!notEmptyForeignFields.isEmpty()) {
                String innerJoinReturn = xqReturn;

                int level = 0;
                for (String notEmptyForeignField : notEmptyForeignFields) {
                    rawQueryStringBuffer.append(" return for $s") //$NON-NLS-1$
                            .append(level).append(" in ") //$NON-NLS-1$
                            .append(notEmptyForeignField);
                    innerJoinReturn = xqReturn.replace(notEmptyForeignField, "$s" + level); //$NON-NLS-1$
                    level++;
                }

                rawQueryStringBuffer.append(" return ") //$NON-NLS-1$
                        .append(innerJoinReturn).append('\n');

            } else {
                rawQueryStringBuffer.append("\nreturn ").append(xqReturn).append('\n'); //$NON-NLS-1$
            }
            String rawQuery = rawQueryStringBuffer.toString();

            if (LOG.isDebugEnabled()) {
                LOG.debug("query:\n"); //$NON-NLS-1$
                LOG.debug(rawQuery);
            }

            return rawQuery;
        } catch (XmlServerException e) {
            throw (e);
        } catch (Exception e) {
            throw new XmlServerException("Unable to build the Item XQuery", e);
        }
    }

    private Set<String> addJoinVariables(StringBuilder query, Map<String, String> joinVariables, List<String> joinKeys,
            LinkedHashMap<String, String> pivotsMap, IWhereItem whereItem) {
        // Not empty fields will be added to where clauses (to ensure they aren't empty).
        Map<String, JoinFieldDefinition> fkMap = new HashMap<String, JoinFieldDefinition>();
        Set<String> joinedTypes = new HashSet<String>();

        for (String joinKey : joinKeys) {
            String[] items = joinKey.split("\\b(or|and)\\b"); //$NON-NLS-1$
            for (String item : items) {
                String key = item.trim();
                if (key.matches("\\((.*?)\\)")) {//$NON-NLS-1$
                    key = key.replaceFirst("\\((.*?)\\)", "$1"); //$NON-NLS-1$ //$NON-NLS-2$
                }

                String[] splits = key.split(WhereCondition.JOINS);
                if (splits.length == 2) {
                    String leftV = splits[0].trim().replace("(", "").replace(")", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    String rightPivotName = splits[1].trim().replace("(", "").replace(")", "").split("/")[0]; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 
                    String rightV = splits[1].trim().replace("(", "").replace(")", "").split("/")[1]; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

                    JoinFieldDefinition definition = fkMap.get(rightPivotName);

                    // TMDM-2367 : Include where clauses for field names that must not be empty in case of inner join
                    String typeName = pivotsMap.get(rightPivotName);
                    joinedTypes.add(typeName);

                    if (definition == null) {
                        definition = new JoinFieldDefinition(rightV);
                        fkMap.put(rightPivotName, definition);
                    }

                    if (!definition.getLeftVariables().contains(leftV)) {
                        definition.getLeftVariables().add(leftV);
                    }
                }
            }
        }

        // TMDM-2367 : Include where clauses for field names that must not be empty in case of inner join
        Set<String> notEmptyFields = new HashSet<String>();
        try {
            LinkedHashMap<String, String> typeToPivot = new LinkedHashMap<String, String>();
            Set<Map.Entry<String, String>> entries = pivotsMap.entrySet();
            // Invert pivot map
            for (Map.Entry<String, String> entry : entries) {
                typeToPivot.put(entry.getValue(), entry.getKey());
            }
            getNotEmptyFields(whereItem, joinedTypes, notEmptyFields, typeToPivot);
        } catch (XmlServerException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, String> joinVariable : joinVariables.entrySet()) {
            String rightVariable = fkMap.get(joinVariable.getKey()).getRightVariable();
            List<String> leftVariables = fkMap.get(joinVariable.getKey()).getLeftVariables();

            if (leftVariables.size() == 1) {
                boolean hasCondition = joinVariable.getValue().contains("]"); //$NON-NLS-1$

                // Handle join to a single key field.
                query.append("\nlet ").append(joinVariable.getKey()) //$NON-NLS-1$
                        .append(" := ") //$NON-NLS-1$
                        .append(joinVariable.getValue());

                // "Fix" for TMDM-2367 : if joined collection expression already contains a condition, add the
                // fk resolution to the condition (and removes the last ']'.
                // "$pivot1=collection("/TMDM-2367/Type2")//p/Type2 [  matches(field1, "Text.*" ,"i" ) ]"
                // becomes "$pivot1=collection("/TMDM-2367/Type2")//p/Type2 [  matches(field1, "Text.*" ,"i" )"
                if (hasCondition) {
                    query.delete(query.toString().lastIndexOf(']'), query.toString().length() - 1);
                    query.append(" and "); //$NON-NLS-1$
                } else {
                    query.append("["); //$NON-NLS-1$
                }

                // Fix for TMDM-2368 Resolution of foreign key in sequence
                query.append(rightVariable).append(" = (for $r in ") //$NON-NLS-1$
                        .append(leftVariables.get(0)).append(" return substring-after(substring-before($r, \"]\"), \"[\"))"); //$NON-NLS-1$

                query.append("]"); //$NON-NLS-1$
            } else {
                // Handle join with a composite key (generates a concat).
                Iterator<String> leftVariablesIterator = leftVariables.iterator();
                String leftVariableValue = "concat("; //$NON-NLS-1$
                while (leftVariablesIterator.hasNext()) {
                    leftVariableValue += "\"[\"" + leftVariablesIterator.next() + "\"]\""; //$NON-NLS-1$ //$NON-NLS-2$
                    if (leftVariablesIterator.hasNext()) {
                        leftVariableValue += ","; //$NON-NLS-1$
                    }
                }

                query.append("\nlet ") //$NON-NLS-1$
                        .append(joinVariable.getKey()).append(" := ") //$NON-NLS-1$
                        .append(joinVariable.getValue()).append("[") //$NON-NLS-1$
                        .append(rightVariable).append(" = substring-after(substring-before(") //$NON-NLS-1$
                        .append(leftVariableValue).append(", \"]\"), \"[\") ]"); //$NON-NLS-1$
            }

        }

        return notEmptyFields;
    }

    private void getNotEmptyFields(IWhereItem whereItem, Set<String> joinedTypes, Set<String> notEmptyFields,
            LinkedHashMap<String, String> typeToPivot) throws XmlServerException {
        if (whereItem instanceof WhereLogicOperator) {
            if (whereItem instanceof WhereAnd) {
                List<IWhereItem> items = ((WhereAnd) whereItem).getItems();
                for (IWhereItem item : items) {
                    getNotEmptyFields(item, joinedTypes, notEmptyFields, typeToPivot);
                }
            } else if (whereItem instanceof WhereOr) {
                List<IWhereItem> items = ((WhereOr) whereItem).getItems();
                for (IWhereItem item : items) {
                    getNotEmptyFields(item, joinedTypes, notEmptyFields, typeToPivot);
                }
            }
        } else if (whereItem instanceof WhereCondition) {
            WhereCondition condition = (WhereCondition) whereItem;
            if (!WhereCondition.JOINS.equals(condition.getOperator())) {
                String leftPath = condition.getLeftPath();
                String[] splitPath = leftPath.split("/"); //$NON-NLS-1$
                if (joinedTypes.contains(splitPath[0])) {
                    notEmptyFields.add(typeToPivot.get(splitPath[0]) + "/" + splitPath[1]); //$NON-NLS-1$
                }
            }
        } else {
            throw new XmlServerException("Unknown Where Type : " + whereItem.getClass().getName());
        }
    }

    private static class JoinFieldDefinition {

        private String rightVariable;

        private List<String> leftVariables = new ArrayList<String>();

        private JoinFieldDefinition(String rightVariable) {
            this.rightVariable = rightVariable;
        }

        public String getRightVariable() {
            return rightVariable;
        }

        public List<String> getLeftVariables() {
            return leftVariables;
        }
    }
}

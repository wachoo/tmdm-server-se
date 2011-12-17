package com.amalto.webapp.v3.hierarchical.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.amalto.webapp.core.bean.ComboItemBean;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetItemsPivotIndex;
import com.amalto.webapp.util.webservices.WSGetItemsPivotIndexPivotWithKeysTypedContentEntry;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSGetViewPKs;
import com.amalto.webapp.util.webservices.WSLinkedHashMap;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.v3.hierarchical.bean.FilterItem;
import com.amalto.webapp.v3.hierarchical.bean.HierarchicalTreeCriterion;

public class HierarchicalUtil {

    public static final String HIERARCHICAL_TREE_CRITERION = "com.amalto.webapp.v3.hierarchical.bean.HierarchicalTreeCriterion";

    public static final String DERIVED_HIERARCHY_EXT_CRITERION = "com.amalto.webapp.v3.hierarchical.bean.DerivedHierarchyExtCriterion";

    public static final String HIERARCHICAL_TREE_UPDATEHISTORY = "com.amalto.webapp.v3.hierarchical.bean.UpdateHistory";

    public static final String DERIVED_HIERARCHY_TREE_UPDATEHISTORY = "com.amalto.webapp.v3.hierarchical.derived.bean.UpdateHistory";

    public static Map<String, String> getBusinessConcepts(String language) throws Exception {
        TreeMap<String, String> map = new TreeMap<String, String>();

        try {
            Configuration config = Configuration.getInstance(true);
            String dataModelPK = config.getModel();
            String[] bc = Util.getPort().getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(dataModelPK)))
                    .getStrings();
            String[] bcLabel = new String[bc.length];
            for (int i = 0; i < bc.length; i++) {
                map.put(bc[i], bcLabel[i] = CommonDWR.getConceptLabel(dataModelPK, bc[i], language));
            }
        } catch (Exception e2) {
            throw e2;
        }

        return map;
    }

    public static LinkedHashMap<String, String> getPossiblePivots(String businessConcept, String language) throws Exception {

        LinkedHashMap<String, String> possiblePivots = new LinkedHashMap<String, String>();
        try {
            HashMap<String, String> xpathToLabel = new HashMap<String, String>();
            Configuration config = Configuration.getInstance();
            String dataModelPK = config.getModel();
            xpathToLabel = CommonDWR.getFieldsByDataModel(dataModelPK, businessConcept, language, false, true);
            xpathToLabel.remove(businessConcept);

            // combine FK-Drill-Paths
            for (Iterator iterator = xpathToLabel.keySet().iterator(); iterator.hasNext();) {
                String xpath = (String) iterator.next();
                String label = xpathToLabel.get(xpath);

                possiblePivots.put(xpath, label);

                String refConcept = parseFKConcept(xpath);
                if (refConcept.length() > 0) {
                    // FIXME: Now only support the same data model
                    HashMap<String, String> xpathToLabel2 = CommonDWR.getFieldsByDataModel(dataModelPK, refConcept, language,
                            false);
                    xpathToLabel2.remove(refConcept);
                    for (Iterator iterator2 = xpathToLabel2.keySet().iterator(); iterator2.hasNext();) {
                        String xpath2 = (String) iterator2.next();
                        String label2 = xpathToLabel2.get(xpath2);

                        possiblePivots.put(xpath.substring(0, xpath.indexOf("@")) + "->" + xpath2, label + "->" + label2);
                    }
                }

            }
        } catch (Exception e1) {
            throw e1;
        }

        return possiblePivots;
    }

    private static String parseFKConcept(String xpath) {
        String refConcept = "";
        if (xpath.lastIndexOf("@FK") != -1) {
            String refPart = xpath.substring(xpath.lastIndexOf("@FK") + 4);
            String[] paths = refPart.split("/");
            if (paths.length > 0)
                refConcept = paths[0];
        }
        return refConcept;
    }

    public static HashMap<String, String> getTitleList(String businessConcept, String language) throws Exception {
        HashMap<String, String> xpathToLabel = new HashMap<String, String>();
        try {
            Configuration config = Configuration.getInstance();
            String dataModelPK = config.getModel();
            // FIXME:only support simple type
            xpathToLabel = CommonDWR.getFieldsByDataModel(dataModelPK, businessConcept, language, false);
            xpathToLabel.remove(businessConcept);
        } catch (Exception e) {
            throw e;
        }
        return xpathToLabel;
    }

    public static String[] getHierarchicalTreeView(String dataObjectName, String pivotPath, String titleFieldPath,
            FilterItem[] filters, String[] pivotDirections, String[] indexDirections, int limit) throws Exception {
        Configuration config = null;
        try {
            config = Configuration.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String dataClusterName = config.getCluster();
        String dataModelName = config.getModel();
        return getHierarchicalTreeView(dataClusterName, dataModelName, dataObjectName, pivotPath, titleFieldPath, filters,
                pivotDirections, indexDirections, limit);
    }

    public static String[] getHierarchicalTreeView(String dataClusterName, String dataModelName, String dataObjectName,
            String pivotPath, String titleFieldPath, FilterItem[] filters, String[] pivotDirections, String[] indexDirections,
            int limit) throws Exception {

        String[] result = null;

        WSGetItemsPivotIndex wsGetItemsPivotIndex;
        try {
            wsGetItemsPivotIndex = new WSGetItemsPivotIndex();

            // dataClusterName
            wsGetItemsPivotIndex.setClusterName(dataClusterName);

            // mainPivotName
            wsGetItemsPivotIndex.setMainPivotName(dataObjectName);

            // pivotWithKeys
            List<WSGetItemsPivotIndexPivotWithKeysTypedContentEntry> pivotWithKeysList = new ArrayList<WSGetItemsPivotIndexPivotWithKeysTypedContentEntry>();
            String[] pivotPaths = pivotPath.split("->");
            for (int i = 0; i < pivotPaths.length; i++) {
                pivotPath = pivotPaths[i];
                pivotPath = cleanPivotPath(pivotPath);
                String[] pks = CommonDWR.getBusinessConceptKeyPaths(dataModelName, parseDataConceptFromPivotPath(pivotPath));
                WSGetItemsPivotIndexPivotWithKeysTypedContentEntry typedContentEntry = new WSGetItemsPivotIndexPivotWithKeysTypedContentEntry(
                        pivotPath, new WSStringArray(pks));
                pivotWithKeysList.add(typedContentEntry);
            }
            WSLinkedHashMap pivotWithKeys = new WSLinkedHashMap(
                    pivotWithKeysList.toArray(new WSGetItemsPivotIndexPivotWithKeysTypedContentEntry[pivotWithKeysList.size()]));
            wsGetItemsPivotIndex.setPivotWithKeys(pivotWithKeys);

            // indexPaths
            wsGetItemsPivotIndex.setIndexPaths(new WSStringArray(new String[] { titleFieldPath }));

            // filters
            if (filters == null || filters.length == 0) {
                wsGetItemsPivotIndex.setWhereItem(null);
            } else {
                ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
                for (int i = 0; i < filters.length; i++) {
                    FilterItem filterItem = filters[i];
                    WSWhereCondition wc = new WSWhereCondition(filterItem.getFieldPath(), getOperator(filterItem.getOperator()),
                            filterItem.getValue(), WSStringPredicate.NONE, false);
                    WSWhereItem item = new WSWhereItem(wc, null, null);
                    conditions.add(item);
                }
                WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
                WSWhereItem wi = new WSWhereItem(null, and, null);
                wsGetItemsPivotIndex.setWhereItem(wi);
            }

            // TODO directions
            if (pivotDirections == null) {
                wsGetItemsPivotIndex.setPivotDirections(null);
            } else {
                wsGetItemsPivotIndex.setPivotDirections(new WSStringArray(pivotDirections));
            }
            // wsGetItemsPivotIndex.setPivotDirections(new WSStringArray(new
            // String[]{"ascending","descending"}));
            if (indexDirections == null) {
                wsGetItemsPivotIndex.setIndexDirections(null);
            } else {
                wsGetItemsPivotIndex.setIndexDirections(new WSStringArray(indexDirections));
            }

            // wsGetItemsPivotIndex.setIndexDirections(new WSStringArray(new
            // String[]{"descending"}));

            // TODO paging
            wsGetItemsPivotIndex.setStart(0);
            wsGetItemsPivotIndex.setLimit(limit);
            // wsGetItemsPivotIndex.setLimit(2);

            result = Util.getPort().getItemsPivotIndex(wsGetItemsPivotIndex).getStrings();

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }

        return result;

    }

    public static String parseDataConceptFromPivotPath(String pivotPath) {
        String pivotPathParts[] = pivotPath.split("/");
        if (pivotPathParts.length == 0)
            return "";
        String dataConcept = pivotPathParts[0];
        return dataConcept;
    }

    public static String cleanPivotPath(String pivotPath) {
        if (pivotPath.indexOf("@FK") != -1)
            pivotPath = pivotPath.substring(0, pivotPath.indexOf("@FK"));
        return pivotPath;
    }

    /**
     * gives the operator associated to the string 'option'
     * 
     * @param option
     * @return
     */
    public static WSWhereOperator getOperator(String option) {
        WSWhereOperator res = null;
        if (option.equalsIgnoreCase("CONTAINS"))
            res = WSWhereOperator.CONTAINS;
        else if (option.equalsIgnoreCase("EQUALS"))
            res = WSWhereOperator.EQUALS;
        else if (option.equalsIgnoreCase("GREATER_THAN"))
            res = WSWhereOperator.GREATER_THAN;
        else if (option.equalsIgnoreCase("GREATER_THAN_OR_EQUAL"))
            res = WSWhereOperator.GREATER_THAN_OR_EQUAL;
        else if (option.equalsIgnoreCase("JOIN"))
            res = WSWhereOperator.JOIN;
        else if (option.equalsIgnoreCase("LOWER_THAN"))
            res = WSWhereOperator.LOWER_THAN;
        else if (option.equalsIgnoreCase("LOWER_THAN_OR_EQUAL"))
            res = WSWhereOperator.LOWER_THAN_OR_EQUAL;
        else if (option.equalsIgnoreCase("NOT_EQUALS"))
            res = WSWhereOperator.NOT_EQUALS;
        else if (option.equalsIgnoreCase("STARTSWITH"))
            res = WSWhereOperator.STARTSWITH;
        else if (option.equalsIgnoreCase("STRICTCONTAINS"))
            res = WSWhereOperator.STRICTCONTAINS;
        return res;
    }

    public static String parseLastPartFromXpath(String input) {
        String rtn = "";
        String[] tmp = input.split("/");
        if (tmp.length > 0) {
            rtn = tmp[tmp.length - 1];
        }
        return rtn;
    }

    public static void equipDirection2HierarchicalTreeCriterion(HierarchicalTreeCriterion criterion, String orderExpr) {

        String[] pivotDirections = null;
        String[] indexDirections = null;

        if (orderExpr == null || orderExpr.length() == 0) {
            // input empty case
            criterion.setPivotDirections(null);
            criterion.setIndexDirections(null);
        } else {
            String[] orderExpressions = orderExpr.split("-");

            String pivotPath = criterion.getPivotPath();
            int pivotLength = pivotPath.split("->").length;
            int indexLength = 1;
            int targetOELength = pivotLength + indexLength;

            if (orderExpressions.length != targetOELength) {
                // format error case
                criterion.setPivotDirections(null);
                criterion.setIndexDirections(null);
            } else {
                // normal case
                pivotDirections = new String[pivotLength];
                for (int i = orderExpressions.length - 1 - indexLength, j = 0; i > -1; i--, j++) {
                    pivotDirections[j] = mapKeywordsOfDirection(orderExpressions[i]);
                }
                indexDirections = new String[] { mapKeywordsOfDirection(orderExpressions[orderExpressions.length - 1]) };

                criterion.setPivotDirections(pivotDirections);
                criterion.setIndexDirections(indexDirections);
            }
        }

    }

    private static String mapKeywordsOfDirection(String input) {
        String output = "";
        if (input != null && input.length() > 0) {
            if (input.trim().toUpperCase().equals("ASC")) {
                output = "ascending";
            } else if (input.trim().toUpperCase().equals("DESC")) {
                output = "descending";
            }
        }
        return output;
    }

    public static void equipLimit2HierarchicalTreeCriterion(HierarchicalTreeCriterion criterion, int limit) {
        // parse
        if (limit == -1)
            limit = Integer.MAX_VALUE;
        // equip
        criterion.setLimit(limit);

    }

    // remove the concept which user can not access from ListRange
    public static void synchronizeConecptList(ListRange listRange, Map<String, String> map) {
        Object[] ObjectArray = listRange.getData();
        ArrayList<ComboItemBean> comboItemBeanList = new ArrayList<ComboItemBean>();
        for (int i = 0; i < ObjectArray.length; i++) {
            ComboItemBean comboItemBean = (ComboItemBean) ObjectArray[i];
            comboItemBeanList.add(comboItemBean);
        }
        Collection collection = map.values();
        int x = comboItemBeanList.size();
        ArrayList<ComboItemBean> removeList = new ArrayList<ComboItemBean>();
        for (int i = 0; i < x; i++) {
            ComboItemBean comboItemBean = comboItemBeanList.get(i);
            boolean flag = false;
            for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
                String concept = (String) iterator.next();
                if (comboItemBean.getValue().equals(concept)) {
                    flag = !flag;
                }
            }
            if (!flag) {
                removeList.add(comboItemBean);
            }
        }
        for (Iterator iterator = removeList.iterator(); iterator.hasNext();) {
            ComboItemBean comboItemBean = (ComboItemBean) iterator.next();
            comboItemBeanList.remove(comboItemBean);
        }
        ObjectArray = comboItemBeanList.toArray();
        listRange.setData(ObjectArray);

    }

    // get the concept which user can access
    public static Map<String, String> getViewsList(String language) throws RemoteException, Exception {
        Configuration config = Configuration.getInstance(true);
        String model = config.getModel();
        String[] businessConcept = Util.getPort().getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(model)))
                .getStrings();
        ArrayList<String> bc = new ArrayList<String>();
        for (int i = 0; i < businessConcept.length; i++) {
            bc.add(businessConcept[i]);
        }
        WSViewPK[] wsViewsPK = Util.getPort().getViewPKs(new WSGetViewPKs("Browse_items.*")).getWsViewPK();
        String[] names = new String[wsViewsPK.length];
        TreeMap<String, String> views = new TreeMap<String, String>();
        // Pattern p = Pattern.compile(".*\\[" + language.toUpperCase()
        // + ":(.*?)\\].*", Pattern.DOTALL);
        for (int i = 0; i < wsViewsPK.length; i++) {
            WSView wsview = Util.getPort().getView(new WSGetView(wsViewsPK[i]));
            String concept = wsview.getName().replaceAll("Browse_items_", "").replaceAll("#.*", "");
            names[i] = wsViewsPK[i].getPk();
            if (bc.contains(concept)) {
                // views.put(wsview.getName(), p.matcher(wsview.getDescription())
                // .replaceAll("$1"));
                views.put(wsview.getName(), concept); // ctoum 2010-01-10
            }
        }
        return CommonDWR.getMapSortedByValue(views);
    }

    private static String changeConceptName4Xpath(String newConcept, String xpath) {
        if (xpath.indexOf("/") != -1) {
            xpath = newConcept + xpath.substring(xpath.indexOf("/"));
        }
        return xpath;
    }

    private static String stripIDs(String input) {

        if (input != null && input.length() > 0) {
            input = input.trim();
            if (input.startsWith("[") && input.endsWith("]")) {
                input = input.replaceAll("\\[", ".");
                input = input.replaceAll("\\]", "");
                input = input.substring(1);
            }
        }

        return input;

    }

}

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
package org.talend.mdm.webapp.base.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.server.util.XmlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;

import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSCountItemsByCustomFKFilters;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItemsByCustomFKFilters;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class ForeignKeyHelper {

    private static final Logger LOG = Logger.getLogger(ForeignKeyHelper.class);

    public static ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model,
            String dataClusterPK, boolean ifFKFilter, String value) {

        try {
            ForeignKeyHolder holder = getForeignKeyHolder(model, ifFKFilter, value);
            String[] results;
            String count;

            if (holder != null) {
                String conceptName = holder.conceptName;
                List<String> xPaths = holder.xpaths;
                String orderbyPath = holder.orderbyPath;
                WSWhereItem whereItem = holder.whereItem;
                String fkFilter = holder.fkFilter;

                // Run the query
                if (!com.amalto.webapp.core.util.Util.isCustomFilter(fkFilter)) {
                    results = CommonUtil
                            .getPort()
                            .xPathsSearch(
                                    new WSXPathsSearch(new WSDataClusterPK(dataClusterPK), null, new WSStringArray(xPaths
                                            .toArray(new String[xPaths.size()])), whereItem, -1, config.getOffset(), config
                                            .getLimit(), orderbyPath, null)).getStrings();
                    count = CommonUtil.getPort()
                            .count(new WSCount(new WSDataClusterPK(dataClusterPK), conceptName, whereItem, -1)).getValue();

                } else {

                    String injectedXpath = com.amalto.webapp.core.util.Util.getInjectedXpath(fkFilter);
                    results = CommonUtil
                            .getPort()
                            .getItemsByCustomFKFilters(
                                    new WSGetItemsByCustomFKFilters(new WSDataClusterPK(dataClusterPK), conceptName,
                                            new WSStringArray(xPaths.toArray(new String[xPaths.size()])), injectedXpath, config
                                                    .getOffset(), config.getLimit(), orderbyPath, null)).getStrings();

                    count = CommonUtil
                            .getPort()
                            .countItemsByCustomFKFilters(
                                    new WSCountItemsByCustomFKFilters(new WSDataClusterPK(dataClusterPK), conceptName,
                                            injectedXpath)).getValue();
                }
            } else {
                results = null;
                count = null;
            }

            List<ForeignKeyBean> fkBeans = new ArrayList<ForeignKeyBean>();
            if (results != null) {
                for (String result : results) {
                    ForeignKeyBean bean = new ForeignKeyBean();
                    String id = ""; //$NON-NLS-1$
                    List<Node> nodes = XmlUtil.getValuesFromXPath(XmlUtil.parseText(result), "//i"); //$NON-NLS-1$
                    if (nodes != null) {
                        for (Node node : nodes) {
                            id += "[" + (node.getText() == null ? "" : node.getText()) + "]"; //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
                        }
                    }
                    bean.setId(id);
                    if (result != null) {
                        Element root = XmlUtil.parseText(result).getRootElement();
                        if (root.getName().equals("result"))//$NON-NLS-1$
                            initFKBean(root, bean);
                        else
                            bean.set(root.getName(), root.getTextTrim());
                    }
                    fkBeans.add(bean);
                }
            }

            return new ItemBasePageLoadResult<ForeignKeyBean>(fkBeans, config.getOffset(), Integer.valueOf(count));
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    protected static class ForeignKeyHolder {

        String conceptName;

        List<String> xpaths;

        String orderbyPath;

        WSWhereItem whereItem;

        String fkFilter;
    }

    protected static ForeignKeyHolder getForeignKeyHolder(TypeModel model, boolean ifFKFilter, String value) throws Exception {

        String xpathForeignKey = model.getForeignkey();
        // to verify
        String xpathInfoForeignKey = model.getForeignKeyInfo().toString().replaceAll("\\[", "").replaceAll("\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        String fkFilter = (ifFKFilter) ? model.getFkFilter() : ""; // in search panel, the fkFilter is empty //$NON-NLS-1$

        if (xpathForeignKey == null)
            return null;

        String initxpathForeignKey = ""; //$NON-NLS-1$
        initxpathForeignKey = com.amalto.webapp.core.util.Util.getForeignPathFromPath(xpathForeignKey);

        WSWhereCondition whereCondition = com.amalto.webapp.core.util.Util.getConditionFromPath(xpathForeignKey);
        WSWhereItem whereItem = null;
        if (whereCondition != null) {
            whereItem = new WSWhereItem(whereCondition, null, null);
        }

        // get FK filter
        WSWhereItem fkFilterWi = com.amalto.webapp.core.util.Util.getConditionFromFKFilter(xpathForeignKey, xpathForeignKey,
                fkFilter);
        if (fkFilterWi != null)
            whereItem = fkFilterWi;
        initxpathForeignKey = initxpathForeignKey.split("/")[0]; //$NON-NLS-1$

        xpathInfoForeignKey = xpathInfoForeignKey == null ? "" : xpathInfoForeignKey; //$NON-NLS-1$
        // foreign key set by business concept
        if (initxpathForeignKey.split("/").length == 1) { //$NON-NLS-1$
            String conceptName = initxpathForeignKey;
            // determine if we have xPath Infos: e.g. labels to display
            String[] xpathInfos = new String[1];
            if (!"".equals(xpathInfoForeignKey) && xpathInfoForeignKey != null)//$NON-NLS-1$
                xpathInfos = xpathInfoForeignKey.split(","); //$NON-NLS-1$
            else
                xpathInfos[0] = conceptName;
            value = value == null ? "" : value; //$NON-NLS-1$

            // build query - add a content condition on the pivot if we search for a particular value
            String filteredConcept = conceptName;

            if (value != null && !"".equals(value.trim()) && !".*".equals(value.trim())) { //$NON-NLS-1$ //$NON-NLS-2$
                List<WSWhereItem> condition = new ArrayList<WSWhereItem>();
                if (whereItem != null)
                    condition.add(whereItem);

                // Although we should filter within FK and FKInfo values, to keep some backward compatibility with
                // previous behavior, filtering is done on all of the values of a record
                String fkWhere;
                if (MDMConfiguration.getDBType().getName().equals(EDBType.QIZX.getName())) {
                    fkWhere = conceptName + "/../* CONTAINS " + value; //$NON-NLS-1$
                } else {
                    fkWhere = conceptName + "/../. CONTAINS " + value; //$NON-NLS-1$
                }

                WSWhereItem wc = com.amalto.webapp.core.util.Util.buildWhereItems(fkWhere);
                condition.add(wc);
                WSWhereAnd and = new WSWhereAnd(condition.toArray(new WSWhereItem[condition.size()]));
                WSWhereItem whand = new WSWhereItem(null, and, null);
                if (whand != null)
                    whereItem = whand;
            }

            // add the xPath Infos Path
            ArrayList<String> xPaths = new ArrayList<String>();
            if (model.isRetrieveFKinfos())
                // add the xPath Infos Path
                for (int i = 0; i < xpathInfos.length; i++) {
                    xPaths.add(com.amalto.webapp.core.util.Util.getFormatedFKInfo(
                            xpathInfos[i].replaceFirst(conceptName, filteredConcept), filteredConcept));
                }
            // add the key paths last, since there may be multiple keys
            xPaths.add(filteredConcept + "/../../i"); //$NON-NLS-1$
            // order by
            String orderbyPath = null;
            if (!MDMConfiguration.getDBType().getName().equals(EDBType.QIZX.getName())) {
                if (!"".equals(xpathInfoForeignKey) && xpathInfoForeignKey != null) { //$NON-NLS-1$
                    orderbyPath = com.amalto.webapp.core.util.Util.getFormatedFKInfo(
                            xpathInfos[0].replaceFirst(conceptName, filteredConcept), filteredConcept);
                }
            }
            ForeignKeyHolder holder = new ForeignKeyHolder();
            holder.xpaths = xPaths;
            holder.orderbyPath = orderbyPath;
            holder.conceptName = conceptName;
            holder.whereItem = whereItem;
            holder.fkFilter = fkFilter;
            return holder;
        }
        return null;
    }

    private static void initFKBean(Element ele, ForeignKeyBean bean) {
        for (Object subEle : ele.elements()) {
            Element curEle = (Element) subEle;
            bean.set(curEle.getName(), curEle.getTextTrim());
            initFKBean(curEle, bean);
        }
    }
}

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
package org.talend.mdm.webapp.base.server;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.base.shared.XpathUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.dmagent.SchemaWebAgent;
import com.amalto.webapp.core.util.Util;
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

    private static final Pattern TOTAL_COUNT_PATTERN = Pattern.compile("<totalCount>(.*)</totalCount>"); //$NON-NLS-1$

    public static ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model,
            String dataClusterPK, boolean ifFKFilter, String value) throws Exception {

        ForeignKeyHolder holder = getForeignKeyHolder(model, ifFKFilter, value);
        String[] results = null;
        if (holder != null) {
            String conceptName = holder.conceptName;
            List<String> xPaths = holder.xpaths;
            String orderByPath = holder.orderbyPath;
            WSWhereItem whereItem = holder.whereItem;
            String fkFilter = holder.fkFilter;

            // Run the query
            if (!Util.isCustomFilter(fkFilter)) {
                results = CommonUtil
                        .getPort()
                        .xPathsSearch(
                                new WSXPathsSearch(new WSDataClusterPK(dataClusterPK), null, new WSStringArray(xPaths
                                        .toArray(new String[xPaths.size()])), whereItem, -1, config.getOffset(), config
                                        .getLimit(), orderByPath, null, true)).getStrings();
            } else {
                String injectedXpath = Util.getInjectedXpath(fkFilter);
                results = CommonUtil
                        .getPort()
                        .getItemsByCustomFKFilters(
                                new WSGetItemsByCustomFKFilters(new WSDataClusterPK(dataClusterPK), conceptName,
                                        new WSStringArray(xPaths.toArray(new String[xPaths.size()])), injectedXpath, config
                                                .getOffset(), config.getLimit(), orderByPath, null, true, whereItem))
                        .getStrings();
            }
        }

        List<ForeignKeyBean> fkBeans = new ArrayList<ForeignKeyBean>();

        if (results != null) {
            if (LOG.isDebugEnabled()) {
                for (int index = 0; index < results.length; index++) {
                    LOG.debug(results[index]);
                }
            }

            String fk = model.getForeignkey().split("/")[0]; //$NON-NLS-1$

            for (int currentResult = 1; currentResult < results.length; currentResult++) { // TMDM-2834: first
                                                                                           // result is count
                if (results[currentResult] == null) {
                    // Ignore null results.
                    continue;
                }
                Element resultAsDOM = Util.parse(results[currentResult]).getDocumentElement();

                ForeignKeyBean bean = new ForeignKeyBean();
                BusinessConcept businessConcept = SchemaWebAgent.getInstance().getBusinessConcept(fk);
                if (businessConcept != null && businessConcept.getCorrespondTypeName() != null) {
                    bean.setTypeName(businessConcept.getCorrespondTypeName());
                    bean.setConceptName(fk);
                }
                String id = ""; //$NON-NLS-1$
                NodeList nodes = Util.getNodeList(resultAsDOM, "//i"); //$NON-NLS-1$
                if (nodes != null) {
                    for (int i = 0; i < nodes.getLength(); i++) {
                        if (nodes.item(i) instanceof Element) {
                            id += "[" + (nodes.item(i).getTextContent() == null ? "" : nodes.item(i).getTextContent()) + "]"; //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$
                        }
                    }
                }

                if (resultAsDOM.getNodeName().equals("result")) { //$NON-NLS-1$
                    initFKBean(resultAsDOM, bean, fk, model.getForeignKeyInfo());
                } else {
                    bean.set(resultAsDOM.getNodeName(), resultAsDOM.getTextContent().trim());
                }

                bean.setId(id);
                fkBeans.add(bean);
            }

            Matcher matcher = TOTAL_COUNT_PATTERN.matcher(results[0]);
            String count;
            if (matcher.matches()) {
                count = matcher.group(1);
            } else {
                throw new IllegalArgumentException("Total count '" + results[0] + "' does not match expected format"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            return new ItemBasePageLoadResult<ForeignKeyBean>(fkBeans, config.getOffset(), Integer.valueOf(count));
        }

        return new ItemBasePageLoadResult<ForeignKeyBean>(fkBeans, config.getOffset(), 0);
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
        if (xpathForeignKey == null) {
            return null;
        }

        // to verify
        String xpathInfoForeignKey = model.getForeignKeyInfo().toString().replaceAll("\\[", "").replaceAll("\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        // in search panel, the fkFilter is empty
        String fkFilter;
        if (ifFKFilter)
            fkFilter = model.getFkFilter().replaceAll("&quot;", "\""); //$NON-NLS-1$ //$NON-NLS-2$
        else
            fkFilter = ""; //$NON-NLS-1$

        String initxpathForeignKey = Util.getForeignPathFromPath(xpathForeignKey);

        WSWhereCondition whereCondition = Util.getConditionFromPath(xpathForeignKey);
        WSWhereItem whereItem = null;
        if (whereCondition != null) {
            whereItem = new WSWhereItem(whereCondition, null, null);
        }

        if (!Util.isCustomFilter(fkFilter)) {
            // get FK filter
            WSWhereItem fkFilterWi = Util.getConditionFromFKFilter(xpathForeignKey, xpathInfoForeignKey, fkFilter);
            if (fkFilterWi != null)
                whereItem = fkFilterWi;
        }

        initxpathForeignKey = initxpathForeignKey.split("/")[0]; //$NON-NLS-1$

        if (xpathInfoForeignKey == null)
            xpathInfoForeignKey = ""; //$NON-NLS-1$

        // foreign key set by business concept
        if (initxpathForeignKey.split("/").length == 1) { //$NON-NLS-1$
            String conceptName = initxpathForeignKey;
            // determine if we have xPath Infos: e.g. labels to display
            String[] xpathInfos = new String[1];
            if (xpathInfoForeignKey.trim().length() != 0) {
                xpathInfos = xpathInfoForeignKey.split(","); //$NON-NLS-1$
            } else {
                xpathInfos[0] = initxpathForeignKey;
            }
            value = value == null ? "" : value; //$NON-NLS-1$

            // build query - add a content condition on the pivot if we search for a particular value
            if (value != null && !"".equals(value.trim()) && !".*".equals(value.trim())) { //$NON-NLS-1$ //$NON-NLS-2$
                List<WSWhereItem> condition = new ArrayList<WSWhereItem>();
                if (whereItem != null) {
                    condition.add(whereItem);
                }
                String fkWhere = initxpathForeignKey + "/../* CONTAINS " + value; //$NON-NLS-1$
                if (xpathInfoForeignKey.trim().length() > 0) {
                    StringBuffer ids = new StringBuffer();
                    String realXpathForeignKey = null; // In studio, ForeignKey = ConceptName, but not ConceptName/Id
                    if (xpathForeignKey.indexOf("/") == -1) { //$NON-NLS-1$
                        String[] fks = Util.getBusinessConceptKeys(conceptName);
                        if (fks != null && fks.length > 0) {
                            realXpathForeignKey = fks[0];
                            for (int i = 0; i < fks.length; i++) {
                                ids.append(fks[i] + " CONTAINS " + value); //$NON-NLS-1$
                                if (i != fks.length - 1)
                                    ids.append(" OR "); //$NON-NLS-1$
                            }
                        }
                    }
                    StringBuffer sb = new StringBuffer();
                    for (String fkInfo : xpathInfos) {
                        sb.append((fkInfo.startsWith(".") ? XpathUtil.convertAbsolutePath( //$NON-NLS-1$
                                (realXpathForeignKey != null && realXpathForeignKey.trim().length() > 0) ? realXpathForeignKey
                                        : xpathForeignKey, fkInfo) : fkInfo)
                                + " CONTAINS " + value); //$NON-NLS-1$
                        sb.append(" OR "); //$NON-NLS-1$
                    }
                    if (realXpathForeignKey != null)
                        sb.append(ids.toString());
                    else
                        sb.append(xpathForeignKey + " CONTAINS " + value); //$NON-NLS-1$
                    fkWhere = sb.toString();
                }

                WSWhereItem wc = Util.buildWhereItems(fkWhere);
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
                for (String xpathInfo : xpathInfos) {
                    xPaths.add(Util.getFormatedFKInfo(xpathInfo.replaceFirst(initxpathForeignKey, initxpathForeignKey),
                            initxpathForeignKey));
                }
            // add the key paths last, since there may be multiple keys
            xPaths.add(initxpathForeignKey + "/../../i"); //$NON-NLS-1$
            // order by
            String orderbyPath = null;
            if (!MDMConfiguration.getDBType().getName().equals(EDBType.QIZX.getName())) {
                if (xpathInfoForeignKey.length() != 0) {
                    orderbyPath = Util.getFormatedFKInfo(xpathInfos[0].replaceFirst(initxpathForeignKey, initxpathForeignKey),
                            initxpathForeignKey);
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

    private static void initFKBean(Element ele, ForeignKeyBean bean, String fk, List<String> getForeignKeyInfos) {
        for (int i = 0; i < ele.getChildNodes().getLength(); i++) {
            if (ele.getChildNodes().item(i) instanceof Element) {
                Element curEle = (Element) ele.getChildNodes().item(i);
                bean.set(curEle.getNodeName(), curEle.getTextContent().trim());
                if (getForeignKeyInfos.contains(fk + "/" + curEle.getNodeName())) { //$NON-NLS-1$
                    bean.getForeignKeyInfo().put(fk + "/" + curEle.getNodeName(), curEle.getTextContent().trim()); //$NON-NLS-1$
                }
                initFKBean(curEle, bean, fk, getForeignKeyInfos);
            }
        }
    }
}
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
package org.talend.mdm.webapp.itemsbrowser2.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.scb.gwt.web.server.i18n.GWTI18N;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.ItemsbrowserMessages;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.SearchTemplate;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.ItemHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.RoleHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.ViewHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.amalto.webapp.core.bean.UpdateReportItem;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSCountItemsByCustomFKFilters;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSDropItem;
import com.amalto.webapp.util.webservices.WSDroppedItemPK;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetItemsByCustomFKFilters;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSGetViewPKs;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSPutItemWithReport;
import com.amalto.webapp.util.webservices.WSRouteItemV2;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * DOC HSHU class global comment. Detailled comment
 * 
 * Customize MDM Jboss related methods here
 */
public class ItemServiceCommonHandler extends ItemsServiceImpl {

    private static final Logger LOG = Logger.getLogger(ItemServiceCommonHandler.class);

    public static ItemsbrowserMessages MESSAGES = null;// FIXME check NPE

    private Object[] getItemBeans(String dataClusterPK, ViewBean viewBean, EntityModel entityModel, String criteria, int skip,
            int max, String sortDir, String sortCol) {

        int totalSize = 0;

        List<ItemBean> itemBeans = new ArrayList<ItemBean>();
        String concept = ViewHelper.getConceptFromDefaultViewName(viewBean.getViewPK());
        try {
            WSWhereItem wi = CommonUtil.buildWhereItems(criteria);
            String[] results = CommonUtil.getPort().viewSearch(
                    new WSViewSearch(new WSDataClusterPK(dataClusterPK), new WSViewPK(viewBean.getViewPK()), wi, -1, skip, max,
                            sortCol, sortDir)).getStrings();

            // TODO change ids to array?
            List<String> idsArray = new ArrayList<String>();
            for (int i = 0; i < results.length; i++) {

                if (i == 0) {
                    totalSize = Integer.parseInt(com.amalto.webapp.core.util.Util.parse(results[i]).getDocumentElement()
                            .getTextContent());
                    continue;
                }

                // aiming modify when there is null value in fields, the viewable fields sequence is the same as the
                // childlist of result
                if (!results[i].startsWith("<result>")) { //$NON-NLS-1$
                    results[i] = "<result>" + results[i] + "</result>"; //$NON-NLS-1$ //$NON-NLS-2$
                }

                Document doc = XmlUtil.parseText(results[i]);
                idsArray.clear();
                for (String key : entityModel.getKeys()) {
                    String id = XmlUtil.queryNode(doc, key.replaceFirst(concept + "/", "result/")).getText();//$NON-NLS-1$ //$NON-NLS-2$ 
                    if (id != null)
                        idsArray.add(id);
                }

                ItemBean itemBean = new ItemBean(concept, CommonUtil.joinStrings(idsArray, "."), results[i]);//$NON-NLS-1$ 
                dynamicAssemble(itemBean, entityModel);
                itemBeans.add(itemBean);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return new Object[] { itemBeans, totalSize };

    }

    public void dynamicAssemble(ItemBean itemBean, EntityModel entityModel) throws DocumentException {
        if (itemBean.getItemXml() != null) {
            Document docXml = XmlUtil.parseText(itemBean.getItemXml());
            Map<String, TypeModel> types = entityModel.getMetaDataTypes();
            Set<String> xpaths = types.keySet();
            for (String path : xpaths) {
                TypeModel typeModel = types.get(path);
                if (typeModel.isSimpleType()) {
                    List nodes = XmlUtil.getValuesFromXPath(docXml, path.substring(path.indexOf('/') + 1));//$NON-NLS-1$ 
                    if (nodes.size() > 0) {
                        Node value = (Node) nodes.get(0);
                        if (typeModel.isMultiOccurrence()) {
                            List<Serializable> list = new ArrayList<Serializable>();
                            for (Object node : nodes) {
                                list.add(((Node) node).getText());
                            }
                            itemBean.set(path, list);
                        } else {
                            itemBean.set(path, value.getText());
                        }
                    }
                }
            }
        }
    }

    /**
     * DOC HSHU Comment method "getView".
     */
    public ViewBean getView(String viewPk, String language) {
        try {

            ViewBean vb = new ViewBean();
            vb.setViewPK(viewPk);

            // get WSView
            WSView wsView = CommonUtil.getPort().getView(new WSGetView(new WSViewPK(viewPk)));

            // bind entity model
            String model = getCurrentDataModel();
            String concept = ViewHelper.getConceptFromDefaultViewName(viewPk);
            EntityModel entityModel = new EntityModel();
            DataModelHelper.parseSchema(model, concept, entityModel, RoleHelper.getUserRoles());
            vb.setBindingEntityModel(entityModel);

            // viewables
            String[] viewables = null;
            viewables = ViewHelper.getViewables(wsView);
            // FIXME remove viewableXpath
            if (viewables != null) {
                for (String viewable : viewables) {
                    vb.addViewableXpath(viewable);
                }
            }
            vb.setViewables(viewables);

            // searchables
            vb.setSearchables(ViewHelper.getSearchables(wsView, model, language, entityModel));

            return vb;
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public ItemResult saveItemBean(ItemBean item) {
        try {
            String message = null;
            int status = 0;

            // if update, check the item is modified by others?
            WSItemPK wsi = null;
            WSPutItemWithReport wsPutItemWithReport = new WSPutItemWithReport(new WSPutItem(new WSDataClusterPK(
                    getCurrentDataCluster()), item.getItemXml(), new WSDataModelPK(getCurrentDataModel()), true),
                    "genericUI", true); //$NON-NLS-1$
            wsi = CommonUtil.getPort().putItemWithReport(wsPutItemWithReport);

            if (com.amalto.webapp.core.util.Util.isTransformerExist("beforeSaving_" + item.getConcept())) { //$NON-NLS-1$
                String outputErrorMessage = wsPutItemWithReport.getSource();
                String errorCode = null;
                if (outputErrorMessage != null) {
                    org.w3c.dom.Document doc = com.amalto.webapp.core.util.Util.parse(outputErrorMessage);
                    // TODO what if multiple error nodes ?
                    String xpath = "/descendant::error"; //$NON-NLS-1$
                    org.w3c.dom.NodeList checkList=com.amalto.webapp.core.util.Util.getNodeList(doc, xpath);
                    org.w3c.dom.Node errorNode = null;
                    if (checkList!=null&&checkList.getLength()>0)errorNode=checkList.item(0);
                    if (errorNode!=null && errorNode instanceof org.w3c.dom.Element) {
                        org.w3c.dom.Element errorElement = (org.w3c.dom.Element) errorNode;
                        errorCode = errorElement.getAttribute("code"); //$NON-NLS-1$
                        org.w3c.dom.Node child = errorElement.getFirstChild();
                        if (child instanceof org.w3c.dom.Text)
                            message = ((org.w3c.dom.Text) child).getTextContent();
                    }
                }

                if ("0".equals(errorCode)) { //$NON-NLS-1$
                    if (message == null || message.length() == 0)
                        message = MESSAGES.save_process_validation_success();
                    status = ItemResult.SUCCESS;
                } else {
                    // Anything but 0 is unsuccessful
                    if (message == null || message.length() == 0)
                        message = MESSAGES.save_process_validation_failure();
                    status = ItemResult.FAILURE;
                }
            } else {
                message = "The record was saved successfully."; //$NON-NLS-1$
                status = ItemResult.SUCCESS;
            }
            return new ItemResult(status, message);
        } catch (Exception e) {
            ItemResult result;
            // TODO
            if (e.getLocalizedMessage().indexOf("routing failed:") == 0) {//$NON-NLS-1$ 
                String saveSUCCE = "Save item '" + item.getConcept() + "."//$NON-NLS-1$ //$NON-NLS-2$ 
                        + com.amalto.webapp.core.util.Util.joinStrings(new String[] { item.getIds() }, ".")//$NON-NLS-1$ 
                        + "' successfully, But " + e.getLocalizedMessage();//$NON-NLS-1$ 
                result = new ItemResult(ItemResult.FAILURE, saveSUCCE);
            } else {
                String err = "Unable to save item '" + item.getConcept() + "."//$NON-NLS-1$ //$NON-NLS-2$ 
                        + com.amalto.webapp.core.util.Util.joinStrings(new String[] { item.getIds() }, ".") + "'"//$NON-NLS-1$ //$NON-NLS-2$ 
                        + e.getLocalizedMessage();
                if (e.getLocalizedMessage().indexOf("ERROR_3:") == 0) {//$NON-NLS-1$
                    err = e.getLocalizedMessage();
                }
                result = new ItemResult(ItemResult.FAILURE, err);
            }
            return result;
        }
    }

    public ItemResult deleteItemBean(ItemBean item) {
        try {
            String dataClusterPK = getCurrentDataCluster();
            String concept = item.getConcept();
            String[] ids = new String[] { item.getIds() };
            String outputErrorMessage = com.amalto.core.util.Util.beforeDeleting(dataClusterPK, concept, ids);

            String message = null;
            String errorCode = null;
            if (outputErrorMessage != null) {
                Document doc = XmlUtil.parse(outputErrorMessage);
                // TODO what if multiple error nodes ?
                String xpath = "/descendant::error"; //$NON-NLS-1$
                Node errorNode = doc.selectSingleNode(xpath);
                if (errorNode instanceof Element) {
                    Element errorElement = (Element) errorNode;
                    errorCode = errorElement.attributeValue("code"); //$NON-NLS-1$
                    message = errorElement.getText();
                }
            }

            if (outputErrorMessage == null || "0".equals(errorCode)) { //$NON-NLS-1$               
                WSItemPK wsItem = CommonUtil.getPort().deleteItem(
                        new WSDeleteItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids)));
                if (wsItem != null)
                    pushUpdateReport(ids, concept, "PHYSICAL_DELETE"); //$NON-NLS-1$ 
                // deleted from the list.
                else
                    message = "ERROR - Unable to delete item"; //$NON-NLS-1$ 

                if (message == null || message.length() == 0)
                    message = "The record was deleted successfully."; //$NON-NLS-1$                
            } else {
                // Anything but 0 is unsuccessful
                if (message == null || message.length() == 0)
                    message = "An error might have occurred. The record was not deleted."; //$NON-NLS-1$
                message = "ERROR_3" + message; //$NON-NLS-1$
            }

            if (message.indexOf("ERROR") > -1)//$NON-NLS-1$ 
                return new ItemResult(ItemResult.FAILURE, message);
            else
                return new ItemResult(ItemResult.SUCCESS, message);

        } catch (Exception e) {
            return new ItemResult(ItemResult.FAILURE, "ERROR -" + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

    public List<ItemResult> deleteItemBeans(List<ItemBean> items) {
        List<ItemResult> itemResultes = new ArrayList<ItemResult>();
        for (ItemBean item : items) {
            ItemResult itemResult = deleteItemBean(item);
            itemResultes.add(itemResult);
        }
        return itemResultes;
    }

    public ItemResult logicalDeleteItem(ItemBean item, String path) {
        try {
            String dataClusterPK = getCurrentDataCluster();

            String xml = null;
            String concept = item.getConcept();
            String[] ids = new String[] { item.getIds() };
            WSItem item1 = CommonUtil.getPort().getItem(
                    new WSGetItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids)));
            xml = item1.getContent();

            WSDroppedItemPK wsItem = CommonUtil.getPort().dropItem(
                    new WSDropItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids), path));

            if (wsItem != null && xml != null)
                if ("/".equalsIgnoreCase(path)) { //$NON-NLS-1$
                    pushUpdateReport(ids, concept, "LOGIC_DELETE"); //$NON-NLS-1$
                }
                // TODO updatereport

                else
                    return new ItemResult(ItemResult.FAILURE, "ERROR - dropItem is NULL");//$NON-NLS-1$ 

            return new ItemResult(ItemResult.SUCCESS, "OK");//$NON-NLS-1$ 

        } catch (Exception e) {
            return new ItemResult(ItemResult.FAILURE, "ERROR -" + e.getLocalizedMessage());//$NON-NLS-1$ 
        }
    }

    private String pushUpdateReport(String[] ids, String concept, String operationType) throws Exception {
        if (LOG.isTraceEnabled())
            LOG.trace("pushUpdateReport() concept " + concept + " operation " + operationType);//$NON-NLS-1$ //$NON-NLS-2$ 

        // TODO check updatedPath
        HashMap<String, UpdateReportItem> updatedPath = null;
        if (!("PHYSICAL_DELETE".equals(operationType) || "LOGIC_DELETE".equals(operationType)) && updatedPath == null) { //$NON-NLS-1$ //$NON-NLS-2$
            return "ERROR_2";//$NON-NLS-1$ 
        }

        String xml2 = createUpdateReport(ids, concept, operationType, updatedPath);

        if (LOG.isDebugEnabled())
            LOG.debug("pushUpdateReport() " + xml2);//$NON-NLS-1$ 

        // TODO routeAfterSaving is true
        return persistentUpdateReport(xml2, false);
    }

    private String createUpdateReport(String[] ids, String concept, String operationType,
            HashMap<String, UpdateReportItem> updatedPath) throws Exception {

        String revisionId = null;
        String dataModelPK = getCurrentDataModel() == null ? "" : getCurrentDataModel();//$NON-NLS-1$ 
        String dataClusterPK = getCurrentDataCluster() == null ? "" : getCurrentDataCluster();//$NON-NLS-1$ 

        String username = com.amalto.webapp.core.util.Util.getLoginUserName();
        String universename = com.amalto.webapp.core.util.Util.getLoginUniverse();
        if (universename != null && universename.length() > 0)
            revisionId = com.amalto.webapp.core.util.Util.getRevisionIdFromUniverse(universename, concept);

        StringBuilder keyBuilder = new StringBuilder();
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                keyBuilder.append(ids[i]);
                if (i != ids.length - 1)
                    keyBuilder.append("."); //$NON-NLS-1$
            }
        }
        String key = keyBuilder.length() == 0 ? "null" : keyBuilder.toString(); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        // TODO what is StringEscapeUtils.escapeXml used for
        sb.append("<Update><UserName>").append(username).append("</UserName><Source>genericUI</Source><TimeInMillis>") //$NON-NLS-1$ //$NON-NLS-2$ 
                .append(System.currentTimeMillis()).append("</TimeInMillis><OperationType>") //$NON-NLS-1$
                .append(operationType).append("</OperationType><RevisionID>").append(revisionId) //$NON-NLS-1$
                .append("</RevisionID><DataCluster>").append(dataClusterPK).append("</DataCluster><DataModel>") //$NON-NLS-1$ //$NON-NLS-2$ 
                .append(dataModelPK).append("</DataModel><Concept>").append(concept) //$NON-NLS-1$
                .append("</Concept><Key>").append(key).append("</Key>"); //$NON-NLS-1$ //$NON-NLS-2$ 

        if ("UPDATE".equals(operationType)) { //$NON-NLS-1$
            Collection<UpdateReportItem> list = updatedPath.values();
            boolean isUpdate = false;
            for (Iterator<UpdateReportItem> iter = list.iterator(); iter.hasNext();) {
                UpdateReportItem item = iter.next();
                String oldValue = item.getOldValue() == null ? "" : item.getOldValue();//$NON-NLS-1$
                String newValue = item.getNewValue() == null ? "" : item.getNewValue();//$NON-NLS-1$
                if (newValue.equals(oldValue))
                    continue;
                sb.append("<Item>   <path>").append(item.getPath()).append("</path>   <oldValue>")//$NON-NLS-1$ //$NON-NLS-2$
                        .append(oldValue).append("</oldValue>   <newValue>")//$NON-NLS-1$
                        .append(newValue).append("</newValue></Item>");//$NON-NLS-1$
                isUpdate = true;
            }
            if (!isUpdate)
                return null;
        }
        sb.append("</Update>");//$NON-NLS-1$
        return sb.toString();
    }

    private static String persistentUpdateReport(String xml2, boolean routeAfterSaving) throws Exception {
        if (xml2 == null)
            return "OK";//$NON-NLS-1$

        WSItemPK itemPK = CommonUtil.getPort().putItem(
                new WSPutItem(new WSDataClusterPK("UpdateReport"), xml2, new WSDataModelPK("UpdateReport"), false)); //$NON-NLS-1$ //$NON-NLS-2$

        if (routeAfterSaving)
            CommonUtil.getPort().routeItemV2(new WSRouteItemV2(itemPK));

        return "OK";//$NON-NLS-1$ 
    }

    public ItemBasePageLoadResult<ItemBean> queryItemBeans(QueryModel config) {
        PagingLoadConfig pagingLoad = config.getPagingLoadConfig();
        String sortDir = null;
        if (SortDir.ASC.equals(pagingLoad.getSortDir())) {
            sortDir = ItemHelper.SEARCH_DIRECTION_ASC;
        }
        if (SortDir.DESC.equals(pagingLoad.getSortDir())) {
            sortDir = ItemHelper.SEARCH_DIRECTION_DESC;
        }
        Object[] result = getItemBeans(config.getDataClusterPK(), config.getView(), config.getModel(), config.getCriteria()
                .toString(), pagingLoad.getOffset(), pagingLoad.getLimit(), sortDir, pagingLoad.getSortField());
        List<ItemBean> itemBeans = (List<ItemBean>) result[0];
        int totalSize = (Integer) result[1];
        return new ItemBasePageLoadResult<ItemBean>(itemBeans, pagingLoad.getOffset(), totalSize);
    }

    public List<ItemBaseModel> getViewsList(String language) {
        try {
            Map<String, String> viewMap = null;

            String model = getCurrentDataModel();
            String[] businessConcept = CommonUtil.getPort().getBusinessConcepts(
                    new WSGetBusinessConcepts(new WSDataModelPK(model))).getStrings();
            ArrayList<String> bc = new ArrayList<String>();
            for (int i = 0; i < businessConcept.length; i++) {
                bc.add(businessConcept[i]);
            }
            WSViewPK[] wsViewsPK;
            wsViewsPK = CommonUtil.getPort().getViewPKs(new WSGetViewPKs(ViewHelper.DEFAULT_VIEW_PREFIX + ".*")).getWsViewPK();//$NON-NLS-1$ 

            // Filter view list according to current datamodel
            TreeMap<String, String> views = new TreeMap<String, String>();
            for (int i = 0; i < wsViewsPK.length; i++) {
                WSView wsview = CommonUtil.getPort().getView(new WSGetView(wsViewsPK[i]));// FIXME: Do we need get each
                // view entity here?
                String concept = ViewHelper.getConceptFromDefaultViewName(wsview.getName());
                if (bc.contains(concept)) {
                    String viewDesc = ViewHelper.getViewLabel(language, wsview);
                    views.put(wsview.getName(), viewDesc);
                }
            }
            viewMap = getMapSortedByValue(views);

            List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();
            for (String key : viewMap.keySet()) {
                ItemBaseModel bm = new ItemBaseModel();
                bm.set("name", viewMap.get(key));//$NON-NLS-1$ 
                bm.set("value", key);//$NON-NLS-1$ 
                list.add(bm);
            }
            return list;
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private static LinkedHashMap<String, String> getMapSortedByValue(Map<String, String> map) {
        TreeSet<Map.Entry> set = new TreeSet<Map.Entry>(new Comparator() {

            public int compare(Object obj, Object obj1) {
                return ((Comparable) ((Map.Entry) obj).getValue()).compareTo(((Map.Entry) obj1).getValue());
            }
        });
        set.addAll(map.entrySet());
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Iterator i = set.iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            sortedMap.put((String) entry.getKey(), (String) entry.getValue());
        }

        return sortedMap;
    }

    /*********************************************************************
     * Foreign key
     *********************************************************************/

    public ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model,
            String dataClusterPK, boolean ifFKFilter) {
        String xpathForeignKey = model.getForeignkey();
        // to verify
        String xpathInfoForeignKey = model.getForeignKeyInfo().toString().replaceAll("\\[", "").replaceAll("\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        // in search panel, the fkFilter is empty
        String fkFilter = ""; //$NON-NLS-1$
        if (ifFKFilter)
            fkFilter = model.getFkFilter();
        String value = ".*"; //$NON-NLS-1$        

        if (xpathForeignKey == null)
            return null;

        List<ForeignKeyBean> fkBeans = new ArrayList<ForeignKeyBean>();
        String[] results = null;
        String count = null;

        try {
            String initxpathForeignKey = ""; //$NON-NLS-1$
            initxpathForeignKey = com.amalto.webapp.core.util.Util.getForeignPathFromPath(xpathForeignKey);

            WSWhereCondition whereCondition = com.amalto.webapp.core.util.Util.getConditionFromPath(xpathForeignKey);
            WSWhereItem whereItem = null;
            if (whereCondition != null) {
                whereItem = new WSWhereItem(whereCondition, null, null);
            }

            // get FK filter
            WSWhereItem fkFilterWi = null;
            fkFilterWi = com.amalto.webapp.core.util.Util.getConditionFromFKFilter(fkFilter);
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
                    WSWhereItem wc = null;
                    String strConcept = conceptName + "/. CONTAINS "; //$NON-NLS-1$

                    // if (MDMConfiguration.getDBType().getName().equals(EDBType.QIZX.getName())) {
                    // strConcept = conceptName + "//* CONTAINS ";
                    // }
                    wc = com.amalto.webapp.core.util.Util.buildWhereItem(strConcept + value);
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
                        xPaths.add(xpathInfos[i].replaceFirst(conceptName, filteredConcept));
                    }
                // add the key paths last, since there may be multiple keys
                xPaths.add(filteredConcept + "/../../i"); //$NON-NLS-1$
                // order by
                String orderbyPath = null;
                if (!"".equals(xpathInfoForeignKey) && xpathInfoForeignKey != null) { //$NON-NLS-1$
                    orderbyPath = xpathInfos[0].replaceFirst(conceptName, filteredConcept);
                } else {

                }

                // Run the query
                if (!com.amalto.webapp.core.util.Util.isCustomFilter(fkFilter)) {

                    results = CommonUtil.getPort().xPathsSearch(
                            new WSXPathsSearch(new WSDataClusterPK(dataClusterPK), null, new WSStringArray(xPaths
                                    .toArray(new String[xPaths.size()])), whereItem, -1, config.getOffset(), config.getLimit(),
                                    orderbyPath, null)).getStrings();
                    count = CommonUtil.getPort().count(
                            new WSCount(new WSDataClusterPK(dataClusterPK), conceptName, whereItem, -1)).getValue();

                } else {

                    String injectedXpath = com.amalto.webapp.core.util.Util.getInjectedXpath(fkFilter);
                    results = CommonUtil.getPort().getItemsByCustomFKFilters(
                            new WSGetItemsByCustomFKFilters(new WSDataClusterPK(dataClusterPK), conceptName, new WSStringArray(
                                    xPaths.toArray(new String[xPaths.size()])), injectedXpath, config.getOffset(), config
                                    .getLimit(), orderbyPath, null)).getStrings();

                    count = CommonUtil.getPort().countItemsByCustomFKFilters(
                            new WSCountItemsByCustomFKFilters(new WSDataClusterPK(dataClusterPK), conceptName, injectedXpath))
                            .getValue();
                }
            }

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
                    bean.setId(id); //$NON-NLS-1$
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

    private void initFKBean(Element ele, ForeignKeyBean bean) {
        for (Object subEle : ele.elements()) {
            Element curEle = (Element) subEle;
            bean.set(curEle.getName(), curEle.getTextTrim());
            initFKBean(curEle, bean);
        }
    }

    /*********************************************************************
     * Bookmark management
     *********************************************************************/

    public boolean isExistCriteria(String dataObjectLabel, String id) {
        try {
            WSItemPK wsItemPK = new WSItemPK();
            wsItemPK.setConceptName("BrowseItem");//$NON-NLS-1$ 

            WSDataClusterPK wsDataClusterPK = new WSDataClusterPK();
            wsDataClusterPK.setPk(XSystemObjects.DC_SEARCHTEMPLATE.getName());
            wsItemPK.setWsDataClusterPK(wsDataClusterPK);

            String[] ids = new String[1];
            ids[0] = id;
            wsItemPK.setIds(ids);

            WSExistsItem wsExistsItem = new WSExistsItem(wsItemPK);
            WSBoolean wsBoolean = CommonUtil.getPort().existsItem(wsExistsItem);
            return wsBoolean.is_true();
        } catch (XtentisWebappException e) {
            // TODO Auto-generated catch block
            LOG.error(e.getMessage(), e);
            return false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public String saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString) {
        String returnString = "OK";//$NON-NLS-1$ 
        try {
            String owner = com.amalto.webapp.core.util.Util.getLoginUserName();
            SearchTemplate searchTemplate = new SearchTemplate();
            searchTemplate.setViewPK(viewPK);
            searchTemplate.setCriteriaName(templateName);
            searchTemplate.setShared(isShared);
            searchTemplate.setOwner(owner);
            searchTemplate.setCriteria(criteriaString);

            WSItemPK pk = CommonUtil.getPort().putItem(
                    new WSPutItem(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), searchTemplate
                            .marshal2String(), new WSDataModelPK(XSystemObjects.DM_SEARCHTEMPLATE.getName()), false));

            if (pk != null)
                returnString = "OK";//$NON-NLS-1$ 
            else
                returnString = null;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            returnString = e.getMessage();
        } finally {
            return returnString;
        }
    }

    public PagingLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, PagingLoadConfig load) {
        List<String> results = Arrays.asList(getSearchTemplateNames(view, isShared, load.getOffset(), load.getLimit()));
        List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();
        for (String result : results) {
            ItemBaseModel bm = new ItemBaseModel();
            bm.set("name", result);//$NON-NLS-1$ 
            bm.set("value", result);//$NON-NLS-1$ 
            list.add(bm);
        }
        int totalSize = Integer.parseInt(countSearchTemplate(view));
        return new BasePagingLoadResult<ItemBaseModel>(list, load.getOffset(), totalSize);
    }

    public List<ItemBaseModel> getUserCriterias(String view) {
        String[] results = getSearchTemplateNames(view, false, 0, 0);
        List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();

        for (String result : results) {
            ItemBaseModel bm = new ItemBaseModel();
            bm.set("name", result);//$NON-NLS-1$ 
            bm.set("value", result);//$NON-NLS-1$ 
            list.add(bm);
        }
        return list;
    }

    private String[] getSearchTemplateNames(String view, boolean isShared, int start, int limit) {
        try {
            int localStart = 0;
            int localLimit = 0;
            if (start == limit && limit == 0) {
                localStart = 0;
                localLimit = Integer.MAX_VALUE;
            } else {
                localStart = start;
                localLimit = limit;

            }
            WSWhereItem wi = new WSWhereItem();

            WSWhereCondition wc1 = new WSWhereCondition("BrowseItem/ViewPK", WSWhereOperator.EQUALS, view,//$NON-NLS-1$ 
                    WSStringPredicate.NONE, false);

            WSWhereCondition wc3 = new WSWhereCondition("BrowseItem/Owner", WSWhereOperator.EQUALS,//$NON-NLS-1$ 
                    RoleHelper.getCurrentUserName(), WSStringPredicate.OR, false);
            WSWhereCondition wc4;
            WSWhereOr or = new WSWhereOr();
            if (isShared) {
                wc4 = new WSWhereCondition("BrowseItem/Shared", WSWhereOperator.EQUALS, "true", WSStringPredicate.NONE, false);//$NON-NLS-1$ //$NON-NLS-2$ 

                or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null), new WSWhereItem(wc4, null, null) });
            } else {
                or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null) });
            }

            WSWhereAnd and = new WSWhereAnd(new WSWhereItem[] { new WSWhereItem(wc1, null, null),

            new WSWhereItem(null, null, or) });

            wi = new WSWhereItem(null, and, null);

            String[] results = CommonUtil.getPort().xPathsSearch(
                    new WSXPathsSearch(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), null,// pivot
                            new WSStringArray(new String[] { "BrowseItem/CriteriaName" }), wi, -1, localStart, localLimit, null, // order //$NON-NLS-1$ 
                            // by
                            null // direction
                    )).getStrings();

            for (int i = 0; i < results.length; i++) {
                results[i] = results[i].replaceAll("<CriteriaName>(.*)</CriteriaName>", "$1");//$NON-NLS-1$ //$NON-NLS-2$ 
            }
            return results;

        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private String countSearchTemplate(String view) {
        try {
            WSWhereItem wi = new WSWhereItem();

            // Configuration config = Configuration.getInstance();
            WSWhereCondition wc1 = new WSWhereCondition("BrowseItem/ViewPK", WSWhereOperator.EQUALS, view,//$NON-NLS-1$ 
                    WSStringPredicate.NONE, false);
            /*
             * WSWhereCondition wc2 = new WSWhereCondition( "hierarchical-report/data-model", WSWhereOperator.EQUALS,
             * config.getModel(), WSStringPredicate.NONE, false);
             */
            WSWhereCondition wc3 = new WSWhereCondition("BrowseItem/Owner", WSWhereOperator.EQUALS,//$NON-NLS-1$ 
                    com.amalto.webapp.core.util.Util.getLoginUserName(), WSStringPredicate.NONE, false);

            WSWhereOr or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null) });

            WSWhereAnd and = new WSWhereAnd(new WSWhereItem[] { new WSWhereItem(wc1, null, null),
            /* new WSWhereItem(wc2, null, null), */
            new WSWhereItem(null, null, or) });

            wi = new WSWhereItem(null, and, null);
            return CommonUtil.getPort().count(
                    new WSCount(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), "BrowseItem", wi, -1))//$NON-NLS-1$ 
                    .getValue();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return "0";//$NON-NLS-1$ 
        }
    }

    public String deleteSearchTemplate(String id) {
        try {
            String[] ids = { id };
            String concept = "BrowseItem";//$NON-NLS-1$ 
            String dataClusterPK = XSystemObjects.DC_SEARCHTEMPLATE.getName();
            if (ids != null) {
                WSItemPK wsItem = CommonUtil.getPort().deleteItem(
                        new WSDeleteItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids)));

                if (wsItem == null)
                    return "ERROR - deleteTemplate is NULL";//$NON-NLS-1$ 
                return "OK";//$NON-NLS-1$ 
            } else {
                return "OK";//$NON-NLS-1$ 
            }
        } catch (Exception e) {
            return "ERROR -" + e.getLocalizedMessage();//$NON-NLS-1$ 
        }
    }

    public String getCriteriaByBookmark(String bookmark) {
        try {
            String criteria = "";//$NON-NLS-1$ 
            String result = CommonUtil.getPort().getItem(
                    new WSGetItem(new WSItemPK(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), "BrowseItem",//$NON-NLS-1$ 
                            new String[] { bookmark }))).getContent().trim();
            if (result != null) {
                if (result.indexOf("<SearchCriteria>") != -1)//$NON-NLS-1$ 
                    criteria = result.substring(result.indexOf("<SearchCriteria>") + 16, result.indexOf("</SearchCriteria>"));//$NON-NLS-1$ //$NON-NLS-2$ 
            }
            return criteria;
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public AppHeader getAppHeader() throws Exception {

        AppHeader header = new AppHeader();
        header.setDatacluster(getCurrentDataCluster());
        header.setDatamodel(getCurrentDataModel());
        header.setStandAloneMode(ItemsBrowserConfiguration.isStandalone());
        header.setUsingDefaultForm(ItemsBrowserConfiguration.isUsingDefaultForm());
        return header;

    }

    /**
     * DOC HSHU Comment method "initMessage".
     * 
     * @throws IOException
     */
    public void initMessages(String language) throws IOException {
        MESSAGES = GWTI18N.create(ItemsbrowserMessages.class, language);

    }

    public ItemBean getItem(ItemBean itemBean, EntityModel entityModel) throws Exception {
        String dataCluster = getCurrentDataCluster();
        String dataModel = getCurrentDataModel();
        String concept = itemBean.getConcept();
        // get item
        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(dataCluster);
        String[] ids = itemBean.getIds() == null ? null : itemBean.getIds().split("\\.");//$NON-NLS-1$ 
        WSItem wsItem = CommonUtil.getPort().getItem(new WSGetItem(new WSItemPK(wsDataClusterPK, itemBean.getConcept(), ids)));
        itemBean.setItemXml(wsItem.getContent());
        // parse schema
        DataModelHelper.parseSchema(dataModel, concept, entityModel, RoleHelper.getUserRoles());
        // dynamic Assemble
        dynamicAssemble(itemBean, entityModel);

        return itemBean;
    }
}

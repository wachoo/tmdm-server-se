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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
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
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.SearchTemplate;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.ViewHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.DateUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.amalto.webapp.core.bean.UpdateReportItem;
import com.amalto.webapp.core.util.Messages;
import com.amalto.webapp.core.util.MessagesFactory;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSDropItem;
import com.amalto.webapp.util.webservices.WSDroppedItemPK;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetItem;
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

    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.itemsbrowser2.server.messages", ItemsServiceImpl.class.getClassLoader()); //$NON-NLS-1$


    private Object[] getItemBeans(String dataClusterPK, ViewBean viewBean, EntityModel entityModel, String criteria, int skip, int max) {

        String sortDir = null;
        String sortCol = null;

        int totalSize = 0;

        List<ItemBean> itemBeans = new ArrayList<ItemBean>();
        String concept = ViewHelper.getConceptFromDefaultViewName(viewBean.getViewPK());
        try {
            
            WSWhereItem wi = com.amalto.webapp.core.util.Util.buildWhereItems(criteria);

            String[] results = CommonUtil
                    .getPort()
                    .viewSearch(
                            new WSViewSearch(new WSDataClusterPK(dataClusterPK), new WSViewPK(viewBean.getViewPK()), wi, -1, skip, max,
                                    sortCol, sortDir)).getStrings();

            // TODO change ids to array?
            String ids = null;
            for (int i = 0; i < results.length; i++) {

                if (i == 0) {
                    totalSize = Integer.parseInt(com.amalto.webapp.core.util.Util.parse(results[i]).getDocumentElement()
                            .getTextContent());
                    continue;
                }

                // aiming modify when there is null value in fields, the viewable fields sequence is the same as the
                // childlist of result
                if (!results[i].startsWith("<result>")) {
                    results[i] = "<result>" + results[i] + "</result>";
                }

                Document doc = XmlUtil.parseText(results[i]);
                for (String key : entityModel.getKeys()) {
                    ids = XmlUtil.queryNode(doc, key.replaceAll(concept, "result")).getText();
                }

                ItemBean itemBean = new ItemBean(concept, ids, results[i]);
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
                    List nodes = XmlUtil.getValuesFromXPath(docXml, path.substring(path.indexOf('/') + 1));
                    if(nodes.size()>0) {
                        Node value = (Node) nodes.get(0);
                        if (typeModel.getTypeName().equals(DataTypeConstants.DATE)) {
                            Date date = DateUtil.convertStringToDate("yyyy-MM-dd",value.getText());
                            itemBean.set(path, date);
                        } else if (typeModel.isMultiple()){
                            List<Serializable> list = new ArrayList<Serializable>();
                            for (Object node : nodes){
                                list.add(((Node)node).getText());
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
            
            //get WSView
            WSView wsView = CommonUtil.getPort().getView(new WSGetView(new WSViewPK(viewPk)));
            
            //bind entity model
            String model = getCurrentDataModel();
            String concept = ViewHelper.getConceptFromDefaultViewName(viewPk);
            EntityModel entityModel = new EntityModel();
            DataModelHelper.parseSchema(model, concept, entityModel);
            vb.setBindingEntityModel(entityModel);
            
            //viewables
            String[] viewables = null;
            viewables = ViewHelper.getViewables(wsView);
            //FIXME remove viewableXpath
            if (viewables != null) {
                for (String viewable : viewables) {
                    vb.addViewableXpath(viewable);
                }
            }
            vb.setViewables(viewables);
            
            //searchables
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
            boolean ifNew = item.getIds().equals("") ? true : false;
            String operationType;
            if (ifNew)
                operationType = "CREATE"; //$NON-NLS-1$
            else
                operationType = "UPDATE"; //$NON-NLS-1$ 

            boolean isUpdateThisItem = true;
            if (ifNew)
                isUpdateThisItem = false;
            // if update, check the item is modified by others?
            WSItemPK wsi = null;
            WSPutItemWithReport wsPutItemWithReport = new WSPutItemWithReport(new WSPutItem(new WSDataClusterPK(
                    getCurrentDataCluster()), item.getItemXml(), new WSDataModelPK(getCurrentDataModel()), isUpdateThisItem),
                    "genericUI", true); //$NON-NLS-1$
            wsi = CommonUtil.getPort().putItemWithReport(wsPutItemWithReport);

            if (com.amalto.webapp.core.util.Util.isTransformerExist("beforeSaving_" + item.getConcept())) { //$NON-NLS-1$
                // TODO
            } else {
                message = "The record was saved successfully."; //$NON-NLS-1$
                status = ItemResult.SUCCESS;
            }
            return new ItemResult(status, message);
        } catch (Exception e) {
            ItemResult result;
            // TODO
            if (e.getLocalizedMessage().indexOf("routing failed:") == 0) {
                String saveSUCCE = "Save item '" + item.getConcept() + "."
                        + com.amalto.webapp.core.util.Util.joinStrings(new String[] { item.getIds() }, ".")
                        + "' successfully, But " + e.getLocalizedMessage();
                result = new ItemResult(ItemResult.FAILURE, saveSUCCE);
            } else {
                String err = "Unable to save item '" + item.getConcept() + "."
                        + com.amalto.webapp.core.util.Util.joinStrings(new String[] { item.getIds() }, ".") + "'"
                        + e.getLocalizedMessage();
                if (e.getLocalizedMessage().indexOf("ERROR_3:") == 0) {
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
                    message = "ERROR - Unable to delete item";

                if (message == null || message.length() == 0)
                    message = "The record was deleted successfully."; //$NON-NLS-1$                
            } else {
                // Anything but 0 is unsuccessful
                if (message == null || message.length() == 0)
                    message = "An error might have occurred. The record was not deleted."; //$NON-NLS-1$
                message = "ERROR_3" + message; //$NON-NLS-1$
            }

            if (message.indexOf("ERROR") > -1)
                return new ItemResult(ItemResult.FAILURE, message);
            else
                return new ItemResult(ItemResult.SUCCESS, message);

        } catch (Exception e) {
            return new ItemResult(ItemResult.FAILURE, "ERROR -" + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }
    
    public List<ItemResult> deleteItemBeans(List<ItemBean> items) {
        List<ItemResult> itemResultes = new ArrayList<ItemResult>();
        for (ItemBean item: items){
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
                    return new ItemResult(ItemResult.FAILURE, "ERROR - dropItem is NULL");

            return new ItemResult(ItemResult.SUCCESS, "OK");

        } catch (Exception e) {
            return new ItemResult(ItemResult.FAILURE, "ERROR -" + e.getLocalizedMessage());
        }
    }

    private String pushUpdateReport(String[] ids, String concept, String operationType) throws Exception {
        if (LOG.isTraceEnabled())
            LOG.trace("pushUpdateReport() concept " + concept + " operation " + operationType);

        // TODO check updatedPath
        HashMap<String, UpdateReportItem> updatedPath = null;
        if (!("PHYSICAL_DELETE".equals(operationType) || "LOGIC_DELETE".equals(operationType)) && updatedPath == null) { //$NON-NLS-1$
            return "ERROR_2";
        }

        String xml2 = createUpdateReport(ids, concept, operationType, updatedPath);

        if (LOG.isDebugEnabled())
            LOG.debug("pushUpdateReport() " + xml2);

        // TODO routeAfterSaving is true
        return persistentUpdateReport(xml2, false);
    }

    private String createUpdateReport(String[] ids, String concept, String operationType,
            HashMap<String, UpdateReportItem> updatedPath) throws Exception {

        String revisionId = null;
        String dataModelPK = getCurrentDataModel() == null ? "" : getCurrentDataModel();
        String dataClusterPK = getCurrentDataCluster() == null ? "" : getCurrentDataCluster();

        String username = com.amalto.webapp.core.util.Util.getLoginUserName();
        String universename = com.amalto.webapp.core.util.Util.getLoginUniverse();
        if (universename != null && universename.length() > 0)
            revisionId = com.amalto.webapp.core.util.Util.getRevisionIdFromUniverse(universename, concept);

        StringBuilder keyBuilder = new StringBuilder();
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                keyBuilder.append(ids[i]);
                if (i != ids.length - 1)
                    keyBuilder.append(".");
            }
        }
        String key = keyBuilder.length() == 0 ? "null" : keyBuilder.toString(); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        // TODO what is StringEscapeUtils.escapeXml used for
        sb.append("<Update><UserName>").append(username).append("</UserName><Source>genericUI</Source><TimeInMillis>") //$NON-NLS-1$
                .append(System.currentTimeMillis()).append("</TimeInMillis><OperationType>") //$NON-NLS-1$
                .append(operationType).append("</OperationType><RevisionID>").append(revisionId) //$NON-NLS-1$
                .append("</RevisionID><DataCluster>").append(dataClusterPK).append("</DataCluster><DataModel>") //$NON-NLS-1$
                .append(dataModelPK).append("</DataModel><Concept>").append(concept) //$NON-NLS-1$
                .append("</Concept><Key>").append(key).append("</Key>"); //$NON-NLS-1$

        if ("UPDATE".equals(operationType)) { //$NON-NLS-1$
            Collection<UpdateReportItem> list = updatedPath.values();
            boolean isUpdate = false;
            for (Iterator<UpdateReportItem> iter = list.iterator(); iter.hasNext();) {
                UpdateReportItem item = iter.next();
                String oldValue = item.getOldValue() == null ? "" : item.getOldValue();
                String newValue = item.getNewValue() == null ? "" : item.getNewValue();
                if (newValue.equals(oldValue))
                    continue;
                sb.append("<Item>   <path>").append(item.getPath()).append("</path>   <oldValue>")//$NON-NLS-1$
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
            return "OK";

        WSItemPK itemPK = CommonUtil.getPort().putItem(
                new WSPutItem(new WSDataClusterPK("UpdateReport"), xml2, new WSDataModelPK("UpdateReport"), false)); //$NON-NLS-1$

        if (routeAfterSaving)
            CommonUtil.getPort().routeItemV2(new WSRouteItemV2(itemPK));

        return "OK";
    }

    public ItemBasePageLoadResult<ItemBean> queryItemBeans(QueryModel config) {
        PagingLoadConfig pagingLoad = config.getPagingLoadConfig();
        Object[] result = getItemBeans(config.getDataClusterPK(), config.getView(), config.getModel(), config.getCriteria().toString(), pagingLoad.getOffset(), pagingLoad.getLimit());
        List<ItemBean> itemBeans = (List<ItemBean>) result[0];
        int totalSize = (Integer) result[1];
        return new ItemBasePageLoadResult<ItemBean>(itemBeans, pagingLoad.getOffset(), totalSize);
    }

    public List<ItemBaseModel> getViewsList(String language) {
        try {
            Map<String, String> viewMap = null;

            String model = getCurrentDataModel();
            String[] businessConcept = CommonUtil.getPort()
                    .getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(model))).getStrings();
            ArrayList<String> bc = new ArrayList<String>();
            for (int i = 0; i < businessConcept.length; i++) {
                bc.add(businessConcept[i]);
            }
            WSViewPK[] wsViewsPK;
            wsViewsPK = CommonUtil.getPort().getViewPKs(new WSGetViewPKs(ViewHelper.DEFAULT_VIEW_PREFIX + ".*")).getWsViewPK();

            //Filter view list according to current datamodel
            TreeMap<String, String> views = new TreeMap<String, String>();
            for (int i = 0; i < wsViewsPK.length; i++) {
                WSView wsview = CommonUtil.getPort().getView(new WSGetView(wsViewsPK[i]));//FIXME: Do we need get each view entity here? 
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
                bm.set("name", viewMap.get(key));
                bm.set("value", key);
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
     * Bookmark management
     *********************************************************************/

    public boolean isExistCriteria(String dataObjectLabel, String id) {
        try {
            WSItemPK wsItemPK = new WSItemPK();
            wsItemPK.setConceptName("BrowseItem");

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
        String returnString = "OK";
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
                returnString = "OK";
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
            bm.set("name", result);
            bm.set("value", result);
            list.add(bm);
        }
        int totalSize = Integer.parseInt(countSearchTemplate(view));
        return new BasePagingLoadResult<ItemBaseModel>(list, load.getOffset(), totalSize);
    }

    public List<ItemBaseModel> getviewItemsCriterias(String view) {
        String[] results = getSearchTemplateNames(view, true, 0, 0);
        List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();

        for (String result : results) {
            ItemBaseModel bm = new ItemBaseModel();
            bm.set("name", result);
            bm.set("value", result);
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

            WSWhereCondition wc1 = new WSWhereCondition("BrowseItem/ViewPK", WSWhereOperator.EQUALS, view,
                    WSStringPredicate.NONE, false);

            WSWhereCondition wc3 = new WSWhereCondition("BrowseItem/Owner", WSWhereOperator.EQUALS,
                    com.amalto.webapp.core.util.Util.getLoginUserName(), WSStringPredicate.NONE, false);
            WSWhereCondition wc4;
            WSWhereOr or = new WSWhereOr();
            if (isShared) {
                wc4 = new WSWhereCondition("BrowseItem/Shared", WSWhereOperator.EQUALS, "true", WSStringPredicate.OR, false);

                or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null), new WSWhereItem(wc4, null, null) });
            } else {
                or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null) });
            }

            WSWhereAnd and = new WSWhereAnd(new WSWhereItem[] { new WSWhereItem(wc1, null, null),

            new WSWhereItem(null, null, or) });

            wi = new WSWhereItem(null, and, null);

            String[] results = CommonUtil.getPort()
                    .xPathsSearch(new WSXPathsSearch(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), null,// pivot
                            new WSStringArray(new String[] { "BrowseItem/CriteriaName" }), wi, -1, localStart, localLimit, null, // order
                            // by
                            null // direction
                            )).getStrings();

            for (int i = 0; i < results.length; i++) {
                results[i] = results[i].replaceAll("<CriteriaName>(.*)</CriteriaName>", "$1");
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
            WSWhereCondition wc1 = new WSWhereCondition("BrowseItem/ViewPK", WSWhereOperator.EQUALS, view,
                    WSStringPredicate.NONE, false);
            /*
             * WSWhereCondition wc2 = new WSWhereCondition( "hierarchical-report/data-model", WSWhereOperator.EQUALS,
             * config.getModel(), WSStringPredicate.NONE, false);
             */
            WSWhereCondition wc3 = new WSWhereCondition("BrowseItem/Owner", WSWhereOperator.EQUALS,
                    com.amalto.webapp.core.util.Util.getLoginUserName(), WSStringPredicate.NONE, false);

            WSWhereOr or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null) });

            WSWhereAnd and = new WSWhereAnd(new WSWhereItem[] { new WSWhereItem(wc1, null, null),
            /* new WSWhereItem(wc2, null, null), */
            new WSWhereItem(null, null, or) });

            wi = new WSWhereItem(null, and, null);
            return CommonUtil.getPort()
                    .count(new WSCount(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), "BrowseItem", wi, -1))
                    .getValue();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return "0";
        }
    }

    public String deleteSearchTemplate(String id) {
        try {
            String[] ids = { id };
            String concept = "BrowseItem";
            String dataClusterPK = XSystemObjects.DC_SEARCHTEMPLATE.getName();
            if (ids != null) {
                WSItemPK wsItem = CommonUtil.getPort().deleteItem(
                        new WSDeleteItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids)));

                if (wsItem == null)
                    return "ERROR - deleteTemplate is NULL";
                return "OK";
            } else {
                return "OK";
            }
        } catch (Exception e) {
            return "ERROR -" + e.getLocalizedMessage();
        }
    }

    public String getCriteriaByBookmark(String bookmark) {
        try {
            String criteria = "";
            String result = CommonUtil
                    .getPort()
                    .getItem(
                            new WSGetItem(new WSItemPK(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()),
                                    "BrowseItem", new String[] { bookmark }))).getContent().trim();
            if (result != null) {
                if (result.indexOf("<SearchCriteria>") != -1)
                    criteria = result.substring(result.indexOf("<SearchCriteria>") + 16, result.indexOf("</SearchCriteria>"));
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
        
        AppHeader header=new AppHeader();
        header.setDatacluster(getCurrentDataCluster());
        header.setDatamodel(getCurrentDataModel());
        header.setStandAloneMode(ItemsBrowserConfiguration.isStandalone());
        header.setUsingDefaultForm(ItemsBrowserConfiguration.isUsingDefaultForm());
        return header;
        
    }
    
    public ItemBean getItem(ItemBean itemBean, EntityModel entityModel) throws Exception{
        String dataCluster=getCurrentDataCluster();
        String dataModel=getCurrentDataModel();
        String concept=itemBean.getConcept();
        //get item
        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(dataCluster);
        String[] ids = itemBean.getIds() == null ? null : itemBean.getIds().split("\\.");
        WSItem wsItem = CommonUtil.getPort().getItem(
                new WSGetItem(new WSItemPK(wsDataClusterPK, itemBean.getConcept(), ids)));
        itemBean.setItemXml(wsItem.getContent());
        //parse schema
        DataModelHelper.parseSchema(dataModel, concept, entityModel);
        //dynamic Assemble
        dynamicAssemble(itemBean, entityModel);
        
        return itemBean;
    }
}

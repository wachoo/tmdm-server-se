package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ItemsServiceAsync {

    void queryItemBeans(QueryModel config, AsyncCallback<ItemBasePageLoadResult<ItemBean>> callback);

    void getViewsList(String language, AsyncCallback<List<ItemBaseModel>> callback);

    void getView(String viewPk, String language, AsyncCallback<ViewBean> callback);

    void isExistCriteria(String dataObjectLabel, String id, AsyncCallback<Boolean> callback);

    void saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString, AsyncCallback<String> callback);

    void querySearchTemplates(String view, boolean isShared, PagingLoadConfig load,
            AsyncCallback<PagingLoadResult<ItemBaseModel>> callback);

    void getCriteriaByBookmark(String bookmark, AsyncCallback<String> callback);

    void getUserCriterias(String view, AsyncCallback<List<ItemBaseModel>> callback);

    void deleteSearchTemplate(String id, AsyncCallback<String> callback);

    void getCurrentDataModel(AsyncCallback<String> callback);

    void saveItemBean(ItemBean item, AsyncCallback<ItemResult> callback);

    void getCurrentDataCluster(AsyncCallback<String> callback);

    void logicalDeleteItem(ItemBean item, String path, AsyncCallback<ItemResult> callback);

    void deleteItemBean(ItemBean item, AsyncCallback<ItemResult> callback);

    void deleteItemBeans(List<ItemBean> items, AsyncCallback<List<ItemResult>> callback);

    void getAppHeader(AsyncCallback<AppHeader> callback);

    void getItem(ItemBean itemBean, EntityModel entityModel, AsyncCallback<ItemBean> callback);

    void getForeignKeyList(PagingLoadConfig config, TypeModel model, String dataClusterPK, boolean ifFKFilter, String value,
            AsyncCallback<ItemBasePageLoadResult<ForeignKeyBean>> callback);

    void logicalDeleteItems(List<ItemBean> items, String path, AsyncCallback<List<ItemResult>> callback);

    void getUploadTableNames(String datacluster, String value, AsyncCallback<List<ItemBaseModel>> callback);

    void getUploadTableDescription(String datacluster, String tableName, AsyncCallback<Map<String, List<String>>> callback);

    void deleteItemsBrowserTable(String datacluster, String tableName, AsyncCallback<List<ItemBaseModel>> callback);
}

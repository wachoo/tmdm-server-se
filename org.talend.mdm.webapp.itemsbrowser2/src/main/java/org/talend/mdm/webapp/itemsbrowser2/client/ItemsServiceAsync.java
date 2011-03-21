package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;

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

    void initMessages(String language, AsyncCallback<Void> callback);

    void getItem(ItemBean itemBean, EntityModel entityModel, AsyncCallback<ItemBean> callback);

    void getForeignKeyList(PagingLoadConfig config, TypeModel model, String dataClusterPK, boolean ifFKFilter,
            AsyncCallback<ItemBasePageLoadResult<ForeignKeyBean>> callback);

}

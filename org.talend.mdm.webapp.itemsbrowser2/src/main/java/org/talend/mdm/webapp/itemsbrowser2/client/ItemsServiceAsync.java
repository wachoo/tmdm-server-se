package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;

import org.talend.mdm.webapp.base.client.model.BasePagingLoadConfigImpl;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.client.model.ItemBean;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.Restriction;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

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

    void querySearchTemplates(String view, boolean isShared, BasePagingLoadConfigImpl load,
            AsyncCallback<ItemBasePageLoadResult<ItemBaseModel>> callback);

    void getCriteriaByBookmark(String bookmark, AsyncCallback<String> callback);

    void getUserCriterias(String view, AsyncCallback<List<ItemBaseModel>> callback);

    void deleteSearchTemplate(String id, AsyncCallback<String> callback);

    void getCurrentDataModel(AsyncCallback<String> callback);

    void saveItemBean(ItemBean item, String language, AsyncCallback<ItemResult> callback);

    void getCurrentDataCluster(AsyncCallback<String> callback);

    void logicalDeleteItem(ItemBean item, String path, AsyncCallback<ItemResult> callback);

    void deleteItemBean(ItemBean item, String language, AsyncCallback<ItemResult> callback);

    void deleteItemBeans(List<ItemBean> items, String language, AsyncCallback<List<ItemResult>> callback);

    void getAppHeader(AsyncCallback<AppHeader> callback);

    void getItem(ItemBean itemBean, EntityModel entityModel, AsyncCallback<ItemBean> callback);

    void getForeignKeyList(BasePagingLoadConfigImpl config, TypeModel model, String dataClusterPK, boolean ifFKFilter,
            String value, AsyncCallback<ItemBasePageLoadResult<ForeignKeyBean>> callback);

    void logicalDeleteItems(List<ItemBean> items, String path, AsyncCallback<List<ItemResult>> callback);

    void getForeignKeyPolymTypeList(String xpathForeignKey, String language, AsyncCallback<List<Restriction>> callback);

    void switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey, String fkFilter,
            AsyncCallback<ForeignKeyDrawer> callback);

    void getMandatoryFieldList(String tableName, AsyncCallback<List<String>> callback);
}

package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ItemsServiceAsync {

    void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

    void getEntityItems(String entityName, AsyncCallback<List<ItemBean>> callback);

    void setForm(ItemBean item, ViewBean view, AsyncCallback<ItemFormBean> callback);

    void queryItemBean(QueryModel config, AsyncCallback<PagingLoadResult<ItemBean>> callback);

    void getViewsList(String language, AsyncCallback<List<ItemBaseModel>> callback);

    void getView(String viewPk, AsyncCallback<ViewBean> callback);

    void isExistCriteria(String dataObjectLabel, String id, AsyncCallback<Boolean> callback);

    void saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString, AsyncCallback<String> callback);

    void querySearchTemplates(String view, boolean isShared, PagingLoadConfig load,
            AsyncCallback<PagingLoadResult<ItemBaseModel>> callback);

    void getCriteriaByBookmark(String bookmark, AsyncCallback<String> callback);

    void getviewItemsCriterias(String view, AsyncCallback<List<ItemBaseModel>> callback);

    void deleteSearchTemplate(String id, AsyncCallback<String> callback);

    void getCurrentDataModel(AsyncCallback<String> callback);
}

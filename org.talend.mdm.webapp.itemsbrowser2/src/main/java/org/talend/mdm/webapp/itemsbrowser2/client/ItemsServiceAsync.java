package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ItemsServiceAsync {

    void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

    void getEntityItems(String entityName, AsyncCallback<List<ItemBean>> callback);

	void setForm(ItemBean item, ViewBean view, AsyncCallback<ItemFormBean> callback);
	void queryItemBean(QueryModel config,
			AsyncCallback<PagingLoadResult<ItemBean>> callback);

	
    void getViewsList(String language, AsyncCallback<Map<String, String>> callback);

    void getView(String viewPk, AsyncCallback<ViewBean> callback);




}

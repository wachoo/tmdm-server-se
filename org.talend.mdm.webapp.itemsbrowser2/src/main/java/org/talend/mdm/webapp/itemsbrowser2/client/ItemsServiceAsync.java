package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ItemsServiceAsync {

    void greetServer(String input, AsyncCallback<String> callback) throws IllegalArgumentException;

    void getEntityItems(String entityName, AsyncCallback<List<ItemBean>> callback);

    void setForm(ItemBean item, AsyncCallback<ItemFormBean> callback);

    void getViewsList(String language, AsyncCallback<Map<String, String>> callback);

    void getView(String viewPk, AsyncCallback<ViewBean> callback);
}

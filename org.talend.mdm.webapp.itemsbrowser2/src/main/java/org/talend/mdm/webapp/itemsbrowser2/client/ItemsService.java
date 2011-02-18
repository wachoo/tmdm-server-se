package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("ItemsService")
public interface ItemsService extends RemoteService {

    public static class Util {

        public static ItemsServiceAsync getInstance() {

            return GWT.create(ItemsService.class);
        }
    }

    String greetServer(String name) throws IllegalArgumentException;

    List<ItemBean> getEntityItems(String entityName);

    ViewBean getView(String viewPk);

    ItemFormBean setForm(ItemBean item);

    Map<String, String> getViewsList(String language);
}

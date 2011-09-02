package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsService;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

public class TestFK extends SimplePanel {

    public TestFK() {
        final ForeignKeyTreeDetail tree = new ForeignKeyTreeDetail();
        Registry.register(UserSession.CURRENT_VIEW, new UserSession());
        Registry.register(BrowseRecords.BROWSERECORDS_SERVICE, GWT.create(BrowseRecordsService.class));
        getItemService().getView("Browse_items_Product", "en", new AsyncCallback<ViewBean>() { //$NON-NLS-1$//$NON-NLS-2$

                    public void onSuccess(ViewBean viewBean) {
                        tree.setViewBean(viewBean);
                    }

                    public void onFailure(Throwable arg0) {
                        Window.alert(arg0.getMessage());
                    }
                });
        setSize("1000px", "800px"); //$NON-NLS-1$//$NON-NLS-2$
        setWidget(tree);
    }

    private static BrowseRecordsServiceAsync getItemService() {

        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        return service;

    }

    public static UserSession getSession() {

        return Registry.get(BrowseRecords.USER_SESSION);

    }
}

package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsService;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TestFK extends ContentPanel {

    final ForeignKeyTreeDetail tree = new ForeignKeyTreeDetail();
    public TestFK() {

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

        add(tree);
        this.setLayout(new FitLayout());
        this.setHeaderVisible(false);
        // this.setAutoHeight(true);
        this.setSize(Window.getClientWidth(), Window.getClientWidth());
        this.doLayout(true);

    }

    private static BrowseRecordsServiceAsync getItemService() {

        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        return service;

    }

    public static UserSession getSession() {

        return Registry.get(BrowseRecords.USER_SESSION);

    }

}

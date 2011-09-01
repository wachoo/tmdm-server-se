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
package org.talend.mdm.webapp.browserecords.client;

import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyTreeDetail;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BrowseRecords implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side BrowseRecords service.
     */
    public static final String BROWSERECORDS_SERVICE = "BrowseRecordsService"; //$NON-NLS-1$

    public static final String USER_SESSION = "UserSession"; //$NON-NLS-1$


    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // // log setting
        // Log.setUncaughtExceptionHandler();
        //
        // Registry.register(BROWSERECORDS_SERVICE, GWT.create(BrowseRecordsService.class));
        //
        // // register user session
        // Registry.register(USER_SESSION, new UserSession());
        //
        // // add controller to dispatcher
        // final Dispatcher dispatcher = Dispatcher.get();
        // dispatcher.addController(new BrowseRecordsController());
        //
        // // init app-header
        // getItemService().getAppHeader(new AsyncCallback<AppHeader>() {
        //
        // public void onFailure(Throwable caught) {
        // Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
        // }
        //
        // public void onSuccess(AppHeader header) {
        // if (header.getDatacluster() == null || header.getDatamodel() == null) {
        // Window.alert(MessagesFactory.getMessages().data_model_not_specified());
        // return;
        // }
        // getSession().put(UserSession.APP_HEADER, header);
        //
        // dispatcher.dispatch(BrowseRecordsEvents.InitFrame);
        // }
        //
        // });
        getItemService().getView("Product", "en", new AsyncCallback<ViewBean>() {
            
            public void onSuccess(ViewBean viewBean) {
                BrowseRecords.getSession().put(UserSession.CURRENT_VIEW, viewBean);
                List<ItemNodeModel> models = CommonUtil.getDefaultTreeModel(viewBean.getBindingEntityModel().getMetaDataTypes()
                        .get("Product"));

                TabPanel tp = new TabPanel();
                tp.setSize(Window.getClientWidth(), Window.getClientHeight());

                tp.add(new ForeignKeyTreeDetail(models.get(0)));
                RootPanel.get().add(tp);
            }
            
            public void onFailure(Throwable arg0) {
                // TODO Auto-generated method stub
                TabPanel tp = new TabPanel();
                // tp.setSize(Window.getClientWidth(), Window.getClientHeight());
                // tp.add(new ForeignKeyTreeDetail());
                RootPanel.get().add(tp);
            }
        });

    }

    private static BrowseRecordsServiceAsync getItemService() {

        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        return service;

    }

    public static UserSession getSession() {

        return Registry.get(BrowseRecords.USER_SESSION);

    }
}

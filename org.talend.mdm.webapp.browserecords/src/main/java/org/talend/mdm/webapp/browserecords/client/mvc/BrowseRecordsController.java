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
package org.talend.mdm.webapp.browserecords.client.mvc;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class BrowseRecordsController extends Controller {

    private BrowseRecordsView view;

    private BrowseRecordsServiceAsync service;

    public BrowseRecordsController() {
        registerEventTypes(BrowseRecordsEvents.Error);
        registerEventTypes(BrowseRecordsEvents.InitFrame);
        registerEventTypes(BrowseRecordsEvents.InitSearchContainer);
        registerEventTypes(BrowseRecordsEvents.SearchView);
        registerEventTypes(BrowseRecordsEvents.GetView);

        registerEventTypes(BrowseRecordsEvents.CreateForeignKeyView);
        registerEventTypes(BrowseRecordsEvents.SelectForeignKeyView);
    }

    public void initialize() {
        service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        view = new BrowseRecordsView(this);
    }

    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == BrowseRecordsEvents.Error) {
            onError(event);
        } else if (type == BrowseRecordsEvents.GetView) {
            onGetView(event);
        } else if (event.getType() == BrowseRecordsEvents.SearchView) {
            onSearchView(event);
        } else if (type == BrowseRecordsEvents.InitFrame) {
            forwardToView(view, event);
        } else if (type == BrowseRecordsEvents.InitSearchContainer) {
            forwardToView(view, event);
        } else if (type == BrowseRecordsEvents.CreateForeignKeyView) {
            onCreateForeignKeyView(event);
        } else if (type == BrowseRecordsEvents.SelectForeignKeyView) {
            onSelectForeignKeyView(event);
        } else if (type == BrowseRecordsEvents.ViewItem) {
            onViewItem(event);
        }

    }

    private void onSelectForeignKeyView(final AppEvent event) {
        String viewFkName = "Browse_items_" + event.getData().toString(); //$NON-NLS-1$
        service.getView(viewFkName, Locale.getLanguage(), new AsyncCallback<ViewBean>() {

            public void onSuccess(ViewBean viewBean) {
                // forward
                AppEvent ae = new AppEvent(event.getType(), viewBean);
                ae.setSource(event.getSource());
                forwardToView(view, ae);
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
            }
        });

    }

    private void onViewItem(final AppEvent event) {
        ItemBean item = (ItemBean) event.getData();
        if (item != null) {
            EntityModel entityModel = (EntityModel) BrowseRecords.getSession().get(UserSession.CURRENT_ENTITY_MODEL);
            service.getItem(item, entityModel, Locale.getLanguage(), new AsyncCallback<ItemBean>() {

                public void onFailure(Throwable caught) {
                    Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
                }

                public void onSuccess(ItemBean result) {
                    AppEvent ae = new AppEvent(event.getType(), result);
                    forwardToView(view, ae);
                }
            });
        }
    }

    private void onCreateForeignKeyView(final AppEvent event) {
        String viewFkName = "Browse_items_"+event.getData().toString(); //$NON-NLS-1$
        service.getView(viewFkName, Locale.getLanguage(), new AsyncCallback<ViewBean>() {

            public void onSuccess(ViewBean viewBean) {
                // forward
                AppEvent ae = new AppEvent(event.getType(), viewBean);
                forwardToView(view, ae);
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
            }
        });

    }

    protected void onGetView(final AppEvent event) {
        Log.info("Get view... ");//$NON-NLS-1$
        String viewName = event.getData();
        service.getView(viewName, Locale.getLanguage(), new AsyncCallback<ViewBean>() {

            public void onSuccess(ViewBean viewbean) {

                // Init CURRENT_VIEW
                BrowseRecords.getSession().put(UserSession.CURRENT_VIEW, viewbean);

                // Init CURRENT_ENTITY_MODEL
                BrowseRecords.getSession().put(UserSession.CURRENT_ENTITY_MODEL, viewbean.getBindingEntityModel());

                // forward
                AppEvent ae = new AppEvent(event.getType(), viewbean);
                forwardToView(view, ae);
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
            }
        });
    }

    protected void onSearchView(final AppEvent event) {
        Log.info("Do view-search... ");//$NON-NLS-1$
        ViewBean viewBean = (ViewBean) BrowseRecords.getSession().getCurrentView();
        AppEvent ae = new AppEvent(event.getType(), viewBean);
        forwardToView(view, ae);
    }

    protected void onError(AppEvent ae) {
        Log.error("error: " + ae.<Object> getData()); //$NON-NLS-1$
        // MessageBox.alert(MessagesFactory.getMessages().error_title(), ae.<Object> getData().toString(), null);
    }

}

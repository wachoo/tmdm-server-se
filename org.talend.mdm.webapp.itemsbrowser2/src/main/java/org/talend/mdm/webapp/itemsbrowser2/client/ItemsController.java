// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.util.UserSession;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ItemsController extends Controller {

    private ItemsView itemsView;

    private ItemsServiceAsync service;

    public ItemsController() {
        registerEventTypes(ItemsEvents.InitFrame);
        registerEventTypes(ItemsEvents.InitSearchContainer);
        registerEventTypes(ItemsEvents.GetView);
        registerEventTypes(ItemsEvents.ViewItems);
        registerEventTypes(ItemsEvents.ViewItemForm);
        registerEventTypes(ItemsEvents.Error);
    }

    @Override
    public void initialize() {
        service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);
        itemsView = new ItemsView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        EventType type = event.getType();
        if (type == ItemsEvents.InitFrame) {
            forwardToView(itemsView, event);
        } else if (type == ItemsEvents.InitSearchContainer) {
            forwardToView(itemsView, event);
        } else if (event.getType() == ItemsEvents.GetView) {
            onGetView(event);
        } else if (event.getType() == ItemsEvents.ViewItems) {
            onViewItems(event);
        } else if (event.getType() == ItemsEvents.ViewItemForm) {
            onViewItemForm(event);
        } else if (type == ItemsEvents.Error) {
            onError(event);
        }
    }

    protected void onViewItemForm(final AppEvent event) {

        ItemBean item = event.getData();
        final String itemsFormTarget = event.getData(ItemsView.ITEMS_FORM_TARGET);
        ViewBean viewBean = (ViewBean) Itemsbrowser2.getSession().get(UserSession.CURRENT_VIEW);
        // TODO get whole item & data model from backend and then gen ItemFormBean
        service.setForm(item, viewBean, new AsyncCallback<ItemFormBean>() {

            public void onSuccess(ItemFormBean result) {
                AppEvent ae = new AppEvent(event.getType(), result);
                ae.setData(ItemsView.ITEMS_FORM_TARGET, itemsFormTarget);
                forwardToView(itemsView, ae);
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
            }
        });

    }

    protected void onGetView(final AppEvent event) {
        Log.info("Get view... ");
        String viewName = event.getData();
        service.getView(viewName, new AsyncCallback<ViewBean>() {

            public void onSuccess(ViewBean result) {
                Itemsbrowser2.getSession().put(UserSession.CURRENT_VIEW, result);
                AppEvent ae = new AppEvent(event.getType(), result);
                forwardToView(itemsView, ae);
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
            }
        });
    }

    protected void onViewItems(final AppEvent event) {
        Log.info("Get items... ");
        ViewBean viewBean = (ViewBean) Itemsbrowser2.getSession().get(UserSession.CURRENT_VIEW);
        String entity = ViewBean.getEntityFromViewName(viewBean.getViewPK());
        service.getEntityItems(entity, new AsyncCallback<List<ItemBean>>() {

            public void onSuccess(List<ItemBean> result) {
                AppEvent ae = new AppEvent(event.getType(), result);
                forwardToView(itemsView, ae);
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
            }
        });
    }

    protected void onError(AppEvent ae) {
        Log.error("error: " + ae.<Object> getData()); //$NON-NLS-1$
        MessageBox.alert(MessagesFactory.getMessages().error_title(), ae.<Object> getData().toString(), null);
    }

}

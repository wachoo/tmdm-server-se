// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.util.Collection;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.ItemBean;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.util.Locale;
import org.talend.mdm.webapp.itemsbrowser2.client.util.UserSession;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class ItemsController extends Controller {

    private ItemsView itemsView;

    private ItemsServiceAsync service;

    public ItemsController() {
        registerEventTypes(ItemsEvents.InitFrame);
        registerEventTypes(ItemsEvents.InitSearchContainer);
        registerEventTypes(ItemsEvents.GetView);
        registerEventTypes(ItemsEvents.SearchView);
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
        } else if (type == ItemsEvents.GetView) {
            onGetView(event);
        } else if (event.getType() == ItemsEvents.SearchView) {
            onSearchView(event);
        } else if (event.getType() == ItemsEvents.ViewItemForm) {
            onViewItemForm(event);
        } else if (type == ItemsEvents.Error) {
            onError(event);
        }
    }

    protected void onViewItemForm(final AppEvent event) {
        // Log.info("View item's form... ");
        // in the controller of ViewItemForm event re-parse model, get-full item
        final ItemBean itemBean = event.getData();
        
        if(Itemsbrowser2.getSession().getAppHeader().isUsingDefaultForm()) {
            EntityModel entityModel = Itemsbrowser2.getSession().getCurrentEntityModel();
            service.getItem(itemBean, entityModel, new SessionAwareAsyncCallback<ItemBean>() {

                @Override
                protected void doOnFailure(Throwable caught) {
                    Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                }

                public void onSuccess(ItemBean _itemBean) {
                    itemBean.setConcept(itemBean.getConcept());
                    itemBean.setIds(itemBean.getIds());
                    itemBean.setItemXml(itemBean.getItemXml());
                    Collection<String> names = _itemBean.getPropertyNames();
                    for (String name : names) {
                        if (!itemBean.getPropertyNames().contains(name)){
                            itemBean.set(name, _itemBean.get(name));    
                        }
                    }
                    forwardToView(itemsView, event);
                }
            });
        }else {
            forwardToView(itemsView, event);
        }
    }

    protected void onGetView(final AppEvent event) {
        Log.info("Get view... ");//$NON-NLS-1$
        String viewName = event.getData();
        service.getView(viewName, Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader()), new SessionAwareAsyncCallback<ViewBean>() {

            public void onSuccess(ViewBean view) {

                // Init CURRENT_VIEW
                Itemsbrowser2.getSession().put(UserSession.CURRENT_VIEW, view);

                // Init CURRENT_ENTITY_MODEL
                Itemsbrowser2.getSession().put(UserSession.CURRENT_ENTITY_MODEL, view.getBindingEntityModel());

                // forward
                AppEvent ae = new AppEvent(event.getType(), view);
                forwardToView(itemsView, ae);
            }

            @Override
            protected void doOnFailure(Throwable caught) {
                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
            }
        });
    }

    protected void onSearchView(final AppEvent event) {
        Log.info("Do view-search... ");//$NON-NLS-1$
        ViewBean viewBean = Itemsbrowser2.getSession().getCurrentView();
        AppEvent ae = new AppEvent(event.getType(), viewBean);
        forwardToView(itemsView, ae);
    }

    protected void onError(AppEvent ae) {
        Log.error("error: " + ae.<Object> getData()); //$NON-NLS-1$
        MessageBox.alert(MessagesFactory.getMessages().error_title(), ae.<Object> getData().toString(), null);
    }

}

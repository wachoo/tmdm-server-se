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
package org.talend.mdm.webapp.general.client.mvc.controller;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.general.client.General;
import org.talend.mdm.webapp.general.client.GeneralServiceAsync;
import org.talend.mdm.webapp.general.client.i18n.MessageFactory;
import org.talend.mdm.webapp.general.client.layout.ActionsPanel;
import org.talend.mdm.webapp.general.client.layout.BrandingBar;
import org.talend.mdm.webapp.general.client.layout.WorkSpace;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.client.mvc.view.GeneralView;
import org.talend.mdm.webapp.general.client.util.UrlUtil;
import org.talend.mdm.webapp.general.model.ActionBean;
import org.talend.mdm.webapp.general.model.ItemBean;
import org.talend.mdm.webapp.general.model.MenuBean;
import org.talend.mdm.webapp.general.model.UserBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class GeneralController extends Controller {

    private GeneralServiceAsync service;

    private GeneralView view;

    public GeneralController() {
        registerEventTypes(GeneralEvent.LoadUser);
        registerEventTypes(GeneralEvent.InitFrame);
        registerEventTypes(GeneralEvent.Error);
        registerEventTypes(GeneralEvent.LoadMenus);
        registerEventTypes(GeneralEvent.LoadLanguages);
        registerEventTypes(GeneralEvent.LoadActions);
        registerEventTypes(GeneralEvent.LoadWelcome);
        registerEventTypes(GeneralEvent.SwitchClusterAndModel);
    }

    @Override
    public void initialize() {
        service = (GeneralServiceAsync) Registry.get(General.OVERALL_SERVICE);
        view = new GeneralView(this);
    }

    @Override
    public void handleEvent(final AppEvent event) {
        EventType type = event.getType();

        if (type == GeneralEvent.LoadUser) {
            loadUser(event);
        } else if (type == GeneralEvent.InitFrame) {
            forwardToView(view, event);
        } else if (type == GeneralEvent.Error) {
            this.forwardToView(view, event);
        } else if (type == GeneralEvent.LoadLanguages) {
            loadLanguages(event);
        } else if (type == GeneralEvent.LoadMenus) {
            loadMenu(event);
        } else if (type == GeneralEvent.LoadActions) {
            loadActions(event);
        } else if (type == GeneralEvent.SwitchClusterAndModel) {
            switchClusterAndModel(event);
        } else if (type == GeneralEvent.LoadWelcome) {
            forwardToView(view, event);
        }
    }

    private void loadLanguages(AppEvent event) {
        service.getLanguages(new SessionAwareAsyncCallback<List<ItemBean>>() {

            public void onSuccess(List<ItemBean> result) {
                BrandingBar.getInstance().buildLanguage(result);
                Dispatcher dispatcher = Dispatcher.get();
                dispatcher.dispatch(GeneralEvent.InitFrame);
            }
        });
    }

    private void loadMenu(final AppEvent event) {
        service.getMenus(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<List<MenuBean>>() {

            public void onSuccess(List<MenuBean> result) {
                event.setData(result);
                forwardToView(view, event);
            }
        });
    }

    private void loadUser(AppEvent event) {
        service.getUsernameAndUniverse(new SessionAwareAsyncCallback<UserBean>() {

            public void onSuccess(UserBean userBean) {
                Registry.register(General.USER_BEAN, userBean);
                Dispatcher dispatcher = Dispatcher.get();
                dispatcher.dispatch(GeneralEvent.LoadLanguages);
            }
        });
    }

    private void loadActions(AppEvent event) {
        service.getAction(new SessionAwareAsyncCallback<ActionBean>() {

            public void onSuccess(ActionBean result) {
                ActionsPanel.getInstance().loadAction(result);
            }
        });
    }

    private void switchClusterAndModel(AppEvent event) {
        String dataCluster = ActionsPanel.getInstance().getDataCluster();
        String dataModel = ActionsPanel.getInstance().getDataModel();
        service.setClusterAndModel(dataCluster, dataModel, new SessionAwareAsyncCallback<String>() {

            public void onSuccess(String result) {
                if ("DONE".equals(result)) { //$NON-NLS-1$
                    MessageBox.alert(MessageFactory.getMessages().status(), MessageFactory.getMessages().status_msg_success(),
                            null);
                } else {
                    MessageBox.alert(MessageFactory.getMessages().status(), MessageFactory.getMessages().status_msg_failure()
                            + " " + result, null); //$NON-NLS-1$
                }
                WorkSpace.getInstance().clearTabs();
                WorkSpace.getInstance().loadApp(GeneralView.WELCOMECONTEXT, GeneralView.WELCOMEAPP);
            }
        });
    }
}

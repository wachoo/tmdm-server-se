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

import org.talend.mdm.webapp.general.client.General;
import org.talend.mdm.webapp.general.client.GeneralServiceAsync;
import org.talend.mdm.webapp.general.client.MdmAsyncCallback;
import org.talend.mdm.webapp.general.client.i18n.MessageFactory;
import org.talend.mdm.webapp.general.client.layout.ActionsPanel;
import org.talend.mdm.webapp.general.client.layout.WorkSpace;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.client.mvc.view.GeneralView;
import org.talend.mdm.webapp.general.client.util.UrlUtil;
import org.talend.mdm.webapp.general.model.ComboBoxModel;
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
	
	public GeneralController(){
	    registerEventTypes(GeneralEvent.LoadUser);
		registerEventTypes(GeneralEvent.InitFrame);
		registerEventTypes(GeneralEvent.Error);
		registerEventTypes(GeneralEvent.LoadMenus);
		registerEventTypes(GeneralEvent.LoadActions);
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

		if (type == GeneralEvent.LoadUser){
		    loadUser(event);
		} else if (type == GeneralEvent.InitFrame){
		    forwardToView(view, event);
		} else if (type == GeneralEvent.Error){
		    this.forwardToView(view, event);
		} else if (type == GeneralEvent.LoadMenus){
		    loadMenu(event);
		} else if (type == GeneralEvent.LoadActions){
		    loadActions(event);
		} else if (type == GeneralEvent.SwitchClusterAndModel){
		    switchClusterAndModel(event);
		}
	}
	
	private void loadMenu(final AppEvent event){
	    service.getMenus(UrlUtil.getLanguage(), new MdmAsyncCallback<List<MenuBean>>() {
            @Override
            public void onSuccess(List<MenuBean> result) {
                event.setData(result);
                forwardToView(view, event);
            }
        });
	}
	
	private void loadUser(AppEvent event){
	    service.getUsernameAndUniverse(new MdmAsyncCallback<UserBean>() {
            public void onSuccess(UserBean userBean) {
                Registry.register(General.USER_BEAN, userBean);
                Dispatcher dispatcher = Dispatcher.get();
                dispatcher.dispatch(GeneralEvent.InitFrame);
            }
        });
	}
	
	private void loadActions(AppEvent event){
        service.getClusters(new MdmAsyncCallback<List<ComboBoxModel>>() {
            public void onSuccess(List<ComboBoxModel> containers) {
                ActionsPanel.getInstance().loadDataContainer(containers);
            }
        });
        service.getModels(new MdmAsyncCallback<List<ComboBoxModel>>() {
            public void onSuccess(List<ComboBoxModel> models) {
                ActionsPanel.getInstance().loadDataModel(models);
            }
        });
	}
	
	private void switchClusterAndModel(AppEvent event){
	    String dataCluster = ActionsPanel.getInstance().getDataCluster();
	    String dataModel = ActionsPanel.getInstance().getDataModel();
	    service.setClusterAndModel(dataCluster, dataModel, new MdmAsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if("DONE".equals(result)){ //$NON-NLS-1$
                    MessageBox.alert(MessageFactory.getMessages().status(), MessageFactory.getMessages().status_msg_success(), null);
                } else {
                    MessageBox.alert(MessageFactory.getMessages().status(), MessageFactory.getMessages().status_msg_failure() + " " + result, null); //$NON-NLS-1$
                }
                WorkSpace.getInstance().clearTabs();
            }
        });
	}
}

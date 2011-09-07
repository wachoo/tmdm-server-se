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
package org.talend.mdm.webapp.general.client;

import org.talend.mdm.webapp.general.client.boundary.PubService;
import org.talend.mdm.webapp.general.client.message.PublicMessageService;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.client.mvc.controller.GeneralController;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class General implements EntryPoint {

    public static final String OVERALL_SERVICE = "GeneralService"; //$NON-NLS-1$

	public static final String USER_BEAN = "UserBean"; //$NON-NLS-1$

	public void onModuleLoad() {
        Window.setTitle(Window.getTitle() + " general"); //$NON-NLS-1$
        XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$

        // standalone initialization Ext nvironment. solve JavaScript code conflict.
        // these code come from Gxt sdk
        GeneralEnvironment.loadAll();

        registerPubServices();

        registerPubService();

		Registry.register(OVERALL_SERVICE, GWT.create(GeneralService.class));
		PublicMessageService.registerMessageService();

		Dispatcher dispatcher = Dispatcher.get();
		dispatcher.addController(new GeneralController());
		dispatcher.dispatch(GeneralEvent.LoadUser);

	}

    private native void registerPubService()/*-{
        var workspace = @org.talend.mdm.webapp.general.client.layout.WorkSpace::getInstance()();
        //Create a fake TabPanel JavaScript Object, imitate original extjs code behaviour
        var tabPanel = {
        // imitate getItem method, return corresponding contentPanel by itemId
        getItem : function(itemId){
        return workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::getItem(Ljava/lang/String;)(itemId);
        },
        // imitate add method, add fake extjs panel to gxt's TabPanel
        add : function(item){
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::addWorkTab(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(item.getItemId(), item);
        },
        doLayout : function(){
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::doLayout();
        },
        //select tabItem by itemId
        setSelection : function(itemId){
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::setSelection(Ljava/lang/String;)(itemId);
        },

        remove: function(item){
        if(typeof item != "string"){
        item = item.getId();
        }
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::remove(Ljava/lang/String;)(item);
        },
        on: function(eventName, handler){
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::on(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(eventName, handler);
        },
        un: function(eventName, handler){
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::un(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(eventName, handler);
        }
        };
        // return global TabPanel
        $wnd.amalto.core.getTabPanel = function(){
        return tabPanel;
        };
        // imitate original extjs project doLayout method
        $wnd.amalto.core.doLayout = function(){
        tabPanel.doLayout();
        };
    }-*/;

    private void registerPubServices() {
        PubService.registerLanguageService();
    }
}

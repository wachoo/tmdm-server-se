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
        //创建一个模拟tabPanel的JavaScript对象，模拟extjs老代码的行为
        var tabPanel = {
        // 模拟getItem方法， 通过itemId返回对应的contentPanel
        getItem : function(itemId){
        return workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::getItem(Ljava/lang/String;)(itemId);
        },
        //模拟add方法，将item表示的panel对象添加到gxt的tabPanel上
        add : function(item){
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::addWorkTab(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(item.getItemId(), item);
        },
        doLayout : function(){
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::doLayout();
        },
        //激活itemId
        setSelection : function(itemId){
        workspace.@org.talend.mdm.webapp.general.client.layout.WorkSpace::setSelection(Ljava/lang/String;)(itemId);
        }
        };
        //返回全局的tabPanel
        $wnd.amalto.core.getTabPanel = function(){
        return tabPanel;
        };
        //模拟extjs老项目的doLayout方法
        $wnd.amalto.core.doLayout = function(){
        tabPanel.doLayout();
        };
    }-*/;

    private void registerPubServices() {
        PubService.registerLanguageService();
    }
}

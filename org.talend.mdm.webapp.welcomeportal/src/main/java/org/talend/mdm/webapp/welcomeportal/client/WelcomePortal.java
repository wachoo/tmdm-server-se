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
package org.talend.mdm.webapp.welcomeportal.client;

import org.talend.mdm.webapp.welcomeportal.client.mvc.WelcomePortalController;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WelcomePortal implements EntryPoint {


    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    public final static String WELCOMEPORTAL_SERVICE = "WelcomePortalService"; //$NON-NLS-1$

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        Window.setTitle(Window.getTitle() + " welcomeportal"); //$NON-NLS-1$
        XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$
        registerPubService();
        // log setting
        Log.setUncaughtExceptionHandler();

        Registry.register(WELCOMEPORTAL_SERVICE, GWT.create(WelcomePortalService.class));

        // add controller to dispatcher
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new WelcomePortalController());
        // GenerateContainer.generateContentPanel();
        // onModuleRender();
        // RootPanel.get().add(GenerateContainer.getContentPanel());
    }

    private native void registerPubService()/*-{
        var instance = this;
        $wnd.amalto.welcomeportal = {};
        $wnd.amalto.welcomeportal.WelcomePortal = function(){

        function initUI(){
        instance.@org.talend.mdm.webapp.welcomeportal.client.WelcomePortal::initUI()();
        }

        return {
        init : function(){initUI();}
        }
        }();
    }-*/;

    private native void _initUI()/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        var panel = tabPanel.getItem("welcomeportal"); 
        if (panel == undefined){
        @org.talend.mdm.webapp.welcomeportal.client.GenerateContainer::generateContentPanel()();
        panel = this.@org.talend.mdm.webapp.welcomeportal.client.WelcomePortal::createPanel()();
        tabPanel.add(panel);
        }
        tabPanel.setSelection(panel.getItemId());
    }-*/;

    native JavaScriptObject createPanel()/*-{
        var instance = this;
        var panel = {
        render : function(el){
        instance.@org.talend.mdm.webapp.welcomeportal.client.WelcomePortal::renderContent(Ljava/lang/String;)(el.id);
        },
        setSize : function(width, height){
        var cp = @org.talend.mdm.webapp.welcomeportal.client.GenerateContainer::getContentPanel()();
        cp.@com.extjs.gxt.ui.client.widget.ContentPanel::setSize(II)(width, height);
        },
        getItemId : function(){
        var cp = @org.talend.mdm.webapp.welcomeportal.client.GenerateContainer::getContentPanel()();
        return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getItemId()();
        },
        getEl : function(){
        var cp = @org.talend.mdm.webapp.welcomeportal.client.GenerateContainer::getContentPanel()();
        var el = cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getElement()();
        return {dom : el};
        },
        doLayout : function(){
        var cp = @org.talend.mdm.webapp.welcomeportal.client.GenerateContainer::getContentPanel()();
        return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::doLayout()();
        }
        };
        return panel;
    }-*/;

    public void renderContent(final String contentId) {
        onModuleRender();
        RootPanel panel = RootPanel.get(contentId);
        panel.add(GenerateContainer.getContentPanel());
    }

    public void initUI() {
        _initUI();
    }

    private void onModuleRender() {
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.dispatch(WelcomePortalEvents.InitFrame);
    }
}

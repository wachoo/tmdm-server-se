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

import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsController;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.GenerateContainer;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
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

    private RootPanel panel;

    private native void regItemDetails()/*-{
        $wnd.amalto.itemsbrowser.ItemsBrowser.editItemDetails = function(ids, entity, callback){
             var idstr;
             if(ids.length == 1){
                 idstr = ids[0];
             }      
             var tabPanel = $wnd.amalto.core.getTabPanel();  
             var panel = tabPanel.getItem("Browse Records");   
             tabPanel.setSelection(panel.getItemId());  
             @org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar::addTreeDetail(Ljava/lang/String;Ljava/lang/String;)(idstr, entity)
        };
    }-*/;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        if (GWT.isScript()) {
            registerPubService();
            regItemDetails();
            Log.setUncaughtExceptionHandler();
            Registry.register(BROWSERECORDS_SERVICE, GWT.create(BrowseRecordsService.class));

            // register user session
            Registry.register(USER_SESSION, new UserSession());

            // add controller to dispatcher
            final Dispatcher dispatcher = Dispatcher.get();
            dispatcher.addController(new BrowseRecordsController());
        } else {

            Log.setUncaughtExceptionHandler();
            // GenerateContainer.generateContentPanel();
            Registry.register(BROWSERECORDS_SERVICE, GWT.create(BrowseRecordsService.class));

            // register user session
            Registry.register(USER_SESSION, new UserSession());

            GenerateContainer.generateContentPanel();

            // add controller to dispatcher
            final Dispatcher dispatcher = Dispatcher.get();
            dispatcher.addController(new BrowseRecordsController());
            panel = RootPanel.get();
            GenerateContainer.getContentPanel().setSize(Window.getClientWidth(), Window.getClientHeight());
            onModuleRender();
        }

    }

    private static BrowseRecordsServiceAsync getItemService() {

        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        return service;

    }

    public void showTreeDetailPanel(final String concept, final String ids) {
        getItemService().getItemBeanById(concept, ids.split("\\."), Locale.getLanguage(), new AsyncCallback<ItemBean>() { //$NON-NLS-1$

            public void onSuccess(ItemBean item) {
                ItemPanel itemPanel = new ItemPanel(item, ItemDetailToolBar.VIEW_OPERATION);
                itemPanel.setItemId(concept + "_" + ids); //$NON-NLS-1$
                        renderPubTreeDetailPanel(itemPanel.getItemId(), itemPanel);
            }

            public void onFailure(Throwable arg0) {

            }
        });
    }
    
    public native void renderPubTreeDetailPanel(String itemId, ItemPanel itemPanel)/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        var panel = tabPanel.getItem(itemId); 
        if (panel == undefined){
        panel = this.@org.talend.mdm.webapp.browserecords.client.BrowseRecords::wrapTreeDetailPanel(Lorg/talend/mdm/webapp/browserecords/client/widget/ItemPanel;)(itemPanel);
        tabPanel.add(panel);
        }
        tabPanel.setSelection(panel.getItemId());
    }-*/;

    public native JavaScriptObject wrapTreeDetailPanel(ItemPanel itemPanel)/*-{
        var panel = {
        // imitate extjs's render method, really call gxt code.
        render : function(el){
        var rootPanel = @com.google.gwt.user.client.ui.RootPanel::get(Ljava/lang/String;)(el.id);
        rootPanel.@com.google.gwt.user.client.ui.RootPanel::add(Lcom/google/gwt/user/client/ui/Widget;)(itemPanel);
        },
        // imitate extjs's setSize method, really call gxt code.
        setSize : function(width, height){
        itemPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemPanel::setSize(II)(width, height);
        },
        // imitate extjs's getItemId, really return itemId of ContentPanel of GXT.
        getItemId : function(){
        return itemPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemPanel::getItemId()();
        },
        // imitate El object of extjs
        getEl : function(){
        var el = itemPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemPanel::getElement()();
        return {dom : el};
        },
        // imitate extjs's doLayout method, really call gxt code.
        doLayout : function(){
        return itemPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemPanel::doLayout()();
        }
        };
        return panel;
    }-*/;

    public static UserSession getSession() {

        return Registry.get(BrowseRecords.USER_SESSION);
    }

    private native void registerPubService()/*-{
        var instance = this;
        $wnd.amalto.browserecords = {};
        $wnd.amalto.browserecords.BrowseRecords = function(){

        function initUI(){
        instance.@org.talend.mdm.webapp.browserecords.client.BrowseRecords::initUI()();
        }

        function showTreeDetailPanel(concept, ids){
        instance.@org.talend.mdm.webapp.browserecords.client.BrowseRecords::showTreeDetailPanel(Ljava/lang/String;Ljava/lang/String;)(concept, ids);
        }

        return {
        init : function(){initUI();},
        showTreeDetailPanel : function(concept, ids){showTreeDetailPanel(concept, ids);}
        }
        }();
    }-*/;

    private native void _initUI()/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        var panel = tabPanel.getItem("Browse Records"); 
        if (panel == undefined){
        @org.talend.mdm.webapp.browserecords.client.widget.GenerateContainer::generateContentPanel()();
        panel = this.@org.talend.mdm.webapp.browserecords.client.BrowseRecords::createPanel()();
        tabPanel.add(panel);
        }
        tabPanel.setSelection(panel.getItemId());
    }-*/;

    native JavaScriptObject createPanel()/*-{
        var instance = this;
        // imitate extjs Panel
        var panel = {
        // imitate extjs's render method, really call gxt code.
        render : function(el){
        instance.@org.talend.mdm.webapp.browserecords.client.BrowseRecords::renderContent(Ljava/lang/String;)(el.id);
        },
        // imitate extjs's setSize method, really call gxt code.
        setSize : function(width, height){
        var cp = @org.talend.mdm.webapp.browserecords.client.widget.GenerateContainer::getContentPanel()();
        cp.@com.extjs.gxt.ui.client.widget.ContentPanel::setSize(II)(width, height);
        },
        // imitate extjs's getItemId, really return itemId of ContentPanel of GXT.
        getItemId : function(){
        var cp = @org.talend.mdm.webapp.browserecords.client.widget.GenerateContainer::getContentPanel()();
        return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getItemId()();
        },
        // imitate El object of extjs
        getEl : function(){
        var cp = @org.talend.mdm.webapp.browserecords.client.widget.GenerateContainer::getContentPanel()();
        var el = cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getElement()();
        return {dom : el};
        },
        // imitate extjs's doLayout method, really call gxt code.
        doLayout : function(){
        var cp = @org.talend.mdm.webapp.browserecords.client.widget.GenerateContainer::getContentPanel()();
        return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::doLayout()();
        }
        };
        return panel;
    }-*/;

    public void renderContent(final String contentId) {
        panel = RootPanel.get(contentId);
        onModuleRender();
    }

    public void initUI() {
        _initUI();
    }

    private void onModuleRender() {
        getItemService().getAppHeader(new AsyncCallback<AppHeader>() {

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
            }

            public void onSuccess(AppHeader header) {
                if (header.getDatacluster() == null || header.getDatamodel() == null) {
                    Window.alert(MessagesFactory.getMessages().data_model_not_specified());
                    return;
                }
                getSession().put(UserSession.APP_HEADER, header);
                Dispatcher dispatcher = Dispatcher.get();
                dispatcher.dispatch(BrowseRecordsEvents.InitFrame);
                panel.add(GenerateContainer.getContentPanel());
            }
        });
    }
}

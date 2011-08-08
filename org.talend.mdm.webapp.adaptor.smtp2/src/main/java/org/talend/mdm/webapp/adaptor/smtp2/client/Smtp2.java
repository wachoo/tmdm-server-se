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

package org.talend.mdm.webapp.adaptor.smtp2.client;

import org.talend.mdm.webapp.adaptor.smtp2.client.mvc.Smtp2Controller;
import org.talend.mdm.webapp.adaptor.smtp2.client.widget.GenerateContainer;

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
public class Smtp2 implements EntryPoint {

    /**
     * Create a remote service proxy to talk to the server-side Smtp2 service.
     */
    public static final String Smtp2_SERVICE = "Smtp2Service";
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        Window.setTitle(Window.getTitle() + " Smtp2"); //$NON-NLS-1$
        XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$
        registerPubService();
        // log setting
        Log.setUncaughtExceptionHandler();

        Registry.register(Smtp2_SERVICE, GWT.create(Smtp2Service.class));
        // add controller to dispatcher
        Dispatcher dispatcher = Dispatcher.get();
        dispatcher.addController(new Smtp2Controller());
        // dispatcher.dispatch(Smtp2Events.InitFrame);

    }

    private native void registerPubService()/*-{
		var instance = this;
		$wnd.amalto.Smtp2 = {};
		$wnd.amalto.Smtp2.Smtp2 = function() {

			function initUI() {
				instance.@org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2::initUI()();
			}

			return {
				init : function() {
					initUI();
				}
			}
		}();
    }-*/;

    private native void _initUI()/*-{

		var tabPanel = $wnd.amalto.core.getTabPanel();
		var panel = tabPanel.getItem("Smtp2");

		if (panel == undefined) {
			@org.talend.mdm.webapp.adaptor.smtp2.client.widget.GenerateContainer::generateContentPanel()();
			panel = this.@org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2::createPanel()();
			tabPanel.add(panel);
		}

		tabPanel.setSelection(panel.getItemId());
    }-*/;

    native JavaScriptObject createPanel()/*-{
		var instance = this;
		var panel = {
			render : function(el) {
				instance.@org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2::renderContent(Ljava/lang/String;)(el.id);
			},
			setSize : function(width, height) {
				var cp = @org.talend.mdm.webapp.adaptor.smtp2.client.widget.GenerateContainer::getContentPanel()();
				cp.@com.extjs.gxt.ui.client.widget.ContentPanel::setSize(II)(width, height);
			},
			getItemId : function() {
				var cp = @org.talend.mdm.webapp.adaptor.smtp2.client.widget.GenerateContainer::getContentPanel()();
				return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getItemId()();
			},
			getEl : function() {
				var cp = @org.talend.mdm.webapp.adaptor.smtp2.client.widget.GenerateContainer::getContentPanel()();
				var el = cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getElement()();
				return {
					dom : el
				};
			},
			doLayout : function() {
				var cp = @org.talend.mdm.webapp.adaptor.smtp2.client.widget.GenerateContainer::getContentPanel()();
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
        dispatcher.dispatch(Smtp2Events.InitFrame);
    }
}

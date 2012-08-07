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
package org.talend.mdm.webapp.stagingarea.client;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Stagingarea implements EntryPoint {


    public static final String STAGINGAREA_ID = "Stagingarea"; //$NON-NLS-1$

    public void onModuleLoad() {
        if (GWT.isScript()) {
            XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$
            registerPubService();
            Log.setUncaughtExceptionHandler();
        } else {
            GenerateContainer.generateContentPanel();
            ContentPanel contentPanel = GenerateContainer.getContentPanel();
            contentPanel.setSize(Window.getClientWidth(), Window.getClientHeight());
            StagingareaMainPanel mainPanel = new StagingareaMainPanel();
            contentPanel.add(mainPanel);
            RootPanel.get().add(contentPanel);
        }
    }

    private native void registerPubService()/*-{
        var instance = this;
        $wnd.amalto.stagingarea = {};
        $wnd.amalto.stagingarea.Stagingarea = function() {

        	function initUI() {
        		instance.@org.talend.mdm.webapp.stagingarea.client.Stagingarea::initUI()();
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
        var panel = tabPanel.getItem("Stagingarea");
        if (panel == undefined) {
        	@org.talend.mdm.webapp.stagingarea.client.GenerateContainer::generateContentPanel()();
        	panel = this.@org.talend.mdm.webapp.stagingarea.client.Stagingarea::createPanel()();
        	tabPanel.add(panel);
        }
        tabPanel.setSelection(panel.getItemId());
    }-*/;

    native JavaScriptObject createPanel()/*-{
        var instance = this;
        var panel = {
        	render : function(el) {
        		instance.@org.talend.mdm.webapp.stagingarea.client.Stagingarea::renderContent(Ljava/lang/String;)(el.id);
        	},
        	setSize : function(width, height) {
        		var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
        		cp.@com.extjs.gxt.ui.client.widget.ContentPanel::setSize(II)(width, height);
        	},
        	getItemId : function() {
        		var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
        		return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getItemId()();
        	},
        	getEl : function() {
        		var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
        		var el = cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getElement()();
        		return {
        			dom : el
        		};
        	},
        	doLayout : function() {
        		var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
        		return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::doLayout()();
        	},
        	title : function() {
        		var cp = @org.talend.mdm.webapp.stagingarea.client.GenerateContainer::getContentPanel()();
        		return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getHeading()();
        	}
        };
        return panel;
    }-*/;

    public void renderContent(final String contentId) {
        onModuleRender();
        RootPanel panel = RootPanel.get(contentId);
        GenerateContainer.getContentPanel().setSize(panel.getOffsetWidth(), panel.getOffsetHeight());
        panel.add(GenerateContainer.getContentPanel());
    }

    public void initUI() {
        _initUI();
    }

    private void onModuleRender() {
        ContentPanel contentPanel = GenerateContainer.getContentPanel();
        StagingareaMainPanel mainPanel = new StagingareaMainPanel();
        contentPanel.add(mainPanel);
    }

}

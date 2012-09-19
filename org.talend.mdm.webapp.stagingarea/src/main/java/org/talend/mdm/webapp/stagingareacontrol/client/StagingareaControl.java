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
package org.talend.mdm.webapp.stagingareacontrol.client;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaConfiguration;
import org.talend.mdm.webapp.stagingareacontrol.client.view.StagingareaMainView;

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
public class StagingareaControl implements EntryPoint {


    public static final String STAGINGAREA_ID = "Stagingarea"; //$NON-NLS-1$

    public static StagingAreaServiceAsync service;

    private static StagingAreaConfiguration stagingAreaConfig;

    public static StagingAreaConfiguration getStagingAreaConfig() {
        return stagingAreaConfig;
    }

    public void onModuleLoad() {
        service = GWT.create(StagingAreaService.class);
        if (GWT.isScript()) {
            XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$
            registerPubService();
            Log.setUncaughtExceptionHandler();
        } else {
            service.getStagingAreaConfig(new SessionAwareAsyncCallback<StagingAreaConfiguration>() {

                public void onSuccess(StagingAreaConfiguration stagingAreaConfig) {
                    StagingareaControl.stagingAreaConfig = stagingAreaConfig;
                    UserContextUtil.setDataContainer("Product"); //$NON-NLS-1$
                    UserContextUtil.setDataModel("Product"); //$NON-NLS-1$
                    GenerateContainer.generateContentPanel();
                    ContentPanel contentPanel = GenerateContainer.getContentPanel();
                    contentPanel.setSize(Window.getClientWidth(), Window.getClientHeight());
                    onModuleRender();
                    RootPanel.get().add(contentPanel);
                }
            });
        }
    }

    private native void registerPubService()/*-{
        var instance = this;
        $wnd.amalto.stagingarea = {};
        $wnd.amalto.stagingarea.Stagingarea = function() {

        	function initUI() {
        		instance.@org.talend.mdm.webapp.stagingareacontrol.client.StagingareaControl::initUI()();
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
        	@org.talend.mdm.webapp.stagingareacontrol.client.GenerateContainer::generateContentPanel()();
        	panel = this.@org.talend.mdm.webapp.stagingareacontrol.client.StagingareaControl::createPanel()();
        	tabPanel.add(panel);
        }
        tabPanel.setSelection(panel.getItemId());
    }-*/;

    native JavaScriptObject createPanel()/*-{
        var instance = this;
        var panel = {
        	render : function(el) {
        		instance.@org.talend.mdm.webapp.stagingareacontrol.client.StagingareaControl::renderContent(Ljava/lang/String;)(el.id);
        	},
        	setSize : function(width, height) {
        		var cp = @org.talend.mdm.webapp.stagingareacontrol.client.GenerateContainer::getContentPanel()();
        		cp.@com.extjs.gxt.ui.client.widget.ContentPanel::setSize(II)(width, height);
        	},
        	getItemId : function() {
        		var cp = @org.talend.mdm.webapp.stagingareacontrol.client.GenerateContainer::getContentPanel()();
        		return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getItemId()();
        	},
        	getEl : function() {
        		var cp = @org.talend.mdm.webapp.stagingareacontrol.client.GenerateContainer::getContentPanel()();
        		var el = cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getElement()();
        		return {
        			dom : el
        		};
        	},
        	doLayout : function() {
        		var cp = @org.talend.mdm.webapp.stagingareacontrol.client.GenerateContainer::getContentPanel()();
        		return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::doLayout()();
        	},
        	title : function() {
        		var cp = @org.talend.mdm.webapp.stagingareacontrol.client.GenerateContainer::getContentPanel()();
        		return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getHeading()();
        	}
        };
        return panel;
    }-*/;

    public void renderContent(final String contentId) {
        service.getStagingAreaConfig(new SessionAwareAsyncCallback<StagingAreaConfiguration>() {

            public void onSuccess(StagingAreaConfiguration stagingAreaConfig) {
                StagingareaControl.stagingAreaConfig = stagingAreaConfig;
                onModuleRender();
                RootPanel panel = RootPanel.get(contentId);
                GenerateContainer.getContentPanel().setSize(panel.getOffsetWidth(), panel.getOffsetHeight());
                panel.add(GenerateContainer.getContentPanel());
            }
        });
    }

    public void initUI() {
        _initUI();
    }

    private void onModuleRender() {
        final ContentPanel contentPanel = GenerateContainer.getContentPanel();
        StagingareaMainView mainPanel = new StagingareaMainView();
        contentPanel.add(mainPanel);
    }
}

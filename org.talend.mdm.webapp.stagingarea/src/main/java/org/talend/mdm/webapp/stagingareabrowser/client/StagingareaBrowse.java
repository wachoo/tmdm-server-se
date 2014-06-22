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
package org.talend.mdm.webapp.stagingareabrowser.client;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingareabrowser.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareabrowser.client.controller.SearchController;
import org.talend.mdm.webapp.stagingareabrowser.client.view.StagingareaBrowseView;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.RootPanel;

public class StagingareaBrowse implements EntryPoint {

    public static final String STAGINGAREA_BROWSE_ID = "StagingareaBrowse"; //$NON-NLS-1$

    public static StagingAreaBrowseServiceAsync service;

    public void onModuleLoad() {
        service = GWT.create(StagingAreaBrowseService.class);
        if (GWT.isScript()) {
            XDOM.setAutoIdPrefix(GWT.getModuleName() + "-" + XDOM.getAutoIdPrefix()); //$NON-NLS-1$
            registerPubService();
        } else {
            UserContextUtil.setDataContainer("TestDataContainer"); //$NON-NLS-1$
            UserContextUtil.setDataModel("TestDataModel"); //$NON-NLS-1$
            UserContextUtil.setDateTimeFormat("yyyy/MM/dd HH:mm:ss"); //$NON-NLS-1$

            StagingareaBrowseView view = new StagingareaBrowseView();
            RootPanel.get().add(view);
            SearchController.loadConcepts(new SessionAwareAsyncCallback<List<BaseModel>>() {
                public void onSuccess(List<BaseModel> result) {
                    ControllerContainer.get().getSearchController().defaultDoSearch(2);
                }
            });
        }
    }

    private native void registerPubService()/*-{
        var instance = this;
        $wnd.amalto.stagingareabrowse = {};
        $wnd.amalto.stagingareabrowse.StagingareaBrowse = function() {
            function initUI(state) {
                instance.@org.talend.mdm.webapp.stagingareabrowser.client.StagingareaBrowse::initUI(Ljava/lang/Integer;)(state);
            }
            return {
                init : function(state) {
                    initUI(state);
                }
            }
        }();
    }-*/;
    
    private native void _initUI(String tabId)/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        var panel = tabPanel.getItem(tabId);
        if (panel == undefined) {
            @org.talend.mdm.webapp.stagingareabrowser.client.GenerateContainer::generateContentPanel()();
            panel = this.@org.talend.mdm.webapp.stagingareabrowser.client.StagingareaBrowse::createPanel()();
            tabPanel.add(panel);
        }
        tabPanel.setSelection(panel.getItemId());
        panel.doLayout();
    }-*/;

    native JavaScriptObject createPanel()/*-{
        var instance = this;
        var panel = {
            render : function(el) {
                instance.@org.talend.mdm.webapp.stagingareabrowser.client.StagingareaBrowse::renderContent(Ljava/lang/String;)(el.id);
            },
            setSize : function(width, height) {
                var cp = @org.talend.mdm.webapp.stagingareabrowser.client.GenerateContainer::getContentPanel()();
                cp.@com.extjs.gxt.ui.client.widget.ContentPanel::setSize(II)(width, height);
            },
            getItemId : function() {
                var cp = @org.talend.mdm.webapp.stagingareabrowser.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getItemId()();
            },
            getEl : function() {
                var cp = @org.talend.mdm.webapp.stagingareabrowser.client.GenerateContainer::getContentPanel()();
                var el = cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getElement()();
                return {
                    dom : el
                };
            },
            doLayout : function() {
                var cp = @org.talend.mdm.webapp.stagingareabrowser.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::doLayout()();
            },
            title : function() {
                var cp = @org.talend.mdm.webapp.stagingareabrowser.client.GenerateContainer::getContentPanel()();
                return cp.@com.extjs.gxt.ui.client.widget.ContentPanel::getHeading()();
            }
        };
        return panel;
    }-*/;

    public void initUI(final Integer defaultState) {
        _initUI(STAGINGAREA_BROWSE_ID);
        SearchController.loadConcepts(new SessionAwareAsyncCallback<List<BaseModel>>() {

            public void onSuccess(List<BaseModel> result) {
                ControllerContainer.get().getSearchController().defaultDoSearch(defaultState);
            }
        });
    }

    public void renderContent(final String contentId) {
        onModuleRender();
        RootPanel panel = RootPanel.get(contentId);
        GenerateContainer.getContentPanel().setSize(panel.getOffsetWidth(), panel.getOffsetHeight());
        panel.add(GenerateContainer.getContentPanel());

    }

    private void onModuleRender() {
        StagingareaBrowseView view = new StagingareaBrowseView();
        GenerateContainer.getContentPanel().add(view);
    }
}

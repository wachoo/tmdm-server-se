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
package org.talend.mdm.webapp.journal.client.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.client.widget.PagingToolBarEx;
import org.talend.mdm.webapp.journal.client.Journal;
import org.talend.mdm.webapp.journal.client.JournalServiceAsync;
import org.talend.mdm.webapp.journal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.state.StateManager;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalGridPanel extends ContentPanel {

    private JournalServiceAsync service = Registry.get(Journal.JOURNAL_SERVICE);
    
    private Grid<JournalGridModel> grid;

    private ListStore<JournalGridModel> store;
    
    private PagingToolBarEx pagetoolBar;

    private PagingLoader<PagingLoadResult<JournalGridModel>> loader;
    
    private final static int PAGE_SIZE = 20;

    public JournalGridPanel() {
        this.setLayout(new FitLayout());
        this.setBodyBorder(false);
        this.setFrame(false);
        this.setHeaderVisible(false);
        
        List<ColumnConfig> ccList = new ArrayList<ColumnConfig>();
        ColumnConfig ccDataContainer = new ColumnConfig();
        ccDataContainer.setId("dataContainer"); //$NON-NLS-1$
        ccDataContainer.setHeader(MessagesFactory.getMessages().data_container_label());
        ccDataContainer.setWidth(120);
        ccList.add(ccDataContainer);

        ColumnConfig ccDataModel = new ColumnConfig();
        ccDataModel.setId("dataModel"); //$NON-NLS-1$
        ccDataModel.setHeader(MessagesFactory.getMessages().data_model_label());
        ccDataModel.setWidth(120);
        ccList.add(ccDataModel);
        
        ColumnConfig ccEntity = new ColumnConfig();
        ccEntity.setId("entity"); //$NON-NLS-1$
        ccEntity.setHeader(MessagesFactory.getMessages().entity_label());
        ccEntity.setWidth(120);
        ccList.add(ccEntity);
        
        ColumnConfig ccKey = new ColumnConfig();
        ccKey.setId("key"); //$NON-NLS-1$
        ccKey.setHeader(MessagesFactory.getMessages().key_label());
        ccKey.setWidth(120);
        ccList.add(ccKey);
        
        ColumnConfig ccRevisionId = new ColumnConfig();
        ccRevisionId.setId("revisionId"); //$NON-NLS-1$
        ccRevisionId.setHeader(MessagesFactory.getMessages().revision_id_label());
        ccRevisionId.setWidth(120);
        ccList.add(ccRevisionId);
        
        ColumnConfig ccOperationType = new ColumnConfig();
        ccOperationType.setId("operationType"); //$NON-NLS-1$
        ccOperationType.setHeader(MessagesFactory.getMessages().operation_type_label());
        ccOperationType.setWidth(120);
        ccList.add(ccOperationType);
        
        ColumnConfig ccOperationTime = new ColumnConfig();
        ccOperationTime.setId("operationTime"); //$NON-NLS-1$
        ccOperationTime.setHeader(MessagesFactory.getMessages().operation_time_label());
        ccOperationTime.setWidth(120);
        ccList.add(ccOperationTime);
        
        ColumnConfig ccSource = new ColumnConfig();
        ccSource.setId("source"); //$NON-NLS-1$
        ccSource.setHeader(MessagesFactory.getMessages().source_label());
        ccSource.setWidth(120);
        ccList.add(ccSource);
        
        ColumnConfig ccUserName = new ColumnConfig();
        ccUserName.setId("userName"); //$NON-NLS-1$
        ccUserName.setHeader(MessagesFactory.getMessages().user_name_label());
        ccUserName.setWidth(120);
        ccList.add(ccUserName);
        
        final JournalSearchCriteria criteria = Registry.get(Journal.SEARCH_CRITERIA);
        RpcProxy<PagingLoadResult<JournalGridModel>> proxy = new RpcProxy<PagingLoadResult<JournalGridModel>>() {

            @Override
            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<JournalGridModel>> callback) {
                service.getJournalList(criteria, (PagingLoadConfig) loadConfig,
                        new SessionAwareAsyncCallback<PagingLoadResult<JournalGridModel>>() {

                            public void onSuccess(PagingLoadResult<JournalGridModel> result) {
                                callback.onSuccess(new BasePagingLoadResult<JournalGridModel>(result.getData(), result
                                        .getOffset(), result.getTotalLength()));
                            }

                            @Override
                            protected void doOnFailure(Throwable caught) {
                                super.doOnFailure(caught);
                                callback.onSuccess(new BasePagingLoadResult<JournalGridModel>(new ArrayList<JournalGridModel>(),
                                        0, 0));
                            }
                        });
            }
        };
        
        loader = new BasePagingLoader<PagingLoadResult<JournalGridModel>>(proxy);
        loader.setRemoteSort(true);
        
        store = new ListStore<JournalGridModel>(loader);
        grid = new Grid<JournalGridModel>(store, new ColumnModel(ccList));
        grid.getView().setAutoFill(true);
        grid.getView().setForceFit(true);
        int usePageSize = PAGE_SIZE;
        if (StateManager.get().get("journalgrid") != null) //$NON-NLS-1$
            usePageSize = Integer.valueOf(((Map<?, ?>) StateManager.get().get("journalgrid")).get("limit").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        pagetoolBar = new PagingToolBarEx(usePageSize);
        pagetoolBar.bind(loader);
        grid.setLoadMask(true);
        grid.setStateful(true);
        grid.setStateId("journalgrid");//$NON-NLS-1$
        grid.addListener(Events.Attach, new Listener<GridEvent<JournalGridModel>>() {

            public void handleEvent(GridEvent<JournalGridModel> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                int pageSize = (Integer) pagetoolBar.getPageSize();
                config.setLimit(pageSize);
                loader.load(config);
            }
        });
        
        GridSelectionModel<JournalGridModel> sm = new GridSelectionModel<JournalGridModel>();
        sm.setSelectionMode(SelectionMode.SINGLE);
        grid.setSelectionModel(sm);
        
        grid.addListener(Events.RowDoubleClick, new Listener<GridEvent<JournalGridModel>>() {

            public void handleEvent(GridEvent<JournalGridModel> be) {
                final JournalGridModel gridModel = be.getModel();
                JournalGridPanel.this.openTabPanel(gridModel);
            }
        });
        
        this.add(grid);
        this.setBottomComponent(pagetoolBar);
    }
    
    public void refreshGrid() {
        pagetoolBar.refresh();
    }
    
    public void openTabPanel(final JournalGridModel gridModel){
        service.getDetailTreeModel(gridModel.getIds(), new SessionAwareAsyncCallback<JournalTreeModel>() {

            public void onSuccess(final JournalTreeModel root) {
                service.isEnterpriseVersion(new SessionAwareAsyncCallback<Boolean>() {
                    
                    public void onSuccess(Boolean isEnterprise) {
                        if (GWT.isScript()) {
                            JournalGridPanel.this.openGWTPanel(isEnterprise, gridModel, root);
                        } else {
                            JournalGridPanel.this.openDebugPanel(isEnterprise, gridModel, root);
                        }
                    }
                });
            }
        });
    }
    
    private native void openHistoryTabPanel(String ids, JournalHistoryPanel source)/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        var dataLogViewer = tabPanel.getItem(ids);
        if(dataLogViewer == undefined){
            var panel = @org.talend.mdm.webapp.journal.client.widget.JournalGridPanel::convertHistoryPanel(Lorg/talend/mdm/webapp/journal/client/widget/JournalHistoryPanel;)(source);
            tabPanel.add(panel);   
        }
        tabPanel.setSelection(ids);        
    }-*/;   
    
    private native void openDataTabPanel(String ids, JournalDataPanel source)/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        var dataLogViewer = tabPanel.getItem(ids);
        if(dataLogViewer == undefined){
            var panel = @org.talend.mdm.webapp.journal.client.widget.JournalGridPanel::convertDataPanel(Lorg/talend/mdm/webapp/journal/client/widget/JournalDataPanel;)(source);
            tabPanel.add(panel);
        }
        tabPanel.setSelection(ids);
    }-*/;

    private native static JavaScriptObject convertHistoryPanel(JournalHistoryPanel journalPanel)/*-{
        var panel = {
        // imitate extjs's render method, really call gxt code.
        render : function(el){
        var rootPanel = @com.google.gwt.user.client.ui.RootPanel::get(Ljava/lang/String;)(el.id);
        rootPanel.@com.google.gwt.user.client.ui.RootPanel::add(Lcom/google/gwt/user/client/ui/Widget;)(journalPanel);
        },
        // imitate extjs's setSize method, really call gxt code.
        setSize : function(width, height){
        journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalHistoryPanel::setSize(II)(width, height);
        },
        // imitate extjs's getItemId, really return itemId of ContentPanel of GXT.
        getItemId : function(){
        return journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalHistoryPanel::getItemId()();
        },
        // imitate El object of extjs
        getEl : function(){
        var el = journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalHistoryPanel::getElement()();
        return {dom : el};
        },
        // imitate extjs's doLayout method, really call gxt code.
        doLayout : function(){
        return journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalHistoryPanel::doLayout()();
        },
        title : function(){
        return journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalHistoryPanel::getHeading()();
        }
        };
        return panel;
    }-*/;

    private native static JavaScriptObject convertDataPanel(JournalDataPanel journalPanel)/*-{
        var panel = {
        // imitate extjs's render method, really call gxt code.
        render : function(el){
        var rootPanel = @com.google.gwt.user.client.ui.RootPanel::get(Ljava/lang/String;)(el.id);
        rootPanel.@com.google.gwt.user.client.ui.RootPanel::add(Lcom/google/gwt/user/client/ui/Widget;)(journalPanel);
        },
        // imitate extjs's setSize method, really call gxt code.
        setSize : function(width, height){
        journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalDataPanel::setSize(II)(width, height);
        },
        // imitate extjs's getItemId, really return itemId of ContentPanel of GXT.
        getItemId : function(){
        return journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalDataPanel::getItemId()();
        },
        // imitate El object of extjs
        getEl : function(){
        var el = journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalDataPanel::getElement()();
        return {dom : el};
        },
        // imitate extjs's doLayout method, really call gxt code.
        doLayout : function(){
        return journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalDataPanel::doLayout()();
        },
        title : function(){
        return journalPanel.@org.talend.mdm.webapp.journal.client.widget.JournalDataPanel::getHeadingString()();
        }
        };
        return panel;
    }-*/;
     
    private void openDebugPanel(Boolean isEnterprise, JournalGridModel gridModel, JournalTreeModel root) {
        if (isEnterprise) {
            JournalHistoryPanel journalHistoryPanel = new JournalHistoryPanel(root, gridModel, root.isAuth(), 550);
            Window window = new Window();
            window.setLayout(new FitLayout());
            window.add(journalHistoryPanel);
            window.setSize(1100, 700);
            window.setMaximizable(true);
            window.setModal(false);
            window.show();
            journalHistoryPanel.getJournalDataPanel().getTree().setExpanded(root, true);
        } else {
            JournalDataPanel journalDataPanel = new JournalDataPanel(root, gridModel);
            Window window = new Window();
            window.setLayout(new FitLayout());
            window.add(journalDataPanel);
            window.setSize(1100, 700);
            window.setMaximizable(true);
            window.setModal(false);
            window.show();
            journalDataPanel.getTree().setExpanded(root, true);
        }
    }
    
    private void openGWTPanel(Boolean isEnterprise, JournalGridModel gridModel, JournalTreeModel root) {
        if (isEnterprise) {
            int width = JournalTabPanel.getInstance().getWidth()/2 - 2;
            JournalHistoryPanel journalHistoryPanel = new JournalHistoryPanel(root, gridModel, root.isAuth(), width);
            this.openHistoryTabPanel(gridModel.getIds(), journalHistoryPanel);
            journalHistoryPanel.getJournalDataPanel().getTree().setExpanded(root, true);
        } else {
            JournalDataPanel journalDataPanel = new JournalDataPanel(root, gridModel);
            this.openDataTabPanel(gridModel.getIds(), journalDataPanel);
            journalDataPanel.getTree().setExpanded(root, true);
        }
    }
  
    public String getLoaderConfigStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(","); //$NON-NLS-1$
        sb.append(loader.getLimit()).append(","); //$NON-NLS-1$
        sb.append(loader.getSortDir().toString()).append(","); //$NON-NLS-1$
        sb.append(loader.getSortField()).append(","); //$NON-NLS-1$
        sb.append(UrlUtil.getLanguage());
        return sb.toString();
    }
    
    public int getOffset() {
        return loader.getOffset();
    }
    
    public int getLimit() {
        return loader.getLimit();
    }
}
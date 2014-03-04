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

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.journal.client.Journal;
import org.talend.mdm.webapp.journal.client.JournalServiceAsync;
import org.talend.mdm.webapp.journal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.journal.client.resources.icon.Icons;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;
import com.amalto.core.ejb.UpdateReportPOJO;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalDataPanel extends FormPanel {
    private JournalHistoryPanel journalHistoryPanel;
    
    private JournalServiceAsync service = Registry.get(Journal.JOURNAL_SERVICE);
    
    private ListStore<JournalGridModel> gridstore = JournalGridPanel.getInstance().getStore();
    
    private PagingToolBar pagetoolBar = JournalGridPanel.getInstance().getPagetoolBar();
    
    private PagingLoadConfig pagingLoadConfig;
    
    private PagingLoader<PagingLoadResult<JournalGridModel>> gridLoader;
    
    private LoadListener myListener;
    
    private int naviStartPageIndex;
    
    private Button openRecordButton;
    
    private Button viewUpdateReportButton;

    private List<JournalGridModel> journalNavigateList;
    
    private boolean naviToPrevious;
    
    private boolean navigationMode = false;
    
    private int totalPages;
    
    private int pageIndex;
    
    private List<JournalGridModel> currentDataList;
    
    private boolean turnPage = false;
    
    private Button prevUpdateReportButton;

    private Button nextUpdateReportButton;
    
    private TreePanel<JournalTreeModel> tree;
    
    private Window treeWindow;
    
    private TreeStore<JournalTreeModel> store;

    private JournalTreeModel root;
    
    private JournalGridModel journalGridModel;
    
    private LayoutContainer main;
    
    private LabelField entityField;
    
    private LabelField sourceField;
    
    private LabelField dataContainerField;
    
    private LabelField dataModelField;
    
    private LabelField keyField;
    
    private LabelField operationTypeField;
    
    private LabelField oeprationTimeField;
    
    private LabelField revisionIdField;
    
    private SelectionListener<ButtonEvent> updateReportListener;
    
    public JournalDataPanel(final JournalTreeModel root, final JournalGridModel journalGridModel) {
        this.setFrame(false);
        this.setItemId(journalGridModel.getIds());
        this.journalGridModel = journalGridModel;
        this.root = root;
        this.setHeading(MessagesFactory.getMessages().update_report_detail_label());
        this.setBodyBorder(false);
        this.setLayout(new FitLayout());
        
        openRecordButton = new Button(MessagesFactory.getMessages().open_record_button());
        if (UpdateReportPOJO.OPERATION_TYPE_LOGICAL_DELETE.equals(journalGridModel.getOperationType())
                || UpdateReportPOJO.OPERATION_TYPE_PHYSICAL_DELETE.equals(journalGridModel.getOperationType())) {
            openRecordButton.disable();
        }
        openRecordButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.browse()));
        openRecordButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                service.checkDCAndDM(journalGridModel.getDataContainer(), journalGridModel.getDataModel(), new SessionAwareAsyncCallback<Boolean>() {
                    
                    public void onSuccess(Boolean result) {
                        if(result) {
                            if (journalGridModel.getDataContainer().endsWith("#STAGING")) { //$NON-NLS-1$
                                JournalDataPanel.this.openBrowseRecordPanel4Staging(MessagesFactory.getMessages().journal_label(),
                                        journalGridModel.getKey(), journalGridModel.getEntity());
                            } else {
                                JournalDataPanel.this.openBrowseRecordPanel(MessagesFactory.getMessages().journal_label(),
                                        journalGridModel.getKey(), journalGridModel.getEntity());
                            }
                        } else {
                            MessageBox.alert(MessagesFactory.getMessages().error_level(), MessagesFactory.getMessages()
                                    .select_contain_model_msg(), null);
                        }
                    }
                });
            }
        });
        
        viewUpdateReportButton = new Button(MessagesFactory.getMessages().view_updatereport_button());
        viewUpdateReportButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.view()));
        
        this.updateReportListener = createUpdateReportListener(root);
        viewUpdateReportButton.addSelectionListener(this.updateReportListener);

        prevUpdateReportButton = new Button(MessagesFactory.getMessages().prev_updatereport_button());
        prevUpdateReportButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.prev()));        
        prevUpdateReportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                naviToPrevious = true;
                JournalGridModel preGridModel = journalNavigateList.get(0);
                if (preGridModel != null) {
                    JournalDataPanel.this.updateTabPanel(preGridModel);
                } else {
                    retrieveNeighbourJournalInOtherPages();
                }
            }
        });

        nextUpdateReportButton = new Button(MessagesFactory.getMessages().next_updatereport_button());
        nextUpdateReportButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.next()));
        nextUpdateReportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                naviToPrevious = false;
                JournalGridModel nextGridModel = journalNavigateList.get(1);
                if (nextGridModel != null) {
                    JournalDataPanel.this.updateTabPanel(nextGridModel);
                } else {
                    retrieveNeighbourJournalInOtherPages();
                }
            }
        });

        this.setJournalNavigateList();
        
        this.addButton(viewUpdateReportButton);
        this.addButton(prevUpdateReportButton);
        this.addButton(nextUpdateReportButton);
        this.addButton(openRecordButton);

        initializeTreeWindow();
              
        this.setHeading(MessagesFactory.getMessages().change_properties());
        this.setButtonAlign(HorizontalAlignment.RIGHT);
        
        initializeMain();
        FormData formData = new FormData("100%"); //$NON-NLS-1$
        formData.setMargins(new Margins(10, 10, 10, 10));
        this.add(main, formData);        
        
        gridLoader = (PagingLoader<PagingLoadResult<JournalGridModel>>)gridstore.getLoader();
               
        myListener =  new LoadListener() {

            @Override
            public void loaderLoad(LoadEvent le) {
                currentDataList = ((BasePagingLoadResult<JournalGridModel>) le.getData()).getData();
                if (!turnPage) {
                    setJournalNaviList();
                } else {
                    JournalGridModel targetGridModel;
                    if (naviToPrevious) {
                        targetGridModel = currentDataList.get(currentDataList.size() - 1);
                    } else {
                        targetGridModel = currentDataList.get(0);
                    }
                    JournalDataPanel.this.updateTabPanel(targetGridModel);
                    turnPage = false;
                }

            }
        };
        
        gridLoader.addLoadListener(myListener);
    }

    protected void setJournalNaviList() {
        this.turnPage = false;
        int index = 0;
        for (int i = 0; i < currentDataList.size(); i++) {
            if (this.journalGridModel.getOperationTime().equals(currentDataList.get(i).getOperationTime())) {
                index = i;
            }
        }
        journalNavigateList = new ArrayList<JournalGridModel>(2);
        journalNavigateList.add(index == 0 ? null : currentDataList.get(index - 1));
        journalNavigateList.add(index == currentDataList.size() - 1 ? null : currentDataList.get(index + 1));
        
        if (pageIndex == 1 && journalNavigateList.get(0) == null) {
            prevUpdateReportButton.setEnabled(false);
        } else {
            prevUpdateReportButton.setEnabled(true);
        }
        
        if (index == totalPages && journalNavigateList.get(1) == null) {
            nextUpdateReportButton.setEnabled(false);
        } else {
            nextUpdateReportButton.setEnabled(true);
        }
    }

    private SelectionListener<ButtonEvent> createUpdateReportListener(final JournalTreeModel newRoot) {
        return new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                treeWindow.show();
                tree.setExpanded(newRoot, true);
            }
        };
    }

    private void initializeMain() {
        main = new LayoutContainer();
        main.setLayout(new ColumnLayout());
        
        LayoutContainer left = new LayoutContainer();
        left.setStyleAttribute("paddingRight", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        FormLayout layout = new FormLayout();
        layout.setLabelAlign(LabelAlign.LEFT);
        layout.setLabelWidth(150);
        left.setWidth(350);
        left.setLayout(layout);
        
        entityField = new LabelField();
        entityField.setFieldLabel(MessagesFactory.getMessages().entity_label() + " : "); //$NON-NLS-1$     
        entityField.setValue(this.journalGridModel.getEntity());
        left.add(entityField);
        sourceField = new LabelField();
        sourceField.setFieldLabel(MessagesFactory.getMessages().source_label() + " : "); //$NON-NLS-1$
        sourceField.setValue(this.journalGridModel.getSource());
        left.add(sourceField);
        dataContainerField = new LabelField();
        dataContainerField.setFieldLabel(MessagesFactory.getMessages().data_container_label() + " : "); //$NON-NLS-1$
        dataContainerField.setValue(this.journalGridModel.getDataContainer());
        left.add(dataContainerField);
        dataModelField = new LabelField();
        dataModelField.setFieldLabel(MessagesFactory.getMessages().data_model_label() + " : "); //$NON-NLS-1$
        dataModelField.setValue(this.journalGridModel.getDataModel());
        left.add(dataModelField);

        LayoutContainer right = new LayoutContainer();
        right.setStyleAttribute("paddingLeft", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        layout = new FormLayout();    
        layout.setLabelAlign(LabelAlign.LEFT);
        layout.setLabelWidth(150);
        right.setWidth(350);
        right.setLayout(layout);
        
        keyField = new LabelField();
        keyField.setFieldLabel(MessagesFactory.getMessages().key_label() + " : "); //$NON-NLS-1$
        keyField.setValue(this.journalGridModel.getKey());        
        right.add(keyField);
        operationTypeField = new LabelField();
        operationTypeField.setFieldLabel(MessagesFactory.getMessages().operation_type_label() + " : "); //$NON-NLS-1$
        operationTypeField.setValue(this.journalGridModel.getOperationType());
        right.add(operationTypeField);
        oeprationTimeField = new LabelField();
        oeprationTimeField.setFieldLabel(MessagesFactory.getMessages().operation_time_label() + " : "); //$NON-NLS-1$
        oeprationTimeField.setValue(this.journalGridModel.getOperationDate());
        right.add(oeprationTimeField);
        revisionIdField = new LabelField();
        revisionIdField.setFieldLabel(MessagesFactory.getMessages().revision_id_label() + " : "); //$NON-NLS-1$
        revisionIdField.setValue(this.journalGridModel.getRevisionId());
        right.add(revisionIdField);     

        main.add(left, new ColumnData(.5));
        main.add(right, new ColumnData(.5));
    }

    private void initializeTreeWindow() {
        treeWindow = new Window();
        treeWindow.setHeading(MessagesFactory.getMessages().updatereport_label());
        treeWindow.setWidth(400);
        treeWindow.setHeight(450);
        treeWindow.setLayout(new FitLayout());
        treeWindow.setScrollMode(Scroll.NONE);
        
        store = new TreeStore<JournalTreeModel>();  
        store.add(this.root, true);
        tree = new TreePanel<JournalTreeModel>(store);
        tree.setDisplayProperty("name"); //$NON-NLS-1$
        tree.getStyle().setLeafIcon(AbstractImagePrototype.create(Icons.INSTANCE.leaf()));
        
        ContentPanel contentPanel = new ContentPanel();
        contentPanel.setHeaderVisible(false);
        contentPanel.setScrollMode(Scroll.AUTO);
        contentPanel.setLayout(new FitLayout());
        contentPanel.add(tree);
        treeWindow.add(contentPanel);
    }

    public TreePanel<JournalTreeModel> getTree() {
        return tree;
    }
    
    public JournalGridModel getJournalGridModel() {
        return this.journalGridModel;
    }

    public List<JournalGridModel> getJournalNavigateList () {
        return this.journalNavigateList;
    }
    
    public String getHeadingString() {
        return MessagesFactory.getMessages().data_change_viewer();
    }    

    public void setjournalHistoryPanel(JournalHistoryPanel journalHistoryPanel) {
        this.journalHistoryPanel = journalHistoryPanel;
    }
    
    public void updateTabPanel(final JournalGridModel gridModel) {
        service.getDetailTreeModel(gridModel.getIds(), new SessionAwareAsyncCallback<JournalTreeModel>() {

            @Override
            public void onSuccess(final JournalTreeModel newRoot) {
                service.isEnterpriseVersion(new SessionAwareAsyncCallback<Boolean>() {

                    @Override
                    public void onSuccess(Boolean isEnterprise) {
                        if (GWT.isScript()) {
                            JournalDataPanel.this.updateGWTPanel(isEnterprise, gridModel, newRoot);
                        } else {
                            JournalDataPanel.this.updateDebugPanel(isEnterprise, gridModel, newRoot);
                        }
                    }
                });
            }
        });
    }

    private void updateGWTPanel(Boolean isEnterprise, JournalGridModel gridModel, JournalTreeModel newRoot) {
        this.update(gridModel, newRoot);
        if (isEnterprise) {
            this.setItemId(this.journalGridModel.getIds());
            this.journalHistoryPanel.update();
        }
    }

    private void updateDebugPanel(Boolean isEnterprise, JournalGridModel gridModel, JournalTreeModel newRoot) {
        this.update(gridModel, newRoot);
        this.journalHistoryPanel.update();
        if (isEnterprise) {
            Window window = new Window();
            window.setLayout(new FitLayout());
            window.add(this.journalHistoryPanel);
            window.setSize(1100, 700);
            window.setMaximizable(true);
            window.setModal(false);
            window.show();
        } else {
            Window window = new Window();
            window.setLayout(new FitLayout());
            window.add(this);
            window.setSize(1100, 700);
            window.setMaximizable(true);
            window.setModal(false);
            window.show();
        }
    }
    
    private void setJournalNavigateList() {
        navigationMode = false;
        totalPages = pagetoolBar.getTotalPages();
        pageIndex = pagetoolBar.getActivePage();
        pagingLoadConfig = (PagingLoadConfig)gridstore.getLoadConfig();
        
        currentDataList = gridstore.getModels();
        journalNavigateList = new ArrayList<JournalGridModel>(2);

        int index = 0;
        for (int i = 0; i < currentDataList.size(); i++) {
            if (this.journalGridModel.getOperationTime().equals(currentDataList.get(i).getOperationTime())) {
                index = i;
            }
        }
        
        journalNavigateList.add(index == 0 ? null : currentDataList.get(index - 1));
        journalNavigateList.add(index == currentDataList.size() - 1 ? null : currentDataList.get(index + 1)); 
        
        if (pageIndex == 1 && journalNavigateList.get(0) == null) {
            prevUpdateReportButton.setEnabled(false);
        } else {
            prevUpdateReportButton.setEnabled(true);
        }
        
        if (index == totalPages && journalNavigateList.get(1) == null) {
            nextUpdateReportButton.setEnabled(false);
        } else {
            nextUpdateReportButton.setEnabled(true);
        }
    }    
    
    private void retrieveNeighbourJournalInOtherPages() {
        naviStartPageIndex = pageIndex;
        navigationMode = true;
        
        if (naviToPrevious) {
            if ( pageIndex > 1) {
                pagetoolBar.previous();
            }
        } else {
            if (pageIndex < totalPages) {
                pagetoolBar.next();
            }
        }
    }
    
    private void update(JournalGridModel gridModel, JournalTreeModel newRoot) {
        this.journalGridModel = gridModel;
        this.root = newRoot;
        
        this.updateUpdateReportListener(newRoot);

        updateMainFieldValues(gridModel);
        setJournalNavigateList();
        this.layout();
    }
    
    private void updateUpdateReportListener(JournalTreeModel newRoot) {
        this.viewUpdateReportButton.removeSelectionListener(this.updateReportListener);
        this.updateReportListener = createUpdateReportListener(newRoot);
        this.viewUpdateReportButton.addSelectionListener(this.updateReportListener);
    }
    private void updateMainFieldValues(JournalGridModel gridModel) {
        this.entityField.setValue(gridModel.getEntity());
        this.sourceField.setValue(gridModel.getSource());
        this.dataContainerField.setValue(gridModel.getDataContainer());
        this.dataModelField.setValue(gridModel.getDataModel());
        this.keyField.setValue(gridModel.getKey());
        this.operationTypeField.setValue(gridModel.getOperationType());
        this.oeprationTimeField.setValue(gridModel.getOperationTime());
        this.revisionIdField.setValue(gridModel.getRevisionId());
    }
        
    private native void openBrowseRecordPanel(String title, String key, String concept)/*-{
        var arr = key.split("\.");
        $wnd.amalto.itemsbrowser.ItemsBrowser.editItemDetails(title, arr, concept, function(){});
    }-*/;
    
    private native void openBrowseRecordPanel4Staging(String title, String key, String concept)/*-{
        var arr = key.split("\.");
        $wnd.amalto.itemsbrowser.ItemsBrowser.editItemDetails4Staging(title, arr, concept, function(){});
    }-*/;
}
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
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.BreadCrumbModel;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsView;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.util.ViewUtil;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.ComboBoxField;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.CloseTabPostDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.ContainerUpdate;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.DeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.DeleteCallback;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.ListRefresh;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.LogicalDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.NoOpPostDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.integrity.PostDeleteAction;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyTreeDetail;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetailUtil;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonGroup;
import com.extjs.gxt.ui.client.widget.button.SplitButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.ToolBarLayout;
import com.extjs.gxt.ui.client.widget.menu.HeaderMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class ItemDetailToolBar extends ToolBar {

    public final static String CREATE_OPERATION = "CREATE"; //$NON-NLS-1$

    public final static String VIEW_OPERATION = "VIEW"; //$NON-NLS-1$

    public final static String SMARTVIEW_OPERATION = "SMARTVIEW"; //$NON-NLS-1$

    public final static String PERSONALEVIEW_OPERATION = "PERSONALVIEW"; //$NON-NLS-1$

    public final static String DUPLICATE_OPERATION = "DUPLICATE_OPERATION"; //$NON-NLS-1$

    private Button saveButton;

    private Button saveAndCloseButton;

    private Button deleteButton;

    private Button relationButton;

    private Button personalviewButton;

    private Button generatedviewButton;

    private Button duplicateButton;

    private Button journalButton;

    private Button refreshButton;

    private Button openTabButton;

    private Button launchProcessButton;

    private ComboBoxField<ItemBaseModel> smartViewCombo;

    private ComboBoxField<ItemBaseModel> workFlowCombo;

    private ItemBean itemBean;

    private ViewBean viewBean;

    private String operation;

    private boolean isFkToolBar;

    private BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private ItemBaseModel selectItem;

    private Button taskButton;

    private Menu deleteMenu;

    private MenuItem delete_SendToTrash;

    private MenuItem delete_Delete;

    private ItemsDetailPanel itemsDetailPanel;

    private boolean isOutMost;

    private boolean isHierarchyCall;

    private boolean openTab;

    public ItemDetailToolBar() {
        this.setBorders(false);
        this.setLayout(new ToolBarExLayout());
    }

    public ItemDetailToolBar(ItemsDetailPanel itemsDetailPanel) {
        this();
        this.itemsDetailPanel = itemsDetailPanel;
    }

    public ItemDetailToolBar(ItemBean itemBean, String operation, ViewBean viewBean, ItemsDetailPanel itemsDetailPanel) {
        this(itemsDetailPanel);
        this.itemBean = itemBean;
        this.operation = operation;
        this.viewBean = viewBean;
        initToolBar();
    }

    public ItemDetailToolBar(ItemBean itemBean, String operation, ViewBean viewBean, ItemsDetailPanel itemsDetailPanel,
            boolean openTab) {
        this(itemsDetailPanel);
        this.itemBean = itemBean;
        this.operation = operation;
        this.viewBean = viewBean;
        this.openTab = openTab;
        initToolBar();
    }

    public ItemDetailToolBar(ItemBean itemBean, String operation, boolean isFkToolBar, ViewBean viewBean,
            ItemsDetailPanel itemsDetailPanel) {
        this(itemsDetailPanel);
        this.itemBean = itemBean;
        this.operation = operation;
        this.isFkToolBar = isFkToolBar;
        this.viewBean = viewBean;
        initToolBar();
    }

    private void checkEntitlement(ViewBean viewBean) {
        if (deleteButton == null)
            return;
        String concept = this.itemBean.getConcept();
        boolean denyLogicalDelete = viewBean.getBindingEntityModel().getMetaDataTypes().get(concept).isDenyLogicalDeletable();
        boolean denyPhysicalDelete = viewBean.getBindingEntityModel().getMetaDataTypes().get(concept).isDenyPhysicalDeleteable();

        if (denyLogicalDelete && denyPhysicalDelete)
            deleteButton.setEnabled(false);
        else {
            deleteButton.setEnabled(true);
            if (denyLogicalDelete)
                delete_SendToTrash.setEnabled(false);
            if (denyPhysicalDelete)
                delete_Delete.setEnabled(false);
        }
    }

    private static int TOOLBAR_HEIGHT = 29;

    private void initToolBar() {
        this.setHeight(TOOLBAR_HEIGHT + "px"); //$NON-NLS-1$
        this.addStyleName("ItemDetailToolBar"); //$NON-NLS-1$       
        if (operation.equalsIgnoreCase(ItemDetailToolBar.VIEW_OPERATION)
                || operation.equalsIgnoreCase(ItemDetailToolBar.PERSONALEVIEW_OPERATION)) {
            initViewToolBar();
        } else if (operation.equalsIgnoreCase(ItemDetailToolBar.CREATE_OPERATION)) {
            initCreateToolBar();
        } else if (operation.equalsIgnoreCase(ItemDetailToolBar.SMARTVIEW_OPERATION)) {
            initSmartViewToolBar();
        } else if (operation.equalsIgnoreCase(ItemDetailToolBar.DUPLICATE_OPERATION)) {
            initCreateToolBar();
        }
    }

    private void initViewToolBar() {
        if (!operation.equalsIgnoreCase(ItemDetailToolBar.VIEW_OPERATION)) {
            addPersonalViewButton();
            this.addSeparator();
        }
        this.addSaveButton();
        this.addSeparator();
        this.addSaveQuitButton();
        this.addSeparator();
        this.addDeleteMenu();
        this.addSeparator();
        this.addDuplicateButton();
        this.addSeparator();
        this.addJournalButton();
        this.addSeparator();
        this.addFreshButton();
        if (this.openTab) {
            this.addSeparator();
            this.addOpenTabButton(false);
        }
        if (isUseRelations()) {
            this.addRelationButton();
        }
        this.addWorkFlosCombo();    
        this.addOpenTaskButton();
        checkEntitlement(viewBean);
    }

    private void initCreateToolBar() {
        this.addSaveButton();
        this.addSeparator();
        this.addSaveQuitButton();
        if (isUseRelations()) {
            this.addRelationButton();
        }
        this.addWorkFlosCombo();
    }

    private boolean isUseRelations() {
        boolean isUseRelations = false;
        if (BrowseRecords.getSession().getAppHeader() != null) {
            isUseRelations = BrowseRecords.getSession().getAppHeader().isUseRelations();
        }
        return isUseRelations;
    }
    
    /**
     * call it only when save the foreignKey in primaryKey view or save the outMost entity
     */
    public void refresh(String ids) {
        if (this.operation.equals(CREATE_OPERATION) || this.operation.equals(DUPLICATE_OPERATION)
                || this.operation.equals(VIEW_OPERATION)) {
            this.removeAll();
            final boolean isClearBreadCrumb = this.operation.equals(CREATE_OPERATION)
                    || this.operation.equals(DUPLICATE_OPERATION);
            this.operation = VIEW_OPERATION;
            if (!this.isOutMost) {
                this.openTab = true;
            }
            service.getForeignKeyModel(itemBean.getConcept(), ids, Locale.getLanguage(),
                    new SessionAwareAsyncCallback<ForeignKeyModel>() {

                        public void onSuccess(ForeignKeyModel model) {
                            itemBean = model.getItemBean();
                            // refresh toolBar
                            initViewToolBar();
                            // refresh tree
                            ItemPanel itemPanel = (ItemPanel) itemsDetailPanel.getFirstTabWidget();
                            itemPanel.setItem(itemBean);
                            itemPanel.refreshTree();
                            // refresh itemsDetailPanel(include tab title, banner, breadCrumb)
                            TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(itemBean.getConcept());
                            String tabText = typeModel.getLabel(Locale.getLanguage()) + " " + itemBean.getIds(); //$NON-NLS-1$
                            if (isClearBreadCrumb) {
                                if (!ItemDetailToolBar.this.isOutMost) {
                                    ItemsMainTabPanel.getInstance().getSelectedItem().setText(tabText);
                                } else {
                                    updateOutTabPanel(tabText);
                                }
                                itemsDetailPanel.clearBreadCrumb();
                                List<BreadCrumbModel> breads = new ArrayList<BreadCrumbModel>();
                                breads.add(new BreadCrumbModel("", BreadCrumb.DEFAULTNAME, null, null, false)); //$NON-NLS-1$
                                breads.add(new BreadCrumbModel(itemBean.getConcept(), itemBean.getLabel(), itemBean.getIds(),
                                        itemBean.getDisplayPKInfo().equals(itemBean.getLabel()) ? null : itemBean
                                                .getDisplayPKInfo(), true));
                                itemsDetailPanel.initBreadCrumb(new BreadCrumb(breads, itemsDetailPanel));
                            }
                            itemsDetailPanel.clearBanner();
                            itemsDetailPanel.initBanner(itemBean.getPkInfoList(), itemBean.getDescription());
                        };
                    });
        }
    }

    private void addSaveButton() {
        if (saveButton == null) {
            saveButton = new Button(MessagesFactory.getMessages().save_btn());
            saveButton.setId("saveButton"); //$NON-NLS-1$
            saveButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
            saveButton.setToolTip(MessagesFactory.getMessages().save_tip());
            saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    Widget widget = itemsDetailPanel.getFirstTabWidget();
                    TreeDetail treeDetail = ((ItemPanel) widget).getTree();
                    if (!BrowseRecords.getSession().getAppHeader().isAutoValidate()) {
                        treeDetail.makeWarning(treeDetail.getRoot());
                    }
                    ItemNodeModel root = treeDetail.getRootModel();
                    if (operation.equalsIgnoreCase(ItemDetailToolBar.CREATE_OPERATION)
                            || operation.equalsIgnoreCase(ItemDetailToolBar.DUPLICATE_OPERATION)
                            || TreeDetailUtil.isChangeValue(root)) {
                        saveItemAndClose(false);
                    } else {
                        MessageBox.info(MessagesFactory.getMessages().info_title(), MessagesFactory.getMessages()
                                .no_change_info(), null);
                    }
                }
            });
        }
        add(saveButton);
    }

    private void addSaveQuitButton() {
        if (saveAndCloseButton == null) {
            saveAndCloseButton = new Button(MessagesFactory.getMessages().save_close_btn());
            saveAndCloseButton.setId("saveAndCloseButton"); //$NON-NLS-1$
            saveAndCloseButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.save_and_close()));
            saveAndCloseButton.setToolTip(MessagesFactory.getMessages().save_close_tip());

            saveAndCloseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    Widget widget = itemsDetailPanel.getFirstTabWidget();
                    TreeDetail treeDetail = ((ItemPanel) widget).getTree();
                    if (!BrowseRecords.getSession().getAppHeader().isAutoValidate()) {
                        treeDetail.makeWarning(treeDetail.getRoot());
                    }
                    ItemNodeModel root = treeDetail.getRootModel();
                    if (operation.equalsIgnoreCase(ItemDetailToolBar.CREATE_OPERATION)
                            || operation.equalsIgnoreCase(ItemDetailToolBar.DUPLICATE_OPERATION)
                            || TreeDetailUtil.isChangeValue(root)) {
                        saveItemAndClose(true);
                    } else {
                        MessageBox.info(MessagesFactory.getMessages().info_title(), MessagesFactory.getMessages()
                                .no_change_info(), null);
                    }
                }
            });
        }
        add(saveAndCloseButton);
    }

    private void addDeleteMenu() {
        if (deleteButton == null) {
            deleteButton = new Button(MessagesFactory.getMessages().delete_btn());
            deleteButton.setId("deleteButton"); //$NON-NLS-1$
            deleteButton.setToolTip(MessagesFactory.getMessages().delete_tip());
            deleteButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));

            deleteMenu = new Menu();
            deleteMenu.setId("deleteMenu"); //$NON-NLS-1$
            delete_SendToTrash = new MenuItem(MessagesFactory.getMessages().trash_btn());
            delete_SendToTrash.setId("delete_SendToTrash"); //$NON-NLS-1$
            delete_SendToTrash.addSelectionListener(new SelectionListener<MenuEvent>() {

                @Override
                public void componentSelected(MenuEvent ce) {
                    PostDeleteAction postDeleteAction = new CloseTabPostDeleteAction(ItemDetailToolBar.this, new ListRefresh(
                            ItemDetailToolBar.this, new ContainerUpdate(ItemDetailToolBar.this, NoOpPostDeleteAction.INSTANCE)));
                    DeleteAction deleteAction = new LogicalDeleteAction("/"); //$NON-NLS-1$
                    // Collections.singletonList(itemBean) --- it could not be sent to backend correctly
                    List<ItemBean> list = new ArrayList<ItemBean>();
                    list.add(itemBean);
                    service.checkFKIntegrity(list, new DeleteCallback(deleteAction, postDeleteAction, service));
                }
            });
            delete_SendToTrash.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
            deleteMenu.add(delete_SendToTrash);

            delete_Delete = new MenuItem(MessagesFactory.getMessages().delete_btn());
            delete_Delete.setId("delete_Delete"); //$NON-NLS-1$
            deleteMenu.add(delete_Delete);
            delete_Delete.addSelectionListener(new SelectionListener<MenuEvent>() {

                @Override
                public void componentSelected(MenuEvent ce) {
                    MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                            .delete_confirm(), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                PostDeleteAction postDeleteAction = new CloseTabPostDeleteAction(ItemDetailToolBar.this,
                                        new ListRefresh(ItemDetailToolBar.this, new ContainerUpdate(ItemDetailToolBar.this,
                                                NoOpPostDeleteAction.INSTANCE)));
                                List<ItemBean> list = new ArrayList<ItemBean>();
                                list.add(itemBean);
                                service.checkFKIntegrity(list, new DeleteCallback(DeleteAction.PHYSICAL, postDeleteAction,
                                        service));
                            }
                        }
                    });

                }
            });

            delete_Delete.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
            deleteButton.setMenu(deleteMenu);
        }

        add(deleteButton);
    }

    private void addDuplicateButton() {
        if (duplicateButton == null) {
            duplicateButton = new Button(MessagesFactory.getMessages().duplicate_btn());
            duplicateButton.setId("duplicateButton"); //$NON-NLS-1$
            duplicateButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.duplicate()));
            duplicateButton.setToolTip(MessagesFactory.getMessages().duplicate_tip());
            duplicateButton.setEnabled(!viewBean.getBindingEntityModel().getMetaDataTypes().get(itemBean.getConcept()).isDenyCreatable());
            duplicateButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    if (!isFkToolBar) {
                        ItemsListPanel.getInstance().setCreate(true);
                    }

                    ItemBean duplicateItemBean = new ItemBean(itemBean.getConcept(), itemBean.getIds(), itemBean.getItemXml(),
                            itemBean.getDescription(), itemBean.getPkInfoList());
                    duplicateItemBean.copy(itemBean);
                    duplicateItemBean.setIds(""); //$NON-NLS-1$

                    TreeDetailUtil.initItemsDetailPanelByItemPanel(viewBean, duplicateItemBean, isFkToolBar, isHierarchyCall,
                            isOutMost);
                    if (!isOutMost && !isFkToolBar) {
                        if (ItemsListPanel.getInstance().getGrid() != null) {
                            ItemsListPanel.getInstance().getGrid().getSelectionModel().deselectAll();
                        }
                    }
                }
            });
        }
        add(duplicateButton);
    }

    private void addJournalButton() {
        if (journalButton == null) {
            journalButton = new Button(MessagesFactory.getMessages().journal_btn());
            journalButton.setId("journalButton"); //$NON-NLS-1$
            journalButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.journal()));
            journalButton.setToolTip(MessagesFactory.getMessages().journal_tip());

            journalButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    String ids = itemBean.getIds();
                    if (ids.indexOf("@") != -1) { //$NON-NLS-1$
                        ids = ids.replaceAll("@", "."); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    initJournal(ids, itemBean.getConcept());
                }

            });
        }
        add(journalButton);
    }

    private void addFreshButton() {
        if (refreshButton == null) {
            refreshButton = new Button();
            refreshButton.setId("refreshButton"); //$NON-NLS-1$
            refreshButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.refreshToolbar()));
            refreshButton.setToolTip(MessagesFactory.getMessages().refresh_tip());
            refreshButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    refreshTreeDetail();
                }

            });
        }
        add(refreshButton);
    }

    private void refreshTreeDetail() {
        Widget widget = itemsDetailPanel.getFirstTabWidget();
        if (widget instanceof ForeignKeyTreeDetail) {
            isFkToolBar = true;
            final ForeignKeyTreeDetail fkTree = (ForeignKeyTreeDetail) widget;
            ItemNodeModel root = fkTree.getRootModel();
            refreshTree(null, fkTree, root);
        } else {
            ItemPanel itemPanel = (ItemPanel) widget;
            ItemNodeModel root = (ItemNodeModel) itemPanel.getTree().getRootModel();
            refreshTree(itemPanel, null, root);
        }
    }

    public void refreshTreeDetailByIds(String ids) {
        Widget widget = itemsDetailPanel.getFirstTabWidget();
        if (widget instanceof ForeignKeyTreeDetail) {
            isFkToolBar = true;
            final ForeignKeyTreeDetail fkTree = (ForeignKeyTreeDetail) widget;
            fkTree.getFkModel().getItemBean().setIds(ids);
            fkTree.refreshTree();
        } else {
            ItemPanel itemPanel = (ItemPanel) widget;
            itemPanel.getItem().setIds(ids);
            itemPanel.refreshTree();
        }
        this.removeAll();
        operation = ItemDetailToolBar.VIEW_OPERATION;
        initToolBar();
    }

    private void refreshTree(final ItemPanel itemPanel, final ForeignKeyTreeDetail fkTree, final ItemNodeModel root) {
        ItemBean itemBean = itemPanel.getItem();
        service.isItemModifiedByOthers(itemBean, new SessionAwareAsyncCallback<Boolean>() {

            public void onSuccess(Boolean result) {
                if (TreeDetailUtil.isChangeValue(root) || result) {
                    MessageBox
                            .confirm(MessagesFactory.getMessages().confirm_title(),
                                    MessagesFactory.getMessages().msg_confirm_refresh_tree_detail(),
                                    new Listener<MessageBoxEvent>() {

                                        public void handleEvent(MessageBoxEvent be) {
                                            if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                                                itemPanel.refreshTree();
                                            }
                                        }
                                    }).getDialog().setWidth(600);
                } else {
                    itemPanel.refreshTree();
                }
            }

            @Override
            protected void doOnFailure(Throwable caught) {
                MessageBox
                        .alert(MessagesFactory.getMessages().refresh_tip(), MessagesFactory.getMessages().refresh_error(), null);
            }

        });
    }

    private void addRelationButton() {
        @SuppressWarnings("unchecked")
        final Map<String, List<String>> lineageEntityMap = (Map<String, List<String>>) BrowseRecords.getSession().get(
                UserSession.CURRENT_LINEAGE_ENTITY_LIST);
        if (lineageEntityMap != null && lineageEntityMap.containsKey(itemBean.getConcept()))
            setRelation(lineageEntityMap.get(itemBean.getConcept()));
        else
            service.getLineageEntity(itemBean.getConcept(), new SessionAwareAsyncCallback<List<String>>() {

                public void onSuccess(List<String> list) {
                    if (lineageEntityMap != null)
                        lineageEntityMap.put(itemBean.getConcept(), list);
                    else {
                        Map<String, List<String>> map = new HashMap<String, List<String>>(1);
                        map.put(itemBean.getConcept(), list);
                        BrowseRecords.getSession().put(UserSession.CURRENT_LINEAGE_ENTITY_LIST, map);
                    }
                    setRelation(list);
                }

            });
    }

    private void setRelation(List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        final List<String> lineageList = list;
        relationButton = new Button(MessagesFactory.getMessages().relations_btn());
        relationButton.setId("relationButton"); //$NON-NLS-1$
        relationButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.relations()));
        relationButton.setToolTip(MessagesFactory.getMessages().relations_tooltip());
        relationButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                StringBuilder entityStr = new StringBuilder();
                for (String str : lineageList)
                    entityStr.append(str).append(","); //$NON-NLS-1$
                String arrStr = entityStr.toString().substring(0, entityStr.length() - 1);
                String ids = itemBean.getIds();
                if (ids == null || ids.trim() == "") //$NON-NLS-1$
                    ids = ""; //$NON-NLS-1$
                initSearchEntityPanel(arrStr, ids, itemBean.getConcept());
            }
        });
        ItemDetailToolBar.this.addSeparator();
        add(relationButton);
    }

    private void addOpenTabButton(final boolean fromSmartView) {
        openTabButton = new Button();
        openTabButton.setId("openTabButton"); //$NON-NLS-1$
        openTabButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.openTab()));
        openTabButton.setToolTip(MessagesFactory.getMessages().openitem_tab());
        openTabButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                // TMDM-3202 open in a top-level tab
                String fromWhichApp = isHierarchyCall ? MessagesFactory.getMessages().hierarchy_title() : ""; //$NON-NLS-1$
                String opt = ItemDetailToolBar.VIEW_OPERATION;
                if (fromSmartView) {
                    opt = ItemDetailToolBar.SMARTVIEW_OPERATION;
                } else {
                    String smartViewMode = itemBean.getSmartViewMode();
                    if (smartViewMode.equals(ItemBean.PERSOMODE) || smartViewMode.equals(ItemBean.SMARTMODE)) {
                        opt = ItemDetailToolBar.PERSONALEVIEW_OPERATION;
                    }
                }
                TreeDetailUtil.initItemsDetailPanelById(fromWhichApp, itemBean.getIds(), itemBean.getConcept(), isFkToolBar,
                        isHierarchyCall, opt);
            }
        });
        openTabButton.setWidth(30);
        add(openTabButton);
    }

    private void addWorkFlosCombo() {
        @SuppressWarnings("unchecked")
        final Map<String, List<ItemBaseModel>> runnableProcessListMap = (Map<String, List<ItemBaseModel>>) BrowseRecords
                .getSession().get(UserSession.CURRENT_RUNNABLE_PROCESS_LIST);
        if (runnableProcessListMap != null && runnableProcessListMap.containsKey(itemBean.getConcept()))
            setWorkFlowCombo(runnableProcessListMap.get(itemBean.getConcept()));
        else
            service.getRunnableProcessList(itemBean.getConcept(), Locale.getLanguage(),
                    new SessionAwareAsyncCallback<List<ItemBaseModel>>() {

                        public void onSuccess(List<ItemBaseModel> processList) {
                            if (runnableProcessListMap != null)
                                runnableProcessListMap.put(itemBean.getConcept(), processList);
                            else {
                                Map<String, List<ItemBaseModel>> map = new HashMap<String, List<ItemBaseModel>>(1);
                                map.put(itemBean.getConcept(), processList);
                                BrowseRecords.getSession().put(UserSession.CURRENT_RUNNABLE_PROCESS_LIST, map);
                            }
                            setWorkFlowCombo(processList);
                        }
                    });
    }

    private void setWorkFlowCombo(List<ItemBaseModel> processList) {
        add(new FillToolItem());
        ListStore<ItemBaseModel> workFlowList = new ListStore<ItemBaseModel>();
        workFlowList.add(processList);
        if (workFlowCombo == null) {
            workFlowCombo = new ComboBoxField<ItemBaseModel>();
            workFlowCombo.setId("workFlowCombo"); //$NON-NLS-1$
            workFlowCombo.setStore(workFlowList);
            workFlowCombo.setDisplayField("value");//$NON-NLS-1$
            workFlowCombo.setValueField("key");//$NON-NLS-1$
            workFlowCombo.setTypeAhead(true);
            workFlowCombo.setTriggerAction(TriggerAction.ALL);
            workFlowCombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

                @Override
                public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                    selectItem = se.getSelectedItem();
                }
            });
        }
        add(workFlowCombo);
        if (launchProcessButton == null) {
            launchProcessButton = new Button();
            launchProcessButton.setId("launchProcessButton"); //$NON-NLS-1$
            launchProcessButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.launch_process()));
            launchProcessButton.setToolTip(MessagesFactory.getMessages().launch_process_tooltip());
            launchProcessButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    if (selectItem == null) {
                        MessageBox.alert(MessagesFactory.getMessages().warning_title(), MessagesFactory.getMessages()
                                .process_select(), null);
                        return;
                    }
                    final MessageBox waitBar = MessageBox.wait(MessagesFactory.getMessages().process_progress_bar_title(),
                            MessagesFactory.getMessages().process_progress_bar_message(), MessagesFactory.getMessages()
                                    .process_progress_bar_title() + "..."); //$NON-NLS-1$
                    String[] ids = itemBean.getIds().split("@"); //$NON-NLS-1$

                    service.processItem(itemBean.getConcept(), ids,
                            (String) selectItem.get("key"), new SessionAwareAsyncCallback<String>() { //$NON-NLS-1$

                                public void onSuccess(final String urlResult) {
                                    waitBar.close();
                                    MessageBox.alert(MessagesFactory.getMessages().status(), MessagesFactory.getMessages()
                                            .process_done(), new Listener<MessageBoxEvent>() {

                                        public void handleEvent(MessageBoxEvent be) {
                                            if (urlResult != null && urlResult.length() > 0) {
                                                openWindow(urlResult);
                                            }
                                        }
                                    });
                                }

                                @Override
                                protected void doOnFailure(Throwable caught) {
                                    waitBar.close();
                                    super.doOnFailure(caught);
                                }
                            });
                }
            });
        }
        add(launchProcessButton);
    }

    private void addPersonalViewButton() {
        personalviewButton = new Button(MessagesFactory.getMessages().personalview_btn());
        personalviewButton.setId("personalviewButton"); //$NON-NLS-1$
        personalviewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                updateSmartViewToolBar();
                if (itemsDetailPanel.getFirstTabWidget() instanceof ItemPanel) {
                    ItemPanel itemPanel = (ItemPanel) itemsDetailPanel.getFirstTabWidget();
                    itemPanel.getTree().setVisible(false);
                    itemPanel.getSmartPanel().setVisible(true);
                    itemsDetailPanel.selectTabAtIndex(0);
                }
            }
        });
        add(personalviewButton);
    }

    private void addGeneratedViewButton() {
        generatedviewButton = new Button(MessagesFactory.getMessages().generatedview_btn());
        generatedviewButton.setId("generatedviewButton"); //$NON-NLS-1$
        generatedviewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                updateViewToolBar();
                if (itemsDetailPanel.getFirstTabWidget() instanceof ItemPanel) {
                    ItemPanel itemPanel = (ItemPanel) itemsDetailPanel.getFirstTabWidget();
                    itemPanel.getTree().setVisible(true);
                    itemPanel.getSmartPanel().setVisible(false);
                    itemsDetailPanel.selectTabAtIndex(0);
                }
            }
        });
        add(generatedviewButton);
    }

    private void addOpenTaskButton() {
        if (taskButton == null && itemBean.getTaskId() != null && itemBean.getTaskId().trim().length() > 0
                && !"null".equalsIgnoreCase(itemBean.getTaskId().trim())) { //$NON-NLS-1$
            ItemDetailToolBar.this.addSeparator();
            this.taskButton = new Button(MessagesFactory.getMessages().open_task());
            taskButton.setId("taskButton"); //$NON-NLS-1$

            taskButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    initDSC(itemBean.getTaskId());
                }
            });

            add(taskButton);
        }
    }

    private native boolean initDSC(String taskId)/*-{
		$wnd.amalto.datastewardship.Datastewardship.taskItem(taskId);
		return true;
    }-*/;

    public void initSmartViewToolBar() {
        addGeneratedViewButton();
        addSeparator();
        addSmartViewCombo();
        addDeleteMenu();
        addSeparator();
        addPrintButton();
        addSeparator();
        this.addDuplicateButton();
        this.addSeparator();
        this.addJournalButton();
        this.addSeparator();
        this.addFreshButton();
        if (this.openTab) {
            this.addSeparator();
            this.addOpenTabButton(true);
        }
        this.addWorkFlosCombo();
    }

    private void updateSmartViewToolBar() {
        int tabCount = itemsDetailPanel.getTabCount();
        for (int i = 0; i < tabCount; ++i) {
            Widget widget = itemsDetailPanel.getTabWidgetAtIndex(i);
            if (widget instanceof ItemPanel) {
                ItemPanel itemPanel = (ItemPanel) widget;
                ItemDetailToolBar toolbar = itemPanel.getToolBar();
                if (toolbar.getOperation().equals(ItemDetailToolBar.VIEW_OPERATION)
                        || toolbar.getOperation().equals(ItemDetailToolBar.PERSONALEVIEW_OPERATION)) {
                    toolbar.removeAll();
                    toolbar.initSmartViewToolBar();
                    toolbar.setOperation(ItemDetailToolBar.SMARTVIEW_OPERATION);
                    toolbar.layout(true);
                }
            }
        }
    }

    private void updateViewToolBar() {
        int tabCount = itemsDetailPanel.getTabCount();

        for (int i = 0; i < tabCount; ++i) {
            Widget widget = itemsDetailPanel.getTabWidgetAtIndex(i);
            if (widget instanceof ItemPanel) {
                ItemPanel itemPanel = (ItemPanel) widget;
                ItemDetailToolBar toolbar = itemPanel.getToolBar();

                if (toolbar.getOperation().equals(ItemDetailToolBar.SMARTVIEW_OPERATION)) {
                    itemsDetailPanel.selectTabAtIndex(i);
                    toolbar.removeAll();
                    toolbar.initViewToolBar();
                    toolbar.setOperation(ItemDetailToolBar.PERSONALEVIEW_OPERATION);
                    toolbar.layout(true);
                }
            }
        }
    }

    private void addSmartViewCombo() {
        final ListStore<ItemBaseModel> smartViewList = new ListStore<ItemBaseModel>();
        if (smartViewCombo == null) {
            smartViewCombo = new ComboBoxField<ItemBaseModel>();
            smartViewCombo.setId("smartViewCombo"); //$NON-NLS-1$
            smartViewCombo.setStore(smartViewList);
            smartViewCombo.setDisplayField("value"); //$NON-NLS-1$
            smartViewCombo.setValueField("key"); //$NON-NLS-1$
            smartViewCombo.setTypeAhead(true);
            smartViewCombo.setTriggerAction(TriggerAction.ALL);
            smartViewCombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

                @Override
                public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                    if (itemsDetailPanel.getFirstTabWidget() instanceof ItemPanel) {
                        ItemPanel itemPanel = (ItemPanel) itemsDetailPanel.getFirstTabWidget();
                        String frameUrl = "/browserecords/secure/SmartViewServlet?ids=" + escapeAmp(itemBean.getIds()) + "&concept=" //$NON-NLS-1$ //$NON-NLS-2$
                                + itemBean.getConcept() + "&language=" + Locale.getLanguage(); //$NON-NLS-1$
                        if (se.getSelectedItem().get("key") != null) //$NON-NLS-1$
                            frameUrl += ("&name=" + se.getSelectedItem().get("key"));//$NON-NLS-1$ //$NON-NLS-2$
                        itemPanel.getSmartPanel().setUrl(frameUrl);
                        itemPanel.getSmartPanel().layout(true);
                    }
                }
                
            });
        }

        String regex = itemBean.getConcept() + "&" + Locale.getLanguage(); //$NON-NLS-1$
        service.getSmartViewList(regex, new SessionAwareAsyncCallback<List<ItemBaseModel>>() {

            public void onSuccess(List<ItemBaseModel> list) {
                smartViewList.add(list);
                smartViewList.sort("value", SortDir.ASC); //$NON-NLS-1$
                if (smartViewCombo.getValue() == null) {
                    smartViewCombo.setValue(ViewUtil.getDefaultSmartViewModel(smartViewList.getModels(), itemBean.getConcept()));
                }
            }

        });
        add(smartViewCombo);
    }

    private String escapeAmp(String ids){
        return ids.replaceAll("&", "%26"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addPrintButton() {
        Button printBtn = new Button(MessagesFactory.getMessages().print_btn());
        printBtn.setId("printBtn"); //$NON-NLS-1$
        printBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (smartViewCombo.getSelection() != null && smartViewCombo.getSelection().size() > 0) {

                    StringBuilder url = new StringBuilder();
                    url.append("/browserecords/secure/SmartViewServlet?ids=") //$NON-NLS-1$
                            .append(escapeAmp(itemBean.getIds())).append("&concept=") //$NON-NLS-1$
                            .append(itemBean.getConcept()).append("&language=") //$NON-NLS-1$
                            .append(Locale.getLanguage()).append("&name=") //$NON-NLS-1$
                            .append(smartViewCombo.getSelection().get(0).get("value")); //$NON-NLS-1$

                    openWindow(url.toString());
                }
            }

        });
        add(printBtn);
    }

    private void addSeparator() {
        add(new SeparatorToolItem());
    }

    public void updateToolBar() {

    }

    private native boolean initJournal(String ids, String concept)/*-{
		$wnd.amalto.journal.Journal
				.browseJournalWithCriteria(ids, concept, true);
		return true;
    }-*/;

    // Please note that this method is duplicated in
    // org.talend.mdm.webapp.browserecords.client.widget.integrity.SingletonDeleteStrategy.initSearchEntityPanel()
    private native boolean initSearchEntityPanel(String arrStr, String ids, String dataObject)/*-{
		var lineageEntities = arrStr.split(",");
		$wnd.amalto.itemsbrowser.ItemsBrowser.lineageItem(lineageEntities, ids,
				dataObject);
		return true;
    }-*/;

    public void saveItemAndClose(final boolean isClose) {
        if (itemBean.getIds().trim().equals("")) { //$NON-NLS-1$
            saveItemWithIdCheck(isClose);
            return;
        }

        service.isItemModifiedByOthers(itemBean, new SessionAwareAsyncCallback<Boolean>() {

            public void onSuccess(Boolean result) {
                if (result) {
                    MessageBox
                            .confirm(MessagesFactory.getMessages().confirm_title(),
                                    MessagesFactory.getMessages().save_concurrent_fail(), new Listener<MessageBoxEvent>() {

                                        public void handleEvent(MessageBoxEvent be) {
                                            if (Dialog.YES.equals(be.getButtonClicked().getItemId())) {
                                                saveItem(isClose);
                                            }
                                        }
                                    }).getDialog().setWidth(600);
                } else {
                    saveItem(isClose);
                }
            }
        });
    }

    private void saveItemWithIdCheck(final boolean isClose) {

        Widget widget = itemsDetailPanel.getFirstTabWidget();
        if (widget instanceof ItemPanel) {
            ItemPanel itemPanel = (ItemPanel) widget;
            ItemNodeModel model = (ItemNodeModel) itemPanel.getTree().getRootModel();
            String[] keyArray = CommonUtil.extractIDs(model,viewBean);

            service.isExistId(this.itemBean.getConcept(), keyArray, Locale.getLanguage(), new SessionAwareAsyncCallback<Boolean>() {

                public void onSuccess(Boolean flag) {
                    if (flag){
                        MessageBox.alert(MessagesFactory.getMessages().info_title(),
                                MessagesFactory.getMessages().record_exists(), null).getDialog().setWidth(400);
                    }else{
                        saveItem(isClose);
                    }
                }
            });

        } else {
            saveItem(isClose);
        }
    }

    private void saveItem(boolean isClose) {
        boolean validate = false;
        Widget widget = itemsDetailPanel.getFirstTabWidget();
        Dispatcher dispatch = Dispatcher.get();
        AppEvent app = new AppEvent(BrowseRecordsEvents.SaveItem);
        ItemNodeModel model = null;
        if (widget instanceof ItemPanel) {// save primary key
            ItemPanel itemPanel = (ItemPanel) widget;
            validate = true;
            model = (ItemNodeModel) itemPanel.getTree().getRootModel();
            app.setData("ItemBean", itemPanel.getItem()); //$NON-NLS-1$
            app.setData(
                    "isCreate", itemPanel.getOperation().equals(ItemDetailToolBar.CREATE_OPERATION) || itemPanel.getOperation().equals(ItemDetailToolBar.DUPLICATE_OPERATION) ? true : false); //$NON-NLS-1$
        } else if (widget instanceof ForeignKeyTreeDetail) { // save foreign key
            ForeignKeyTreeDetail fkDetail = (ForeignKeyTreeDetail) widget;
            if (fkDetail.validateTree()) {
                validate = true;
                model = fkDetail.getRootModel();
                app.setData(
                        "ItemBean", fkDetail.isCreate() ? new ItemBean(fkDetail.getViewBean().getBindingEntityModel().getConceptName(), "", "") : itemBean); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                app.setData("isCreate", fkDetail.isCreate()); //$NON-NLS-1$
            }
        }
        app.setData("viewBean", viewBean); //$NON-NLS-1$
        app.setData(model);
        app.setData("isClose", isClose); //$NON-NLS-1$
        app.setData("itemDetailToolBar", this); //$NON-NLS-1$
        app.setData(BrowseRecordsView.ITEMS_DETAIL_PANEL, itemsDetailPanel);

        if (validate) {
            dispatch.dispatch(app);
        } else {
            // TMDM-3349 button 'save and close' function
            if (isClose && !isOutMost && !isHierarchyCall)
                ItemsListPanel.getInstance().initSpecialVariable();
            MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().save_error(), null);
        }
    }

    public void setSelectItem(ItemBaseModel selectItem) {
        this.selectItem = selectItem;
    }

    public String getOperation() {
        return operation;
    }

    public ItemBean getItemBean() {
        return itemBean;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setOutMost(boolean isOutMost) {
        this.isOutMost = isOutMost;
    }

    public boolean isOutMost() {
        return isOutMost;
    }

    public boolean isHierarchyCall() {
        return isHierarchyCall;
    }

    public void setHierarchyCall(boolean isHierarchyCall) {
        this.isHierarchyCall = isHierarchyCall;
    }

    public boolean isOpenTab() {
        return openTab;
    }

    public void setOpenTab(boolean openTab) {
        this.openTab = openTab;
    }

    public void closeCurrentTabPanel() {
        ItemsMainTabPanel.getInstance().remove(ItemsMainTabPanel.getInstance().getSelectedItem());
    }

    /**
     * call when save the most out tabItem
     */
    public void refreshNodeStatus() {
        ItemPanel itemPanel = (ItemPanel) itemsDetailPanel.getFirstTabWidget();
        ItemNodeModel root = (ItemNodeModel) itemPanel.getTree().getRootModel();
        setChangeValue(root);
    }

    private void setChangeValue(ItemNodeModel model) {
        if (model.isChangeValue())
            model.setChangeValue(false);
        for (ModelData node : model.getChildren()) {
            setChangeValue((ItemNodeModel) node);
        }
    }

    public native void closeOutTabPanel()/*-{
		var tabPanel = $wnd.amalto.core.getTabPanel();
		tabPanel.closeCurrentTab();
    }-*/;

    public native void updateOutTabPanel(String tabText)/*-{
		var tabPanel = $wnd.amalto.core.getTabPanel();
		tabPanel.updateCurrentTabText(tabText);
    }-*/;

    class MenuEx extends Menu {

        public MenuEx() {
            super();
            monitorWindowResize = false;
        }
    }

    class ToolBarExLayout extends ToolBarLayout {

        protected void onComponentShow(Component component) {
            if (component.isRendered()) {
                if (component.el().getParent() != null) {
                    component.el().getParent().removeStyleName(component.getHideMode().value());
                }
            }
        }

        protected void initMore() {
            if (more == null) {
                moreMenu = new MenuEx();
                moreMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {

                    public void handleEvent(MenuEvent be) {
                        clearMenu();
                        for (Component c : container.getItems()) {

                            if (isHidden(c)) {
                                addComponentToMenu(be.getContainer(), c);
                            }
                        }
                        if (be.getContainer().getItemCount() == 0) {
                            be.getContainer().add(new HeaderMenuItem(getNoItemsMenuText()));
                        }
                    }

                });

                more = new Button();
                more.addStyleName("x-toolbar-more"); //$NON-NLS-1$
                more.setIcon(GXT.IMAGES.toolbar_more());
                more.setMenu(moreMenu);

            }
            Element td = insertCell(more, getExtrasTr(), 100);
            if (more.isRendered()) {
                td.appendChild(more.el().dom);
            } else {
                more.render(td);
            }
            ComponentHelper.doAttach(more);

            moreMenu.setWidth(230);
        }

        private native El getExtrasTr()/*-{
			return this.@com.extjs.gxt.ui.client.widget.layout.ToolBarLayout::extrasTr;
        }-*/;

        @SuppressWarnings("unchecked")
        protected void addComponentToMenu(Menu menu, Component c) {
            if (c instanceof SeparatorToolItem) {
                menu.add(new SeparatorMenuItem());
            } else if (c instanceof SplitButton) {
                final SplitButton sb = (SplitButton) c;
                MenuItem item = new MenuItem(sb.getText(), sb.getIcon());
                item.setEnabled(c.isEnabled());
                item.setItemId(c.getItemId());
                if (sb.getMenu() != null) {
                    item.setSubMenu(sb.getMenu());
                }
                item.addSelectionListener(new SelectionListener<MenuEvent>() {

                    @Override
                    public void componentSelected(MenuEvent ce) {
                        ButtonEvent e = new ButtonEvent(sb);
                        e.setEvent(ce.getEvent());
                        sb.fireEvent(Events.Select, e);
                    }

                });
                menu.add(item);
            } else if (c instanceof LabelToolItem) {
                LabelToolItem l = (LabelToolItem) c;
                MenuItem item = new MenuItem(l.getLabel());
                menu.add(item);
            } else if (c instanceof ComboBox<?>) {
                final ComboBox<ItemBaseModel> cb = (ComboBox<ItemBaseModel>) c;
                ComboBox<ItemBaseModel> comboBoxClone = new ComboBox<ItemBaseModel>();
                comboBoxClone.setStore(cb.getStore());
                comboBoxClone.setDisplayField("value");//$NON-NLS-1$
                comboBoxClone.setValueField("key");//$NON-NLS-1$
                comboBoxClone.setTypeAhead(true);
                comboBoxClone.setTriggerAction(TriggerAction.ALL);
                comboBoxClone.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                        cb.setValue(se.getSelectedItem());
                    }
                });
                if (cb.getValue() != null)
                    comboBoxClone.setValue(cb.getValue());
                menu.add(comboBoxClone);
            } else if (c instanceof ComboBoxField<?>) {
                final ComboBoxField<ItemBaseModel> cb = (ComboBoxField<ItemBaseModel>) c;
                ComboBoxField<ItemBaseModel> comboBoxClone = new ComboBoxField<ItemBaseModel>();
                comboBoxClone.setStore(cb.getStore());
                comboBoxClone.setDisplayField("value");//$NON-NLS-1$
                comboBoxClone.setValueField("key");//$NON-NLS-1$
                comboBoxClone.setTypeAhead(true);
                comboBoxClone.setTriggerAction(TriggerAction.ALL);
                comboBoxClone.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

                    @Override
                    public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                        cb.setValue(se.getSelectedItem());
                    }
                });
                if (cb.getValue() != null)
                    comboBoxClone.setValue(cb.getValue());
                menu.add(comboBoxClone);
            } else if (c instanceof Button) {
                final Button b = (Button) c;
                String menuText = b.getText();
                if (menuText == null || menuText.trim().length() == 0) {
                    menuText = b.getToolTip().getToolTipConfig() == null ? "" : b.getToolTip().getToolTipConfig().getText(); //$NON-NLS-1$
                }
                MenuItem item = new MenuItem(menuText, b.getIcon());
                if (b.getToolTip() != null) {
                    item.setToolTip(b.getToolTip().getToolTipConfig());
                }
                item.setItemId(c.getItemId());
                if (b.getMenu() != null) {
                    item.setHideOnClick(false);
                    item.setSubMenu(b.getMenu());
                }
                item.setEnabled(c.isEnabled());
                item.addSelectionListener(new SelectionListener<MenuEvent>() {

                    @Override
                    public void componentSelected(MenuEvent ce) {
                        ButtonEvent e = new ButtonEvent(b);
                        e.setEvent(ce.getEvent());
                        b.fireEvent(Events.Select, e);
                    }

                });
                menu.add(item);
            } else if (c instanceof ButtonGroup) {
                ButtonGroup g = (ButtonGroup) c;
                g.setItemId(c.getItemId());
                menu.add(new SeparatorMenuItem());
                String heading = g.getHeading();
                if (heading != null && heading.length() > 0 && !heading.equals("&#160;")) { //$NON-NLS-1$
                    menu.add(new HeaderMenuItem(g.getHeading()));
                }
                for (Component c2 : g.getItems()) {
                    addComponentToMenu(menu, c2);
                }
                menu.add(new SeparatorMenuItem());
            }

            if (menu.getItemCount() > 0) {
                if (menu.getItem(0) instanceof SeparatorMenuItem) {
                    menu.remove(menu.getItem(0));
                }
                if (menu.getItemCount() > 0) {
                    if (menu.getItem(menu.getItemCount() - 1) instanceof SeparatorMenuItem) {
                        menu.remove(menu.getItem(menu.getItemCount() - 1));
                    }
                }
            }
        }
    }

    private native void openWindow(String url)/*-{
		window.open(url);
    }-*/;

    public boolean isFkToolBar() {
        return isFkToolBar;
    }

    public void setFkToolBar(boolean isFkToolBar) {
        this.isFkToolBar = isFkToolBar;
    }
}

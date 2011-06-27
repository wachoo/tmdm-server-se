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
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.creator.ItemCreator;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.util.Locale;
import org.talend.mdm.webapp.itemsbrowser2.client.util.ViewUtil;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.AdvancedSearchPanel;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.SimpleCriterionPanel;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.ComboBoxField;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ItemsToolBar extends ToolBar {

    private final static int PAGE_SIZE = 10;

    private boolean isSimple;

    private static String userCluster = null;

    private SimpleCriterionPanel<?> simplePanel;

    private AdvancedSearchPanel advancedPanel;

    private ComboBoxField<ItemBaseModel> entityCombo = new ComboBoxField<ItemBaseModel>();

    public final Button searchBut = new Button(MessagesFactory.getMessages().search_btn());

    private final ToggleButton advancedBut = new ToggleButton(MessagesFactory.getMessages().advsearch_btn());

    private final Button managebookBtn = new Button();

    private final Button bookmarkBtn = new Button();

    private Button createBtn = new Button(MessagesFactory.getMessages().create_btn());

    private Button menu = new Button(MessagesFactory.getMessages().delete_btn());

    private Button uploadBtn = new Button(MessagesFactory.getMessages().itemsBrowser_Import_Export());

    private ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    private ItemsToolBar instance = this;

    private List<ItemBaseModel> userCriteriasList;

    private ListStore<ItemBaseModel> tableList = new ListStore<ItemBaseModel>();

    private boolean advancedPanelVisible = false;

    private boolean bookmarkShared = false;

    private String bookmarkName = null;

    private String currentTableName = null;

    private ComboBox<ItemBaseModel> combo = null;

    private int fieldCount = 1;

    /*************************************/

    public ItemsToolBar() {
        // init user saved model
        userCluster = Itemsbrowser2.getSession().getAppHeader().getDatacluster();
        this.setBorders(false);
        initToolBar();
    }

    public void setQueryModel(QueryModel qm) {
        qm.setDataClusterPK(userCluster);
        qm.setView(Itemsbrowser2.getSession().getCurrentView());
        qm.setModel(Itemsbrowser2.getSession().getCurrentEntityModel());
        if (isSimple)
            qm.setCriteria(simplePanel.getCriteria().toString());
        else
            qm.setCriteria(advancedPanel.getCriteria());
    }

    public void updateToolBar(ViewBean viewBean) {
        simplePanel.updateFields(viewBean);
        if (advancedPanel != null) {
            advancedPanel.setView(viewBean);
            advancedPanel.cleanCriteria();
        }
        searchBut.setEnabled(true);
        advancedBut.setEnabled(true);
        managebookBtn.setEnabled(true);
        bookmarkBtn.setEnabled(true);

        createBtn.setEnabled(false);
        menu.setEnabled(false);
        String concept = ViewUtil.getConceptFromBrowseItemView(entityCombo.getValue().get("value").toString());//$NON-NLS-1$
        if (!viewBean.getBindingEntityModel().getMetaDataTypes().get(concept).isDenyCreatable())
            createBtn.setEnabled(true);
        boolean denyLogicalDelete = viewBean.getBindingEntityModel().getMetaDataTypes().get(concept).isDenyLogicalDeletable();
        boolean denyPhysicalDelete = viewBean.getBindingEntityModel().getMetaDataTypes().get(concept).isDenyPhysicalDeleteable();

        if (denyLogicalDelete && denyPhysicalDelete)
            menu.setEnabled(false);
        else {
            menu.setEnabled(true);
            if (denyPhysicalDelete)
                menu.getMenu().getItemByItemId("physicalDelMenuInGrid").setEnabled(false); //$NON-NLS-1$
            else
                menu.getMenu().getItemByItemId("physicalDelMenuInGrid").setEnabled(true); //$NON-NLS-1$
            if (denyLogicalDelete)
                menu.getMenu().getItemByItemId("logicalDelMenuInGrid").setEnabled(false); //$NON-NLS-1$
            else
                menu.getMenu().getItemByItemId("logicalDelMenuInGrid").setEnabled(true); //$NON-NLS-1$
        }

        uploadBtn.setEnabled(false);
        boolean denyUploadFile = viewBean.getBindingEntityModel().getMetaDataTypes().get(concept).isDenyLogicalDeletable();

        if (denyUploadFile)
            uploadBtn.setEnabled(false);
        else {
            uploadBtn.setEnabled(true);
            if (denyUploadFile)
                uploadBtn.getMenu().getItemByItemId("uploadMenuInGrid").setEnabled(false);//$NON-NLS-1$
            else
                uploadBtn.getMenu().getItemByItemId("uploadMenuInGrid").setEnabled(true);//$NON-NLS-1$
        }

        updateUserCriteriasList();
    }

    public int getSuccessItemsNumber(List<ItemResult> results) {
        int itemSuccessNumber = 0;
        for (ItemResult result : results) {
            if (result.getStatus() == ItemResult.SUCCESS) {
                itemSuccessNumber++;
            }
        }
        return itemSuccessNumber;
    }

    public int getFailureItemsNumber(List<ItemResult> results) {
        int itemFailureNumber = 0;
        for (ItemResult result : results) {
            if (result.getStatus() == ItemResult.FAILURE) {
                itemFailureNumber++;
            }
        }
        return itemFailureNumber;
    }

    public int getSelectItemNumber() {
        int number = 0;
        ItemsListPanel list = (ItemsListPanel) instance.getParent();
        number = list.getGrid().getSelectionModel().getSelectedItems().size();
        return number;
    }

    private void initToolBar() {
        createBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Create()));
        createBtn.setEnabled(false);
        add(createBtn);
        createBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                String concept = ViewUtil.getConceptFromBrowseItemView(entityCombo.getValue().get("value").toString());//$NON-NLS-1$

                EntityModel entityModel = (EntityModel) Itemsbrowser2.getSession().getCurrentEntityModel();
                ItemBean item = ItemCreator.createDefaultItemBean(concept, entityModel);

                AppEvent evt = new AppEvent(ItemsEvents.ViewItemForm, item);
                evt.setData(ItemsView.ITEMS_FORM_TARGET, ItemsView.TARGET_IN_NEW_TAB);
                Dispatcher.forwardEvent(evt);
            }

        });

        menu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
        Menu sub = new Menu();
        MenuItem delMenu = new MenuItem(MessagesFactory.getMessages().delete_btn());
        delMenu.setId("physicalDelMenuInGrid");//$NON-NLS-1$
        delMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));

        // TODO duplicate with recordToolbar
        delMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {

                if (((ItemsListPanel) instance.getParent()).getGrid() == null) {
                    com.google.gwt.user.client.Window.alert(MessagesFactory.getMessages().select_delete_item_record());
                } else {
                    if (getSelectItemNumber() == 0) {
                        com.google.gwt.user.client.Window.alert(MessagesFactory.getMessages().select_delete_item_record());
                    } else {
                        MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                                .delete_confirm(), new Listener<MessageBoxEvent>() {

                            final ItemsListPanel list = (ItemsListPanel) instance.getParent();

                            public void handleEvent(MessageBoxEvent be) {
                                if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                    if (list.getGrid() != null) {
                                        service.deleteItemBeans(list.getGrid().getSelectionModel().getSelectedItems(),
                                                new AsyncCallback<List<ItemResult>>() {

                                                    public void onFailure(Throwable caught) {
                                                        Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                                                    }

                                                    public void onSuccess(List<ItemResult> results) {
                                                        StringBuffer msgs = new StringBuffer();

                                                        int successNum = getSuccessItemsNumber(results);
                                                        int failureNum = getFailureItemsNumber(results);

                                                        if (successNum == 1 && failureNum == 0) {
                                                            String msg = results.iterator().next().getDescription();
                                                            MessageBox.info(MessagesFactory.getMessages().info_title(),
                                                                    pickOutISOMessage(msg.toString()), null);
                                                        } else if (successNum > 1 && failureNum == 0) {
                                                            msgs.append(MessagesFactory.getMessages().delete_item_record_success(
                                                                    successNum));
                                                            MessageBox.info(MessagesFactory.getMessages().info_title(),
                                                                    msgs.toString(), null);
                                                        } else if (successNum == 0 && failureNum == 1) {
                                                            String msg = results.iterator().next().getDescription();
                                                            MessageBox.alert(MessagesFactory.getMessages().error_title(),
                                                                    pickOutISOMessage(msg), null);
                                                        } else if (successNum == 0 && failureNum > 1) {
                                                            msgs.append(MessagesFactory.getMessages().delete_item_record_failure(
                                                                    failureNum));
                                                            MessageBox.alert(MessagesFactory.getMessages().error_title(),
                                                                    msgs.toString(), null);
                                                        } else if (successNum > 0 && failureNum > 0) {
                                                            msgs.append(MessagesFactory.getMessages().delete_item_record_success(
                                                                    successNum)
                                                                    + "\n");//$NON-NLS-1$
                                                            msgs.append(MessagesFactory.getMessages().delete_item_record_failure(
                                                                    failureNum)
                                                                    + "\n");//$NON-NLS-1$
                                                            MessageBox.info(MessagesFactory.getMessages().info_title(),
                                                                    msgs.toString(), null);
                                                        }

                                                        list.getStore().getLoader().load();
                                                    }

                                                });
                                    }

                                }
                            }

                            private String pickOutISOMessage(String message) {
                                String identy = "[" + Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader())//$NON-NLS-1$
                                        .toUpperCase() + ":";//$NON-NLS-1$
                                int mask = message.indexOf(identy);
                                if (mask != -1) {
                                    String snippet = message.substring(mask + identy.length());
                                    if (!snippet.isEmpty()) {
                                        String pickOver = "";//$NON-NLS-1$
                                        boolean enclosed = false;
                                        for (int j = 0; j < snippet.trim().length(); j++) {
                                            String c = snippet.trim().charAt(j) + "";//$NON-NLS-1$
                                            if ("]".equals(c)) {//$NON-NLS-1$
                                                if (!pickOver.isEmpty()) {
                                                    enclosed = true;
                                                    break;
                                                }
                                            } else {
                                                pickOver += c;
                                            }
                                        }

                                        if (enclosed)
                                            return pickOver;
                                    }
                                }
                                return message;
                            }
                        });
                    }
                }
            }
        });

        MenuItem trashMenu = new MenuItem(MessagesFactory.getMessages().trash_btn());
        trashMenu.setId("logicalDelMenuInGrid");//$NON-NLS-1$
        trashMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
        trashMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {
                final MessageBox box = MessageBox.prompt(MessagesFactory.getMessages().path(), MessagesFactory.getMessages()
                        .path_desc(), new Listener<MessageBoxEvent>() {

                    public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
                            final ItemsListPanel list = (ItemsListPanel) instance.getParent();
                            if (list.getGrid() != null) {
                                service.logicalDeleteItems(list.getGrid().getSelectionModel().getSelectedItems(), "/", //$NON-NLS-1$
                                        new AsyncCallback<List<ItemResult>>() {

                                            public void onFailure(Throwable caught) {
                                                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                                            }

                                            public void onSuccess(List<ItemResult> results) {
                                                for (ItemResult result : results) {
                                                    if (result.getStatus() == ItemResult.FAILURE) {
                                                        MessageBox.alert(MessagesFactory.getMessages().error_title(),
                                                                result.getDescription(), null);
                                                        return;
                                                    }
                                                }
                                                list.getStore().getLoader().load();
                                            }

                                        });

                            }
                        }
                    }
                });
                box.getTextBox().setValue("/"); //$NON-NLS-1$
            }
        });

        sub.add(trashMenu);
        sub.add(delMenu);

        menu.setMenu(sub);
        menu.setEnabled(false);
        add(menu);

        uploadBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        Menu subFile = new Menu();
        MenuItem uploadMenu = new MenuItem(MessagesFactory.getMessages().itemsBrowser_Import());
        uploadMenu.setId("uploadMenuInGrid");//$NON-NLS-1$
        uploadMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));

        uploadMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {
                TabPanel tabFrame = (TabPanel) Registry.get(ItemsView.TAB_FRAME);
                TabItem item = tabFrame.getItemByItemId("upload-main-panel"); //$NON-NLS-1$
                currentTableName = null;

                if (item == null) {

                    item = new TabItem();
                    item.setItemId("upload-main-panel"); //$NON-NLS-1$
                    item.setText(MessagesFactory.getMessages().label_items_browser());
                    item.setLayout(new FitLayout());
                    item.setScrollMode(Scroll.NONE);
                    item.setBorders(false);
                    item.setClosable(true);

                    final ContentPanel panel = new ContentPanel();
                    panel.setCollapsible(true);
                    panel.setFrame(false);
                    panel.setHeaderVisible(false);
                    panel.setWidth("100%"); //$NON-NLS-1$
                    panel.setLayout(new FitLayout());

                    ToolBar toolBar = new ToolBar();
                    toolBar.setWidth("100%"); //$NON-NLS-1$

                    service.getUploadTableNames(Itemsbrowser2.getSession().getAppHeader().getDatacluster(), "", //$NON-NLS-1$
                            new AsyncCallback<List<ItemBaseModel>>() {

                                public void onFailure(Throwable caught) {
                                    Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                                }

                                public void onSuccess(List<ItemBaseModel> list) {
                                    tableList.removeAll();
                                    tableList.add(list);
                                }
                            });

                    combo = new ComboBox<ItemBaseModel>();
                    combo.setEmptyText("Select an existing table"); //TODO String to externalize
                    combo.setDisplayField("label"); //$NON-NLS-1$
                    combo.setWidth(150);
                    combo.setStore(tableList);
                    combo.setTypeAhead(true);
                    combo.setTriggerAction(TriggerAction.ALL);

                    combo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

                        @Override
                        public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                            if (se.getSelectedItem() == null)
                                return;
                            currentTableName = (String) se.getSelectedItem().get("key"); //$NON-NLS-1$
                            ItemsToolBar.this.showContenPanel(panel, currentTableName);
                        }
                    });

                    toolBar.add(combo);
                    toolBar.add(new Button("Edit", new SelectionListener<ButtonEvent>() { //$NON-NLS-1$

                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            if (currentTableName == null)
                                return;
                            ItemsToolBar.this.showContenPanel(panel, currentTableName);
                        }
                    }));

                    toolBar.add(new SeparatorToolItem());
                    toolBar.add(new Button("Upload Data File", new SelectionListener<ButtonEvent>() { //TODO String to externalize

                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            if (currentTableName == null)
                                return;
                            panel.removeAll();
                            final FormPanel formPanel = new FormPanel();
                            formPanel.setCollapsible(false);
                            formPanel.setHeading("Upload data"); //$NON-NLS-1$
                            formPanel.setFrame(false);
                            formPanel.setHeaderVisible(true);
                            formPanel.setEncoding(Encoding.MULTIPART);
                            formPanel.setButtonAlign(HorizontalAlignment.CENTER);
                            formPanel.setMethod(Method.POST);
                            formPanel.setWidth("100%"); //$NON-NLS-1$
                            // formPanel.setUrl("secure/upload");

                            FileUploadField file = new FileUploadField();
                            file.setAllowBlank(false);
                            file.setName("uploadedfile"); //$NON-NLS-1$
                            file.setFieldLabel("File"); //$NON-NLS-1$
                            formPanel.add(file);

                            List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();
                            ItemBaseModel excel = new ItemBaseModel();
                            excel.set("label", "Excel"); //$NON-NLS-1$ //$NON-NLS-2$
                            excel.set("key", "Excel"); //$NON-NLS-1$ //$NON-NLS-2$
                            list.add(excel);

                            ItemBaseModel csv = new ItemBaseModel();
                            csv.set("label", "CSV"); //$NON-NLS-1$ //$NON-NLS-2$
                            csv.set("key", "CSV"); //$NON-NLS-1$ //$NON-NLS-2$
                            list.add(csv);
                            ListStore<ItemBaseModel> typeList = new ListStore<ItemBaseModel>();
                            typeList.add(list);

                            ComboBox<ItemBaseModel> fileTypecombo = new ComboBox<ItemBaseModel>();
                            fileTypecombo.setEmptyText("Select..."); //TODO String to externalize
                            fileTypecombo.setFieldLabel("File type"); ///TODO String to externalize
                            fileTypecombo.setDisplayField("label"); //$NON-NLS-1$
                            fileTypecombo.setValueField("key"); //$NON-NLS-1$
                            fileTypecombo.setForceSelection(true);
                            fileTypecombo.setStore(typeList);
                            fileTypecombo.setTriggerAction(TriggerAction.ALL);
                            formPanel.add(fileTypecombo);

                            CheckBox headerLine = new CheckBox();
                            headerLine.setFieldLabel("Headers on First Line"); //TODO String to externalize
                            formPanel.add(headerLine);

                            List<ItemBaseModel> separatorList = new ArrayList<ItemBaseModel>();
                            ItemBaseModel comma = new ItemBaseModel();
                            comma.set("label", "comma"); //$NON-NLS-1$ //$NON-NLS-2$
                            comma.set("key", "comma"); //$NON-NLS-1$ //$NON-NLS-2$
                            separatorList.add(comma);

                            ItemBaseModel semicolon = new ItemBaseModel();
                            semicolon.set("label", "semicolon"); //$NON-NLS-1$ //$NON-NLS-2$
                            semicolon.set("key", "semicolon"); //$NON-NLS-1$ //$NON-NLS-2$
                            separatorList.add(semicolon);

                            ListStore<ItemBaseModel> separatorStoreList = new ListStore<ItemBaseModel>();
                            separatorStoreList.add(separatorList);

                            final ComboBox<ItemBaseModel> separatorCombo = new ComboBox<ItemBaseModel>();
                            separatorCombo.setFieldLabel("Separator"); //$NON-NLS-1$
                            separatorCombo.setDisplayField("label"); //$NON-NLS-1$
                            separatorCombo.setValueField("key"); //$NON-NLS-1$
                            separatorCombo.setForceSelection(true);
                            separatorCombo.setStore(separatorStoreList);
                            separatorCombo.setTriggerAction(TriggerAction.ALL);
                            formPanel.add(separatorCombo);

                            List<ItemBaseModel> textDelimiterList = new ArrayList<ItemBaseModel>();
                            ItemBaseModel doubleDelimiter = new ItemBaseModel();
                            doubleDelimiter.set("label", "\""); //$NON-NLS-1$ //$NON-NLS-2$
                            doubleDelimiter.set("key", "d"); //$NON-NLS-1$ //$NON-NLS-2$
                            textDelimiterList.add(doubleDelimiter);

                            ItemBaseModel singleDelimiter = new ItemBaseModel();
                            singleDelimiter.set("label", "\'"); //$NON-NLS-1$ //$NON-NLS-2$
                            singleDelimiter.set("key", "s"); //$NON-NLS-1$ //$NON-NLS-2$
                            textDelimiterList.add(singleDelimiter);

                            ListStore<ItemBaseModel> textDelimiterStoreList = new ListStore<ItemBaseModel>();
                            textDelimiterStoreList.add(textDelimiterList);

                            final ComboBox<ItemBaseModel> textDelimiterCombo = new ComboBox<ItemBaseModel>();
                            textDelimiterCombo.setFieldLabel("Text Delimiter"); //$NON-NLS-1$
                            textDelimiterCombo.setDisplayField("label"); //$NON-NLS-1$
                            textDelimiterCombo.setValueField("key"); //$NON-NLS-1$
                            textDelimiterCombo.setForceSelection(true);
                            textDelimiterCombo.setStore(textDelimiterStoreList);
                            textDelimiterCombo.setTriggerAction(TriggerAction.ALL);
                            formPanel.add(textDelimiterCombo);

                            List<ItemBaseModel> encodingList = new ArrayList<ItemBaseModel>();
                            ItemBaseModel utf8 = new ItemBaseModel();
                            utf8.set("label", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
                            utf8.set("key", "utf8"); //$NON-NLS-1$ //$NON-NLS-2$
                            encodingList.add(utf8);

                            ItemBaseModel iso88591 = new ItemBaseModel();
                            iso88591.set("label", "ISO-8859-1"); //$NON-NLS-1$ //$NON-NLS-2$
                            iso88591.set("key", "iso88591"); //$NON-NLS-1$ //$NON-NLS-2$
                            encodingList.add(iso88591);

                            ItemBaseModel iso885915 = new ItemBaseModel();
                            iso885915.set("label", "iso885915"); //$NON-NLS-1$ //$NON-NLS-2$
                            iso885915.set("key", "iso885915"); //$NON-NLS-1$ //$NON-NLS-2$
                            encodingList.add(iso885915);

                            ItemBaseModel cp1252 = new ItemBaseModel();
                            cp1252.set("label", "cp1252"); //$NON-NLS-1$ //$NON-NLS-2$
                            cp1252.set("key", "cp1252"); //$NON-NLS-1$ //$NON-NLS-2$
                            encodingList.add(cp1252);

                            ListStore<ItemBaseModel> encodingStoreList = new ListStore<ItemBaseModel>();
                            encodingStoreList.add(encodingList);

                            final ComboBox<ItemBaseModel> encodingCombo = new ComboBox<ItemBaseModel>();
                            encodingCombo.setFieldLabel("Encoding"); //TODO String to externalize
                            encodingCombo.setDisplayField("label"); //$NON-NLS-1$
                            encodingCombo.setValueField("key"); //$NON-NLS-1$
                            encodingCombo.setForceSelection(true);
                            encodingCombo.setStore(encodingStoreList);
                            encodingCombo.setTriggerAction(TriggerAction.ALL);
                            formPanel.add(encodingCombo);

                            fileTypecombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

                                @Override
                                public void selectionChanged(SelectionChangedEvent<ItemBaseModel> event) {
                                    String type = (String) event.getSelectedItem().get("key"); //$NON-NLS-1$
                                    if (type.equalsIgnoreCase("CSV")) { //$NON-NLS-1$
                                        separatorCombo.enable();
                                        textDelimiterCombo.enable();
                                        encodingCombo.enable();
                                    } else {
                                        separatorCombo.disable();
                                        textDelimiterCombo.disable();
                                        encodingCombo.disable();
                                    }
                                }
                            });

                            Button submit = new Button("Submit", new SelectionListener<ButtonEvent>() { //TODO String to externalize

                                @Override
                                public void componentSelected(ButtonEvent ce) {
                                    formPanel.submit();
                                    panel.removeAll();
                                }
                            });

                            formPanel.add(submit);

                            separatorCombo.disable();
                            textDelimiterCombo.disable();
                            encodingCombo.disable();

                            formPanel.setLabelWidth(200);
                            panel.add(formPanel);
                            panel.layout();
                        }
                    }));
                    toolBar.add(new SeparatorToolItem());
                    toolBar.add(new Button("Delete", new SelectionListener<ButtonEvent>() { //TODO String to externalize

                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            if (currentTableName == null)
                                return;
                            MessageBox.confirm("Delete Table", "Are you sure you want to delete this Items-Browser table?", //TODO Strings to externalize
                                    new Listener<MessageBoxEvent>() {

                                        public void handleEvent(MessageBoxEvent event) {
                                            
                                            if (event.getButtonClicked().getText().equalsIgnoreCase("Yes")) { ///TODO Wrong!!!! 
                                                String model = Itemsbrowser2.getSession().getAppHeader().getDatacluster();
                                                service.deleteItemsBrowserTable(model, currentTableName,
                                                        new AsyncCallback<List<ItemBaseModel>>() {

                                                            public void onSuccess(List<ItemBaseModel> list) {
                                                                tableList.removeAll();
                                                                tableList.add(list);
                                                                combo.setStore(tableList);
                                                                combo.reset();
                                                                panel.removeAll();
                                                                panel.layout();
                                                            }

                                                            public void onFailure(Throwable caught) {
                                                                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }));
                    toolBar.add(new SeparatorToolItem());
                    toolBar.add(new Button("New table", new SelectionListener<ButtonEvent>() { //TODO String to externalize

                        @Override
                        public void componentSelected(ButtonEvent ce) {

                            panel.removeAll();
                            fieldCount = 1;
                            ContentPanel cp = new ContentPanel();
                            cp.setCollapsible(true);
                            cp.setFrame(false);
                            cp.setHeaderVisible(false);
                            cp.setWidth("100%"); //$NON-NLS-1$
                            cp.setLayout(new FitLayout());
                            cp.setBodyBorder(false);
                            cp.setBorders(false);
                            cp.setScrollMode(Scroll.AUTO);

                            final FormData formData = new FormData("100%"); //$NON-NLS-1$
                            final FormPanel addPanel = new FormPanel();
                            addPanel.setCollapsible(false);
                            addPanel.setHeading("New table"); //TODO String to externalize. What's that ???
                            addPanel.setFrame(false);
                            addPanel.setHeaderVisible(true);
                            addPanel.setEncoding(Encoding.MULTIPART);
                            addPanel.setButtonAlign(HorizontalAlignment.CENTER);
                            addPanel.setMethod(Method.POST);
                            addPanel.setWidth("100%"); //$NON-NLS-1$
                            addPanel.setLabelWidth(200);

                            final LayoutContainer main = new LayoutContainer();
                            main.setLayout(new ColumnLayout());

                            TextField<String> tableName = new TextField<String>();
                            tableName.setFieldLabel("Table name"); //TODO String to externalize
                            addPanel.add(tableName);

                            final LayoutContainer left = new LayoutContainer();
                            left.setStyleAttribute("paddingRight", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
                            FormLayout layout = new FormLayout();
                            left.setLayout(layout);

                            TextField<String> field1 = new TextField<String>();
                            field1.setFieldLabel("Field " + fieldCount); //$NON-NLS-1$
                            left.add(field1, formData);

                            final LayoutContainer right = new LayoutContainer();
                            right.setStyleAttribute("paddingLeft", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
                            layout = new FormLayout();
                            right.setLayout(layout);

                            CheckBox keycb = new CheckBox();
                            keycb.setFieldLabel("Key"); //$NON-NLS-1$
                            right.add(keycb, formData);

                            main.add(left, new com.extjs.gxt.ui.client.widget.layout.ColumnData(.347));
                            main.add(right, new com.extjs.gxt.ui.client.widget.layout.ColumnData(.347));
                            addPanel.add(main, new FormData("100%")); //$NON-NLS-1$

                            cp.add(addPanel);
                            ToolBar tb = new ToolBar();
                            tb.setWidth("100%"); //$NON-NLS-1$
                            tb.add(new Button("Save", new SelectionListener<ButtonEvent>() { //TODO String to externalize

                                @Override
                                public void componentSelected(ButtonEvent ce) {

                                }
                            }));
                            tb.add(new SeparatorToolItem());
                            tb.add(new Button("Add a Field", new SelectionListener<ButtonEvent>() { //$NON-NLS-1$

                                @Override
                                public void componentSelected(ButtonEvent ce) {
                                    fieldCount = fieldCount + 1;
                                    TextField<String> f = new TextField<String>();
                                    f.setFieldLabel("Field " + fieldCount); //$NON-NLS-1$
                                    left.add(f, formData);

                                    CheckBox kcb = new CheckBox();
                                    kcb.setFieldLabel("Key"); //$NON-NLS-1$
                                    right.add(kcb, formData);

                                    main.layout();
                                    addPanel.layout();
                                }
                            }));
                            cp.setBottomComponent(tb);
                            panel.add(cp);
                            panel.layout();
                        }
                    }));

                    panel.setTopComponent(toolBar);
                    item.add(panel);
                    tabFrame.add(item);
                }

                tabFrame.setSelection(item);
            }
        });

        subFile.add(uploadMenu);

        uploadBtn.setMenu(subFile);
        uploadBtn.setEnabled(false);
        add(uploadBtn);

        add(new FillToolItem());

        // add entity combo
        RpcProxy<List<ItemBaseModel>> Entityproxy = new RpcProxy<List<ItemBaseModel>>() {

            @Override
            public void load(Object loadConfig, AsyncCallback<List<ItemBaseModel>> callback) {
                service.getViewsList(Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader()), callback);
            }
        };
        ListLoader<ListLoadResult<ItemBaseModel>> Entityloader = new BaseListLoader<ListLoadResult<ItemBaseModel>>(Entityproxy);

        HorizontalPanel entityPanel = new HorizontalPanel();
        final ListStore<ItemBaseModel> list = new ListStore<ItemBaseModel>(Entityloader);

        entityCombo.setAutoWidth(true);
        entityCombo.setEmptyText(MessagesFactory.getMessages().empty_entity());
        entityCombo.setLoadingText(MessagesFactory.getMessages().loading());
        entityCombo.setStore(list);
        entityCombo.setDisplayField("name");//$NON-NLS-1$
        entityCombo.setValueField("value");//$NON-NLS-1$
        entityCombo.setForceSelection(true);
        entityCombo.setTriggerAction(TriggerAction.ALL);
        entityCombo.setId("EntityComboBox");//$NON-NLS-1$
        entityCombo.setStyleAttribute("padding-right", "17px"); //$NON-NLS-1$ //$NON-NLS-2$

        entityCombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                String viewPk = se.getSelectedItem().get("value").toString();//$NON-NLS-1$
                Dispatcher.forwardEvent(ItemsEvents.GetView, viewPk);
            }

        });
        entityPanel.add(entityCombo);
        add(entityPanel);
        simplePanel = new SimpleCriterionPanel(null, null, searchBut);
        add(simplePanel);

        // add simple search button
        searchBut.setEnabled(false);
        searchBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (simplePanel.getCriteria() != null) {
                    isSimple = true;
                    String viewPk = entityCombo.getValue().get("value");//$NON-NLS-1$
                    Dispatcher.forwardEvent(ItemsEvents.SearchView, viewPk);
                    resizeAfterSearch();
                } else {
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages()
                            .advsearch_lessinfo(), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            simplePanel.focusField();
                        }
                    });
                }
            }

        });
        add(searchBut);

        add(new SeparatorToolItem());

        // add advanced search button
        advancedBut.setEnabled(false);
        advancedBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                // show advanced Search panel
                advancedPanelVisible = !advancedPanelVisible;
                advancedPanel.setVisible(advancedPanelVisible);
                advancedPanel.getButtonBar().getItemByItemId("updateBookmarkBtn").setVisible(false); //$NON-NLS-1$

                if (((ItemsListPanel) instance.getParent()).gridContainer != null)
                    ((ItemsListPanel) instance.getParent()).gridContainer.setHeight(instance.getParent().getOffsetHeight()
                            - instance.getOffsetHeight() - advancedPanel.getOffsetHeight());
                if (isSimple)
                    advancedPanel.setCriteria("((" + simplePanel.getCriteria().toString() + "))"); //$NON-NLS-1$ //$NON-NLS-2$
            }

        });
        add(advancedBut);

        add(new SeparatorToolItem());

        // add bookmark management button
        managebookBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Display()));
        managebookBtn.setTitle(MessagesFactory.getMessages().bookmarkmanagement_heading());
        managebookBtn.setEnabled(false);
        managebookBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                final Window winBookmark = new Window();
                winBookmark.setHeading(MessagesFactory.getMessages().bookmarkmanagement_heading());
                winBookmark.setAutoHeight(true);
                // winBookmark.setAutoWidth(true);
                winBookmark.setWidth(413);
                winBookmark.setModal(true);
                FormPanel content = new FormPanel();
                FormData formData = new FormData("-10");//$NON-NLS-1$
                content.setFrame(false);
                content.setLayout(new FitLayout());
                content.setBodyBorder(false);
                content.setHeaderVisible(false);
                content.setSize(400, 350);
                winBookmark.add(content);

                // display bookmark grid
                RpcProxy<PagingLoadResult<ItemBaseModel>> proxyBookmark = new RpcProxy<PagingLoadResult<ItemBaseModel>>() {

                    @Override
                    public void load(Object loadConfig, AsyncCallback<PagingLoadResult<ItemBaseModel>> callback) {
                        service.querySearchTemplates(entityCombo.getValue().get("value").toString(), true, //$NON-NLS-1$
                                (PagingLoadConfig) loadConfig, callback);
                    }
                };

                // loader
                final PagingLoader<PagingLoadResult<ItemBaseModel>> loaderBookmark = new BasePagingLoader<PagingLoadResult<ItemBaseModel>>(
                        proxyBookmark);

                ListStore<ItemBaseModel> store = new ListStore<ItemBaseModel>(loaderBookmark);
                store.setDefaultSort("name", SortDir.ASC); //$NON-NLS-1$

                final PagingToolBar pagetoolBar = new PagingToolBar(PAGE_SIZE);
                pagetoolBar.bind(loaderBookmark);

                List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
                columns.add(new ColumnConfig("name", MessagesFactory.getMessages().bookmark_heading(), 200)); //$NON-NLS-1$
                ColumnConfig colEdit = new ColumnConfig("value", MessagesFactory.getMessages().bookmark_edit(), 100); //$NON-NLS-1$
                colEdit.setRenderer(new GridCellRenderer<ItemBaseModel>() {

                    public Object render(final ItemBaseModel model, String property, ColumnData config, int rowIndex,
                            int colIndex, ListStore<ItemBaseModel> store, Grid<ItemBaseModel> grid) {
                        Image image = new Image();
                        image.setResource(Icons.INSTANCE.Edit());
                        if (!ifManage(model))
                            image.addStyleName("x-item-disabled");//$NON-NLS-1$
                        else
                            image.addClickListener(new ClickListener() {

                                public void onClick(Widget arg0) {
                                    // edit the bookmark
                                    if (advancedPanel == null) {
                                        advancedPanel = new AdvancedSearchPanel(simplePanel.getView(), null);
                                    }
                                    service.getCriteriaByBookmark(model.get("value").toString(), new AsyncCallback<String>() { //$NON-NLS-1$

                                                public void onFailure(Throwable caught) {
                                                    Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                                                }

                                                public void onSuccess(String arg0) {
                                                    // set criteria
                                                    if (arg0 != null)
                                                        advancedPanel.setCriteria(arg0);
                                                    else
                                                        advancedPanel.cleanCriteria();
                                                    advancedPanelVisible = true;
                                                    advancedPanel.setVisible(advancedPanelVisible);
                                                    advancedPanel.getButtonBar().getItemByItemId("updateBookmarkBtn")
                                                            .setVisible(true);
                                                    bookmarkName = model.get("value").toString();
                                                    bookmarkShared = Boolean.parseBoolean(model.get("shared").toString());
                                                    if (((ItemsListPanel) instance.getParent()).gridContainer != null)
                                                        ((ItemsListPanel) instance.getParent()).gridContainer.setHeight(instance
                                                                .getParent().getOffsetHeight()
                                                                - instance.getOffsetHeight()
                                                                - advancedPanel.getOffsetHeight());
                                                    winBookmark.close();
                                                    // showAdvancedWin(instance, arg0);
                                                    // winBookmark.close();
                                                }

                                            });
                                }

                            });
                        return image;
                    }

                });
                columns.add(colEdit);

                ColumnConfig colDel = new ColumnConfig("value", MessagesFactory.getMessages().bookmark_del(), 100); //$NON-NLS-1$
                colDel.setRenderer(new GridCellRenderer<ItemBaseModel>() {

                    public Object render(final ItemBaseModel model, String property, ColumnData config, int rowIndex,
                            int colIndex, ListStore<ItemBaseModel> store, Grid<ItemBaseModel> grid) {
                        Image image = new Image();
                        image.setResource(Icons.INSTANCE.remove());
                        if (!ifManage(model))
                            image.addStyleName("x-item-disabled"); //$NON-NLS-1$
                        else
                            image.addClickListener(new ClickListener() {

                                public void onClick(Widget arg0) {
                                    MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory
                                            .getMessages().bookmark_DelMsg(), new Listener<MessageBoxEvent>() {

                                        public void handleEvent(MessageBoxEvent be) {
                                            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                                // delete the bookmark
                                                service.deleteSearchTemplate(model.get("value").toString(), //$NON-NLS-1$
                                                        new AsyncCallback<String>() {

                                                            public void onFailure(Throwable caught) {
                                                                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                                                            }

                                                            public void onSuccess(String arg0) {
                                                                loaderBookmark.load();
                                                            }

                                                        });
                                            }
                                        }
                                    });
                                }

                            });
                        return image;
                    }

                });

                columns.add(colDel);

                ColumnConfig colSearch = new ColumnConfig("value", MessagesFactory.getMessages().bookmark_search(), 100); //$NON-NLS-1$
                colSearch.setRenderer(new GridCellRenderer<ItemBaseModel>() {

                    public Object render(final ItemBaseModel model, String property, ColumnData config, int rowIndex,
                            int colIndex, ListStore<ItemBaseModel> store, Grid<ItemBaseModel> grid) {
                        Image image = new Image();
                        image.setResource(Icons.INSTANCE.dosearch());
                        image.addClickListener(new ClickListener() {

                            public void onClick(Widget arg0) {
                                doSearch(model, winBookmark);
                            }

                        });
                        return image;
                    }

                });
                columns.add(colSearch);

                ColumnModel cm = new ColumnModel(columns);

                final Grid<ItemBaseModel> bookmarkgrid = new Grid<ItemBaseModel>(store, cm);
                if (cm.getColumnCount() > 0) {
                    bookmarkgrid.setAutoExpandColumn(cm.getColumn(0).getHeader());
                }
                bookmarkgrid.getView().setForceFit(true);
                bookmarkgrid.addListener(Events.Attach, new Listener<GridEvent<ItemBaseModel>>() {

                    public void handleEvent(GridEvent<ItemBaseModel> be) {
                        PagingLoadConfig config = new BasePagingLoadConfig();
                        config.setOffset(0);
                        config.setLimit(PAGE_SIZE);
                        loaderBookmark.load(config);
                    }
                });

                bookmarkgrid.addListener(Events.OnDoubleClick, new Listener<GridEvent<ItemBaseModel>>() {

                    public void handleEvent(final GridEvent<ItemBaseModel> be) {
                        doSearch(be.getModel(), winBookmark);
                    }
                });

                bookmarkgrid.setLoadMask(true);
                bookmarkgrid.setBorders(false);

                content.setBottomComponent(pagetoolBar);
                content.add(bookmarkgrid, formData);

                winBookmark.show();
            }

        });
        add(managebookBtn);

        // add bookmark save button
        bookmarkBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        bookmarkBtn.setTitle(MessagesFactory.getMessages().advsearch_bookmark());
        bookmarkBtn.setEnabled(false);
        bookmarkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                showBookmarkSavedWin(true);
            }

        });
        add(bookmarkBtn);

        initAdvancedPanel();
    }

    private void updateUserCriteriasList() {
        service.getUserCriterias(entityCombo.getValue().get("value").toString(), //$NON-NLS-1$
                new AsyncCallback<List<ItemBaseModel>>() {

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                    }

                    public void onSuccess(List<ItemBaseModel> list) {
                        userCriteriasList = list;
                    }

                });
    }

    private boolean ifManage(ItemBaseModel model) {
        // only the shared bookmark could be managed
        Iterator<ItemBaseModel> i = userCriteriasList.iterator();
        while (i.hasNext()) {
            if ((i.next()).get("value").equals( //$NON-NLS-1$
                    model.get("value").toString())) { //$NON-NLS-1$    
                return true;
            }
        }

        return false;
    }

    private void doSearch(final ItemBaseModel model, final Window winBookmark) {
        service.getCriteriaByBookmark(model.get("value").toString(), //$NON-NLS-1$
                new AsyncCallback<String>() {

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                    }

                    public void onSuccess(String arg0) {
                        isSimple = false;
                        if (advancedPanel == null) {
                            advancedPanel = new AdvancedSearchPanel(simplePanel.getView(), null);
                        }
                        advancedPanel.setCriteria(arg0);
                        String viewPk = entityCombo.getValue().get("value"); //$NON-NLS-1$
                        Dispatcher.forwardEvent(ItemsEvents.SearchView, viewPk);
                        winBookmark.close();
                    }

                });

    }

    private void showBookmarkSavedWin(final boolean ifSimple) {
        final Window winBookmark = new Window();
        winBookmark.setHeading(MessagesFactory.getMessages().bookmark_heading());
        // winBookmark.setAutoHeight(true);
        // winBookmark.setAutoWidth(true);
        winBookmark.setWidth(355);
        winBookmark.setHeight(191);
        FormPanel content = new FormPanel();
        content.setFrame(false);
        content.setBodyBorder(false);
        content.setHeaderVisible(false);
        content.setButtonAlign(HorizontalAlignment.CENTER);
        content.setLabelWidth(100);
        content.setFieldWidth(200);
        final CheckBox cb = new CheckBox();
        cb.setFieldLabel(MessagesFactory.getMessages().bookmark_shared());
        content.add(cb);

        final TextField<String> bookmarkfield = new TextField<String>();
        bookmarkfield.setFieldLabel(MessagesFactory.getMessages().bookmark_name());
        Validator validator = new Validator() {

            public String validate(Field<?> field, String value) {
                if (field == bookmarkfield) {
                    if (bookmarkfield.getValue() == null || bookmarkfield.getValue().trim().equals("")) //$NON-NLS-1$
                        return MessagesFactory.getMessages().required_field();
                }

                return null;
            }
        };
        bookmarkfield.setValidator(validator);
        content.add(bookmarkfield);

        Button btn = new Button(MessagesFactory.getMessages().ok_btn());
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                service.isExistCriteria(entityCombo.getValue().get("value").toString(), bookmarkfield.getValue(), //$NON-NLS-1$
                        new AsyncCallback<Boolean>() {

                            public void onFailure(Throwable caught) {
                                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                                MessageBox.alert(MessagesFactory.getMessages().warning_title(), MessagesFactory.getMessages()
                                        .bookmark_existMsg(), null);
                            }

                            public void onSuccess(Boolean arg0) {
                                if (!arg0) {
                                    String curCriteria = null;
                                    if (ifSimple)
                                        curCriteria = simplePanel.getCriteria().toString();
                                    else
                                        curCriteria = advancedPanel.getCriteria();
                                    saveBookmark(bookmarkfield.getValue().toString(), cb.getValue(), curCriteria, winBookmark);
                                } else {
                                    MessageBox.alert(MessagesFactory.getMessages().warning_title(), MessagesFactory.getMessages()
                                            .bookmark_existMsg(), null);
                                }
                            }

                        });
            }
        });
        winBookmark.addButton(btn);

        winBookmark.add(content);
        winBookmark.show();
    }

    private void saveBookmark(String name, boolean shared, String curCriteria, final Window winBookmark) {

        service.saveCriteria(entityCombo.getValue().get("value").toString(), name, shared, curCriteria, //$NON-NLS-1$
                new AsyncCallback<String>() {

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                        MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages()
                                .bookmark_saveFailed(), null);
                    }

                    public void onSuccess(String arg0) {
                        if (arg0.equals("OK")) { //$NON-NLS-1$
                            MessageBox.info(MessagesFactory.getMessages().info_title(), MessagesFactory.getMessages()
                                    .bookmark_saveSuccess(), null);
                            updateUserCriteriasList();
                            if (winBookmark != null)
                                winBookmark.close();
                        } else
                            MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages()
                                    .bookmark_saveFailed(), null);
                    }

                });
    }

    public FormPanel getAdvancedPanel() {
        return advancedPanel;
    }

    private void resizeAfterSearch() {
        advancedPanelVisible = false;
        advancedPanel.setVisible(advancedPanelVisible);
        advancedBut.toggle(advancedPanelVisible);
        // resize result grid
        if (((ItemsListPanel) instance.getParent()).gridContainer != null)
            ((ItemsListPanel) instance.getParent()).gridContainer.setHeight(instance.getParent().getOffsetHeight()
                    - instance.getOffsetHeight() - advancedPanel.getOffsetHeight());
    }

    private void initAdvancedPanel() {
        if (advancedPanel == null) {
            Button searchBtn = new Button(MessagesFactory.getMessages().search_btn());
            advancedPanel = new AdvancedSearchPanel(simplePanel.getView(), searchBtn);
            advancedPanel.setItemId("advancedPanel"); //$NON-NLS-1$
            advancedPanel.setButtonAlign(HorizontalAlignment.CENTER);

            searchBtn.setItemId("searchBtn"); //$NON-NLS-1$
            searchBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    if (advancedPanel.getCriteria() == null || advancedPanel.getCriteria().equals("")) //$NON-NLS-1$
                        MessageBox.alert(MessagesFactory.getMessages().warning_title(), MessagesFactory.getMessages()
                                .search_expression_notempty(), null);
                    else {
                        isSimple = false;
                        String viewPk = entityCombo.getValue().get("value"); //$NON-NLS-1$
                        Dispatcher.forwardEvent(ItemsEvents.SearchView, viewPk);
                        resizeAfterSearch();
                    }
                }

            });
            advancedPanel.addButton(searchBtn);

            Button advancedBookmarkBtn = new Button(MessagesFactory.getMessages().advsearch_bookmark());
            advancedBookmarkBtn.setItemId("advancedBookmarkBtn"); //$NON-NLS-1$
            advancedBookmarkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    showBookmarkSavedWin(false);
                }

            });
            advancedPanel.addButton(advancedBookmarkBtn);

            Button updateBookmarkBtn = new Button(MessagesFactory.getMessages().bookmark_update());
            updateBookmarkBtn.setItemId("updateBookmarkBtn"); //$NON-NLS-1$
            updateBookmarkBtn.setVisible(false);
            updateBookmarkBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    saveBookmark(bookmarkName, bookmarkShared, advancedPanel.getCriteria(), null);
                }

            });
            advancedPanel.addButton(updateBookmarkBtn);

            Button cancelBtn = new Button(MessagesFactory.getMessages().button_reset());
            cancelBtn.setItemId("cancelBtn"); //$NON-NLS-1$
            cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    advancedPanel.cleanCriteria();

                    if (((ItemsListPanel) instance.getParent()).gridContainer != null)
                        ((ItemsListPanel) instance.getParent()).gridContainer.setHeight(instance.getParent().getOffsetHeight()
                                - instance.getOffsetHeight() - advancedPanel.getOffsetHeight());
                }

            });
            advancedPanel.addButton(cancelBtn);
            advancedPanel.setVisible(false);
        }
    }

    public void showContenPanel(final ContentPanel panel, String currentTableName) {

        String model = Itemsbrowser2.getSession().getAppHeader().getDatacluster();
        service.getUploadTableDescription(model, currentTableName, new AsyncCallback<Map<String, List<String>>>() {

            public void onSuccess(Map<String, List<String>> description) {

                panel.removeAll();

                ContentPanel content = new ContentPanel();
                content.setCollapsible(false);
                content.setHeading("Update Table"); //TODO String to externalize
                content.setFrame(false);
                content.setHeaderVisible(true);
                content.setWidth("100%"); //$NON-NLS-1$

                List<String> fieldList = description.get("fields"); //$NON-NLS-1$
                List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
                for (String str : fieldList) {
                    ColumnConfig column = new ColumnConfig();
                    column.setId(str);
                    column.setHeader(str);
                    column.setWidth(200);
                    configs.add(column);
                }
                ColumnModel cm = new ColumnModel(configs);
                final ListStore<ItemBean> store = new ListStore<ItemBean>();
                final EditorGrid<ItemBean> grid = new EditorGrid<ItemBean>(store, cm);
                grid.setAutoExpandColumn(fieldList.get(0));
                grid.setBorders(true);

                ToolBar buttomBar = new ToolBar();
                buttomBar.setWidth("100%"); //$NON-NLS-1$
                buttomBar.add(new Button("Add row", new SelectionListener<ButtonEvent>() { //TODO String to externalize

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        ItemBean bean = new ItemBean();
                        grid.stopEditing();
                        store.insert(bean, 0);
                        grid.startEditing(store.indexOf(bean), 0);
                    }
                }));

                buttomBar.add(new SeparatorToolItem());
                buttomBar.add(new Button("Save")); //TODO String to externalize
                buttomBar.add(new SeparatorToolItem());
                buttomBar.add(new Button("Export")); //TODO String to externalize

                content.add(grid);
                content.setBottomComponent(buttomBar);
                panel.add(content);
                panel.layout();
            }

            public void onFailure(Throwable caught) {
                Dispatcher.forwardEvent(ItemsEvents.Error, caught);
            }
        });
    }
}

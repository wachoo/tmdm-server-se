package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.ItemBean;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.util.Locale;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class RecordToolBar extends ToolBar {

    Button saveBtn = new Button(MessagesFactory.getMessages().save_btn());

    Button saveCloseBtn = new Button(MessagesFactory.getMessages().savaClose_btn());

    private RecordToolBar instance = this;

    ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    public RecordToolBar() {
        initToolBar();
    }

    public void updateToolBar() {
        if (instance.getItemByItemId("delete_Record") == null) {//$NON-NLS-1$
            add(new SeparatorToolItem());

            Button menu = new Button(MessagesFactory.getMessages().delete_btn());
            menu.setId("delete_Record");//$NON-NLS-1$
            menu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
            Menu sub = new Menu();
            MenuItem delMenu = new MenuItem(MessagesFactory.getMessages().delete_btn());
            delMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
            delMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

                @Override
                public void componentSelected(MenuEvent ce) {
                    MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                            .delete_confirm(), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                                service.deleteItemBean(parent.getItemBean(), GetService.getLanguage(), new SessionAwareAsyncCallback<ItemResult>() {

                                    @Override
                                    protected void doOnFailure(Throwable arg0) {

                                    }

                                    public void onSuccess(ItemResult arg0) {
                                        if (arg0.getStatus() == ItemResult.SUCCESS) {
                                            parent.refreshGrid();
                                            MessageBox.alert(MessagesFactory.getMessages().info_title(), arg0.getDescription(),
                                                    null);
                                        } else if (arg0.getStatus() == ItemResult.FAILURE) {
                                            MessageBox.alert(MessagesFactory.getMessages().error_title(), arg0.getDescription(),
                                                    null);
                                        }
                                    }

                                });
                            }
                        }
                    });

                }
            });
            sub.add(delMenu);
            MenuItem trashMenu = new MenuItem(MessagesFactory.getMessages().trash_btn());
            trashMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
            trashMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

                @Override
                public void componentSelected(MenuEvent ce) {
                    final MessageBox box = MessageBox.prompt(MessagesFactory.getMessages().path(), MessagesFactory.getMessages()
                            .path_desc(), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
                                final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                                // TODO xpath
                                service.logicalDeleteItem(parent.getItemBean(), be.getValue(), new SessionAwareAsyncCallback<ItemResult>() {

                                    @Override
                                    protected void doOnFailure(Throwable arg0) {

                                    }

                                    public void onSuccess(ItemResult arg0) {
                                        if (arg0.getStatus() == ItemResult.SUCCESS) {
                                            parent.refreshGrid();
                                            MessageBox.alert(MessagesFactory.getMessages().info_title(), arg0.getDescription(),
                                                    null);
                                        } else if (arg0.getStatus() == ItemResult.FAILURE) {
                                            MessageBox.alert(MessagesFactory.getMessages().error_title(), arg0.getDescription(),
                                                    null);
                                        }
                                    }

                                });
                            }

                        }

                    });
                    box.getTextBox().setValue("/");//$NON-NLS-1$
                }
            });
            sub.add(trashMenu);
            menu.setMenu(sub);
            // menu.setEnabled(false);
            add(menu);

            add(new SeparatorToolItem());

            Button duplicateBtn = new Button(MessagesFactory.getMessages().duplicate_btn());
            duplicateBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Duplicate()));
            duplicateBtn.setTitle(MessagesFactory.getMessages().duplicate_tip());
            add(duplicateBtn);
            duplicateBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                    ItemBean item = parent.getItemBean();
                    ItemBean dupItem = new ItemBean(item.getConcept(), "", null);//$NON-NLS-1$
                    Map<String, Object> properties = new LinkedHashMap<String, Object>();
                    EntityModel entityModel = (EntityModel) Itemsbrowser2.getSession().getCurrentEntityModel();
                    boolean ifKey = false;
                    for (String key : item.getProperties().keySet()) {
                        ifKey = false;
                        for (String subkey : entityModel.getKeys()) {
                            if (subkey.equals(key)) {
                                properties.put(key, "");//$NON-NLS-1$
                                ifKey = true;
                                break;
                            }
                        }
                        if (!ifKey)
                            properties.put(key, item.getProperties().get(key));
                    }
                    dupItem.setProperties(properties);
                    AppEvent evt = new AppEvent(ItemsEvents.ViewItemForm, dupItem);
                    evt.setData(ItemsView.ITEMS_FORM_TARGET, ItemsView.TARGET_IN_NEW_TAB);
                    Dispatcher.forwardEvent(evt);
                }
            });

            add(new SeparatorToolItem());

            Button journalBtn = new Button(MessagesFactory.getMessages().journal_btn());
            journalBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Journal()));
            journalBtn.setTitle(MessagesFactory.getMessages().jouranl_tip());
            add(journalBtn);
            journalBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                    ItemBean item = parent.getItemBean();
                    InvokeJournal(item.getIds(), item.getConcept());
                }
            });

            add(new SeparatorToolItem());

            Button refreshBtn = new Button();
            refreshBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Refresh()));
            refreshBtn.setTitle(MessagesFactory.getMessages().refresh());
            add(refreshBtn);
            refreshBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                    String viewType = null;
                    ItemBean item = parent.getItemBean();
                    AppEvent evt = new AppEvent(ItemsEvents.ViewItemForm, item);
                    if (parent != null && parent.getParent() instanceof TabItem) {
                        viewType = ItemsView.TARGET_IN_NEW_TAB;
                    } else {
                        viewType = ItemsView.TARGET_IN_SEARCH_TAB;
                    }
                    evt.setData(ItemsView.ITEMS_FORM_TARGET, viewType);
                    Dispatcher.forwardEvent(evt);
                }
            });
        }
    }

    private void initToolBar() {
        saveBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        saveBtn.setTitle(MessagesFactory.getMessages().save_tip());
        add(saveBtn);
        saveBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                if (!parent.getNewItemBean().getIds().equals("")) {//$NON-NLS-1$
                    MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                            .save_confirm(), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                saveItemBean(parent);
                            }
                        }
                    });
                } else
                    saveItemBean(parent);

            }
        });

        add(new SeparatorToolItem());

        saveCloseBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveClose()));
        saveCloseBtn.setTitle(MessagesFactory.getMessages().saveClose_tip());
        add(saveCloseBtn);
        saveCloseBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                if (!parent.getNewItemBean().getIds().equals("")) {//$NON-NLS-1$
                    MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                            .save_confirm(), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                saveItemBean(parent);
                                if (parent.getParent() instanceof TabItem)
                                    // close the current tab
                                    ((TabItem) parent.getParent()).close();
                                else
                                    ((Window) parent.getParent()).close();
                            }
                        }
                    });
                } else {
                    saveItemBean(parent);
                    if (parent.getParent() instanceof TabItem)
                        // close the current tab
                        ((TabItem) parent.getParent()).close();
                    else
                        ((Window) parent.getParent()).close();
                }
            }
        });
    }

    private void saveItemBean(final ItemsFormPanel parent) {
        ItemBean itemBean = parent.getNewItemBean();
        service.saveItemBean(itemBean, Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader()),
                new SessionAwareAsyncCallback<ItemResult>() {

            @Override
            protected void doOnFailure(Throwable arg0) {

            }

            public void onSuccess(ItemResult arg0) {
                if (arg0.getStatus() == ItemResult.SUCCESS) {
                    parent.commitItemBean();
                    MessageBox.alert(MessagesFactory.getMessages().info_title(), Locale.getExceptionMessageByLanguage(GetService.getLanguage(), arg0.getDescription()), null);
                } else if (arg0.getStatus() == ItemResult.FAILURE) {
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), Locale.getExceptionMessageByLanguage(GetService.getLanguage(), arg0.getDescription()), null);
                }
            }

        });
    }

    private native void InvokeJournal(String ids, String concept)/*-{
        if(ids.indexOf("@")>0)//$NON-NLS-1$
        ids=ids.replaceAll("@",".");//$NON-NLS-1$       
        $wnd.amalto.updatereport.UpdateReport.browseUpdateReportWithSearchCriteria(concept, ids, true);
    }-*/;

}

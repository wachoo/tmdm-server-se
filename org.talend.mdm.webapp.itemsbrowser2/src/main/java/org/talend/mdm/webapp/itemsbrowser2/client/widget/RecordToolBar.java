package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
        if (instance.getItemByItemId("delete_Record") == null) {
            add(new SeparatorToolItem());

            Button menu = new Button(MessagesFactory.getMessages().delete_btn());
            menu.setId("delete_Record");
            menu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
            Menu sub = new Menu();
            MenuItem delMenu = new MenuItem(MessagesFactory.getMessages().delete_btn());
            delMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
            delMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

                public void componentSelected(MenuEvent ce) {
                    final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                    service.deleteItemBean(parent.getItemBean(), new AsyncCallback<ItemResult>() {

                        public void onFailure(Throwable arg0) {

                        }

                        public void onSuccess(ItemResult arg0) {
                            if (arg0.getStatus() == ItemResult.SUCCESS) {
                                parent.refreshGrid();
                                MessageBox.alert(MessagesFactory.getMessages().info_title(), arg0.getDescription(), null);
                            } else if (arg0.getStatus() == ItemResult.FAILURE) {
                                MessageBox.alert(MessagesFactory.getMessages().error_title(), arg0.getDescription(), null);
                            }
                        }

                    });
                }
            });
            sub.add(delMenu);
            MenuItem trashMenu = new MenuItem(MessagesFactory.getMessages().trash_btn());
            trashMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
            trashMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

                public void componentSelected(MenuEvent ce) {
                    final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                    // TODO xpath
                    service.logicalDeleteItem(parent.getItemBean(), "/", new AsyncCallback<ItemResult>() {

                        public void onFailure(Throwable arg0) {

                        }

                        public void onSuccess(ItemResult arg0) {
                            if (arg0.getStatus() == ItemResult.SUCCESS) {
                                parent.refreshGrid();
                                MessageBox.alert(MessagesFactory.getMessages().info_title(), arg0.getDescription(), null);
                            } else if (arg0.getStatus() == ItemResult.FAILURE) {
                                MessageBox.alert(MessagesFactory.getMessages().error_title(), arg0.getDescription(), null);
                            }
                        }

                    });

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

                public void componentSelected(ButtonEvent ce) {
                    final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                    ItemBean item = parent.getItemBean();
                    item.setIds("");
                    AppEvent evt = new AppEvent(ItemsEvents.ViewItemForm, item);
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
            journalBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                public void componentSelected(ButtonEvent ce) {
                    final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                    parent.refreshGrid();
                }
            });
        }
    }

    private void initToolBar() {
        saveBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        saveBtn.setTitle(MessagesFactory.getMessages().save_tip());
        add(saveBtn);
        saveBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                service.saveItemBean(parent.getNewItemBean(), new AsyncCallback<ItemResult>() {

                    public void onFailure(Throwable arg0) {

                    }

                    public void onSuccess(ItemResult arg0) {
                        if (arg0.getStatus() == ItemResult.SUCCESS) {
                            parent.refreshGrid();
                            MessageBox.alert(MessagesFactory.getMessages().info_title(), arg0.getDescription(), null);
                        } else if (arg0.getStatus() == ItemResult.FAILURE) {
                            MessageBox.alert(MessagesFactory.getMessages().error_title(), arg0.getDescription(), null);
                        }
                    }

                });

            }
        });

        add(new SeparatorToolItem());

        saveCloseBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveClose()));
        saveCloseBtn.setTitle(MessagesFactory.getMessages().saveClose_tip());
        add(saveCloseBtn);
        saveCloseBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                final ItemsFormPanel parent = (ItemsFormPanel) instance.getParent().getParent();
                service.saveItemBean(parent.getNewItemBean(), new AsyncCallback<ItemResult>() {

                    public void onFailure(Throwable arg0) {

                    }

                    public void onSuccess(ItemResult arg0) {
                        if (arg0.getStatus() == ItemResult.SUCCESS) {
                            parent.refreshGrid();
                            MessageBox.alert(MessagesFactory.getMessages().info_title(), arg0.getDescription(), null);
                            // close the current tab
                            ((TabItem) parent.getParent()).close();
                        } else if (arg0.getStatus() == ItemResult.FAILURE) {
                            MessageBox.alert(MessagesFactory.getMessages().error_title(), arg0.getDescription(), null);
                        }
                    }

                });
            }
        });
    }

    private native void InvokeJournal(String ids, String concept)/*-{
        if(ids.indexOf("@")>0)ids=ids.replaceAll("@",".");
        amalto.updatereport.UpdateReport.browseUpdateReportWithSearchCriteria(dataObject, ids, true);
    }-*/;

}

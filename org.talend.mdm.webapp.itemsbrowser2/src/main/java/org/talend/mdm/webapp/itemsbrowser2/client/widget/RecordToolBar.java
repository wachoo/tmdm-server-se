package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class RecordToolBar extends ToolBar {

    Button saveBtn = new Button(MessagesFactory.getMessages().save_btn());

    Button saveCloseBtn = new Button(MessagesFactory.getMessages().savaClose_btn());

    public RecordToolBar() {
        initToolBar();
    }

    public void updateToolBar() {
        add(new SeparatorToolItem());

        Button menu = new Button(MessagesFactory.getMessages().delete_btn());
        menu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
        Menu sub = new Menu();
        MenuItem delMenu = new MenuItem(MessagesFactory.getMessages().delete_btn());
        delMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Delete()));
        sub.add(delMenu);
        MenuItem trashMenu = new MenuItem(MessagesFactory.getMessages().trash_btn());
        trashMenu.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Send_to_trash()));
        sub.add(trashMenu);
        menu.setMenu(sub);
        menu.setEnabled(false);
        add(menu);

        add(new SeparatorToolItem());

        Button duplicateBtn = new Button(MessagesFactory.getMessages().duplicate_btn());
        duplicateBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Duplicate()));
        duplicateBtn.setTitle(MessagesFactory.getMessages().duplicate_tip());
        add(duplicateBtn);
        duplicateBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {

            }
        });

        add(new SeparatorToolItem());

        Button journalBtn = new Button(MessagesFactory.getMessages().journal_btn());
        journalBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Journal()));
        journalBtn.setTitle(MessagesFactory.getMessages().jouranl_tip());
        add(journalBtn);
        journalBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {

            }
        });

        add(new SeparatorToolItem());

        Button refreshBtn = new Button();
        refreshBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Refresh()));
        refreshBtn.setTitle(MessagesFactory.getMessages().refresh());
        add(refreshBtn);
        journalBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {

            }
        });
    }

    private void initToolBar() {
        saveBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Save()));
        saveBtn.setTitle(MessagesFactory.getMessages().save_tip());
        add(saveBtn);
        saveBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {

            }
        });

        add(new SeparatorToolItem());

        saveCloseBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.SaveClose()));
        saveCloseBtn.setTitle(MessagesFactory.getMessages().saveClose_tip());
        add(saveCloseBtn);
        saveCloseBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {

            }
        });
    }
}

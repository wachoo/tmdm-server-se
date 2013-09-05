// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecordsinstaging.client.widget;

import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsToolBar;
import org.talend.mdm.webapp.browserecordsinstaging.client.i18n.BrowseRecordsInStagingMessages;

import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class ItemsToolBar4Staging extends ItemsToolBar {

    public ItemsToolBar4Staging() {
        super();
        final BrowseRecordsInStagingMessages msg = org.talend.mdm.webapp.browserecordsinstaging.client.i18n.MessagesFactory
                .getMessages();
        deleteMenu.setText(msg.mark_as_deleted());
        Menu menu = deleteMenu.getMenu();

        MenuItem trashMenu = (MenuItem) menu.getItemByItemId("logicalDelMenuInGrid"); //$NON-NLS-1$
        trashMenu.setText(msg.send_to_trans_mark());

        MenuItem delMenu = (MenuItem) menu.getItemByItemId("physicalDelMenuInGrid"); //$NON-NLS-1$
        delMenu.setText(msg.mark_as_deleted());

        delMenu.removeAllListeners();

        delMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

            @Override
            public void componentSelected(MenuEvent ce) {

                if (ItemsListPanel.getInstance().getGrid() == null) {
                    MessageBox.alert(MessagesFactory.getMessages().info_title(), msg.select_mark_item_record(), null);
                } else {
                    if (getSelectItemNumber() == 0) {
                        MessageBox.alert(MessagesFactory.getMessages().info_title(), msg.select_mark_item_record(), null);
                    } else {
                        MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), msg.mark_deleted_confirm(),
                                new DeleteItemsBoxListener(service));
                    }
                }
            }
        });
    }
}

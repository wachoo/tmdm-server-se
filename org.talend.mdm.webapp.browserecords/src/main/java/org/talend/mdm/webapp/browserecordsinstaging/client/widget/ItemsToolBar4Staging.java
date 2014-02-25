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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class ItemsToolBar4Staging extends ItemsToolBar {

    private final BrowseRecordsInStagingMessages msg = org.talend.mdm.webapp.browserecordsinstaging.client.i18n.MessagesFactory
            .getMessages();

    public ItemsToolBar4Staging() {
        super();
        deleteMenu.setText(msg.mark_as_deleted());
        deleteMenu.setMenu(null);

        deleteMenu.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {

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

    @Override
    protected boolean isStaging() {
        return true;
    }
}

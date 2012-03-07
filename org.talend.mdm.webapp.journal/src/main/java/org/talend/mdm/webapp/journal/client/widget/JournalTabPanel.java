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

import org.talend.mdm.webapp.journal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.journal.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalTabPanel extends TabPanel {

    private static JournalTabPanel tabPanel;
    
    private TabItem resultTabItem;
    
    private TabItem timeLineTabItem;
    
    private JournalGridPanel journalGridPanel;

    public static JournalTabPanel getInstance() {
        if (tabPanel == null)
            tabPanel = new JournalTabPanel();
        return tabPanel;
    }

    private JournalTabPanel() {
        this.setResizeTabs(true);
        this.setAnimScroll(true);
        
        resultTabItem = new TabItem(MessagesFactory.getMessages().results_tab());
        resultTabItem.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.table()));
        resultTabItem.setId("resultTabItem"); //$NON-NLS-1$
        resultTabItem.setLayout(new FitLayout());
        resultTabItem.setClosable(false);
        journalGridPanel = new JournalGridPanel();
        resultTabItem.add(journalGridPanel);
        this.add(resultTabItem);
                
        timeLineTabItem = new TabItem(MessagesFactory.getMessages().timeline_tab());
        timeLineTabItem.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.time()));
        timeLineTabItem.setId("tl"); //$NON-NLS-1$
        timeLineTabItem.setClosable(false);
        this.add(timeLineTabItem);
        
        this.setSelection(resultTabItem);
    }

    public TabItem getResultTabItem() {
        return resultTabItem;
    }

    public TabItem getTimeLineTabItem() {
        return timeLineTabItem;
    }

    public JournalGridPanel getJournalGridPanel() {
        return journalGridPanel;
    }
}
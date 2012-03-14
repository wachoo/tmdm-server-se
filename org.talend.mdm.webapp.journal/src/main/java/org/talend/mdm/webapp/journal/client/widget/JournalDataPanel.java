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
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalDataPanel extends ContentPanel {

    private ToolBar toolBar;
    
    private Button openRecordButton;
    
    private TreePanel<JournalTreeModel> tree;
    
    public JournalDataPanel(JournalTreeModel root) {
        this.setFrame(false);
        this.setHeading(MessagesFactory.getMessages().update_report_detail_label());
        this.setBodyBorder(false);
        this.setLayout(new FitLayout());
        
        openRecordButton = new Button(MessagesFactory.getMessages().open_record_button());
        openRecordButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.browse()));
        openRecordButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                
            }
        });
        
        toolBar = new ToolBar();
        toolBar.add(new FillToolItem());
        toolBar.add(openRecordButton);
        this.setTopComponent(toolBar);
        
        TreeStore<JournalTreeModel> store = new TreeStore<JournalTreeModel>();  
        store.add(root, true);
        tree = new TreePanel<JournalTreeModel>(store);
        tree.setDisplayProperty("name"); //$NON-NLS-1$
        tree.getStyle().setLeafIcon(AbstractImagePrototype.create(Icons.INSTANCE.leaf()));
        this.add(tree);
    }

    public TreePanel<JournalTreeModel> getTree() {
        return tree;
    }
}
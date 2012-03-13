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

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.journal.client.Journal;
import org.talend.mdm.webapp.journal.client.JournalServiceAsync;
import org.talend.mdm.webapp.journal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.journal.client.resources.icon.Icons;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalComparisonPanel extends ContentPanel {

    private JournalServiceAsync service = Registry.get(Journal.JOURNAL_SERVICE);
    
    private Button restoreButton;
    
    private TreePanel<JournalTreeModel> tree;
    
    private JournalTreeModel root;
    
    public JournalComparisonPanel(String title, JournalParameters parameter) {
        this.setFrame(false);
        this.setHeading(title);
        this.setLayout(new AnchorLayout());
        this.setBodyBorder(false);
        
        restoreButton = new Button(MessagesFactory.getMessages().restore_button());
        restoreButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                
            }
        });
        this.add(restoreButton);
               
        service.getComparisionTree(parameter, new SessionAwareAsyncCallback<JournalTreeModel>() {
            
            public void onSuccess(JournalTreeModel root) {
                JournalComparisonPanel.this.root = root;
                TreeStore<JournalTreeModel> store = new TreeStore<JournalTreeModel>();
                store.add(root, true);
                tree = new TreePanel<JournalTreeModel>(store);
                tree.setDisplayProperty("name"); //$NON-NLS-1$
                tree.getStyle().setLeafIcon(AbstractImagePrototype.create(Icons.INSTANCE.leaf()));
                JournalComparisonPanel.this.add(tree);
                JournalComparisonPanel.this.layout(true);
                JournalComparisonPanel.this.expandRoot();
            }
        });        
    }

    public void expandRoot() {
        JournalTreeModel model = (JournalTreeModel) root.getChildren().get(0);
        tree.setExpanded(root, true);
        tree.setExpanded(model, true);
    }

    public TreePanel<JournalTreeModel> getTree() {
        return tree;
    }
    
    public void expandElement(TreePanel<JournalTreeModel> treePanel, JournalTreeModel model) {
        treePanel.setExpanded(model, true);
    }

    public void collapseElement(TreePanel<JournalTreeModel> treePanel, JournalTreeModel model) {
        treePanel.setExpanded(model, false);
    }
}
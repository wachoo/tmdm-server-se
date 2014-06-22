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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.journal.client.Journal;
import org.talend.mdm.webapp.journal.client.JournalServiceAsync;
import org.talend.mdm.webapp.journal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.journal.client.resources.icon.Icons;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelView.TreeViewRenderMode;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalComparisonPanel extends ContentPanel {

    private JournalServiceAsync service = Registry.get(Journal.JOURNAL_SERVICE);
    
    private ToolBar toolbar;
    
    private Button restoreButton;
    
    private TreePanel<JournalTreeModel> tree;
    
    private JournalTreeModel root;
    
    private JournalComparisonPanel otherPanel;
    
    private Map<String, JournalTreeModel> modelMap = new HashMap<String, JournalTreeModel>();

    public JournalComparisonPanel(String title, final JournalParameters parameter) {
        this.setFrame(false);
        this.setHeading(title);
        this.setLayout(new FitLayout());
        this.setBodyBorder(false);
        
        restoreButton = new Button(MessagesFactory.getMessages().restore_button());
        restoreButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.restore()));
        restoreButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                service.restoreRecord(parameter, new SessionAwareAsyncCallback<Boolean>() {
                    
                    public void onSuccess(Boolean success) {
                        if(success) {
                            JournalComparisonPanel.this.closeTabPanel();
                        }
                    }
                });
            };
        });
        restoreButton.setEnabled(parameter.isAuth());
        
        toolbar = new ToolBar();
        toolbar.add(new FillToolItem());
        toolbar.add(restoreButton);
        this.setTopComponent(toolbar);
               
        service.getComparisionTree(parameter, new SessionAwareAsyncCallback<JournalTreeModel>() {
            
            public void onSuccess(JournalTreeModel root) {
                JournalComparisonPanel.this.root = root;
                TreeStore<JournalTreeModel> store = new TreeStore<JournalTreeModel>();
                store.add(root, true);
                tree = new TreePanel<JournalTreeModel>(store){

                    @Override
                    protected String renderChild(JournalTreeModel parent, JournalTreeModel child, int depth,
                            TreeViewRenderMode renderMode) {
                        String nodeStr = super.renderChild(parent, child, depth, renderMode);
                        if(child.get("cls") != null){ //$NON-NLS-1$
                            nodeStr = nodeStr.replaceFirst("x-tree3-node", "x-tree3-node " + child.get("cls")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        }
                        return nodeStr;
                    }
                    
                };

                tree.setDisplayProperty("name"); //$NON-NLS-1$
                tree.getStyle().setLeafIcon(AbstractImagePrototype.create(Icons.INSTANCE.leaf()));

                if (modelMap.size() == 0) {
                    List<JournalTreeModel> list = store.getAllItems();
                    for(JournalTreeModel model : list)
                        modelMap.put(model.getId(), model);
                }

                tree.addListener(Events.Expand, new Listener<TreePanelEvent<JournalTreeModel>>() {

                    public void handleEvent(TreePanelEvent<JournalTreeModel> be) {
                        if(otherPanel.getModelMap().size() > 0){
                            JournalTreeModel model = otherPanel.getModelMap().get(be.getItem().getId());
                            otherPanel.getTree().setExpanded(model, true);
                        }                       
                    }
                });

                tree.addListener(Events.Collapse, new Listener<TreePanelEvent<JournalTreeModel>>() {

                    public void handleEvent(TreePanelEvent<JournalTreeModel> be) {
                        if(otherPanel.getModelMap().size() > 0){
                            JournalTreeModel model = otherPanel.getModelMap().get(be.getItem().getId());
                            otherPanel.getTree().setExpanded(model, false);
                        } 
                    }
                });
                
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

    public void setOtherPanel(JournalComparisonPanel otherPanel) {
        this.otherPanel = otherPanel;
    }

    public Map<String, JournalTreeModel> getModelMap() {
        return modelMap;
    }

    private native void closeTabPanel()/*-{
        var tabPanel = $wnd.amalto.core.getTabPanel();
        tabPanel.closeCurrentTab();
    }-*/;
}
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
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.journal.client.Journal;
import org.talend.mdm.webapp.journal.client.JournalServiceAsync;
import org.talend.mdm.webapp.journal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.journal.client.resources.icon.Icons;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.amalto.core.ejb.UpdateReportPOJO;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.data.ModelData;
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
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelView;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelView.TreeViewRenderMode;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalComparisonPanel extends ContentPanel {

    private JournalServiceAsync service = Registry.get(Journal.JOURNAL_SERVICE);
    
    private ToolBar toolbar;
    
    private TreePanel<JournalTreeModel> tree;
    
    private TreePanelView<JournalTreeModel> view;
    
    private JournalTreeModel root;
    
    private JournalComparisonPanel otherPanel;

    private Map<String, JournalTreeModel> modelMap = new HashMap<String, JournalTreeModel>();

    private List<String> changeNodeList;

    private Button previousChangeButton;

    private Button nextChangeButton;

   public JournalComparisonPanel(String title, final JournalParameters parameter,final JournalGridModel journalGridModel,final boolean isBeforePanel) {
        this.setFrame(false);
        this.setHeading(title);
        this.setLayout(new FitLayout());
        this.setBodyBorder(false);
        
        service.getComparisionTree(parameter,UrlUtil.getLanguage(), new SessionAwareAsyncCallback<JournalTreeModel>() {
            
            public void onSuccess(JournalTreeModel root) {
                JournalComparisonPanel.this.root = root;
                TreeStore<JournalTreeModel> store = new TreeStore<JournalTreeModel>();
                store.add(root, true);

                view = new TreePanelView<JournalTreeModel>() {
                    
                    @Override
                    public String getTemplate(ModelData m, String id, String text, AbstractImagePrototype icon, boolean checkable,
                            boolean checked, Joint joint, int level, TreeViewRenderMode renderMode) {
                        // see TMDM-5438
                        if ("Document".equals(text) && level == 0 && ((JournalTreeModel)m).getChildCount() == 0) { //$NON-NLS-1$
                            return super.getTemplate(m, id, text, GXT.IMAGES.tree_folder(), checkable, checked, joint, level, renderMode);
                        } else {
                            return super.getTemplate(m, id, text, icon, checkable, checked, joint, level, renderMode);
                        }                        
                    }                                      
                };
                
                
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
                    for(JournalTreeModel model : list) {
                        modelMap.put(model.getPath(), model);
                    }
                }

                tree.addListener(Events.Expand, new Listener<TreePanelEvent<JournalTreeModel>>() {

                    public void handleEvent(TreePanelEvent<JournalTreeModel> be) {
                        if(otherPanel.getModelMap().size() > 0){
                            JournalTreeModel model = otherPanel.getModelMap().get(be.getItem().getPath());
                            otherPanel.getTree().setExpanded(model, true);
                        }                       
                    }
                });

                tree.addListener(Events.Collapse, new Listener<TreePanelEvent<JournalTreeModel>>() {

                    public void handleEvent(TreePanelEvent<JournalTreeModel> be) {
                        if(otherPanel.getModelMap().size() > 0){
                            JournalTreeModel model = otherPanel.getModelMap().get(be.getItem().getPath());
                            otherPanel.getTree().setExpanded(model, false);
                        } 
                    }
                });
       
                JournalComparisonPanel.this.add(tree);
                JournalComparisonPanel.this.layout(true);
                JournalComparisonPanel.this.expandRoot();
                if (changeNodeList != null && changeNodeList.size() > 0) {
                    tree.expandAll();
                }
            }
        });

        if (UpdateReportPOJO.OPERATION_TYPE_UPDATE.equals(journalGridModel.getOperationType())) {
            
            this.changeNodeList = journalGridModel.getChangeNodeList();
            
            final int count[] = new int[1];
            count[0] = -1;
            
            toolbar = new ToolBar();
            
            if (isBeforePanel) {
                previousChangeButton = new Button(MessagesFactory.getMessages().previous_change_button());
                previousChangeButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.prev()));            
                previousChangeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                    public void componentSelected(ButtonEvent ce) {
                        if (count[0] > 0){
                            count[0] = count[0] - 1;
                        }                    
                        buttonStatus(count[0]);
                        selectTreeNodeByPath(changeNodeList.get(count[0]));
                        otherPanel.selectTreeNodeByPath(changeNodeList.get(count[0]));
                    }           
                });
                previousChangeButton.setEnabled(false);                
                                     
                nextChangeButton = new Button(MessagesFactory.getMessages().next_change_button());
                nextChangeButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.next()));
                nextChangeButton.setIconAlign(IconAlign.RIGHT);
                nextChangeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {                
                    public void componentSelected(ButtonEvent ce) {
                        if (count[0] < changeNodeList.size()-1){
                            count[0] = count[0] + 1;
                        }                   
                        buttonStatus(count[0]);
                        selectTreeNodeByPath(changeNodeList.get(count[0]));
                        otherPanel.selectTreeNodeByPath(changeNodeList.get(count[0]));
                    }
                });            
                nextChangeButton.setEnabled(true);                
            } else {
                previousChangeButton = new Button();
                previousChangeButton.setEnabled(false);
                nextChangeButton = new Button();
                nextChangeButton.setEnabled(false);
            }
            
            toolbar.add(previousChangeButton);
            toolbar.add(nextChangeButton);
            toolbar.add(new FillToolItem());
            
            service.isAdmin(new SessionAwareAsyncCallback<Boolean>() {

                public void onSuccess(Boolean isAdmin) {
                    if (isAdmin){
                        Button restoreButton = new Button(MessagesFactory.getMessages().restore_button());
                        restoreButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.restore()));
                        restoreButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
                            
                            @Override
                            public void componentSelected(ButtonEvent ce) {
                                service.restoreRecord(parameter,UrlUtil.getLanguage(), new SessionAwareAsyncCallback<Boolean>() {
                                    
                                    public void onSuccess(Boolean success) {
                                        if(success) {
                                            JournalComparisonPanel.this.closeTabPanel();
                                        }
                                    }
                                });
                            };
                        });
                        restoreButton.setEnabled(parameter.isAuth());                            
                        toolbar.add(restoreButton);
                    }
                }
            });
            
            this.setTopComponent(toolbar);
        }                     
    }
   
   private void buttonStatus(int index) {
       if (index == 0) {
           previousChangeButton.setEnabled(false);
       } else {
           previousChangeButton.setEnabled(true);
       } 
       if (index == changeNodeList.size()-1) {
           nextChangeButton.setEnabled(false);
       } else {
           nextChangeButton.setEnabled(true);
       }  
   }

    public void expandRoot() {
        if (root.getChildren().size() > 0) {
            JournalTreeModel model = (JournalTreeModel) root.getChildren().get(0);
            tree.setExpanded(root, true);
            tree.setExpanded(model, true);
        }
    }
    
    public void selectTreeNodeByPath(String path) {
        JournalTreeModel model = modelMap.get(path);
        if (path.endsWith("[1]") && model == null) { //$NON-NLS-1$
            model = modelMap.get(path.replace("[1]", "")); //$NON-NLS-1$ //$NON-NLS-2$
        }        
        tree.getSelectionModel().select(false, model);            
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
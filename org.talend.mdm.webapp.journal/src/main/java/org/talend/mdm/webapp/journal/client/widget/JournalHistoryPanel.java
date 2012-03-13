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
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalHistoryPanel extends ContentPanel {
        
    private ToolBar toolBar;
    
    private Button openRecordButton;
    
    private TreePanel<JournalTreeModel> tree;
    
    private JournalComparisonPanel beforePanel;
    
    private JournalComparisonPanel afterPanel;
    
    public JournalHistoryPanel(JournalTreeModel root, JournalGridModel gridModel) {
        this.setFrame(false);
        this.setHeading(MessagesFactory.getMessages().update_report_detail_label());
        this.setLayout(new BorderLayout());
                
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
        
        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 150);
        northData.setCollapsible(false);
        northData.setSplit(true);
        northData.setMargins(new Margins(0, 0, 0, 0));        
        TreeStore<JournalTreeModel> store = new TreeStore<JournalTreeModel>();  
        store.add(root, true);
        tree = new TreePanel<JournalTreeModel>(store);
        tree.setDisplayProperty("name"); //$NON-NLS-1$
        tree.getStyle().setLeafIcon(AbstractImagePrototype.create(Icons.INSTANCE.leaf()));
        this.add(tree, northData);        
        
        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 400);
        westData.setCollapsible(false);
        westData.setSplit(true);
        westData.setMargins(new Margins(5, 5, 0, 0));
        
        beforePanel = new JournalComparisonPanel(MessagesFactory.getMessages().before_label(),
                this.buildParameter(gridModel, "before")); //$NON-NLS-1$
        this.add(beforePanel, westData);
        
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        westData.setCollapsible(false);
        centerData.setMargins(new Margins(5, 0, 0, 0));
        centerData.setSplit(true);
        
        afterPanel = new JournalComparisonPanel(MessagesFactory.getMessages().after_label(),
                this.buildParameter(gridModel, "current")); //$NON-NLS-1$
        this.add(afterPanel, centerData);
    }

    public TreePanel<JournalTreeModel> getTree() {
        return tree;
    }
    
    private JournalParameters buildParameter(JournalGridModel gridModel, String action){
        JournalParameters parameter = new JournalParameters();
        parameter.setDataClusterName(gridModel.getDataContainer());
        parameter.setDataModelName(gridModel.getDataModel());
        parameter.setConceptName(gridModel.getEntity());
        parameter.setDate(Long.parseLong(gridModel.getOperationTime()));
        parameter.setRevisionId(gridModel.getRevisionId());
        parameter.setIds(gridModel.getIds());
        parameter.setAction(action);
        String[] id = gridModel.getKey().split("\\."); //$NON-NLS-1$
        parameter.setId(id);
        return parameter;
    }
 
    public JournalComparisonPanel getBeforePanel() {
        return beforePanel;
    }
  
    public JournalComparisonPanel getAfterPanel() {
        return afterPanel;
    }
}

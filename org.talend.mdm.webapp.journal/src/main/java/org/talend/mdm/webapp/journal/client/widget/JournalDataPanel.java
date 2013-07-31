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
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.amalto.core.ejb.UpdateReportPOJO;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalDataPanel extends FormPanel {

    private JournalServiceAsync service = Registry.get(Journal.JOURNAL_SERVICE);
    
    private ToolBar toolBar;
    
    private Button openRecordButton;
    
    private Button viewUpdateReportButton;
    
    private TreePanel<JournalTreeModel> tree;
    
    private Window treeWindow;
    
    private JournalGridModel journalGridModel;
    
    public JournalDataPanel(final JournalTreeModel root, final JournalGridModel journalGridModel) {
        this.setFrame(false);
        this.setItemId(journalGridModel.getIds());
        this.journalGridModel = journalGridModel;
        this.setHeading(MessagesFactory.getMessages().update_report_detail_label());
        this.setBodyBorder(false);
        this.setLayout(new FitLayout());
        
        openRecordButton = new Button(MessagesFactory.getMessages().open_record_button());
        if (UpdateReportPOJO.OPERATION_TYPE_LOGICAL_DELETE.equals(journalGridModel.getOperationType())
                || UpdateReportPOJO.OPERATION_TYPE_PHYSICAL_DELETE.equals(journalGridModel.getOperationType())) {
            openRecordButton.disable();
        }
        openRecordButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.browse()));
        openRecordButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                service.checkDCAndDM(journalGridModel.getDataContainer(), journalGridModel.getDataModel(), new SessionAwareAsyncCallback<Boolean>() {
                    
                    public void onSuccess(Boolean result) {
                        if(result) {
                            if (journalGridModel.getDataContainer().endsWith("#STAGING")) { //$NON-NLS-1$
                                JournalDataPanel.this.openBrowseRecordPanel4Staging(MessagesFactory.getMessages().journal_label(),
                                        journalGridModel.getKey(), journalGridModel.getEntity());
                            } else {
                                JournalDataPanel.this.openBrowseRecordPanel(MessagesFactory.getMessages().journal_label(),
                                        journalGridModel.getKey(), journalGridModel.getEntity());
                            }
                        } else {
                            MessageBox.alert(MessagesFactory.getMessages().error_level(), MessagesFactory.getMessages()
                                    .select_contain_model_msg(), null);
                        }
                    }
                });
            }
        });
        
        viewUpdateReportButton = new Button(MessagesFactory.getMessages().view_updatereport_button());
        viewUpdateReportButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.view()));
        viewUpdateReportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                treeWindow.show();
                tree.setExpanded(root, true);
            }
        });

        this.addButton(viewUpdateReportButton);
        this.addButton(openRecordButton);

        treeWindow = new Window();
        treeWindow.setHeading(MessagesFactory.getMessages().updatereport_label());
        treeWindow.setWidth(400);
        treeWindow.setHeight(450);
        treeWindow.setLayout(new FitLayout());
        treeWindow.setScrollMode(Scroll.NONE);
        
        TreeStore<JournalTreeModel> store = new TreeStore<JournalTreeModel>();  
        store.add(root, true);
        tree = new TreePanel<JournalTreeModel>(store);
        tree.setDisplayProperty("name"); //$NON-NLS-1$
        tree.getStyle().setLeafIcon(AbstractImagePrototype.create(Icons.INSTANCE.leaf()));
        
        ContentPanel contentPanel = new ContentPanel();
        contentPanel.setHeaderVisible(false);
        contentPanel.setScrollMode(Scroll.AUTO);
        contentPanel.setLayout(new FitLayout());
        contentPanel.add(tree);
        treeWindow.add(contentPanel);
              
        this.setHeading(MessagesFactory.getMessages().change_properties());
        this.setButtonAlign(HorizontalAlignment.RIGHT);
        
        LayoutContainer main = new LayoutContainer();
        main.setLayout(new ColumnLayout());
        
        LayoutContainer left = new LayoutContainer();
        left.setStyleAttribute("paddingRight", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        FormLayout layout = new FormLayout();
        layout.setLabelAlign(LabelAlign.LEFT);
        layout.setLabelWidth(150);
        left.setWidth(350);
        left.setLayout(layout);
        
        LabelField entityField = new LabelField();
        entityField.setFieldLabel(MessagesFactory.getMessages().entity_label() + " : "); //$NON-NLS-1$     
        entityField.setValue(journalGridModel.getEntity());
        left.add(entityField);
        LabelField sourceField = new LabelField();
        sourceField.setFieldLabel(MessagesFactory.getMessages().source_label() + " : "); //$NON-NLS-1$
        sourceField.setValue(journalGridModel.getSource());
        left.add(sourceField);
        LabelField dataContainerField = new LabelField();
        dataContainerField.setFieldLabel(MessagesFactory.getMessages().data_container_label() + " : "); //$NON-NLS-1$
        dataContainerField.setValue(journalGridModel.getDataContainer());
        left.add(dataContainerField);
        LabelField dataModelField = new LabelField();
        dataModelField.setFieldLabel(MessagesFactory.getMessages().data_model_label() + " : "); //$NON-NLS-1$
        dataModelField.setValue(journalGridModel.getDataModel());
        left.add(dataModelField);

        LayoutContainer right = new LayoutContainer();
        right.setStyleAttribute("paddingLeft", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        layout = new FormLayout();    
        layout.setLabelAlign(LabelAlign.LEFT);
        layout.setLabelWidth(150);
        right.setWidth(350);
        right.setLayout(layout);
        
        LabelField keyField = new LabelField();
        keyField.setFieldLabel(MessagesFactory.getMessages().key_label() + " : "); //$NON-NLS-1$
        keyField.setValue(journalGridModel.getKey());        
        right.add(keyField);
        LabelField operationTypeField = new LabelField();
        operationTypeField.setFieldLabel(MessagesFactory.getMessages().operation_type_label() + " : "); //$NON-NLS-1$
        operationTypeField.setValue(journalGridModel.getOperationType());
        right.add(operationTypeField);
        LabelField oeprationTimeField = new LabelField();
        oeprationTimeField.setFieldLabel(MessagesFactory.getMessages().operation_time_label() + " : "); //$NON-NLS-1$
        oeprationTimeField.setValue(journalGridModel.getOperationDate());
        right.add(oeprationTimeField);
        LabelField revisionIdField = new LabelField();
        revisionIdField.setFieldLabel(MessagesFactory.getMessages().revision_id_label() + " : "); //$NON-NLS-1$
        revisionIdField.setValue(journalGridModel.getRevisionId());
        right.add(revisionIdField);     

        main.add(left, new ColumnData(.5));
        main.add(right, new ColumnData(.5));
        FormData formData = new FormData("100%"); //$NON-NLS-1$
        formData.setMargins(new Margins(10, 10, 10, 10));
        this.add(main, formData);
    }

    public TreePanel<JournalTreeModel> getTree() {
        return tree;
    }
    
    public JournalGridModel getJournalGridModel() {
        return this.journalGridModel;
    }

    public String getHeadingString() {
        return MessagesFactory.getMessages().data_change_viewer();
    }
    
    private native void openBrowseRecordPanel(String title, String key, String concept)/*-{
        var arr = key.split("\.");
        $wnd.amalto.itemsbrowser.ItemsBrowser.editItemDetails(title, arr, concept, function(){});
    }-*/;
    
    private native void openBrowseRecordPanel4Staging(String title, String key, String concept)/*-{
        var arr = key.split("\.");
        $wnd.amalto.itemsbrowser.ItemsBrowser.editItemDetails4Staging(title, arr, concept, function(){});
    }-*/;
}
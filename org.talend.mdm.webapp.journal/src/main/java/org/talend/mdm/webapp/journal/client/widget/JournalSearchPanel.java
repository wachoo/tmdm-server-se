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

import org.talend.mdm.webapp.journal.client.Journal;
import org.talend.mdm.webapp.journal.client.JournalServiceAsync;
import org.talend.mdm.webapp.journal.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalSearchPanel extends FormPanel {

    private static JournalSearchPanel formPanel;

    private JournalServiceAsync service = Registry.get(Journal.JOURNAL_SERVICE);
    
    public static JournalSearchPanel getInstance() {
        if (formPanel == null)
            formPanel = new JournalSearchPanel();
        return formPanel;
    }
    
    private JournalSearchPanel() {
        FormData formData = new FormData("100%"); //$NON-NLS-1$
        this.setFrame(true);
        this.setHeading(MessagesFactory.getMessages().search_panel_title());
        this.setButtonAlign(HorizontalAlignment.RIGHT);
        
        LayoutContainer main = new LayoutContainer();  
        main.setLayout(new ColumnLayout()); 
        
        LayoutContainer left = new LayoutContainer();  
        left.setStyleAttribute("paddingRight", "10px");   //$NON-NLS-1$ //$NON-NLS-2$
        FormLayout layout = new FormLayout();  
        layout.setLabelAlign(LabelAlign.LEFT);  
        left.setLayout(layout);
        
        TextField<String> entityField = new TextField<String>();  
        entityField.setFieldLabel(MessagesFactory.getMessages().entity_label());  
        left.add(entityField, formData);
        
        TextField<String> sourceField = new TextField<String>();  
        sourceField.setFieldLabel(MessagesFactory.getMessages().source_label());  
        left.add(sourceField, formData);
        
        TextField<String> startDateField = new TextField<String>();  
        startDateField.setFieldLabel(MessagesFactory.getMessages().start_date_label());  
        left.add(startDateField, formData);
        
        LayoutContainer right = new LayoutContainer();  
        right.setStyleAttribute("paddingLeft", "10px");   //$NON-NLS-1$ //$NON-NLS-2$
        layout = new FormLayout();  
        layout.setLabelAlign(LabelAlign.LEFT);  
        right.setLayout(layout);
        
        TextField<String> keyField = new TextField<String>();  
        keyField.setFieldLabel(MessagesFactory.getMessages().key_label());  
        right.add(keyField, formData);
        
        TextField<String> operationTypeField = new TextField<String>();  
        operationTypeField.setFieldLabel(MessagesFactory.getMessages().operation_type_label());  
        right.add(operationTypeField, formData);
        
        TextField<String> endDateField = new TextField<String>();  
        endDateField.setFieldLabel(MessagesFactory.getMessages().end_date_label());  
        right.add(endDateField, formData);
        
        main.add(left, new ColumnData(.5));  
        main.add(right, new ColumnData(.5)); 
        this.add(main, new FormData("100%")); //$NON-NLS-1$
        
        this.addButton(new Button(MessagesFactory.getMessages().reset_button()));
        this.addButton(new Button(MessagesFactory.getMessages().search_button()));
        this.addButton(new Button(MessagesFactory.getMessages().exprot_excel_button()));
    }
}

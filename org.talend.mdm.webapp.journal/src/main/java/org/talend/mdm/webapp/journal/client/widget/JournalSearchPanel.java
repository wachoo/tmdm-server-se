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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.client.util.PostDataUtil;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.journal.client.Journal;
import org.talend.mdm.webapp.journal.client.JournalEvents;
import org.talend.mdm.webapp.journal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.KeyCodes;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class JournalSearchPanel extends FormPanel {

    private static JournalSearchPanel formPanel;
        
    private TextField<String> entityField;
    
    private TextField<String> keyField;
    
    private ComboBox<ItemBaseModel> sourceCombo;
    
    private ComboBox<ItemBaseModel> operationTypeCombo;
    
    private DateField startDateField;
    
    private DateField endDateField;
        
    private Button resetButton;
    
    private Button searchButton;
    
    private Button exportButton;
    
    
    public static JournalSearchPanel getInstance() {
        if (formPanel == null)
            formPanel = new JournalSearchPanel();
        return formPanel;
    }
    
    private JournalSearchPanel() {
        FormData formData = new FormData("50%"); //$NON-NLS-1$
        this.setFrame(true);
        this.setHeading(MessagesFactory.getMessages().search_panel_title());
        this.setButtonAlign(HorizontalAlignment.RIGHT);

        LayoutContainer main = new LayoutContainer();
        main.setLayout(new ColumnLayout());

        LayoutContainer left = new LayoutContainer();
        left.setStyleAttribute("paddingRight", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        FormLayout layout = new FormLayout();
        layout.setLabelAlign(LabelAlign.LEFT);
        left.setLayout(layout);

        entityField = new TextField<String>();
        entityField.setFieldLabel(MessagesFactory.getMessages().entity_label());
        entityField.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (searchButton != null) {
                        searchButton.fireEvent(Events.Select);
                    }
                }
            }
        });
        left.add(entityField, formData);

        List<String> list = new ArrayList<String>();
        list.add("genericUI"); //$NON-NLS-1$
        list.add("adminWorkbench"); //$NON-NLS-1$
        list.add("dataSynchronization"); //$NON-NLS-1$
        list.add("workflow"); //$NON-NLS-1$

        sourceCombo = new ComboBox<ItemBaseModel>();
        sourceCombo.setId("source");//$NON-NLS-1$
        sourceCombo.setName("source");//$NON-NLS-1$
        sourceCombo.setFieldLabel(MessagesFactory.getMessages().source_label());
        sourceCombo.setDisplayField("label"); //$NON-NLS-1$
        sourceCombo.setValueField("key"); //$NON-NLS-1$
        sourceCombo.setStore(this.getListStore(list));
        sourceCombo.setTriggerAction(TriggerAction.ALL);
        sourceCombo.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (searchButton != null) {
                        searchButton.fireEvent(Events.Select);
                    }
                }
            }
        });
        left.add(sourceCombo, formData);

        startDateField = new DateField();
        startDateField.setFieldLabel(MessagesFactory.getMessages().start_date_label());
        startDateField.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (searchButton != null) {
                        searchButton.fireEvent(Events.Select);
                    }
                }
            }
        });
        left.add(startDateField, formData);

        LayoutContainer right = new LayoutContainer();
        right.setStyleAttribute("paddingLeft", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        layout = new FormLayout();
        layout.setLabelAlign(LabelAlign.LEFT);
        right.setLayout(layout);

        keyField = new TextField<String>();
        keyField.setFieldLabel(MessagesFactory.getMessages().key_label());
        keyField.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (searchButton != null) {
                        searchButton.fireEvent(Events.Select);
                    }
                }
            }
        });
        right.add(keyField, formData);

        list.clear();
        list.add("CREATE"); //$NON-NLS-1$
        list.add("UPDATE"); //$NON-NLS-1$
        list.add("PHYSICAL_DELETE"); //$NON-NLS-1$
        list.add("LOGIC_DELETE"); //$NON-NLS-1$
        list.add("RESTORED"); //$NON-NLS-1$
        list.add("ACTION"); //$NON-NLS-1$

        operationTypeCombo = new ComboBox<ItemBaseModel>();
        operationTypeCombo.setId("operationType");//$NON-NLS-1$
        operationTypeCombo.setName("operationType");//$NON-NLS-1$
        operationTypeCombo.setFieldLabel(MessagesFactory.getMessages().operation_type_label());
        operationTypeCombo.setDisplayField("label"); //$NON-NLS-1$
        operationTypeCombo.setValueField("key"); //$NON-NLS-1$
        operationTypeCombo.setStore(this.getListStore(list));
        operationTypeCombo.setTriggerAction(TriggerAction.ALL);
        operationTypeCombo.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (searchButton != null) {
                        searchButton.fireEvent(Events.Select);
                    }
                }
            }
        });
        right.add(operationTypeCombo, formData);

        endDateField = new DateField();
        endDateField.setFieldLabel(MessagesFactory.getMessages().end_date_label());
        endDateField.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (searchButton != null) {
                        searchButton.fireEvent(Events.Select);
                    }
                }
            }
        });
        right.add(endDateField, formData);

        main.add(left, new ColumnData(.5));
        main.add(right, new ColumnData(.5));
        this.add(main, new FormData("100%")); //$NON-NLS-1$

        resetButton = new Button(MessagesFactory.getMessages().reset_button());
        resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                entityField.clear();
                keyField.clear();
                sourceCombo.clear();
                operationTypeCombo.clear();
                startDateField.clear();
                endDateField.clear();
            }
        });
        this.addButton(resetButton);

        searchButton = new Button(MessagesFactory.getMessages().search_button());
        searchButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                bundleCriteria();
                Dispatcher dispatcher = Dispatcher.get();
                dispatcher.dispatch(JournalEvents.DoSearch);
            }
        });
        this.addButton(searchButton);

        exportButton = new Button(MessagesFactory.getMessages().exprot_excel_button());
        exportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                PostDataUtil.postData("/journal/journalExport", getCriteriaMap()); //$NON-NLS-1$
            }
        });
        this.addButton(exportButton);
    }
    
    private ListStore<ItemBaseModel> getListStore(List<String> list) {
        List<ItemBaseModel> modelList = new ArrayList<ItemBaseModel>();
        for (String str : list) {
            ItemBaseModel model = new ItemBaseModel();
            model.set("label", str); //$NON-NLS-1$
            model.set("key", str); //$NON-NLS-1$
            modelList.add(model);
        }
        ListStore<ItemBaseModel> store = new ListStore<ItemBaseModel>();
        store.add(modelList);
        return store;
    }
    
    private void bundleCriteria() {
        JournalSearchCriteria criteria = Registry.get(Journal.SEARCH_CRITERIA);
        criteria.setEntity(entityField.getValue());
        criteria.setKey(keyField.getValue());
        if (sourceCombo.getValue() != null)
            criteria.setSource(sourceCombo.getValue().get("key").toString()); //$NON-NLS-1$
        else
            criteria.setSource(null);

        if (operationTypeCombo.getValue() != null)
            criteria.setOperationType(operationTypeCombo.getValue().get("key").toString()); //$NON-NLS-1$
        else
            criteria.setOperationType(null);

        criteria.setStartDate(startDateField.getValue());
        criteria.setEndDate(endDateField.getValue());
        criteria.setBrowseRecord(false);
    }

    private Map<String, String> getCriteriaMap() {
        Map<String, String> map = new HashMap<String, String>();
        if (entityField.getValue() != null)
            map.put("entity", entityField.getValue()); //$NON-NLS-1$
        if (keyField.getValue() != null)
            map.put("key", keyField.getValue()); //$NON-NLS-1$
        if (sourceCombo.getValue() != null)
            map.put("source", sourceCombo.getValue().get("key").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        if (operationTypeCombo.getValue() != null)
            map.put("operationType", operationTypeCombo.getValue().get("key").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        if (startDateField.getValue() != null)
            map.put("startDate", String.valueOf(startDateField.getValue().getTime())); //$NON-NLS-1$
        if (endDateField.getValue() != null)
            map.put("endDate", String.valueOf(endDateField.getValue().getTime())); //$NON-NLS-1$
        map.put("language", UrlUtil.getLanguage()); //$NON-NLS-1$

        return map;
    }
    
    public void setEntityFieldValue(String value) {
        this.entityField.setValue(value);
    }
    
    public void setKeyFieldValue(String value) {
        this.keyField.setValue(value);
    }
}
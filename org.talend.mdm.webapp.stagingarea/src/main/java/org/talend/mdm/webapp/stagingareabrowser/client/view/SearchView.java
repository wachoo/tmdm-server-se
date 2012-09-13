// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingareabrowser.client.view;

import org.talend.mdm.webapp.stagingareabrowser.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareabrowser.client.controller.SearchController;
import org.talend.mdm.webapp.stagingareabrowser.client.model.SearchModel;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;


public class SearchView extends AbstractView {

    private final int ALL_RECORDS = 1;

    private final int INVALID_RECORDS = 2;

    private final int VALID_RECORDS = 3;

    private ContentPanel leftPanel;

    private ContentPanel rightPanel;

    private ComboBox<BaseModel> entityCombo;

    private TextField<String> sourceField;

    private TextField<String> keyField;

    private DateField startDate;

    private DateField endDate;
    
    private ComboBox<BaseModel> stateCombo;

    private TextField<String> statusCodeField;

    private FieldSet fieldSet;

    private Button searchButton;

    @Override
    protected void initComponents() {
        entityCombo = new ComboBox<BaseModel>();
        entityCombo.setStore(SearchController.getStore());
        entityCombo.setFieldLabel(messages.entity());
        entityCombo.setTriggerAction(TriggerAction.ALL);
        entityCombo.setEditable(false);
        entityCombo.setDisplayField("name");//$NON-NLS-1$
        entityCombo.setValueField("value");//$NON-NLS-1$
        entityCombo.setForceSelection(true);


        sourceField = new TextField<String>();
        sourceField.setFieldLabel(messages.source());
        keyField = new TextField<String>();
        keyField.setFieldLabel(messages.key());
        startDate = new DateField();
        startDate.setFieldLabel(messages.start_date());
        endDate = new DateField();
        endDate.setFieldLabel(messages.end_date());

        stateCombo = new ComboBox<BaseModel>();
        stateCombo.setFieldLabel(messages.stage());
        ListStore<BaseModel> stateStore = new ListStore<BaseModel>();
        BaseModel state1 = new BaseModel();
        state1.set("name", messages.all_records()); //$NON-NLS-1$
        state1.set("value", ALL_RECORDS); //$NON-NLS-1$
        
        BaseModel state2 = new BaseModel();
        state2.set("name", messages.invalid_records()); //$NON-NLS-1$
        state2.set("value", INVALID_RECORDS); //$NON-NLS-1$
        
        BaseModel state3 = new BaseModel();
        state3.set("name", messages.valid_records()); //$NON-NLS-1$
        state3.set("value", VALID_RECORDS); //$NON-NLS-1$
        
        stateStore.add(state1);
        stateStore.add(state2);
        stateStore.add(state3);
        stateCombo.setStore(stateStore);
        stateCombo.setDisplayField("name"); //$NON-NLS-1$
        stateCombo.setValueField("value"); //$NON-NLS-1$
        statusCodeField = new TextField<String>();
        statusCodeField.setFieldLabel(messages.status_code());

        leftPanel = new ContentPanel();
        leftPanel.setHeaderVisible(false);
        leftPanel.setBodyBorder(false);
        rightPanel = new ContentPanel();
        rightPanel.setHeaderVisible(false);
        rightPanel.setBodyBorder(false);
        fieldSet = new FieldSet();

        fieldSet.setHeading(messages.status());
        searchButton = new Button(messages.search());
        searchButton.setSize(300, 30);
    }

    @Override
    protected void initLayout() {
        mainPanel.setLayout(new HBoxLayout());
        leftPanel.setLayout(new FormLayout());
        fieldSet.setLayout(new FormLayout());

        leftPanel.add(entityCombo);
        leftPanel.add(sourceField);
        leftPanel.add(keyField);
        leftPanel.add(startDate);
        leftPanel.add(endDate);

        fieldSet.add(stateCombo);
        fieldSet.add(statusCodeField);
        rightPanel.add(fieldSet);
        rightPanel.add(searchButton);

        mainPanel.add(leftPanel, new HBoxLayoutData(10, 0, 0, 10));
        mainPanel.add(rightPanel, new HBoxLayoutData(5, 0, 0, 10));
    }

    @Override
    protected void registerEvent() {
        searchButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                SearchModel searchModel = new SearchModel();
                if (entityCombo.getValue() != null) {
                    searchModel.setEntity((String) entityCombo.getValue().get("value")); //$NON-NLS-1$
                }
                searchModel.setSource(sourceField.getValue());
                searchModel.setKey(keyField.getValue());
                if (stateCombo.getValue() != null) {
                    searchModel.setState((Integer) stateCombo.getValue().get("value")); //$NON-NLS-1$
                }
                searchModel.setStatusCode(statusCodeField.getValue());
                searchModel.setStartDate(startDate.getValue());
                searchModel.setEndDate(endDate.getValue());
                ControllerContainer.get().getResultsController().searchResult(searchModel);
            }
        });
    }
}

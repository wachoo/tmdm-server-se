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
package org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.Constants;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class SimpleCriterionPanel extends HorizontalPanel {

    private ComboBox<BaseModel> keyComboBox;

    private ComboBox<BaseModel> operatorComboBox;

    private TextField valueTextBox;

    private ComboBox<BaseModel> valueComboBox;

    private DatePicker valuePicker;

    private ViewBean view;

    private Map<String, ArrayList<String>> itemsPredicates = new HashMap<String, ArrayList<String>>();

    private ListStore<BaseModel> list = new ListStore<BaseModel>();

    private ListStore<BaseModel> operatorlist = new ListStore<BaseModel>();

    public SimpleCriterionPanel(final MultipleCriteriaPanel ancestor, final Panel parent) {
        super();
        setSpacing(3);

        keyComboBox = new ComboBox<BaseModel>();
        keyComboBox.setDisplayField("name");
        keyComboBox.setValueField("value");
        keyComboBox.setStore(list);
        keyComboBox.setTriggerAction(TriggerAction.ALL);

        keyComboBox.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

            public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
                // TODO Auto-generated method stub
                adaptOperatorAndValue();
            }

        });

        add(keyComboBox);

        operatorComboBox = new ComboBox<BaseModel>();
        operatorComboBox.setStore(operatorlist);
        add(operatorComboBox);

        valueTextBox = new TextField();
        valueTextBox.setVisible(false);
        add(valueTextBox);

        valueComboBox = new ComboBox<BaseModel>();
        valueComboBox.setStore(getEmptyStore());
        valueComboBox.setVisible(false);
        add(valueComboBox);

        valuePicker = new DatePicker();
        valuePicker.setVisible(false);
        add(valuePicker);

        if (ancestor != null)
            add(new Image(Icons.INSTANCE.remove()) {

                {
                    addClickListener(new ClickListener() {

                        public void onClick(Widget sender) {
                            parent.remove(SimpleCriterionPanel.this);
                            if (ancestor != null)
                                ancestor.redraw();
                        }
                    });
                }
            });
        else {
            // add simple search button
            Button searchBut = new Button("Search");
            searchBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

                public void componentSelected(ButtonEvent ce) {
                    // TODO
                }

            });
            add(searchBut);

            // add advanced search button
            Button advancedBut = new Button("Advanced Search");
            advancedBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

                public void componentSelected(ButtonEvent ce) {
                    // TODO
                    // show advanced Search panel
                    Window winAdvanced = new Window();
                    winAdvanced.setBodyBorder(false);
                    winAdvanced.setFrame(false);
                    winAdvanced.setLayout(new FitLayout());
                    winAdvanced.setModal(true);
                    winAdvanced.add(new AdvancedSearchPanel());
                    winAdvanced.show();
                }

            });
            add(advancedBut);
        }

    }

    public void updateFields(ViewBean view) {
        // field combobox
        BaseModel field;
        if (view.getSearchables() != null)
            for (String key : view.getSearchables().keySet()) {
                field = new BaseModel();
                field.set("name", view.getSearchables().get(key));
                field.set("value", key);
                list.add(field);
            }
        else {
            field = new BaseModel();
            field.set("", "");
            list.add(field);
        }

        if (view.getMetaDataTypes() != null)
            for (String key : view.getMetaDataTypes().keySet()) {
                itemsPredicates.put(key, view.getMetaDataTypes().get(key));
            }

        adaptOperatorAndValue();
    }

    private void setOperatorComboBox(List<String> cons) {
        BaseModel field;

        for (String curOper : cons) {
            field = new BaseModel();
            field.set("name", curOper);
            field.set("value", curOper);
            operatorlist.add(field);
        }

        operatorComboBox.setDisplayField("name");
        operatorComboBox.setValueField("value");
        operatorComboBox.setStore(list);
        operatorComboBox.setTriggerAction(TriggerAction.ALL);
    }

    private void adaptOperatorAndValue() {
        int delimeter = getKey().indexOf("/");
        if (delimeter == -1) {
            String conceptName = view.getViewPK();

            if (getKey().equals(conceptName)) {
                setOperatorComboBox(Constants.fulltextOperators);
            }

            valueComboBox.setVisible(false);
            valuePicker.setVisible(false);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*");
            return;
        }

        String predicateValues = itemsPredicates.get(getKey()).get(0);
        if (predicateValues == "xsd:string" || predicateValues == "xsd:normalizedString" || predicateValues == "xsd:token") {
            setOperatorComboBox(Constants.fullOperators);
            valueComboBox.setVisible(false);
            valuePicker.setVisible(false);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*");
        } else if (predicateValues == "xsd:date" || predicateValues == "xsd:time" || predicateValues == "xsd:dateTime") {
            setOperatorComboBox(Constants.dateOperators);
            valueComboBox.setVisible(false);
            valuePicker.setVisible(true);
            valueTextBox.setVisible(true);
        } else if (predicateValues == "xsd:double" || predicateValues == "xsd:float" || predicateValues == "xsd:integer"
                || predicateValues == "xsd:decimal" || predicateValues == "xsd:byte" || predicateValues == "xsd:int"
                || predicateValues == "xsd:long" || predicateValues == "xsd:negativeInteger"
                || predicateValues == "xsd:nonNegativeInteger" || predicateValues == "xsd:nonPositiveInteger"
                || predicateValues == "xsd:positiveInteger" || predicateValues == "xsd:short"
                || predicateValues == "xsd:unsignedLong" || predicateValues == "xsd:unsignedInt"
                || predicateValues == "xsd:unsignedShort" || predicateValues == "xsd:unsignedByte") {
            setOperatorComboBox(Constants.numOperators);
            valueComboBox.setVisible(false);
            valuePicker.setVisible(false);
            valueTextBox.setVisible(true);
        } else if (predicateValues == "xsd:boolean") {
            // TODO
            // var booleanPredicates = ["true" , "false"];
            // var prefix = EQUAL_OPERS[language];
            // for(var i = 0; i < booleanPredicates.length; i++)
            // {
            // booleanPredicates[i] = prefix + " " + booleanPredicates[i];
            // }
            setOperatorComboBox(Constants.booleanOperators);
        } else if (predicateValues == "foreign key") {
        } else if (predicateValues == "enumeration") {

        } else if (predicateValues == "complex type") {
            setOperatorComboBox(Constants.fullOperators);
            valueComboBox.setVisible(false);
            valuePicker.setVisible(false);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*");
        } else {
            setOperatorComboBox(Constants.fullOperators);
            valueComboBox.setVisible(false);
            valuePicker.setVisible(false);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*");
        }

    }

    // private void adaptFields() {
    // Field field = view.getSearchables().get(getKey());
    // switch (field.getCriterionType()) {
    // case LIST:
    // valueListBox.setVisible(true);
    // valueTextBox.setVisible(false);
    //
    // valueListBox.clear();
    // for (KeyValue s : field.getAvailables()) {
    // valueListBox.addItem(s.getLabel(), s.getId());
    // }
    // break;
    // case TEXT:
    // valueListBox.setVisible(false);
    // valueTextBox.setVisible(true);
    // break;
    // }
    // }

    private String getKey() {
        return "Agency";
        // return keyComboBox.getSelectedText();
    }

    private String getOperator() {
        return operatorComboBox.getSelectedText();
    }

    // private String getValue() {
    // Field field = configuration.get(getKey());
    // switch (field.getCriterionType()) {
    // case LIST:
    // return valueListBox.getSelectedValue();
    // case TEXT:
    // return valueTextBox.getText();
    // }
    // return null;
    // }

    // public SimpleCriterion getCriterion() {
    // return new SimpleCriterion(getKey(), getOperator(), getValue());
    // }

    // public void setCriterion(SimpleCriterion criterion) {
    // keyComboBox.setSelected(criterion.getKey());
    // adaptOperatorAndValue();
    // operatorListBox.setSelected(criterion.getOperator());
    //
    // Field field = configuration.get(getKey());
    // switch (field.getCriterionType()) {
    // case LIST:
    // valueListBox.setSelected(criterion.getValue());
    // break;
    // case TEXT:
    // valueTextBox.setText(criterion.getValue());
    // break;
    // }
    // }

    private ListStore<BaseModel> getEmptyStore() {
        ListStore<BaseModel> list = new ListStore<BaseModel>();
        BaseModel fields = new BaseModel();
        fields.set("", "");
        list.add(fields);
        return list;
    }

}

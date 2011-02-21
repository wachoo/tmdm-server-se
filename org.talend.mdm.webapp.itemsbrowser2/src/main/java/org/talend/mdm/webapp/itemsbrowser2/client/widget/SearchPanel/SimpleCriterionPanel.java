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

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.Constants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.SimpleCriterion;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class SimpleCriterionPanel<T> extends HorizontalPanel {

    private ComboBox<BaseModel> keyComboBox;

    private ComboBox<BaseModel> operatorComboBox;

    private TextField valueTextBox;

    private ComboBox<BaseModel> valueComboBox;

    private DateField valueDate;

    private ViewBean view;

    private Map<String, String> itemsPredicates = new HashMap<String, String>();

    private ListStore<BaseModel> list = new ListStore<BaseModel>();

    private ListStore<BaseModel> operatorlist = new ListStore<BaseModel>();

    private ListStore<BaseModel> valuelist = new ListStore<BaseModel>();

    public SimpleCriterionPanel(final MultipleCriteriaPanel ancestor, final Panel parent) {
        super();
        setSpacing(3);

        keyComboBox = new ComboBox<BaseModel>();
        keyComboBox.setWidth(100);
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
        operatorComboBox.setDisplayField("name");
        operatorComboBox.setValueField("value");
        operatorComboBox.setStore(operatorlist);
        operatorComboBox.setTriggerAction(TriggerAction.ALL);
        add(operatorComboBox);

        valueTextBox = new TextField();
        add(valueTextBox);

        valueComboBox = new ComboBox<BaseModel>();
        valueComboBox.setStore(valuelist);
        valueComboBox.setVisible(false);
        add(valueComboBox);

        valueDate = new DateField();
        valueDate.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));
        valueDate.setVisible(false);
        add(valueDate);

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
        }

    }

    public void updateFields(ViewBean view) {
        this.view = view;
        // field combobox
        BaseModel field;
        list.removeAll();
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

        if (view.getMetaDataTypes() != null) {
            itemsPredicates.clear();
            for (String key : view.getMetaDataTypes().keySet()) {
                itemsPredicates.put(key, view.getMetaDataTypes().get(key));
            }
        }

        keyComboBox.setValue(list.getAt(0));
    }

    private void setOperatorComboBox(Map<String, String> cons) {
        BaseModel field;

        operatorlist.removeAll();
        for (String curOper : cons.keySet()) {
            field = new BaseModel();
            field.set("name", cons.get(curOper));
            field.set("value", curOper);
            operatorlist.add(field);
        }
        operatorComboBox.setValue(operatorlist.getAt(0));
    }

    private void adaptOperatorAndValue() {
        int delimeter = getKey().indexOf("/");
        if (delimeter == -1) {
            String conceptName = view.getViewPK();

            if (getKey().equals(conceptName)) {
                setOperatorComboBox(Constants.fulltextOperators);
            }

            valueComboBox.setVisible(false);
            valueDate.setVisible(false);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*");
            return;
        }

        String predicateValues = itemsPredicates.get(getKey());

        if (predicateValues.equals("string") || predicateValues.equals("normalizedString") || predicateValues.equals("token")) {
            setOperatorComboBox(Constants.fullOperators);
            valueComboBox.setVisible(false);
            valueDate.setVisible(false);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*");
        } else if (predicateValues.equals("date") || predicateValues.equals("time") || predicateValues.equals("dateTime")) {
            setOperatorComboBox(Constants.dateOperators);
            valueComboBox.setVisible(false);
            valueDate.setVisible(true);
            valueTextBox.setVisible(false);
        } else if (predicateValues.equals("double") || predicateValues.equals("float") || predicateValues.equals("integer")
                || predicateValues.equals("decimal") || predicateValues.equals("byte") || predicateValues.equals("int")
                || predicateValues.equals("long") || predicateValues.equals("negativeInteger")
                || predicateValues.equals("nonNegativeInteger") || predicateValues.equals("nonPositiveInteger")
                || predicateValues.equals("positiveInteger") || predicateValues.equals("short")
                || predicateValues.equals("unsignedLong") || predicateValues.equals("unsignedInt")
                || predicateValues.equals("unsignedShort") || predicateValues.equals("unsignedByte")) {
            setOperatorComboBox(Constants.numOperators);
            valueComboBox.setVisible(false);
            valueDate.setVisible(false);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*");
        } else if (predicateValues.equals("boolean")) {
            // TODO
            // var booleanPredicates = ["true" , "false"];
            // var prefix = EQUAL_OPERS[language];
            // for(var i = 0; i < booleanPredicates.length; i++)
            // {
            // booleanPredicates[i] = prefix + " " + booleanPredicates[i];
            // }
            setOperatorComboBox(Constants.booleanOperators);
        } else if (predicateValues.equals("foreign key")) {
        } else if (predicateValues.equals("enumeration")) {

        } else if (predicateValues.equals("complex type")) {
            setOperatorComboBox(Constants.fullOperators);
            valueComboBox.setVisible(false);
            valueDate.setVisible(false);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*");
        } else {
            setOperatorComboBox(Constants.fullOperators);
            valueComboBox.setVisible(false);
            valueDate.setVisible(false);
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
        return keyComboBox.getValue().get("value");
    }

    private String getOperator() {
        return operatorComboBox.getValue().get("value");
    }

    private String getValue() {
        if (valueComboBox.isVisible())
            return valueComboBox.getValue().get("value");
        if (valueDate.isVisible())
            return valueDate.getValue().toGMTString();
        if (valueTextBox.isVisible())
            return valueTextBox.getValue().toString();
        return null;
    }

    public SimpleCriterion getCriterion() {
        return new SimpleCriterion(getKey(), getOperator(), getValue());
    }

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

    public ViewBean getView() {
        return view;
    }

    public void setView(ViewBean view) {
        this.view = view;
    }

}

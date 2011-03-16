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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.OperatorConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.SimpleCriterion;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
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
import com.google.gwt.i18n.client.DateTimeFormat;
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

    private TextField<String> valueTextBox;

    private ComboBox<BaseModel> valueComboBox;

    private DateField valueDate;

    private ViewBean view;

    private Map<String, TypeModel> itemsPredicates = new HashMap<String, TypeModel>();

    private ListStore<BaseModel> list = new ListStore<BaseModel>();

    private ListStore<BaseModel> operatorlist = new ListStore<BaseModel>();

    private ListStore<BaseModel> valuelist = new ListStore<BaseModel>();

    public SimpleCriterionPanel(final MultipleCriteriaPanel ancestor, final Panel parent) {
        super();
        setSpacing(3);

        keyComboBox = new ComboBox<BaseModel>();
        keyComboBox.setAutoWidth(true);
        keyComboBox.setDisplayField("name"); //$NON-NLS-1$
        keyComboBox.setValueField("value"); //$NON-NLS-1$
        keyComboBox.setStore(list);
        keyComboBox.setTriggerAction(TriggerAction.ALL);

        keyComboBox.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

            public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
                adaptOperatorAndValue();
            }

        });

        add(keyComboBox);

        operatorComboBox = new ComboBox<BaseModel>();
        operatorComboBox.setAutoWidth(true);
        operatorComboBox.setDisplayField("name"); //$NON-NLS-1$
        operatorComboBox.setValueField("value"); //$NON-NLS-1$
        operatorComboBox.setStore(operatorlist);
        operatorComboBox.setTriggerAction(TriggerAction.ALL);
        add(operatorComboBox);

        valueTextBox = new TextField<String>();
        add(valueTextBox);

        valueComboBox = new ComboBox<BaseModel>();
        valueComboBox.setAutoWidth(true);
        valueComboBox.setDisplayField("name"); //$NON-NLS-1$
        valueComboBox.setValueField("value"); //$NON-NLS-1$
        valueComboBox.setStore(valuelist);
        valueComboBox.setTriggerAction(TriggerAction.ALL);
        valueComboBox.setVisible(false);
        add(valueComboBox);

        valueDate = new DateField();
        valueDate.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd")); //$NON-NLS-1$
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
        if (this.view.getSearchables() != null)
            for (String key : this.view.getSearchables().keySet()) {
                field = new BaseModel();
                field.set("name", this.view.getSearchables().get(key)); //$NON-NLS-1$
                field.set("value", key); //$NON-NLS-1$
                list.add(field);
            }
        else {
            field = new BaseModel();
            field.set("", ""); //$NON-NLS-1$  //$NON-NLS-2$
            list.add(field);
        }

        if (this.view.getBindingEntityModel().getMetaDataTypes() != null) {
            itemsPredicates.clear();
            for (String key : this.view.getBindingEntityModel().getMetaDataTypes().keySet()) {
                itemsPredicates.put(key, this.view.getBindingEntityModel().getMetaDataTypes().get(key));
            }
        }

        keyComboBox.setValue(list.getAt(0));
    }

    private void setOperatorComboBox(Map<String, String> cons) {
        BaseModel field;

        operatorlist.removeAll();
        for (String curOper : cons.keySet()) {
            field = new BaseModel();
            field.set("name", cons.get(curOper)); //$NON-NLS-1$
            field.set("value", curOper); //$NON-NLS-1$
            operatorlist.add(field);
        }
        operatorComboBox.setValue(operatorlist.getAt(0));
    }

    private void adaptOperatorAndValue() {
        int delimeter = getKey().indexOf("/"); //$NON-NLS-1$
        if (delimeter == -1) {
            if (getKey().equals(CommonUtil.getConceptFromBrowseItemView(view.getViewPK()))) {
                setOperatorComboBox(OperatorConstants.fulltextOperators);
            }

            valueComboBox.setVisible(false);
            valueComboBox.setValue(null);
            valueDate.setVisible(false);
            valueDate.setValue(null);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*"); //$NON-NLS-1$
            return;
        }

        String predicateValues = itemsPredicates.get(getKey()).getType().getTypeName();

        if (predicateValues.equals("string") || predicateValues.equals("normalizedString") || predicateValues.equals("token")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            setOperatorComboBox(OperatorConstants.fullOperators);
            valueComboBox.setVisible(false);
            valueComboBox.setValue(null);
            valueDate.setVisible(false);
            valueDate.setValue(null);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*"); //$NON-NLS-1$
        } else if (predicateValues.equals("date") || predicateValues.equals("time") || predicateValues.equals("dateTime")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            setOperatorComboBox(OperatorConstants.dateOperators);
            valueComboBox.setVisible(false);
            valueComboBox.setValue(null);
            valueDate.setVisible(true);
            valueTextBox.setVisible(false);
            valueTextBox.setValue(null);
        } else if (predicateValues.equals("double") || predicateValues.equals("float") || predicateValues.equals("integer") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                || predicateValues.equals("decimal") || predicateValues.equals("byte") || predicateValues.equals("int") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                || predicateValues.equals("long") || predicateValues.equals("negativeInteger") //$NON-NLS-1$ //$NON-NLS-2$
                || predicateValues.equals("nonNegativeInteger") || predicateValues.equals("nonPositiveInteger") //$NON-NLS-1$ //$NON-NLS-2$
                || predicateValues.equals("positiveInteger") || predicateValues.equals("short") //$NON-NLS-1$ //$NON-NLS-2$
                || predicateValues.equals("unsignedLong") || predicateValues.equals("unsignedInt") //$NON-NLS-1$ //$NON-NLS-2$
                || predicateValues.equals("unsignedShort") || predicateValues.equals("unsignedByte")) { //$NON-NLS-1$ //$NON-NLS-2$
            setOperatorComboBox(OperatorConstants.numOperators);
            valueComboBox.setVisible(false);
            valueComboBox.setValue(null);
            valueDate.setVisible(false);
            valueDate.setValue(null);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*"); //$NON-NLS-1$
        } else if (predicateValues.equals("boolean")) { //$NON-NLS-1$
            setOperatorComboBox(OperatorConstants.booleanOperators);
            valueComboBox.setVisible(false);
            valueDate.setVisible(false);
            valueTextBox.setVisible(false);
            valueTextBox.setValue(null);
        } else if (predicateValues.equals("foreign key")) { //$NON-NLS-1$
            setOperatorComboBox(OperatorConstants.fullOperators);
            valueComboBox.setVisible(true);
            valueDate.setVisible(false);
            valueTextBox.setVisible(false);
            valueTextBox.setValue(null);
        } else if (predicateValues.equals("complex type")) { //$NON-NLS-1$
            setOperatorComboBox(OperatorConstants.fullOperators);
            valueComboBox.setVisible(false);
            valueComboBox.setValue(null);
            valueDate.setVisible(false);
            valueDate.setValue(null);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*"); //$NON-NLS-1$
        } else if (itemsPredicates.get(getKey()).isSimpleType()) {
            SimpleTypeModel type = (SimpleTypeModel) itemsPredicates.get(getKey());
            List<FacetModel> facets = type.getFacets();
            if (facets != null && facets.get(0).getName().equals("enumeration")) { //$NON-NLS-1$   
                valuelist.removeAll();
                BaseModel field;
                for (FacetModel facet : facets) {
                    field = new BaseModel();
                    field.set("name", facet.getValue()); //$NON-NLS-1$
                    field.set("value", facet.getName()); //$NON-NLS-1$
                    valuelist.add(field);
                }
                setOperatorComboBox(OperatorConstants.enumOperators);
                valueComboBox.setVisible(true);
                valueDate.setVisible(false);
                valueTextBox.setVisible(false);
                valueTextBox.setValue(null);
            }
        } else {
            setOperatorComboBox(OperatorConstants.fullOperators);
            valueComboBox.setVisible(false);
            valueComboBox.setValue(null);
            valueDate.setVisible(false);
            valueDate.setValue(null);
            valueTextBox.setVisible(true);
            valueTextBox.setValue("*"); //$NON-NLS-1$
        }

    }

    private String getKey() {
        return keyComboBox.getValue().get("value"); //$NON-NLS-1$
    }

    private String getOperator() {
        return operatorComboBox.getValue().get("value"); //$NON-NLS-1$
    }

    private String getValue() {
        String curValue = null;
        if (valueComboBox.getValue() != null)
            return valueComboBox.getValue().get("value").toString(); //$NON-NLS-1$ 
        if (valueDate.getValue() != null) {
            return DateTimeFormat.getFormat("yyyy-MM-dd").format(valueDate.getValue()); //$NON-NLS-1$
        }
        if (valueTextBox.getValue() != null)
            return valueTextBox.getValue().toString(); //$NON-NLS-1$

        return curValue;
    }

    public SimpleCriterion getCriteria() {
        return new SimpleCriterion(getKey(), getOperator(), getValue());
    }

    public void setCriterion(SimpleCriterion criterion) {
        try {
            keyComboBox.setValue(list.findModel("value", criterion.getKey())); //$NON-NLS-1$
            adaptOperatorAndValue();
            operatorComboBox.setValue(operatorlist.findModel("value", criterion.getOperator())); //$NON-NLS-1$
            if (valueComboBox.isVisible())
                valueComboBox.setValue(valuelist.findModel("value", criterion.getValue())); //$NON-NLS-1$
            if (valueDate.isVisible())
                valueDate.setValue(new Date(criterion.getValue()));
            if (valueTextBox.isVisible())
                valueTextBox.setValue(criterion.getValue());
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
    }

    public ViewBean getView() {
        return view;
    }

    public void setView(ViewBean view) {
        this.view = view;
    }

}

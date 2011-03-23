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
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.SimpleCriterion;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey.FKField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey.ReturnCriteriaFK;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.creator.SearchFieldCreator;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class SimpleCriterionPanel<T> extends HorizontalPanel implements ReturnCriteriaFK {

    private ComboBox<BaseModel> keyComboBox;

    private ComboBox<BaseModel> operatorComboBox;

    private Field field;

    private LayoutContainer content = new LayoutContainer();

    private ViewBean view;

    private Map<String, TypeModel> itemsPredicates = new HashMap<String, TypeModel>();

    private ListStore<BaseModel> list = new ListStore<BaseModel>();

    private ListStore<BaseModel> operatorlist = new ListStore<BaseModel>();

    private ListStore<BaseModel> valuelist = new ListStore<BaseModel>();

    private ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    public SimpleCriterionPanel(final MultipleCriteriaPanel ancestor, final Panel parent) {
        super();
        setSpacing(3);

        keyComboBox = new ComboBox<BaseModel>();
        keyComboBox.setAutoWidth(true);
        keyComboBox.setDisplayField("name"); //$NON-NLS-1$
        keyComboBox.setValueField("value"); //$NON-NLS-1$
        keyComboBox.setStore(list);
        keyComboBox.setTriggerAction(TriggerAction.ALL);
        keyComboBox.setId("SimpleKeyComboBox"); //$NON-NLS-1$

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
        operatorComboBox.setId("SimpleOperatorComboBox"); //$NON-NLS-1$
        add(operatorComboBox);

        add(content);

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
        content.removeAll();
        field = null;
        field = SearchFieldCreator.createField(itemsPredicates.get(getKey()));
        if (field != null) {
            field.setId("SimpleSearchValueFiled"); //$NON-NLS-1$
            if (field instanceof FKField)
                ((FKField) field).Update(getKey(), this);
            content.add(field);
        }
        setOperatorComboBox(SearchFieldCreator.cons);
        content.layout();
    }

    private String getKey() {
        return keyComboBox.getValue().get("value"); //$NON-NLS-1$
    }

    private String getOperator() {
        return operatorComboBox.getValue().get("value"); //$NON-NLS-1$
    }

    private String getValue() {

        if (field != null) {
            if (field instanceof FKField)
                return ((FKField) field).getValue().getId();
            else if (field instanceof DateField) {
                return ((DateField) field).getPropertyEditor().getFormat().format(((DateField) field).getValue());
            } else if (field instanceof RadioGroup) {
                return ((RadioGroup) field).getValue().getValueAttribute();
            } else if (field instanceof SimpleComboBox) {
                return ((SimpleComboBox) field).getValue().get("value"); //$NON-NLS-1$
            }
            return field.getValue().toString();
        }

        return null;
    }

    public SimpleCriterion getCriteria() {
        try {
            return new SimpleCriterion(getKey(), getOperator(), getValue());
        } catch (Exception e) {
            MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().advsearch_lessinfo(),
                    null);
            Log.error(e.getMessage(), e);
            return null;
        }
    }

    public void setCriterion(SimpleCriterion criterion) {
        try {
            keyComboBox.setValue(list.findModel("value", criterion.getKey())); //$NON-NLS-1$
            adaptOperatorAndValue();
            operatorComboBox.setValue(operatorlist.findModel("value", criterion.getOperator())); //$NON-NLS-1$
            if (field != null) {
                if (field instanceof DateField) {
                    ((DateField) field).setValue(new Date(criterion.getValue()));
                } else if (field instanceof SimpleComboBox) {
                    ((SimpleComboBox) field).setValue(((SimpleComboBox) field).findModel(criterion.getValue()));
                } else if (field instanceof RadioGroup) {
                    for (Field f : ((RadioGroup) field).getAll()) {
                        if (((Radio) f).getValueAttribute().equals(criterion.getValue())) {
                            ((Radio) f).setValue(true);
                            return;
                        }
                    }
                } else if (field instanceof FKField) {
                    ForeignKeyBean fk = new ForeignKeyBean();
                    fk.setId(criterion.getValue());
                    ((FKField) field).setValue(fk);
                } else {
                    field.setValue(criterion.getValue());
                }
            }
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
    }

    public void setCriteriaFK(ForeignKeyBean fk) {
        if (field != null) {
            if (field instanceof FKField) {
                ((FKField) field).setValue(fk);
            }
        }
    }

    public ViewBean getView() {
        return view;
    }

    public void setView(ViewBean view) {
        this.view = view;
    }

}

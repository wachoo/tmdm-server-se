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
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.FacetModel;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ComboBoxModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.mvc.BrowseRecordsView;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKey.FKPropertyEditor;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.BooleanField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.ComboBoxField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.ForeignKeyField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatDateField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatNumberField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatTextField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.validator.TextFieldValidator;
import org.talend.mdm.webapp.browserecords.client.widget.typefield.TypeFieldCreateContext;
import org.talend.mdm.webapp.browserecords.client.widget.typefield.TypeFieldCreator;
import org.talend.mdm.webapp.browserecords.client.widget.typefield.TypeFieldSource;
import org.talend.mdm.webapp.browserecords.client.widget.typefield.TypeFieldStyle;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.FacetEnum;

import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Widget;

public class TreeDetailGridFieldCreator {

    public static Field<?> createField(ItemNodeModel node, final TypeModel dataType, String language,
            Map<String, Field<?>> fieldMap, String operation, final ItemsDetailPanel itemsDetailPanel) {
        // Field

        Serializable value = node.getObjectValue();
        Field<?> field;
        boolean hasValue = value != null && !"".equals(value); //$NON-NLS-1$
        if (dataType.getForeignkey() != null) {
            ForeignKeyField fkField = new ForeignKeyField(dataType.getXpath(), dataType.getFkFilter(), dataType.getForeignkey(),
                    dataType.getForeignKeyInfo(), itemsDetailPanel);
            if (value instanceof ForeignKeyBean) {
                ForeignKeyBean fkBean = (ForeignKeyBean) value;
                if (fkBean != null) {
                    // fkBean.setDisplayInfo(fkBean.getId());
                    fkField.setValue(fkBean);
                    fkField.setPropertyEditor(new FKPropertyEditor());
                }
            }
            field = fkField;
        } else if (dataType.hasEnumeration()) {
            SimpleComboBox<String> comboBox = new SimpleComboBox<String>();
            comboBox.setFireChangeEventOnSetValue(true);
            if (dataType.getMinOccurs() > 0)
                comboBox.setAllowBlank(false);
            comboBox.setEditable(false);
            comboBox.setForceSelection(true);
            comboBox.setTriggerAction(TriggerAction.ALL);
            setEnumerationValues(dataType, comboBox);
            comboBox.setSimpleValue(hasValue ? value.toString() : ""); //$NON-NLS-1$
            field = comboBox;

        } else if (dataType instanceof ComplexTypeModel) {
            final ComboBoxField<ComboBoxModel> comboxField = new ComboBoxField<ComboBoxModel>();
            comboxField.setDisplayField("text"); //$NON-NLS-1$
            comboxField.setValueField("value"); //$NON-NLS-1$
            comboxField.setTypeAhead(true);
            comboxField.setTriggerAction(TriggerAction.ALL);

            // final ComplexTypeModel complexTypeModel = (ComplexTypeModel) dataType;
            List<ComplexTypeModel> reusableTypes = ((ComplexTypeModel) dataType).getReusableComplexTypes();
            ListStore<ComboBoxModel> comboxStore = new ListStore<ComboBoxModel>();
            comboxField.setStore(comboxStore);
            for (int i = 0; i < reusableTypes.size(); i++) {
                ComboBoxModel cbm;
                if (dataType.isAbstract() && i == 0) {
                    cbm = new ComboBoxModel(reusableTypes.get(i).getLabel(language),
                            "[" + MessagesFactory.getMessages().abstract_type() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    cbm = new ComboBoxModel(reusableTypes.get(i).getLabel(language), reusableTypes.get(i).getName());
                }
                cbm.set("reusableType", reusableTypes.get(i)); //$NON-NLS-1$
                comboxStore.add(cbm);
            }
            if (comboxStore.getCount() > 0) {
                comboxField.setValue(comboxStore.getAt(0));
            }

            if (node.getRealType() != null && node.getRealType().trim().length() > 0) {
                comboxField.setValue(comboxStore.findModel("value", node.getRealType())); //$NON-NLS-1$
            } else if (hasValue) {
                comboxField.setValue(comboxStore.findModel(value.toString(), value));
            }

            comboxField.addSelectionChangedListener(new SelectionChangedListener<ComboBoxModel>() {

                @Override
                public void selectionChanged(SelectionChangedEvent<ComboBoxModel> se) {
                    ComplexTypeModel reusableType = se.getSelectedItem().get("reusableType"); //$NON-NLS-1$
                    AppEvent app = new AppEvent(BrowseRecordsEvents.UpdatePolymorphism);
                    app.setData(reusableType);
                    app.setData(BrowseRecordsView.ITEMS_DETAIL_PANEL, itemsDetailPanel);
                    Dispatcher.forwardEvent(app);
                }

            });

            field = comboxField;
        } else {
            TypeFieldCreateContext context = new TypeFieldCreateContext(dataType);
            context.setLanguage(language);
            TypeFieldCreator typeFieldCreator = new TypeFieldCreator(new TypeFieldSource(TypeFieldSource.FORM_INPUT), context);
            Map<String, TypeFieldStyle> sytles = new HashMap<String, TypeFieldStyle>();
            sytles.put(TypeFieldStyle.ATTRI_WIDTH, new TypeFieldStyle(TypeFieldStyle.ATTRI_WIDTH, "400", TypeFieldStyle.SCOPE_BUILTIN_TYPEFIELD)); //$NON-NLS-1$
            field = typeFieldCreator.createFieldWithValueAndUpdateStyle(node, sytles);
        }

        field.setFieldLabel(dataType.getLabel(language));
        field.setName(dataType.getXpath());
        if (!dataType.getType().equals(DataTypeConstants.UUID) && !dataType.getType().equals(DataTypeConstants.AUTO_INCREMENT)) {
            field.setReadOnly(dataType.isReadOnly());
            field.setEnabled(!dataType.isReadOnly());
        }

        if (node.isKey() && hasValue && ItemDetailToolBar.DUPLICATE_OPERATION.equals(operation)) {
            field.setEnabled(true);
            field.setReadOnly(false);
        } else if (node.isKey() && hasValue && ItemDetailToolBar.CREATE_OPERATION.equals(operation)) {
            field.setEnabled(true);
            field.setReadOnly(false);
        } else if (node.isKey() && hasValue) {
            field.setEnabled(false);
            field.setReadOnly(true);
        }

        // facet set
        if (field instanceof TextField<?> && !(dataType instanceof ComplexTypeModel)) {
            buildFacets(dataType, field);

            String errorMsg = dataType.getFacetErrorMsgs().get(language);
            field.setData("facetErrorMsgs", errorMsg);//$NON-NLS-1$        
            FacetEnum.setFacetValue("maxOccurence", (Widget) field, String.valueOf(dataType.getMaxOccurs())); //$NON-NLS-1$

            if (((TextField<?>) field).getValidator() == null)
                ((TextField<?>) field).setValidator(TextFieldValidator.getInstance());

            if (errorMsg != null && !errorMsg.equals("")) //$NON-NLS-1$                
                ((TextField<?>) field).getMessages().setBlankText(errorMsg);

        }
        fieldMap.put(node.getId().toString(), field);
        updateMandatory(field, node, fieldMap);
        addFieldListener(field, node, fieldMap);
        return field;
    }

    public static Field<?> createField(ItemNodeModel node, final TypeModel dataType, String language,
            Map<String, Field<?>> fieldMap, final ItemsDetailPanel itemsDetailPanel) {
        return createField(node, dataType, language, fieldMap, null, itemsDetailPanel);
    }

    public static void deleteField(ItemNodeModel node, Map<String, Field<?>> fieldMap) {

        Field<?> updateField = fieldMap.get(node.getId().toString());
        node.setObjectValue(null);
        updateMandatory(updateField, node, fieldMap);
        fieldMap.remove(node.getId().toString());
    }

    private static void addFieldListener(final Field<?> field, final ItemNodeModel node, final Map<String, Field<?>> fieldMap) {

        field.addListener(Events.Change, new Listener<FieldEvent>() {

            @SuppressWarnings("rawtypes")
            public void handleEvent(FieldEvent fe) {
                if (fe.getField() instanceof ComboBoxField) {
                    node.setObjectValue(((ComboBoxModel) fe.getValue()).getValue());
                } else if (fe.getField() instanceof CheckBox) {
                    node.setObjectValue(fe.getValue().toString());
                } else {
                    node.setObjectValue(fe.getField() instanceof ComboBox ? ((SimpleComboValue) fe.getValue()).getValue()
                            .toString() : (Serializable) fe.getValue());
                }
                if (fe.getField() instanceof FormatDateField)
                    ((FormatDateField) field).setFormatedValue();
                
                node.setChangeValue(true);

                validate(fe.getField(), node);

                updateMandatory(field, node, fieldMap);
            }
        });

        field.addListener(Events.Attach, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent fe) {
                field.getElement().getStyle().setMarginRight(16D, Unit.PX);
                validate(field, node);
            }
        });

        field.addListener(Events.Invalid, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                final WidgetComponent errorIcon = getErrorIcon(field);
                errorIcon.el().removeStyleName("x-hide-visibility"); //$NON-NLS-1$
                errorIcon.setHideMode(HideMode.DISPLAY);
                errorIcon.el().dom.getStyle().setProperty("position", "absolute"); //$NON-NLS-1$//$NON-NLS-2$

                DeferredCommand.addCommand(new Command() {

                    public void execute() {
                        errorIcon.el().dom.getStyle().setMarginLeft(field.getWidth(), Unit.PX);
                        errorIcon.el().dom.getStyle().setMarginTop(-18, Unit.PX);
                        errorIcon.el().dom.getStyle().clearLeft();
                        errorIcon.el().dom.getStyle().clearTop();
                    }
                });
            }

        });

        field.addListener(Events.Blur, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent fe) {
                // TMDM-3353 only when node is valid, call setObjectValue(); otherwise objectValue is changed to
                // original value
                if (node.isValid())
                    if (fe.getField() instanceof FormatTextField) {
                        node.setObjectValue(((FormatTextField) fe.getField()).getOjbectValue());
                    } else if (fe.getField() instanceof FormatNumberField) {
                        node.setObjectValue(((FormatNumberField) fe.getField()).getOjbectValue());
                    } else if (fe.getField() instanceof FormatDateField) {
                        node.setObjectValue(((FormatDateField) fe.getField()).getOjbectValue());
                    }
            }
        });
    }

    private static native WidgetComponent getErrorIcon(Field<?> field)/*-{
        return field.@com.extjs.gxt.ui.client.widget.form.Field::errorIcon;
    }-*/;

    private static void buildFacets(TypeModel typeModel, Widget w) {
        if (typeModel instanceof SimpleTypeModel) {
            List<FacetModel> facets = ((SimpleTypeModel) typeModel).getFacets();
            for (FacetModel facet : facets) {
                FacetEnum.setFacetValue(facet.getName(), w, facet.getValue());
            }
        }
    }

    private static void setEnumerationValues(TypeModel typeModel, Widget w) {
        List<String> enumeration = ((SimpleTypeModel) typeModel).getEnumeration();
        if (enumeration != null && enumeration.size() > 0) {
            @SuppressWarnings("unchecked")
            SimpleComboBox<String> field = (SimpleComboBox<String>) w;
            for (String value : enumeration) {
                field.add(value);
            }
        }
    }

    public static void updateMandatory(Field<?> field, ItemNodeModel node, Map<String, Field<?>> fieldMap) {

        boolean flag = false;
        ItemNodeModel parent = (ItemNodeModel) node.getParent();
        if (parent != null && parent.getParent() != null && !parent.isMandatory()) {
            List<ModelData> childs = parent.getChildren();
            for (int i = 0; i < childs.size(); i++) {
                ItemNodeModel child = (ItemNodeModel) childs.get(i);
                if (child.getObjectValue() != null && !"".equals(child.getObjectValue())) { //$NON-NLS-1$
                    flag = true;
                    break;
                }
            }

            for (int i = 0; i < childs.size(); i++) {
                ItemNodeModel mandatoryNode = (ItemNodeModel) childs.get(i);
                Field<?> updateField = fieldMap.get(mandatoryNode.getId().toString());
                if (updateField != null && mandatoryNode.isMandatory()) {
                    setMandatory(updateField, flag ? mandatoryNode.isMandatory() : !mandatoryNode.isMandatory());
                    mandatoryNode.setValid(updateField.validate());
                }
            }

        } else {
            setMandatory(field, node.isMandatory());
        }
    }

    @SuppressWarnings("rawtypes")
    private static void setMandatory(Field<?> field, boolean mandatory) {
        if (field instanceof NumberField) {
            ((NumberField) field).setAllowBlank(!mandatory);
        } else if (field instanceof BooleanField) {
            ((BooleanField) field).setAllowBlank(!mandatory);
        } else if (field instanceof DateField) {
            ((DateField) field).setAllowBlank(!mandatory);
        } else if (field instanceof TextField) {
            ((TextField) field).setAllowBlank(!mandatory);
        }
    }

    private static void validate(Field<?> field, ItemNodeModel node) {
        node.setValid(field.isValid());
    }
}

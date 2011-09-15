package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.io.Serializable;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.model.ComboBoxModel;
import org.talend.mdm.webapp.browserecords.client.model.DataTypeConstants;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.DateUtil;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.BooleanField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.ComboBoxField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.ForeignKeyField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.PictureField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.UrlField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.validator.NumberFieldValidator;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.validator.TextFieldValidator;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.FacetEnum;
import org.talend.mdm.webapp.browserecords.shared.FacetModel;
import org.talend.mdm.webapp.browserecords.shared.SimpleTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Widget;

public class TreeDetailGridFieldCreator {

    public static Field<?> createField(ItemNodeModel node, final TypeModel dataType, String language) {
        Serializable value = node.getObjectValue();
        Field<?> field;
        boolean hasValue = value != null && !"".equals(value); //$NON-NLS-1$
        if (dataType.getForeignkey() != null) {
            ForeignKeyField fkField = new ForeignKeyField(dataType.getForeignkey(), dataType.getForeignKeyInfo());
            ForeignKeyBean fkBean = (ForeignKeyBean) value;
            if (fkBean != null) {
                fkField.setValue(fkBean);
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
        } else if (dataType.getType().equals(DataTypeConstants.UUID)) {
            TextField<String> uuidField = new TextField<String>();
            uuidField.setEnabled(false);
            uuidField.setReadOnly(true);
            if (hasValue)
                uuidField.setValue(value.toString());
            field = uuidField;
        } else if (dataType.getType().equals(DataTypeConstants.AUTO_INCREMENT)) {
            TextField<String> autoIncrementField = new TextField<String>();
            autoIncrementField.setEnabled(false);
            autoIncrementField.setReadOnly(true);
            if (hasValue)
                autoIncrementField.setValue(value.toString());
            field = autoIncrementField;
        } else if (dataType.getType().equals(DataTypeConstants.PICTURE)) {
            PictureField pictureField = new PictureField();
            pictureField.setValue(hasValue ? value.toString() : ""); //$NON-NLS-1$
            field = pictureField;
        } else if (dataType.getType().equals(DataTypeConstants.URL)) {
            UrlField urlField = new UrlField();
            urlField.setFieldLabel(dataType.getLabel(language));
            urlField.setValue(hasValue ? value.toString() : ""); //$NON-NLS-1$
            field = urlField;
        } else if (dataType instanceof ComplexTypeModel) {
            final ComboBoxField<ComboBoxModel> comboxField = new ComboBoxField<ComboBoxModel>();
            comboxField.setDisplayField("value"); //$NON-NLS-1$
            comboxField.setValueField("value"); //$NON-NLS-1$
            comboxField.setTypeAhead(true);
            comboxField.setTriggerAction(TriggerAction.ALL);

            // final ComplexTypeModel complexTypeModel = (ComplexTypeModel) dataType;
            List<TypeModel> reusableTypes = ((ComplexTypeModel) dataType).getReusableComplexTypes();
            ListStore<ComboBoxModel> comboxStore = new ListStore<ComboBoxModel>();
            comboxField.setStore(comboxStore);
            for (TypeModel subType : reusableTypes) {
                comboxStore.add(new ComboBoxModel(subType.getName(), subType.getName()));
            }
            if (comboxStore.getCount() > 0) {
                comboxField.setValue(comboxStore.getAt(0));
            }
            comboxField.addSelectionChangedListener(new SelectionChangedListener<ComboBoxModel>() {

                @Override
                public void selectionChanged(SelectionChangedEvent<ComboBoxModel> se) {
                    String value = comboxField.getValue().getText();
                    dataType.setRealType(value);
                    Dispatcher.forwardEvent(BrowseRecordsEvents.UpdatePolymorphism, dataType);
                }

            });
            if (hasValue) {
                comboxField.setValue(comboxStore.findModel(value.toString(), value));
            }

            field = comboxField;
        } else {
            field = createCustomField(value, dataType, language);
        }

        field.setFieldLabel(dataType.getLabel(language));
        field.setName(dataType.getXpath());
        if (!dataType.getType().equals(DataTypeConstants.UUID) && !dataType.getType().equals(DataTypeConstants.AUTO_INCREMENT)) {
            field.setReadOnly(dataType.isReadOnly());
            field.setEnabled(!dataType.isReadOnly());
        }

        addFieldListener(field, node);
        return field;
    }

    @SuppressWarnings("serial")
    public static Field<?> createCustomField(Serializable value, TypeModel dataType, String language) {
        Field<?> field;
        boolean hasValue = value != null && !"".equals(value); //$NON-NLS-1$
        String baseType = dataType.getType().getBaseTypeName();
        if (DataTypeConstants.INTEGER.getTypeName().equals(baseType) || DataTypeConstants.INT.getTypeName().equals(baseType)
                || DataTypeConstants.LONG.getTypeName().equals(baseType)) {
            NumberField numberField = new NumberField();
            numberField.setData("numberType", "integer");//$NON-NLS-1$ //$NON-NLS-2$
            numberField.setPropertyEditorType(Integer.class);
            numberField.setValidator(NumberFieldValidator.getInstance());
            numberField.setValue((hasValue ? Long.parseLong(value.toString()) : null));
            field = numberField;
        } else if (DataTypeConstants.FLOAT.getTypeName().equals(baseType)
                || DataTypeConstants.DOUBLE.getTypeName().equals(baseType)) {
            NumberField numberField = new NumberField();
            numberField.setData("numberType", "double");//$NON-NLS-1$ //$NON-NLS-2$
            numberField.setPropertyEditorType(Double.class);
            numberField.setValidator(NumberFieldValidator.getInstance());
            if (DataTypeConstants.DOUBLE.getTypeName().equals(baseType))
                numberField.setValue((hasValue ? Double.parseDouble(value.toString()) : null));
            else
                numberField.setValue((hasValue ? Float.parseFloat(value.toString()) : null));
            field = numberField;
        } else if (DataTypeConstants.DECIMAL.getTypeName().equals(baseType)) {
            NumberField numberField = new NumberField();
            numberField.setData("numberType", "decimal");//$NON-NLS-1$ //$NON-NLS-2$
            numberField.setValidator(NumberFieldValidator.getInstance());
            numberField.setPropertyEditorType(Double.class);
            // NumberFormat nf = NumberFormat.getDecimalFormat();
            numberField.setValue((hasValue ? Double.parseDouble(value.toString()) : null));
            field = numberField;
        } else if (DataTypeConstants.BOOLEAN.getTypeName().equals(baseType)) {
            BooleanField booleanField = new BooleanField();
            booleanField.setDisplayField("text");//$NON-NLS-1$
            SimpleComboValue<Boolean> trueValue = new SimpleComboValue<Boolean>() {

                {
                    this.setValue(true);
                    this.set("text", "TRUE");}};//$NON-NLS-1$ //$NON-NLS-2$
            booleanField.getStore().add(trueValue);
            SimpleComboValue<Boolean> falseValue = new SimpleComboValue<Boolean>() {

                {
                    this.setValue(false);
                    this.set("text", "FALSE");}};//$NON-NLS-1$ //$NON-NLS-2$
            booleanField.getStore().add(falseValue);
            if (hasValue)
                booleanField.setValue((value.toString().equals("true") || value.equals(true)) ? trueValue : falseValue); //$NON-NLS-1$
            field = booleanField;
        } else if (DataTypeConstants.DATE.getTypeName().equals(baseType)) {
            DateField dateField = new DateField();
            if (hasValue)
                dateField.setValue(DateUtil.convertStringToDate(value.toString()));
            dateField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));//$NON-NLS-1$
            field = dateField;
        } else if (DataTypeConstants.DATETIME.getTypeName().equals(baseType)) {
            DateField dateTimeField = new DateField();
            dateTimeField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd HH:mm:ss"));//$NON-NLS-1$
            if (hasValue)
                dateTimeField.setValue(DateUtil.convertStringToDate(DateUtil.dateTimePattern, value.toString()));
            field = dateTimeField;
        } else if (DataTypeConstants.STRING.getTypeName().equals(baseType)) {
            TextField<String> textField = new TextField<String>();
            textField.setValidator(TextFieldValidator.getInstance());
            textField.setValue(hasValue ? value.toString() : ""); //$NON-NLS-1$
            field = textField;
        } else {
            TextField<String> textField = new TextField<String>();
            textField.setValue(hasValue ? value.toString() : ""); //$NON-NLS-1$
            textField.setValidator(TextFieldValidator.getInstance());
            field = textField;
            textField.setMessages(null);
        }

        field.setWidth(400);
        field.setData("facetErrorMsgs", dataType.getFacetErrorMsgs().get(language));//$NON-NLS-1$
        buildFacets(dataType, field);
        return field;
    }

    private static void addFieldListener(final Field<?> field, final ItemNodeModel node) {
        field.addListener(Events.Change, new Listener<FieldEvent>() {


            @SuppressWarnings("rawtypes")
            public void handleEvent(FieldEvent fe) {

                node.setObjectValue(field instanceof ComboBox ? ((SimpleComboValue) fe.getValue()).getValue().toString()
                        : (Serializable) fe.getValue());
            }
        });

    }

    private static void buildFacets(TypeModel typeModel, Widget w) {
        List<FacetModel> facets = ((SimpleTypeModel) typeModel).getFacets();
        for (FacetModel facet : facets) {
            FacetEnum.setFacetValue(facet.getName(), w, facet.getValue());
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
}

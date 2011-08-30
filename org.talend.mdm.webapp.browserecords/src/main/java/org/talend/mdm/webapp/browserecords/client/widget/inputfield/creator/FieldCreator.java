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
package org.talend.mdm.webapp.browserecords.client.widget.inputfield.creator;

import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.DataTypeConstants;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKey.FKField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.BooleanField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.MultipleField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.PictureField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.UrlField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.converter.BooleanConverter;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.converter.DateConverter;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.converter.DateTimeConverter;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.converter.FKConverter;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.converter.NumberConverter;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.validator.NumberFieldValidator;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.validator.TextFieldValidator;
import org.talend.mdm.webapp.browserecords.shared.FacetEnum;
import org.talend.mdm.webapp.browserecords.shared.FacetModel;
import org.talend.mdm.webapp.browserecords.shared.SimpleTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.user.client.ui.Widget;

public class FieldCreator {

    public static Field<?> createField(SimpleTypeModel dataType, FormBinding formBindings, boolean enableMultiple, String language) {
        Field<?> field;

        if (dataType.isMultiOccurrence() && enableMultiple) {
            MultipleField multipleField = new MultipleField(dataType, language);
            field = multipleField;
        } else if (dataType.getForeignkey() != null) {
            FKField fkField = new FKField();
            fkField.Update(dataType.getXpath(), fkField);
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
            field = comboBox;
        } else if (dataType.getType().equals(DataTypeConstants.UUID)) {
            TextField<String> uuidField = new TextField<String>();
            uuidField.setEnabled(false);
            field = uuidField;
        } else if (dataType.getType().equals(DataTypeConstants.AUTO_INCREMENT)) {
            TextField<String> autoIncrementField = new TextField<String>();
            autoIncrementField.setEnabled(false);
            field = autoIncrementField;
        } else if (dataType.getType().equals(DataTypeConstants.PICTURE)) {
            PictureField pictureField = new PictureField();
            field = pictureField;
        } else if (dataType.getType().equals(DataTypeConstants.URL)) {
            UrlField urlField = new UrlField();
            urlField.setFieldLabel(dataType.getLabel(language));
            field = urlField;
        } else  {
            field = createCustomField(dataType, language);
        }

        field.setFieldLabel(dataType.getLabel(language));
        field.setName(dataType.getXpath());

        if (formBindings != null) {
            FieldBinding binding = null;
            if (field instanceof SimpleComboBox) {
                binding = new SimpleComboBoxFieldBinding((SimpleComboBox) field, field.getName());
                String baseType = dataType.getType().getBaseTypeName();
                if (DataTypeConstants.BOOLEAN.getTypeName().equals(baseType)){
                    binding.setConverter(new BooleanConverter());
                }
            } else if (field instanceof DateField) {
                binding = new FieldBinding(field, field.getName());
                binding.setConverter(new DateConverter());
            } else if (field instanceof DateField) {
                binding = new FieldBinding(field, field.getName());
                binding.setConverter(new DateTimeConverter());
            } else if (field instanceof NumberField){
                binding = new FieldBinding(field, field.getName());
                binding.setConverter(new NumberConverter(field));
            } else if (field instanceof FKField){
                binding = new FieldBinding(field, field.getName());
                binding.setConverter(new FKConverter());
            } else {
                binding = new FieldBinding(field, field.getName());
            }
            formBindings.addFieldBinding(binding);
        }

        
        field.setReadOnly(dataType.isReadOnly());
        field.setEnabled(!dataType.isReadOnly());
        
        return field;
    }

    public static Field<?> createCustomField(SimpleTypeModel dataType, String language){
        Field<?> field;
        String baseType = dataType.getType().getBaseTypeName(); 
        if (DataTypeConstants.INTEGER.getTypeName().equals(baseType) ||
                    DataTypeConstants.INT.getTypeName().equals(baseType)||
                    DataTypeConstants.LONG.getTypeName().equals(baseType)){
            NumberField numberField = new NumberField();
            numberField.setData("numberType", "integer");//$NON-NLS-1$ //$NON-NLS-2$
            numberField.setPropertyEditorType(Integer.class);
            numberField.setValidator(NumberFieldValidator.getInstance());
            field = numberField;
        } else if (DataTypeConstants.FLOAT.getTypeName().equals(baseType) ||
                    DataTypeConstants.DOUBLE.getTypeName().equals(baseType)){
            NumberField numberField = new NumberField();
            numberField.setData("numberType", "double");//$NON-NLS-1$ //$NON-NLS-2$
            numberField.setPropertyEditorType(Double.class);
            numberField.setValidator(NumberFieldValidator.getInstance());
            field = numberField;
        } else if (DataTypeConstants.DECIMAL.getTypeName().equals(baseType)){
            NumberField numberField = new NumberField();
            numberField.setData("numberType", "decimal");//$NON-NLS-1$ //$NON-NLS-2$
            numberField.setValidator(NumberFieldValidator.getInstance());
            numberField.setPropertyEditorType(Double.class);
            field = numberField;
        } else if (DataTypeConstants.BOOLEAN.getTypeName().equals(baseType)){
            BooleanField booleanField = new BooleanField();
            booleanField.setDisplayField("text");//$NON-NLS-1$
            booleanField.getStore().add(new SimpleComboValue<Boolean>(){{this.setValue(true);this.set("text", "TRUE");}});//$NON-NLS-1$ //$NON-NLS-2$
            booleanField.getStore().add(new SimpleComboValue<Boolean>(){{this.setValue(false);this.set("text", "FALSE");}});//$NON-NLS-1$ //$NON-NLS-2$
            field = booleanField;
        } else if (DataTypeConstants.DATE.getTypeName().equals(baseType)){
            DateField dateField = new DateField();
            dateField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));//$NON-NLS-1$
            field = dateField;
        } else if (DataTypeConstants.DATETIME.getTypeName().equals(baseType)){
            DateField dateTimeField = new DateField();
            dateTimeField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd HH:mm:ss"));//$NON-NLS-1$
            field = dateTimeField;
        } else if (DataTypeConstants.STRING.getTypeName().equals(baseType)){
            TextField<String> textField = new TextField<String>();
            textField.setValidator(TextFieldValidator.getInstance());
            field = textField;
        } else {
            TextField<String> textField = new TextField<String>();
            textField.setValidator(TextFieldValidator.getInstance());
            field = textField;textField.setMessages(null);
        }
        field.setWidth(400);
        field.setData("facetErrorMsgs", dataType.getFacetErrorMsgs().get(language));//$NON-NLS-1$
        buildFacets(dataType, field);
        return field;
    }
    
    private static void buildFacets(TypeModel typeModel, Widget w) {
        List<FacetModel> facets = ((SimpleTypeModel) typeModel).getFacets();
        for (FacetModel facet : facets) {
            FacetEnum.setFacetValue(facet.getName(), w, facet.getValue());
        }
    }

    private static void setEnumerationValues(TypeModel typeModel, Widget w){
        List<String> enumeration = ((SimpleTypeModel) typeModel).getEnumeration();
        if (enumeration != null && enumeration.size() > 0){
            SimpleComboBox<String> field = (SimpleComboBox<String>) w;
            for (String value : enumeration){
                field.add(value);
            }
        }
    }
}

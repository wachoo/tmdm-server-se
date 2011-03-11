// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.creator;

import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.FKField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.MultipleField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.PictureField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.UrlField;
import org.talend.mdm.webapp.itemsbrowser2.shared.ComplexTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetEnum;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.ui.Widget;

public class FieldCreator {

    public static Field createField(SimpleTypeModel dataType, FormBinding formBindings, boolean enableMultiple) {
        Field field = null;

        if (dataType.isMultiple() && enableMultiple){
            MultipleField multipleField = new MultipleField(dataType);
            field = multipleField;
        } else if (dataType.hasEnumeration()) {
            SimpleComboBox<String> comboBox = new SimpleComboBox<String>();
            comboBox.setFireChangeEventOnSetValue(true);
            if (dataType.getMinOccurs() > 0)
                comboBox.setAllowBlank(false);
            comboBox.setEditable(false);
            comboBox.setForceSelection(true);
            comboBox.setTriggerAction(TriggerAction.ALL);
            buildFacets(dataType, comboBox);
            field = comboBox;
        } else if (dataType.getForeignkey() != null) {
            FKField fkField = new FKField();
            field = fkField;
        } else if (dataType.getTypeName().equals(DataTypeConstants.STRING)) {
            TextField<String> textField = new TextField<String>();
            buildFacets(dataType, textField);
            if (dataType.getMinOccurs() > 0)
                textField.setAllowBlank(false);
            field = textField;

        } else if (dataType.getTypeName().equals(DataTypeConstants.DECIMAL)) {
            NumberField numberField = new NumberField();
            numberField.setValidator(validator);
            buildFacets(dataType, numberField);
            field = numberField;
        } else if (dataType.getTypeName().equals(DataTypeConstants.UUID)) {

        } else if (dataType.getTypeName().equals(DataTypeConstants.AUTO_INCREMENT)) {

        } else if (dataType.getTypeName().equals(DataTypeConstants.PICTURE)) {
            PictureField pictureField = new PictureField();
            field =  pictureField;
        } else if (dataType.getTypeName().equals(DataTypeConstants.URL)) {
            UrlField urlField = new UrlField();
            urlField.setFieldLabel(dataType.getLabel());
            field =  urlField;
        } else if (dataType.getTypeName().equals(DataTypeConstants.DATE)) {
            DateField dateField = new DateField();
            dateField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));//$NON-NLS-1$
            if (dataType.getMinOccurs() > 0)
                dateField.setAllowBlank(false);
            field =  dateField;
        } else if (dataType instanceof SimpleTypeModel) {
            TextField<String> textField = new TextField<String>();
            buildFacets(dataType, textField);
            field =  textField;
        }


        field.setFieldLabel(dataType.getLabel());
        field.setName(dataType.getXpath());
        
        if (formBindings != null){
            FieldBinding binding = null;
            if (field instanceof SimpleComboBox){
                binding = new SimpleComboBoxFieldBinding((SimpleComboBox) field, field.getName());
            } else {
                binding = new FieldBinding(field, field.getName());
            }
            formBindings.addFieldBinding(binding);
        }

        return field;
    }

    private static void buildFacets(TypeModel typeModel, Widget w) {
        List<FacetModel> facets = ((SimpleTypeModel) typeModel).getFacets();
        for (FacetModel facet : facets) {
            FacetEnum.setFacetValue(facet.getName(), w, facet.getValue());
        }
    }
    
    static Validator validator = new Validator() {

        public String validate(Field<?> field, String value) {
            String msg = "";
            String totalDigits = field.getElement().getAttribute(FacetEnum.TOTAL_DIGITS.getFacetName());
            if (totalDigits != null && !totalDigits.equals("")) {
                if (value.replace(".", "").length() > Integer.parseInt(totalDigits)) {

                    msg += MessagesFactory.getMessages().check_totalDigits() + totalDigits + "\n";
                }
            }

            String fractionDigits = field.getElement().getAttribute(FacetEnum.FRACTION_DIGITS.getFacetName());
            if (fractionDigits != null && !fractionDigits.equals("")) {
                String[] digits = value.split(".");
                if (digits[1].length() > Integer.parseInt(fractionDigits)) {
                    msg += MessagesFactory.getMessages().check_fractionDigits() + fractionDigits;
                }
            }
            if (msg.length() > 0)
                return msg;
            else
                return null;
        }
    };
}

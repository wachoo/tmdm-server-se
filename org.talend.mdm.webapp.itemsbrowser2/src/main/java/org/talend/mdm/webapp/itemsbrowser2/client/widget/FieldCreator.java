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
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.io.Serializable;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.FKField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.PictureField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.UrlField;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetEnum;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.user.client.ui.Widget;

public class FieldCreator {

    public static <D extends Serializable, F extends Field<D>> F createField(TypeModel dataType) {
        F field = null;

        if (dataType.hasEnumeration()) {
            SimpleComboBox<String> comboBox = new SimpleComboBox<String>();
            comboBox.setFireChangeEventOnSetValue(true);
            comboBox.setAllowBlank(false);
            comboBox.setEditable(false);
            comboBox.setForceSelection(true);
            comboBox.setTriggerAction(TriggerAction.ALL);
            buildFacets(dataType, comboBox);
            field = (F) comboBox;
        } else if (dataType.getForeignkey() != null) {
            FKField fkField = new FKField();
            field = (F) fkField;
        } else if (dataType.getTypeName().equals(DataTypeConstants.STRING)) {
            TextField<String> textField = new TextField<String>();
            buildFacets(dataType, textField);
            field = (F) textField;
        } else if (dataType.getTypeName().equals(DataTypeConstants.DECIMAL)) {
            NumberField numberField = new NumberField();
            numberField.setValidator(validator);

            buildFacets(dataType, numberField);
            field = (F) numberField;
        } else if (dataType.getTypeName().equals(DataTypeConstants.UUID)) {

        } else if (dataType.getTypeName().equals(DataTypeConstants.AUTO_INCREMENT)) {

        } else if (dataType.getTypeName().equals(DataTypeConstants.PICTURE)) {
            PictureField pictureField = new PictureField();
            field = (F) pictureField;
        } else if (dataType.getTypeName().equals(DataTypeConstants.URL)) {
            UrlField urlField = new UrlField();
            field = (F) urlField;
        } else if (dataType instanceof SimpleTypeModel) {
            TextField<String> textField = new TextField<String>();
            buildFacets(dataType, textField);
            field = (F) textField;
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

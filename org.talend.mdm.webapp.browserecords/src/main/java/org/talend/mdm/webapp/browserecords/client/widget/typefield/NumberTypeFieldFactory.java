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
package org.talend.mdm.webapp.browserecords.client.widget.typefield;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.browserecords.client.model.OperatorConstants;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatNumberField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.validator.NumberFieldValidator;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;


public class NumberTypeFieldFactory extends TypeFieldFactory {


    public NumberTypeFieldFactory() {

    }

    public NumberTypeFieldFactory(TypeFieldSource source, TypeFieldCreateContext context) {
        super(source, context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.base.client.widget.typefield.TypeFieldFactory#createField()
     */
    @Override
    public Field<?> createField() {

        FormatNumberField numberField = new FormatNumberField();

        if (!isEmpty(displayformatPattern))
            numberField.setFormatPattern(displayformatPattern);

        numberField.setValidator(NumberFieldValidator.getInstance());

        //exceptions
        if (DataTypeConstants.FLOAT.getTypeName().equals(baseType)
           || DataTypeConstants.DOUBLE.getTypeName().equals(baseType)
                || DataTypeConstants.DECIMAL.getTypeName().equals(baseType)) {

            numberField.setData("numberType", DataTypeConstants.DECIMAL.getTypeName().equals(baseType) ? "decimal" : "double");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            numberField.setPropertyEditorType(Double.class);
            
        } else {

            numberField.setData("numberType", "integer");//$NON-NLS-1$ //$NON-NLS-2$
            numberField.setPropertyEditorType(Integer.class);

        }

        if (context.isWithValue()) {

            Number toSetValue = null;
            if(hasValue()){
                if (DataTypeConstants.FLOAT.getTypeName().equals(baseType))
                    toSetValue = Float.parseFloat(getValue().toString());
                else if (DataTypeConstants.DECIMAL.getTypeName().equals(baseType)
                        || DataTypeConstants.DOUBLE.getTypeName().equals(baseType))
                    toSetValue = Double.parseDouble(getValue().toString());
                else
                    toSetValue = Long.parseLong(getValue().toString());
            }

            numberField.setValue(toSetValue);
        }

        return numberField;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.browserecords.client.widget.typefield.TypeFieldFactory#createSearchField()
     */
    @Override
    public Field<?> createSearchField() {
        Field<?> field = null;
        if (baseType.equals(DataTypeConstants.INT.getBaseTypeName())
                || baseType.equals(DataTypeConstants.INTEGER.getBaseTypeName())
                || baseType.equals(DataTypeConstants.LONG.getBaseTypeName())
                || baseType.equals(DataTypeConstants.SHORT.getBaseTypeName())) {

            TextField<String> textField = new TextField<String>();
            textField.setWidth(80);
            textField.setValue("*"); //$NON-NLS-1$
            field = textField;
            source.setOperatorMap(OperatorConstants.numOperators);
        } else if (baseType.equals(DataTypeConstants.DOUBLE.getBaseTypeName())
                || baseType.equals(DataTypeConstants.DECIMAL.getBaseTypeName())
                || baseType.equals(DataTypeConstants.FLOAT.getBaseTypeName())) {
            TextField<String> textField = new TextField<String>();
            textField.setWidth(80);
            textField.setValue("*"); //$NON-NLS-1$
            field = textField;
            source.setOperatorMap(OperatorConstants.numOperators);
        } else {
            field = genTextSearchField();
        }
        return field;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.browserecords.client.widget.typefield.TypeFieldFactory#updateStyle()
     */
    @Override
    public void updateStyle(Field<?> field) {
        updateBuiltInTypeFiledsStyle(field);
    }


}

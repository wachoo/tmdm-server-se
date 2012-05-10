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

import java.util.Date;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.browserecords.client.model.OperatorConstants;
import org.talend.mdm.webapp.browserecords.client.util.DateUtil;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatDateField;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;


public class DateTimeTypeFieldFactory extends TypeFieldFactory {

    public DateTimeTypeFieldFactory() {

    }

    public DateTimeTypeFieldFactory(TypeFieldSource source, TypeFieldCreateContext context) {
        super(source, context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.base.client.widget.typefield.TypeFieldFactory#createField()
     */
    @Override
    public Field<?> createField() {

        Field<?> field = null;

        if (DataTypeConstants.DATE.getTypeName().equals(baseType)) {

            FormatDateField dateField = new FormatDateField(context.getNode());
            if (!isEmpty(displayformatPattern)) {               
                dateField.setFormatPattern(displayformatPattern);
                dateField.setShowFormateValue(true);
            }
            dateField.setPropertyEditor(new DateTimePropertyEditor(DateUtil.datePattern));

            if (context.isWithValue() && hasValue()) {
                Date d = DateUtil.convertStringToDate(getValue().toString());
                dateField.setValue(hasValue() ? d : null);
                dateField.setDate(d);
                if (!isEmpty(displayformatPattern)) {
                    dateField.setFormatedValue();
                }
            }

            field = dateField;

        } else if (DataTypeConstants.DATETIME.getTypeName().equals(baseType)) {

            FormatDateField dateField = new FormatDateField(context.getNode());
            if (!isEmpty(displayformatPattern)) {
                dateField.setFormatPattern(displayformatPattern);
                dateField.setShowFormateValue(true);
            }
            dateField.setPropertyEditor(new DateTimePropertyEditor(DateUtil.formatDateTimePattern));

            if (context.isWithValue() && hasValue()) {
                Date d = DateUtil.convertStringToDate(DateUtil.dateTimePattern, getValue().toString());
                dateField.setValue(hasValue() ? d : null);
                dateField.setDate(d);
                if (!isEmpty(displayformatPattern)) {
                    dateField.setFormatedValue();
                }
            }

            field = dateField;

        } else if (DataTypeConstants.DURATION.getTypeName().equals(baseType)
                || DataTypeConstants.TIME.getTypeName().equals(baseType)
                || DataTypeConstants.GYEARMONTH.getTypeName().equals(baseType)
                || DataTypeConstants.GYEAR.getTypeName().equals(baseType)
                || DataTypeConstants.GDAY.getTypeName().equals(baseType)
                || DataTypeConstants.GMONTH.getTypeName().equals(baseType)) {
            // TODO have a time widget
            field = genFormatTextField();
        }

        return field;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.browserecords.client.widget.typefield.TypeFieldFactory#createSearchField()
     */
    @Override
    public Field<?> createSearchField() {
        Field<?> field = null;
        if (baseType.equals(DataTypeConstants.DATE.getBaseTypeName())) {
            DateField dateField = new DateField();
            dateField.setMessageTarget("tooltip");//$NON-NLS-1$
            dateField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));//$NON-NLS-1$            
            field = dateField;
            source.setOperatorMap(OperatorConstants.dateOperators);
        } else if (baseType.equals(DataTypeConstants.DATETIME.getBaseTypeName())) {
            DateField dateField = new DateField();
            dateField.setPropertyEditor(new DateTimePropertyEditor(DateUtil.formatDateTimePattern));
            field = dateField;
            source.setOperatorMap(OperatorConstants.dateOperators);
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

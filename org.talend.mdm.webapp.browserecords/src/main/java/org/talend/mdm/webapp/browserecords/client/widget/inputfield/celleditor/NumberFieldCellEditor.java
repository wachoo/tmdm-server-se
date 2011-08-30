package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;


public class NumberFieldCellEditor extends CellEditor {

    public NumberFieldCellEditor(NumberField field) {
        super(field);
    }
    public Object preProcessValue(Object value) {
        if (value == null) return null;
        String numberType = getField().getData("numberType");//$NON-NLS-1$
        if ("integer".equals(numberType)){//$NON-NLS-1$
            return Integer.parseInt((String)value);
        } else {
            return Double.parseDouble((String)value);
        }
    }

    public Object postProcessValue(Object value) {
        if (value == null) return null;
        String numberType = getField().getData("numberType");//$NON-NLS-1$
        if ("integer".equals(numberType)){//$NON-NLS-1$
            Integer v = (Integer) value;
            return Integer.toString(v.intValue());
        } else {
            Double v = (Double) value;
            return Double.toString(v);
        }
    }
}

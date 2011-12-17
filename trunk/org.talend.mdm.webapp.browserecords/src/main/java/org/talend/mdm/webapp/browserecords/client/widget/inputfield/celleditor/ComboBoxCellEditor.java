package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;

public class ComboBoxCellEditor extends CellEditor {

    public ComboBoxCellEditor(SimpleComboBox field) {
        super(field);
    }

    public Object preProcessValue(Object value) {
        if (value == null) {
            return null;
        }
        return ((SimpleComboBox) getField()).findModel(value.toString());
    }

    public Object postProcessValue(Object value) {
        if (value == null) {
            return "";//$NON-NLS-1$
        }
        Object v = ((ModelData) value).get("value");//$NON-NLS-1$
        if (v == null){
            return "";//$NON-NLS-1$
        } else {
            return v;
        }
    }
}

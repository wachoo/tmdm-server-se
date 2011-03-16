package org.talend.mdm.webapp.itemsbrowser2.client.creator;

import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor.ComboBoxCellEditor;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor.DateFieldCellEditor;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.google.gwt.user.client.ui.Widget;


public class CellEditorCreator {

    public static CellEditor createCellEditor(Widget field){
        if (field instanceof SimpleComboBox){
            final SimpleComboBox comboBox = (SimpleComboBox) field;
            return new ComboBoxCellEditor(comboBox);
        }
        
        if (field instanceof DateField){
            return new DateFieldCellEditor((DateField) field);
        }
        
        if (field instanceof Field){
            return new CellEditor((Field<? extends Object>) field);
        }
        
        return null;
        
    }
}

package org.talend.mdm.webapp.itemsbrowser2.client.creator;

import org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey.FKField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.BooleanField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.DateTimeField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor.BooleanFieldCellEditor;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor.ComboBoxCellEditor;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor.DateFieldCellEditor;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor.DateTimeFieldCellEditor;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor.FKCellEditor;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor.NumberFieldCellEditor;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.google.gwt.user.client.ui.Widget;


public class CellEditorCreator {

    public static CellEditor createCellEditor(Widget field){
        if (field instanceof SimpleComboBox){
            if (field instanceof BooleanField){
                return new BooleanFieldCellEditor((BooleanField) field);
            } else {
                return new ComboBoxCellEditor((SimpleComboBox) field);    
            }
        }
        
        if (field instanceof DateField){
            return new DateFieldCellEditor((DateField) field);
        }
        
        if (field instanceof DateTimeField){
            return new DateTimeFieldCellEditor((DateTimeField) field);
        }
        if (field instanceof NumberField){
            return new NumberFieldCellEditor((NumberField) field);
        }
        
        if (field instanceof FKField){
            return new FKCellEditor((FKField) field);
        }
        
        if (field instanceof Field){
            return new CellEditor((Field<? extends Object>) field);
        }
        
        return null;
        
    }
}

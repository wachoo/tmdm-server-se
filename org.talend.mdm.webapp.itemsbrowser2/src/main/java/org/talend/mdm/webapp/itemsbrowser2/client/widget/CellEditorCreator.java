package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.google.gwt.user.client.ui.Widget;


public class CellEditorCreator {

    public static CellEditor createCellEditor(Widget field){
        if (field instanceof SimpleComboBox){
            final SimpleComboBox comboBox = (SimpleComboBox) field;
            CellEditor editor = new CellEditor(comboBox) {
                public Object preProcessValue(Object value) {  
                  if (value == null) {  
                    return value;  
                  }  
                  return comboBox.findModel(value.toString());  
                }  
                public Object postProcessValue(Object value) {  
                  if (value == null) {  
                    return value;  
                  }  
                  return ((ModelData) value).get("value");  
                }  
              };
              return editor;
        }
        
        if (field instanceof Field){
            return new CellEditor((Field<? extends Object>) field);
        }
        
        return null;
        
    }
}

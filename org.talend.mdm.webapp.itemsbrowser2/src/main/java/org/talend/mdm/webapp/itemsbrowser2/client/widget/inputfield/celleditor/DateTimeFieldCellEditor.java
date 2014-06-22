package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor;

import java.util.Date;

import org.talend.mdm.webapp.itemsbrowser2.client.util.DateUtil;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.DateTimeField;

import com.extjs.gxt.ui.client.widget.grid.CellEditor;


public class DateTimeFieldCellEditor extends CellEditor {

    public DateTimeFieldCellEditor(DateTimeField field) {
        super(field);
    }

    public Object preProcessValue(Object value) {
        return DateUtil.convertStringToDate(DateUtil.dateTimePattern, (String) value);
    }

    public Object postProcessValue(Object value) {
        
        return DateUtil.getDateTime((Date) value);
    }
}

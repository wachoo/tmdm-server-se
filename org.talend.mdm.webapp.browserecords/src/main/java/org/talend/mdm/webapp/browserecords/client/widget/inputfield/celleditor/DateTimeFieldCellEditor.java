package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import java.util.Date;

import org.talend.mdm.webapp.browserecords.client.util.DateUtil;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;


public class DateTimeFieldCellEditor extends CellEditor {

    public DateTimeFieldCellEditor(DateField field) {
        super(field);
    }

    public Object preProcessValue(Object value) {
        return DateUtil.convertStringToDate(DateUtil.dateTimePattern, (String) value);
    }

    public Object postProcessValue(Object value) {
        
        return DateUtil.getDateTime((Date) value);
    }
}

package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.converter;

import java.util.Date;

import org.talend.mdm.webapp.itemsbrowser2.client.util.DateUtil;
import com.extjs.gxt.ui.client.binding.Converter;

public class DateConverter extends Converter {

    public Object convertModelValue(Object value) {
        return DateUtil.convertStringToDate((String)value);
    }

    public Object convertFieldValue(Object value) {
        return DateUtil.convertDateToString((Date)value);
    }
}

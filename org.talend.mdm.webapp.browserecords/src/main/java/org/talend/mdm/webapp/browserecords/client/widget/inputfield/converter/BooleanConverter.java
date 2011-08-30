package org.talend.mdm.webapp.browserecords.client.widget.inputfield.converter;

import com.extjs.gxt.ui.client.binding.Converter;


public class BooleanConverter extends Converter {
    public Object convertModelValue(Object value) {
        if (value == null) return null;
        return Boolean.parseBoolean((String) value);
    }

    public Object convertFieldValue(Object value) {
        if (value == null) return null;
        return value.toString();
    }
}

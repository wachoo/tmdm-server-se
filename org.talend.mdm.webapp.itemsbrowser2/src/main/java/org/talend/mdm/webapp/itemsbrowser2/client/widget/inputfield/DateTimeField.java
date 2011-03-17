package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import java.util.Date;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;


public class DateTimeField extends Field<Date> {
    protected El wrap = new El(DOM.createSpan());
    
    DateField dateField = new DateField();
    
    TimeField timeField = new TimeField();
    
    public DateTimeField(){
        dateField.setHideLabel(true);
        timeField.setHideLabel(true);
    }
    
    protected void onRender(Element target, int index) {
        setElement(wrap.dom, target, index);
        dateField.render(wrap.dom);
        timeField.render(wrap.dom);
        super.onRender(target, index);
    }
    
    public void setValue(Date value) {
        dateField.setValue(value);
        if (value != null){
            Time time = new Time(value);
            timeField.setValue(time);
        }
    }
    
    public Date getValue() {
        Date value = dateField.getValue();
        if (value != null){
            Time time = timeField.getValue();
            if (time != null){
                value.setHours(time.getDate().getHours());
                value.setMinutes(time.getDate().getMinutes());
                value.setSeconds(time.getDate().getSeconds());
            }
        }
        return value;
    }
    
    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(dateField);
        ComponentHelper.doAttach(timeField);
    }

    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(dateField);
        ComponentHelper.doDetach(timeField);
    }
}

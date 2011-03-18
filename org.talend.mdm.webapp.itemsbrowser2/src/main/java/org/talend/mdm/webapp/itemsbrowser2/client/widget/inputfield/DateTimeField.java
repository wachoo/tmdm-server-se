package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import java.util.Date;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HorizontalPanel;


public class DateTimeField extends Field<Date> {
    
    HorizontalPanel hp = new HorizontalPanel();
    DateField dateField = new DateField();
    SpinnerField hh = new SpinnerField(){
        public void setValue(Number value){
            if (value == null)
                super.setValue(null);
            else
                super.setValue(value.intValue());
        }
    };
    SpinnerField mm = new SpinnerField(){
        public void setValue(Number value){
            if (value == null)
                super.setValue(null);
            else
                super.setValue(value.intValue());
        }
    };
    SpinnerField ss = new SpinnerField(){
        public void setValue(Number value){
            if (value == null)
                super.setValue(null);
            else
                super.setValue(value.intValue());
        }
    };
    
    public DateTimeField(){
        this.setFireChangeEventOnSetValue(true);
        dateField.setHideLabel(true);
        hp.add(dateField);
        hh.setWidth(40);
        mm.setWidth(40);
        ss.setWidth(40);
        hh.setMinValue(0);
        hh.setMaxValue(23);
        mm.setMinValue(0);
        mm.setMaxValue(59);
        ss.setMinValue(0);
        ss.setMaxValue(59);
        hp.add(new FillToolItem());
        hp.add(hh);
        hp.add(new Label(":"));
        hp.add(mm);
        hp.add(new Label(":"));
        hp.add(ss);
        
        dateField.addListener(Events.Change, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                DateTimeField.this.setValue(dateField.getValue());
            }
        });
        
        Listener<BaseEvent> changeListener = new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                Date v = DateTimeField.this.getValue();
                Date newDate = new Date(v.getTime());
                DateTimeField.this.setValue(newDate);
            }
        };
        
        hh.addListener(Events.Change, changeListener);
        mm.addListener(Events.Change, changeListener);
        ss.addListener(Events.Change, changeListener);
        
    }
    
    protected void onRender(Element target, int index) {
        setElement(hp.getElement(), target, index);
        super.onRender(target, index);
    }
    
    public void setValue(Date value) {
        
        Date oldValue = this.value;
        this.value = value;
        dateField.setValue(value);
        if (value != null){
            hh.setValue(value.getHours());
            mm.setValue(value.getMinutes());
            ss.setValue(value.getSeconds());
        }
        if (isFireChangeEventOnSetValue()) {
          fireChangeEvent(oldValue, value);
        }
    }

    public Date getValue() {
        Date value = dateField.getValue();
        if (value != null){
            value.setHours(((Double)hh.getValue()).intValue());
            value.setMinutes(((Double)mm.getValue()).intValue());
            value.setSeconds(((Double)ss.getValue()).intValue());
        }
        return value;
    }
    
    public void setPropertyEditor(PropertyEditor<Date> propertyEditor) {
        dateField.setPropertyEditor(propertyEditor);
    }
    
    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(hp);
    }

    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(hp);
    }
    
    public void setWidth(int width){
        dateField.setWidth(width - 130);
        super.setWidth(width);
    }
    
    public void setHeight(int height){
        dateField.setHeight(height);
        hh.setHeight(height);
        mm.setHeight(height);
        ss.setHeight(height);
    }
}

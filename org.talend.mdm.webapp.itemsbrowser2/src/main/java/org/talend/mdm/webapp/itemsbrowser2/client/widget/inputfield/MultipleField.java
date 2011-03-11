package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.creator.FieldCreator;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.plugin.FieldPlugin;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class MultipleField extends Field<List<Object>> {

    VerticalPanel vp = new VerticalPanel();

    Listener<BaseEvent> changeListener = new Listener<BaseEvent>() {
        public void handleEvent(BaseEvent be) {
            TextField field = (TextField) be.getSource();
            int index = vp.getWidgetIndex(field);
            value.set(index, (Serializable) field.getValue());
            MultipleField.this.fireChangeEvent(null, value);
        }
    };
    int min, max;
    TypeModel dataType;
    
    public MultipleField(TypeModel dataType){
        this.dataType = dataType;
        int[] range = dataType.getRange();
        min = range[0];
        max = range[1];
        value = new ArrayList<Object>();
        for (int i = 0;i < min; i++) {
            value.add("");
        }
    }
    
    private boolean validateRange(int size){
        if (size >= min && size <= max){
            return true;
        }
        return false;
    }
    
    protected void onRender(Element target, int index) {
        if (el() == null) {
            vp.setSpacing(5);
            setElement(vp.getElement(), target, index);
        }
        super.onRender(target, index);
        removeStyleName(fieldStyle);
    }
    
    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(vp);
    }

    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(vp);
    }
    
    private void buildFields(){
        if (value != null){
            vp.clear();
            for (Object item : value){
                Field field = createField(item);
                vp.add(field);
            }
        } else {
            vp.clear();
        }
    }
    
    private void buildValue(){
        value.clear();
        Iterator<Widget> iter = vp.iterator();
        while (iter.hasNext()){
            Field field = (Field) iter.next();
            value.add((Serializable) field.getValue());
        }
    }
    
    private Field createField(Object value){
        Field field = (Field) FieldCreator.createField((SimpleTypeModel)dataType, null, false);
        field.addPlugin(createFp());
        field.setValue(value);
        field.addListener(Events.Change, changeListener);
        return field;
    }
    
    public void setValue(List<Object> value) {
        this.value = value;
        if (rendered){
            buildFields();
        }
    }
    
    public List<Object> getValue() {
        return value;
    }
    
    public FieldPlugin createFp(){
        FieldPlugin fp = new FieldPlugin();
        fp.setListener(new FieldPlugin.Add_Remove_Listener() {
            
            public void onRemove(Field field) {
                if (!validateRange(value.size() - 1)){
                    Window.alert("At least " + min);
                    return;
                }
                field.removeListener(Events.Change, changeListener);
                field.removeFromParent();
                buildValue();
                MultipleField.this.fireChangeEvent(null, value);
            }
            
            public void onAdd(Field field) {
                if (!validateRange(value.size() + 1)){
                    Window.alert("Maximum of " + max);
                    return;
                }
                Field newField = createField(dataType.getTypeName().getDefaultValue());
                int index = vp.getWidgetIndex(field);
                vp.insert(newField, index + 1);
                buildValue();
                MultipleField.this.fireChangeEvent(null, value);
            }
        });
        return fp;
    }
}

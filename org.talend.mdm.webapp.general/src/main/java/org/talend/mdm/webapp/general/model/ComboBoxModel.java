package org.talend.mdm.webapp.general.model;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class ComboBoxModel extends BaseModelData {

    public ComboBoxModel(){}
    
    public ComboBoxModel(String text, String value){
        setText(text);
        setValue(value);
    }
    
    public String getText(){
        return get("text"); //$NON-NLS-1$
    }
    
    public void setText(String text){
        set("text", text); //$NON-NLS-1$
    }
    
    public String getValue(){
        return get("value"); //$NON-NLS-1$
    }
    
    public void setValue(String value){
        set("value", value); //$NON-NLS-1$
    }
}

package org.talend.mdm.webapp.general.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;


public class ComboBoxModel extends BaseModelData implements Serializable, IsSerializable {

    private static final long serialVersionUID = 2046573370049375384L;

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

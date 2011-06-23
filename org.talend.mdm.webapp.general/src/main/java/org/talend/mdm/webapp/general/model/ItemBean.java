package org.talend.mdm.webapp.general.model;

import java.io.Serializable;


public class ItemBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5087750309890091801L;
    private String text;
    private String value;
    
    public ItemBean(){}

    public ItemBean(String text, String value){
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}

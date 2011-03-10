package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.util.Date;

public enum DataTypeConstants {

    STRING("string", ""),
    DECIMAL("decimal", 0.0),
    UUID("UUID", ""),
    AUTO_INCREMENT("AUTO_INCREMENT", ""),
    PICTURE("PICTURE", "http://"),
    URL("URL", "http://"),
    DATE("date", new Date()),
    
    UNKNOW("unknow", "");
    
    String typeName;
    Object defaultValue;
    
    DataTypeConstants(String typeName, Object defaultValue){
        this.typeName = typeName;
        this.defaultValue = defaultValue;
    }

    public String getTypeName() {
        return typeName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public static DataTypeConstants getDataTypeByName(String typeName){
        DataTypeConstants[] values = DataTypeConstants.values();
        for (DataTypeConstants value : values){
            if (value.getTypeName().equals(typeName)){
                return value;
            }
        }
        return UNKNOW;
    }
}

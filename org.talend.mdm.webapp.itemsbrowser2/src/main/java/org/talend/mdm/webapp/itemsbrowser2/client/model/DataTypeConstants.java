package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.util.Date;

public enum DataTypeConstants implements DataType {

    // TODO support more types
    BOOLEAN("boolean", false),
    DATE("date", new Date()),
    DATETIME("dateTime", new Date()),
    DECIMAL("decimal", 0.0),
    DOUBLE("double", 0.0),
    FLOAT("float", 0.0),
    INT("int", 0),
    INTEGER("integer", 0),
    LONG("long", 0),
    SHORT("short", 0),
    STRING("string", ""),

    UUID("UUID", ""),
    AUTO_INCREMENT("AUTO_INCREMENT", ""),
    PICTURE("PICTURE", "http://"),
    URL("URL", "http://"),

    UNKNOW("unknow", "");

    String typeName;

    Object defaultValue;

    DataTypeConstants(String typeName, Object defaultValue) {
        this.typeName = typeName;
        this.defaultValue = defaultValue;
    }

    public String getTypeName() {
        return typeName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
    /* (non-Jsdoc)
     * @see org.talend.mdm.webapp.itemsbrowser2.client.model.DataType#getBaseTypeName()
     */
    public String getBaseTypeName() {
        return getTypeName();
    }

}

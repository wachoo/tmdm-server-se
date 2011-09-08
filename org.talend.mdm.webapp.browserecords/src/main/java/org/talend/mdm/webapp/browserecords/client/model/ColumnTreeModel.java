package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;
import java.util.List;

public class ColumnTreeModel implements Serializable {

    private String width;

    private String name;

    private List<ColumnElement> columnElements;

    public ColumnTreeModel() {

    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColumnElement> getColumnElements() {
        return columnElements;
    }

    public void setColumnElements(List<ColumnElement> columnElements) {
        this.columnElements = columnElements;
    }

}

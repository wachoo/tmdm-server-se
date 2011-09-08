package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;
import java.util.List;

public class ColumnTreeLayoutModel implements Serializable {

    private String datamodel;

    private String entity;

    private List<ColumnTreeModel> columnTreeModels;

    public ColumnTreeLayoutModel() {

    }

    public String getDatamodel() {
        return datamodel;
    }

    public void setDatamodel(String datamodel) {
        this.datamodel = datamodel;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public List<ColumnTreeModel> getColumnTreeModels() {
        return columnTreeModels;
    }

    public void setColumnTreeModels(List<ColumnTreeModel> columnTreeModels) {
        this.columnTreeModels = columnTreeModels;
    }

}

package org.talend.mdm.webapp.browserecords.client.model;

import org.talend.mdm.webapp.base.client.model.ItemBaseModel;


public class ForeignKeyValidateModel extends ItemBaseModel {

    private static final long serialVersionUID = -2070259885223573368L;

    private boolean isHaveNodeValue;

    private boolean isNodeValid;

    public ForeignKeyValidateModel() {

    }

    public ForeignKeyValidateModel(boolean isHaveNodeValue, boolean isNodeValid) {
        this.isHaveNodeValue = isHaveNodeValue;
        this.isNodeValid = isNodeValid;
    }

    public boolean isHaveNodeValue() {
        return isHaveNodeValue;
    }

    public void setHaveNodeValue(boolean isHaveNodeValue) {
        this.isHaveNodeValue = isHaveNodeValue;
    }

    public boolean isNodeValid() {
        return isNodeValid;
    }

    public void setNodeValid(boolean isNodeValid) {
        this.isNodeValid = isNodeValid;
    }

}

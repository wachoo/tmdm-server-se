package org.talend.mdm.webapp.base.client.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserContextModel implements Serializable, IsSerializable {

    private static final long serialVersionUID = -3689563019277748075L;

    private String dataContainer;

    private String dataModel;

    private String language;

    private String dateTimeFormat;

    public UserContextModel() {

    }

    public String getDataContainer() {
        return dataContainer;
    }

    public void setDataContainer(String dataContainer) {
        this.dataContainer = dataContainer;
    }

    public String getDataModel() {
        return dataModel;
    }

    public void setDataModel(String dataModel) {
        this.dataModel = dataModel;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public void setDateTimeFormat(String dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

}

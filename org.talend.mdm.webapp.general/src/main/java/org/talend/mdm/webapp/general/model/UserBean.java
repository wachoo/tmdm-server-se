package org.talend.mdm.webapp.general.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public class UserBean implements Serializable, IsSerializable {

    private static final long serialVersionUID = 7651498501644312002L;
    private String name;
    private String universe;
    private boolean enterprise;
    
    public UserBean(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniverse() {
        return universe;
    }

    public void setUniverse(String universe) {
        this.universe = universe;
    }

    public boolean isEnterprise() {
        return enterprise;
    }

    public void setEnterprise(boolean enterprise) {
        this.enterprise = enterprise;
    }
    
}

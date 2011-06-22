package org.talend.mdm.webapp.general.model;

import java.io.Serializable;


public class UserBean implements Serializable {

    
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
    };
    
}

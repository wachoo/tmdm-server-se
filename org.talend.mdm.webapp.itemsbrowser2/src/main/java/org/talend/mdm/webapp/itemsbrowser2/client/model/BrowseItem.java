package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.io.Serializable;

public class BrowseItem implements Serializable {

    private String CriteriaName;

    private String ViewPK;

    private String owner;

    private Boolean shared;

    private String criteria;

    public String getCriteriaName() {
        return CriteriaName;
    }

    public void setCriteriaName(String criteriaName) {
        CriteriaName = criteriaName;
    }

    public String getViewPK() {
        return ViewPK;
    }

    public void setViewPK(String viewPK) {
        ViewPK = viewPK;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Boolean getShared() {
        return shared;
    }

    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public BrowseItem() {
        super();
    }

    public String marshal2String() {

        String marshaledItem = "<BrowseItem>" + "<CriteriaName>" + this.CriteriaName + "</CriteriaName>" + "<ViewPK>"
                + this.ViewPK + "</ViewPK>" + "<Owner>" + this.owner + "</Owner>" + "<Shared>" + this.shared + "</Shared>"
                + "<WhereCriteria>" + this.criteria + "</WhereCriteria>" + "</BrowseItem>";
        return marshaledItem;

    }
}

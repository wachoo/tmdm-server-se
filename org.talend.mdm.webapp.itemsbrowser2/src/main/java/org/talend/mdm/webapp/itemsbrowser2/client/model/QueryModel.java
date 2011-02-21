package org.talend.mdm.webapp.itemsbrowser2.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class QueryModel extends BaseModel {

    private static final long serialVersionUID = 4315775494963149856L;

    PagingLoadConfig pagingLoadConfig;

    String dataClusterPK;

    String viewPK;

    Criteria criteria;

    public QueryModel() {

    }

    public PagingLoadConfig getPagingLoadConfig() {
        return pagingLoadConfig;
    }

    public void setPagingLoadConfig(PagingLoadConfig pagingLoadConfig) {
        this.pagingLoadConfig = pagingLoadConfig;
    }

    public String getDataClusterPK() {
        return dataClusterPK;
    }

    public void setDataClusterPK(String dataClusterPK) {
        this.dataClusterPK = dataClusterPK;
    }

    public String getViewPK() {
        return viewPK;
    }

    public void setViewPK(String viewPK) {
        this.viewPK = viewPK;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }
}

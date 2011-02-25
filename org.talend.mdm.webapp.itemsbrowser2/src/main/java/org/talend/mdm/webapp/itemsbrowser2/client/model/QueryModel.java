// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class QueryModel extends ItemBaseModel {

    private static final long serialVersionUID = 4315775494963149856L;

    PagingLoadConfig pagingLoadConfig;

    String dataClusterPK;

    String viewPK;

    String criteria;

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

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }
}

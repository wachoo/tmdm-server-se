// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;

public class QueryModel extends ItemBaseModel {

    private static final long serialVersionUID = 4315775494963149856L;

    PagingLoadConfig pagingLoadConfig;

    String dataClusterPK;

    ViewBean view;
    
    EntityModel model;

    String criteria;

    String language;
    
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

    public ViewBean getView() {
        return view;
    }

    public void setView(ViewBean view) {
        this.view = view;
    }
    
    public EntityModel getModel() {
        return model;
    }

    public void setModel(EntityModel model) {
        this.model = model;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}

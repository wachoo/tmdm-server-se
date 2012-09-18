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
package org.talend.mdm.webapp.browserecords.client.model;

import java.io.Serializable;

import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ForeignKeyTabModel implements Serializable, IsSerializable {

    private static final long serialVersionUID = -5519030179416660595L;
    
    private ItemNodeModel fkParentModel;

    private String fkTabTitle;

    private ItemPanel fkTabPanel;

    public ForeignKeyTabModel() {

    }

    public ForeignKeyTabModel(ItemNodeModel fkParentModel, String fkTabTitle, ItemPanel fkTabPanel) {
        this.fkParentModel = fkParentModel;
        this.fkTabTitle = fkTabTitle;
        this.fkTabPanel = fkTabPanel;
    }

    public ItemNodeModel getFkParentModel() {
        return fkParentModel;
    }

    public void setFkParentModel(ItemNodeModel fkParentModel) {
        this.fkParentModel = fkParentModel;
    }

    public String getFkTabTitle() {
        return fkTabTitle;
    }

    public void setFkTabTitle(String fkTabTitle) {
        this.fkTabTitle = fkTabTitle;
    }

    public ItemPanel getFkTabPanel() {
        return fkTabPanel;
    }

    public void setFkTabPanel(ItemPanel fkTabPanel) {
        this.fkTabPanel = fkTabPanel;
    }

}

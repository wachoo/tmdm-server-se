// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget.ForeignKey;

import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyListWindow;

public class FKRelRecordWindow extends ForeignKeyListWindow {

    public FKRelRecordWindow() {
    }

    @Override
    protected void closeOrHideWindow() {
        hide();
    }

    @Override
    public void setEntityModel(EntityModel entityModel) {
        super.setEntityModel(entityModel);
    }

    @Override
    public EntityModel getEntityModel() {
        return BrowseRecords.getSession().getCurrentEntityModel();
    }

    @Override
    public TypeModel buildTypeModel() {
        return getEntityModel().getMetaDataTypes().get(getFkKey());
    }

    public EntityModel getParentEntityModel() {
        return super.getEntityModel();
    }

}

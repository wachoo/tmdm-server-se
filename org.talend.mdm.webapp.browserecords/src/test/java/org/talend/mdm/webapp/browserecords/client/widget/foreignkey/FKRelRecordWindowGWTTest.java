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
package org.talend.mdm.webapp.browserecords.client.widget.foreignkey;


import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.util.ClientResourceData;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtilTestData;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKey.FKRelRecordWindow;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("nls")
public class FKRelRecordWindowGWTTest extends GWTTestCase {

    private String foreignKey = "Product/Family";

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        UserSession session = new UserSession();
        session.put(UserSession.CURRENT_ENTITY_MODEL, getCurrentEntityModel());
        Registry.register(BrowseRecords.USER_SESSION, session);
    }

    public void testGetEntityModel() {
        FKRelRecordWindow fkWindow = new FKRelRecordWindow();
        fkWindow.setEntityModel(getProductFamilyModel());
        fkWindow.setFkKey(foreignKey);
        assertNotNull(fkWindow.getEntityModel());
        assertTrue(fkWindow.getEntityModel().getConceptName().equals("Product"));
        assertTrue(fkWindow.buildTypeModel().getXpath().equals(foreignKey));
        assertNotNull(fkWindow.getParentEntityModel().getConceptName().equals("ProductFamily"));
    }

    private EntityModel getCurrentEntityModel() {
        EntityModel productEntityModel = CommonUtilTestData.getEntityModel(ClientResourceData.getModelProduct());
        return productEntityModel;
    }

    private EntityModel getProductFamilyModel() {
        EntityModel familyEntityModel = CommonUtilTestData.getEntityModel(ClientResourceData.getModelProductFamily());
        return familyEntityModel;
    }

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords";
    }
}
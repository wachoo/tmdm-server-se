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
package org.talend.mdm.webapp.browserecords.client.rest;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.util.StagingConstant;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.junit.client.GWTTestCase;

public class ExplainRestServiceHandlerGWTTest extends GWTTestCase {

    ExplainRestServiceHandler handler;

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        handler = ExplainRestServiceHandler.get();
        handler.setClient(new ClientResourceMockWrapper());
    }

    public void testExplainGroupResult() {
        String dataCluster = "Product"; //$NON-NLS-1$
        String concept = "Product"; //$NON-NLS-1$
        String groupId = "74849595-27be-4b48-9238-cca56c6e6658"; //$NON-NLS-1$
        MockBaseTreeModelCallback callback = new MockBaseTreeModelCallback();
        handler.explainGroupResult(dataCluster, concept, groupId, callback);
        assertTrue(callback.isSucceed());
        BaseTreeModel root = callback.getModel();
        assertNotNull(root);
        BaseTreeModel treeNode1 = (BaseTreeModel) root.getChild(0);
        assertEquals("N-ValueN-ValueN-ValueN-Value", treeNode1.get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("b3c570ae-d988-4536-a92e-6f2a33834253", treeNode1.get(StagingConstant.MATCH_GROUP_ID)); //$NON-NLS-1$
        assertEquals(4, treeNode1.get(StagingConstant.MATCH_GROUP_SZIE));
        assertEquals("1", treeNode1.get(StagingConstant.MATCH_GROUP_CONFIDENCE)); //$NON-NLS-1$
        assertEquals("group1", treeNode1.get(StagingConstant.MATCH_GROUP_NAME)); //$NON-NLS-1$
        BaseTreeModel treeNode2 = (BaseTreeModel) treeNode1.getChild(0);
        assertEquals("N-Value1", treeNode2.get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Id 1", treeNode2.get(StagingConstant.MATCH_GROUP_ID)); //$NON-NLS-1$
        assertEquals(false, treeNode2.get(StagingConstant.MATCH_IS_GROUP));
        assertEquals("Name:1", treeNode2.get(StagingConstant.MATCH_SCORE)); //$NON-NLS-1$
        treeNode2 = (BaseTreeModel) treeNode1.getChild(1);
        assertEquals("N-Value4", treeNode2.get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Id 4", treeNode2.get(StagingConstant.MATCH_GROUP_ID)); //$NON-NLS-1$
        assertEquals(false, treeNode2.get(StagingConstant.MATCH_IS_GROUP));
        assertEquals("Name:4", treeNode2.get(StagingConstant.MATCH_SCORE)); //$NON-NLS-1$
        treeNode2 = (BaseTreeModel) treeNode1.getChild(2);
        assertEquals("N-Value3", treeNode2.get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Id 3", treeNode2.get(StagingConstant.MATCH_GROUP_ID)); //$NON-NLS-1$
        assertEquals(false, treeNode2.get(StagingConstant.MATCH_IS_GROUP));
        assertEquals("Name:3", treeNode2.get(StagingConstant.MATCH_SCORE)); //$NON-NLS-1$
        treeNode2 = (BaseTreeModel) treeNode1.getChild(3);
        assertEquals("N-ValueN-Value2", treeNode2.get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Id 2", treeNode2.get(StagingConstant.MATCH_GROUP_ID)); //$NON-NLS-1$
        assertEquals(false, treeNode2.get(StagingConstant.MATCH_IS_GROUP));
        assertEquals("Name:2", treeNode2.get(StagingConstant.MATCH_SCORE)); //$NON-NLS-1$
    }

    public void testSimulateMatch() {
        String ids = "1" + "\n" + "4" + "\n" + "7"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
        String dataCluster = "Product"; //$NON-NLS-1$
        String concept = "Product"; //$NON-NLS-1$
        MockBaseTreeModelCallback callback = new MockBaseTreeModelCallback();
        handler.simulateMatch(dataCluster, concept, ids, callback);
        assertTrue(callback.isSucceed());
        BaseTreeModel root = callback.getModel();
        assertNotNull(root);
        BaseTreeModel treeNode1 = (BaseTreeModel) root.getChild(0);
        assertEquals(null, treeNode1.get("Name")); //$NON-NLS-1$
        assertEquals(null, treeNode1.get(StagingConstant.MATCH_GROUP_ID));
        assertEquals(null, treeNode1.get(StagingConstant.MATCH_GROUP_SZIE));
        assertEquals(null, treeNode1.get(StagingConstant.MATCH_GROUP_CONFIDENCE));
        assertEquals("group1", treeNode1.get(StagingConstant.MATCH_GROUP_NAME)); //$NON-NLS-1$
        BaseTreeModel treeNode2 = (BaseTreeModel) treeNode1.getChild(0);
        assertEquals("1", treeNode2.get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Simulate1", treeNode2.get(StagingConstant.MATCH_GROUP_ID)); //$NON-NLS-1$
        assertEquals(false, treeNode2.get(StagingConstant.MATCH_IS_GROUP));
        assertEquals("Name:1", treeNode2.get(StagingConstant.MATCH_SCORE)); //$NON-NLS-1$
        treeNode2 = (BaseTreeModel) treeNode1.getChild(1);
        assertEquals("1", treeNode2.get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Simulate2", treeNode2.get(StagingConstant.MATCH_GROUP_ID)); //$NON-NLS-1$
        assertEquals(false, treeNode2.get(StagingConstant.MATCH_IS_GROUP));
        assertEquals("Name:1", treeNode2.get(StagingConstant.MATCH_SCORE)); //$NON-NLS-1$
        treeNode2 = (BaseTreeModel) treeNode1.getChild(2);
        assertEquals("1", treeNode2.get("Name")); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("Simulate3", treeNode2.get(StagingConstant.MATCH_GROUP_ID)); //$NON-NLS-1$
        assertEquals(false, treeNode2.get(StagingConstant.MATCH_IS_GROUP));
        assertEquals("Name:1", treeNode2.get(StagingConstant.MATCH_SCORE)); //$NON-NLS-1$
    }

    private class MockBaseTreeModelCallback extends SessionAwareAsyncCallback<BaseTreeModel> {

        private boolean succeed = false;

        private BaseTreeModel model;

        @Override
        public void onSuccess(BaseTreeModel treeModel) {
            this.model = treeModel;
            this.succeed = true;
        }

        public BaseTreeModel getModel() {
            return model;
        }

        public boolean isSucceed() {
            return succeed;
        }
    }

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }

}

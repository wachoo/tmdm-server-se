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
        String value = printAllTreeByRoot(root);
        String expectedValue = "groups : [groups : [group : [result : [id : 8] : [confidence : 1] : [related_ids : [9] : [8] : [7]] : [values : [value : [field : Name] : [value : C]]]] : [details : [detail : [id : 7] : [match : [is_match : true] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 1] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 8]] : [detail : [id : 9] : [match : [is_match : true] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 1] : [algorithm : Exact] : [threshold : 1]]]]]]]]"; //$NON-NLS-1$
        assertEquals(expectedValue, value);
    }

    public void testSimulateMatch() {
        String ids = "1" + "\n" + "4" + "\n" + "7"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
        String dataCluster = "Product"; //$NON-NLS-1$
        String concept = "Product"; //$NON-NLS-1$
        MockBaseTreeModelCallback callback = new MockBaseTreeModelCallback();
        handler.simulateMatch(dataCluster, concept, ids, callback);
        assertTrue(callback.isSucceed());
        BaseTreeModel model = callback.getModel();
        assertNotNull(model);
        String value = printAllTreeByRoot(model);
        String expectedValue = "groups : [groups : [group : [result : [id : 7] : [confidence : 1] : [related_ids : [7]] : [values : [value : [field : Name] : [value : C]]]] : [details : [detail : [id : 7] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 1] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 4]]]] : [group : [result : [id : 1] : [confidence : 1] : [related_ids : [1]] : [values : [value : [field : Name] : [value : A]]]] : [details : [detail : [id : 7] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 1] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 4]]]] : [group : [result : [id : 4] : [confidence : 1] : [related_ids : [4]] : [values : [value : [field : Name] : [value : B]]]] : [details : [detail : [id : 7] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 1] : [match : [is_match : false] : [scores : [score : [pair_id : 7] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 4]]]]]"; //$NON-NLS-1$
        assertEquals(expectedValue, value);
    }

    public void testCompareRecords() {
        String dataModel = "Product"; //$NON-NLS-1$
        String concept = "Product"; //$NON-NLS-1$
        String xmlRecord = "<Product><Id>11e18f27-2a8c-4ef8-b80f-5ba27d8fd103</Id><Name>C</Name><Description>8</Description><Features><Sizes/><Colors/></Features><Price>19.00</Price><Stores/></Product><Product><Id>17cd4337-6073-4778-a50d-748247249a99</Id><Name>D</Name><Description>D</Description><Features><Sizes/><Colors/></Features><Price>2222.00</Price><Stores/></Product><Product><Id>74849595-27be-4b48-9238-cca56c6e6658</Id><Name>B</Name><Description>4</Description><Features><Sizes/><Colors/></Features><Price>16.00</Price><Stores/></Product>"; //$NON-NLS-1$
        MockBaseTreeModelCallback callback = new MockBaseTreeModelCallback();
        handler.compareRecords(dataModel, concept, xmlRecord, callback);
        assertTrue(callback.isSucceed());
        BaseTreeModel model = callback.getModel();
        assertNotNull(model);
        String value = printAllTreeByRoot(model);
        String expectedValue = "groups : [groups : [group : [result : [id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [confidence : 1] : [related_ids : [11e18f27-2a8c-4ef8-b80f-5ba27d8fd103]] : [values : [value : [field : Name] : [value : C]]]] : [details : [detail : [id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 74849595-27be-4b48-9238-cca56c6e6658] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 17cd4337-6073-4778-a50d-748247249a99]]]] : [group : [result : [id : 74849595-27be-4b48-9238-cca56c6e6658] : [confidence : 1] : [related_ids : [74849595-27be-4b48-9238-cca56c6e6658]] : [values : [value : [field : Name] : [value : B]]]] : [details : [detail : [id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 74849595-27be-4b48-9238-cca56c6e6658] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 17cd4337-6073-4778-a50d-748247249a99]]]] : [group : [result : [id : 17cd4337-6073-4778-a50d-748247249a99] : [confidence : 1] : [related_ids : [17cd4337-6073-4778-a50d-748247249a99]] : [values : [value : [field : Name] : [value : D]]]] : [details : [detail : [id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 74849595-27be-4b48-9238-cca56c6e6658] : [match : [is_match : false] : [scores : [score : [pair_id : 11e18f27-2a8c-4ef8-b80f-5ba27d8fd103] : [field : Name] : [value : 0] : [algorithm : Exact] : [threshold : 1]]]]] : [detail : [id : 17cd4337-6073-4778-a50d-748247249a99]]]]]"; //$NON-NLS-1$
        assertEquals(expectedValue, value);
    }

    private String printAllTreeByRoot(BaseTreeModel root) {
        StringBuilder treeValue = new StringBuilder();
        treeValue.append(root.get("name")); //$NON-NLS-1$
        retriveTree(root, treeValue);
        return treeValue.toString();
    }

    private void retriveTree(BaseTreeModel model, StringBuilder treeValue) {
        treeValue.append(" : ["); //$NON-NLS-1$
        treeValue.append(model.get("name")); //$NON-NLS-1$
        for (int i = 0; i < model.getChildCount(); i++) {
            retriveTree((BaseTreeModel) model.getChild(i), treeValue);
        }
        treeValue.append("]"); //$NON-NLS-1$
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

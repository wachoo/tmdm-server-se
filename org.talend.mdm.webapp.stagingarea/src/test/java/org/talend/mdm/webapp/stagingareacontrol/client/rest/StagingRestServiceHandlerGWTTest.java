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
package org.talend.mdm.webapp.stagingareacontrol.client.rest;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaValidationModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("nls")
public class StagingRestServiceHandlerGWTTest extends GWTTestCase {

    StagingRestServiceHandler handler;

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        handler = StagingRestServiceHandler.get();
        handler.setClient(new ClientResourceMockWrapper());
    }

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.stagingareacontrol.StagingareaControl"; //$NON-NLS-1$
    }

    public void testGetDefaultStagingContainerSummary() {
        MockStagingContainerModelCallback callback = new MockStagingContainerModelCallback();
        handler.getDefaultStagingContainerSummary(callback);
        assertTrue(callback.isSucceed());
        assertNotNull(callback.getModel());
        assertEquals("TestDataContainer", callback.getModel().getDataContainer());
        assertEquals("TestDataModel", callback.getModel().getDataModel());
        assertEquals(1000, callback.getModel().getInvalidRecords());
        assertEquals(10000, callback.getModel().getTotalRecords());
        assertEquals(8000, callback.getModel().getValidRecords());
        assertEquals(1000, callback.getModel().getWaitingValidationRecords());
    }

    public void testGetStagingContainerSummary() {
        MockStagingContainerModelCallback callback = new MockStagingContainerModelCallback();
        handler.getStagingContainerSummary("Product", "Product", callback);
        assertTrue(callback.isSucceed());
        assertNotNull(callback.getModel());
        assertEquals("Product", callback.getModel().getDataContainer());
        assertEquals("Product", callback.getModel().getDataModel());
        assertEquals(800, callback.getModel().getInvalidRecords());
        assertEquals(7000, callback.getModel().getTotalRecords());
        assertEquals(5000, callback.getModel().getValidRecords());
        assertEquals(1200, callback.getModel().getWaitingValidationRecords());
    }

    public void testGetStagingAreaExecutionIds() {
        MockListArrayModelCallback callback = new MockListArrayModelCallback();
        handler.getStagingAreaExecutionIds("TestDataContainer", -1, 1, callback);
        assertEquals(2, callback.getResultSize());
    }

    public void testGetStagingAreaExecution() {
        MockStagingAreaExecutionModel callback = new MockStagingAreaExecutionModel();
        handler.getStagingAreaExecution("TestDataContainer", "fa011993-648f-48b3-9e4d-9c71de82f91a", callback);
        assertTrue(callback.isSucceed());
        assertNotNull(callback.getModel());
        assertEquals(8, callback.getModel().getEndDate().getMonth() + 1);
        assertEquals("fa011993-648f-48b3-9e4d-9c71de82f91a", callback.getModel().getId());
        assertEquals(973, callback.getModel().getInvalidRecords());
        assertEquals(772, callback.getModel().getProcessedRecords());
        assertEquals(20, callback.getModel().getStartDate().getMinutes());
        assertEquals(772, callback.getModel().getTotalRecord());
    }

    public void testGetStagingAreaExecutionsWithPaging() {
        MockStagingAreaExecutionArrayModel callback = new MockStagingAreaExecutionArrayModel();
        handler.getStagingAreaExecutionsWithPaging("TestDataContainer", 1, 10, DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss")
                .parse("2012-12-12T00:00:00"), callback);
        assertTrue(callback.isSucceed());
        assertEquals(2, callback.getModel().size());
        assertEquals("fa011993-648f-48b3-9e4d-9c71de82f91a", callback.getModel().get(0).getId());
        assertEquals(772, callback.getModel().get(0).getTotalRecord());
    }

    public void testGetValidationTaskStatus() {
        MockStagingAreaValidationModel callback = new MockStagingAreaValidationModel();
        handler.getValidationTaskStatus("TestDataContainer", callback);
        assertTrue(callback.isSucceed());
        assertNotNull(callback.getModel());
        assertEquals("1ad084c1-5f70-4b89-aeef-613e7e44f134", callback.getModel().getId());
        assertEquals(5, callback.getModel().getInvalidRecords());
        assertEquals(10, callback.getModel().getProcessedRecords());
        assertEquals(10000, callback.getModel().getTotalRecord());
        assertEquals(20, callback.getModel().getStartDate().getMinutes());
    }

    public void testRunValidationTask() {
        MockStringModelCallback callback = new MockStringModelCallback();
        handler.runValidationTask("TestDataContainer", "TestDataModel", null, callback);
        assertEquals("1ad084c1-5f70-4b89-aeef-613e7e44f134", callback.getResult());
    }

    public void testCancelValidationTask() {
        MockBooleanModelCallback callback = new MockBooleanModelCallback();
        handler.cancelValidationTask("TestDataContainer", callback);
        assertTrue(callback.isSuccess());
    }

    private class MockStagingContainerModelCallback extends SessionAwareAsyncCallback<StagingContainerModel> {

        private boolean succeed = false;

        private StagingContainerModel model;

        @Override
        public void onSuccess(StagingContainerModel result) {
            this.model = result;
            this.succeed = true;
        }

        public StagingContainerModel getModel() {
            return model;
        }

        public boolean isSucceed() {
            return succeed;
        }
    }

    private class MockStringModelCallback extends SessionAwareAsyncCallback<String> {

        private String text;

        @Override
        public void onSuccess(String result) {
            text = result;
        }

        public String getResult() {
            return text;
        }
    }

    private class MockBooleanModelCallback extends SessionAwareAsyncCallback<Boolean> {

        private boolean success;

        @Override
        public void onSuccess(Boolean result) {
            success = result;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private class MockListArrayModelCallback extends SessionAwareAsyncCallback<List<String>> {

        private int resultSize;

        @Override
        public void onSuccess(List<String> result) {
            resultSize = result.size();
        }

        public int getResultSize() {
            return resultSize;
        }
    }

    private class MockStagingAreaExecutionModel extends SessionAwareAsyncCallback<StagingAreaExecutionModel> {

        private boolean succeed = false;

        private StagingAreaExecutionModel model;

        @Override
        public void onSuccess(StagingAreaExecutionModel result) {
            this.model = result;
            this.succeed = true;
        }

        public StagingAreaExecutionModel getModel() {
            return model;
        }

        public boolean isSucceed() {
            return succeed;
        }
    }

    private class MockStagingAreaValidationModel extends SessionAwareAsyncCallback<StagingAreaValidationModel> {

        private boolean succeed = false;

        private StagingAreaValidationModel model;

        @Override
        public void onSuccess(StagingAreaValidationModel result) {
            this.model = result;
            this.succeed = true;
        }

        public StagingAreaValidationModel getModel() {
            return model;
        }

        public boolean isSucceed() {
            return succeed;
        }
    }

    private class MockStagingAreaExecutionArrayModel extends SessionAwareAsyncCallback<List<StagingAreaExecutionModel>> {

        private boolean succeed = false;

        private List<StagingAreaExecutionModel> model;

        @Override
        public void onSuccess(List<StagingAreaExecutionModel> result) {
            this.model = result;
            this.succeed = true;
        }

        public List<StagingAreaExecutionModel> getModel() {
            return model;
        }

        public boolean isSucceed() {
            return succeed;
        }
    }

}

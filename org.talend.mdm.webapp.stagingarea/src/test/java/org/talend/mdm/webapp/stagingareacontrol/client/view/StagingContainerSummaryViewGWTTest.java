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
package org.talend.mdm.webapp.stagingareacontrol.client.view;

import org.talend.mdm.webapp.base.client.rest.model.StagingContainerModel;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.TestUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaConfiguration;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

@SuppressWarnings("nls")
public class StagingContainerSummaryViewGWTTest extends GWTTestCase {

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        TestUtil.initRestServices(new ResourceMockWrapper());
        TestUtil.initUserContext("TestDataContainer", "TestDataModel");
    }

    public native void setContextModel(StagingAreaConfiguration cm)/*-{
		@org.talend.mdm.webapp.stagingareacontrol.client.StagingareaControl::stagingAreaConfig = cm;
    }-*/;

    public void testHtmlContentAfterRefreshSummary() {
        TestUtil.initContainer();
        StagingContainerSummaryView summaryView = (StagingContainerSummaryView) ControllerContainer.get().getSummaryController()
                .getBindingView();

        UserContextUtil.setDataContainer("TestDataContainer");
        UserContextUtil.setDataModel("TestDataModel");

        RootPanel.get().add(summaryView);
        ControllerContainer.get().getSummaryController().refreshView();

        StagingContainerModel model = summaryView.getStagingContainerModel();
        assertNotNull(model);
        assertEquals("TestDataContainer", model.getDataContainer());
        assertEquals("TestDataModel", model.getDataModel());
        assertEquals(1000, model.getInvalidRecords());
        assertEquals(10000, model.getTotalRecords());
        assertEquals(8000, model.getValidRecords());
        assertEquals(1000, model.getWaitingValidationRecords());

        HTMLPanel detailPanel = getDetailPanel(summaryView);
        Element titleEl = detailPanel.getElementById(StagingContainerSummaryView.STAGING_AREA_TITLE);
        assertTrue(titleEl.getInnerHTML().contains("<b>10000</b>"));

        Element waitingEl = detailPanel.getElementById(StagingContainerSummaryView.STAGING_AREA_WAITING);
        assertTrue(waitingEl.getInnerHTML().contains("<b>1000</b>"));

        Element invalidEl = detailPanel.getElementById(StagingContainerSummaryView.STAGING_AREA_INVALID);
        assertTrue(invalidEl.getInnerHTML().contains("<b>1000</b>"));

        Element validEl = detailPanel.getElementById(StagingContainerSummaryView.STAGING_AREA_VALID);
        assertTrue(validEl.getInnerHTML().contains("<b>8000</b>"));
    }

    private native HTMLPanel getDetailPanel(StagingContainerSummaryView view)/*-{
		return view.@org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView::detailPanel;
    }-*/;

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.stagingareacontrol.StagingareaControl"; //$NON-NLS-1$
    }
}

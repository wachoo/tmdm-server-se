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
package org.talend.mdm.webapp.stagingarea.client.view;

import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingarea.client.TestUtil;
import org.talend.mdm.webapp.stagingarea.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingarea.client.model.ContextModel;
import org.talend.mdm.webapp.stagingarea.client.model.StagingContainerModel;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;

@SuppressWarnings("nls")
public class StagingContainerSummaryViewTest extends GWTTestCase {

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        TestUtil.initRestServices(new ResourceMockWrapper());
        TestUtil.initUserContext("TestDataContainer", "TestDataModel");
    }

    public native void setContextModel(ContextModel cm)/*-{
        @org.talend.mdm.webapp.stagingarea.client.Stagingarea::contextModel = cm;
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

    public void testStartValidationStatus() {
        TestUtil.initContainer();
        StagingContainerSummaryView summaryView = (StagingContainerSummaryView) ControllerContainer.get().getSummaryController().getBindingView();

        Button chart = new Button("chart");
        StagingContainerSummaryView.setChart(chart);

        assertEquals(false, summaryView.getStartValidateButton().isEnabled());
        RootPanel.get().add(summaryView);
        assertEquals(true, summaryView.getStartValidateButton().isEnabled());

        ControllerContainer.get().getSummaryController().startValidation();
        assertEquals(false, summaryView.getStartValidateButton().isEnabled());
    }

    private native HTMLPanel getDetailPanel(StagingContainerSummaryView view)/*-{
        return view.@org.talend.mdm.webapp.stagingarea.client.view.StagingContainerSummaryView::detailPanel;
    }-*/;
    
    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.stagingarea.Stagingarea"; //$NON-NLS-1$
    }
}

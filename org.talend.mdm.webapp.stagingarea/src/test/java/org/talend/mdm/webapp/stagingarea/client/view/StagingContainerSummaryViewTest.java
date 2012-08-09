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
import org.talend.mdm.webapp.stagingarea.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingarea.client.model.StagingContainerModel;
import org.talend.mdm.webapp.stagingarea.client.rest.RestServiceHandler;

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
        RestServiceHandler.get().setClient(new ResourceMockWrapper());
    }

    public void testStagingContainerSummaryView() {
        UserContextUtil.setDataContainer("TestDataContainer");
        UserContextUtil.setDataModel("TestDataModel");

        Button chart = new Button("chart");
        StagingContainerSummaryView.setChart(chart);
        StagingContainerSummaryView view = new StagingContainerSummaryView();
        ControllerContainer.setStagingContainerSummaryView(view);
        RootPanel.get().add(view);
        ControllerContainer.get().getSummaryController().refreshView();

        StagingContainerModel model = view.getStagingContainerModel();
        assertNotNull(model);
        assertEquals("TestDataContainer", model.getDataContainer());
        assertEquals("TestDataModel", model.getDataModel());
        assertEquals(1000, model.getInvalidRecords());
        assertEquals(10000, model.getTotalRecords());
        assertEquals(8000, model.getValidRecords());
        assertEquals(1000, model.getWaitingValidationRecords());

        HTMLPanel detailPanel = getDetailPanel(view);
        Element titleEl = detailPanel.getElementById(StagingContainerSummaryView.STAGING_AREA_TITLE);
        assertEquals("<b>10000</b> records in staging area:", titleEl.getInnerHTML());

        Element waitingEl = detailPanel.getElementById(StagingContainerSummaryView.STAGING_AREA_WAITING);
        assertEquals("Waiting for validation: <b>1000</b> records", waitingEl.getInnerHTML());

        Element invalidEl = detailPanel.getElementById(StagingContainerSummaryView.STAGING_AREA_INVALID);
        assertEquals("Invalid: <b>1000</b> records <a style=\"color:red\" href=\"#\">open invalid records...</a>", invalidEl.getInnerHTML());

        Element validEl = detailPanel.getElementById(StagingContainerSummaryView.STAGING_AREA_VALID);
        assertEquals("Valid: <b>8000</b> records", validEl.getInnerHTML());

    }

    private native HTMLPanel getDetailPanel(StagingContainerSummaryView view)/*-{
        return view.@org.talend.mdm.webapp.stagingarea.client.view.StagingContainerSummaryView::detailPanel;
    }-*/;
    
    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.stagingarea.Stagingarea"; //$NON-NLS-1$
    }
}

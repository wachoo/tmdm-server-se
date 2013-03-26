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
package org.talend.mdm.webapp.stagingareacontrol.client.mvc;

import org.talend.mdm.webapp.stagingareacontrol.client.GenerateContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.TestUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.RestServiceHandler;
import org.talend.mdm.webapp.stagingareacontrol.client.view.CurrentValidationView;
import org.talend.mdm.webapp.stagingareacontrol.client.view.ResourceMockWrapper;
import org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

@SuppressWarnings("nls")
public class CurrentValidationGWTTest extends GWTTestCase {

    private StagingContainerSummaryView summaryView;

    private CurrentValidationView validationView;

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        RestServiceHandler.get().setClient(new ResourceMockWrapper());

        TestUtil.initRestServices(new ResourceMockWrapper());
        TestUtil.initUserContext("TestDataContainer", "TestDataModel");
        TestUtil.initContainer();
        GenerateContainer.generateContentPanel();
        summaryView = (StagingContainerSummaryView) ControllerContainer.get().getSummaryController().getBindingView();
        validationView = (CurrentValidationView) ControllerContainer.get().getCurrentValidationController().getBindingView();
        RootPanel.get().add(summaryView);
        RootPanel.get().add(validationView);
    }

    public void testValidationStatusAfterStartValidation() {

        assertEquals(CurrentValidationView.Status.None, validationView.getStatus());

        ControllerContainer.get().getSummaryController().startValidation();

        assertEquals(CurrentValidationView.Status.HasValidation, validationView.getStatus());

        ControllerContainer.get().getCurrentValidationController().cancelValidation();

        assertEquals(CurrentValidationView.Status.None, validationView.getStatus());

    }

    public void testViewReactionAfterStartValidation() {

        assertNull(validationView.getStartDateField().getValue());

        ControllerContainer.get().getSummaryController().startValidation();

        assertNotNull(validationView.getStartDateField().getValue());
        validationView.getCancelButton().isEnabled();
        assertFalse(summaryView.getStartValidateButton().isEnabled());

    }

    public void testModelChangeAfterStartValidation() {

        assertNull(validationView.getCurrentValidationModel());

        ControllerContainer.get().getSummaryController().startValidation();

        validationView.getCancelButton().isEnabled();
        assertNotNull(validationView.getCurrentValidationModel());

        assertEquals("100b8d26-02d5-49ea-adad-bf56a6057be5", validationView.getCurrentValidationModel().getId());
        assertEquals(10000, validationView.getCurrentValidationModel().getTotalRecord());
        assertEquals(10, validationView.getCurrentValidationModel().getProcessedRecords());
        assertEquals(5, validationView.getCurrentValidationModel().getInvalidRecords());

    }

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.stagingareacontrol.StagingareaControl"; //$NON-NLS-1$
    }
}

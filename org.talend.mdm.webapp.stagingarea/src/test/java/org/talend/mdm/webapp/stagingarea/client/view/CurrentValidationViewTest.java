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

import org.talend.mdm.webapp.stagingarea.client.TestUtil;
import org.talend.mdm.webapp.stagingarea.client.controller.ControllerContainer;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

@SuppressWarnings("nls")
public class CurrentValidationViewTest extends GWTTestCase {

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        TestUtil.initRestServices(new ResourceMockWrapper());
        TestUtil.initUserContext("TestDataContainer", "TestDataModel");
    }

    public void testCurrentValidationPanelAfterStartValidation() {
        TestUtil.initContainer();
        StagingContainerSummaryView summaryView = (StagingContainerSummaryView) ControllerContainer.get().getSummaryController()
                .getBindingView();
        CurrentValidationView currentView = (CurrentValidationView) ControllerContainer.get().getCurrentValidationController()
                .getBindingView();

        assertEquals(false, summaryView.getStartValidateButton().isEnabled());
        assertNull(currentView.getActivePanel());

        RootPanel.get().add(summaryView);
        RootPanel.get().add(currentView);

        assertEquals(true, summaryView.getStartValidateButton().isEnabled());
        assertEquals(currentView.getDefaultMessagePanel(), currentView.getActivePanel());

        ControllerContainer.get().getSummaryController().startValidation();
        assertEquals(false, summaryView.getStartValidateButton().isEnabled());
        assertEquals(true, currentView.getCancelButton().isEnabled());
        assertEquals(currentView.getContentPanel(), currentView.getActivePanel());
    }

    public void testCurrentValidationFieldValuesAfterStartValidation() {
        TestUtil.initContainer();
        StagingContainerSummaryView summaryView = (StagingContainerSummaryView) ControllerContainer.get().getSummaryController()
                .getBindingView();
        CurrentValidationView currentView = (CurrentValidationView) ControllerContainer.get().getCurrentValidationController()
                .getBindingView();

        RootPanel.get().add(summaryView);
        RootPanel.get().add(currentView);

        ControllerContainer.get().getSummaryController().startValidation();
        assertEquals(currentView.getContentPanel(), currentView.getActivePanel());

        String pattern = "yyyy-MM-dd";
        String startDate = DateTimeFormat.getFormat(pattern).format(getStartDateField(currentView).getValue());

        assertEquals("2012-08-09", startDate);
        assertEquals(10.0, getRecordToProcessField(currentView).getValue());
        assertEquals(5D, getInvalidField(currentView).getValue());
    }

    public native DateField getStartDateField(CurrentValidationView currentView)/*-{
        return currentView.@org.talend.mdm.webapp.stagingarea.client.view.CurrentValidationView::startDateField;
    }-*/;

    public native NumberField getRecordToProcessField(CurrentValidationView currentView)/*-{
        return currentView.@org.talend.mdm.webapp.stagingarea.client.view.CurrentValidationView::recordToProcessField;
    }-*/;

    public native NumberField getInvalidField(CurrentValidationView currentView)/*-{
        return currentView.@org.talend.mdm.webapp.stagingarea.client.view.CurrentValidationView::invalidField;
    }-*/;

    public native TextField<String> getEtaField(CurrentValidationView currentView)/*-{
        return currentView.@org.talend.mdm.webapp.stagingarea.client.view.CurrentValidationView::etaField;
    }-*/;

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.stagingarea.Stagingarea"; //$NON-NLS-1$
    }
}

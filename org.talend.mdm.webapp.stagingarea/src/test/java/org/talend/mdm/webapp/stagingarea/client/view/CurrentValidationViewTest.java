package org.talend.mdm.webapp.stagingarea.client.view;

import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingarea.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingarea.client.rest.RestServiceHandler;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

@SuppressWarnings("nls")
public class CurrentValidationViewTest extends GWTTestCase {

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        RestServiceHandler.get().setClient(new ResourceMockWrapper());
    }

    public void testCurrentValidationView() {
        UserContextUtil.setDataContainer("TestDataContainer");
        UserContextUtil.setDataModel("TestDataModel");

        Button chart = new Button("chart");
        StagingContainerSummaryView.setChart(chart);

        StagingContainerSummaryView summaryView = new StagingContainerSummaryView();
        CurrentValidationView view = new CurrentValidationView();
        ControllerContainer.setCurrentValidationView(view);
        ControllerContainer.setStagingContainerSummaryView(summaryView);

        RootPanel.get().add(summaryView);
        RootPanel.get().add(view);

        ControllerContainer.get().getSummaryController().refreshView();
        ControllerContainer.get().getCurrentValidationController().refreshView();

        assertEquals(CurrentValidationView.Status.None, view.getStatus());

    }
    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.stagingarea.Stagingarea"; //$NON-NLS-1$
    }
}

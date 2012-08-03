package org.talend.mdm.webapp.stagingarea.client.view;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

public class StagingAreaTaskItemsViewTest extends GWTTestCase {

    public void testA() {
        PreviousValidationView view = new PreviousValidationView();

        RootPanel.get().add(view);
    }

    public String getModuleName() {
        return "org.talend.mdm.webapp.stagingarea.Stagingarea"; //$NON-NLS-1$
    }
}

package org.talend.mdm.webapp.welcomeportal.client;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.Window;


public class GenerateContainer {

    private static ContentPanel instance;

    public static void generateContentPanel() {
        if (instance != null) {
            instance.removeFromParent();
        }

        instance = new ContentPanel() {

            public void onAttach() {
                monitorWindowResize = true;
                Window.enableScrolling(true);
                // setSize(Window.getClientWidth(), Window.getClientHeight());
                super.onAttach();
                GXT.hideLoadingPanel("loading");//$NON-NLS-1$
            }

            // protected void onWindowResize(int width, int height) {
            // setSize(width, height);
            // this.doLayout(true);
            // }
        };
        instance.setId("welcomeportal"); //$NON-NLS-1$       
    }

    public static ContentPanel getContentPanel() {
        return instance;
    }
}

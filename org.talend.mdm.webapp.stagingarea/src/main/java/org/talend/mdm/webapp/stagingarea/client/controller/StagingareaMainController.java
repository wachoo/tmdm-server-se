package org.talend.mdm.webapp.stagingarea.client.controller;

import org.talend.mdm.webapp.stagingarea.client.view.StagingareaMainView;


public class StagingareaMainController extends AbstractController {

    private StagingareaMainView view;

    public StagingareaMainController(StagingareaMainView view) {
        this.view = view;
    }

    public void doLayout() {
        view.doLayout();
    }
}

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
package org.talend.mdm.webapp.stagingarea.client.controller;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.stagingarea.client.model.StagingContainerModel;
import org.talend.mdm.webapp.stagingarea.client.rest.RestServiceHandler;
import org.talend.mdm.webapp.stagingarea.client.view.StagingContainerSummaryView;

public class StagingContainerSummaryController extends AbstractController {

    private StagingContainerSummaryView view;


    public StagingContainerSummaryController(StagingContainerSummaryView view) {
        setBindingView(view);
        this.view = (StagingContainerSummaryView) bindingView;
    }

    public void refreshView() {
        RestServiceHandler.get().getStagingContainerSummary(view.getContainer(), view.getDataModel(),
                new SessionAwareAsyncCallback<StagingContainerModel>() {

            public void onSuccess(StagingContainerModel result) {
                view.refresh(result);
                ControllerContainer.get().getCurrentValidationController().refreshView(view.getContainer());
                ControllerContainer.get().getPreviousExecutionController().setDataContainer(view.getContainer());
            }
        });
    }

    public void startValidation() {
        RestServiceHandler.get().runValidationTask(view.getContainer(), view.getDataModel(), null,
                new SessionAwareAsyncCallback<String>() {
            public void onSuccess(String result) {
                        ControllerContainer.get().getCurrentValidationController().refreshView(view.getContainer());
            }
        });
    }

    public void setEnabledStartValidation(boolean enabled) {
        if (enabled) {
            view.enabledStartValidation();
        } else {
            view.disabledStartValidation();
        }
    }
}

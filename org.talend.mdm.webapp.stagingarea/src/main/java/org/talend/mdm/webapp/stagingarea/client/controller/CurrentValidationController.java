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
import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaValidationModel;
import org.talend.mdm.webapp.stagingarea.client.rest.RestServiceHandler;
import org.talend.mdm.webapp.stagingarea.client.view.CurrentValidationView;

import com.google.gwt.user.client.Timer;


public class CurrentValidationController extends AbstractController {

    RestServiceHandler handler = new RestServiceHandler();

    private CurrentValidationView view;

    private String stagingId;

    private Timer timer;

    public CurrentValidationController(CurrentValidationView view) {
        this.view = view;
        timer = new Timer() {

            @Override
            public void run() {
                refreshView();
            }
        };
    }

    public void autoRefresh(boolean auto) {
        if (stagingId != null) {
            if (auto) {
                timer.scheduleRepeating(1000);
            } else {
                timer.cancel();
            }
        }
    }

    public void refreshView() {

        handler.getValidationTaskStatus("TestDataContainer", new SessionAwareAsyncCallback<StagingAreaValidationModel>() { //$NON-NLS-1$

                    public void onSuccess(StagingAreaValidationModel result) {
                        if (result != null) {
                            view.setStatus(CurrentValidationView.Status.HasValidation);
                            view.refresh(result);
                        } else {
                            view.setStatus(CurrentValidationView.Status.None);
                        }
                    }
                });
    }
}

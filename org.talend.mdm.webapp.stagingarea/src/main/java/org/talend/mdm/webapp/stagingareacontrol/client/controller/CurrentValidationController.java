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
package org.talend.mdm.webapp.stagingareacontrol.client.controller;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.UserContextModel;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.StagingareaControl;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaValidationModel;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.RestServiceHandler;
import org.talend.mdm.webapp.stagingareacontrol.client.view.CurrentValidationView;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Timer;


public class CurrentValidationController extends AbstractController {

    private CurrentValidationView view;

    private Timer summaryTimer;
    
    private Timer currentValidationTimer;

    private boolean auto = true;

    private interface Callback {

        void callback();
    }

    public CurrentValidationController(final CurrentValidationView view) {
        setBindingView(view);
        this.view = (CurrentValidationView) bindingView;
        summaryTimer = new Timer() {
            
            @Override
            public void run() {
                if (isOrHasChild(Document.get().getBody(), view.getElement())) {
                    if (auto && ControllerContainer.get().getSummaryController().isEnabledStartValidation()) {
                        ControllerContainer.get().getSummaryController().refreshView();
                    }
                    schedule(StagingareaControl.getStagingAreaConfig().getRefreshIntervals());
                } else {
                    this.cancel();
                }
            }
        };
        currentValidationTimer = new Timer() {

            @Override
            public void run() {
                if (isOrHasChild(Document.get().getBody(), view.getElement())) {
                    refreshView(new Callback() {
                        public void callback() {
                            if (auto) {
                                schedule(StagingareaControl.getStagingAreaConfig().getRefreshIntervals());
                            }
                        }
                    });
                } else {
                    this.cancel();
                }
            }
        };
    }

    private boolean isOrHasChild(Node parent, Node child) {
        Node current = child;
        while (current != null) {
            if (current == parent) {
                return true;
            }
            current = current.getParentNode();
        }
        return false;
    }

    public void autoRefresh(boolean auto) {
        if (this.auto != auto) {
            this.auto = auto;
            if (auto) {
                if (!ControllerContainer.get().getSummaryController().isEnabledStartValidation()) {
                    currentValidationTimer.run();
                }
                summaryTimer.run();
            }
        }
    }

    public void refreshView() {
        currentValidationTimer.run();
        summaryTimer.run();
    }

    private void refreshView(final Callback callback) {
        UserContextModel ucx = UserContextUtil.getUserContext();
        RestServiceHandler.get().getValidationTaskStatus(ucx.getDataContainer(),
                new SessionAwareAsyncCallback<StagingAreaValidationModel>() {
                    public void onSuccess(StagingAreaValidationModel result) {
                        if (result != null) {
                            ControllerContainer.get().getSummaryController().setEnabledStartValidation(false);
                            view.setStatus(CurrentValidationView.Status.HasValidation);
                            view.refresh(result);
                            if (callback != null) {
                                callback.callback();
                            }
                        } else {
                            ControllerContainer.get().getSummaryController().setEnabledStartValidation(true);
                            view.setStatus(CurrentValidationView.Status.None);
                            ControllerContainer.get().getPreviousExecutionController().searchByBeforeDate();
                        }
                    }

                    @Override
                    protected void doOnFailure(Throwable caught) {
                        ControllerContainer.get().getSummaryController().setEnabledStartValidation(true);
                        view.setStatus(CurrentValidationView.Status.None);
                        ControllerContainer.get().getPreviousExecutionController().searchByBeforeDate();
                    }
                });
    }

    public void cancelValidation() {
        UserContextModel ucx = UserContextUtil.getUserContext();
        RestServiceHandler.get().cancelValidationTask(ucx.getDataContainer(),
                new SessionAwareAsyncCallback<Boolean>() {
            public void onSuccess(Boolean result) {
                if (result){
                    ControllerContainer.get().getSummaryController().setEnabledStartValidation(true);
                    ControllerContainer.get().getSummaryController().refreshView();
                    view.setStatus(CurrentValidationView.Status.None);
                    ControllerContainer.get().getPreviousExecutionController().searchByBeforeDate();
                }
            }
        });
    }
}

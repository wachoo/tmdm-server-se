// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.RestServiceHandler;
import org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView;

public class StagingContainerSummaryController extends AbstractController {

    private StagingContainerSummaryView view;

    public StagingContainerSummaryController(StagingContainerSummaryView view) {
        setBindingView(view);
        this.view = (StagingContainerSummaryView) bindingView;
    }
    
    public void refreshView() {
        final UserContextModel ucx = UserContextUtil.getUserContext();
        RestServiceHandler.get().getStagingContainerSummary(ucx.getDataContainer(), ucx.getDataModel(),
                new SessionAwareAsyncCallback<StagingContainerModel>() {

                    public void onSuccess(StagingContainerModel result) {
                        view.refresh(result);
                    }
                });
    }

    public void startValidation() {
        final UserContextModel ucx = UserContextUtil.getUserContext();
        RestServiceHandler.get().runValidationTask(ucx.getDataContainer(), ucx.getDataModel(), null,
                new SessionAwareAsyncCallback<String>() {
            public void onSuccess(String result) {
                ControllerContainer.get().getCurrentValidationController().refreshView();
            }
        });
    }

    public native void openInvalidRecordToBrowseRecord(int state)/*-{
		if ($wnd.amalto.stagingareabrowse
				&& $wnd.amalto.stagingareabrowse.StagingareaBrowse) {
			$wnd.amalto.stagingareabrowse.StagingareaBrowse.init(state);
		}
    }-*/;

    public void setEnabledStartValidation(boolean enabled) {
        view.setEnabledStartValidation(enabled);
    }

    public boolean isEnabledStartValidation() {
        return view.isEnabledStartValidation();
    }
}

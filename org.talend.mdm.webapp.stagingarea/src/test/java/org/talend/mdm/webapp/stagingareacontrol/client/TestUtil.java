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
package org.talend.mdm.webapp.stagingareacontrol.client;

import org.talend.mdm.webapp.base.client.rest.ClientResourceWrapper;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaConfiguration;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.StagingRestServiceHandler;
import org.talend.mdm.webapp.stagingareacontrol.client.view.CurrentValidationView;
import org.talend.mdm.webapp.stagingareacontrol.client.view.PreviousExecutionView;
import org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView;
import org.talend.mdm.webapp.stagingareacontrol.client.view.StagingareaMainView;

@SuppressWarnings("nls")
public class TestUtil {

    public static native void setContextModel(StagingAreaConfiguration cm)/*-{
		@org.talend.mdm.webapp.stagingareacontrol.client.StagingareaControl::stagingAreaConfig = cm;
    }-*/;

    public static void initContainer() {

        StagingAreaConfiguration cm = new StagingAreaConfiguration();
        cm.setRefreshIntervals(1000);
        setContextModel(cm);

        StagingContainerSummaryView summaryView = new StagingContainerSummaryView();
        CurrentValidationView validationView = new CurrentValidationView();
        StagingareaMainView mainView = new StagingareaMainView();
        PreviousExecutionView execView = new PreviousExecutionView();

        ControllerContainer.initController(mainView, summaryView, validationView, execView);

    }

    public static void initUserContext(String dataContainer, String dataModel) {
        UserContextUtil.setDataContainer(dataContainer);
        UserContextUtil.setDataModel(dataModel);
        UserContextUtil.setDateTimeFormat("MM/dd/yyyy HH:mm:ss");
    }

    public static void initRestServices(ClientResourceWrapper resourceWrapper) {
        StagingRestServiceHandler.get().setClient(resourceWrapper);
    }

}

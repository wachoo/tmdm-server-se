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
package org.talend.mdm.webapp.stagingarea.client;

import java.util.Arrays;

import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingarea.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingarea.client.model.ContextModel;
import org.talend.mdm.webapp.stagingarea.client.rest.ClientResourceWrapper;
import org.talend.mdm.webapp.stagingarea.client.rest.RestServiceHandler;
import org.talend.mdm.webapp.stagingarea.client.view.CurrentValidationView;
import org.talend.mdm.webapp.stagingarea.client.view.PreviousExecutionView;
import org.talend.mdm.webapp.stagingarea.client.view.StagingContainerSummaryView;
import org.talend.mdm.webapp.stagingarea.client.view.StagingareaMainView;

import com.extjs.gxt.ui.client.widget.Label;

@SuppressWarnings("nls")
public class TestUtil {

    public static native void setContextModel(ContextModel cm)/*-{
        @org.talend.mdm.webapp.stagingarea.client.Stagingarea::contextModel = cm;
    }-*/;

    public static void initContainer() {

        ContextModel cm = new ContextModel();
        cm.setDataContainer(Arrays.asList("Product", "TestDataContainer", "DStar"));
        cm.setDataModels(Arrays.asList("Product", "TestDataModel", "DStar"));
        cm.setRefreshIntervals(1000);
        setContextModel(cm);

        Label emptyChart = new Label();
        StagingContainerSummaryView.setChart(emptyChart);
        StagingContainerSummaryView summaryView = new StagingContainerSummaryView();
        CurrentValidationView validationView = new CurrentValidationView();
        StagingareaMainView mainView = new StagingareaMainView();
        PreviousExecutionView execView = new PreviousExecutionView();

        ControllerContainer.initController(mainView, summaryView, validationView, execView);

    }

    public static void initUserContext(String dataContainer, String dataModel) {
        UserContextUtil.setDataContainer(dataContainer);
        UserContextUtil.setDataModel(dataModel);
    }

    public static void initRestServices(ClientResourceWrapper resourceWrapper) {
        RestServiceHandler.get().setClient(resourceWrapper);
    }

}

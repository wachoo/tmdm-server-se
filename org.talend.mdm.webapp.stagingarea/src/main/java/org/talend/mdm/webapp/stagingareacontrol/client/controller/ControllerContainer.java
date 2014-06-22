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

import org.talend.mdm.webapp.stagingareacontrol.client.view.CurrentValidationView;
import org.talend.mdm.webapp.stagingareacontrol.client.view.PreviousExecutionView;
import org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView;
import org.talend.mdm.webapp.stagingareacontrol.client.view.StagingareaMainView;

public class ControllerContainer {

    private static StagingareaMainController stagingareaMainController;

    private static StagingContainerSummaryController stagingContainerSummaryController;

    private static CurrentValidationController currentValidationController;

    private static PreviousExecutionController previousExecutionController;

    private static ControllerContainer instance;

    public static void initController(StagingareaMainView mainView, StagingContainerSummaryView summaryView,
            CurrentValidationView currentView,
            PreviousExecutionView previousView) {
        setStagingareaMainView(mainView);
        setStagingContainerSummaryView(summaryView);
        setCurrentValidationView(currentView);
        setPreviousExecutionView(previousView);
    }

    public static void setStagingareaMainView(StagingareaMainView mainView) {
        stagingareaMainController = new StagingareaMainController(mainView);
    }

    public static void setStagingContainerSummaryView(StagingContainerSummaryView summaryView) {
        stagingContainerSummaryController = new StagingContainerSummaryController(summaryView);
    }

    public static void setCurrentValidationView(CurrentValidationView currentView) {
        currentValidationController = new CurrentValidationController(currentView);
    }

    public static void setPreviousExecutionView(PreviousExecutionView previousView) {
        previousExecutionController = new PreviousExecutionController(previousView);
    }

    private ControllerContainer() {

    }

    public static ControllerContainer get() {
        if (instance == null) {
            instance = new ControllerContainer();
        }
        return instance;
    }

    public StagingareaMainController getStagingareaMainController() {
        return stagingareaMainController;
    }

    public StagingContainerSummaryController getSummaryController() {
        return stagingContainerSummaryController;
    }

    public CurrentValidationController getCurrentValidationController() {
        return currentValidationController;
    }

    public PreviousExecutionController getPreviousExecutionController() {
        return previousExecutionController;
    }
}

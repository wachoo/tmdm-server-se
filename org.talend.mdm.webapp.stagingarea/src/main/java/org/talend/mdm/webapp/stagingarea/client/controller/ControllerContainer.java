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

import org.talend.mdm.webapp.stagingarea.client.view.CurrentValidationView;
import org.talend.mdm.webapp.stagingarea.client.view.PreviousExecutionView;
import org.talend.mdm.webapp.stagingarea.client.view.StagingContainerSummaryView;

public class ControllerContainer {

    private static StagingContainerSummaryController stagingContainerSummaryController;

    private static CurrentValidationController currentValidationController;

    private static PreviousExecutionController previousExecutionController;

    private static ControllerContainer instance;

    public static void initController(StagingContainerSummaryView summaryView, CurrentValidationView currentView,
            PreviousExecutionView previousView) {
        stagingContainerSummaryController = new StagingContainerSummaryController(summaryView);
        currentValidationController = new CurrentValidationController(currentView);
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

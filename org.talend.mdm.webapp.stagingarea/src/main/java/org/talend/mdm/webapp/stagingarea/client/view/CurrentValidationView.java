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
package org.talend.mdm.webapp.stagingarea.client.view;

import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaExecutionModel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.user.client.ui.Grid;


public class CurrentValidationView extends AbstractView {

    private StagingAreaExecutionModel stagingAreaExecutionModel;

    private Label autoRefeshLabel;

    private ToggleButton toggle;
    
    private Label startDateLabel;

    private Label recordToProcessLabel;

    private Label performanceLabel;
    
    private Label etaLabel;

    private ProgressBar progressBar;

    @Override
    protected void initComponents() {
        autoRefeshLabel = new Label("Auto refresh"); //$NON-NLS-1$
        toggle = new ToggleButton("OFF");
        startDateLabel = new Label("Start date: <start_date>");
        recordToProcessLabel = new Label("Record to process: <record_to_process>");
        performanceLabel = new Label("Performance: <record_performance>");
        etaLabel = new Label("ETA: 28m 05s");
        progressBar = new ProgressBar();
    }

    @Override
    protected void registerEvent() {
        toggle.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                toggle.setText(toggle.isPressed() ? "ON" : "OFF"); //$NON-NLS-1$//$NON-NLS-2$
            }
        });
    }

    @Override
    protected void initLayout() {
        mainPanel.setLayout(new VBoxLayout());
        Grid rowGrid = new Grid(1, 2);
        rowGrid.setWidget(0, 0, autoRefeshLabel);
        rowGrid.setWidget(0, 1, toggle);
        mainPanel.add(rowGrid);

        mainPanel.add(startDateLabel);
        mainPanel.add(recordToProcessLabel);
        mainPanel.add(performanceLabel);
        mainPanel.add(etaLabel);
        mainPanel.add(progressBar);
    }

    public void refresh(StagingAreaExecutionModel stagingAreaExecutionModel) {
        this.stagingAreaExecutionModel = stagingAreaExecutionModel;

    }
}

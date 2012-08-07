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

import java.util.Date;

import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaValidationModel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.ui.Grid;


public class CurrentValidationView extends AbstractView {

    private static final long SECOND = 1000L;
    
    private static final long MINITE = SECOND * 60;
    
    private static final long HOUR = MINITE * 60;
    
    private static final long DAY = HOUR * 24;

    private CardLayout cardLayout = new CardLayout();

    private FormPanel formPanel = new FormPanel();

    private ContentPanel defaultMessagePanel = new ContentPanel();

    private StagingAreaValidationModel stagingAreaValidationModel;

    private Label autoRefeshLabel;

    private ToggleButton toggle;
    
    private DateField startDateField;

    private NumberField recordToProcessField;

    private NumberField invalidField;
    
    private TextField<String> etaField;

    private ProgressBar progressBar;

    @Override
    protected void initComponents() {
        autoRefeshLabel = new Label("Auto refresh"); //$NON-NLS-1$
        toggle = new ToggleButton("OFF"); //$NON-NLS-1$
        startDateField = new DateField();
        startDateField.setEnabled(false);
        startDateField.setFieldLabel("Start Date"); //$NON-NLS-1$
        recordToProcessField = new NumberField();
        recordToProcessField.setEnabled(false);
        recordToProcessField.setFieldLabel("Record to process"); //$NON-NLS-1$
        invalidField = new NumberField();
        invalidField.setEnabled(false);
        invalidField.setFieldLabel("Invalid record"); //$NON-NLS-1$
        etaField = new TextField<String>();
        etaField.setReadOnly(true);
        etaField.setFieldLabel("ETA"); //$NON-NLS-1$
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

        mainPanel.setLayout(cardLayout);

        Grid rowGrid = new Grid(1, 2);
        rowGrid.setWidget(0, 0, autoRefeshLabel);
        rowGrid.setWidget(0, 1, toggle);
        formPanel.add(rowGrid);

        formPanel.add(startDateField);
        formPanel.add(recordToProcessField);
        formPanel.add(invalidField);
        formPanel.add(etaField);
        FormData progressData = new FormData();
        progressData.setMargins(new Margins(3));
        formPanel.add(progressBar, progressData);
        progressBar.setWidth("100%"); //$NON-NLS-1$

        mainPanel.add(formPanel);

        defaultMessagePanel.setLayout(new CenterLayout());
        defaultMessagePanel.add(new Label("No validation is being performed, please click on \"Start validation\"")); //$NON-NLS-1$

        mainPanel.add(defaultMessagePanel);

        mainPanel.setHeight(180);
        mainPanel.setBodyBorder(false);
    }

    @SuppressWarnings("deprecation")
    public void refresh(StagingAreaValidationModel stagingAreaValidationModel) {
        this.stagingAreaValidationModel = stagingAreaValidationModel;
        cardLayout.setActiveItem(formPanel);
        startDateField.setValue(stagingAreaValidationModel.getStartDate());
        recordToProcessField.setValue(stagingAreaValidationModel.getProcessedRecords());
        invalidField.setValue(stagingAreaValidationModel.getInvalidRecords());

        Date startDate = stagingAreaValidationModel.getStartDate();
        Date currentDate = new Date();

        int process = stagingAreaValidationModel.getProcessedRecords();
        int total = stagingAreaValidationModel.getTotalRecord();
        double percentage =  process * 1.0D / total;
        long costTime = currentDate.getTime() - startDate.getTime();
        long etaTime = (long) (costTime / percentage) - costTime;

        int day = (int) (etaTime / DAY);
        int hour = (int) ((etaTime % DAY) / HOUR);
        int minite = (int) ((etaTime % DAY) % HOUR / MINITE);
        int second = (int) (etaTime % MINITE / SECOND);

        StringBuffer buffer = new StringBuffer();
        if (day > 0){
            buffer.append(day + "d "); //$NON-NLS-1$
        }
        if(hour > 0){
            buffer.append(hour + "h "); //$NON-NLS-1$
        }
        if (minite > 0){
            buffer.append(minite + "m "); //$NON-NLS-1$
        }
        if (second > 0){
            buffer.append(second + "s"); //$NON-NLS-1$
        }
        etaField.setValue(buffer.toString());
        
        progressBar.updateProgress(percentage, "process " + process + "/ total " + total + ""); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }

    public void showDefaultMessage() {
        cardLayout.setActiveItem(defaultMessagePanel);
    }
}

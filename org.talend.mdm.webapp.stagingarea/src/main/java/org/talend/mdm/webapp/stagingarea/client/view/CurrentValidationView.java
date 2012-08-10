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

import org.talend.mdm.webapp.stagingarea.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaValidationModel;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.Grid;


public class CurrentValidationView extends AbstractView {

    public enum Status {
        None,
        HasValidation
    };

    private Status status = Status.None;

    private static final long SECOND = 1000L;
    
    private static final long MINITE = SECOND * 60;
    
    private static final long HOUR = MINITE * 60;
    
    private static final long DAY = HOUR * 24;

    private Button cancelButton;

    private CardLayout cardLayout;

    private ContentPanel contentPanel;

    private FormPanel formPanel;

    private ContentPanel defaultMessagePanel;

    private LabelField autoRefeshLabel;

    private ToggleButton toggle;
    
    private DateField startDateField;

    private NumberField recordToProcessField;

    private NumberField invalidField;
    
    private TextField<String> etaField;

    private ProgressBar progressBar;

    private StagingAreaValidationModel currentValidationModel;

    @Override
    protected void initComponents() {
        autoRefeshLabel = new LabelField(messages.auto_refresh());
        autoRefeshLabel.setWidth(100);
        toggle = new ToggleButton(messages.off());
        toggle.setWidth(210);
        startDateField = new DateField();
        startDateField.setReadOnly(true);
        startDateField.setFieldLabel(messages.start_date());
        recordToProcessField = new NumberField();
        recordToProcessField.setReadOnly(true);
        recordToProcessField.setFieldLabel(messages.record_to_process());
        invalidField = new NumberField();
        invalidField.setReadOnly(true);
        invalidField.setFieldLabel(messages.invalid_record());
        etaField = new TextField<String>();
        etaField.setReadOnly(true);
        etaField.setFieldLabel(messages.eta());
        progressBar = new ProgressBar();
        cancelButton = new Button(messages.cancel());
        cancelButton.setStyleAttribute("margin-top", "10px"); //$NON-NLS-1$//$NON-NLS-2$
        cancelButton.setSize(200, 30);

        contentPanel = new ContentPanel();
        contentPanel.setHeaderVisible(false);
        contentPanel.setBodyBorder(false);
        TableLayout contentLayout = new TableLayout(2);
        contentLayout.setWidth("100%"); //$NON-NLS-1$
        contentPanel.setLayout(contentLayout);

        formPanel = new FormPanel();
        formPanel.setLabelWidth(100);
        formPanel.setHeaderVisible(false);
        formPanel.setBodyBorder(false);

        defaultMessagePanel = new ContentPanel();
        defaultMessagePanel.setLayout(new CenterLayout());
        defaultMessagePanel.setBodyBorder(false);
        defaultMessagePanel.setHeaderVisible(false);
        defaultMessagePanel.add(new Label(messages.no_validation()));
    }

    @Override
    protected void registerEvent() {
        toggle.addListener(Events.Toggle, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                toggle.setText(toggle.isPressed() ? messages.on() : messages.off());
                ControllerContainer.get().getCurrentValidationController().autoRefresh(toggle.isPressed());
            }
        });
        cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                MessageBox.confirm(messages.please_confirm(), messages.confirm_message(), new Listener<MessageBoxEvent>() {
                    public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.YES)){
                            ControllerContainer.get().getCurrentValidationController().cancelValidation();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void initLayout() {
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        Grid rowGrid = new Grid(1, 2);
        rowGrid.setWidget(0, 0, autoRefeshLabel);
        rowGrid.setWidget(0, 1, toggle);

        formPanel.add(rowGrid);
        formPanel.add(startDateField);
        formPanel.add(recordToProcessField);
        formPanel.add(invalidField);
        formPanel.add(etaField);

        TableData fpData = new TableData();
        fpData.setWidth("400px"); //$NON-NLS-1$
        contentPanel.add(formPanel, fpData);

        TableData cb = new TableData();
        cb.setVerticalAlign(VerticalAlignment.TOP);
        contentPanel.add(cancelButton, cb);
        TableData progressData = new TableData();
        progressData.setColspan(2);
        contentPanel.add(progressBar, progressData);

        mainPanel.add(new Label());
        mainPanel.add(defaultMessagePanel);
        mainPanel.add(contentPanel);
        mainPanel.setBodyBorder(false);
    }

    public void refresh(StagingAreaValidationModel stagingAreaValidationModel) {
        cardLayout.setActiveItem(formPanel);
        currentValidationModel = stagingAreaValidationModel;
        startDateField.setValue(stagingAreaValidationModel.getStartDate());
        recordToProcessField.setValue(stagingAreaValidationModel.getProcessedRecords());
        invalidField.setValue(stagingAreaValidationModel.getInvalidRecords());

        Date startDate = stagingAreaValidationModel.getStartDate();
        Date currentDate = new Date();

        final int process = stagingAreaValidationModel.getProcessedRecords();
        final int total = stagingAreaValidationModel.getTotalRecord();
        final double percentage = process * 1.0D / total;
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
        progressBar.updateProgress(percentage, messages.percentage(process, total));
    }


    public void setStatus(Status status) {

        if (status == Status.None) {
            toggle.toggle(false);
            startDateField.clear();
            recordToProcessField.clear();
            invalidField.clear();
            etaField.clear();
            progressBar.reset();
            mainPanel.setHeight(30);
            cardLayout.setActiveItem(defaultMessagePanel);
        } else {
            mainPanel.setHeight(190);
            cardLayout.setActiveItem(contentPanel);
        }
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public DateField getStartDateField() {
        return startDateField;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public StagingAreaValidationModel getCurrentValidationModel() {
        return currentValidationModel;
    }
}

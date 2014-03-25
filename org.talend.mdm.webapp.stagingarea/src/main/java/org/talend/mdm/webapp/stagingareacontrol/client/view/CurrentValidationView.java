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
package org.talend.mdm.webapp.stagingareacontrol.client.view;

import org.talend.mdm.webapp.stagingareacontrol.client.GenerateContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaValidationModel;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.i18n.client.NumberFormat;


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

    private DateField startDateField;

    private NumberField recordToProcessField;

    private NumberField invalidField;

    private ProgressBar progressBar;

    private StagingAreaValidationModel currentValidationModel;

    @Override
    protected void initComponents() {
        startDateField = new DateField();
        startDateField.setReadOnly(true);
        startDateField.setFieldLabel(messages.start_date());
        recordToProcessField = new NumberField();
        recordToProcessField.setReadOnly(true);
        recordToProcessField.setFieldLabel(messages.record_to_process());
        invalidField = new NumberField();
        invalidField.setReadOnly(true);
        invalidField.setFieldLabel(messages.invalid_record());
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
        formPanel.setLabelWidth(120);
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

        formPanel.add(startDateField);
        formPanel.add(recordToProcessField);
        formPanel.add(invalidField);

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
        int total = stagingAreaValidationModel.getTotalRecord();
        if (total == 0) {
            return;
        }
        currentValidationModel = stagingAreaValidationModel;
        startDateField.setValue(stagingAreaValidationModel.getStartDate());
        recordToProcessField.setValue(stagingAreaValidationModel.getTotalRecord() - stagingAreaValidationModel.getProcessedRecords());
        invalidField.setValue(stagingAreaValidationModel.getInvalidRecords());

        int process = stagingAreaValidationModel.getProcessedRecords();
        double percentage = process * 1D / total;
        NumberFormat format = NumberFormat.getFormat("#0.00"); //$NON-NLS-1$
        double validPercentage = format.parse(format.format(percentage * 100));
        progressBar.updateProgress(percentage, validPercentage + " %"); //$NON-NLS-1$
    }

    public void setStatus(Status status) {

        if (status == Status.None) {
            startDateField.clear();
            recordToProcessField.clear();
            invalidField.clear();
            progressBar.reset();
            mainPanel.setHeight(30);
            cardLayout.setActiveItem(defaultMessagePanel);
        } else {
            mainPanel.setHeight(120);
            cardLayout.setActiveItem(contentPanel);
        }
        GenerateContainer.getContentPanel().layout(true);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public DateField getStartDateField() {
        return startDateField;
    }

    public ContentPanel getDefaultMessagePanel() {
        return defaultMessagePanel;
    }

    public ContentPanel getContentPanel() {
        return contentPanel;
    }

    public ContentPanel getActivePanel() {
        return (ContentPanel) cardLayout.getActiveItem();
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public StagingAreaValidationModel getCurrentValidationModel() {
        return currentValidationModel;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        ControllerContainer.get().getCurrentValidationController().refreshView();
    }
}

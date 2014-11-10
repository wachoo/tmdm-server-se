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
package org.talend.mdm.webapp.stagingarea.control.client.view;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.*;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import org.talend.mdm.webapp.stagingarea.control.client.GenerateContainer;
import org.talend.mdm.webapp.stagingarea.control.shared.controller.Controllers;
import org.talend.mdm.webapp.stagingarea.control.shared.event.ModelEvent;
import org.talend.mdm.webapp.stagingarea.control.shared.event.ModelEventHandler;
import org.talend.mdm.webapp.stagingarea.control.shared.model.StagingAreaValidationModel;

public class CurrentValidationView extends AbstractView implements ModelEventHandler {

    private final HandlerRegistration executionModelHook;

    private Button                    cancelButton;

    private CardLayout                cardLayout;

    private ContentPanel              contentPanel;

    private FormPanel                 formPanel;

    private ContentPanel              defaultMessagePanel;

    private DateField                 startDateField;

    private NumberField               recordToProcessField;

    private NumberField               invalidField;

    private ProgressBar               progressBar;

    public CurrentValidationView(StagingAreaValidationModel executionModel) {
        executionModelHook = executionModel.addModelEventHandler(this);
    }

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
        cancelButton.setStyleAttribute("margin-top", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
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
        // Message when no validation is being executed
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

                    @Override
                    public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                            Controllers.get().getValidationController().cancel();
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
        // Summary of records
        formPanel.add(startDateField);
        formPanel.add(recordToProcessField);
        formPanel.add(invalidField);
        TableData fpData = new TableData();
        fpData.setWidth("400px"); //$NON-NLS-1$
        contentPanel.add(formPanel, fpData);
        // Cancel button
        TableData cb = new TableData();
        cb.setVerticalAlign(VerticalAlignment.TOP);
        contentPanel.add(cancelButton, cb);
        // Progress bar
        TableData progressData = new TableData();
        progressData.setColspan(2);
        contentPanel.add(progressBar, progressData);
        // Set panels to main panel
        mainPanel.add(new Label());
        mainPanel.add(defaultMessagePanel);
        mainPanel.add(contentPanel);
        mainPanel.setBodyBorder(false);
    }

    @Override
    public void onModelEvent(ModelEvent e) {
        GwtEvent.Type<ModelEventHandler> type = e.getAssociatedType();
        if (type == ModelEvent.Types.VALIDATION_MODEL_CHANGED.getType()) {
            StagingAreaValidationModel currentValidationModel = e.getModel();
            int total = currentValidationModel.getTotalRecord();
            if (total == 0) {
                return;
            }
            startDateField.setValue(currentValidationModel.getStartDate());
            recordToProcessField.setValue(currentValidationModel.getTotalRecord() - currentValidationModel.getProcessedRecords());
            invalidField.setValue(currentValidationModel.getInvalidRecords());

            int process = currentValidationModel.getProcessedRecords();
            double percentage = process * 1D / total;
            NumberFormat format = NumberFormat.getFormat("#0.00"); //$NON-NLS-1$
            double validPercentage = format.parse(format.format(percentage * 100));
            progressBar.updateProgress(percentage, validPercentage + " %"); //$NON-NLS-1$
        }
        if (type == ModelEvent.Types.VALIDATION_CANCEL.getType() || type == ModelEvent.Types.VALIDATION_END.getType()) {
            startDateField.clear();
            recordToProcessField.clear();
            invalidField.clear();
            progressBar.reset();
            mainPanel.setHeight(30);
            cardLayout.setActiveItem(defaultMessagePanel);
            cancelButton.disable();
        }
        if (type == ModelEvent.Types.VALIDATION_START.getType()) {
            mainPanel.setHeight(120);
            cardLayout.setActiveItem(contentPanel);
            cancelButton.enable();
        }
        GenerateContainer.getContentPanel().layout(true);
    }

    // Called by JS
    public ContentPanel getContentPanel() {
        return contentPanel;
    }

    // Called by JS
    public ContentPanel getActivePanel() {
        return (ContentPanel) cardLayout.getActiveItem();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        executionModelHook.removeHandler();
    }
}

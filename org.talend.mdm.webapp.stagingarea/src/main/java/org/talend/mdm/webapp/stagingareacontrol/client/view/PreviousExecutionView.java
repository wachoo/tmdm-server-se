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
package org.talend.mdm.webapp.stagingareacontrol.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.talend.mdm.webapp.base.client.model.UserContextModel;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.base.client.widget.PagingToolBarEx;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.PreviousExecutionController;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaExecutionModel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;

public class PreviousExecutionView extends AbstractView {

    private static final int PAGE_SIZE = 10;

    private PagingToolBarEx taskPagingBar;

    private ToolBar bar;

    private Label beforeDateLabel;

    private DateField beforeDateField;

    private Button searchButton;

    private Grid<StagingAreaExecutionModel> taskGrid;

    private ColumnModel taskColumnModel;

    private UserContextModel ucx;

    private void buildColumns() {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        ColumnConfig startDateColumn = new ColumnConfig("start_date", messages.start_date(), 100); //$NON-NLS-1$
        if (ucx.getDateTimeFormat() != null) {
            startDateColumn.setDateTimeFormat(DateTimeFormat.getFormat(ucx.getDateTimeFormat()));
        }
        columns.add(startDateColumn);
        ColumnConfig endDateColumn = new ColumnConfig("end_date", messages.end_date(), 100); //$NON-NLS-1$
        if (ucx.getDateTimeFormat() != null) {
            endDateColumn.setDateTimeFormat(DateTimeFormat.getFormat(ucx.getDateTimeFormat()));
        }
        columns.add(endDateColumn);
        ColumnConfig processRecordsColumn = new ColumnConfig("processed_records", messages.process_records(), 100); //$NON-NLS-1$
        columns.add(processRecordsColumn);
        ColumnConfig invalidRecordsColumn = new ColumnConfig("invalid_records", messages.invalid_records(), 100); //$NON-NLS-1$
        columns.add(invalidRecordsColumn);

        ColumnConfig recordLeftColumn = new ColumnConfig("total_record", messages.total_record(), 100); //$NON-NLS-1$
        columns.add(recordLeftColumn);

        taskColumnModel = new ColumnModel(columns);
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        ucx = UserContextUtil.getUserContext();
        buildColumns();
        beforeDateLabel = new Label(messages.display_before());
        beforeDateField = new DateField();
        searchButton = new Button(messages.search());
        bar = new ToolBar();
        taskPagingBar = new PagingToolBarEx(PAGE_SIZE);
        taskGrid = new Grid<StagingAreaExecutionModel>(PreviousExecutionController.getClearStore(), taskColumnModel);
        taskGrid.setAutoHeight(true);
    }

    @Override
    protected void initLayout() {
        bar.add(beforeDateLabel);
        bar.add(beforeDateField);
        bar.add(searchButton);

        taskPagingBar.bind(PreviousExecutionController.getLoader());
        taskGrid.getView().setForceFit(true);
        taskGrid.setAutoExpandColumn(taskColumnModel.getColumn(0).getHeader());

        mainPanel.setLayout(new FitLayout());

        mainPanel.setHeaderVisible(false);
        mainPanel.setTopComponent(bar);
        mainPanel.add(taskGrid);
        mainPanel.setBottomComponent(taskPagingBar);
    }

    public Date getBeforeDate() {
        return beforeDateField.getValue();
    }

    public Button getSearchButton() {
        return searchButton;
    }

    @Override
    protected void registerEvent() {
        searchButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                ControllerContainer.get().getPreviousExecutionController().searchByBeforeDate();
            }
        });
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        ControllerContainer.get().getPreviousExecutionController().setDataContainer(ucx.getDataContainer());
    }
}

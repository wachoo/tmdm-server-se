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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingarea.client.rest.RestDataProxy;
import org.talend.mdm.webapp.stagingarea.client.rest.RestServiceHandler;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PreviousExecutionView extends AbstractView {

    private static final int PAGE_SIZE = 1;
    
    private PagingToolBar taskPagingBar;

    private ToolBar bar;

    private Label beforeDateLabel;

    private DateField beforeDateField;

    private Button searchButton;

    private Grid<StagingAreaExecutionModel> taskGrid;

    private ColumnModel taskColumnModel;
    
    private DataProxy<PagingLoadResult<StagingAreaExecutionModel>> proxy;

    private BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>> loader;

    ListStore<StagingAreaExecutionModel> taskStore;

    private void buildColumns() {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        ColumnConfig startDateColumn = new ColumnConfig("start_date", "Start Date", 100); //$NON-NLS-1$ //$NON-NLS-2$
        columns.add(startDateColumn);
        ColumnConfig endDateColumn = new ColumnConfig("end_date", "End Date", 100); //$NON-NLS-1$ //$NON-NLS-2$
        columns.add(endDateColumn);
        ColumnConfig processRecordsColumn = new ColumnConfig("processed_records", "Process Records", 100); //$NON-NLS-1$//$NON-NLS-2$
        columns.add(processRecordsColumn);
        ColumnConfig invalidRecordsColumn = new ColumnConfig("invalid_records", "Invalid Records", 100); //$NON-NLS-1$//$NON-NLS-2$
        columns.add(invalidRecordsColumn);

        ColumnConfig recordLeftColumn = new ColumnConfig("total_record", "Total Record", 100); //$NON-NLS-1$//$NON-NLS-2$
        columns.add(recordLeftColumn);

        taskColumnModel =  new ColumnModel(columns);
    }

    private void buildDataSource() {
        proxy = new RestDataProxy<PagingLoadResult<StagingAreaExecutionModel>>() {

            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<StagingAreaExecutionModel>> callback) {
                // final BasePagingLoadConfig pagingLoadConfig = (BasePagingLoadConfig) loadConfig;
                final Date beforeDate = beforeDateField.getValue();

                RestServiceHandler handler = new RestServiceHandler();

                handler.getStagingAreaExecutionsWithPaging("TestDataContainer", 1, 1, beforeDate, //$NON-NLS-1$
                        new SessionAwareAsyncCallback<List<StagingAreaExecutionModel>>() {

                            public void onSuccess(List<StagingAreaExecutionModel> result) {
                                BasePagingLoadResult<StagingAreaExecutionModel> pagingResult = new BasePagingLoadResult<StagingAreaExecutionModel>(
                                        result, 0, 1);
                                callback.onSuccess(pagingResult);
                            }
                        });
            }
        };

        loader = new BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>>(proxy);
        loader.setRemoteSort(true);
        taskStore = new ListStore<StagingAreaExecutionModel>(loader);
        taskStore.setKeyProvider(new ModelKeyProvider<StagingAreaExecutionModel>() {

            public String getKey(StagingAreaExecutionModel model) {
                return model.getId();
            }
        });
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        buildColumns();
        buildDataSource();
        beforeDateLabel = new Label("Display Before"); //$NON-NLS-1$
        beforeDateField = new DateField();
        searchButton = new Button("Search"); //$NON-NLS-1$
        bar = new ToolBar();
        taskPagingBar = new PagingToolBar(PAGE_SIZE);
        taskGrid = new Grid<StagingAreaExecutionModel>(taskStore, taskColumnModel);

    }

    @Override
    protected void initLayout() {
        bar.add(beforeDateLabel);
        bar.add(beforeDateField);
        bar.add(searchButton);

        taskPagingBar.bind(loader);

        taskGrid.setStateful(true);
        taskGrid.setStateId("grid"); //$NON-NLS-1$
        taskGrid.getView().setForceFit(true);
        taskGrid.setAutoExpandColumn(taskColumnModel.getColumn(0).getHeader());

        mainPanel.setLayout(new FitLayout());

        mainPanel.setHeaderVisible(false);
        mainPanel.setTopComponent(bar);
        mainPanel.add(taskGrid);
        mainPanel.setBottomComponent(taskPagingBar);
    }

    protected void registerEvent() {
        searchButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                loader.load();
            }
        });
    }
}

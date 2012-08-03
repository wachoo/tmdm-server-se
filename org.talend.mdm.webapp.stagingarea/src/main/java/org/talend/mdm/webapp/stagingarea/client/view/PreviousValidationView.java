package org.talend.mdm.webapp.stagingarea.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.talend.mdm.webapp.stagingarea.client.model.FakeData;
import org.talend.mdm.webapp.stagingarea.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingarea.client.rest.RestDataProxy;

import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
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

public class PreviousValidationView extends AbstractView {

    private static final int PAGE_SIZE = 10;
    
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
        ColumnConfig invalidRecordsColumn = new ColumnConfig("performance", "performance", 100); //$NON-NLS-1$//$NON-NLS-2$
        columns.add(invalidRecordsColumn);

        ColumnConfig recordLeftColumn = new ColumnConfig("record_left", "Record Left", 100); //$NON-NLS-1$//$NON-NLS-2$
        columns.add(recordLeftColumn);

        ColumnConfig elapsedTimeColumn = new ColumnConfig("elapsed_time", "Elapsed Time", 100); //$NON-NLS-1$//$NON-NLS-2$
        columns.add(elapsedTimeColumn);

        taskColumnModel =  new ColumnModel(columns);
    }

    private void buildDataSource() {
        proxy = new RestDataProxy<PagingLoadResult<StagingAreaExecutionModel>>() {

            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<StagingAreaExecutionModel>> callback) {
                final BasePagingLoadConfig pagingLoadConfig = (BasePagingLoadConfig) loadConfig;
                final Date beforeDate = beforeDateField.getValue();
                List<StagingAreaExecutionModel> tasks = FakeData.getTasks(beforeDate, pagingLoadConfig.getOffset(), pagingLoadConfig
                        .getLimit());
                BasePagingLoadResult<StagingAreaExecutionModel> loadResult = new BasePagingLoadResult<StagingAreaExecutionModel>(tasks,
                        pagingLoadConfig.getOffset(), FakeData.getTotal());
                callback.onSuccess(loadResult);
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

    protected void initView() {

        buildColumns();
        buildDataSource();

        beforeDateLabel = new Label("Display Before"); //$NON-NLS-1$
        beforeDateField = new DateField();
        searchButton = new Button("Search"); //$NON-NLS-1$
        bar = new ToolBar();

        bar.add(beforeDateLabel);
        bar.add(beforeDateField);
        bar.add(searchButton);

        taskPagingBar = new PagingToolBar(PAGE_SIZE);
        taskPagingBar.bind(loader);
        taskGrid = new Grid<StagingAreaExecutionModel>(taskStore, taskColumnModel);

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

    protected void initEvent() {
        searchButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                loader.load();
            }
        });
    }
}

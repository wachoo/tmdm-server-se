// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.exception.ParserException;
import org.talend.mdm.webapp.base.client.model.DataType;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.client.model.SimpleCriterion;
import org.talend.mdm.webapp.base.client.model.UserContextModel;
import org.talend.mdm.webapp.base.client.util.Parser;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.base.client.widget.ColumnAlignGrid;
import org.talend.mdm.webapp.base.client.widget.PagingToolBarEx;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.BrowseStagingRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.creator.CellEditorCreator;
import org.talend.mdm.webapp.browserecords.client.creator.CellRendererCreator;
import org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.OperatorConstants;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.RecordStatus;
import org.talend.mdm.webapp.browserecords.client.model.RecordStatusWrapper;
import org.talend.mdm.webapp.browserecords.client.model.RecordsPagingConfig;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.StagingConstant;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.util.ViewUtil;
import org.talend.mdm.webapp.browserecords.client.widget.filter.BooleanFilter;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.creator.FieldCreator;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetailUtil;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.task.StagingConstants;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.data.BaseFilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.ListFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

/**
 * created by yjli on 2014-1-24 Detailled comment
 * 
 */
public class StagingGridPanel extends ContentPanel {

    private Map<Integer, String> errorTitles;

    private QueryModel currentQueryModel;

    private PagingLoadConfig pagingLoadConfig;

    private boolean isPagingAccurate;

    private String taskId;

    private String concept;

    private String cluster;

    private ViewBean viewBean;

    private EntityModel entityModel;

    private GridFilters filters;

    private BrowseRecordsServiceAsync browseRecordService = (BrowseRecordsServiceAsync) Registry
            .get(BrowseRecords.BROWSERECORDS_SERVICE);

    private BrowseStagingRecordsServiceAsync browseStagingRecordService = (BrowseStagingRecordsServiceAsync) Registry
            .get(BrowseRecords.BROWSESTAGINGRECORDS_SERVICE);

    RpcProxy<PagingLoadResult<ItemBean>> proxy = new RpcProxy<PagingLoadResult<ItemBean>>() {

        @Override
        public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ItemBean>> callback) {
            pagingLoadConfig = (PagingLoadConfig) loadConfig;
            final QueryModel qm = generateQueryModel();
            qm.setPagingLoadConfig(copyPgLoad(pagingLoadConfig));
            int pageSize = pagingBar.getPageSize();
            qm.getPagingLoadConfig().setLimit(pageSize);
            qm.setLanguage(Locale.getLanguage());

            try {
                if (qm.getCriteria() == null) {
                    return;
                } else if (qm.getErrorValue() != null && !"".equals(qm.getErrorValue())) { //$NON-NLS-1$
                    MessageBox.alert(MessagesFactory.getMessages().search_field_error_title(), MessagesFactory.getMessages()
                            .search_field_error_info(qm.getErrorValue()), null);
                    callback.onSuccess(new BasePagingLoadResult<ItemBean>(new ArrayList<ItemBean>(), 0, 0));
                    return;
                }
                if (BrowseRecords.getStagingArea() != null) {
                    JavaScriptObject stagingArea = BrowseRecords.getStagingArea();
                    qm.setDataClusterPK(getDataContainer(stagingArea));
                    qm.setCriteria(getCriteria(stagingArea));
                }
                Parser.parse(qm.getCriteria());
            } catch (ParserException e) {
                MessageBox.alert(MessagesFactory.getMessages().error_title(), e.getMessage(), null);
                callback.onSuccess(new BasePagingLoadResult<ItemBean>(new ArrayList<ItemBean>(), 0, 0));
                return;
            }

            browseStagingRecordService.queryItemBeans(qm, Locale.getLanguage(),
                    new SessionAwareAsyncCallback<ItemBasePageLoadResult<ItemBean>>() {

                        @Override
                        public void onSuccess(ItemBasePageLoadResult<ItemBean> result) {
                            isPagingAccurate = result.isPagingAccurate();
                            callback.onSuccess(new BasePagingLoadResult<ItemBean>(result.getData(), result.getOffset(), result
                                    .getTotalLength()));
                            if (result.getTotalLength() == 0) {
                                ItemsMainTabPanel.getInstance().removeAll();
                            }
                            currentQueryModel = qm;
                        }

                        @Override
                        protected void doOnFailure(Throwable caught) {
                            super.doOnFailure(caught);
                            callback.onSuccess(new BasePagingLoadResult<ItemBean>(new ArrayList<ItemBean>(), 0, 0));
                            currentQueryModel = null;
                        }
                    });
        }
    };

    private void initErrorTitles() {
        UserContextModel ucx = UserContextUtil.getUserContext();
        BrowseRecordsMessages messages = MessagesFactory.getMessages();
        errorTitles = new HashMap<Integer, String>();
        errorTitles.put(RecordStatus.NEW.getStatusCode(), messages.status_000());
        errorTitles.put(RecordStatus.SUCCESS_IDENTIFIED_CLUSTERS.getStatusCode(), messages.status_201(ucx.getDataContainer()));
        errorTitles.put(RecordStatus.SUCCESS_MERGE_CLUSTERS.getStatusCode(), messages.status_202(ucx.getDataContainer()));
        errorTitles.put(RecordStatus.SUCCESS_MERGE_CLUSTER_TO_RESOLVE.getStatusCode(),
                messages.status_203(ucx.getDataContainer()));
        errorTitles.put(RecordStatus.SUCCESS_MERGED_RECORD.getStatusCode(), messages.status_204(ucx.getDataContainer()));
        errorTitles.put(RecordStatus.SUCCESS_VALIDATE.getStatusCode(), messages.status_205());
        errorTitles.put(RecordStatus.FAIL_IDENTIFIED_CLUSTERS.getStatusCode(), messages.status_401(ucx.getDataContainer()));
        errorTitles.put(RecordStatus.FAIL_MERGE_CLUSTERS.getStatusCode(), messages.status_402(ucx.getDataContainer()));
        errorTitles.put(RecordStatus.FAIL_VALIDATE_VALIDATION.getStatusCode(), messages.status_403());
        errorTitles.put(RecordStatus.FAIL_VALIDATE_CONSTRAINTS.getStatusCode(), messages.status_404());
    }

    private native String getDataContainer(JavaScriptObject stagingAreaConfig)/*-{
		return stagingAreaConfig.dataContainer;
    }-*/;

    private native String getCriteria(JavaScriptObject stagingAreaConfig)/*-{
		return stagingAreaConfig.criteria;
    }-*/;

    private RecordsPagingConfig copyPgLoad(PagingLoadConfig pconfig) {
        RecordsPagingConfig rpConfig = new RecordsPagingConfig();
        rpConfig.setLimit(pconfig.getLimit());
        rpConfig.setOffset(pconfig.getOffset());
        rpConfig.setSortDir(pconfig.getSortDir() == null ? "NONE" : pconfig.getSortDir().toString()); //$NON-NLS-1$
        rpConfig.setSortField(pconfig.getSortField());
        return rpConfig;
    }

    public QueryModel getCurrentQueryModel() {
        return currentQueryModel;
    }

    ModelKeyProvider<ItemBean> keyProvidernew = new ModelKeyProvider<ItemBean>() {

        @Override
        public String getKey(ItemBean model) {
            return model.getIds();
        }
    };

    PagingLoader<PagingLoadResult<ModelData>> loader;

    ListStore<ItemBean> store;

    protected Grid<ItemBean> grid;

    private ContentPanel panel;

    private final static int PAGE_SIZE = 20;

    private PagingToolBarEx pagingBar = null;

    public StagingGridPanel(String concept, String taskId) {
        this.concept = concept;
        this.taskId = taskId;
        this.cluster = BrowseRecords.getSession().getAppHeader().getDatacluster() + StorageAdmin.STAGING_SUFFIX;
        this.viewBean = BrowseRecords.getSession().getCurrentView();
        this.entityModel = BrowseRecords.getSession().getCurrentEntityModel();
        initPanel();
    }

    private void initPanel() {
        setLayout(new FitLayout());
        setHeaderVisible(false);
        setHeading(MessagesFactory.getMessages().staging_data_viewer_title());
        this.layout();
        initErrorTitles();
        ContentPanel gridPanel = generateGrid();
        add(gridPanel);
    }

    private List<ColumnConfig> generateColumnList() {
        List<ColumnConfig> columnConfigList = new ArrayList<ColumnConfig>();
        filters = new GridFilters();
        filters.setLocal(true);
        List<String> viewableXpaths = viewBean.getViewableXpaths();
        Map<String, TypeModel> dataTypes = entityModel.getMetaDataTypes();
        List<String> keys = Arrays.asList(entityModel.getKeys());
        for (String xpath : viewableXpaths) {
            TypeModel typeModel = dataTypes.get(xpath);

            ColumnConfig cc = new ColumnConfig(xpath, typeModel == null ? xpath : ViewUtil.getViewableLabel(Locale.getLanguage(),
                    typeModel), 200);
            if (typeModel instanceof SimpleTypeModel && !keys.contains(xpath) && !typeModel.isMultiOccurrence()) {
                Field<?> field = FieldCreator.createField((SimpleTypeModel) typeModel, null, false, Locale.getLanguage());

                CellEditor cellEditor = CellEditorCreator.createCellEditor(field);
                if (cellEditor != null) {
                    cc.setEditor(cellEditor);
                }
            }

            if (typeModel != null) {
                GridCellRenderer<ModelData> renderer = CellRendererCreator.createRenderer(typeModel, xpath);
                if (renderer != null) {
                    cc.setRenderer(renderer);
                }
            }
            if (typeModel == null || typeModel.isVisible()) {
                columnConfigList.add(cc);
            }

            filters.addFilter(createFilter(xpath, typeModel != null ? typeModel.getType() : DataTypeConstants.STRING));
        }

        String taskIdPath = entityModel.getConceptName() + StagingConstant.STAGING_TASKID;
        ColumnConfig groupColumn = new ColumnConfig(taskIdPath, MessagesFactory.getMessages().match_group(), 200);
        columnConfigList.add(groupColumn);
        filters.addFilter(createFilter(taskIdPath, DataTypeConstants.STRING));

        String statusPath = entityModel.getConceptName() + StagingConstant.STAGING_STATUS;
        ColumnConfig statusColumn = new ColumnConfig(statusPath, MessagesFactory.getMessages().status(), 200);
        statusColumn.setRenderer(new GridCellRenderer<ItemBean>() {

            @Override
            public Object render(ItemBean model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ItemBean> statusListStore, Grid<ItemBean> statusGrid) {
                com.google.gwt.user.client.ui.Grid g = new com.google.gwt.user.client.ui.Grid(1, 2);
                g.setCellPadding(0);
                g.setCellSpacing(0);
                String status = model.get(entityModel.getConceptName() + StagingConstant.STAGING_STATUS);

                if (status == null || status.trim().length() == 0) {
                    status = "0"; //$NON-NLS-1$
                }
                RecordStatusWrapper wrapper = new RecordStatusWrapper(RecordStatus.newStatus(Integer.valueOf(status)));

                String color = wrapper.getColor();

                if (color != null) {
                    g.getElement().getStyle().setColor(color);
                }
                if (wrapper.getIcon() != null) {
                    g.setWidget(0, 0, new Image(wrapper.getIcon()));
                }
                g.setText(0, 1, status);
                g.setTitle(status
                        + ": " + (errorTitles.get(Integer.valueOf(status)) == null ? "" : errorTitles.get(Integer.valueOf(status)))); //$NON-NLS-1$ //$NON-NLS-2$
                return g;
            }
        });
        columnConfigList.add(statusColumn);

        ListStore<ModelData> statusListFilterStore = new ListStore<ModelData>();
        statusListFilterStore.add(buildFilterItem(statusPath, String.valueOf(RecordStatus.NEW.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath, String.valueOf(RecordStatus.SUCCESS.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath,
                String.valueOf(RecordStatus.SUCCESS_IDENTIFIED_CLUSTERS.getStatusCode())));
        statusListFilterStore
                .add(buildFilterItem(statusPath, String.valueOf(RecordStatus.SUCCESS_MERGE_CLUSTERS.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath,
                String.valueOf(RecordStatus.SUCCESS_MERGE_CLUSTER_TO_RESOLVE.getStatusCode())));
        statusListFilterStore
                .add(buildFilterItem(statusPath, String.valueOf(RecordStatus.SUCCESS_MERGED_RECORD.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath, String.valueOf(RecordStatus.SUCCESS_VALIDATE.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath, String.valueOf(RecordStatus.SUCCESS_DELETED.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath, String.valueOf(RecordStatus.FAIL.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath,
                String.valueOf(RecordStatus.FAIL_IDENTIFIED_CLUSTERS.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath, String.valueOf(RecordStatus.FAIL_MERGE_CLUSTERS.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath,
                String.valueOf(RecordStatus.FAIL_VALIDATE_VALIDATION.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath,
                String.valueOf(RecordStatus.FAIL_VALIDATE_CONSTRAINTS.getStatusCode())));
        statusListFilterStore.add(buildFilterItem(statusPath, String.valueOf(RecordStatus.UNKNOWN.getStatusCode())));
        ListFilter statusFilter = new ListFilter(statusPath, statusListFilterStore);
        statusFilter.setDisplayProperty(statusPath);
        filters.addFilter(statusFilter);

        String sourcePath = entityModel.getConceptName() + StagingConstant.STAGING_SOURCE;
        ColumnConfig sourceColumn = new ColumnConfig(sourcePath, MessagesFactory.getMessages().source(), 200);
        columnConfigList.add(sourceColumn);

        ListStore<ModelData> sourceListFilterStore = new ListStore<ModelData>();
        sourceListFilterStore.add(buildFilterItem(sourcePath, StagingConstants.STAGING_MDM_SOURCE));
        ListFilter sourceFilter = new ListFilter(sourcePath, sourceListFilterStore);
        sourceFilter.setDisplayProperty(sourcePath);
        filters.addFilter(sourceFilter);

        for (ColumnConfig cc : columnConfigList) {
            final GridCellRenderer<ModelData> render = cc.getRenderer();

            GridCellRenderer<ModelData> renderProxy = new GridCellRenderer<ModelData>() {

                @Override
                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> listStore, Grid<ModelData> g) {
                    Object value = null;
                    if (render != null) {
                        value = render.render(model, property, config, rowIndex, colIndex, listStore, g);
                    } else {
                        value = model.get(property);
                    }
                    if (value instanceof String) {
                        ItemBean item = (ItemBean) model;
                        String matchGroup = model.get(item.getConcept() + StagingConstant.STAGING_TASKID);
                        if (matchGroup != null && matchGroup.length() != 0) {
                            String status = model.get(item.getConcept() + StagingConstant.STAGING_STATUS);
                            if ("205".equals(status) || "203".equals(status) || "204".equals(status)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                return "<b>" + value + "</b>"; //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        }
                    }
                    return value;
                }
            };
            cc.setRenderer(renderProxy);
        }
        return columnConfigList;
    }

    private ContentPanel generateGrid() {
        ContentPanel gridContainer = new ContentPanel(new FitLayout());
        ColumnModel cm = new ColumnModel(generateColumnList());
        gridContainer.setBodyBorder(false);
        gridContainer.setHeaderVisible(false);

        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        loader.setRemoteSort(true);

        store = new ListStore<ItemBean>(loader);
        store.setKeyProvider(keyProvidernew);

        int usePageSize = PAGE_SIZE;
        if (Cookies.getCookie(PagingToolBarEx.BROWSERECORD_PAGESIZE) != null) {
            usePageSize = Integer.parseInt(Cookies.getCookie(PagingToolBarEx.BROWSERECORD_PAGESIZE));
        }

        pagingBar = new PagingToolBarEx(usePageSize) {

            @Override
            protected void onLoad(LoadEvent event) {
                String of_word = MessagesFactory.getMessages().of_word();
                msgs.setDisplayMsg("{0} - {1} " + of_word + " " + (isPagingAccurate ? "" : "~") + "{2}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                super.onLoad(event);
            }
        };
        pagingBar.setBrowseRecordsGridCall(true);
        pagingBar.setHideMode(HideMode.VISIBILITY);
        pagingBar.getMessages().setDisplayMsg(MessagesFactory.getMessages().page_displaying_records());

        pagingBar.setVisible(false);
        pagingBar.bind(loader);
        gridContainer.setBottomComponent(pagingBar);
        grid = new ColumnAlignGrid<ItemBean>(store, cm);

        {
            grid.setStateful(true);
            AppHeader header = (AppHeader) BrowseRecords.getSession().get(UserSession.APP_HEADER);
            ViewBean vb = (ViewBean) BrowseRecords.getSession().get(UserSession.CURRENT_VIEW);
            grid.setStateId(header.getDatamodel() + "." + entityModel.getConceptName() + "." + vb.getViewPK()); //$NON-NLS-1$//$NON-NLS-2$
        }

        grid.getView().setForceFit(true);
        if (cm.getColumnCount() > 0) {
            grid.setAutoExpandColumn(cm.getColumn(0).getHeader());
        }

        grid.addListener(Events.RowDoubleClick, new Listener<GridEvent<ItemBean>>() {

            @Override
            public void handleEvent(GridEvent<ItemBean> ge) {
                int rowIndex = ge.getRowIndex();
                if (rowIndex != -1) {
                    ItemBean item = grid.getStore().getAt(rowIndex);
                    TreeDetailUtil.initStagingItemsDetailPanelById(MessagesFactory.getMessages().browse_staging_records(),
                            item.getIds(), concept, false, false);
                }
            }
        });

        grid.addListener(Events.Attach, new Listener<GridEvent<ItemBean>>() {

            @Override
            public void handleEvent(GridEvent<ItemBean> be) {
                PagingLoadConfig config = new BaseFilterPagingLoadConfig();
                config.setOffset(0);
                int pageSize = pagingBar.getPageSize();
                config.setLimit(pageSize);
                loader.load(config);
                pagingBar.setVisible(true);
            }
        });

        grid.setLoadMask(true);

        grid.getAriaSupport().setDescribedBy("abcdefg"); //$NON-NLS-1$
        grid.getAriaSupport().setLabelledBy(this.getHeader().getId() + "-label"); //$NON-NLS-1$

        grid.addPlugin(filters);
        gridContainer.add(grid);
        return gridContainer;
    }

    private QueryModel generateQueryModel() {
        SimpleCriterion criterion = new SimpleCriterion(StagingConstant.STAGING_TASKID, OperatorConstants.EQUALS_OPERATOR, taskId);
        QueryModel queryModel = new QueryModel();
        queryModel.setDataClusterPK(cluster);
        queryModel.setView(viewBean);
        queryModel.setModel(entityModel);
        queryModel.setCriteria(criterion.toString());
        return queryModel;
    }

    private Filter createFilter(String xpath, DataType type) {
        Filter filter;
        if (DataTypeConstants.BOOLEAN.equals(type) || DataTypeConstants.HEXBINARY.equals(type)
                || DataTypeConstants.BASE64BINARY.equals(type)) {
            filter = new BooleanFilter(xpath);
        } else if (DataTypeConstants.DURATION.equals(type) || DataTypeConstants.DATETIME.equals(type)
                || DataTypeConstants.TIME.equals(type) || DataTypeConstants.DATE.equals(type)
                || DataTypeConstants.GYEARMONTH.equals(type) || DataTypeConstants.GYEAR.equals(type)
                || DataTypeConstants.GDAY.equals(type) || DataTypeConstants.GMONTH.equals(type)) {
            filter = new DateFilter(xpath);
        } else if (DataTypeConstants.INTEGER.equals(type) || DataTypeConstants.NONPOSITIVEINTEGER.equals(type)
                || DataTypeConstants.NEGATIVEINTEGER.equals(type) || DataTypeConstants.NONNEGATIVEINTEGER.equals(type)
                || DataTypeConstants.LONG.equals(type) || DataTypeConstants.INT.equals(type)
                || DataTypeConstants.SHORT.equals(type) || DataTypeConstants.UNSIGNEDLONG.equals(type)
                || DataTypeConstants.UNSIGNEDINT.equals(type) || DataTypeConstants.UNSIGNEDSHORT.equals(type)
                || DataTypeConstants.POSITIVEINTEGER.equals(type) || DataTypeConstants.BYTE.equals(type)
                || DataTypeConstants.UNSIGNEDBYTE.equals(type) || DataTypeConstants.DECIMAL.equals(type)
                || DataTypeConstants.FLOAT.equals(type) || DataTypeConstants.DOUBLE.equals(type)) {
            filter = new NumericFilter(xpath);
        } else {
            filter = new StringFilter(xpath);
        }
        return filter;
    }

    private ModelData buildFilterItem(String xpath, String value) {
        ModelData model = new BaseModelData();
        model.set(xpath, value);
        return model;
    }
}

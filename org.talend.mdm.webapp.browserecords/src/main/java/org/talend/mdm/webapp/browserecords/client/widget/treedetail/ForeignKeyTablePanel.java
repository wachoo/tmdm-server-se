// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.exception.ParserException;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.client.util.Parser;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.PagingToolBarEx;
import org.talend.mdm.webapp.browserecords.client.widget.SaveRowEditor;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
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
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.state.StateManager;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ForeignKeyTablePanel extends ContentPanel {

    List<ItemBean> selectedItems = null;

    ForeignKeyItemsToolBar fkToolBar;

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    RpcProxy<PagingLoadResult<ItemBean>> proxy = new RpcProxy<PagingLoadResult<ItemBean>>() {

        @Override
        public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ItemBean>> callback) {
            final QueryModel qm = new QueryModel();
            fkToolBar.setQueryModel(qm);
            qm.setPagingLoadConfig((PagingLoadConfig) loadConfig);
            int pageSize = pagingBar.getPageSize();
            qm.getPagingLoadConfig().setLimit(pageSize);
            qm.setLanguage(Locale.getLanguage());

            // validate criteria on client-side first
            try {
                Parser.parse(qm.getCriteria());
            } catch (ParserException e) {
                MessageBox.alert(MessagesFactory.getMessages().error_title(), e.getMessage(), null);
                callback.onSuccess(new BasePagingLoadResult<ItemBean>(new ArrayList<ItemBean>(), 0, 0));
                return;
            }

            service.queryItemBeans(qm, new SessionAwareAsyncCallback<ItemBasePageLoadResult<ItemBean>>() {

                public void onSuccess(ItemBasePageLoadResult<ItemBean> result) {
                    callback.onSuccess(new BasePagingLoadResult<ItemBean>(result.getData(), result.getOffset(), result
                            .getTotalLength()));
                }

                @Override
                protected void doOnFailure(Throwable caught) {
                    super.doOnFailure(caught);
                    callback.onSuccess(new BasePagingLoadResult<ItemBean>(new ArrayList<ItemBean>(), 0, 0));
                }
            });
        }
    };

    ModelKeyProvider<ItemBean> keyProvidernew = new ModelKeyProvider<ItemBean>() {

        public String getKey(ItemBean model) {
            return model.getIds();
        }
    };

    PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);

    final ListStore<ItemBean> store = new ListStore<ItemBean>(loader);

    private Grid<ItemBean> grid;

    private RowEditor<ItemBean> re;

    ContentPanel gridContainer;

    private ContentPanel panel;

    private final static int PAGE_SIZE = 10;

    private PagingToolBarEx pagingBar = null;

    private Boolean gridUpdateLock = Boolean.FALSE;

    @Override
    protected void onDetach() {
        super.onDetach();
    }

    public ForeignKeyTablePanel() {
        setLayout(new FitLayout());
        setHeaderVisible(false);
        initPanel();

        store.setKeyProvider(keyProvidernew);

        loader.setRemoteSort(true);
        loader.addLoadListener(new LoadListener() {

            @Override
            public void loaderLoad(LoadEvent le) {
                if (store.getModels().size() > 0) {
                    if (selectedItems != null) {
                        grid.getSelectionModel().select(selectedItems, false);
                        ItemBean selectedItem = grid.getSelectionModel().getSelectedItem();
                        if (selectedItem == null) {
                            grid.getSelectionModel().select(0, false);
                        }
                    } else {
                        grid.getSelectionModel().select(0, false);
                    }
                } else {
                    fkToolBar.searchBut.setEnabled(true);
                    fkToolBar.getListPanel().getElement().setInnerHTML(""); //$NON-NLS-1$
                }
            }
        });
        this.layout();
    }

    private void initPanel() {
        panel = new ContentPanel();
        Html promptMsg = new Html("<div class=\"promptMsg\">" + MessagesFactory.getMessages().search_initMsg() + "</div>");//$NON-NLS-1$ //$NON-NLS-2$
        panel.setBodyBorder(false);
        panel.setBorders(false);
        panel.setHeaderVisible(false);
        panel.add(promptMsg);
        add(panel);
    }

    public void updateGrid(CheckBoxSelectionModel<ItemBean> sm, List<ColumnConfig> columnConfigList) {
        if (gridContainer != null && this.findItem(gridContainer.getElement()) != null)
            remove(gridContainer);
        if (panel != null && this.findItem(panel.getElement()) != null)
            remove(panel);

        ColumnModel cm = new ColumnModel(columnConfigList);
        gridContainer = new ContentPanel(new FitLayout());
        gridContainer.setBodyBorder(false);
        gridContainer.setHeaderVisible(false);
        int usePageSize = PAGE_SIZE;
        if (StateManager.get().get("grid") != null) //$NON-NLS-1$
            usePageSize = Integer.valueOf(((Map<?, ?>) StateManager.get().get("grid")).get("limit").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        pagingBar = new PagingToolBarEx(usePageSize);
        pagingBar.setHideMode(HideMode.VISIBILITY);
        pagingBar.getMessages().setDisplayMsg(MessagesFactory.getMessages().page_displaying_records());

        pagingBar.setVisible(false);
        pagingBar.bind(loader);
        gridContainer.setBottomComponent(pagingBar);
        grid = new Grid<ItemBean>(store, cm);
        grid.setSelectionModel(sm);
        grid.setStateful(true);
        grid.setStateId("grid"); //$NON-NLS-1$
        re = new SaveRowEditor();
        grid.getView().setForceFit(true);
        if (cm.getColumnCount() > 0) {
            grid.setAutoExpandColumn(cm.getColumn(0).getHeader());
        }

        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ItemBean>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ItemBean> se) {
                ItemBean item = se.getSelectedItem();
                if (item != null) {
                    if (gridUpdateLock) {
                        return;
                    }
                    gridUpdateLock = true;
                    fkToolBar.renderTreeDetail(item);
                    gridUpdateLock = false;
                }
            }
        });

        grid.addListener(Events.Attach, new Listener<GridEvent<ItemBean>>() {

            public void handleEvent(GridEvent<ItemBean> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                int pageSize = pagingBar.getPageSize();
                config.setLimit(pageSize);
                loader.load(config);
                pagingBar.setVisible(true);
            }
        });
        grid.setLoadMask(true);
        grid.addPlugin(re);
        grid.addPlugin(sm);
        grid.setAriaIgnore(true);
        grid.setAriaDescribedBy("abcdefg");//$NON-NLS-1$
        grid.setAriaLabelledBy(this.getHeader().getId() + "-label");//$NON-NLS-1$

        gridContainer.add(grid);
        gridContainer.setHeight(200);

        add(gridContainer);
        this.syncSize();
        this.doLayout();
    }

    public ListStore<ItemBean> getStore() {
        return store;
    }

    public Grid<ItemBean> getGrid() {
        return grid;
    }

    public void setEnabledGridSearchButton(boolean enabled) {
        gridContainer.setEnabled(enabled);
        ItemsToolBar.getInstance().searchBut.setEnabled(enabled);
    }

    public void refreshGrid() {
        if (pagingBar != null)
            pagingBar.refresh();
    }

    public void lastPage() {
        if (pagingBar != null)
            pagingBar.last();
    }

    public void resetGrid() {
        store.removeAll();
        if (pagingBar != null)
            pagingBar.clear();
    }

    public void refresh(String ids, final boolean refreshItemForm) {
        if (grid != null) {
            final ListStore<ItemBean> store = grid.getStore();
            final ItemBean itemBean = store.findModel(ids);
            if (itemBean != null) {
                EntityModel entityModel = fkToolBar.getListPanel().getViewBean().getBindingEntityModel();
                service.getItem(itemBean, entityModel, Locale.getLanguage(), new SessionAwareAsyncCallback<ItemBean>() {

                    public void onSuccess(ItemBean result) {
                        Record record = store.getRecord(itemBean);
                        itemBean.copy(result);
                        record.commit(false);
                    }
                });
            } else {
                pagingBar.first();
            }
        } else {
            // ButtonEvent be = new ButtonEvent(fkToolBar.searchBut);
            fkToolBar.searchBut.fireEvent(Events.Select);
        }
    }

    public void layoutGrid(int height) {
        this.layout(true);
        if (gridContainer != null) {
            Element parent = DOM.getParent(gridContainer.getElement());
            gridContainer.setSize(parent.getOffsetWidth(), height);
        }
    }
}

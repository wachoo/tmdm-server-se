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
package org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel.SimpleCriterionPanel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class FKRelRecordWindow extends Window {

    private static final int COLUMN_WIDTH = 100;

    private static final int WINDOW_WIDTH = 450;

    private static final int WINDOW_HEIGH = 300;

    private Grid<ForeignKeyBean> relatedRecordGrid;

    private String fkKey;

    private final SimpleCriterionPanel simpleCriterionPanel;

    private ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    private PagingLoader<PagingLoadResult<ModelData>> loader;

    private int pageSize = 20;

    public FKRelRecordWindow(String fkKey, SimpleCriterionPanel panel) {
        this.fkKey = fkKey;
        this.simpleCriterionPanel = panel;
    }

    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        RpcProxy<PagingLoadResult<ForeignKeyBean>> proxy = new RpcProxy<PagingLoadResult<ForeignKeyBean>>() {

            public void load(final Object loadConfig, final AsyncCallback<PagingLoadResult<ForeignKeyBean>> callback) {
                service.getForeignKeyList((PagingLoadConfig) loadConfig, Itemsbrowser2.getSession().getCurrentEntityModel()
                        .getMetaDataTypes().get(fkKey), Itemsbrowser2.getSession().getAppHeader().getDatacluster(),
                        new AsyncCallback<ItemBasePageLoadResult<ForeignKeyBean>>() {

                            public void onFailure(Throwable caught) {
                                callback.onFailure(caught);
                            }

                            public void onSuccess(ItemBasePageLoadResult<ForeignKeyBean> result) {
                                callback.onSuccess(new BasePagingLoadResult<ForeignKeyBean>(result.getData(), result.getOffset(),
                                        result.getTotalLength()));
                            }

                        });

            }
        };

        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        ListStore<ForeignKeyBean> store = new ListStore<ForeignKeyBean>(loader);

        FormPanel panel = new FormPanel();
        panel.setFrame(false);
        panel.setLayout(new FitLayout());
        panel.setHeaderVisible(false);
        panel.setSize(WINDOW_WIDTH, WINDOW_HEIGH);
        panel.setHeaderVisible(false);

        StoreFilterField<ForeignKeyBean> filter = new StoreFilterField<ForeignKeyBean>() {

            protected boolean doSelect(Store<ForeignKeyBean> store, ForeignKeyBean parent, ForeignKeyBean record,
                    String property, String filter) {

                for (String key : record.getProperties().keySet()) {
                    if (record.getProperties().get(key).toString().toLowerCase().startsWith(filter.toLowerCase()))
                        return true;
                }
                return false;
            }

        };
        filter.setWidth(WINDOW_WIDTH - 40);
        filter.bind(store);

        ToolBar toolBar = new ToolBar();
        Button filterBtn = new Button();
        filterBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.funnel()));
        filterBtn.setWidth(30);
        toolBar.add(filterBtn);
        toolBar.add(filter);
        panel.setTopComponent(toolBar);

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        // build columns by specify store
        final PagingToolBar pageToolBar = new PagingToolBar(pageSize);
        pageToolBar.bind(loader);

        // change label display
        List<String> foreignKeyInfo = Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey)
                .getForeignKeyInfo();
        String foreignKey = Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey).getForeignkey();
        if (foreignKeyInfo != null)
            for (String info : foreignKeyInfo) {
                columns.add(new ColumnConfig(CommonUtil.getElementFromXpath(info), CommonUtil.getElementFromXpath(info),
                        COLUMN_WIDTH));
            }
        else
            columns.add(new ColumnConfig(CommonUtil.getElementFromXpath(foreignKey), CommonUtil.getElementFromXpath(foreignKey),
                    COLUMN_WIDTH));

        ColumnModel cm = new ColumnModel(columns);
        relatedRecordGrid = new Grid<ForeignKeyBean>(store, cm);
        relatedRecordGrid.getView().setForceFit(true);
        relatedRecordGrid.setLoadMask(true);
        relatedRecordGrid.setBorders(false);
        relatedRecordGrid.addListener(Events.Attach, new Listener<GridEvent<ForeignKeyBean>>() {

            public void handleEvent(GridEvent<ForeignKeyBean> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                config.setLimit(pageSize);
                loader.load(config);
            }
        });

        relatedRecordGrid.addListener(Events.OnDoubleClick, new Listener<GridEvent<ForeignKeyBean>>() {

            public void handleEvent(final GridEvent<ForeignKeyBean> be) {
                simpleCriterionPanel.setCriteriaFK(be.getModel());
                close();
            }
        });

        panel.add(relatedRecordGrid);
        panel.setBottomComponent(pageToolBar);

        Button cancelBtn = new Button(MessagesFactory.getMessages().cancel_btn());
        cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        addButton(cancelBtn);
        add(panel, new FlowData(5));

        setAutoHeight(true);
        setAutoWidth(true);
    }
}

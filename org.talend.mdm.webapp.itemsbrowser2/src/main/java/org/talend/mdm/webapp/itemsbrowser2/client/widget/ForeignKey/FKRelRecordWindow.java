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
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.EventListener;
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

    private ReturnCriteriaFK returnCriteriaFK;

    private ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    private PagingLoader<PagingLoadResult<ModelData>> loader;

    private int pageSize = 20;

    TextField<String> filter = new TextField<String>();
    
    public FKRelRecordWindow() {}
    
    public FKRelRecordWindow(String fkKey, ReturnCriteriaFK returnCriteriaFK) {
        this.fkKey = fkKey;
        this.returnCriteriaFK = returnCriteriaFK;
    }

    public String getFkKey() {
        return fkKey;
    }

    public void setFkKey(String fkKey) {
        this.fkKey = fkKey;
    }

    public ReturnCriteriaFK getReturnCriteriaFK() {
        return returnCriteriaFK;
    }

    public void setReturnCriteriaFK(ReturnCriteriaFK returnCriteriaFK) {
        this.returnCriteriaFK = returnCriteriaFK;
    }

    private String getFilterValue(){
        String value = filter.getRawValue();
        if (value == null || value.trim().equals("")){
            value = ".*"; //$NON-NLS-1$
        }
        return value;
    }
    
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        RpcProxy<PagingLoadResult<ForeignKeyBean>> proxy = new RpcProxy<PagingLoadResult<ForeignKeyBean>>() {

            public void load(final Object loadConfig, final AsyncCallback<PagingLoadResult<ForeignKeyBean>> callback) {
                service.getForeignKeyList((PagingLoadConfig) loadConfig, Itemsbrowser2.getSession().getCurrentEntityModel()
                        .getMetaDataTypes().get(fkKey), Itemsbrowser2.getSession().getAppHeader().getDatacluster(), false, getFilterValue(),
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
        final ListStore<ForeignKeyBean> store = new ListStore<ForeignKeyBean>(loader);

        FormPanel panel = new FormPanel();
        panel.setFrame(false);
        panel.setLayout(new FitLayout());
        panel.setHeaderVisible(false);
        panel.setSize(WINDOW_WIDTH, WINDOW_HEIGH);
        panel.setHeaderVisible(false);

//        StoreFilterField<ForeignKeyBean> filter = new StoreFilterField<ForeignKeyBean>() {
//
//            protected boolean doSelect(Store<ForeignKeyBean> store, ForeignKeyBean parent, ForeignKeyBean record,
//                    String property, String filter) {
//
//                for (String key : record.getProperties().keySet()) {
//                    if (record.getProperties().get(key).toString().toLowerCase().startsWith(filter.toLowerCase()))
//                        return true;
//                }
//                return false;
//            }
//
//        };
//        filter.setWidth(WINDOW_WIDTH - 40);
//        filter.bind(store);
        
        filter.addListener(Events.KeyUp, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_UP){
                    ForeignKeyBean fkBean = relatedRecordGrid.getSelectionModel().getSelectedItem();
                    if (fkBean == null){
                        relatedRecordGrid.getSelectionModel().select(store.getCount() - 1, true);
                    } else {
                        relatedRecordGrid.getSelectionModel().selectPrevious(false);
                        filter.focus();
                    }
                    return;
                }
                if (be.getKeyCode() == KeyCodes.KEY_DOWN){
                    ForeignKeyBean fkBean = relatedRecordGrid.getSelectionModel().getSelectedItem();
                    if (fkBean == null){
                        relatedRecordGrid.getSelectionModel().select(0, true);
                    } else {
                        relatedRecordGrid.getSelectionModel().selectNext(false);
                        filter.focus();
                    }
                    return;
                }
                if (be.getKeyCode() == KeyCodes.KEY_ENTER){
                    returnCriteriaFK.setCriteriaFK(relatedRecordGrid.getSelectionModel().getSelectedItem());
                    close();
                }
                loader.load(0, pageSize);
            }
        });
        filter.setWidth(WINDOW_WIDTH - 40);

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
        boolean retrieveFKinfos = Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey)
                .isRetrieveFKinfos();
        String foreignKey = Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey).getForeignkey();
        if (retrieveFKinfos) {
            List<String> foreignKeyInfo = Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey)
                    .getForeignKeyInfo();
            for (String info : foreignKeyInfo) {
                columns.add(new ColumnConfig(CommonUtil.getElementFromXpath(info), CommonUtil.getElementFromXpath(info),
                        COLUMN_WIDTH));
            }
        } else
            columns.add(new ColumnConfig("i", CommonUtil.getElementFromXpath(foreignKey), COLUMN_WIDTH)); //$NON-NLS-1$

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
                returnCriteriaFK.setCriteriaFK(be.getModel());
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

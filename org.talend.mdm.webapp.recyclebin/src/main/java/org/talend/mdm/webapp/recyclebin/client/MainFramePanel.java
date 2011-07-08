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
package org.talend.mdm.webapp.recyclebin.client;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.recyclebin.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.recyclebin.client.resources.icon.Icons;
import org.talend.mdm.webapp.recyclebin.shared.ItemsTrashItem;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.state.StateManager;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class MainFramePanel extends ContentPanel {

    private static MainFramePanel instance;

    private RecycleBinServiceAsync service = (RecycleBinServiceAsync) Registry.get(RecycleBin.RECYCLEBIN_SERVICE);

    private TextField text = new TextField();

    private final static int PAGE_SIZE = 20;

    private PagingToolBarEx pagetoolBar = null;

    private Grid grid;

    private MainFramePanel() {
        setLayout(new FitLayout());
        setBodyBorder(false);
        setHeaderVisible(false);
        initGrid();
        initTopBar();
    }

    private void initGrid() {
        List<ColumnConfig> ccList = new ArrayList<ColumnConfig>();
        ColumnConfig colPK = new ColumnConfig();
        colPK.setId("itemPK");//$NON-NLS-1$
        colPK.setHeader(MessagesFactory.getMessages().dataClusterName());
        ccList.add(colPK);
        ColumnConfig colRevisionID = new ColumnConfig();
        colRevisionID.setId("revisionID");//$NON-NLS-1$
        colRevisionID.setHeader(MessagesFactory.getMessages().revisionID());
        ccList.add(colRevisionID);
        ColumnConfig colConceptName = new ColumnConfig();
        colConceptName.setId("conceptName");//$NON-NLS-1$
        colConceptName.setHeader(MessagesFactory.getMessages().conceptName());
        ccList.add(colConceptName);
        ColumnConfig colIds = new ColumnConfig();
        colIds.setId("ids");//$NON-NLS-1$
        colIds.setHeader(MessagesFactory.getMessages().Ids());
        ccList.add(colIds);
        ColumnConfig colPartPath = new ColumnConfig();
        colPartPath.setId("partPath");//$NON-NLS-1$
        colPartPath.setHeader(MessagesFactory.getMessages().partPath());
        ccList.add(colPartPath);
        ColumnConfig colUserName = new ColumnConfig();
        colUserName.setId("insertionUserName");//$NON-NLS-1$
        colUserName.setHeader(MessagesFactory.getMessages().UserName());
        ccList.add(colUserName);
        ColumnConfig colDate = new ColumnConfig();
        colDate.setId("insertionTime");//$NON-NLS-1$
        colDate.setHeader(MessagesFactory.getMessages().Date());
        ccList.add(colDate);
        ColumnConfig colProjection = new ColumnConfig("projection", "projection", 0);//$NON-NLS-1$ //$NON-NLS-2$
        colProjection.setHidden(true);
        ccList.add(colProjection);
        ColumnConfig colDelete = new ColumnConfig();
        colDelete.setId("delete");//$NON-NLS-1$
        colDelete.setHeader(MessagesFactory.getMessages().delete());
        colDelete.setRenderer(new GridCellRenderer<BaseModelData>() {

            public Object render(final BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<BaseModelData> store, final Grid<BaseModelData> grid) {
                Image image = new Image();
                image.setResource(Icons.INSTANCE.delete());
                image.addClickListener(new ClickListener() {

                    public void onClick(Widget arg0) {
                        service.isEntityPhysicalDeletable(model.get("conceptName").toString(), new AsyncCallback<Boolean>() {//$NON-NLS-1$

                            public void onFailure(Throwable caught) {
                                Dispatcher.forwardEvent(RecycleBinEvents.Error, caught);
                            }

                                    public void onSuccess(Boolean result) {
                                        if (!result)
                                            return;
                                        MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory
                                                .getMessages().delete_confirm(), new Listener<MessageBoxEvent>() {

                                            public void handleEvent(MessageBoxEvent be) {
                                                if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {

                                                    if (model.get("projection") != null && !model.get("projection").equals("")) {//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                                                        String[] picArray = model.get("projection").toString().split(//$NON-NLS-1$
                                                                "/imageserver/");//$NON-NLS-1$
                                                        for (int i = 1; i < picArray.length; i++) {
                                                            String array = picArray[i];
                                                            if (!array.isEmpty()) {
                                                                String uri = array.substring(0, array.indexOf("?"));//$NON-NLS-1$

                                                                RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                                                                        "/imageserver/secure/ImageDeleteServlet?uri=" + uri);//$NON-NLS-1$
                                                                builder.setCallback(new RequestCallback() {

                                                                    public void onResponseReceived(Request request,
                                                                            Response response) {
                                                                        // TODO result
                                                                    }

                                                                    public void onError(Request request, Throwable exception) {
                                                                        Dispatcher
                                                                                .forwardEvent(RecycleBinEvents.Error, exception);
                                                                    }
                                                                });

                                                                try {
                                                                    builder.send();
                                                                } catch (RequestException e) {
                                                                    MessageBox.alert(MessagesFactory.getMessages().error_title(),
                                                                            e.getMessage(), null);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    deleteItem(model);
                                                }
                                            }

                                        });
                            }

                        });

                    }

                });
                return image;
            }

        });
        ccList.add(colDelete);
        ColumnConfig colRestore = new ColumnConfig();
        colRestore.setId("restore");//$NON-NLS-1$
        colRestore.setHeader(MessagesFactory.getMessages().restore());
        colRestore.setRenderer(new GridCellRenderer<BaseModelData>() {

            public Object render(final BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<BaseModelData> store, final Grid<BaseModelData> grid) {
                Image image = new Image();
                image.setResource(Icons.INSTANCE.restore());
                image.addClickListener(new ClickListener() {

                    public void onClick(Widget arg0) {
                        MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                                .restore_confirm(), new Listener<MessageBoxEvent>() {

                            public void handleEvent(MessageBoxEvent be) {
                                if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                    service.recoverDroppedItem(model.get("itemPK").toString(), model.get("partPath").toString(),//$NON-NLS-1$//$NON-NLS-2$
                                            model.get("revisionId") == null ? null : model.get("revisionId").toString(), model //$NON-NLS-1$//$NON-NLS-2$
                                                    .get("conceptName").toString(), model.get("ids").toString(),//$NON-NLS-1$//$NON-NLS-2$
                                            new AsyncCallback<Void>() {

                                                public void onFailure(Throwable caught) {
                                                    Dispatcher.forwardEvent(RecycleBinEvents.Error, caught);
                                                }

                                                public void onSuccess(Void arg0) {
                                                    pagetoolBar.refresh();
                                                    grid.getStore().remove(model);
                                                }

                                            });
                                }
                            }

                        });

                    }

                });
                return image;
            }

        });
        ccList.add(colRestore);
        ColumnModel cm = new ColumnModel(ccList);

        RpcProxy<PagingLoadResult<ItemsTrashItem>> proxy = new RpcProxy<PagingLoadResult<ItemsTrashItem>>() {

            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ItemsTrashItem>> callback) {
                service.getTrashItems(text.getValue() == null ? "*" : text.getValue().toString(), (PagingLoadConfig) loadConfig,//$NON-NLS-1$
                        new AsyncCallback<PagingLoadResult<ItemsTrashItem>>() {

                            public void onSuccess(PagingLoadResult<ItemsTrashItem> result) {
                                callback.onSuccess(new BasePagingLoadResult<ItemsTrashItem>(result.getData(), result.getOffset(),
                                        result.getTotalLength()));
                            }

                            public void onFailure(Throwable caught) {
                                MessageBox.alert(MessagesFactory.getMessages().error_title(), caught.getMessage(), null);
                                callback
                                        .onSuccess(new BasePagingLoadResult<ItemsTrashItem>(new ArrayList<ItemsTrashItem>(), 0, 0));
                            }
                        });
            }

        };

        // loader
        final PagingLoader<PagingLoadResult<ItemsTrashItem>> loader = new BasePagingLoader<PagingLoadResult<ItemsTrashItem>>(
                proxy);

        final ListStore<ItemsTrashItem> store = new ListStore<ItemsTrashItem>(loader);
        grid = new Grid(store, cm);
        grid.getView().setAutoFill(true);
        grid.getView().setForceFit(true);
        grid.setSize(350, 600);
        int usePageSize = PAGE_SIZE;
        if (StateManager.get().get("trashgrid") != null) //$NON-NLS-1$
            usePageSize = Integer.valueOf(((Map) StateManager.get().get("trashgrid")).get("limit").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        pagetoolBar = new PagingToolBarEx(usePageSize);
        pagetoolBar.bind(loader);
        grid.setLoadMask(true);
        grid.setStateId("trashgrid");//$NON-NLS-1$
        grid.addListener(Events.Attach, new Listener<GridEvent<ItemsTrashItem>>() {

            public void handleEvent(GridEvent<ItemsTrashItem> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                int pageSize = (Integer) pagetoolBar.getPageSize();
                config.setLimit(pageSize);
                loader.load(config);
            }
        });
        this.setBottomComponent(pagetoolBar);
        add(grid);
    }

    private void initTopBar(){
        ToolBar bar = new ToolBar();
        text.setId("trash-criteria");//$NON-NLS-1$
        text.setEmptyText("*");//$NON-NLS-1$        
        bar.add(text);
        final Button btn = new Button(MessagesFactory.getMessages().search());
        btn.setId("search");//$NON-NLS-1$
        btn.setToolTip(MessagesFactory.getMessages().serarch_tooltip());
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                int pageSize = (Integer) pagetoolBar.getPageSize();
                config.setLimit(pageSize);
                grid.getStore().getLoader().load(config);
            }

        });
        text.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent e) {
                if (e.getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (btn != null) {
                        btn.fireEvent(Events.Select);
                    }
                }
            }
        });
        bar.add(btn);
        this.setTopComponent(bar);
    }

    private void deleteItem(final BaseModelData model) {
        service.removeDroppedItem(model.get("itemPK").toString(), model.get("partPath").toString(),//$NON-NLS-1$//$NON-NLS-2$
                model.get("revisionId") == null ? null : model.get("revisionId").toString(), model //$NON-NLS-1$//$NON-NLS-2$
                        .get("conceptName").toString(), model.get("ids").toString(),//$NON-NLS-1$//$NON-NLS-2$
                new AsyncCallback<Void>() {

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(RecycleBinEvents.Error, caught);
                    }

                    public void onSuccess(Void arg0) {
                        pagetoolBar.refresh();
                        grid.getStore().remove(model);
                    }

                });
    }

    public static MainFramePanel getInstance() {
        if (instance == null)
            instance = new MainFramePanel();
        return instance;
    }
}

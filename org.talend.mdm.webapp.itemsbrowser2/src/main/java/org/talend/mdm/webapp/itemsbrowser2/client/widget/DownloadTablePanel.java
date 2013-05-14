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
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.client.model.ItemBean;
import org.talend.mdm.webapp.base.client.widget.PagingToolBarEx;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.util.LabelUtil;
import org.talend.mdm.webapp.itemsbrowser2.client.util.Locale;
import org.talend.mdm.webapp.itemsbrowser2.client.util.ViewUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.state.StateManager;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class DownloadTablePanel extends ContentPanel {

    private ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    private ContentPanel gridContainer;

    private Grid<ItemBean> grid;

    private String tableName = null;

    private final static int PAGE_SIZE = 20;

    private PagingToolBarEx pagetoolBar = null;
    
    private List<String> headerList = new ArrayList<String>();

    private List<String> xPathList = new ArrayList<String>();
      
    private DownloadTablePanel(String name) {
        super();
        tableName = name;
        this.setLayout(new FitLayout());
        this.setHeaderVisible(false);
    }

    public static DownloadTablePanel getInstance(String name) {
        return new DownloadTablePanel(name);
    }

    private List<ColumnConfig> initColumns(ViewBean viewBean, int width) {
       
        List<String> viewableXpaths = viewBean.getViewableXpaths();
        EntityModel entityModel = viewBean.getBindingEntityModel();
        Map<String, TypeModel> dataTypes = entityModel.getMetaDataTypes();
        
        List<ColumnConfig> ccList = new ArrayList<ColumnConfig>();
        headerList.clear();
        xPathList.clear();
        
        int subWidth = (width) / viewBean.getViewables().length;
        for (String xpath : viewableXpaths) {
            TypeModel typeModel = dataTypes.get(xpath);
            String header = typeModel == null ? xpath : ViewUtil.getViewableLabel(
                    Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader()), typeModel);
            headerList.add(header);
            xPathList.add(xpath);
            ColumnConfig cc = new ColumnConfig(xpath, header, subWidth);      
            ccList.add(cc);
        }

        return ccList;
    }

    public void updateGrid(final ViewBean viewBean, int width) {
        List<ColumnConfig> columnConfigList = initColumns(viewBean, width);
        RpcProxy<PagingLoadResult<ItemBean>> proxy = new RpcProxy<PagingLoadResult<ItemBean>>() {

            @Override
            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ItemBean>> callback) {
                final QueryModel qm = new QueryModel();
                qm.setDataClusterPK(Itemsbrowser2.getSession().getAppHeader().getDatacluster());
                qm.setView(viewBean);
                qm.setModel(viewBean.getBindingEntityModel());
     
                qm.setPagingLoadConfig((PagingLoadConfig) loadConfig);
                int pageSize = (Integer) pagetoolBar.getPageSize();
                qm.getPagingLoadConfig().setLimit(pageSize);
                qm.setLanguage(Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader()));
                                
                service.queryItemBeans(qm, new SessionAwareAsyncCallback<ItemBasePageLoadResult<ItemBean>>() {
                    
                    public void onSuccess(ItemBasePageLoadResult<ItemBean> result) {
                        callback.onSuccess(new BasePagingLoadResult<ItemBean>(result.getData(), result.getOffset(), result
                                .getTotalLength()));
                    }
                    
                    @Override
                    protected void doOnFailure(Throwable caught) {
                        MessageBox.alert(MessagesFactory.getMessages().error_title(), caught.getMessage(), null);
                        callback.onSuccess(new BasePagingLoadResult<ItemBean>(new ArrayList<ItemBean>(), 0, 0));
                        
                    }
                });
            }
        };

        // loader
        final PagingLoader<PagingLoadResult<ItemBean>> loader = new BasePagingLoader<PagingLoadResult<ItemBean>>(
                proxy);

        final ListStore<ItemBean> store = new ListStore<ItemBean>(loader);
        int usePageSize = PAGE_SIZE;
        if (StateManager.get().get("crossgrid") != null) //$NON-NLS-1$
            usePageSize = Integer.valueOf(((Map<?,?>) StateManager.get().get("crossgrid")).get("limit").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        pagetoolBar = new PagingToolBarEx(usePageSize);
        pagetoolBar.bind(loader);
        ColumnModel cm = new ColumnModel(columnConfigList);

        grid = new Grid<ItemBean>(store, cm);
        grid.setId("UpdateTableGrid"); //$NON-NLS-1$
        grid.addListener(Events.Attach, new Listener<GridEvent<ItemBean>>() {

            public void handleEvent(GridEvent<ItemBean> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                int pageSize = (Integer) pagetoolBar.getPageSize();
                config.setLimit(pageSize);
                loader.load(config);
            }
        });
        grid.setLoadMask(true);
        grid.setStateId("crossgrid");//$NON-NLS-1$
        gridContainer = new ContentPanel(new FitLayout());
        gridContainer.setBodyBorder(false);
        gridContainer.setHeaderVisible(false);
        gridContainer.setBottomComponent(pagetoolBar);
        gridContainer.add(grid);
        add(gridContainer);

        ToolBar toolBar = new ToolBar();
        Button export = new Button("Export"); //$NON-NLS-1$
        toolBar.add(export);
        export.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                StringBuilder url = new StringBuilder("/itemsbrowser2/download?tableName="); //$NON-NLS-1$
                url.append(tableName)
                   .append("&header="); //$NON-NLS-1$
                int i = 0;
                int count = headerList.size();
                for(String header : headerList){
                    i++;
                    url.append(header);
                    if(i < count)
                        url.append("@"); //$NON-NLS-1$
                }
                
                i=0;
                count = xPathList.size();
                url.append("&xpath="); //$NON-NLS-1$
                for(String path : xPathList){
                    i++;
                    url.append(path);
                    if(i < count)
                        url.append("@"); //$NON-NLS-1$
                }
                
                Window.open(url.toString(), "_parent", "location=no"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        });
        gridContainer.setTopComponent(toolBar);
        this.syncSize();
    }
    
    public String getViewableLabel(String language, TypeModel typeModel) {
        
        String label = typeModel.getLabel(language);
        if(LabelUtil.isDynamicLabel(label)) {
            label = typeModel.getName();
        }
        return label;
    }
}

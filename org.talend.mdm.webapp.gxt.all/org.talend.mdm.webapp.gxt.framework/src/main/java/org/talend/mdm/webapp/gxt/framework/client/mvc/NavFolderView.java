// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.gxt.framework.client.mvc;

import java.util.List;

import org.talend.mdm.webapp.gxt.framework.client.AppEvents;
import org.talend.mdm.webapp.gxt.framework.client.action.HookMenuAppAction;
import org.talend.mdm.webapp.gxt.framework.client.resources.Resources;
import org.talend.mdm.webapp.gxt.framework.shared.NavFolder;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class NavFolderView extends View {

    private TreeStore<NavFolder> store;

    private TreePanel<NavFolder> tree;

    private TreeLoader<NavFolder> loader;

    /**
     * DOC HSHU NavFolderView constructor comment.
     * @param controller
     */
    public NavFolderView(Controller controller) {
        super(controller);
    }
    
    protected void initialize() {

    }
    
    protected void initUI() {
        
        ContentPanel side = (ContentPanel) Registry.get(AppView.SIDE_PANEL);

        ContentPanel sidePanel = new ContentPanel();
        sidePanel.setLayout(new FitLayout());
        sidePanel.setHeaderVisible(false);
        sidePanel.setBodyBorder(false);
        sidePanel.setAnimCollapse(false);
        sidePanel.setScrollMode(Scroll.AUTO);

        // init filter
        StoreFilterField<NavFolder> filter = new StoreFilterField<NavFolder>() {

            @Override
            protected boolean doSelect(Store<NavFolder> store, NavFolder parent, NavFolder child, String property, String filter) {
                if (child.get("type") != null && child.get("type").equals(NavFolder._CATALOG_)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    return false;
                }
                String name = child.get("name"); //$NON-NLS-1$
                name = name.toLowerCase();
                if (name.indexOf(filter.toLowerCase()) != -1) {
                    return true;
                }
                return false;

            }

        };
        
        ToolBar toolBar = (ToolBar) side.getTopComponent();
        IconButton filterBtn = new IconButton("icon-filter"); //$NON-NLS-1$
        // filterBtn.setWidth(20);
        IconButton refreshBtn = new IconButton("icon-reload"); //$NON-NLS-1$
        // refreshBtn.setWidth(20);
        refreshBtn.setToolTip("reload");
        refreshBtn.addSelectionListener(new SelectionListener<IconButtonEvent>() {

            @Override
            public void componentSelected(IconButtonEvent ce) {
               Dispatcher.forwardEvent(AppEvents.LoadMenus);
            }
        });

        toolBar.add(filterBtn);
        toolBar.add(filter);
        toolBar.add(refreshBtn);

        // init tree
        loader = new BaseTreeLoader<NavFolder>(new TreeModelReader<List<NavFolder>>());
        store = new TreeStore<NavFolder>(loader);

        tree = new TreePanel<NavFolder>(store);
        // tree.getStyle().setLeafIcon(Resources.ICONS.arrow());
        tree.getStyle().setNodeOpenIcon(null);
        tree.getStyle().setNodeCloseIcon(null);
        tree.setIconProvider(new ModelIconProvider<NavFolder>() {

            public AbstractImagePrototype getIcon(NavFolder model) {
                
                String toCheckMenuID = model.getContext()+"."+model.getApplication();
                if(model.getIcon()!=null){
                    //TODO support customize icon
                }else if(toCheckMenuID.equals("itemsbrowser.ItemsBrowser")||toCheckMenuID.equals("viewbrowser.ViewBrowser")){
                    return Resources.ICONS.browse();
                }else if(toCheckMenuID.equals("hierarchical.GroupingHierarchy")){
                    return Resources.ICONS.grouping_hier();
                }else if(toCheckMenuID.equals("reporting.Reporting")){
                    return Resources.ICONS.reporting();
                }else if(toCheckMenuID.equals("ItemsTrash.ItemsTrash")){
                    return Resources.ICONS.trash();
                }else if(toCheckMenuID.equals("updatereport.UpdateReport")){
                    return Resources.ICONS.updatereport();
                }else{
                    //default menus icon
                    return Resources.ICONS.default_icon();
                }
                
                return Resources.ICONS.default_icon();
            }

        });

        tree.setLabelProvider(new ModelStringProvider<NavFolder>() {

            public String getStringValue(NavFolder model, String property) {

                String value = ""; //$NON-NLS-1$

                if (model.get("type") != null && model.get("type").equals(NavFolder._CATALOG_)) { //$NON-NLS-1$ //$NON-NLS-2$
                    value = "<SPAN STYLE=\"font-weight:bold;color:000000;\">" + model.get("name") + "</SPAN>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                } else {
                    value = "<SPAN CLASS=\"menus-list\">" + model.get("name") + "</SPAN>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                return value;
            }
        });
        
        tree.setDisplayProperty("name"); //$NON-NLS-1$
        // tree.setAutoSelect(true);
        tree.addListener(Events.OnClick, new Listener<BaseEvent>() {

            public void handleEvent(BaseEvent be) {
                HookMenuAppAction hookMenuAppAction=new HookMenuAppAction();
                hookMenuAppAction.execute(be);
            }

        });

        filter.bind(store);

        sidePanel.add(tree);

        side.add(sidePanel);

    }

    protected void handleEvent(AppEvent event) {
        
        if (event.getType() == AppEvents.InitSideBar) {
            initUI();
            Dispatcher.forwardEvent(AppEvents.LoadMenus);
        }else if (event.getType() == AppEvents.LoadMenus) {
            NavFolder f = event.getData();
            if (f != null) {
                loader.addListener(Loader.Load, new LoadListener() {

                    @Override
                    public void loaderLoad(LoadEvent le) {
                        loader.removeLoadListener(this);
                    }
                });
                loader.load(f);
                tree.expandAll();
            }
        }
        
    }

}

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
package org.talend.mdm.webapp.itemsbrowser2.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.util.UserSession;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.CellEditorCreator;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.CellRendererCreator;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.FieldCreator;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ItemsFormPanel;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ItemsSearchContainer;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemsView extends View {

    private TabPanel tabFrame = null;

    private ItemsSearchContainer itemsSearchContainer = null;

    public static final String ROOT_DIV = "talend_itemsbrowser2_ItemsBrowser2";//$NON-NLS-1$

    public static final String TAB_FRAME = "tab_frame";//$NON-NLS-1$

    public static final String ITEMS_SEARCH_CONTAINER = "itemsSearchContainer";//$NON-NLS-1$

    public static final String ITEMS_FORM_TARGET = "items_form_target";

    public static final String TARGET_IN_NEW_WINDOW = "target_in_new_window";

    public static final String TARGET_IN_NEW_TAB = "target_in_new_tab";

    public static final String TARGET_IN_SEARCH_TAB = "target_in_search_tab";

    public ItemsView(Controller controller) {
        super(controller);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see com.extjs.gxt.ui.client.mvc.Viewinitialize()
     */
    @Override
    protected void initialize() {
        super.initialize();
    }

    protected void handleEvent(AppEvent event) {

        if (event.getType() == ItemsEvents.InitFrame) {
            onInitFrame(event);
        } else if (event.getType() == ItemsEvents.InitSearchContainer) {
            onInitSearchContainer(event);
        } else if (event.getType() == ItemsEvents.GetView) {
            onGetView(event);
        } else if (event.getType() == ItemsEvents.ViewItems) {
            onViewItems(event);
        } else if (event.getType() == ItemsEvents.ViewItemForm) {
            onViewItemForm(event);
        } else if (event.getType() == ItemsEvents.InitView) {
            onInitView(event);
        }
    }

    protected void onInitView(final AppEvent event) {
        ViewBean viewBean = event.getData();

        itemsSearchContainer = Registry.get(ITEMS_SEARCH_CONTAINER);
        itemsSearchContainer.getItemsListPanel().getToolBar().updateToolBar(viewBean);
    }

    protected void onGetView(final AppEvent event) {
        ViewBean viewBean = event.getData();

        // TODO update columns
        itemsSearchContainer = Registry.get(ITEMS_SEARCH_CONTAINER);
        List<ColumnConfig> ccList = new ArrayList<ColumnConfig>();
        List<String> viewableXpaths = viewBean.getViewableXpaths();
        Map<String, TypeModel> dataTypes = viewBean.getMetaDataTypes();
        for (String xpath : viewableXpaths) {
            // TODO convert xpath 2 label
            xpath = getViewLabelFromViewable(xpath);
            TypeModel typeModel = dataTypes.get(xpath);
            Field<Serializable> field = FieldCreator.createField(typeModel);
            field.setName(xpath);
            CellEditor cellEditor = CellEditorCreator.createCellEditor(field);
            ColumnConfig cc = new ColumnConfig(xpath, typeModel.getLabel(), 200);
            cc.setEditor(cellEditor);
            GridCellRenderer<ModelData> renderer = CellRendererCreator.createRenderer(typeModel);
            if (renderer != null) {
                cc.setRenderer(renderer);
            }

            ccList.add(cc);
        }

        itemsSearchContainer.getItemsListPanel().updateGrid(ccList);
        itemsSearchContainer.getItemsFormPanel().paint(viewBean, false);
        // Dispatcher.forwardEvent(ItemsEvents.ViewItems, null);
    }

    private String getViewLabelFromViewable(String xpath) {
        // TODO getViewLabelFromViewable
        return xpath;
    }

    protected void onViewItems(AppEvent event) {
        if (event.getType() == ItemsEvents.ViewItems) {

            List<ItemBean> itemBeans = dynamicAssembleItems(event);

            ItemsListPanel itemsListPanel = itemsSearchContainer.getItemsListPanel();

            ListStore<ItemBean> store = itemsListPanel.getStore();
            store.removeAll();

            store.add(itemBeans);

            if (itemBeans.size() > 0) {
                itemsListPanel.getGrid().getSelectionModel().select(((ItemBean) itemBeans.get(0)), false);
            } else {
                // itemsListPanel.showItem(null);
            }
            return;
        }
    }

    private List<ItemBean> dynamicAssembleItems(AppEvent event) {
        List<ItemBean> itemBeans = event.getData();
        for (ItemBean itemBean : itemBeans) {
            ViewBean viewBean = (ViewBean) Itemsbrowser2.getSession().get(UserSession.CURRENT_VIEW);
            itemBean.dynamicAssemble(viewBean);
        }
        return itemBeans;
    }

    protected void onViewItemForm(AppEvent event) {
        boolean ifNew = false;
        ItemBean itemBean = event.getData();
        String itemsFormTarget = event.getData(ItemsView.ITEMS_FORM_TARGET);
        if (itemBean != null) {
            String tabTitle = itemBean.getConcept() + itemBean.getIds();
            if (itemBean.getIds().equals(""))
                ifNew = true;
            if (itemsFormTarget.equals(ItemsView.TARGET_IN_SEARCH_TAB)) {
                itemsSearchContainer = Registry.get(ITEMS_SEARCH_CONTAINER);
                itemsSearchContainer.getItemsFormPanel().bind(itemBean);
            } else if (itemsFormTarget.equals(ItemsView.TARGET_IN_NEW_TAB)) {
                ItemsFormPanel itemsFormPanel = new ItemsFormPanel();
                addTab(itemsFormPanel, tabTitle, tabTitle, true);
                ViewBean viewBean = (ViewBean) Itemsbrowser2.getSession().get(UserSession.CURRENT_VIEW);
                itemsFormPanel.paint(viewBean, ifNew);
                itemsFormPanel.bind(itemBean);
            } else if (itemsFormTarget.equals(ItemsView.TARGET_IN_NEW_WINDOW)) {
                ItemsFormPanel itemsFormPanel = new ItemsFormPanel();
                addWin(itemsFormPanel, tabTitle);
                ViewBean viewBean = (ViewBean) Itemsbrowser2.getSession().get(UserSession.CURRENT_VIEW);
                itemsFormPanel.paint(viewBean, ifNew);
                itemsFormPanel.bind(itemBean);
            }
        }
    }

    protected void onInitFrame(AppEvent ae) {

        // create search panel
        // build frame
        Log.info("Init tab-frame... ");

        Viewport container = new Viewport() {

            public void onAttach() {
                super.onAttach();
                Widget w = this.getParent();
                setSize(w.getOffsetWidth(), w.getOffsetHeight());
            }

            protected void onWindowResize(int width, int height) {
                Widget w = this.getParent();
                setSize(w.getOffsetWidth(), w.getOffsetHeight());
            }
        };
        container.setLayout(new FitLayout());
        container.setAutoWidth(true);

        // build tab
        tabFrame = new TabPanel();
        tabFrame.setMinTabWidth(115);
        tabFrame.setResizeTabs(true);
        tabFrame.setAnimScroll(true);
        tabFrame.setTabScroll(true);
        tabFrame.setCloseContextMenu(true);
        tabFrame.setTabPosition(TabPosition.BOTTOM);
        tabFrame.setBodyBorder(false);

        container.add(tabFrame);

        // registry serves as a global context
        Registry.register(TAB_FRAME, tabFrame);

        // FIXME can not auto-fill
        container.setStyleAttribute("height", "100%");
        RootPanel.get(ROOT_DIV).add(container);
        tabFrame.setHeight(container.getOffsetHeight());

        Dispatcher.forwardEvent(ItemsEvents.InitSearchContainer);
    }

    protected void onInitSearchContainer(AppEvent ae) {

        // create search panel
        Log.info("Init items-search-container... ");
        itemsSearchContainer = new ItemsSearchContainer();

        addTab(itemsSearchContainer, "Search Tab", "Search Tab", false);

        Registry.register(ITEMS_SEARCH_CONTAINER, itemsSearchContainer);

        // FIXME
        // String defaultViewName = ClientFakeData.DEFAULT_VIEW;
        // Dispatcher.forwardEvent(ItemsEvents.GetView, defaultViewName);

    }

    private void addTab(Component c, String tabId, String tabName, boolean closable) {
        TabItem item = tabFrame.getItemByItemId(tabId);

        if (item == null) {
            item = new TabItem();
            item.setItemId(tabId);
            item.setLayout(new FitLayout());
            item.setText(tabName);
            item.setClosable(closable);
            item.add(c);
            item.addStyleName("pad-text");
            tabFrame.add(item);
        }

        tabFrame.setSelection(item);

    }

    /**
     * DOC HSHU Comment method "addWin".
     */
    private void addWin(Component c, String title) {

        // FIXME Do we need one window for one item?

        Window window = new Window();
        window.setSize(500, 500);
        window.setPlain(true);
        window.setModal(false);
        window.setHeading(title);
        window.setLayout(new FitLayout());

        window.add(c);

        window.setClosable(true);
        window.setResizable(true);
        window.setMaximizable(true);

        window.show();

        // random start point
        window.center();
        int left = window.getAbsoluteLeft();
        int top = window.getAbsoluteTop();
        int offset = Random.nextInt(35);
        window.setPosition(left + offset, top + offset);

    }

}

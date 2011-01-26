/*
 * Ext GWT - Ext for GWT Copyright(c) 2007-2009, Ext JS, LLC. licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style;

public class ItemsListPanel extends ContentPanel {

    private Grid<ItemBean> grid;

    private ListStore<ItemBean> store;

    public ItemsListPanel() {
        setLayout(new FitLayout());

        setHeaderVisible(false);

        addToolBar();

    }

    private void addToolBar() {
        ToolBar toolBar = new ToolBar();
        Button create = new Button("Create");
        create.setIcon(IconHelper.createStyle("icon-email-add"));
        toolBar.add(create);

        Button reply = new Button("Reply");
        reply.setIcon(IconHelper.createStyle("icon-email-reply"));
        toolBar.add(reply);

        setTopComponent(toolBar);
    }

    public void updateGrid(List<ColumnConfig> columnConfigList) {
        
        if(grid!=null)this.remove(grid);

        ColumnModel cm = new ColumnModel(columnConfigList);

        store = new ListStore<ItemBean>();

        grid = new Grid<ItemBean>(store, cm);
        grid.getView().setForceFit(true);
        grid.addListener(Events.OnMouseOver, new Listener<GridEvent<ItemBean>>() {

            public void handleEvent(GridEvent<ItemBean> be) {
                ItemBean item = grid.getStore().getAt(be.getRowIndex());
                grid.getView().getRow(item).getStyle().setCursor(Style.Cursor.POINTER);
            }
        });
        grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ItemBean>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ItemBean> se) {
                ItemBean m = se.getSelectedItem();
                showItem(m);
            }
        });

        add(grid);
        
        this.doLayout();
        
    }

    public ListStore<ItemBean> getStore() {
        return store;
    }

    public Grid<ItemBean> getGrid() {
        return grid;
    }

    private void showItem(ItemBean item) {
        AppEvent evt = new AppEvent(ItemsEvents.ViewItemsForm, item);
        Dispatcher.forwardEvent(evt);
    }

}

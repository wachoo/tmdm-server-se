package org.talend.mdm.webapp.base.client.widget;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class ColumnAlignGrid<M extends ModelData> extends Grid<M> {

    public ColumnAlignGrid(ListStore<M> store, ColumnModel cm) {
        super(store, cm);
        clearGridViewBorderWidth();
    }
    
    private native void clearGridViewBorderWidth()/*-{
        var view = this.@com.extjs.gxt.ui.client.widget.grid.Grid::view;
        view.@com.extjs.gxt.ui.client.widget.grid.GridView::borderWidth = 0; 
    }-*/;
}

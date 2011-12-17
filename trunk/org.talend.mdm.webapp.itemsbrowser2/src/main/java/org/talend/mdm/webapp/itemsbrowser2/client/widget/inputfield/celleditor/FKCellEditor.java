package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor;

import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ItemsSearchContainer;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey.FKField;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;


public class FKCellEditor extends CellEditor {

    public FKCellEditor(FKField field) {
        super(field);
    }
    public Object preProcessValue(Object value) {
        if (value == null) return null;

        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        Grid<ItemBean> grid = itemsSearchContainer.getItemsListPanel().getGrid();

        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        ForeignKeyBean fkBean = itemBean.getForeignkeyDesc((String)value);
        return fkBean;
    }

    public Object postProcessValue(Object value) {
        if (value == null) {
            return value;
        }
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        Grid<ItemBean> grid = itemsSearchContainer.getItemsListPanel().getGrid();
        
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        ForeignKeyBean fkBean = (ForeignKeyBean) value;
        if (itemBean.getForeignkeyDesc(fkBean.getFullString()) == null)
            itemBean.setForeignkeyDesc(fkBean.getFullString(), fkBean);
        return fkBean.getFullString();
    }
}

package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor;

import java.util.Date;

import org.talend.mdm.webapp.base.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.util.DateUtil;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ItemsSearchContainer;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class DateFieldCellEditor extends CellEditor {

    private DateField field;
    
    public DateFieldCellEditor(DateField field) {
        super(field);
        this.field = field;
    }

    public Object preProcessValue(Object value) {
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        Grid<ItemBean> grid = itemsSearchContainer.getItemsListPanel().getGrid();
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        if(itemBean.getOriginalMap().containsKey(field.getName()))
            return itemBean.getOriginalMap().get(field.getName());
        return DateUtil.convertStringToDate((String) value);
    }

    public Object postProcessValue(Object value) {
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        Grid<ItemBean> grid = itemsSearchContainer.getItemsListPanel().getGrid();
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        if(itemBean.getOriginalMap().containsKey(field.getName()))
            itemBean.getOriginalMap().put(field.getName(), (Date)value);
        return field.getPropertyEditor().getStringValue((Date)value);
    }
}

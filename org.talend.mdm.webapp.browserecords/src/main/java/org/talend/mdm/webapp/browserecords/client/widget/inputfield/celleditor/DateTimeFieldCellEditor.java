package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import java.util.Date;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.DateUtil;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;


public class DateTimeFieldCellEditor extends CellEditor {

    public DateTimeFieldCellEditor(DateField field) {
        super(field);
    }

    public Object preProcessValue(Object value) {
        Grid<ItemBean> grid = ItemsListPanel.getInstance().getGrid();
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        if (itemBean.getOriginalMap().containsKey(this.getField().getName()))
            return itemBean.getOriginalMap().get(this.getField().getName());
        return DateUtil.tryConvertStringToDate((String) value);
    }

    public Object postProcessValue(Object value) {
        Grid<ItemBean> grid = ItemsListPanel.getInstance().getGrid();
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        if (itemBean.getOriginalMap().containsKey(this.getField().getName()))
            itemBean.getOriginalMap().put(this.getField().getName(), (Date) value);
        return DateUtil.getDateTime((Date) value);
    }
}

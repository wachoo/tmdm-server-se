package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;

import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;


public class NumberFieldCellEditor extends CellEditor {

    public NumberFieldCellEditor(NumberField field) {
        super(field);
    }
    public Object preProcessValue(Object value) {
        String numberType = getField().getData("numberType");//$NON-NLS-1$

        Grid<ItemBean> grid = ItemsListPanel.getInstance().getGrid();
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        Number num = null;
        if (itemBean.getOriginalMap().containsKey(getField().getName())) {
            num = (Number) itemBean.getOriginalMap().get(getField().getName());
        } else {
            if ("integer".equals(numberType)) { //$NON-NLS-1$
                num = Integer.parseInt((String) value);
            } else {
                num = Double.parseDouble((String) value);
            }
        }
        return num;
    }

    public Object postProcessValue(Object value) {
        if (value == null)
            return ""; //$NON-NLS-1$

        Grid<ItemBean> grid = ItemsListPanel.getInstance().getGrid();
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        if (itemBean.getOriginalMap().containsKey(getField().getName()))
            itemBean.getOriginalMap().put(getField().getName(), (Number) value);

        String numberType = getField().getData("numberType");//$NON-NLS-1$
        if ("integer".equals(numberType)){//$NON-NLS-1$
            Integer v = (Integer) value;
            return Integer.toString(v.intValue());
        } else {
            Double v = (Double) value;
            return Double.toString(v);
        }
    }
}

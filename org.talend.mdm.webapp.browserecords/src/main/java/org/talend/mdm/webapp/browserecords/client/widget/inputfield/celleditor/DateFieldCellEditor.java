package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import java.util.Date;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.DateUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatDateField;

import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class DateFieldCellEditor extends CellEditor {
    private FormatDateField field;
    
    public DateFieldCellEditor(FormatDateField field) {
        super(field);
        this.field = field;
    }

    public Object preProcessValue(Object value) {
        Grid<ItemBean> grid = ItemsListPanel.getInstance().getGrid();
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        if(itemBean.getOriginalMap().containsKey(field.getName()))
            return itemBean.getOriginalMap().get(field.getName());
        return DateUtil.convertStringToDate((String) value);
    }

    public Object postProcessValue(Object value) {
        Grid<ItemBean> grid = ItemsListPanel.getInstance().getGrid();
        ItemBean itemBean = grid.getSelectionModel().getSelectedItem();
        if(itemBean.getOriginalMap().containsKey(field.getName()))
            itemBean.getOriginalMap().put(field.getName(), (Date)value);
//        return field.getDiplayValue();
        
        if (field.getFormatPattern() == null || field.getFormatPattern().trim().length() == 0) {
            return DateUtil.getDate((Date) value);
        }

        return getDisplayValue(Locale.getLanguage(), field.getFormatPattern(), Long.toString(getDate().getTime()));
    }

    private native String getDisplayValue(String lang, String format, String data)/*-{
        var v = "";
        $wnd.DWREngine.setAsync(false);
        $wnd.LayoutInterface.formatValue(lang, format, data, function(value){
            v = value;
        });
        $wnd.DWREngine.setAsync(true);
        return v;
    }-*/;

    private Date getDate() {
        return field.getValue();
    }
}

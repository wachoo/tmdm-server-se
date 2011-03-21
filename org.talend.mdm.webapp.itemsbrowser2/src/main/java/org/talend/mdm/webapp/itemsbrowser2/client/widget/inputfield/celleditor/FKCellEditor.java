package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.celleditor;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey.FKField;

import com.extjs.gxt.ui.client.widget.grid.CellEditor;


public class FKCellEditor extends CellEditor {

    public FKCellEditor(FKField field) {
        super(field);
    }
    public Object preProcessValue(Object value) {
        if (value == null) return null;
        ForeignKeyBean fkBean = new ForeignKeyBean();
        fkBean.setId((String) value);
        return fkBean;
    }

    public Object postProcessValue(Object value) {
        if (value == null) {
            return value;
        }
        ForeignKeyBean fkBean = (ForeignKeyBean) value;
        return fkBean.getId();
    }
}

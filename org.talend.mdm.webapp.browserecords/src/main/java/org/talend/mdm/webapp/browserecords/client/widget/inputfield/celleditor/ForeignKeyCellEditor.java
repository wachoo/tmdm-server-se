package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.TypeModel;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;

public class ForeignKeyCellEditor extends CellEditor {

    private TypeModel typeModel;

    ForeignKeyBean fkBean;

    public ForeignKeyCellEditor(Field<? extends Object> field, TypeModel typeModel) {
        super(field);
        this.typeModel = typeModel;
    }

    public Object preProcessValue(Object value) {
        fkBean = (ForeignKeyBean) value;
        if (value == null)
            return null;
        if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.STRING.getTypeName())
                || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.LONG.getTypeName())) {
            return fkBean.getForeignKeyInfo().get(typeModel.getXpath());
        }
        return null;
    }

    public Object postProcessValue(Object value) {
        if (fkBean != null)
            fkBean.getForeignKeyInfo().put(typeModel.getXpath(), value.toString());
        return fkBean;

    }

}

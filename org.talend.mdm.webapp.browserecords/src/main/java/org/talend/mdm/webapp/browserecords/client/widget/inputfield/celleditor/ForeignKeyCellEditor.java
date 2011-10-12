package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.TypeModel;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;

public class ForeignKeyCellEditor extends CellEditor {

    private TypeModel typeModel;

    private String value;

    ForeignKeyBean fkBean;

    public ForeignKeyCellEditor(Field<? extends Object> field, String value, TypeModel typeModel) {
        super(field);
        this.typeModel = typeModel;
        this.value = value;

    }

    public Object preProcessValue(Object value) {
        fkBean = (ForeignKeyBean) value;
        if (value == null)
            return null;
        if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.STRING.getTypeName())) {
            return this.value;
        }
        return null;
    }

    public Object postProcessValue(Object value) {

        fkBean.set(typeModel.getXpath(), value.toString());
        return fkBean;

    }

}

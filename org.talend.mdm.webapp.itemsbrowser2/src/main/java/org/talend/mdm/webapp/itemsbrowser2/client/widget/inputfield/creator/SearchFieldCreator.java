package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.creator;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.shared.ComplexTypeModel;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.OperatorConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey.FKField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.DateTimeField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.SpinnerField;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;

public class SearchFieldCreator {

    public static Map<String, String> cons;

    public static Field createField(TypeModel typeModel) {
        Field field = null;
        if (typeModel instanceof SimpleTypeModel) {
            if (typeModel.getForeignkey() != null) {
                FKField fkField = new FKField();
                field = fkField;
                cons = OperatorConstants.fullOperators;
            } else if (typeModel.hasEnumeration()) {
                SimpleComboBox<String> comboBox = new SimpleComboBox<String>();
                comboBox.setFireChangeEventOnSetValue(true);
                if (typeModel.getMinOccurs() > 0)
                    comboBox.setAllowBlank(false);
                comboBox.setEditable(false);
                comboBox.setForceSelection(true);
                comboBox.setTriggerAction(TriggerAction.ALL);
                setEnumerationValues(typeModel, comboBox);
                field = comboBox;
                cons = OperatorConstants.enumOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.INT.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.INTEGER.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.LONG.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.SHORT.getBaseTypeName())) {
                SpinnerField spinnerField = new SpinnerField();
                spinnerField.setWidth(80);
                spinnerField.setStepValue(Integer.valueOf(1));
                spinnerField.setValue(Integer.valueOf(1));
                spinnerField.setPropertyEditorType(Integer.class);
                spinnerField.setFormat(NumberFormat.getFormat("0"));//$NON-NLS-1$
                field = spinnerField;
                cons = OperatorConstants.numOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.DOUBLE.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.DECIMAL.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.FLOAT.getBaseTypeName())) {
                SpinnerField spinnerField = new SpinnerField();
                spinnerField.setWidth(80);
                spinnerField.setStepValue(.1d);
                spinnerField.setValue(Double.valueOf(0));
                spinnerField.setPropertyEditorType(Double.class);
                spinnerField.setFormat(NumberFormat.getFormat("00.0"));//$NON-NLS-1$
                field = spinnerField;
                cons = OperatorConstants.numOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.DATE.getBaseTypeName())) {
                DateField dateField = new DateField();
                dateField.setMessageTarget("tooltip");//$NON-NLS-1$
                dateField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));//$NON-NLS-1$            
                field = dateField;
                cons = OperatorConstants.dateOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.DATETIME.getBaseTypeName())) {
                DateTimeField dateField = new DateTimeField();
                dateField.setMessageTarget("tooltip");//$NON-NLS-1$
                dateField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd'T'HH:mm:ss"));//$NON-NLS-1$            
                field = dateField;
                cons = OperatorConstants.dateOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.BOOLEAN.getBaseTypeName())) {
                final CheckBox checkBox = new CheckBox();
                checkBox.setBoxLabel("False"); //$NON-NLS-1$
                checkBox.addListener(Events.Change, new Listener<BaseEvent>() {
                    public void handleEvent(BaseEvent be) {
                        checkBox.setBoxLabel(checkBox.getValue() ? " True" : "False"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                });
                field = checkBox;
                cons = OperatorConstants.booleanOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.STRING.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.UUID.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.AUTO_INCREMENT.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.PICTURE.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.URL.getBaseTypeName())) {
                TextField<String> textField = new TextField<String>();
                textField.setValue("*");//$NON-NLS-1$
                field = textField;
                cons = OperatorConstants.fullOperators;
            } else {
                TextField<String> textField = new TextField<String>();
                textField.setValue("*");//$NON-NLS-1$
                field = textField;
                cons = OperatorConstants.fullOperators;
            }
        } else if (typeModel instanceof ComplexTypeModel) {
            TextField<String> textField = new TextField<String>();
            textField.setValue("*");//$NON-NLS-1$
            field = textField;
            cons = OperatorConstants.fulltextOperators;
        } else {
            TextField<String> textField = new TextField<String>();
            textField.setValue("*");//$NON-NLS-1$
            field = textField;
            cons = OperatorConstants.fullOperators;
        }

        return field;
    }

    private static void setEnumerationValues(TypeModel typeModel, Widget w) {
        List<String> enumeration = ((SimpleTypeModel) typeModel).getEnumeration();
        if (enumeration != null && enumeration.size() > 0) {
            SimpleComboBox<String> field = (SimpleComboBox<String>) w;
            for (String value : enumeration) {
                field.add(value);
            }
        }
    }

}

package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.creator;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.OperatorConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey.FKField;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.SpinnerField;
import org.talend.mdm.webapp.itemsbrowser2.shared.ComplexTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.i18n.client.NumberFormat;

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
                buildFacets(typeModel, comboBox);
                comboBox.setFireChangeEventOnSetValue(true);
                if (typeModel.getMinOccurs() > 0)
                    comboBox.setAllowBlank(false);
                comboBox.setEditable(false);
                comboBox.setForceSelection(true);
                comboBox.setTriggerAction(TriggerAction.ALL);
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
                spinnerField.setFormat(NumberFormat.getFormat("0"));
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
                spinnerField.setFormat(NumberFormat.getFormat("00.0"));
                field = spinnerField;
                cons = OperatorConstants.numOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.DATE.getBaseTypeName())) {
                DateField dateField = new DateField();
                dateField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));//$NON-NLS-1$            
                field = dateField;
                cons = OperatorConstants.dateOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.DATETIME.getBaseTypeName())) {
                DateField dateField = new DateField();
                dateField.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd HH:mm"));//$NON-NLS-1$            
                field = dateField;
                cons = OperatorConstants.dateOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.BOOLEAN.getBaseTypeName())) {
                Radio radio = new Radio();
                radio.setBoxLabel("True"); //$NON-NLS-1$ 
                radio.setValue(true);
                Radio radio2 = new Radio();
                radio2.setBoxLabel("False");//$NON-NLS-1$ 
                RadioGroup radioGroup = new RadioGroup();
                radioGroup.add(radio);
                radioGroup.add(radio2);
                field = radioGroup;
                cons = OperatorConstants.booleanOperators;
            } else if (typeModel.getType().getBaseTypeName().equals(DataTypeConstants.STRING.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.UUID.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.AUTO_INCREMENT.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.PICTURE.getBaseTypeName())
                    || typeModel.getType().getBaseTypeName().equals(DataTypeConstants.URL.getBaseTypeName())) {
                TextField<String> textField = new TextField<String>();
                textField.setValue("*");
                field = textField;
                cons = OperatorConstants.fullOperators;
            } else {
                TextField<String> textField = new TextField<String>();
                textField.setValue("*");
                field = textField;
                cons = OperatorConstants.fullOperators;
            }
        } else if (typeModel instanceof ComplexTypeModel) {
            TextField<String> textField = new TextField<String>();
            textField.setValue("*");
            field = textField;
            cons = OperatorConstants.fulltextOperators;
        } else {
            TextField<String> textField = new TextField<String>();
            textField.setValue("*");
            field = textField;
            cons = OperatorConstants.fullOperators;
        }

        return field;
    }

    private static void buildFacets(TypeModel typeModel, SimpleComboBox cb) {
        List<String> facets = ((SimpleTypeModel) typeModel).getEnumeration();
        for (String facet : facets) {
            cb.add(facet);
        }
    }
}

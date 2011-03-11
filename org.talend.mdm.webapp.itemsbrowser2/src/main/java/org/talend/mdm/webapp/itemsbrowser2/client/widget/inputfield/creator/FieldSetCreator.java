package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.creator;

import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.shared.ComplexTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;

import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;


public class FieldSetCreator {

    public static FieldSet createFieldGroup(ComplexTypeModel typeModel, FormBinding formBindings, boolean enableMultiple){
        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeading(typeModel.getLabel());
        
        FormLayout layout = new FormLayout();  
        layout.setLabelWidth(75);  
        fieldSet.setLayout(layout);  
        
        List<SimpleTypeModel> simples = typeModel.getSubSimpleTypes();
        if (simples != null){
            for (SimpleTypeModel simpleModel : simples){
                Component field = FieldCreator.createField(simpleModel, formBindings, enableMultiple);
                fieldSet.add(field);
            }
        }
        List<ComplexTypeModel> complexes = typeModel.getSubComplexTypes();
        if (complexes != null){
            for (ComplexTypeModel complexModel : complexes){
                FieldSet subSet = createFieldGroup(complexModel, formBindings, enableMultiple);
                fieldSet.add(subSet);
            }
        }
        return fieldSet;
    }
    
}

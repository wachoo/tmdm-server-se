package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.creator;

import java.util.List;

import org.talend.mdm.webapp.base.shared.ComplexTypeModel;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;

import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;


public class FieldSetCreator {

    public static FieldSet createFieldGroup(ComplexTypeModel typeModel, FormBinding formBindings, boolean enableMultiple, String language){
        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeading(typeModel.getLabel(language));
        
        FormLayout layout = new FormLayout();  
        layout.setLabelWidth(75);  
        fieldSet.setLayout(layout);  
        
        List<TypeModel> types = typeModel.getSubTypes();
        if (types != null){
            for (TypeModel type : types){
                if(type instanceof SimpleTypeModel) {
                    Component field = FieldCreator.createField((SimpleTypeModel) type, formBindings, enableMultiple, language);
                    fieldSet.add(field); 
                }else if(type instanceof ComplexTypeModel) {
                    FieldSet subSet = createFieldGroup((ComplexTypeModel) type, formBindings, enableMultiple, language);
                    fieldSet.add(subSet);
                }
                
            }
        }
   
        return fieldSet;
    }
    
}

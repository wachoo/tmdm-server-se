package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.validator;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetEnum;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;


public class TextFieldValidator implements Validator {

    private static TextFieldValidator instance;
    
    public static TextFieldValidator getInstance(){
        if (instance == null){
            instance = new TextFieldValidator();
        }
        return instance;
    }
    
    private TextFieldValidator(){}
    
    public String validate(Field<?> field, String value) {
        String defaultMessage = "";
        boolean succeed = true;
        String length = field.getData(FacetEnum.LENGTH.getFacetName());
        if (length != null && !length.equals("")) {
            if (value.length() != Integer.parseInt(length)){
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_length() + length + "\n";
            }
        }
        
        String minLength = field.getData(FacetEnum.MIN_LENGTH.getFacetName());
        if (minLength != null && !minLength.equals("")) {
            if (value.length() < Integer.parseInt(minLength)){
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_minLength() + minLength + "\n";
            }
        }
        
        String maxLength = field.getData(FacetEnum.MAX_LENGTH.getFacetName());
        if (maxLength != null && !maxLength.equals("")) {
            if (value.length() > Integer.parseInt(maxLength)){
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_maxLength() + maxLength + "\n";
            }
        }
        
        if (!succeed){
            String error = field.getData("facetErrorMsgs");
            if (error == null || error.equals("")){
                return defaultMessage;
            } 
            return error;
        }
        return null;
    }

}

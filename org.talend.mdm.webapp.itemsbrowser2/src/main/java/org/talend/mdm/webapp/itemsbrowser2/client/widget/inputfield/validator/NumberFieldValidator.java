package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.validator;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetEnum;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.google.gwt.user.client.Window;


public class NumberFieldValidator implements Validator {

    private static NumberFieldValidator instance;
    
    public static NumberFieldValidator getInstance(){
        if (instance == null){
            instance = new NumberFieldValidator();
        }
        return instance;
    }
    
    private NumberFieldValidator(){}
    
    public String validate(Field<?> field, String value) {
        String defaultMessage = "";
        boolean succeed = true;
        String totalDigits = field.getData(FacetEnum.TOTAL_DIGITS.getFacetName());
        if (totalDigits != null && !totalDigits.equals("")) {
            if (value.replace(".", "").length() > Integer.parseInt(totalDigits)) {
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_totalDigits() + totalDigits + "\n";
            }
        }

        String fractionDigits = field.getData(FacetEnum.FRACTION_DIGITS.getFacetName());
        if (fractionDigits != null && !fractionDigits.equals("")) {
            String[] digits = value.split(".");
            if (digits[1].length() > Integer.parseInt(fractionDigits)) {
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_fractionDigits() + fractionDigits + "\n";
            }
        }
        String minInclusive = field.getData(FacetEnum.MIN_INCLUSIVE.getFacetName());
        if (minInclusive != null && !minInclusive.equals("")){
            double min = Double.parseDouble(minInclusive);
            double numberValue = Double.parseDouble(value);
            if (numberValue < min){
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_minInclusive() + min + "\n";
            }
        }
        
        String maxInclusive = field.getData(FacetEnum.MAX_INCLUSIVE.getFacetName());
        if (maxInclusive != null && !maxInclusive.equals("")){
            double max = Double.parseDouble(maxInclusive);
            double numberValue = Double.parseDouble(value);
            if (numberValue > max){
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_maxInclusive() + max + "\n";
            }
        }
        
        String minExclusive = field.getData(FacetEnum.MIN_EXCLUSIVE.getFacetName());
        if (minExclusive != null && !minExclusive.equals("")){
            double min = Double.parseDouble(minExclusive);
            double numberValue = Double.parseDouble(value);
            if (numberValue <= min){
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_minExclusive() + min + "\n";
            }
        }
        
        String maxExclusive = field.getData(FacetEnum.MAX_EXCLUSIVE.getFacetName());
        if (maxExclusive != null && !maxExclusive.equals("")){
            double max = Double.parseDouble(maxExclusive);
            double numberValue = Double.parseDouble(value);
            if (numberValue >= max){
                succeed = false;
                defaultMessage += MessagesFactory.getMessages().check_minExclusive() + max + "\n";
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

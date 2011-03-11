package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.validator;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.shared.FacetEnum;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;


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
        String msg = "";
        String totalDigits = field.getElement().getAttribute(FacetEnum.TOTAL_DIGITS.getFacetName());
        if (totalDigits != null && !totalDigits.equals("")) {
            if (value.replace(".", "").length() > Integer.parseInt(totalDigits)) {
                msg += MessagesFactory.getMessages().check_totalDigits() + totalDigits + "\n";
            }
        }

        String fractionDigits = field.getElement().getAttribute(FacetEnum.FRACTION_DIGITS.getFacetName());
        if (fractionDigits != null && !fractionDigits.equals("")) {
            String[] digits = value.split(".");
            if (digits[1].length() > Integer.parseInt(fractionDigits)) {
                msg += MessagesFactory.getMessages().check_fractionDigits() + fractionDigits;
            }
        }
        
        String minExclusive = field.getElement().getAttribute(FacetEnum.MIN_EXCLUSIVE.getFacetName());
        if (minExclusive != null && !minExclusive.equals("")){
            double min = Double.parseDouble(minExclusive);
            double numberValue = Double.parseDouble(value);
            if (numberValue <= min){
                msg += MessagesFactory.getMessages().check_minExclusive() + min;
            }
        }
        
        String maxExclusive = field.getElement().getAttribute(FacetEnum.MAX_EXCLUSIVE.getFacetName());
        if (maxExclusive != null && !maxExclusive.equals("")){
            double max = Double.parseDouble(maxExclusive);
            double numberValue = Double.parseDouble(value);
            if (numberValue >= max){
                msg += MessagesFactory.getMessages().check_minExclusive() + max;
            }
        }
        
        if (msg.length() > 0)
            return msg;
        else
            return null;
    }
}

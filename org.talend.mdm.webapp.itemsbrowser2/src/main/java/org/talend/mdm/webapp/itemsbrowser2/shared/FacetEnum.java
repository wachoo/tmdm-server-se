package org.talend.mdm.webapp.itemsbrowser2.shared;

import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Widget;


public enum FacetEnum {
    
    MIN_INCLUSIVE("minInclusive"),
    MAX_INCLUSIVE("maxInclusive"),
    MIN_EXCLUSIVE("minExclusive"),
    MAX_EXCLUSIVE("maxExclusive"),
    
    LENGTH("length"),
    MIN_LENGTH("minLength"),
    MAX_LENGTH("maxLength"),
    
    TOTAL_DIGITS("totalDigits"),
    FRACTION_DIGITS("fractionDigits"),
    
    ENUMERATION("enumeration"),
    
    PATTERN("pattern"),
    
    WHTE_SPACE("whiteSpace");
    
    private String facetName;
    
    FacetEnum(String facetName){
        this.facetName = facetName;
    }
    
    public String getFacetName(){
        return facetName;
    }
    
    
    public static void setFacetValue(String facet, Widget w, String value){
        if (facet.equals(MIN_INCLUSIVE.getFacetName())){
            NumberField field = (NumberField) w;
            field.setMinValue(Integer.parseInt(value));
        } else if(facet.equals(MAX_INCLUSIVE.getFacetName())){
            NumberField field = (NumberField) w;
            field.setMaxValue(Integer.parseInt(value));
        } else if (facet.equals(MIN_EXCLUSIVE.getFacetName())){
            NumberField field = (NumberField) w;
            field.setMinValue(Integer.parseInt(value));
        } else if (facet.equals(MAX_EXCLUSIVE.getFacetName())){
            NumberField field = (NumberField) w;
            field.setMaxValue(Integer.parseInt(value));
        } else if (facet.equals(LENGTH.getFacetName())){
            TextField<String> field = (TextField<String>) w;
            if (Integer.parseInt(value) > 0){
                field.setAllowBlank(false);
            }
            field.setMaxLength(Integer.parseInt(value));
            field.setMinLength(Integer.parseInt(value));
        } else if (facet.equals(MIN_LENGTH.getFacetName())){
            TextField<String> field = (TextField<String>) w;
            if (Integer.parseInt(value) > 0){
                field.setAllowBlank(false);
            }
            field.setMinLength(Integer.parseInt(value));
        } else if (facet.equals(MAX_LENGTH.getFacetName())){
            TextField<String> field = (TextField<String>) w;
            field.setMaxLength(Integer.parseInt(value));
        } else if (facet.equals(TOTAL_DIGITS.getFacetName())){
            NumberField field = (NumberField) w;
            field.getElement().setAttribute(TOTAL_DIGITS.getFacetName(), value);
        } else if (facet.equals(FRACTION_DIGITS.getFacetName())){
            NumberField field = (NumberField) w;
            field.getElement().setAttribute(FRACTION_DIGITS.getFacetName(), value);
        } else if (facet.equals(ENUMERATION.getFacetName())){
            SimpleComboBox<String> field = (SimpleComboBox<String>) w;
            field.add(value);
        } else if (facet.equals(PATTERN.getFacetName())){
            
        } else if (facet.equals(WHTE_SPACE.getFacetName())){
            
        }
    }

}
